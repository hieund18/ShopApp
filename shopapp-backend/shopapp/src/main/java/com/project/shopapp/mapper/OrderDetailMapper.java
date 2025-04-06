package com.project.shopapp.mapper;

import com.project.shopapp.dto.request.OrderDetailCreationRequest;
import com.project.shopapp.dto.request.OrderDetailUpdateRequest;
import com.project.shopapp.dto.response.OrderDetailResponse;
import com.project.shopapp.entity.Cart;
import com.project.shopapp.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderDetail toOrderDetail(OrderDetailCreationRequest request);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    OrderDetail updateOrderDetail(@MappingTarget OrderDetail orderDetail, OrderDetailUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(source = "quantity", target = "numberOfProducts")
    OrderDetail cartToOrderDetail(Cart cart);
}
