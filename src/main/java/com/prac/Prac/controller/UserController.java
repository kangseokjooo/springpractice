package com.prac.Prac.controller;

import com.prac.Prac.Dto.AuthenticationRequest;
import com.prac.Prac.Dto.AuthenticationResponse;
import com.prac.Prac.Dto.DeleteRequest;
import com.prac.Prac.Dto.UserDto;
import com.prac.Prac.Entity.UserEntity;
import com.prac.Prac.Service.UserService;
import com.prac.Prac.Util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserService userDetailService;
    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping("/insert")
    @ResponseBody
    public String createUser (@RequestBody UserDto u){
        if(userService.createUser(u)){
            return "Yes";
        }
        else{
            return "No!!";
        }


    }
    @DeleteMapping("/delete")
    @Operation(summary = "유저 정보 삭제",description = "id를 통한 유저정보가 삭제됩니다")
    public ResponseEntity<String> deleteUser(@RequestBody DeleteRequest d) {
        if (userService.deleteUser(d.getUser_id())) {
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserEntity u) {
        System.out.println("Attempting to authenticate user: " + u.getUserName());

        // 입력 받은 패스워드 확인
        System.out.println("Password entered: " + u.getPw());

        try {
            // 사용자 인증
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(u.getUserName(), u.getPw())
            );
        } catch (BadCredentialsException e) {
            // 잘못된 자격 증명 처리
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // 유저 정보를 기반으로 JWT 생성
        final UserDetails userDetails = userDetailService.loadUserByUsername(u.getUserName());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // JWT 값 출력
        System.out.println("Generated JWT: " + jwt);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }




}
