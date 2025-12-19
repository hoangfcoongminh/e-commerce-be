package com.edward.order.dto;

import com.edward.order.entity.Promotion;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionDto {

    private Long id;

    private String name;

    private String description;

    private Integer discountPercent;

    private Long discountAmount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public static PromotionDto toDto(Promotion promotion) {
        return PromotionDto.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountPercent(promotion.getDiscountPercent())
                .discountAmount(promotion.getDiscountAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }
}
