package fr.abes.cerclebaconapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String JWT_COOKIE_NAME = "access_token";

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User u) {

        Date now = Calendar.getInstance().getTime();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(u.getUserKey())// USER_KEY de la base
                .issuedAt(new Date())
                .expiration(expiryDate)
                .claim("userNum", u.getUserNum())
                .claim("iln", u.getIln())
                .claim("library", u.getLibrary())
                .claim("rcr", u.getLibRcr())
                .claim("loginAllowed", u.getLoginAllowed())
                .claim("role", u.getRole())
                .claim("shortName", u.getShortName())
                .claim("userGroup", u.getUserGroup())
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return getJwtFromCookie(request);
    }

    public ResponseCookie createJwtCookie(String jwt) {
        return ResponseCookie.from(JWT_COOKIE_NAME, jwt)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(jwtExpirationInMs))
                .build();
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (JWT_COOKIE_NAME.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public User getUtilisateurFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        User u = new User();
        u.setUserNum(claims.get("userNum").toString());
        u.setIln(claims.get("iln").toString());
        u.setLibrary(claims.get("library").toString());
        u.setLibRcr(claims.get("rcr").toString());
        u.setLoginAllowed(claims.get("loginAllowed").toString());
        u.setRole(claims.get("role").toString());
        u.setShortName(claims.get("shortName").toString());
        u.setUserGroup(claims.get("userGroup").toString());
        u.setAuthorities(Collections.singleton(new SimpleGrantedAuthority(u.getRole())));
        return u;
    }
}
