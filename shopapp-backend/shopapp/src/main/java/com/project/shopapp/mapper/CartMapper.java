package com.project.shopapp.mapper;

import com.project.shopapp.dto.request.CartCreationRequest;
import com.project.shopapp.dto.request.CartUpdateRequest;
import com.project.shopapp.dto.response.CartResponse;
import com.project.shopapp.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    Cart toCart(CartCreationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")
    CartResponse toCartResponse(Cart cart);

    void updateCart(@MappingTarget Cart cart, CartUpdateRequest request);
}
