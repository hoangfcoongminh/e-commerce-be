package com.edward.order.dto;

import com.edward.order.entity.Cart;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CartDto {

    private Long id;
    private Long userId;
    private List<CartDetailDto> cartDetails;

    public static CartDto toDto(Cart cart, List<CartDetailDto> cartDetails) {
        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .cartDetails(cartDetails)
                .build();
    }
}
