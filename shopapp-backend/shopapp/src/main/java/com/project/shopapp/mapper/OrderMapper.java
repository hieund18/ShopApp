package com.project.shopapp.mapper;

import com.project.shopapp.dto.request.OrderCreationRequest;
import com.project.shopapp.dto.request.OrderUpdateRequest;
import com.project.shopapp.dto.response.OrderResponse;
import com.project.shopapp.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "user", ignore = true)
    Order toOrder(OrderCreationRequest request);

    @Mapping(source = "user.id", target = "userId")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "shippingDate", ignore = true)
    @Mapping(target = "trackingNumber", ignore = true)
    void updateOrder(@MappingTarget Order order, OrderUpdateRequest request);
}
