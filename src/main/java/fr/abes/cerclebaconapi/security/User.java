package fr.abes.cerclebaconapi.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class User implements UserDetails {
    private Collection<? extends GrantedAuthority> authorities;
    private String userNum;
    private String userKey;
    private String userGroup;
    private String role;
    private String library;
    private String shortName;
    private String loginAllowed;
    private String iln;
    private String libRcr;
    private String mail;
    private String password;

    public User() {
    }

    public User(String userNum, String userKey, String userGroup) {
        this.userNum = userNum;
        this.userKey = userKey;
        this.userGroup = userGroup;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getRole() {
        if (role == null || role.isEmpty()) {
            if (this.userGroup.toLowerCase().trim().equals("coordinateur"))
                role = "USER";
            if (this.userGroup.toLowerCase().trim().equals("abes"))
                role = "ADMIN";
        }
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLoginAllowed() {
        return loginAllowed;
    }

    public void setLoginAllowed(String loginAllowed) {
        this.loginAllowed = loginAllowed;
    }

    public String getIln() {
        return iln;
    }

    public void setIln(String iln) {
        this.iln = iln;
    }

    public String getLibRcr() {
        return libRcr;
    }

    public void setLibRcr(String libRcr) {
        this.libRcr = libRcr;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.userKey;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


