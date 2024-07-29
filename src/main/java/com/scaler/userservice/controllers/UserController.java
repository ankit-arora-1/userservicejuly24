package com.scaler.userservice.controllers;

import com.scaler.userservice.dtos.*;
import com.scaler.userservice.dtos.ResponseStatus;
import com.scaler.userservice.models.Token;
import com.scaler.userservice.models.User;
import com.scaler.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody  LoginRequestDto loginRequestDto) {
        Token token = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setToken(token);

        return responseDto;
    }

    @PostMapping("/signup")
    public SignUpResponseDto signUp(@RequestBody SignUpRequestDto requestDto) {
        User user = userService.signUp(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        SignUpResponseDto responseDto = new SignUpResponseDto();
        responseDto.setUser(user);
        responseDto.setResponseStatus(ResponseStatus.SUCCESS);

        return responseDto;
    }

    @PostMapping("/validate")
    public UserDto validateToken(@RequestHeader("Authorization") String token) {
        User user = userService.validateToken(token);
        return UserDto.fromUser(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        userService.logout(logoutRequestDto.getToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
