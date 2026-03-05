package fr.abes.cerclebaconapi.controller;

import fr.abes.cerclebaconapi.security.*;
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
@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User)authentication.getPrincipal();
        if (user.getAuthorities().isEmpty()) {
            return ResponseEntity.badRequest().body("Ce login ne dispose pas des droits nécessaires pour accéder à CercleBacon.");
        }
        String jwt = tokenProvider.generateToken(user);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, user.getUserNum(), user.getShortName(), user.getIln(), user.getRole(), user.getMail()));
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