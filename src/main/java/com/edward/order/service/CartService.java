package com.edward.order.service;

import com.edward.order.dto.CartDto;
import com.edward.order.repository.CartDetailRepository;
import com.edward.order.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

//    public CartDto updateCart()
}
