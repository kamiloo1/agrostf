package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.*;
import com.example.agrosoft1.crud.repository.*;
import com.example.agrosoft1.crud.config.DataInitializer;
import com.example.agrosoft1.crud.util.ExcelImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
@RequestMapping("/admin/busquedas")
public class BusquedasController {
    
    private static final Logger logger = LoggerFactory.getLogger(BusquedasController.class);
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private GanadoRepository ganadoRepository;
    
    @Autowired
    private CultivoRepository cultivoRepository;
    
    @Autowired
    private TratamientoRepository tratamientoRepository;
    
    @Autowired
    private ActividadRepository actividadRepository;
    
    @Autowired
    private DataInitializer dataInitializer;
    
    @Autowired
    private ExcelImporter excelImporter;
    
    @GetMapping
    public String busquedas(Model model) {
        logger.info("Cargando página de búsquedas");
        return "admin/busquedas";
    }
    
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscar(
            @RequestParam String tipo,
            @RequestParam(required = false, defaultValue = "") String termino,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            // Filtros para usuarios
            @RequestParam(required = false) String filterRol,
            @RequestParam(required = false) String filterActivo,
            // Filtros para ganado
            @RequestParam(required = false) String filterTipo,
            @RequestParam(required = false) String filterEstado,
            @RequestParam(required = false) String filterRaza,
            @RequestParam(required = false) Double filterPesoMin,
            @RequestParam(required = false) Double filterPesoMax,
            // Filtros para cultivos
            @RequestParam(required = false) String filterTipoCultivo,
            @RequestParam(required = false) String filterEstadoCultivo,
            // Filtros para tratamientos
            @RequestParam(required = false) String filterTipoTratamiento,
            @RequestParam(required = false) String filterFechaDesde,
            @RequestParam(required = false) String filterFechaHasta,
            // Filtros para actividades
            @RequestParam(required = false) String filterTipoActividad,
            @RequestParam(required = false) String filterEstadoActividad) {
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> resultados = new ArrayList<>();
        
        try {
            logger.info("=== Búsqueda iniciada ===");
            logger.info("Tipo: {}, Término: '{}', Ordenar por: {}, Orden: {}", tipo, termino, ordenarPor, orden);
            
            switch (tipo.toLowerCase()) {
                case "usuarios":
                    resultados = buscarUsuarios(termino, filterRol, filterActivo, ordenarPor, orden);
                    break;
                case "ganado":
                    resultados = buscarGanado(termino, filterTipo, filterEstado, filterRaza, filterPesoMin, filterPesoMax, ordenarPor, orden);
                    break;
                case "cultivos":
                    resultados = buscarCultivos(termino, filterTipoCultivo, filterEstadoCultivo, ordenarPor, orden);
                    break;
                case "tratamientos":
                    resultados = buscarTratamientos(termino, filterTipoTratamiento, filterFechaDesde, filterFechaHasta, ordenarPor, orden);
                    break;
                case "actividades":
                    resultados = buscarActividades(termino, filterTipoActividad, filterEstadoActividad, ordenarPor, orden);
                    break;
                case "global":
                    resultados = buscarGlobal(termino);
                    break;
                default:
                    logger.warn("Tipo de búsqueda no válido: {}", tipo);
                    response.put("status", "error");
                    response.put("mensaje", "Tipo de búsqueda no válido: " + tipo);
                    return ResponseEntity.badRequest()
                            .header("Content-Type", "application/json")
                            .body(response);
            }
            
            response.put("status", "success");
            response.put("resultados", resultados);
            response.put("total", resultados.size());
            
            logger.info("Búsqueda completada. Tipo: {}, Resultados encontrados: {}", tipo, resultados.size());
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(response);
            
        } catch (Exception e) {
            logger.error("Error en búsqueda: {}", e.getMessage(), e);
            e.printStackTrace();
            response.put("status", "error");
            response.put("mensaje", "Error al realizar la búsqueda: " + e.getMessage());
            return ResponseEntity.status(500)
                    .header("Content-Type", "application/json")
                    .body(response);
        }
    }
    
    @PostMapping("/cargar-datos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cargarDatosIniciales() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Iniciando carga de datos iniciales desde la vista");
            
            // Ejecutar la carga de datos usando el método público
            dataInitializer.ejecutarCargaDatos();
            
            // Verificar resultados
            long usuarios = usuarioRepository.count();
            long ganado = ganadoRepository.count();
            long tratamientos = tratamientoRepository.count();
            
