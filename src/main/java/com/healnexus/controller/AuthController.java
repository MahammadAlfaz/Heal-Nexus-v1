package com.healnexus.controller;

import com.healnexus.dto.request.LoginRequest;
import com.healnexus.dto.request.UserRegistrationRequest;
import com.healnexus.dto.response.APIResponse;
import com.healnexus.dto.response.LoginResponse;
import com.healnexus.model.Role;
import com.healnexus.model.User;
import com.healnexus.repositories.UserRepository;
import com.healnexus.security.LoginAttemptService;
import com.healnexus.security.jwt.JwtService;
import com.healnexus.security.refresh.RefreshToken;
import com.healnexus.security.refresh.RefreshTokenRepository;
import com.healnexus.security.refresh.RefreshTokenService;
import com.healnexus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/register")
        ResponseEntity<APIResponse> register(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        User user =new User();
        user.setEmail(userRegistrationRequest.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRole(userRegistrationRequest.getRole());
        user.setActive(true);
        userService.registerUser(user);
        return new ResponseEntity<>(new APIResponse("User registered successfully",true), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest){
        String ip = servletRequest.getHeader("X-Forwarded-For");
        if(ip == null) ip = servletRequest.getRemoteAddr();
        if(loginAttemptService.isBlocked(ip)){
            log.warn("Blocked login attempt from IP {}", ip);
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new LoginResponse("Too many login attempts. Try later.", null));
        }

        String email = request.getEmail().trim().toLowerCase();
        User user=userService.findByEmail(email);
        if(!user.isActive()) throw new IllegalStateException("User is not active");
        if(user.isAccountLocked()) {
            boolean unlock=userService.unlockIfLockExpires(user);
            if(!unlock){
                throw new IllegalStateException("Account has been locked try again later...");
            }

        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                          email,
                            request.getPassword()
                    )
            );
            loginAttemptService.loginSuccess(ip);
            userService.resetFailedLoginAttempts(user);
            String role=authentication.getAuthorities().iterator().next().getAuthority();
            String token= jwtService.generateToken(email, role );
            RefreshToken refreshToken=refreshTokenService.createRefreshToken(email);
            ResponseCookie cookie=ResponseCookie.from("refreshToken", refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/api/users/refresh")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Lax")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new LoginResponse("Login Sucessfull",token));
        }catch (Exception e){
            loginAttemptService.loginFailed(ip);
            userService.increaseFailedLoginAttempts(user);
            log.warn("Login failed for email: {}, attempt count: {}",
                    user.getEmail(),
                    user.getFailedLoginAttempts());
            throw new IllegalArgumentException("Invalid email or password");
        }


    }
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(name = "refreshToken",required = false) String token){

        if(token==null){
            return new ResponseEntity<>(new LoginResponse("Refresh token misiing",null),HttpStatus.UNAUTHORIZED);
        }
        RefreshToken refreshToken=refreshTokenService.validateRefreshToken(token);
        String email=refreshToken.getEmail();
        User user=userService.findByEmail(email);
        String role=user.getRole().name();
        String newAccessToken= jwtService.generateToken(email,role);
        RefreshToken newRefreshToken=refreshTokenService.createRefreshToken(email);

        ResponseCookie cookie=ResponseCookie.from("refreshToken",newRefreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/api/users/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse("Token refreshed",newAccessToken));
    }
    @PostMapping("/logout")
    public ResponseEntity<APIResponse> logout(@CookieValue(name = "refreshToken",required = false)String token){
        if(token!=null){
            refreshTokenService.deleteRefreshToken(token);
        }
        ResponseCookie cookie=ResponseCookie.from("refreshToken","")
                .path("/api/users/refresh")
                .maxAge(0)
                .secure(false)
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(new APIResponse("Logout successful",true));
    }


}
