package com.prac.Prac.Config;

import com.prac.Prac.Util.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    //스프링 시큐리티 필터체인을 정의 하는 메서드. HTTP요청이 들어올 때 어떤 보안 규칙이 적용될지 정의
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/authenticate").permitAll()  // 인증 없이 접근 가능한 URL
                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // JWT 기반이므로 세션 사용하지 않음
                );

        // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    //비밀번호를 안전하게 저장하기 위해 암호화 하는 빈입니다. 여기서는 BCryptPasswordEncoder를 사용 비밀번호를 해싱
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    //AuthenticationManager:사용자 인증을 처리하는 스프링 시큐리티의 핵심 클래스
    //AuthenticationConfiguration에서 제공하는 AuthenticationManager를 반환하여 스프링 시큐리티의 인증 관리를 담당함
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
