package fr.abes.cerclebaconapi.security;

import lombok.Getter;

@Getter
public class JwtAuthenticationResponse {
    private String accessToken;
    private final String tokenType = "Bearer";
    private String userNum;
    private String shortName;
    private String iln;
    private String role;
    private String email;

    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String accessToken, String userNum, String shortName, String iln, String role, String email) {
        this.accessToken = accessToken;
        this.userNum = userNum;
        this.shortName = shortName;
        this.iln = iln;
        this.role = role;
        this.email = email;
    }

}
