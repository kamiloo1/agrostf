package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.repository.AuditoriaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaController(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @GetMapping
    public String verAuditoria(Model model) {
        model.addAttribute("registros", auditoriaRepository.findTop100ByOrderByFechaDesc());
        return "admin/auditoria";
    }
}
