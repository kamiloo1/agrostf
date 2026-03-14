package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.TokenRecuperacion;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.repository.TokenRecuperacionRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class RecuperacionContrasenaService {

    private static final int EXPIRACION_HORAS = 24;
    private static final int TOKEN_BYTES = 32;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public RecuperacionContrasenaService(UsuarioRepository usuarioRepository,
                                         TokenRecuperacionRepository tokenRepository,
                                         PasswordEncoder passwordEncoder,
                                         EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Genera un token, lo guarda y envía el correo con el enlace. No revela si el correo existe o no.
     */
    public void solicitarRecuperacion(String correo, String urlBase) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo != null ? correo.trim() : "");
        if (opt.isEmpty()) {
            return; // No revelar que el correo no existe
        }
        Usuario usuario = opt.get();
        tokenRepository.deleteByUsuario_Id(usuario.getId());
        String token = generarToken();
        TokenRecuperacion tr = new TokenRecuperacion();
        tr.setUsuario(usuario);
        tr.setToken(token);
        tr.setFechaExpiracion(LocalDateTime.now().plusHours(EXPIRACION_HORAS));
        tokenRepository.save(tr);

        String enlace = urlBase + "/recuperar/restablecer?token=" + token;
        String asunto = "AgroSoft - Restablecer contraseña";
        String mensaje = "Hola,\n\nHas solicitado restablecer tu contraseña en AgroSoft.\n\n"
                + "Haz clic en el siguiente enlace (válido por " + EXPIRACION_HORAS + " horas):\n\n"
                + enlace + "\n\nSi no solicitaste este correo, ignóralo.\n\n— AgroSoft";
        emailService.enviarCorreo(usuario.getCorreo(), asunto, mensaje);
    }

    /**
     * Restablece la contraseña si el token es válido y no ha expirado. Elimina el token después.
     */
    @Transactional
    public boolean restablecerConToken(String token, String nuevaContrasena) {
        if (token == null || token.isBlank() || nuevaContrasena == null || nuevaContrasena.length() < 6) {
            return false;
        }
        Optional<TokenRecuperacion> opt = tokenRepository.findByToken(token.trim());
        if (opt.isEmpty() || opt.get().isExpirado()) {
            return false;
        }
        TokenRecuperacion tr = opt.get();
        Usuario u = tr.getUsuario();
        u.setPassword(passwordEncoder.encode(nuevaContrasena.trim()));
        usuarioRepository.save(u);
        tokenRepository.delete(tr);
        return true;
    }

    /**
     * Comprueba si un token existe y no ha expirado (para mostrar el formulario de nueva contraseña).
     */
    public boolean tokenValido(String token) {
        if (token == null || token.isBlank()) return false;
        return tokenRepository.findByToken(token.trim())
                .filter(t -> !t.isExpirado())
                .isPresent();
    }

    private static String generarToken() {
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[TOKEN_BYTES];
        sr.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
