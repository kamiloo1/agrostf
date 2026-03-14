package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Tratamiento;
import com.example.agrosoft1.crud.pattern.strategy.CalculadoraPrecio;
import com.example.agrosoft1.crud.pattern.strategy.CalculoPrecioNormal;
import com.example.agrosoft1.crud.pattern.strategy.CalculoPrecioDescuento;
import com.example.agrosoft1.crud.pattern.strategy.CalculoPrecioMayorista;
import com.example.agrosoft1.crud.repository.TratamientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TratamientoService {

    private static final Logger logger = LoggerFactory.getLogger(TratamientoService.class);

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    @Lazy
    private GanadoService ganadoService;

    @Autowired(required = false)
    private NotificacionService notificacionService;
    
    // PATRÓN STRATEGY: Calculadora de precios para aplicar descuentos
    private final CalculadoraPrecio calculadoraPrecio;
    
    public TratamientoService() {
        // Inicializar con estrategia normal por defecto
        this.calculadoraPrecio = new CalculadoraPrecio(new CalculoPrecioNormal());
    }

    public List<Tratamiento> listarTratamientos() {
        return tratamientoRepository.findAll();
    }

    public Tratamiento guardarTratamiento(Tratamiento tratamiento) {

        if (tratamiento.getGanado() == null || tratamiento.getGanado().getIdGanado() == null) {
            throw new IllegalArgumentException("No se puede crear un tratamiento sin un paciente (ganado) asociado");
        }

        if (!ganadoService.obtenerGanadoPorId(tratamiento.getGanado().getIdGanado()).isPresent()) {
            throw new IllegalArgumentException("El paciente (ganado) especificado no existe");
        }

        if (tratamiento.getTipoTratamiento() == null || tratamiento.getTipoTratamiento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de tratamiento es obligatorio");
        }

        if (tratamiento.getFechaTratamiento() == null) {
            throw new IllegalArgumentException("La fecha de tratamiento es obligatoria");
        }

        if (tratamiento.getFechaCreacion() == null) {
            tratamiento.setFechaCreacion(java.time.LocalDateTime.now());
        }
        
        // PATRÓN STRATEGY: Calcular costo final aplicando estrategia de descuento
        if (tratamiento.getCosto() != null && tratamiento.getCosto().doubleValue() > 0) {
            BigDecimal costoBase = tratamiento.getCosto();
            double costoFinal = calcularCostoConDescuento(costoBase.doubleValue(), tratamiento.getTipoTratamiento());
            tratamiento.setCosto(BigDecimal.valueOf(costoFinal));
            logger.info("Costo calculado: Base={}, Final={}, Estrategia={}", 
                costoBase, costoFinal, calculadoraPrecio.getInfoEstrategia());
        }

        Tratamiento guardado = tratamientoRepository.save(tratamiento);
        if (notificacionService != null) {
            String msg = "Nuevo tratamiento: " + guardado.getTipoTratamiento() + " para paciente " + guardado.getGanado().getTipo();
            notificacionService.notificarAdministradores(msg, "TRATAMIENTO", "/vet/tratamientos");
        }
        return guardado;
    }
    
    /**
     * PATRÓN STRATEGY: Calcula el costo final aplicando una estrategia de descuento
     * según el tipo de tratamiento
     * 
     * @param costoBase El costo base del tratamiento
     * @param tipoTratamiento El tipo de tratamiento
     * @return El costo final después de aplicar la estrategia
     */
    private double calcularCostoConDescuento(double costoBase, String tipoTratamiento) {
        // Determinar estrategia según el tipo de tratamiento
        if (tipoTratamiento != null) {
            String tipo = tipoTratamiento.toLowerCase();
            
            // Tratamientos preventivos (vacunación, desparasitación) tienen descuento del 10%
            if (tipo.contains("vacunación") || tipo.contains("vacunacion") || 
                tipo.contains("desparasitación") || tipo.contains("desparasitacion")) {
                calculadoraPrecio.setEstrategia(new CalculoPrecioDescuento());
                logger.debug("Aplicando descuento del 10% para tratamiento preventivo: {}", tipoTratamiento);
            }
            // Tratamientos múltiples o mayoristas tienen descuento del 20%
            else if (tipo.contains("mayorista") || tipo.contains("múltiple") || tipo.contains("multiple")) {
                calculadoraPrecio.setEstrategia(new CalculoPrecioMayorista());
                logger.debug("Aplicando descuento del 20% para tratamiento mayorista: {}", tipoTratamiento);
            }
            // Otros tratamientos: precio normal
            else {
                calculadoraPrecio.setEstrategia(new CalculoPrecioNormal());
                logger.debug("Aplicando precio normal para tratamiento: {}", tipoTratamiento);
            }
        }
        
        return calculadoraPrecio.calcularPrecioFinal(costoBase);
    }

    @SuppressWarnings("null")
    public Tratamiento actualizarTratamiento(Tratamiento tratamiento) {
        Long idTratamiento = tratamiento.getIdTratamiento();
        if (idTratamiento == null) {
            throw new IllegalArgumentException("El ID del tratamiento es obligatorio para actualizar");
        }
        Tratamiento existente = tratamientoRepository.findById(idTratamiento)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));

        existente.setGanado(tratamiento.getGanado());
        existente.setTipoTratamiento(tratamiento.getTipoTratamiento());
        existente.setFechaTratamiento(tratamiento.getFechaTratamiento());
        existente.setObservaciones(tratamiento.getObservaciones());
        existente.setVeterinarioResponsable(tratamiento.getVeterinarioResponsable());
        
        // PATRÓN STRATEGY: Calcular costo final si se proporciona
        if (tratamiento.getCosto() != null && tratamiento.getCosto().doubleValue() > 0) {
            BigDecimal costoBase = tratamiento.getCosto();
            double costoFinal = calcularCostoConDescuento(costoBase.doubleValue(), tratamiento.getTipoTratamiento());
            existente.setCosto(BigDecimal.valueOf(costoFinal));
            logger.info("Costo actualizado: Base={}, Final={}, Estrategia={}", 
                costoBase, costoFinal, calculadoraPrecio.getInfoEstrategia());
        } else {
            existente.setCosto(tratamiento.getCosto());
        }

        return tratamientoRepository.save(existente);
    }

    @SuppressWarnings("null")
    public void eliminarTratamiento(Long id) {
        tratamientoRepository.deleteById(id);
    }

    @SuppressWarnings("null")
    public Optional<Tratamiento> obtenerTratamientoPorId(Long id) {
        return tratamientoRepository.findById(id);
    }

    public List<Tratamiento> buscarPorGanado(Long idGanado) {
        return tratamientoRepository.findByGanadoIdGanado(idGanado);
    }

    public List<Tratamiento> buscarPorTipo(String tipo) {
        return tratamientoRepository.findByTipoTratamiento(tipo);
    }

    public List<Tratamiento> buscarPorVeterinario(String veterinario) {
        return tratamientoRepository.findByVeterinarioResponsable(veterinario);
    }

    public List<Tratamiento> buscarPorFecha(LocalDate fecha) {
        return tratamientoRepository.findByFechaTratamiento(fecha);
    }

    public long contarTratamientos() {
        return tratamientoRepository.count();
    }

    public long contarTratamientosPorGanado(Long idGanado) {
        return tratamientoRepository.countByGanadoIdGanado(idGanado);
    }
}
