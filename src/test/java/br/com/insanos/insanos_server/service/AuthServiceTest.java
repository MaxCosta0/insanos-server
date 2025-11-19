package br.com.insanos.insanos_server.service;

import br.com.insanos.insanos_server.dto.JwtResponse;
import br.com.insanos.insanos_server.dto.LoginRequest;
import br.com.insanos.insanos_server.dto.MessageResponse;
import br.com.insanos.insanos_server.dto.RegisterRequest;
import br.com.insanos.insanos_server.model.User;
import br.com.insanos.insanos_server.repository.UserRepository;
import br.com.insanos.insanos_server.security.UserDetailsImpl;
import br.com.insanos.insanos_server.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Configurar LoginRequest
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Configurar RegisterRequest
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@test.com");
        registerRequest.setPassword("password123");

        // Configurar User
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        // Configurar UserDetails
        userDetails = new UserDetailsImpl(
                1L,
                "testuser",
                "test@test.com",
                "encodedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true
        );
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void shouldAuthenticateUserWithValidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");

        // When
        JwtResponse response = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@test.com", response.getEmail());
        assertEquals(1L, response.getId());
        assertTrue(response.getRoles().contains("ROLE_USER"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    @DisplayName("Deve lançar exceção com credenciais inválidas")
    void shouldThrowExceptionWithInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateJwtToken(any());
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        MessageResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("Usuário registrado com sucesso!", response.getMessage());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@test.com");
        verify(encoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar erro quando username já existe")
    void shouldReturnErrorWhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When
        MessageResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("Erro: Username já está em uso!", response.getMessage());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar erro quando email já existe")
    void shouldReturnErrorWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        MessageResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("Erro: Email já está em uso!", response.getMessage());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atribuir ROLE_USER quando nenhuma role é especificada")
    void shouldAssignUserRoleWhenNoRoleSpecified() {
        // Given
        registerRequest.setRoles(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertTrue(savedUser.getRoles().contains("ROLE_USER"));
            assertEquals(1, savedUser.getRoles().size());
            return savedUser;
        });

        // When
        MessageResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("Usuário registrado com sucesso!", response.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atribuir ROLE_ADMIN quando role admin é especificada")
    void shouldAssignAdminRoleWhenAdminRoleSpecified() {
        // Given
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        registerRequest.setRoles(roles);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertTrue(savedUser.getRoles().contains("ROLE_ADMIN"));
            return savedUser;
        });

        // When
        MessageResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("Usuário registrado com sucesso!", response.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atribuir ROLE_USER para role desconhecida")
    void shouldAssignUserRoleForUnknownRole() {
        // Given
        Set<String> roles = new HashSet<>();
        roles.add("unknown");
        registerRequest.setRoles(roles);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertTrue(savedUser.getRoles().contains("ROLE_USER"));
            assertFalse(savedUser.getRoles().contains("ROLE_UNKNOWN"));
            return savedUser;
        });

        // When
        MessageResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("Usuário registrado com sucesso!", response.getMessage());
        verify(userRepository).save(any(User.class));
    }
}

