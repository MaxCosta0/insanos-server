package br.com.insanos.insanos_server.security.jwt;

import br.com.insanos.insanos_server.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        logger.debug("🔑 Gerando JWT token para usuário: {}", userPrincipal.getUsername());

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + jwtExpirationMs);

        logger.debug("Token será válido de {} até {}", issuedAt, expiresAt);

        String token = Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSigningKey())
                .compact();

        logger.info("✅ JWT token gerado com sucesso para: {} (expira em {})",
            userPrincipal.getUsername(), expiresAt);

        return token;
    }

    public String getUserNameFromJwtToken(String token) {
        logger.debug("🔍 Extraindo username do JWT token");

        try {
            String username = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            logger.debug("Username extraído do token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("❌ Erro ao extrair username do token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Assinatura JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string vazio: {}", e.getMessage());
        }
        return false;
    }
}

