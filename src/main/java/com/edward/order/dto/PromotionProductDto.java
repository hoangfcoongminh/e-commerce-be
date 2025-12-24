package com.edward.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProductDto {

    private Long productId;
    private String name;
    private String description;
    private Integer discountPercent;
    private Long discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
