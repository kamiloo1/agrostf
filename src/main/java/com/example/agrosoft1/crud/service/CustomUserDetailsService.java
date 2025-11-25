package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    public CustomUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        // Verificar que el usuario tenga rol asignado y esté activo
        if (usuario.getRole() == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado. Contacte al administrador.");
        }

        if (usuario.getActivo() == null || !usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo. Contacte al administrador.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        String nombreRol = usuario.getRole().getNombre().toUpperCase();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + nombreRol));

        return new User(usuario.getCorreo(), usuario.getPassword(), authorities);
    }
}
