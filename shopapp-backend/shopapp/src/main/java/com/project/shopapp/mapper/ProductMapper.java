package com.project.shopapp.mapper;

import com.project.shopapp.dto.request.ProductRequest;
import com.project.shopapp.dto.response.ProductResponse;
import com.project.shopapp.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    Product toProduct(ProductRequest request);

    ProductResponse toProductResponse(Product product);

    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product updateProduct(@MappingTarget Product product, ProductRequest request);
}
