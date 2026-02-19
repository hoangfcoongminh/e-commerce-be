package com.edward.order.service;

import com.edward.order.dto.CartDetailDto;
import com.edward.order.dto.CartDto;
import com.edward.order.entity.Cart;
import com.edward.order.entity.CartDetail;
import com.edward.order.entity.Product;
import com.edward.order.exception.BusinessException;
import com.edward.order.repository.CartDetailRepository;
import com.edward.order.repository.CartRepository;
import com.edward.order.repository.ProductRepository;
import com.edward.order.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofDays(7);

    private String key(String cartId) {
        return "cart:" + cartId;
    }

    public Cart getCart(String cartId) {
        return (Cart) redisTemplate.opsForValue().get(key(cartId));
    }

    public Cart getOrCreateCart(String cartId) {
        Cart cart = getCart(cartId);
        if (cart == null) {
            cart = new Cart();
            save(cartId, cart);
        }

        return cart;
    }

    public void save(String cartId, Cart cart) {
        redisTemplate.opsForValue().set(key(cartId), cart, TTL);
    }

    public void delete(String cartId) {
        redisTemplate.delete(key(cartId));
    }

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;

    public CartDto getCart() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(currentUserId);
        List<CartDetail> cartDetails = getCartDetailsByCartId(cart.getId());
        List<CartDetailDto> cartDetailDtos = cartDetails.stream()
                .map(CartDetailDto::toDto)
                .toList();
        return CartDto.toDto(cart, cartDetailDtos);
    }

    @Transactional
    public CartDto addToCart(String slug) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Product product = productRepository.findBySlugAndActive(slug)
                .orElseThrow(() -> new BusinessException("product.not.found"));
        Cart cart = cartRepository.findByUserId(currentUserId);

        Optional<CartDetail> cartDetailOptional = cartDetailRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            cartDetail.setQuantity(cartDetail.getQuantity() + 1);
            cartDetailRepository.save(cartDetail);
        } else {
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCartId(cart.getId());
            cartDetail.setProductId(product.getId());
            cartDetail.setQuantity(1);
            cartDetailRepository.save(cartDetail);
        }
        List<CartDetail> cartDetails = getCartDetailsByCartId(cart.getId());

        List<CartDetailDto> cartDetailDtos = cartDetails.stream()
                .map(CartDetailDto::toDto)
                .toList();
        return CartDto.toDto(cart, cartDetailDtos);
    }

    private List<CartDetail> getCartDetailsByCartId(Long cartId) {
        return cartDetailRepository.findAllByCartId(cartId);
    }
}
