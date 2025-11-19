package br.com.insanos.insanos_server.security.jwt;

import br.com.insanos.insanos_server.security.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils - Testes Unitários")
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private UserDetailsImpl userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        // Configurar propriedades via reflection
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
            "insanosSecretKeyForTestingPurposesOnlyDoNotUseInProduction1234567890");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000); // 1 hora

        // Configurar UserDetails
        userDetails = new UserDetailsImpl(
                1L,
                "testuser",
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true
        );
    }

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void shouldGenerateValidJwtToken() {
        // Given
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes separadas por ponto
    }

    @Test
    @DisplayName("Deve extrair username do token JWT")
    void shouldExtractUsernameFromToken() {
        // Given
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Deve validar token JWT correto")
    void shouldValidateCorrectJwtToken() {
        // Given
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token JWT inválido")
    void shouldRejectInvalidJwtToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token JWT vazio")
    void shouldRejectEmptyJwtToken() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtUtils.validateJwtToken(emptyToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token JWT malformado")
    void shouldRejectMalformedJwtToken() {
        // Given
        String malformedToken = "malformed.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token JWT com assinatura inválida")
    void shouldRejectTokenWithInvalidSignature() {
        // Given
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // Modificar o token para invalidar a assinatura
        String[] parts = token.split("\\.");
        String invalidToken = parts[0] + "." + parts[1] + ".invalidsignature";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token JWT expirado")
    void shouldRejectExpiredJwtToken() {
        // Given - Criar JwtUtils com expiração muito curta
        JwtUtils shortExpirationJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "jwtSecret",
            "insanosSecretKeyForTestingPurposesOnlyDoNotUseInProduction1234567890");
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "jwtExpirationMs", -1000); // Já expirado

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String expiredToken = shortExpirationJwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Tokens diferentes devem ser gerados para diferentes usuários")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        // Given
        UserDetailsImpl anotherUser = new UserDetailsImpl(
                2L,
                "anotheruser",
                "another@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true
        );

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        Authentication anotherAuth = mock(Authentication.class);
        when(anotherAuth.getPrincipal()).thenReturn(anotherUser);

        // When
        String token1 = jwtUtils.generateJwtToken(authentication);
        String token2 = jwtUtils.generateJwtToken(anotherAuth);

        // Then
        assertNotEquals(token1, token2);

        String username1 = jwtUtils.getUserNameFromJwtToken(token1);
        String username2 = jwtUtils.getUserNameFromJwtToken(token2);

        assertEquals("testuser", username1);
        assertEquals("anotheruser", username2);
    }

    @Test
    @DisplayName("Deve lançar exceção ao extrair username de token inválido")
    void shouldThrowExceptionWhenExtractingUsernameFromInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtils.getUserNameFromJwtToken(invalidToken);
        });
    }
}

