package br.com.insanos.insanos_server.service;

import br.com.insanos.insanos_server.dto.JwtResponse;
import br.com.insanos.insanos_server.dto.LoginRequest;
import br.com.insanos.insanos_server.dto.MessageResponse;
import br.com.insanos.insanos_server.dto.RegisterRequest;
import br.com.insanos.insanos_server.model.User;
import br.com.insanos.insanos_server.repository.UserRepository;
import br.com.insanos.insanos_server.security.UserDetailsImpl;
import br.com.insanos.insanos_server.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("🔐 AuthService: Iniciando autenticação - Username: {}", loginRequest.getUsername());

        try {
            logger.debug("Criando token de autenticação para: {}", loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            logger.debug("Autenticação bem-sucedida, configurando contexto de segurança");
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.debug("Gerando token JWT");
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toSet());

            logger.info("✅ Token JWT gerado com sucesso - Username: {}, ID: {}, Roles: {}",
                userDetails.getUsername(),
                userDetails.getId(),
                roles);

            return new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);
        } catch (Exception e) {
            logger.error("❌ Falha na autenticação - Username: {}, Erro: {}",
                loginRequest.getUsername(),
                e.getMessage());
            throw e;
        }
    }

    public MessageResponse registerUser(RegisterRequest signUpRequest) {
        logger.info("📝 AuthService: Iniciando registro - Username: {}, Email: {}",
            signUpRequest.getUsername(),
            signUpRequest.getEmail());

        // Verificar username
        logger.debug("Verificando se username '{}' já existe", signUpRequest.getUsername());
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.warn("⚠️ Registro falhou - Username '{}' já está em uso", signUpRequest.getUsername());
            return new MessageResponse("Erro: Username já está em uso!");
        }

        // Verificar email
        logger.debug("Verificando se email '{}' já existe", signUpRequest.getEmail());
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("⚠️ Registro falhou - Email '{}' já está em uso", signUpRequest.getEmail());
            return new MessageResponse("Erro: Email já está em uso!");
        }

        // Criar nova conta de usuário
        logger.debug("Criando novo usuário - Username: {}", signUpRequest.getUsername());
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());

        logger.debug("Criptografando senha para: {}", signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<String> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            logger.debug("Nenhuma role especificada, atribuindo ROLE_USER padrão");
            roles.add("ROLE_USER");
        } else {
            logger.debug("Processando roles especificadas: {}", strRoles);
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    logger.debug("Atribuindo ROLE_ADMIN");
                    roles.add("ROLE_ADMIN");
                } else {
                    logger.debug("Atribuindo ROLE_USER");
                    roles.add("ROLE_USER");
                }
            });
        }

        user.setRoles(roles);

        logger.debug("Salvando usuário no banco de dados: {}", signUpRequest.getUsername());
        userRepository.save(user);

        logger.info("✅ Usuário registrado com sucesso - Username: {}, Email: {}, Roles: {}",
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            roles);

        return new MessageResponse("Usuário registrado com sucesso!");
    }
}

