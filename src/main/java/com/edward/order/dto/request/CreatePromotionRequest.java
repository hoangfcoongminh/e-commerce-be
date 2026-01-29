package com.edward.order.dto.request;

import com.edward.order.entity.Promotion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreatePromotionRequest {

    @NotBlank(message = "Promotion name is required")
    private String name;
    private String description;

    @Max(value = 80, message = "Discount percent cannot exceed 80")
    private Integer discountPercent;

    private Long discountAmount;

    @NotNull(message = "Start date is required")
    private String startDate;

    @NotNull(message = "End date is required")
    private String endDate;

    private List<Long> productIds;

    public static Promotion of(CreatePromotionRequest request) {
        return Promotion.builder()
                .name(request.getName())
                .description(request.getDescription())
                .discountPercent(request.getDiscountPercent())
                .discountAmount(request.getDiscountAmount())
                .startDate(java.time.LocalDateTime.parse(request.getStartDate()))
                .endDate(java.time.LocalDateTime.parse(request.getEndDate()))
                .build();
    }
}
