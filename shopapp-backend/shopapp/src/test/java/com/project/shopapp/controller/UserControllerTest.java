package com.project.shopapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.shopapp.dto.request.UserCreationRequest;
import com.project.shopapp.dto.request.UserUpdateRequest;
import com.project.shopapp.dto.response.UserResponse;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@TestPropertySource("/test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest request;
    private UserUpdateRequest userUpdateRequest;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(2000, 10, 20);

        request = UserCreationRequest.builder()
                .fullName("Nguyen Hoa")
                .phoneNumber("1234567890")
                .password("12345678")
                .address("Ha Noi")
                .dateOfBirth(dob)
                .googleAccountId(123)
                .facebookAccountId(123)
                .build();

        userUpdateRequest = UserUpdateRequest.builder()
                .fullName("Nguyen Hoa")
                .password("12345678")
                .address("Ha Noi")
                .dateOfBirth(dob)
                .googleAccountId(123)
                .facebookAccountId(123)
                .build();

        userResponse = UserResponse.builder()
                .id((long) 1)
                .fullName("Nguyen Hoa")
                .phoneNumber("1234567890")
                .address("Ha Noi")
                .dateOfBirth(dob)
                .googleAccountId(123)
                .facebookAccountId(123)
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        when(userService.createUser(any())).thenReturn(userResponse);
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code")
                        .value(1000))
                .andExpect(jsonPath("result.id")
                        .value(1)
                );
    }

    @Test
    void createUser_invalidPhoneNumber_fail() throws Exception {
        // GIVEN
        request.setPhoneNumber("123a");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1402))
                .andExpect(jsonPath("message").value("Phone number must be 10 digits")
                );
    }

    @Test
    void createUser_phoneNumberExisted_fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        when(userService.createUser(any())).thenThrow(new AppException(ErrorCode.PHONE_NUMBER_EXISTED));
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1405))
                .andExpect(jsonPath("message").value("Phone number existed")
                );
    }

    @Test
    void createUser_invalidPassword_fail() throws Exception {
        // GIVEN
        request.setPassword("1234567");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1404))
                .andExpect(jsonPath("message").value("Password must be at least 8 characters")
                );
    }

    @Test
    void createUser_invalidDob_fail() throws Exception {
        // GIVEN
        request.setDateOfBirth(LocalDate.of(2010, 10, 20));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("1407"))
                .andExpect(jsonPath("message").value("Your age must be at least 16")
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_validRequest_success() throws Exception {
        // GIVEN
        Page<UserResponse> userResponses = new PageImpl<>(List.of(userResponse));
        when(userService.getUsers(anyInt(), anyInt())).thenReturn(userResponses);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.content[0].id").value(1)
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_noAdminRole_forbidden() throws Exception {
        // GIVEN
        when(userService.getUsers(anyInt(), anyInt())).thenThrow(new AppException(ErrorCode.UNAUTHORIZED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1902))
                .andExpect(jsonPath("message").value("You do not have permission")
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUsers_validRequest_success() throws Exception {
        // GIVEN
        Page<UserResponse> userResponses = new PageImpl<>(List.of(userResponse));
        when(userService.searchUsers(anyString(), anyInt(), anyInt())).thenReturn(userResponses);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/search")
                        .param("keyword", "Nguyen")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.content[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchUsers_noAdminRole_forbidden() throws Exception {
        // GIVEN
        when(userService.searchUsers(anyString(), anyInt(), anyInt())).thenThrow(new AppException(ErrorCode.UNAUTHORIZED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/search")
                        .param("keyword", "Nguyen")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1902))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }


    @Test
    @WithMockUser(username = "hoa")
    void getMyInfo_validRequest_success() throws Exception {
        // GIVEN
        when(userService.getMyInfo()).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/my-info")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(1));
    }

    @Test
    @WithMockUser(username = "hoa")
    void updateUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userUpdateRequest);
        when(userService.updateUser(anyLong(), any())).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(1))
                .andExpect(jsonPath("result.phoneNumber").value(1234567890));

    }

    @Test
    @WithMockUser(username = "hoa")
    void updateUser_userNotFound_fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userUpdateRequest);
        when(userService.updateUser(anyLong(), any())).thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(jsonPath("code").value(1406))
                .andExpect(jsonPath("message").value("User not existed"));

    }
}
