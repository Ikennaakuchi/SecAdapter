package com.example.securityadapter.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@RequiredArgsConstructor
@Component
public class CustomAspect {

    private final HttpServletRequest request;
    private final CacheManager cacheManager;

    @Around("@annotation(pleaseSecureMe)")
    public Object verifyHeader(ProceedingJoinPoint joinPoint, PleaseSecureMe pleaseSecureMe) throws Throwable{

        if (request != null) {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                String storedToken = Objects.requireNonNull(cacheManager.getCache("token")).get("userId", String.class);

                if (token.equals(storedToken)){
                    return joinPoint.proceed();
                }
            } else {
                throw new Throwable();
            }
        } else {
            throw new Throwable();
        }
    }
}
