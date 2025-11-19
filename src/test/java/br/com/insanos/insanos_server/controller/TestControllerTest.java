package br.com.insanos.insanos_server.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TestController - Testes de Integração")
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/test/all - Deve permitir acesso público")
    void shouldAllowPublicAccess() throws Exception {
        // Este endpoint é público e deve retornar OK (200)
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Conteúdo público"))
                .andExpect(jsonPath("$.accessLevel").value("public"));
    }

    @Test
    @DisplayName("GET /api/test/user - Deve retornar 401 sem autenticação")
    void shouldReturn401WithoutAuthForUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/test/user - Deve permitir acesso com ROLE_USER")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowAccessWithUserRole() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Conteúdo do usuário"))
                .andExpect(jsonPath("$.accessLevel").value("user"))
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @DisplayName("GET /api/test/user - Deve permitir acesso com ROLE_ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAccessWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Conteúdo do usuário"))
                .andExpect(jsonPath("$.accessLevel").value("user"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @DisplayName("GET /api/test/admin - Deve retornar 401 sem autenticação")
    void shouldReturn401WithoutAuthForAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/test/admin - Deve retornar 403 com ROLE_USER")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldReturn403WithUserRole() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/test/admin - Deve permitir acesso com ROLE_ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAccessWithAdminRoleToAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Painel do administrador"))
                .andExpect(jsonPath("$.accessLevel").value("admin"))
                .andExpect(jsonPath("$.username").value("admin"));
    }
}

