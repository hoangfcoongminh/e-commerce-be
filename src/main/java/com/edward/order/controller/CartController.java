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

    @PostMapping("/add-to-cart/{slug}/{quantity}")
    public ResponseEntity<?> addToCart(
            @CookieValue(value = "cartToken") String cartToken,
            @PathVariable String slug,
            @PathVariable Integer quantity
    ) {
        return ResponseUtils.success(cartService.addToCart(cartToken, slug, quantity));
    }

    @GetMapping()
    public ResponseEntity<?> getCart(
            @CookieValue(value = "cartToken") String cartToken
    ) {
        return ResponseUtils.success(cartService.getOrCreateCart(cartToken));
    }
}
