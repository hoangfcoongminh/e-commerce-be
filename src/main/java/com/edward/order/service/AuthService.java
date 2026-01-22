package com.edward.order.service;

import com.edward.order.dto.UserDto;
import com.edward.order.dto.request.LoginRequest;
import com.edward.order.dto.request.RegisterRequest;
import com.edward.order.dto.response.AuthResponse;
import com.edward.order.entity.Cart;
import com.edward.order.entity.User;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.CartRepository;
import com.edward.order.repository.UserRepository;
import com.edward.order.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CartRepository cartRepository;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        validateRegister(registerRequest);
        User user = RegisterRequest.of(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cart cart = new Cart(null, user.getId());
        cartRepository.save(cart);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(UserDto.toDto(user));

        return response;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(UserDto.toDto(user));

        return response;
    }

    public void validateRegister(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(user -> {
                    throw new BusinessException("Email already exists");
                });
    }
}
