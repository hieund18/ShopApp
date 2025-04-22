package com.project.shopapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.dto.request.OrderCreationRequest;
import com.project.shopapp.dto.response.OrderResponse;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private OrderCreationRequest request;
    private OrderResponse orderResponse;

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

        orderResponse = OrderResponse.builder()
                .id(1L)
                .fullName("Nguyen Hoa")
                .email("hoa@gmail.com")
                .phoneNumber("1234567890")
                .address("Ha Noi")
                .note("abc")
                .shippingMethod("Nhanh")
                .shippingAddress("Cau giay")
                .paymentMethod("Cod")
                .isActive(true)
                .build();
    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        when(orderService.createOrder(any())).thenReturn(orderResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(1))
                .andExpect(jsonPath("result.fullName").value("Nguyen Hoa"));
    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_invalidPhoneNumber_fail() throws Exception {
        // GIVEN
        request.setPhoneNumber("123a");
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1402))
                .andExpect(jsonPath("message").value("Phone number must be 10 digits"));
    }

    @Test
    void createOrder_unauthenticated_fail() throws Exception {
        // GIVEN

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1901))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    @Test
    @WithMockUser(username = "hoa")
    void createOrder_userNotFound_fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        when(orderService.createOrder(any())).thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(1406))
                .andExpect(jsonPath("message").value("User not existed"));
    }
}
