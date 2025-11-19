package br.com.insanos.insanos_server.config;

import br.com.insanos.insanos_server.model.User;
import br.com.insanos.insanos_server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
    }

    private void initializeUsers() {
        // Criar usuário padrão: user@insanos.com / insanos321
        if (!userRepository.existsByEmail("user@insanos.com")) {
            User user = new User();
            user.setUsername("insanos");
            user.setEmail("user@insanos.com");
            user.setPassword(passwordEncoder.encode("insanos321"));

            Set<String> roles = new HashSet<>();
            roles.add("ROLE_USER");
            user.setRoles(roles);
            user.setEnabled(true);

            userRepository.save(user);
            logger.info("✓ Usuário padrão criado com sucesso!");
            logger.info("  Username: insanos");
            logger.info("  Email: user@insanos.com");
            logger.info("  Password: insanos321");
        } else {
            logger.info("✓ Usuário padrão já existe no banco de dados");
        }

        // Criar usuário admin (opcional)
        if (!userRepository.existsByEmail("admin@insanos.com")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@insanos.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            Set<String> adminRoles = new HashSet<>();
            adminRoles.add("ROLE_USER");
            adminRoles.add("ROLE_ADMIN");
            admin.setRoles(adminRoles);
            admin.setEnabled(true);

            userRepository.save(admin);
            logger.info("✓ Usuário admin criado com sucesso!");
            logger.info("  Username: admin");
            logger.info("  Email: admin@insanos.com");
            logger.info("  Password: admin123");
        } else {
            logger.info("✓ Usuário admin já existe no banco de dados");
        }
    }
}

