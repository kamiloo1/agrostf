package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Role;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.repository.RoleRepository;
import com.example.agrosoft1.crud.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping
    public String mostrarFormulario(Model model) {
        // Solo roles que puede elegir al registrarse: NO incluir ADMIN (solo el admin lo asigna desde su panel)
        List<Role> roles = roleRepository.findByNombreNotIgnoreCase("ADMIN");
        if (roles.isEmpty()) {
            roles = roleRepository.findAll(); // fallback por si no hay otros roles
        }
        model.addAttribute("roles", roles);
        return "registrarse";
    }

    @PostMapping("/guardar")
    public String registrarUsuario(@RequestParam String nombre,
                                  @RequestParam String email,
                                  @RequestParam String telefono,
                                  @RequestParam String numeroDocumento,
                                  @RequestParam String contrasena,
                                  @RequestParam(required = false) Integer roleId,
                                  Model model) {
        try {
            // Roles permitidos en registro (sin ADMIN)
            List<Role> rolesRegistro = roleRepository.findByNombreNotIgnoreCase("ADMIN");
            if (rolesRegistro.isEmpty()) {
                rolesRegistro = roleRepository.findAll();
            }
            model.addAttribute("roles", rolesRegistro);

            // Validar que el correo no exista
            if (usuarioService.findByCorreo(email).isPresent()) {
                model.addAttribute("error", "El correo electrónico ya está registrado");
                return "registrarse";
            }

            // Validar que el número de documento no exista
            if (usuarioService.findByNumeroDocumento(numeroDocumento).isPresent()) {
                model.addAttribute("error", "El número de documento ya está registrado");
                return "registrarse";
            }

            // Validar que se haya seleccionado un rol
            if (roleId == null) {
                model.addAttribute("error", "Debe seleccionar un rol");
                return "registrarse";
            }

            // Obtener el rol seleccionado
            Optional<Role> rolSeleccionado = roleRepository.findById(roleId);
            if (rolSeleccionado.isEmpty()) {
                model.addAttribute("error", "El rol seleccionado no existe");
                return "registrarse";
            }

            // El rol de administrador solo lo asigna un admin desde su panel, no en el registro
            if ("ADMIN".equalsIgnoreCase(rolSeleccionado.get().getNombre())) {
                model.addAttribute("error", "El rol de administrador solo puede ser asignado por un administrador desde el panel de usuarios.");
                return "registrarse";
            }

            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre != null && !nombre.trim().isEmpty() ? nombre.trim() : null);
            usuario.setCorreo(email);
            usuario.setTelefono(telefono != null && !telefono.trim().isEmpty() ? telefono.trim() : null);
            usuario.setNumeroDocumento(numeroDocumento);
            usuario.setPassword(contrasena);
            usuario.setRole(rolSeleccionado.get()); // Asignar el rol seleccionado
            usuario.setActivo(true); // Por defecto activo
            usuario.setFechaCreacion(LocalDateTime.now());
            
            usuarioService.guardarUsuario(usuario);
            
            // Autenticar automáticamente al usuario después del registro
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                // Redirigir al dashboard según el rol
                String nombreRol = rolSeleccionado.get().getNombre().toUpperCase();
                String redirectUrl;
                switch (nombreRol) {
                    case "ADMIN":
                        redirectUrl = "/dashboard/administrador";
                        break;
                    case "VETERINARIO":
                        redirectUrl = "/dashboard/veterinario";
                        break;
                    case "TRABAJADOR":
                        redirectUrl = "/dashboard/trabajador";
                        break;
                    default:
                        redirectUrl = "/login?registroexitoso=true";
                        break;
                }
                
                return "redirect:" + redirectUrl;
            } catch (Exception e) {
                // Si falla la autenticación automática, redirigir al login
                return "redirect:/login?registroexitoso=true";
            }
        } catch (Exception e) {
            List<Role> rolesRegistro = roleRepository.findByNombreNotIgnoreCase("ADMIN");
            if (rolesRegistro.isEmpty()) {
                rolesRegistro = roleRepository.findAll();
            }
            model.addAttribute("roles", rolesRegistro);
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "registrarse";
        }
    }
}
