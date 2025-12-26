package com.edward.order.service.user;

import com.edward.order.dto.CartDetailDto;
import com.edward.order.dto.CartDto;
import com.edward.order.dto.ProductDto;
import com.edward.order.dto.request.FilterProductRequest;
import com.edward.order.entity.Cart;
import com.edward.order.entity.CartDetail;
import com.edward.order.entity.Product;
import com.edward.order.repository.CartDetailRepository;
import com.edward.order.repository.CartRepository;
import com.edward.order.repository.ProductRepository;
import com.edward.order.service.admin.ProductAdminService;
import com.edward.order.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductUserService {

    private final ProductRepository productRepository;
    private final ProductAdminService productAdminService;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    public Page<ProductDto> getAll(Pageable pageable) {
        Page<Product> data = productRepository.findAllAndActive(pageable);
        List<ProductDto> productDtos = data.getContent().stream().map(ProductDto::toDto).toList();

        return new PageImpl<>(productDtos, pageable, data.getTotalElements());
    }

    public ProductDto getById(Long id) {
        Product product = productRepository.findByIdAndActive(id)
                .orElseThrow(() -> new RuntimeException("product.not.found"));
        return ProductDto.toDto(product);
    }

    public Page<ProductDto> filter(FilterProductRequest request, Pageable pageable) {
        List<Long> subCategoryIds = request.getSubCategoryIds() == null || request.getSubCategoryIds().isEmpty() ? null : request.getSubCategoryIds();
        String keyword = request.getKeyword() == null || request.getKeyword().isBlank() ? null : request.getKeyword().toLowerCase();
        if (request.getMinPrice() > request.getMaxPrice()) {
            throw new RuntimeException("invalid.price.range");
        }

        Page<Product> data = productRepository.filterProducts(
                subCategoryIds,
                keyword,
                request.getMinPrice(),
                request.getMaxPrice(),
                pageable
        );

        List<ProductDto> response = productAdminService.toResponse(data.getContent());

        return new PageImpl<>(response, pageable, data.getTotalElements());
    }

    @Transactional
    public CartDto addToCart(Long productId, Integer quantity) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Cart cart = cartRepository.findByUserId(currentUserId);
        List<CartDetail> cartDetail = cartDetailRepository.findAllByCartId(cart.getId());

        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setUserId(cart.getUserId());
        cartDto.setCartDetails(cartDetail.stream().map(CartDetailDto::toDto).toList());

        return cartDto;
    }
}
