package br.com.insanos.insanos_server.security.jwt;

import br.com.insanos.insanos_server.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        logger.debug("🔒 Filtro JWT ativado para: {} {}", request.getMethod(), requestPath);

        try {
            String jwt = parseJwt(request);

            if (jwt != null) {
                logger.debug("Token JWT encontrado na requisição");

                if (jwtUtils.validateJwtToken(jwt)) {
                    logger.debug("Token JWT válido, extraindo username");
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);

                    logger.debug("Carregando UserDetails para: {}", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("✅ Usuário autenticado via JWT - Username: {}, Path: {}",
                        username, requestPath);
                } else {
                    logger.warn("⚠️ Token JWT inválido para path: {}", requestPath);
                }
            } else {
                logger.debug("Nenhum token JWT encontrado na requisição para: {}", requestPath);
            }
        } catch (Exception e) {
            logger.error("❌ Erro no filtro JWT - Path: {}, Erro: {}", requestPath, e.getMessage());
            logger.debug("Stack trace do erro no filtro:", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth)) {
            logger.debug("Header Authorization encontrado: {}",
                headerAuth.length() > 20 ? headerAuth.substring(0, 20) + "..." : headerAuth);

            if (headerAuth.startsWith("Bearer ")) {
                String token = headerAuth.substring(7);
                logger.debug("Token JWT extraído do header (tamanho: {})", token.length());
                return token;
            } else {
                logger.warn("⚠️ Header Authorization não começa com 'Bearer '");
            }
        } else {
            logger.trace("Nenhum header Authorization encontrado");
        }

        return null;
    }
}

