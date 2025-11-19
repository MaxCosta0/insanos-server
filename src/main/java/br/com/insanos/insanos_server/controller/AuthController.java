package br.com.insanos.insanos_server.controller;

import br.com.insanos.insanos_server.dto.JwtResponse;
import br.com.insanos.insanos_server.dto.LoginRequest;
import br.com.insanos.insanos_server.dto.MessageResponse;
import br.com.insanos.insanos_server.dto.RegisterRequest;
import br.com.insanos.insanos_server.security.UserDetailsImpl;
import br.com.insanos.insanos_server.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("🔐 Tentativa de login - Username: {}", loginRequest.getUsername());

        try {
            logger.debug("Iniciando autenticação para usuário: {}", loginRequest.getUsername());
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);

            logger.info("✅ Login bem-sucedido - Username: {}, ID: {}, Roles: {}",
                jwtResponse.getUsername(),
                jwtResponse.getId(),
                jwtResponse.getRoles());

            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            logger.error("❌ Falha no login - Username: {}, Erro: {}",
                loginRequest.getUsername(),
                e.getMessage());
            logger.debug("Stack trace do erro de login:", e);

            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciais inválidas");
            error.put("message", "Usuário ou senha incorretos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        logger.info("📝 Tentativa de registro - Username: {}, Email: {}",
            signUpRequest.getUsername(),
            signUpRequest.getEmail());

        try {
            logger.debug("Validando dados de registro para: {}", signUpRequest.getUsername());
            MessageResponse response = authService.registerUser(signUpRequest);

            if (response.getMessage().startsWith("Erro")) {
                logger.warn("⚠️ Falha no registro - Username: {}, Motivo: {}",
                    signUpRequest.getUsername(),
                    response.getMessage());

                Map<String, String> error = new HashMap<>();
                error.put("error", "Erro no registro");
                error.put("message", response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            logger.info("✅ Registro bem-sucedido - Username: {}, Email: {}",
                signUpRequest.getUsername(),
                signUpRequest.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("❌ Erro no registro - Username: {}, Erro: {}",
                signUpRequest.getUsername(),
                e.getMessage());
            logger.debug("Stack trace do erro de registro:", e);

            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro no servidor");
            error.put("message", "Erro ao registrar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        logger.debug("🔍 Verificação de autenticação solicitada");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                logger.info("✅ Usuário autenticado - Username: {}, ID: {}",
                    userDetails.getUsername(),
                    userDetails.getId());

                Map<String, Object> userData = new HashMap<>();
                userData.put("id", userDetails.getId());
                userData.put("username", userDetails.getUsername());
                userData.put("email", userDetails.getEmail());
                userData.put("roles", userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toSet()));
                userData.put("authenticated", true);

                return ResponseEntity.ok(userData);
            }

            logger.warn("⚠️ Verificação de autenticação falhou - Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false, "message", "Não autenticado"));
        } catch (Exception e) {
            logger.error("❌ Erro na verificação de autenticação: {}", e.getMessage());
            logger.debug("Stack trace:", e);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false, "message", "Erro ao verificar autenticação"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        logger.debug("👤 Solicitação de dados do usuário atual");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                logger.info("✅ Dados do usuário recuperados - Username: {}, ID: {}",
                    userDetails.getUsername(),
                    userDetails.getId());

                Map<String, Object> userData = new HashMap<>();
                userData.put("id", userDetails.getId());
                userData.put("username", userDetails.getUsername());
                userData.put("email", userDetails.getEmail());
                userData.put("roles", userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toSet()));

                return ResponseEntity.ok(userData);
            }

            logger.warn("⚠️ Tentativa de obter dados sem autenticação");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Não autenticado"));
        } catch (Exception e) {
            logger.error("❌ Erro ao buscar dados do usuário: {}", e.getMessage());
            logger.debug("Stack trace:", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao buscar dados do usuário"));
        }
    }
}

