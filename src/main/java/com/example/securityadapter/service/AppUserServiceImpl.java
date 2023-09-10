package com.example.securityadapter.service;

import com.example.securityadapter.config.AppConfig;
import com.example.securityadapter.dto.EditProfileRequest;
import com.example.securityadapter.dto.SignUpRequest;
import com.example.securityadapter.entity.AppUser;
import com.example.securityadapter.enums.Roles;
import com.example.securityadapter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public String updateProfile(EditProfileRequest request, Long userId) {
        String message = null;
        Optional<AppUser> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()){
            AppUser user = foundUser.get();

            if (request.getUsername() != null){
                user.setUsername(request.getUsername());
                message = "Username Changed Successfully";
            }
        }else{
            message = "Not found";
        }
        return message;
    }
}
