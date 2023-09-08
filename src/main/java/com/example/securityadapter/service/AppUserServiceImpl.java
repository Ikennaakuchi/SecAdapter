package com.example.securityadapter.service;

import com.example.securityadapter.config.AppConfig;
import com.example.securityadapter.dto.SignUpRequest;
import com.example.securityadapter.entity.AppUser;
import com.example.securityadapter.enums.Roles;
import com.example.securityadapter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService{

    private final UserRepository userRepository;
    private final AppConfig appConfig;

    @Override
    public String signUp(SignUpRequest signUpRequest) {

    try{
        AppUser newUser = AppUser.builder()
                .username(signUpRequest.getUsername())
                .password(appConfig.hashPassword(signUpRequest.getPassword()))
                .role(Roles.USER)
                .build();
        userRepository.save(newUser);
    }catch (Exception ex){
        String message = "An error has occurred it is possible that the username already exists, " +
                "change the username and try again";
        ex.printStackTrace();
        return message;
    }
        return "Sign Up Complete";
    }
}
