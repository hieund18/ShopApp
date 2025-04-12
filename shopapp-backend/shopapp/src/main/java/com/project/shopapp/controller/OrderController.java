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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping("/find-by-querydsl")
    ApiResponse<Page<OrderResponse>> findOrdersByQuerydsl(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Float totalMoneyFrom,
            @RequestParam(required = false) Float totalMoneyTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.findOrdersByQuerydsl(keyword, totalMoneyFrom, totalMoneyTo,
                        startDate, endDate, status, isActive, userId, pageable))
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
