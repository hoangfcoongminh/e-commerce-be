package com.edward.order.controller;

import com.edward.order.dto.request.LoginRequest;
import com.edward.order.dto.request.RegisterRequest;
import com.edward.order.service.AuthService;
import com.edward.order.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseUtils.success(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseUtils.success(authService.login(loginRequest));
    }
}
