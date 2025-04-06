package com.project.shopapp.service;

import com.project.shopapp.dto.request.OrderCreationRequest;
import com.project.shopapp.entity.*;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource("/test.properties")
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private OrderDetailRepository orderDetailRepository;

    private OrderCreationRequest request;
    private User user;
    private Product product;
    private Product product2;
    private Cart cart;
    private Cart cart2;
    private Order order;
    private OrderDetail orderDetail;
    private OrderDetail orderDetail2;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        request = OrderCreationRequest.builder()
                .fullName("Nguyen Hoa")
                .email("hoa@gmail.com")
                .phoneNumber("1234567890")
                .address("Ha Noi")
                .note("abc")
                .shippingMethod("Nhanh")
                .shippingAddress("Cau giay")
                .paymentMethod("Cod")
                .build();

        dob = LocalDate.of(2000, 10, 20);

        user = User.builder()
                .id(1L)
                .fullName("Nguyen Hoa")
                .phoneNumber("1234567890")
                .address("Ha Noi")
                .dateOfBirth(dob)
                .googleAccountId(123)
                .facebookAccountId(123)
                .isActive(true)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Samsung")
                .quantity(10000)
                .price(10F)
                .description("abc")
                .isActive(true)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Sony")
                .quantity(20000)
                .price(20F)
                .description("abc")
                .isActive(true)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .product(product)
                .quantity(30)
                .price(10F)
                .totalMoney(300F)
                .build();

        cart2 = Cart.builder()
                .id(2L)
                .user(user)
                .product(product2)
                .quantity(10)
                .price(20F)
                .totalMoney(200F)
                .build();

        order = Order.builder()
                .id(1L)
                .fullName("Nguyen Hoa")
                .email("hoa@gmail.com")
                .phoneNumber("1234567890")
                .address("Ha Noi")
                .note("abc")
                .shippingMethod("Nhanh")
                .shippingAddress("Cau giay")
                .paymentMethod("Cod")
                .totalMoney(500F)
                .isActive(true)
                .build();

        orderDetail = OrderDetail.builder()
                .id(1L)
                .product(product)
                .order(order)
                .numberOfProducts(30)
                .price(10F)
                .totalMoney(300F)
                .build();

        orderDetail2 = OrderDetail.builder()
                .id(2L)
                .product(product2)
                .order(order)
                .numberOfProducts(10)
                .price(20F)
                .totalMoney(200F)
                .build();
    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_validRequest_success() {
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findAllByUserId(anyLong())).thenReturn(List.of(cart, cart2));
        when(orderRepository.save(any())).thenReturn(order);
        // khong dung gia tri tra ve thi khong mock (productRepository.save, orderDetailRepository.save, cartRepository.deleteAll)
//        when(orderDetailRepository.save(any())).thenReturn(orderDetail);

        // WHEN
        var response = orderService.createOrder(request);

        // THEN
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getPhoneNumber()).isEqualTo("1234567890");
        verify(orderDetailRepository, times(2)).save(any());
        verify(productRepository, times(1)).save(argThat(product1 -> product1.getQuantity() == 9970));
        verify(productRepository, times(1)).save(argThat(p -> p.getQuantity() == 19990));
        verify(cartRepository, times(1)).deleteAll(anyList());

    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_userNotFound_fail(){
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        // WHEN
        var exception = assertThrows(AppException.class, () -> orderService.createOrder(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);

    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_deactivatedUser_fail(){
        // GIVEN
        user.setIsActive(false);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var exception = assertThrows(AppException.class, () -> orderService.createOrder(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1408);

    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_cartEmpty_fail(){
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        // WHEN
        var exception = assertThrows(AppException.class, () -> orderService.createOrder(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1606);

    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_inactiveProduct_fail(){
        // GIVEN
        product.setIsActive(false);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findAllByUserId(anyLong())).thenReturn(List.of(cart, cart2));

        // WHEN
        var exception = assertThrows(AppException.class, () -> orderService.createOrder(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1803);

    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_exceededQuantity_fail(){
        // GIVEN
        cart.setQuantity(100000);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findAllByUserId(anyLong())).thenReturn(List.of(cart, cart2));

        // WHEN
        var exception = assertThrows(AppException.class, () -> orderService.createOrder(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1801);

    }
}
