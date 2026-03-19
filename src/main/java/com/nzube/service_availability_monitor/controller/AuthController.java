package com.nzube.service_availability_monitor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nzube.service_availability_monitor.dto.auth.AuthResponse;
import com.nzube.service_availability_monitor.dto.auth.LoginRequest;
import com.nzube.service_availability_monitor.dto.auth.RegisterRequest;
import com.nzube.service_availability_monitor.dto.user.UserResponse;
import com.nzube.service_availability_monitor.entity.User;
import com.nzube.service_availability_monitor.exception.BadRequestException;
import com.nzube.service_availability_monitor.mapper.UserMapper;
import com.nzube.service_availability_monitor.security.util.JwtUtil;
import com.nzube.service_availability_monitor.service.RedisService;
import com.nzube.service_availability_monitor.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Registration and login")
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        System.out.println("Registering user: " + request);
        return ResponseEntity.ok(userMapper.toResponse(userService.register(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // check if blocked first
        if (redisService.isLoginBlocked(request.email())) {
            throw new BadRequestException("Too many failed login attempts. Try again in 30 minutes");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            redisService.recordFailedLoginAttempt(request.email()); // record failed attempt
            throw new BadCredentialsException("Invalid email or password");
        }

        // clear attempts on successful login
        redisService.clearLoginAttempts(request.email());
        User user = userService.getUserByEmail(request.email());
        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(user)));
    }
}
