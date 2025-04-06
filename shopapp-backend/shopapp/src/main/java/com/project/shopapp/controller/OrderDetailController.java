package com.project.shopapp.controller;

import com.project.shopapp.dto.request.OrderDetailCreationRequest;
import com.project.shopapp.dto.request.OrderDetailUpdateRequest;
import com.project.shopapp.dto.response.ApiResponse;
import com.project.shopapp.dto.response.OrderDetailResponse;
import com.project.shopapp.service.OrderDetailService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderDetails")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailController {
    OrderDetailService orderDetailService;

    @PostMapping
    ApiResponse<OrderDetailResponse> createOrderDetail(@RequestBody @Valid OrderDetailCreationRequest request) {
        return ApiResponse.<OrderDetailResponse>builder()
                .result(orderDetailService.createOrderDetail(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<OrderDetailResponse>> getOrderDetails() {
        return ApiResponse.<List<OrderDetailResponse>>builder()
                .result(orderDetailService.getOrderDetails())
                .build();
    }

    @GetMapping("/order/{orderId}")
    ApiResponse<Page<OrderDetailResponse>> getOrderDetailsByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.<Page<OrderDetailResponse>>builder()
                .result(orderDetailService.getOrderDetailsByOrderId(orderId, page, limit))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<OrderDetailResponse> getOrderDetail(@PathVariable Long id) {
        return ApiResponse.<OrderDetailResponse>builder()
                .result(orderDetailService.getOrderDetail(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<OrderDetailResponse> updateOrderDetail(@PathVariable Long id, @RequestBody @Valid OrderDetailUpdateRequest request) {
        return ApiResponse.<OrderDetailResponse>builder()
                .result(orderDetailService.updateOrderDetail(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteOrderDetail(@PathVariable Long id) {
        orderDetailService.deleteOrderDetail(id);
        return ApiResponse.<Void>builder().build();
    }
}
