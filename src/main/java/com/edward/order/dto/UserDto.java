package com.edward.order.dto;

import com.edward.order.entity.User;
import com.edward.order.enums.Gender;
import com.edward.order.enums.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

//    private Long id;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private Role role;

    public static UserDto toDto(User user) {
        return UserDto.builder()
//                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .gender(user.getGender())
                .role(user.getRole())
                .build();
    }
}
