package com.edward.order.dto;

import com.edward.order.entity.CartDetail;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetailDto {

    private Long id;
    private Long cartId;
    private Long productId;
    private Integer quantity;

    public static CartDetailDto toDto(CartDetail cartDetail) {
        return CartDetailDto.builder()
                .id(cartDetail.getId())
                .cartId(cartDetail.getCartId())
                .productId(cartDetail.getProductId())
                .quantity(cartDetail.getQuantity())
                .build();
    }
}
