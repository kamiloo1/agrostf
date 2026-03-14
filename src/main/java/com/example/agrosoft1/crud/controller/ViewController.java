package com.example.agrosoft1.crud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String raiz() {
        return "inicio"; // inicio.html
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio"; // inicio.html
    }

    @GetMapping("/registrarse")
    public String registrarse() {
        return "registrarse"; // registrarse.html
    }
}
