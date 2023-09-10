package com.example.securityadapter.config;

import com.example.securityadapter.exception.UnauthorizedException;
import com.example.securityadapter.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@RequiredArgsConstructor
@Component
public class CustomAspect {

    private final HttpServletRequest request;
    private final CacheManager cacheManager;
    private static final Logger log = LoggerFactory.getLogger(CustomAspect.class);
    private final AuthService authService;

    @Around("@annotation(pleaseSecureMe)")
    public Object verifyHeader(ProceedingJoinPoint joinPoint, PleaseSecureMe pleaseSecureMe) {
        log.info("Trying to carry out security checks");
        try {
            if (request != null) {
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String token = authorizationHeader.substring(7);
                    log.info("Token from request: {}", token);

                    String username = authService.getUsernameFromToken(token);
                    String storedToken = Objects.requireNonNull(cacheManager.getCache("token")).get(username, String.class);
                    log.info("Token from cache: {}", storedToken);
                    if (token.equals(storedToken) && authService.verifyToken(token, username)) {
                        return joinPoint.proceed();
                    }
                }
            }
            throw new UnauthorizedException("Unauthorized access");
        } catch (Throwable e) {

            log.error("Error in verifyHeader: " + e.getMessage());
            throw new UnauthorizedException("Unauthorized access");
        }
    }

}
