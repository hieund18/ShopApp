package com.project.shopapp.controller;

import com.project.shopapp.dto.request.OrderCreationRequest;
import com.project.shopapp.dto.request.OrderStatusUpdateRequest;
import com.project.shopapp.dto.request.OrderUpdateRequest;
import com.project.shopapp.dto.response.ApiResponse;
import com.project.shopapp.dto.response.OrderResponse;
import com.project.shopapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping
    ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderCreationRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<OrderResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.getOrders(page, size))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<Page<OrderResponse>> searchOrders(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.searchOrders(keyword, page, size))
                .build();
    }

    @GetMapping("/my-orders")
    ApiResponse<Page<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.getMyOrders(page, limit))
                .build();
    }

    @GetMapping("/{orderId}")
    ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrder(orderId))
                .build();
    }

    @PutMapping("/{orderId}")
    ApiResponse<OrderResponse> updateOrder(@PathVariable Long orderId, @RequestBody @Valid OrderUpdateRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrder(orderId, request))
                .build();
    }

    @PatchMapping("/status/{orderId}")
    ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, request))
                .build();
    }

    @PatchMapping("/active-status/{orderId}")
    ApiResponse<OrderResponse> updateOrderActiveStatus(@PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderActiveStatus(orderId))
                .build();
    }

//    @DeleteMapping("/{orderId}")
//    ApiResponse<Void> delete(@PathVariable Long orderId) {
//        orderService.deleteOrder(orderId);
//        return ApiResponse.<Void>builder().build();
//    }
}
