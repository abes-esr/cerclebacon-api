package fr.abes.cerclebaconapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;

    private final LoginAttemptService loginAttemptService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, LoginAttemptService loginAttemptService) {
        this.tokenProvider = tokenProvider;
        this.loginAttemptService = loginAttemptService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String ip = getClientIP(request);
            if (loginAttemptService.isBlocked(ip)) {
                throw new RuntimeException("IP_BLOCKED");
            }

            String jwt = tokenProvider.getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                User u = tokenProvider.getUtilisateurFromJwt(jwt);
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(u.getRole()));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(u, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("userNum", u.getUserNum());
                request.setAttribute("iln", u.getIln());
            }
        } catch (Exception ex) {
            log.error("ERROR_AUTHENTICATION_IN_SECURITY_CONTEXT", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        final var xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}

