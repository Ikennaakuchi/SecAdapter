package com.example.securityadapter.service;

import com.example.securityadapter.config.AppConfig;
import com.example.securityadapter.dto.AuthRequest;
import com.example.securityadapter.entity.AppUser;
import com.example.securityadapter.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${myJwtSecret}")
    private String jwtSecret;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final AppConfig appConfig;

    @Cacheable(value = "token", key = "#request.username")
    public String authenticate(AuthRequest request){

            AppUser foundUser = userRepository.findByUsername(request.getUsername());

            if (foundUser != null && appConfig.isPasswordCorrect(request.getPassword(), foundUser.getPassword())){
                Map<String, Object> claims = new HashMap<>();
                claims.put("ROLE", foundUser.getRole());

                String token = createToken(request.getUsername(), claims);

                Cache cache = cacheManager.getCache("token");
                if (cache != null){
                    cache.put(request.getUsername(), token);
                }

                return token;
            }else{
                return "Wrong username or password";
            }

    }

    private String createToken(String username, Map<String, Object> claims){

        Instant expirationTime = Instant.now().plus(20, ChronoUnit.MINUTES);
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setExpiration(Date.from(expirationTime))
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenCorrect(String token, String username){
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Jws<Claims> result = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build().parseClaimsJws(token);
        return (result.getBody().get("ROLE").equals("USER") && result.getBody().get("sub").equals(username));
    }

}
