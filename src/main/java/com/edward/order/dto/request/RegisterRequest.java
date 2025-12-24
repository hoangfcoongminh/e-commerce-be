package com.edward.order.dto.request;

import com.edward.order.entity.User;
import com.edward.order.enums.Gender;
import com.edward.order.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email not valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String fullName;

    private String phoneNumber;

    private String address;

    private Gender gender;

    private Role role;

    public static User of(RegisterRequest registerRequest) {
        return User.builder()
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .address(registerRequest.getAddress())
                .gender(registerRequest.getGender())
                .role(registerRequest.getRole())
                .build();
    }
}
