package com.prac.Prac.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
    private String user_name;
    private String pw;
}


