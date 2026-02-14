package com.healnexus.controller;

import com.healnexus.dto.request.LoginRequest;
import com.healnexus.dto.request.UserRegistrationRequest;
import com.healnexus.dto.response.APIResponse;
import com.healnexus.dto.response.LoginResponse;
import com.healnexus.model.User;
import com.healnexus.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
        ResponseEntity<APIResponse> register(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User user =new User();
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRole(userRegistrationRequest.getRole());
        userService.registerUser(user);
        return new ResponseEntity<>(new APIResponse("User registered successfully",true), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        return new ResponseEntity<>(new LoginResponse("Login successful"),HttpStatus.OK);
    }


}
