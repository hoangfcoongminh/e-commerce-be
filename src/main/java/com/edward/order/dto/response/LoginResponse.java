package com.edward.order.dto.response;

import com.edward.order.entity.User;
import com.edward.order.enums.Gender;
import com.edward.order.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {

    private String email;
    private String token;
    private String fullName;
    private String address;
    private String phoneNumber;
    private Gender gender;
    private Role role;

    public static LoginResponse toResponse(User user, String token) {
        return LoginResponse.builder()
                .email(user.getEmail())
                .token(token)
                .fullName(user.getFullName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .role(user.getRole())
                .build();
    }
}
