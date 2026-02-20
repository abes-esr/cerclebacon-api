package fr.abes.cerclebaconapi.controller;

import fr.abes.cerclebaconapi.security.JwtAuthenticationResponse;
import fr.abes.cerclebaconapi.security.JwtTokenProvider;
import fr.abes.cerclebaconapi.security.LoginRequest;
import fr.abes.cerclebaconapi.security.User;
import fr.abes.cerclebaconapi.security.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final LoginAttemptService loginAttemptService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) throws BadCredentialsException {
        String ip = getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            return ResponseEntity.status(429).body("Votre adresse IP est bloquée suite à trop de tentatives de connexion échouées.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            assert user != null;
            if (user.getAuthorities().isEmpty()) {
                return ResponseEntity.badRequest().body("Ce login ne dispose pas des droits nécessaires pour accéder à l'application");
            }
            String jwt = tokenProvider.generateToken(user);

            loginAttemptService.loginSucceeded(ip);

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, user.getUserNum(), user.getShortName(), user.getIln(), user.getRole(), user.getMail()));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(ip);
            throw e;
        }
    }

    @GetMapping("/checkToken")
    public Boolean checkToken(HttpServletRequest request) {
        String jwt = tokenProvider.getJwtFromRequest(request);
        if (jwt == null) return false;
        return tokenProvider.validateToken(jwt);
    }

    private String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}