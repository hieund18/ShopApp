package com.project.shopapp.controller;

import java.util.List;

import com.project.shopapp.dto.response.ApiResponse;
import com.project.shopapp.dto.response.ProductImageResponse;
import com.project.shopapp.service.ProductImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageController {
    ProductImageService productImageService;

    @PostMapping("/product/{productId}")
    ApiResponse<List<ProductImageResponse>> uploadProductImagesByProductId(
            @PathVariable Long productId, @RequestParam("files") List<MultipartFile> files) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.uploadProductImagesByProductId(productId, files))
                .build();
    }

    @GetMapping("/product/{productId}")
    ApiResponse<List<ProductImageResponse>> getProductImagesByProductId(@PathVariable Long productId) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.getProductImagesByProductId(productId))
                .build();
    }

    @PutMapping("/{imageId}")
    ApiResponse<ProductImageResponse> updateProductImage(@PathVariable Long imageId) {
        return ApiResponse.<ProductImageResponse>builder()
                .result(productImageService.updateProductImage(imageId))
                .build();
    }

    @DeleteMapping("/{imageId}")
    ApiResponse<Void> deleteProductImage(@PathVariable Long imageId) {
        productImageService.deleteProductImage(imageId);
        return ApiResponse.<Void>builder().build();
    }
}
