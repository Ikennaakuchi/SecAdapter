package com.example.securityadapter.service;

import com.example.securityadapter.dto.EditProfileRequest;
import com.example.securityadapter.dto.SignUpRequest;

public interface AppUserService {
 String signUp(SignUpRequest signUpRequest);
 String updateProfile(EditProfileRequest request, Long userId);
}
