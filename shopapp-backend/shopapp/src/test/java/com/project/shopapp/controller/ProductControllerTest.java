package com.project.shopapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.shopapp.dto.request.ProductRequest;
import com.project.shopapp.dto.response.ProductResponse;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.service.ProductService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductRequest request;
    private ProductResponse productResponse;

    @BeforeEach
    void initData() {
        request = ProductRequest.builder()
                .name("Samsung")
                .price(10F)
                .description("abc")
                .quantity(10000)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Samsung")
                .price(10F)
                .description("abc")
                .quantity(10000)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_vailRequest_success() throws Exception {
        // GIVEN
        when(productService.createProduct(any())).thenReturn(productResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/products")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .param("name", "Samsung")
                        .param("price", "10")
                        .param("description", "abc")
                        .param("quantity", "10000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(1)
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_noAdminRole_fail() throws Exception {
        // GIVEN
        when(productService.createProduct(any())).thenThrow(new AppException(ErrorCode.UNAUTHORIZED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/products")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .param("name", "Samsung")
                        .param("price", "10")
                        .param("description", "abc")
                        .param("quantity", "10000"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1902))
                .andExpect(jsonPath("message").value("You do not have permission")
                );
    }
}
