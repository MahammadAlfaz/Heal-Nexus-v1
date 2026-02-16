package com.healnexus.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {


    @Value("${jwt.secret}")
    private  String JWT_SECRET;

    private  Key key;

    private static final   long EXPIRATION_TIME=15*60*1000;

    @PostConstruct
    public void init(){
        this.key= Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }



    public String generateToken(String email,String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public Claims extractClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();
    }
    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }
    public String extractRole(String token){
        return extractClaims(token).get("role",String.class);
    }
    public boolean validateToken(String token, UserDetails userDetails){

        return extractEmail(token).equals(userDetails.getUsername()) && !isTokenExpired(token);

    }
    public boolean isTokenExpired(String token){
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }


}
