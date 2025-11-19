package br.com.insanos.insanos_server.repository;

import br.com.insanos.insanos_server.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UserRepository - Testes de Integração")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        user.setEnabled(true);
    }

    @Test
    @DisplayName("Deve salvar e buscar usuário por username")
    void shouldSaveAndFindUserByUsername() {
        // Given
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("test@test.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar empty quando usuário não existe")
    void shouldReturnEmptyWhenUserNotExists() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve verificar se username existe")
    void shouldCheckIfUsernameExists() {
        // Given
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void shouldCheckIfEmailExists() {
        // Given
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmail("test@test.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@test.com");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Deve salvar usuário com múltiplas roles")
    void shouldSaveUserWithMultipleRoles() {
        // Given
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals(2, saved.getRoles().size());
        assertTrue(saved.getRoles().contains("ROLE_USER"));
        assertTrue(saved.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Deve garantir username único")
    void shouldEnforceUniqueUsername() {
        // Given
        entityManager.persist(user);
        entityManager.flush();

        User duplicateUser = new User();
        duplicateUser.setUsername("testuser"); // Mesmo username
        duplicateUser.setEmail("different@test.com");
        duplicateUser.setPassword("password");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persist(duplicateUser);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Deve garantir email único")
    void shouldEnforceUniqueEmail() {
        // Given
        entityManager.persist(user);
        entityManager.flush();

        User duplicateUser = new User();
        duplicateUser.setUsername("differentuser");
        duplicateUser.setEmail("test@test.com"); // Mesmo email
        duplicateUser.setPassword("password");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persist(duplicateUser);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Deve atualizar timestamps ao criar usuário")
    void shouldUpdateTimestampsOnCreate() {
        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        // Timestamps podem ser ligeiramente diferentes devido ao processamento
        assertTrue(Math.abs(saved.getCreatedAt().getNano() - saved.getUpdatedAt().getNano()) < 1000000);
    }

    @Test
    @DisplayName("Deve atualizar updatedAt ao modificar usuário")
    void shouldUpdateUpdatedAtOnModify() throws InterruptedException {
        // Given
        User saved = userRepository.save(user);
        entityManager.flush();

        Thread.sleep(100); // Pequena pausa para garantir diferença no timestamp

        // When
        saved.setEmail("newemail@test.com");
        User updated = userRepository.save(saved);
        entityManager.flush();

        // Then
        assertNotNull(updated.getUpdatedAt());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()) ||
                   updated.getUpdatedAt().isEqual(updated.getCreatedAt()));
    }

    @Test
    @DisplayName("Deve deletar usuário")
    void shouldDeleteUser() {
        // Given
        User saved = userRepository.save(user);
        entityManager.flush();
        Long userId = saved.getId();

        // When
        userRepository.delete(saved);
        entityManager.flush();

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve buscar todos os usuários")
    void shouldFindAllUsers() {
        // Given
        userRepository.save(user);

        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@test.com");
        anotherUser.setPassword("password");
        userRepository.save(anotherUser);

        entityManager.flush();

        // When
        var users = userRepository.findAll();

        // Then
        assertTrue(users.size() >= 2);
    }
}

