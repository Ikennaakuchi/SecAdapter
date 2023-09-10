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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
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

        Instant expirationTime = Instant.now().plus(10, ChronoUnit.MINUTES);
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setExpiration(Date.from(expirationTime))
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(secretKey)
                .compact();
    }

    public boolean verifyToken(String token, String username) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jws<Claims> result = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Instant expirationTime = result.getBody().getExpiration().toInstant();
            Instant now = Instant.now();
            if (expirationTime.isBefore(now)) {
                return false;
            }

            return result.getBody().get("ROLE").equals("USER") && result.getBody().getSubject().equals(username);

        } catch (Exception e) {
            log.info("Exception occurred, Token has expired, error: {}", e.getMessage());
            return false;
        }
    }


    public String getUsernameFromToken(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Jws<Claims> result = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build().parseClaimsJws(token);
        return (String) result.getBody().get("sub");
    }

}
