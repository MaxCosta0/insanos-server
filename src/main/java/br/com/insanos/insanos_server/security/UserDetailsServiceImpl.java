package br.com.insanos.insanos_server.security;

import br.com.insanos.insanos_server.model.User;
import br.com.insanos.insanos_server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("👤 Carregando UserDetails para: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("❌ Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });

        logger.info("✅ UserDetails carregado com sucesso - Username: {}, ID: {}, Roles: {}",
            user.getUsername(),
            user.getId(),
            user.getRoles());

        return UserDetailsImpl.build(user);
    }
}

