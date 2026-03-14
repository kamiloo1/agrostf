package com.example.agrosoft1.crud.model;

/**
 * DTO para el registro de nuevos usuarios
 */
public class RegistroDTO {
    private String email;
    private String numeroDocumento;
    private String contrasena;
    private String confirmarContrasena;

    public RegistroDTO() {
    }

    public RegistroDTO(String email, String numeroDocumento, String contrasena, String confirmarContrasena) {
        this.email = email;
        this.numeroDocumento = numeroDocumento;
        this.contrasena = contrasena;
        this.confirmarContrasena = confirmarContrasena;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }

    /**
     * Valida que las contraseñas coincidan
     */
    public boolean validarContrasenas() {
        return contrasena != null && contrasena.equals(confirmarContrasena);
    }
}
