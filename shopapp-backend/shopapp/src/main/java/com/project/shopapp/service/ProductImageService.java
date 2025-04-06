package com.project.shopapp.service;

import com.project.shopapp.dto.request.ProductImageRequest;
import com.project.shopapp.dto.response.ProductImageResponse;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.ProductImage;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.ProductImageMapper;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductImageService {
    ProductImageRepository productImageRepository;
    ProductRepository productRepository;
    FileStorageService fileStorageService;
    ProductImageMapper productImageMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductImageResponse> uploadProductImagesByProductId(Long productId, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            log.info("file: name: {}, isEmpty: {}, isNull: {}, files.isEmpty: {}", file.getOriginalFilename(), file.isEmpty(), file == null, files.isEmpty());
        }

        //Neu user khong gui file nhung van tich truong files thi khong null -> can isEmpty(): true
        if (files.stream().allMatch(MultipartFile::isEmpty))
            return Collections.emptyList();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<ProductImage> existedImages = productImageRepository.findAllByProductId(productId);
        if (existedImages.size() + files.size() > 5)
            throw new AppException(ErrorCode.MAX_IMAGE_QUANTITY);

        List<ProductImage> newImages = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                String imageUrl = fileStorageService.storeFile(file);

                ProductImage productImage = ProductImage.builder()
                        .imageUrl(imageUrl)
                        .isPrimary(existedImages.isEmpty() && newImages.isEmpty())
                        .product(product)
                        .build();

                newImages.add(productImage);
            }

            productImageRepository.saveAll(newImages);
        } catch (Exception e) {
            for (ProductImage productImage : newImages) {
                fileStorageService.deleteFile(productImage.getImageUrl());
            }
            throw e;
        }


        return newImages.stream().map(productImageMapper::toProductImageResponse).toList();
    }

    public List<ProductImageResponse> getProductImagesByProductId(Long productId) {
        if (!productRepository.existsById(productId))
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);

        return productImageRepository.findAllByProductId(productId).stream()
                .map(productImageMapper::toProductImageResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductImageResponse updateProductImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));

        if (!productImage.getIsPrimary()) {
            List<ProductImage> productImages = productImageRepository.findAllByProductId(productImage.getProduct().getId());

            for (ProductImage image : productImages) {
                if (image.getIsPrimary())
                    image.setIsPrimary(false);
                productImageRepository.save(image);
            }

            productImage.setIsPrimary(true);
        }

        return productImageMapper.toProductImageResponse(productImageRepository.save(productImage));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProductImage(Long imageId) {
        Optional<ProductImage> productImageOptional = productImageRepository.findById(imageId);

        if (productImageOptional.isPresent()){
            ProductImage productImage = productImageOptional.get();

            try {
                fileStorageService.deleteFile(productImage.getImageUrl());
            }catch (Exception e){
                log.info("Cannot delete images: "+ e.toString());
            }
        }

        productImageRepository.deleteById(imageId);
    }
}
