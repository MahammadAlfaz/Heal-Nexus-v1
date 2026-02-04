package com.healnexus.controller;

import com.healnexus.dto.request.UserRegistrationRequest;
import com.healnexus.model.User;
import com.healnexus.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    ResponseEntity<String> register( @Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User user =new User();
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(userRegistrationRequest.getPassword());
        user.setRole(userRegistrationRequest.getRole());
        userService.registerUser(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }


}
