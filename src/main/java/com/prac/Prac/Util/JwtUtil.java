package com.prac.Prac.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class JwtUtil {
    private String SECRET_KEY="PRAC";
    //주어진 토큰에서 사용자 이름(주체)을 호출합니다.
    //Claims::getSubject: JWT의 주체 필드(사용자 이름)을 가져오는 함수입니다.
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    //주어진 토큰에서 만료 날짜를 추출합니다.
    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
    //토큰에서 특정 클레임을 추출하는 일반적인 메서드 ex)사용자 이름이나 만료 날짜를 추출할 때 사용됩니다.
    public <T> T extractClaim(String token, Function<Claims,T>claimsResolver){
        final Claims claims=extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }
    //주어진 JWT 토큰에서 모든 클레임을 추출함, 이 메서드는 토큰을 파싱하고 서명을 검증
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJwt(token).getBody();
    }
    //토큰이 만료되엇는지 여부를 확인 만료 날짜가 현재 날짜보다 이전이면 토큰이 만료
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    //JWT토큰을 생성 비어있는 클레임 맴과 이름을 기반으로 토큰을 생성
    public String generateToken(String username){
        Map<String, Object> claims=new HashMap<>();
        return createToken(claims,username);
    }
    //실제 JWT토큰을 생성하는 메서드 입니다. 클레임 주체(사용자 이름, 발급시간 , 만료 시간을 설정하고 SECRET_KEY로 서명한뒤에 토큰을 반환)
    private String createToken(Map<String,Object> claims,String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
                .signWith(SignatureAlgorithm.ES256,SECRET_KEY).compact();
    }
    //주어진 토큰이 유효한지 검증하는 메서드입니다. 토큰에서 추출한 사용자 이름과 주어진 사용자이름이 일치, 토큰이 만료되지 않았다면 true 반환

    public Boolean validate(String token, String username){
        final String extractUsername=extractUsername(token);
        return (extractUsername.equals(username)&& !isTokenExpired(token));
    }
}
