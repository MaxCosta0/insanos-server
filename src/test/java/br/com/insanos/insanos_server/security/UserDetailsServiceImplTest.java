package br.com.insanos.insanos_server.security;

import br.com.insanos.insanos_server.model.User;
import br.com.insanos.insanos_server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl - Testes Unitários")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        user.setEnabled(true);
    }

    @Test
    @DisplayName("Deve carregar usuário por username com sucesso")
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent")
        );

        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Deve carregar usuário com múltiplas roles")
    void shouldLoadUserWithMultipleRoles() {
        // Given
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve carregar usuário desabilitado")
    void shouldLoadDisabledUser() {
        // Given
        user.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Deve retornar UserDetailsImpl com dados corretos")
    void shouldReturnUserDetailsImplWithCorrectData() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertInstanceOf(UserDetailsImpl.class, userDetails);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertEquals(1L, userDetailsImpl.getId());
        assertEquals("testuser", userDetailsImpl.getUsername());
        assertEquals("test@test.com", userDetailsImpl.getEmail());
    }
}

