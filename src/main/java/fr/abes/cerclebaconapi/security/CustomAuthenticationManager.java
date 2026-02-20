package fr.abes.cerclebaconapi.security;

import fr.abes.cerclebaconapi.exception.WsAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    private final AuthenticationEventPublisher authenticationEventPublisher;

    @Value("${wsAuthSudoc.url}")
    String urlWsAuthSudoc;

    public CustomAuthenticationManager(AuthenticationEventPublisher authenticationEventPublisher) {
        this.authenticationEventPublisher = authenticationEventPublisher;
    }


    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        log.debug("entree dans authenticate...");

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        try {
            User u = this.callWsAuth(name, password);
            List<GrantedAuthority> authorities;
            if (u.getRole() != null && (u.getRole().equals("USER") || u.getRole().equals("ADMIN"))) {
                authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(u.getRole()));
            } else {
                authorities = Collections.emptyList();
            }
            u.setAuthorities(authorities);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            authenticationEventPublisher.publishAuthenticationSuccess(auth);
            return auth;
        } catch (WsAuthException ex) {
            authenticationEventPublisher.publishAuthenticationFailure(new BadCredentialsException(ex.getMessage()), authentication);
            throw new BadCredentialsException(ex.getMessage());
        }
    }


    private User callWsAuth(String userKey, String password) throws WsAuthException{
        try {
            RestTemplate restTemplate = new RestTemplate();
            String requestJson = "{\"userKey\": \"" + userKey + "\", \"password\": \"" + password + "\"}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            User user = restTemplate.postForObject(this.urlWsAuthSudoc, entity, User.class);
            if (user == null) {
                throw new WsAuthException("L'authentification a échoué : réponse vide du service.");
            }
            return user;
        }
        catch (Exception e) {
            log.error("rejet du service web d'authentification Sudoc " + e);
            throw new WsAuthException("Une erreur s'est produite : Identifiant ou mot de passe incorrect.");
        }
    }
}
