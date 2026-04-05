package com.example.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {
    String secret = "senlaProjectSecret-secret-key-as-long-as-possible-for-no-error-500";
    private final Long expiration = 86400L;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Authentication auth){
        System.out.println("Authentiaction " + auth);
        for (GrantedAuthority authority : auth.getAuthorities()) {
            System.out.println("Authority " + authority);
        }


        return Jwts.builder()
                .subject(auth.getName()) // "sub":"username"
                .claim("role", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())) // "roles":["ROLE_USER"]
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expiration)))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token){
        try {
            parseToken(token);
            return true;
        }catch (Exception w){
            return false;
        }
    }
}
