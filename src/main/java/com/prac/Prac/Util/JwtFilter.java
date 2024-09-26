package com.prac.Prac.Util;

import com.prac.Prac.Service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

//jWT Filter
@Component
public class JwtFilter extends HttpFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userDetailsService;

    @Override
    //dofilter 메서드는 ㅣㄹ터의 핵심 메서드로, 요청이 들어올 때마다 실행됨
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //요청의헤더에서 Authoriaztion값을 가져옴. JWT토큰이 여기에 포함되어 있어야함
        final String authorizationHeader = request.getHeader("Authorization");
        //username과 jwt변수를 선언합니다. JWT토큰과 해당 토큰에서 추출한 사용자 이름을 저장
        String username = null;
        String jwt = null;

        //Authorization헤더가 존재하고, 토큰이 "Bearer"로 시작하는지 확인합니다. JWT는 종종 "Bearer"라는 접두사와 함께 제공 됨
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //"Bearer"접두사 이후에 실제 Jwt토큰 값을 추출합니다.
            jwt = authorizationHeader.substring(7);
            //Jwt토큰에서 사용자 이름을 추출하려고 합니다. 토큰이 만료된 경우 ExpiredJwtException이 발생하며, 이 경우 "JWT token has expired"라는 메시지를 출력합니다.
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token has expired");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validate(jwt, userDetails.getUsername())) {
                //사용자 인증 정보를 기반으로 UsernamePasswordAuthenticationToken 객체를 생성함
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        //생성된 인증 토큰에 요청의 세부 정보를 추가합니다.
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //인증된 사용자의 정보를 SecurityContextHolder에 설정하여 이후에 인증된 사용자로 요청을 처리할 수 있도록 합니다.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        //필터 체인의 타음 필터로 요청을 넘깁니다.
        chain.doFilter(request, response);
    }
}
