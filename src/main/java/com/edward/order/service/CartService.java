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
import com.edward.order.service.user.ProductUserService;
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
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final ProductUserService productUserService;

    private static final Duration TTL = Duration.ofDays(7);

    private String key(String cartId) {
        return "cart:" + cartId;
    }

    public Cart getCart(String cartId) {
        return (Cart) redisTemplate.opsForValue().get(key(cartId));
    }

    public Cart getOrCreateCart(String cartToken) {
        Long userId = SecurityUtils.getCurrentUserId();
        Optional<Cart> cartOpt;

        if (userId != null) {
            cartOpt = cartRepository.findByUserIdAndCheckedOutFalse(userId);
        } else {
            cartOpt = cartRepository.findByCartTokenAndCheckedOutFalse(cartToken);
        }

        if (cartOpt.isPresent()) {
            return cartOpt.get();
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCartToken(cartToken);
        return cartRepository.save(cart);
    }

    public void save(String cartId, Cart cart) {
        redisTemplate.opsForValue().set(key(cartId), cart, TTL);
    }

    public void delete(String cartId) {
        redisTemplate.delete(key(cartId));
    }

    @Transactional
    public String addToCart(String cartToken, String slug, Integer quantity) {

        Cart cart = getOrCreateCart(cartToken);

        Long productId = productRepository.findBySlugAndActive(slug)
                .orElseThrow(() -> new BusinessException("product.not.found"))
                .getId();

        CartDetail detail = cartDetailRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        long currentPrice = productUserService.getCurrentPrice(productId);

        if (detail == null) {
            detail = new CartDetail();
            detail.setCartId(cart.getId());
            detail.setProductId(productId);
            detail.setQuantity(quantity);
            detail.setSnapShotPrice(currentPrice);
        } else {
            detail.setQuantity(detail.getQuantity() + quantity);
        }

        cartDetailRepository.save(detail);

        return "success";
    }

    private List<CartDetail> getCartDetailsByCartId(Long cartId) {
        return cartDetailRepository.findAllByCartId(cartId);
    }
}
