package com.project.shopapp.service;

import java.util.List;

import com.project.shopapp.dto.request.OrderDetailCreationRequest;
import com.project.shopapp.dto.request.OrderDetailUpdateRequest;
import com.project.shopapp.dto.response.OrderDetailResponse;
import com.project.shopapp.entity.Order;
import com.project.shopapp.entity.OrderDetail;
import com.project.shopapp.entity.Product;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.OrderDetailMapper;
import com.project.shopapp.repository.OrderDetailRepository;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailService {
    OrderDetailRepository orderDetailRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderDetailMapper orderDetailMapper;

    public OrderDetailResponse createOrderDetail(OrderDetailCreationRequest request) {
        Order order = orderRepository
                .findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        OrderDetail orderDetail = orderDetailMapper.toOrderDetail(request);

        orderDetail.setOrder(order);
        orderDetail.setProduct(product);

        return orderDetailMapper.toOrderDetailResponse(orderDetailRepository.save(orderDetail));
    }

    public List<OrderDetailResponse> getOrderDetails() {
        return orderDetailRepository.findAll().stream()
                .map(orderDetailMapper::toOrderDetailResponse)
                .toList();
    }

    public Page<OrderDetailResponse> getOrderDetailsByOrderId(Long orderId, int page, int limit) {
        Order order =
                orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var phoneNumber = authentication.getName();
        var authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !phoneNumber.equals(order.getUser().getPhoneNumber()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        Pageable pageable = PageRequest.of(page, limit);

        return orderDetailRepository.findAllByOrderId(orderId, pageable).map(orderDetailMapper::toOrderDetailResponse);
    }

    public OrderDetailResponse getOrderDetail(Long id) {
        OrderDetail orderDetail = orderDetailRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var phoneNumber = authentication.getName();
        var authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !phoneNumber.equals(orderDetail.getOrder().getUser().getPhoneNumber()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        return orderDetailMapper.toOrderDetailResponse(orderDetail);
    }

    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailUpdateRequest orderDetailUpdateRequest) {
        OrderDetail orderDetail = orderDetailRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));

        orderDetailMapper.updateOrderDetail(orderDetail, orderDetailUpdateRequest);

        return orderDetailMapper.toOrderDetailResponse(orderDetailRepository.save(orderDetail));
    }

    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}
