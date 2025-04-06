package com.project.shopapp.service;

import com.project.shopapp.dto.request.OrderCreationRequest;
import com.project.shopapp.dto.request.OrderStatusUpdateRequest;
import com.project.shopapp.dto.request.OrderUpdateRequest;
import com.project.shopapp.dto.response.OrderResponse;
import com.project.shopapp.entity.*;
import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.OrderDetailMapper;
import com.project.shopapp.mapper.OrderMapper;
import com.project.shopapp.repository.*;
import com.project.shopapp.repository.specification.OrderSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    UserRepository userRepository;
    OrderDetailRepository orderDetailRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;
    OrderDetailMapper orderDetailMapper;
    OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderCreationRequest request) {
        var phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getIsActive())
            throw new AppException(ErrorCode.DEACTIVATED_USER);

        List<Cart> carts = cartRepository.findAllByUserId(user.getId());
        if (carts.isEmpty())
            throw new AppException(ErrorCode.CART_EMPTY);

        for (Cart cart : carts) {
            Product product = cart.getProduct();

            if (!product.getIsActive())
                throw new AppException(ErrorCode.INVALID_PRODUCT);

            if (cart.getQuantity() > product.getQuantity())
                throw new AppException(ErrorCode.EXCEEDED_QUANTITY_AVAILABLE);
        }

        Order order = orderMapper.toOrder(request);

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING.name());
        order.setShippingDate(LocalDate.now().plusDays(3));
        order.setIsActive(true);
        order.setTotalMoney((float) carts.stream().mapToDouble(Cart::getTotalMoney).sum());

        order = orderRepository.save(order);

        for (Cart cart : carts) {
            OrderDetail orderDetail = orderDetailMapper.cartToOrderDetail(cart);

            orderDetail.setOrder(order);
            orderDetail.setNumberOfProducts(cart.getQuantity());

            orderDetailRepository.save(orderDetail);

            Product product = cart.getProduct();
            product.setQuantity(product.getQuantity() - cart.getQuantity());
            productRepository.save(product);
        }

        cartRepository.deleteAll(carts);

        return orderMapper.toOrderResponse(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponse> getOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return orderRepository.findAll(pageable).map(orderMapper::toOrderResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponse> searchOrders(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Order> orderSpecification = OrderSpecification.searchByKeyword(keyword);

        return orderRepository.findAll(orderSpecification, pageable).map(orderMapper::toOrderResponse);
    }

    public Page<OrderResponse> getMyOrders(int page, int limit) {
        var phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        return orderRepository.findAllByUser(user, pageable).map(orderMapper::toOrderResponse);
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var phoneNumber = authentication.getName();
        var authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !phoneNumber.equals(order.getUser().getPhoneNumber()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse updateOrder(Long orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getStatus().equals(OrderStatus.SHIPPED.name()) ||
                order.getStatus().equals(OrderStatus.DELIVERED.name()) ||
                order.getStatus().equals(OrderStatus.CANCELLED.name()))
            throw new AppException(ErrorCode.CANNOT_MODIFY_ORDER);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var phoneNumber = authentication.getName();
        var authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !phoneNumber.equals(order.getUser().getPhoneNumber()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        if (!order.getStatus().equals(OrderStatus.PENDING.name()) && !isAdmin)
            throw new AppException(ErrorCode.CANNOT_MODIFY_ORDER);

        if ((request.getShippingDate() != null || request.getTrackingNumber() != null) && !isAdmin)
            throw new AppException(ErrorCode.CANNOT_MODIFY_ORDER);

        if (request.getShippingDate() != null)
            order.setShippingDate(request.getShippingDate());

        if (request.getTrackingNumber() != null)
            order.setTrackingNumber(request.getTrackingNumber());

        orderMapper.updateOrder(order, request);

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        try {
            OrderStatus nextStatus = OrderStatus.valueOf(request.getStatus());

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

            OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());

            var authentication = SecurityContextHolder.getContext().getAuthentication();
            var phoneNumber = authentication.getName();
            var authorities = authentication.getAuthorities();
            boolean isAdmin = authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !phoneNumber.equals(order.getUser().getPhoneNumber()))
                throw new AppException(ErrorCode.UNAUTHORIZED);

            if (!isAdmin && !isValidUserTransaction(currentStatus, nextStatus))
                throw new AppException(ErrorCode.INVALID_STATE_TRANSITION);

            if (isAdmin && !isValidAdminTransaction(currentStatus, nextStatus))
                throw new AppException(ErrorCode.INVALID_STATE_TRANSITION);

            if (currentStatus == OrderStatus.SHIPPED && nextStatus == OrderStatus.DELIVERED)
                order.setShippingDate(LocalDate.now());

            order.setStatus(nextStatus.name());
            orderRepository.save(order);

            if ((currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PROCESSING) && nextStatus == OrderStatus.CANCELLED) {
                List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(orderId);

                for (OrderDetail orderDetail : orderDetails) {
                    Product product = orderDetail.getProduct();

                    product.setQuantity(product.getQuantity() + orderDetail.getNumberOfProducts());
                    productRepository.save(product);
                }
            }

            return orderMapper.toOrderResponse(order);
        } catch (IllegalArgumentException exception) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateOrderActiveStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (!order.getStatus().equals(OrderStatus.DELIVERED.name()))
            throw new AppException(ErrorCode.INVALID_ACTIVE_STATUS);

        order.setIsActive(!order.getIsActive());

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    private boolean isValidAdminTransaction(OrderStatus current, OrderStatus next) {
        if (current == next)
            return true;

        return (current == OrderStatus.PENDING && (next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED)) ||
                (current == OrderStatus.PROCESSING && (next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED)) ||
                (current == OrderStatus.SHIPPED && next == OrderStatus.DELIVERED);
    }

    private boolean isValidUserTransaction(OrderStatus current, OrderStatus next) {
        if (current == next)
            return true;

        switch (current) {
            case PENDING:
                return next == OrderStatus.CANCELLED;
            case SHIPPED:
                return next == OrderStatus.DELIVERED;
            default:
                return false;
        }
    }
}
