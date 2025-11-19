package br.com.insanos.insanos_server.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTOs - Testes Unitários")
class DtoTests {

    @Test
    @DisplayName("LoginRequest - Deve criar e definir valores")
    void loginRequestShouldSetValues() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    @DisplayName("RegisterRequest - Deve criar e definir valores")
    void registerRequestShouldSetValues() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@test.com");
        registerRequest.setPassword("password123");

        Set<String> roles = new HashSet<>();
        roles.add("user");
        registerRequest.setRoles(roles);

        assertEquals("newuser", registerRequest.getUsername());
        assertEquals("new@test.com", registerRequest.getEmail());
        assertEquals("password123", registerRequest.getPassword());
        assertEquals(1, registerRequest.getRoles().size());
        assertTrue(registerRequest.getRoles().contains("user"));
    }

    @Test
    @DisplayName("JwtResponse - Deve criar com todos os campos")
    void jwtResponseShouldCreateWithAllFields() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        JwtResponse jwtResponse = new JwtResponse(
                "test-token",
                1L,
                "testuser",
                "test@test.com",
                roles
        );

        assertEquals("test-token", jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getType());
        assertEquals(1L, jwtResponse.getId());
        assertEquals("testuser", jwtResponse.getUsername());
        assertEquals("test@test.com", jwtResponse.getEmail());
        assertEquals(1, jwtResponse.getRoles().size());
        assertTrue(jwtResponse.getRoles().contains("ROLE_USER"));
    }

    @Test
    @DisplayName("JwtResponse - Deve ter tipo Bearer por padrão")
    void jwtResponseShouldHaveBearerTypeByDefault() {
        Set<String> roles = new HashSet<>();
        JwtResponse jwtResponse = new JwtResponse(
                "token",
                1L,
                "user",
                "email@test.com",
                roles
        );

        assertEquals("Bearer", jwtResponse.getType());
    }

    @Test
    @DisplayName("MessageResponse - Deve criar e obter mensagem")
    void messageResponseShouldCreateAndGetMessage() {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Test message");

        assertEquals("Test message", messageResponse.getMessage());
    }

    @Test
    @DisplayName("RegisterRequest - Deve aceitar roles vazias")
    void registerRequestShouldAcceptEmptyRoles() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setRoles(new HashSet<>());

        assertNotNull(registerRequest.getRoles());
        assertTrue(registerRequest.getRoles().isEmpty());
    }

    @Test
    @DisplayName("RegisterRequest - Deve aceitar roles nulas")
    void registerRequestShouldAcceptNullRoles() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setRoles(null);

        assertNull(registerRequest.getRoles());
    }

    @Test
    @DisplayName("JwtResponse - Deve definir tipo customizado")
    void jwtResponseShouldSetCustomType() {
        Set<String> roles = new HashSet<>();
        JwtResponse jwtResponse = new JwtResponse(
                "token",
                1L,
                "user",
                "email@test.com",
                roles
        );

        jwtResponse.setType("CustomType");
        assertEquals("CustomType", jwtResponse.getType());
    }

    @Test
    @DisplayName("JwtResponse - Deve modificar roles")
    void jwtResponseShouldModifyRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        JwtResponse jwtResponse = new JwtResponse(
                "token",
                1L,
                "user",
                "email@test.com",
                roles
        );

        jwtResponse.getRoles().add("ROLE_ADMIN");

        assertEquals(2, jwtResponse.getRoles().size());
        assertTrue(jwtResponse.getRoles().contains("ROLE_USER"));
        assertTrue(jwtResponse.getRoles().contains("ROLE_ADMIN"));
    }
}

