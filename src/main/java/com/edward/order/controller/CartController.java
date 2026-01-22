package com.edward.order.controller;

import com.edward.order.service.CartService;
import com.edward.order.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add-to-cart/{slug}")
    public ResponseEntity<?> addToCart(
            @PathVariable String slug
    ) {
        return ResponseUtils.success(cartService.addToCart(slug));
    }

    @GetMapping()
    public ResponseEntity<?> getCart() {
        return ResponseUtils.success(cartService.getCart());
    }
}
