package com.example.securityadapter.controller;

import com.example.securityadapter.dto.AuthRequest;
import com.example.securityadapter.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequest authRequest){
        String response = authService.authenticate(authRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
