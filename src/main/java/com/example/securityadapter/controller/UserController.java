package com.example.securityadapter.controller;

import com.example.securityadapter.config.PleaseSecureMe;
import com.example.securityadapter.dto.EditProfileRequest;
import com.example.securityadapter.dto.SignUpRequest;
import com.example.securityadapter.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class UserController {

    private final AppUserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest){
        String response = userService.signUp(signUpRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    @PleaseSecureMe
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody EditProfileRequest request){
        String response = userService.updateProfile(request, userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/test")
    @PleaseSecureMe
    public ResponseEntity<?> test(){
        String response = "Hello World";
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test2")
    public ResponseEntity<?> test2(){
        String response = "Hello World UnSecured";
        return ResponseEntity.ok(response);
    }
}
