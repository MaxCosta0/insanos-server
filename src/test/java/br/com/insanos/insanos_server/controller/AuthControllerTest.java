package br.com.insanos.insanos_server.controller;

import br.com.insanos.insanos_server.dto.JwtResponse;
import br.com.insanos.insanos_server.dto.LoginRequest;
import br.com.insanos.insanos_server.dto.MessageResponse;
import br.com.insanos.insanos_server.dto.RegisterRequest;
import br.com.insanos.insanos_server.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController - Testes de Integração")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@test.com");
        registerRequest.setPassword("password123");

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        jwtResponse = new JwtResponse(
                "test-jwt-token",
                1L,
                "testuser",
                "test@test.com",
                roles
        );
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve fazer login com credenciais válidas")
    void shouldLoginWithValidCredentials() throws Exception {
        // Given
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 401 com credenciais inválidas")
    void shouldReturn401WithInvalidCredentials() throws Exception {
        // Given
        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciais inválidas"))
                .andExpect(jsonPath("$.message").value("Usuário ou senha incorretos"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 400 sem username")
    void shouldReturn400WithoutUsername() throws Exception {
        // Given
        loginRequest.setUsername(null);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 400 sem password")
    void shouldReturn400WithoutPassword() throws Exception {
        // Given
        loginRequest.setPassword(null);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve registrar novo usuário")
    void shouldRegisterNewUser() throws Exception {
        // Given
        MessageResponse response = new MessageResponse("Usuário registrado com sucesso!");
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve retornar erro quando username existe")
    void shouldReturnErrorWhenUsernameExists() throws Exception {
        // Given
        MessageResponse response = new MessageResponse("Erro: Username já está em uso!");
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro no registro"))
                .andExpect(jsonPath("$.message").value("Erro: Username já está em uso!"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve retornar 400 sem email")
    void shouldReturn400WithoutEmail() throws Exception {
        // Given
        registerRequest.setEmail(null);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Deve retornar 400 com email inválido")
    void shouldReturn400WithInvalidEmail() throws Exception {
        // Given
        registerRequest.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/auth/check - Deve retornar 401 sem autenticação")
    void shouldReturn401WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/check"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/auth/me - Deve retornar 401 sem autenticação")
    void shouldReturn401ForMeWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}

