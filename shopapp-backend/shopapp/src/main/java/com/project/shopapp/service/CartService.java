package com.project.shopapp.service;

import java.util.Optional;

import com.project.shopapp.dto.request.CartCreationRequest;
import com.project.shopapp.dto.request.CartUpdateRequest;
import com.project.shopapp.dto.response.CartResponse;
import com.project.shopapp.entity.Cart;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.CartMapper;
import com.project.shopapp.repository.CartRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    CartMapper cartMapper;

    public CartResponse createCart(CartCreationRequest request) {
        var phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if (!product.getIsActive()) throw new AppException(ErrorCode.INVALID_PRODUCT);

        Optional<Cart> cartOptional = cartRepository.findByUserAndProductAndColor(user, product, request.getColor());

        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            if (request.getQuantity() + cart.getQuantity() > product.getQuantity())
                throw new AppException(ErrorCode.EXCEEDED_QUANTITY_AVAILABLE);

            cart.setQuantity(request.getQuantity() + cart.getQuantity());
            cart.setTotalMoney(cart.getPrice() * cart.getQuantity());

            return cartMapper.toCartResponse(cartRepository.save(cart));
        } else {
            if (request.getQuantity() > product.getQuantity())
                throw new AppException(ErrorCode.EXCEEDED_QUANTITY_AVAILABLE);

            Cart cart = cartMapper.toCart(request);

            cart.setUser(user);
            cart.setProduct(product);
            cart.setPrice(product.getPrice());
            cart.setTotalMoney(product.getPrice() * request.getQuantity());

            return cartMapper.toCartResponse(cartRepository.save(cart));
        }
    }

    public Page<CartResponse> getMyCart(int page, int limit) {
        var phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, limit, Sort.by("updatedAt").descending());

        return cartRepository.findAllByUser(user, pageable).map(cartMapper::toCartResponse);
    }

    public CartResponse getCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        if (!cart.getUser().getPhoneNumber().equals(phoneNumber)) throw new AppException(ErrorCode.UNAUTHORIZED);

        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCart(Long cartId, CartUpdateRequest request) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        var phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!cart.getUser().getPhoneNumber().equals(phoneNumber)) throw new AppException(ErrorCode.UNAUTHORIZED);

        if (!cart.getProduct().getIsActive()) throw new AppException(ErrorCode.INVALID_PRODUCT);

        Optional<Cart> cartOptional = cartRepository.findByUserAndProductAndColorAndIdNot(
                cart.getUser(), cart.getProduct(), request.getColor(), cartId);

        if (cartOptional.isPresent()) {
            Cart cartOld = cartOptional.get();
            if (request.getQuantity() + cartOld.getQuantity()
                    > cartOld.getProduct().getQuantity()) throw new AppException(ErrorCode.EXCEEDED_QUANTITY_AVAILABLE);

            cartOld.setQuantity(request.getQuantity() + cartOld.getQuantity());
            cartOld.setTotalMoney(cartOld.getPrice() * cartOld.getQuantity());

            cartRepository.deleteById(cartId);

            return cartMapper.toCartResponse(cartRepository.save(cartOld));
        } else {
            Product product = cart.getProduct();

            if (request.getQuantity() > product.getQuantity())
                throw new AppException(ErrorCode.EXCEEDED_QUANTITY_AVAILABLE);

            cartMapper.updateCart(cart, request);

            cart.setTotalMoney(cart.getQuantity() * cart.getPrice());

            return cartMapper.toCartResponse(cartRepository.save(cart));
        }
    }

    @Transactional
    public void deleteMyCart() {
        var phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        cartRepository.deleteAllByUser(user);
    }

    public void deleteCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);

        if (cart != null) {
            String phoneNumber =
                    SecurityContextHolder.getContext().getAuthentication().getName();
            if (!cart.getUser().getPhoneNumber().equals(phoneNumber)) throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        cartRepository.deleteById(cartId);
    }

    //    public void updateCartPrice(Product product) {
    //        List<Cart> carts = cartRepository.findAllByProduct(product);
    //
    //        for (Cart cart : carts) {
    //            cart.setPrice(product.getPrice());
    //            cart.setTotalMoney(product.getPrice() * cart.getQuantity());
    //        }
    //
    //        cartRepository.saveAll(carts);
    //    }
}