            response.put("status", "success");
            response.put("mensaje", "Datos cargados exitosamente");
            response.put("usuarios", usuarios);
            response.put("ganado", ganado);
            response.put("tratamientos", tratamientos);
            
            logger.info("Carga de datos completada. Usuarios: {}, Ganado: {}, Tratamientos: {}", 
                usuarios, ganado, tratamientos);
            
        } catch (Exception e) {
            logger.error("Error al cargar datos: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("mensaje", "Error al cargar datos: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para cargar datos desde un archivo Excel
     */
    @PostMapping("/cargar-excel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cargarDatosDesdeExcel(
            @RequestParam("archivo") MultipartFile archivo) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (archivo == null || archivo.isEmpty()) {
                response.put("status", "error");
                response.put("mensaje", "No se ha seleccionado ningún archivo");
                return ResponseEntity.badRequest().body(response);
            }
            
            String filename = archivo.getOriginalFilename();
            if (filename == null || 
                (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                response.put("status", "error");
                response.put("mensaje", "El archivo debe ser un Excel (.xlsx o .xls)");
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Iniciando importación desde Excel: {}", archivo.getOriginalFilename());
            
            // Importar datos desde Excel
            Map<String, Object> resultado = excelImporter.importarDatos(archivo);
            
            // Obtener conteos actualizados
            long usuarios = usuarioRepository.count();
            long ganado = ganadoRepository.count();
            long tratamientos = tratamientoRepository.count();
            long cultivos = cultivoRepository.count();
            
            resultado.put("usuariosTotal", usuarios);
            resultado.put("ganadoTotal", ganado);
            resultado.put("tratamientosTotal", tratamientos);
            resultado.put("cultivosTotal", cultivos);
            
            logger.info("Importación desde Excel completada");
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            logger.error("Error al importar desde Excel: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("mensaje", "Error al procesar el archivo Excel: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private List<Map<String, Object>> buscarUsuarios(String termino, String filterRol, String filterActivo, String ordenarPor, String orden) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        List<Usuario> usuarios;
        
        if (termino == null || termino.trim().isEmpty()) {
            usuarios = usuarioRepository.findAll();
        } else {
            final String terminoFinal = termino.toLowerCase();
            usuarios = usuarioRepository.findAll().stream()
                .filter(u -> 
                    (u.getNombre() != null && u.getNombre().toLowerCase().contains(terminoFinal)) ||
                    (u.getEmail() != null && u.getEmail().toLowerCase().contains(terminoFinal))
                )
                .toList();
        }
        
        // Aplicar filtros adicionales
        if (filterRol != null && !filterRol.isEmpty()) {
            usuarios = usuarios.stream()
                .filter(u -> u.getRole() != null && u.getRole().getNombre().equals(filterRol))
                .toList();
        }
        
        if (filterActivo != null && !filterActivo.isEmpty()) {
            boolean activo = Boolean.parseBoolean(filterActivo);
            usuarios = usuarios.stream()
                .filter(u -> u.getActivo() != null && u.getActivo().equals(activo))
                .toList();
        }
        
        // Ordenar
        if (ordenarPor != null && !ordenarPor.isEmpty()) {
            usuarios = usuarios.stream()
                .sorted((u1, u2) -> {
                    int comparacion = 0;
                    switch (ordenarPor) {
                        case "id":
                            comparacion = Integer.compare(u1.getId(), u2.getId());
                            break;
                        case "nombre":
                            comparacion = (u1.getNombre() != null && u2.getNombre() != null) 
                                ? u1.getNombre().compareToIgnoreCase(u2.getNombre()) : 0;
                            break;
                    }
                    return "desc".equalsIgnoreCase(orden) ? -comparacion : comparacion;
                })
                .toList();
        }
        
        for (Usuario u : usuarios) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("nombre", u.getNombre());
            item.put("email", u.getEmail());
            item.put("rol", u.getRole() != null ? u.getRole().getNombre() : "Sin rol");
            item.put("activo", u.getActivo());
            resultados.add(item);
        }
        
        return resultados;
    }
    
    private List<Map<String, Object>> buscarGanado(String termino, String filterTipo, String filterEstado, 
            String filterRaza, Double filterPesoMin, Double filterPesoMax, String ordenarPor, String orden) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        List<Ganado> ganado;
        
        if (termino == null || termino.trim().isEmpty()) {
            ganado = ganadoRepository.findAll();
        } else {
            final String terminoFinal = termino.toLowerCase();
            ganado = ganadoRepository.findAll().stream()
                .filter(g -> 
                    (g.getTipo() != null && g.getTipo().toLowerCase().contains(terminoFinal)) ||
                    (g.getRaza() != null && g.getRaza().toLowerCase().contains(terminoFinal)) ||
                    (g.getEstadoSalud() != null && g.getEstadoSalud().toLowerCase().contains(terminoFinal))
                )
                .toList();
        }
        
        // Aplicar filtros adicionales
        if (filterTipo != null && !filterTipo.isEmpty()) {
            ganado = ganado.stream()
                .filter(g -> g.getTipo() != null && g.getTipo().equals(filterTipo))
                .toList();
        }
        
        if (filterEstado != null && !filterEstado.isEmpty()) {
            ganado = ganado.stream()
                .filter(g -> g.getEstadoSalud() != null && g.getEstadoSalud().equals(filterEstado))
                .toList();
        }
        
        if (filterRaza != null && !filterRaza.isEmpty()) {
            final String razaFinal = filterRaza.toLowerCase();
            ganado = ganado.stream()
                .filter(g -> g.getRaza() != null && g.getRaza().toLowerCase().contains(razaFinal))
                .toList();
        }
        
        if (filterPesoMin != null) {
            ganado = ganado.stream()
                .filter(g -> g.getPeso() != null && g.getPeso() >= filterPesoMin)
                .toList();
        }
        
        if (filterPesoMax != null) {
            ganado = ganado.stream()
                .filter(g -> g.getPeso() != null && g.getPeso() <= filterPesoMax)
                .toList();
        }
        
        // Ordenar
        if (ordenarPor != null && !ordenarPor.isEmpty()) {
            ganado = ganado.stream()
                .sorted((g1, g2) -> {
                    int comparacion = 0;
                    switch (ordenarPor) {
                        case "id":
                            comparacion = Long.compare(g1.getIdGanado(), g2.getIdGanado());
                            break;
                        case "nombre":
                            comparacion = (g1.getTipo() != null && g2.getTipo() != null) 
                                ? g1.getTipo().compareToIgnoreCase(g2.getTipo()) : 0;
                            break;
                    }
                    return "desc".equalsIgnoreCase(orden) ? -comparacion : comparacion;
                })
                .toList();
        }
        
        for (Ganado g : ganado) {
            Map<String, Object> item = new HashMap<>();
            item.put("idGanado", g.getIdGanado());
            item.put("tipo", g.getTipo());
            item.put("raza", g.getRaza());
            item.put("edad", g.getEdad());
            item.put("peso", g.getPeso());
            item.put("estadoSalud", g.getEstadoSalud());
            resultados.add(item);
        }
        
        return resultados;
    }
    
    private List<Map<String, Object>> buscarCultivos(String termino, String filterTipoCultivo, String filterEstadoCultivo, String ordenarPor, String orden) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        List<Cultivo> cultivos;
        
        if (termino == null || termino.trim().isEmpty()) {
            cultivos = cultivoRepository.findAll();
        } else {
            final String terminoFinal = termino.toLowerCase();
            cultivos = cultivoRepository.findAll().stream()
                .filter(c -> 
                    (c.getNombre() != null && c.getNombre().toLowerCase().contains(terminoFinal)) ||
                    (c.getTipo() != null && c.getTipo().toLowerCase().contains(terminoFinal)) ||
                    (c.getEstado() != null && c.getEstado().toLowerCase().contains(terminoFinal))
                )
                .toList();
        }
        
        // Aplicar filtros adicionales
        if (filterTipoCultivo != null && !filterTipoCultivo.isEmpty()) {
            final String tipoFinal = filterTipoCultivo.toLowerCase();
            cultivos = cultivos.stream()
                .filter(c -> c.getTipo() != null && c.getTipo().toLowerCase().contains(tipoFinal))
                .toList();
        }
        
        if (filterEstadoCultivo != null && !filterEstadoCultivo.isEmpty()) {
            cultivos = cultivos.stream()
                .filter(c -> c.getEstado() != null && c.getEstado().equals(filterEstadoCultivo))
                .toList();
        }
        
        // Ordenar
        if (ordenarPor != null && !ordenarPor.isEmpty()) {
            cultivos = cultivos.stream()
                .sorted((c1, c2) -> {
                    int comparacion = 0;
                    switch (ordenarPor) {
                        case "id":
                            comparacion = Long.compare(c1.getId(), c2.getId());
                            break;
                        case "nombre":
                            comparacion = (c1.getNombre() != null && c2.getNombre() != null) 
                                ? c1.getNombre().compareToIgnoreCase(c2.getNombre()) : 0;
                            break;
                    }
                    return "desc".equalsIgnoreCase(orden) ? -comparacion : comparacion;
                })
                .toList();
        }
        
        for (Cultivo c : cultivos) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("nombre", c.getNombre());
            item.put("tipo", c.getTipo());
            item.put("area", c.getArea());
            item.put("estado", c.getEstado());
            resultados.add(item);
        }
        
        return resultados;
    }
    
    private List<Map<String, Object>> buscarTratamientos(String termino, String filterTipoTratamiento, 
            String filterFechaDesde, String filterFechaHasta, String ordenarPor, String orden) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        List<Tratamiento> tratamientos;
        
        if (termino == null || termino.trim().isEmpty()) {
            tratamientos = tratamientoRepository.findAll();
        } else {
            final String terminoFinal = termino.toLowerCase();
            tratamientos = tratamientoRepository.findAll().stream()
                .filter(t -> 
                    (t.getTipoTratamiento() != null && t.getTipoTratamiento().toLowerCase().contains(terminoFinal)) ||
                    (t.getObservaciones() != null && t.getObservaciones().toLowerCase().contains(terminoFinal))
                )
                .toList();
        }
        
        // Aplicar filtros adicionales
        if (filterTipoTratamiento != null && !filterTipoTratamiento.isEmpty()) {
            final String tipoFinal = filterTipoTratamiento.toLowerCase();
            tratamientos = tratamientos.stream()
                .filter(t -> t.getTipoTratamiento() != null && t.getTipoTratamiento().toLowerCase().contains(tipoFinal))
                .toList();
        }
        
        if (filterFechaDesde != null && !filterFechaDesde.isEmpty()) {
            try {
                java.time.LocalDate fechaDesde = java.time.LocalDate.parse(filterFechaDesde);
                tratamientos = tratamientos.stream()
                    .filter(t -> t.getFechaTratamiento() != null && !t.getFechaTratamiento().isBefore(fechaDesde))
                    .toList();
            } catch (Exception e) {
                logger.warn("Error al parsear fecha desde: {}", e.getMessage());
            }
        }
        
        if (filterFechaHasta != null && !filterFechaHasta.isEmpty()) {
            try {
                java.time.LocalDate fechaHasta = java.time.LocalDate.parse(filterFechaHasta);
                tratamientos = tratamientos.stream()
                    .filter(t -> t.getFechaTratamiento() != null && !t.getFechaTratamiento().isAfter(fechaHasta))
                    .toList();
            } catch (Exception e) {
                logger.warn("Error al parsear fecha hasta: {}", e.getMessage());
            }
        }
        
        // Ordenar
        if (ordenarPor != null && !ordenarPor.isEmpty()) {
            tratamientos = tratamientos.stream()
                .sorted((t1, t2) -> {
                    int comparacion = 0;
                    switch (ordenarPor) {
                        case "id":
                            comparacion = Long.compare(t1.getIdTratamiento(), t2.getIdTratamiento());
                            break;
                        case "fecha":
                            comparacion = (t1.getFechaTratamiento() != null && t2.getFechaTratamiento() != null) 
                                ? t1.getFechaTratamiento().compareTo(t2.getFechaTratamiento()) : 0;
                            break;
                    }
                    return "desc".equalsIgnoreCase(orden) ? -comparacion : comparacion;
                })
                .toList();
        }
        
        for (Tratamiento t : tratamientos) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", t.getIdTratamiento());
            item.put("tipo", t.getTipoTratamiento());
            item.put("fecha", t.getFechaTratamiento());
            item.put("observaciones", t.getObservaciones());
            item.put("ganadoId", t.getGanado() != null ? t.getGanado().getIdGanado() : null);
            resultados.add(item);
        }
        
        return resultados;
    }
    
    private List<Map<String, Object>> buscarActividades(String termino, String filterTipoActividad, 
            String filterEstadoActividad, String ordenarPor, String orden) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        List<Actividad> actividades;
        
        if (termino == null || termino.trim().isEmpty()) {
            actividades = actividadRepository.findAll();
        } else {
            final String terminoFinal = termino.toLowerCase();
            actividades = actividadRepository.findAll().stream()
                .filter(a -> 
                    (a.getTipoActividad() != null && a.getTipoActividad().toLowerCase().contains(terminoFinal)) ||
                    (a.getDescripcion() != null && a.getDescripcion().toLowerCase().contains(terminoFinal))
                )
                .toList();
        }
        
        // Aplicar filtros adicionales
        if (filterTipoActividad != null && !filterTipoActividad.isEmpty()) {
            final String tipoFinal = filterTipoActividad.toLowerCase();
            actividades = actividades.stream()
                .filter(a -> a.getTipoActividad() != null && a.getTipoActividad().toLowerCase().contains(tipoFinal))
                .toList();
        }
        
        if (filterEstadoActividad != null && !filterEstadoActividad.isEmpty()) {
            actividades = actividades.stream()
                .filter(a -> a.getEstado() != null && a.getEstado().equals(filterEstadoActividad))
                .toList();
        }
        
        // Ordenar
        if (ordenarPor != null && !ordenarPor.isEmpty()) {
            actividades = actividades.stream()
                .sorted((a1, a2) -> {
                    int comparacion = 0;
                    switch (ordenarPor) {
                        case "id":
                            comparacion = Long.compare(a1.getIdActividad(), a2.getIdActividad());
                            break;
                        case "fecha":
                            comparacion = (a1.getFechaActividad() != null && a2.getFechaActividad() != null) 
                                ? a1.getFechaActividad().compareTo(a2.getFechaActividad()) : 0;
                            break;
                    }
                    return "desc".equalsIgnoreCase(orden) ? -comparacion : comparacion;
                })
                .toList();
        }
        
        for (Actividad a : actividades) {
            Map<String, Object> item = new HashMap<>();
            item.put("idActividad", a.getIdActividad());
            item.put("tipo", a.getTipoActividad());
            item.put("descripcion", a.getDescripcion());
            item.put("fecha", a.getFechaActividad());
            item.put("estado", a.getEstado());
            resultados.add(item);
        }
        
        return resultados;
    }

    private List<Map<String, Object>> buscarGlobal(String termino) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String query = termino == null ? "" : termino.trim().toLowerCase();
        if (query.isEmpty()) {
            return resultados;
        }

        usuarioRepository.findAll().stream()
                .filter(u -> (u.getNombre() != null && u.getNombre().toLowerCase().contains(query))
                        || (u.getEmail() != null && u.getEmail().toLowerCase().contains(query)))
                .limit(5)
                .forEach(u -> resultados.add(crearResultadoGlobal(
                        "Usuario",
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        "/admin/usuarios"
                )));

        ganadoRepository.findAll().stream()
                .filter(g -> (g.getTipo() != null && g.getTipo().toLowerCase().contains(query))
                        || (g.getRaza() != null && g.getRaza().toLowerCase().contains(query))
                        || (g.getEstadoSalud() != null && g.getEstadoSalud().toLowerCase().contains(query)))
                .limit(5)
                .forEach(g -> resultados.add(crearResultadoGlobal(
                        "Ganado",
                        g.getIdGanado(),
                        g.getTipo(),
                        g.getRaza(),
                        "/admin/ganado"
                )));

        cultivoRepository.findAll().stream()
                .filter(c -> (c.getNombre() != null && c.getNombre().toLowerCase().contains(query))
                        || (c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(query)))
                .limit(5)
                .forEach(c -> resultados.add(crearResultadoGlobal(
                        "Cultivo",
                        c.getId(),
                        c.getNombre(),
                        c.getDescripcion(),
                        "/admin/cultivos"
                )));

        tratamientoRepository.findAll().stream()
                .filter(t -> (t.getTipoTratamiento() != null && t.getTipoTratamiento().toLowerCase().contains(query))
                        || (t.getObservaciones() != null && t.getObservaciones().toLowerCase().contains(query)))
                .limit(5)
                .forEach(t -> resultados.add(crearResultadoGlobal(
                        "Tratamiento",
                        t.getIdTratamiento(),
                        t.getTipoTratamiento(),
                        t.getObservaciones(),
                        "/vet/tratamientos"
                )));

        actividadRepository.findAll().stream()
                .filter(a -> (a.getTipoActividad() != null && a.getTipoActividad().toLowerCase().contains(query))
                        || (a.getDescripcion() != null && a.getDescripcion().toLowerCase().contains(query)))
                .limit(5)
                .forEach(a -> resultados.add(crearResultadoGlobal(
                        "Actividad",
                        a.getIdActividad(),
                        a.getTipoActividad(),
                        a.getDescripcion(),
                        "/trabajador/actividades"
                )));

        return resultados;
    }

    private Map<String, Object> crearResultadoGlobal(String tipo, Object id, String titulo, String detalle, String url) {
        Map<String, Object> item = new HashMap<>();
        item.put("tipoEntidad", tipo);
        item.put("id", id);
        item.put("titulo", titulo != null ? titulo : "-");
        item.put("detalle", detalle != null ? detalle : "-");
        item.put("url", url);
        return item;
    }
}

