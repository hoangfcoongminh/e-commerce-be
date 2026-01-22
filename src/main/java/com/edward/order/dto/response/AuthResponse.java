package com.edward.order.dto.response;

import com.edward.order.dto.UserDto;
import com.edward.order.entity.User;
import com.edward.order.enums.Gender;
import com.edward.order.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String refreshToken;
    private UserDto user;
}
