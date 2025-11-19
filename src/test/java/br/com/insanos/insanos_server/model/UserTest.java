package br.com.insanos.insanos_server.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model - Testes Unitários")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Deve criar usuário com valores padrão")
    void shouldCreateUserWithDefaultValues() {
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Deve definir e obter username")
    void shouldSetAndGetUsername() {
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }

    @Test
    @DisplayName("Deve definir e obter email")
    void shouldSetAndGetEmail() {
        user.setEmail("test@test.com");
        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    @DisplayName("Deve definir e obter password")
    void shouldSetAndGetPassword() {
        user.setPassword("encodedPassword");
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    @DisplayName("Deve definir e obter roles")
    void shouldSetAndGetRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");

        user.setRoles(roles);

        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains("ROLE_USER"));
        assertTrue(user.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Deve definir e obter enabled")
    void shouldSetAndGetEnabled() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());

        user.setEnabled(true);
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Deve criar usuário com construtor completo")
    void shouldCreateUserWithFullConstructor() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        User fullUser = new User(
                1L,
                "testuser",
                "test@test.com",
                "password",
                roles,
                null,
                null,
                true
        );

        assertEquals(1L, fullUser.getId());
        assertEquals("testuser", fullUser.getUsername());
        assertEquals("test@test.com", fullUser.getEmail());
        assertEquals("password", fullUser.getPassword());
        assertEquals(1, fullUser.getRoles().size());
        assertTrue(fullUser.isEnabled());
    }

    @Test
    @DisplayName("Deve usar Lombok corretamente")
    void shouldUseLombokCorrectly() {
        // Testar equals e hashCode gerados pelo Lombok
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("test");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("test");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Deve converter para string")
    void shouldConvertToString() {
        user.setUsername("testuser");
        user.setEmail("test@test.com");

        String userString = user.toString();

        assertNotNull(userString);
        assertTrue(userString.contains("testuser"));
        assertTrue(userString.contains("test@test.com"));
    }

    @Test
    @DisplayName("Deve inicializar roles vazia")
    void shouldInitializeEmptyRoles() {
        Set<String> roles = new HashSet<>();
        user.setRoles(roles);

        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Deve adicionar role a roles existentes")
    void shouldAddRoleToExistingRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        user.getRoles().add("ROLE_ADMIN");

        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains("ROLE_USER"));
        assertTrue(user.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Deve remover role de roles existentes")
    void shouldRemoveRoleFromExistingRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);

        user.getRoles().remove("ROLE_ADMIN");

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains("ROLE_USER"));
        assertFalse(user.getRoles().contains("ROLE_ADMIN"));
    }
}

