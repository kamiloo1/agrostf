package com.example.agrosoft1.crud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración para verificar que las rutas respetan los roles configurados.
 * Alineado con docs/PERMISOS_ROLES.md (ganado todos, tratamientos admin+vet, búsquedas/reportes autenticados, etc.)
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPage_permiteAccesoAnonimo() throws Exception {
        mockMvc.perform(get("/login").with(anonymous()))
                .andExpect(status().isOk());
    }

    @Test
    void adminUsuarios_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/admin/usuarios").with(anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login*"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
    void adminUsuarios_conRolAdmin_permiteAcceso() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void adminUsuarios_conRolVeterinario_redirigeALogin() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login*"));
    }

    @Test
    @WithMockUser(username = "trab@test.com", authorities = "ROLE_TRABAJADOR")
    void adminUsuarios_conRolTrabajador_redirigeALogin() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login*"));
    }

    @Test
    void cuenta_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/cuenta").with(anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login*"));
    }

    @Test
    void cuentaCambiarPassword_sinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/cuenta/cambiar-password").with(anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login*"));
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_ADMIN")
    void cuentaCambiarPassword_autenticado_permiteAcceso() throws Exception {
        mockMvc.perform(get("/cuenta/cambiar-password"))
                .andExpect(status().isOk());
    }

    // --- Ganado: Admin, Veterinario y Trabajador ---
    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void adminGanado_conRolVeterinario_permiteAcceso() throws Exception {
        mockMvc.perform(get("/admin/ganado"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trab@test.com", authorities = "ROLE_TRABAJADOR")
    void adminGanado_conRolTrabajador_permiteAcceso() throws Exception {
        mockMvc.perform(get("/admin/ganado"))
                .andExpect(status().isOk());
    }

    // --- Cultivos: Admin y Trabajador ---
    @Test
    @WithMockUser(username = "trab@test.com", authorities = "ROLE_TRABAJADOR")
    void adminCultivos_conRolTrabajador_permiteAcceso() throws Exception {
        mockMvc.perform(get("/admin/cultivos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void adminCultivos_conRolVeterinario_redirigeALogin() throws Exception {
        mockMvc.perform(get("/admin/cultivos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login*"));
    }

    // --- Tratamientos (/vet/**): Admin y Veterinario ---
    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void vetTratamientos_conRolVeterinario_permiteAcceso() throws Exception {
        mockMvc.perform(get("/vet/tratamientos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
    void vetTratamientos_conRolAdmin_permiteAcceso() throws Exception {
        mockMvc.perform(get("/vet/tratamientos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trab@test.com", authorities = "ROLE_TRABAJADOR")
    void vetTratamientos_conRolTrabajador_redirigeALogin() throws Exception {
        mockMvc.perform(get("/vet/tratamientos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login*"));
    }

    // --- Búsquedas y Reportes: cualquier autenticado ---
    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void adminBusquedas_conRolVeterinario_permiteAcceso() throws Exception {
        mockMvc.perform(get("/admin/busquedas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void adminReportes_conRolVeterinario_permiteAcceso() throws Exception {
        mockMvc.perform(get("/admin/reportes"))
                .andExpect(status().isOk());
    }

    // --- Actividades (/trabajador/**): Veterinario y Trabajador ---
    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void trabajadorActividades_conRolVeterinario_permiteAcceso() throws Exception {
        mockMvc.perform(get("/trabajador/actividades"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trab@test.com", authorities = "ROLE_TRABAJADOR")
    void trabajadorActividades_conRolTrabajador_permiteAcceso() throws Exception {
        mockMvc.perform(get("/trabajador/actividades"))
                .andExpect(status().isOk());
    }

    // --- Dashboard por rol ---
    @Test
    @WithMockUser(username = "vet@test.com", authorities = "ROLE_VETERINARIO")
    void dashboardVeterinario_conRolVeterinario_permiteAcceso() throws Exception {
        mockMvc.perform(get("/dashboard/veterinario"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trab@test.com", authorities = "ROLE_TRABAJADOR")
    void dashboardTrabajador_conRolTrabajador_permiteAcceso() throws Exception {
        mockMvc.perform(get("/dashboard/trabajador"))
                .andExpect(status().isOk());
    }
}
