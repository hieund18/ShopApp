package com.project.shopapp.service;

import java.util.List;

import com.project.shopapp.dto.request.ProductRequest;
import com.project.shopapp.dto.response.ProductResponse;
import com.project.shopapp.entity.Cart;
import com.project.shopapp.entity.Category;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.ProductImage;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.ProductMapper;
import com.project.shopapp.repository.CartRepository;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    CartRepository cartRepository;
    ProductImageRepository productImageRepository;
    FileStorageService fileStorageService;
    ProductMapper productMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) throw new AppException(ErrorCode.PRODUCT_EXISTED);

        Product product = productMapper.toProduct(request);

        Category category = categoryRepository
                .findById(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        product.setCategory(category);

        String imageUrl = "";
        // Neu user khong gui file nhung van tich truong files thi khong null -> can isEmpty(): true
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty())
            imageUrl = fileStorageService.storeFile(request.getThumbnail());

        product.setThumbnail(imageUrl);

        product.setIsActive(true);

        return productMapper.toProductResponse(productRepository.save(product));
    }

    public Page<ProductResponse> getProductsByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return productRepository.findByKeyword(keyword, pageable).map(productMapper::toProductResponse);
    }

    public Page<ProductResponse> findProductsByQuerydsl(
            String keyword, Float fromPrice, Float toPrice, Boolean isActive, Long categoryId, Pageable pageable) {
        return productRepository
                .findProductsByQuerydsl(keyword, fromPrice, toPrice, categoryId, isActive, pageable)
                .map(productMapper::toProductResponse);
    }

    public ProductResponse getProduct(Long productId) {
        return productMapper.toProductResponse(productRepository
                .findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Category category = categoryRepository
                .findById(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.PRODUCT_EXISTED);

        float oldPrice = product.getPrice();

        productMapper.updateProduct(product, request);

        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty())
            product.setThumbnail(fileStorageService.storeFile(request.getThumbnail()));

        product.setCategory(category);

        productRepository.save(product);

        List<Cart> carts = cartRepository.findAllByProduct(product);

        if (oldPrice != product.getPrice()) {
            for (Cart cart : carts) {
                cart.setPrice(product.getPrice());
                cart.setTotalMoney(product.getPrice() * cart.getQuantity());
            }

            cartRepository.saveAll(carts);
        }

        return productMapper.toProductResponse(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProductStatus(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        product.setIsActive(!product.getIsActive());

        return productMapper.toProductResponse(productRepository.save(product));
    }

    public void deleteProduct(Long productId) {
        List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);
        log.info("{}", productImages.size());

        productImageRepository.deleteAll(productImages);

        productRepository.deleteById(productId);

        for (ProductImage productImage : productImages) {
            try {
                fileStorageService.deleteFile(productImage.getImageUrl());
            } catch (Exception e) {
                log.info("Cannot delete images: " + e.toString());
            }
        }
    }
}
