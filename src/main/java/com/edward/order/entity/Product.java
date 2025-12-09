package com.edward.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long subCategoryId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Long originalPrice;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, unique = true)
    private String slug;
}
