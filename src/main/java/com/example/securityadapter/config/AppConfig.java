package com.example.securityadapter.config;

import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class AppConfig {

    public String hashPassword(String password) {

        try{
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];

            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(salt);
            byte[] hashedBytes = md.digest(password.getBytes());

            byte[] saltAndHash = new byte[salt.length + hashedBytes.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedBytes, 0, saltAndHash, salt.length, hashedBytes.length);

            return Base64.getEncoder().encodeToString(saltAndHash);

        }catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return "Password hashing algorithm not available.";
        }
    }

    public boolean isPasswordCorrect(String inputPassword, String actualPassword){
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(actualPassword.getBytes());
            byte[] salt = new byte[16];
            byte[] hashedPassword = new byte[saltAndHash.length - salt.length];

            System.arraycopy(saltAndHash, 0, salt, 0, salt.length);
            System.arraycopy(saltAndHash, salt.length, hashedPassword, 0, hashedPassword.length);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] hashedInputPassword = md.digest(inputPassword.getBytes());
            return MessageDigest.isEqual(hashedPassword, hashedInputPassword);

        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
