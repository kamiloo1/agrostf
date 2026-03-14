package com.example.agrosoft1.crud.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador de errores para mostrar páginas 404 y 500 con estilo AgroSoft.
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
        String msg = message != null ? message.toString() : "Ha ocurrido un error.";

        model.addAttribute("status", statusCode);
        model.addAttribute("message", msg);
        model.addAttribute("titulo", statusCode == 404 ? "Página no encontrada" : "Error del servidor");

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        }
        return "error/500";
    }
}
