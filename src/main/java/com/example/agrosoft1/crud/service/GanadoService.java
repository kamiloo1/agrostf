package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GanadoService {

    private static final Logger logger = LoggerFactory.getLogger(GanadoService.class);

    private final GanadoRepository ganadoRepository;

    // INYECCIÓN VIA SETTER PARA EVITAR CICLOS DE DEPENDENCIA
    private TratamientoService tratamientoService;

    @Autowired(required = false)
    private AuditoriaService auditoriaService;

    @Autowired(required = false)
    private NotificacionService notificacionService;

    public GanadoService(GanadoRepository ganadoRepository) {
        this.ganadoRepository = ganadoRepository;
    }

    @Autowired
    public void setTratamientoService(TratamientoService tratamientoService) {
        this.tratamientoService = tratamientoService;
    }

    public List<Ganado> listarGanado() {
        return ganadoRepository.findAll();
    }

    public List<Ganado> listarGanadoActivo() {
        return ganadoRepository.findByActivoTrue();
    }

    @Transactional
    public Ganado guardarGanado(Ganado ganado) {
        if (ganado.getTipo() == null || ganado.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de ganado es obligatorio");
        }

        if (ganado.getFechaCreacion() == null) {
            ganado.setFechaCreacion(LocalDateTime.now());
        }
        if (ganado.getActivo() == null) {
            ganado.setActivo(true);
        }

        if (ganado.getFechaNacimiento() != null &&
                ganado.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        if (ganado.getFechaNacimiento() != null && ganado.getEdad() == null) {
            ganado.setEdad(calcularEdad(ganado.getFechaNacimiento()));
        }

        logger.info("Guardando ganado: {} - {}", ganado.getTipo(), ganado.getRaza());
        Ganado guardado = ganadoRepository.save(ganado);
        if (auditoriaService != null) {
            auditoriaService.registrar("CREAR", "Ganado", guardado.getIdGanado(), guardado.getTipo() + " - " + guardado.getRaza());
        }
        if (notificacionService != null) {
            String msg = "Nuevo registro de ganado: " + guardado.getTipo() + " - " + guardado.getRaza();
            notificacionService.notificarAdministradores(msg, "GANADO", "/admin/ganado");
        }
        return guardado;
    }

    @Transactional
    @SuppressWarnings("null")
    public Ganado actualizarGanado(Ganado ganado) {
        if (ganado.getIdGanado() == null) {
            throw new IllegalArgumentException("El ID del ganado es obligatorio para actualizar");
        }

        Long idGanado = ganado.getIdGanado();
        Ganado existente = ganadoRepository.findById(idGanado)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado con ID: " + idGanado));

        if (ganado.getTipo() != null && !ganado.getTipo().trim().isEmpty()) {
            existente.setTipo(ganado.getTipo());
        }
        if (ganado.getRaza() != null) {
            existente.setRaza(ganado.getRaza());
        }
        if (ganado.getEdad() != null) {
            existente.setEdad(ganado.getEdad());
        }
        if (ganado.getPeso() != null) {
            existente.setPeso(ganado.getPeso());
        }
        if (ganado.getEstadoSalud() != null) {
            existente.setEstadoSalud(ganado.getEstadoSalud());
        }
        if (ganado.getFechaNacimiento() != null) {
            if (ganado.getFechaNacimiento().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            existente.setFechaNacimiento(ganado.getFechaNacimiento());
            if (ganado.getEdad() == null) {
                existente.setEdad(calcularEdad(ganado.getFechaNacimiento()));
            }
        }
        if (ganado.getActivo() != null) {
            existente.setActivo(ganado.getActivo());
        }

        logger.info("Actualizando ganado ID: {}", idGanado);
        Ganado actualizado = ganadoRepository.save(existente);
        if (auditoriaService != null) {
            auditoriaService.registrar("ACTUALIZAR", "Ganado", actualizado.getIdGanado(), actualizado.getTipo() + " - " + actualizado.getRaza());
        }
        return actualizado;
    }

    @Transactional
    public Ganado cambiarEstadoGanado(Long id, boolean activo) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del ganado es obligatorio");
        }

        @SuppressWarnings("null")
        Long idGanado = id;
        Ganado existente = ganadoRepository.findById(idGanado)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado con ID: " + idGanado));

        existente.setActivo(activo);
        logger.info("Cambiando estado del ganado ID: {} a {}", id, activo);
        Ganado guardado = ganadoRepository.save(existente);
        if (auditoriaService != null) {
            auditoriaService.registrar(activo ? "ACTIVAR" : "DESACTIVAR", "Ganado", guardado.getIdGanado(), "Estado: " + activo);
        }
        return guardado;
    }

    @SuppressWarnings("null")
    public Optional<Ganado> obtenerGanadoPorId(Long id) {
        return ganadoRepository.findById(id);
    }

    @Transactional
    public void eliminarGanado(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del ganado es obligatorio");
        }

        if (!ganadoRepository.existsById(id)) {
            throw new RuntimeException("Ganado no encontrado con ID: " + id);
        }

        if (tratamientoService == null) {
            throw new IllegalStateException("TratamientoService no disponible");
        }
        long tratamientosCount = tratamientoService.contarTratamientosPorGanado(id);
        if (tratamientosCount > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar un paciente (ganado) con tratamientos activos. Tiene "
                            + tratamientosCount + " tratamiento(s) asociado(s)."
            );
        }

        logger.info("Eliminando ganado ID: {}", id);
        if (auditoriaService != null) {
            auditoriaService.registrar("ELIMINAR", "Ganado", id, "Ganado eliminado");
        }
        ganadoRepository.deleteById(id);
    }

    public List<Ganado> buscarPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de ganado es obligatorio");
        }
        return ganadoRepository.findByTipo(tipo);
    }

    public List<Ganado> buscarPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de salud es obligatorio");
        }
        return ganadoRepository.findByEstadoSalud(estado);
    }

    public List<Ganado> buscarPorRaza(String raza) {
        if (raza == null || raza.trim().isEmpty()) {
            throw new IllegalArgumentException("La raza es obligatoria");
        }
        return ganadoRepository.findByRaza(raza);
    }

    public long contarGanado() {
        return ganadoRepository.countByActivoTrue();
    }

    public long contarGanadoSaludable() {
        return ganadoRepository.countByEstadoSaludAndActivoTrue("Saludable");
    }

    public long contarGanadoPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de ganado es obligatorio");
        }
        return ganadoRepository.countByTipo(tipo);
    }

    public long contarGanadoPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de salud es obligatorio");
        }
        return ganadoRepository.countByEstadoSalud(estado);
    }

    public boolean existeGanado(Long id) {
        return id != null && ganadoRepository.existsById(id);
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - fechaNacimiento.getYear();
        if (hoy.getDayOfYear() < fechaNacimiento.getDayOfYear()) {
            edad--;
        }
        return Math.max(0, edad);
    }
}
