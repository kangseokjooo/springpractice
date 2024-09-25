package com.prac.Prac.Dto;

public class AuthenticationResponse {
    private final String jwt;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;  // this.jwt를 초기화하는 올바른 방식
    }

    public String getJwt() {
        return jwt;
    }
}
