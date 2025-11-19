package br.com.insanos.insanos_server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/all")
    public ResponseEntity<?> allAccess() {
        logger.info("🌐 Acesso público solicitado - /api/test/all");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Conteúdo público");
        response.put("accessLevel", "public");

        logger.debug("Retornando conteúdo público");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> userAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "unknown";

        logger.info("👤 Acesso de usuário solicitado - /api/test/user - Username: {}", username);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Conteúdo do usuário");
        response.put("accessLevel", "user");
        response.put("username", username);

        logger.debug("Retornando conteúdo de usuário para: {}", username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "unknown";

        logger.info("👨‍💼 Acesso de admin solicitado - /api/test/admin - Username: {}", username);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Painel do administrador");
        response.put("accessLevel", "admin");
        response.put("username", username);

        logger.debug("Retornando conteúdo de admin para: {}", username);
        return ResponseEntity.ok(response);
    }
}

