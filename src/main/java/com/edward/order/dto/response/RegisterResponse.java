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
public class RegisterResponse {

    private String email;
    private String token;

    private String fullName;

    private String phoneNumber;

    private String address;

    private Gender gender;

    private Role role;

    public static RegisterResponse toResponse(User user, String token) {
        return RegisterResponse.builder()
                .email(user.getEmail())
                .token(token)
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .gender(user.getGender())
                .role(user.getRole())
                .build();
    }
}
