package com.scaler.userservice.controllers;

import com.scaler.userservice.dtos.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/login")
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        return null;
    }

    @PostMapping("/signup")
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        return null;
    }

    public UserDto validateToken(String token) {
        return null;
    }

    public void logout(LogoutRequestDto logoutRequestDto) {

    }


}
