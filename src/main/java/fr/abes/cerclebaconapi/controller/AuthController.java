package fr.abes.cerclebaconapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import fr.abes.cerclebaconapi.security.JwtAuthenticationResponse;
import fr.abes.cerclebaconapi.security.JwtTokenProvider;
import fr.abes.cerclebaconapi.security.LoginRequest;
import fr.abes.cerclebaconapi.security.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();

            if (user.getAuthorities().isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Ce compte ne dispose pas des droits nécessaires pour accéder à CercleBacon.");
            }

            String jwt = tokenProvider.generateToken(user);

            return ResponseEntity.ok(new JwtAuthenticationResponse(
                    jwt,
                    user.getUserNum(),
                    user.getShortName(),
                    user.getIln(),
                    user.getRole(),
                    user.getMail()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiant ou mot de passe incorrect. Veuillez vérifier vos informations de connexion.");
        }
    }

    @GetMapping("/checkToken")
    public Boolean checkToken(HttpServletRequest request) {
        String jwt = tokenProvider.getJwtFromRequest(request);
        if (jwt == null) return false;
        return tokenProvider.validateToken(jwt);
    }
}
