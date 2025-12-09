package com.edward.order.entity;

import com.edward.order.enums.PaymentMethodType;
import com.edward.order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private String transactionId;

    private String providerRawResponse; //JSON response từ nguồn thanh toán

    @Column(nullable = false)
    private LocalDateTime paymentDate;
}

