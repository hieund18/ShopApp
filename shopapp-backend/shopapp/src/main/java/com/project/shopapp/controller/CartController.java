package com.project.shopapp.controller;

import com.project.shopapp.dto.request.CartCreationRequest;
import com.project.shopapp.dto.request.CartUpdateRequest;
import com.project.shopapp.dto.response.ApiResponse;
import com.project.shopapp.dto.response.CartResponse;
import com.project.shopapp.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping
    ApiResponse<CartResponse> createCart(@RequestBody @Valid CartCreationRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.createCart(request))
                .build();
    }

    @GetMapping("/my-cart")
    ApiResponse<Page<CartResponse>> getMyCart(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<Page<CartResponse>>builder()
                .result(cartService.getMyCart(page, limit))
                .build();
    }

    @GetMapping("/{cartId}")
    ApiResponse<CartResponse> getCart(@PathVariable Long cartId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(cartId))
                .build();
    }

    @PutMapping("/{cartId}")
    ApiResponse<CartResponse> updateCart(@PathVariable Long cartId, @RequestBody @Valid CartUpdateRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateCart(cartId, request))
                .build();
    }

    @DeleteMapping("/my-cart")
    ApiResponse<Void> deleteMyCart() {
        cartService.deleteMyCart();

        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/{cartId}")
    ApiResponse<Void> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);

        return ApiResponse.<Void>builder().build();
    }
}
