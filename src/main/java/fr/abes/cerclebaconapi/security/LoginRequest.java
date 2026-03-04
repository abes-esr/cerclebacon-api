package fr.abes.cerclebaconapi.security;

import lombok.Getter;
import lombok.Setter;

public class LoginRequest {
    @Getter @Setter
    private String username;

    @Setter
    @Getter
    private String password;


}

