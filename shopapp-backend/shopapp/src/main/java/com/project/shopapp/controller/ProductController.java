package com.project.shopapp.controller;

import com.project.shopapp.dto.request.ProductRequest;
import com.project.shopapp.dto.response.ApiResponse;
import com.project.shopapp.dto.response.ProductResponse;
import com.project.shopapp.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<ProductResponse> createProduct(@ModelAttribute @Valid ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<ProductResponse>> getProductsByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.getProductsByKeyword(keyword, page, size))
                .build();
    }

    @GetMapping("/find-by-querydsl")
    ApiResponse<Page<ProductResponse>> findProductsByQuerydsl(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Float fromPrice,
            @RequestParam(required = false) Float toPrice,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.findProductsByQuerydsl(
                        keyword, fromPrice, toPrice, isActive, categoryId, pageable))
                .build();
    }

    @GetMapping("/{productId}")
    ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(productId))
                .build();
    }

    @PutMapping("/{productId}")
    ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long productId, @ModelAttribute @Valid ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(productId, request))
                .build();
    }

    @PatchMapping("/status/{productId}")
    ApiResponse<ProductResponse> updateProductStatus(@PathVariable Long productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProductStatus(productId))
                .build();
    }

    @DeleteMapping("/{productId}")
    ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ApiResponse.<Void>builder().build();
    }
}
