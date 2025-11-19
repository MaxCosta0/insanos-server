package br.com.insanos.insanos_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public ResponseEntity<?> allAccess() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Conteúdo público");
        response.put("accessLevel", "public");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> userAccess() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Conteúdo do usuário");
        response.put("accessLevel", "user");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAccess() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Painel do administrador");
        response.put("accessLevel", "admin");
        return ResponseEntity.ok(response);
    }
}

