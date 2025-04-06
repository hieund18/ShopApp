package com.project.shopapp.mapper;

import com.project.shopapp.dto.response.ProductImageResponse;
import com.project.shopapp.entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageResponse toProductImageResponse(ProductImage productImage);
}
