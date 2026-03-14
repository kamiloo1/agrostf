package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Notificacion;
import com.example.agrosoft1.crud.service.NotificacionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Expone la configuración de sesión y la API de notificaciones.
 */
@RestController
@RequestMapping("/api")
public class SessionConfigController {

    @Value("${app.session.timeout.minutes:30}")
    private int timeoutMinutes;

    private final NotificacionService notificacionService;

    public SessionConfigController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping("/session-config")
    public ResponseEntity<Map<String, Object>> getSessionConfig() {
        return ResponseEntity.ok(Map.of(
            "timeoutMinutes", timeoutMinutes,
            "timeoutMs", timeoutMinutes * 60 * 1000L
        ));
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<Map<String, Object>> listarNotificaciones(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.ok(Map.of("notificaciones", List.of(), "totalNoLeidas", 0));
        }
        List<Notificacion> lista = notificacionService.listarRecientes(auth.getName(), 15);
        long noLeidas = notificacionService.contarNoLeidas(auth.getName());
        List<Map<String, Object>> items = lista.stream().map(n -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getId());
            m.put("mensaje", n.getMensaje());
            m.put("tipo", n.getTipo());
            m.put("leida", n.getLeida());
            m.put("fechaCreacion", n.getFechaCreacion().toString());
            m.put("enlace", n.getEnlace());
            return m;
        }).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("notificaciones", items);
        resp.put("totalNoLeidas", noLeidas);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/notificaciones/leer-todas")
    public ResponseEntity<Map<String, Long>> marcarTodasLeidas(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.ok(Map.of("totalNoLeidas", 0L));
        }
        notificacionService.marcarTodasComoLeidas(auth.getName());
        return ResponseEntity.ok(Map.of("totalNoLeidas", 0L));
    }

    @PostMapping("/notificaciones/{id}/leer")
    public ResponseEntity<Map<String, Long>> marcarLeida(@PathVariable Long id, Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.ok(Map.of("totalNoLeidas", 0L));
        }
        notificacionService.marcarComoLeida(id, auth.getName());
        long count = notificacionService.contarNoLeidas(auth.getName());
        return ResponseEntity.ok(Map.of("totalNoLeidas", count));
    }
}
