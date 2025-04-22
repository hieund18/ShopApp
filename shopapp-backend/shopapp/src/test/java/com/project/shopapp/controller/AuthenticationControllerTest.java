package com.project.shopapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.dto.request.AuthenticationRequest;
import com.project.shopapp.dto.request.IntrospectRequest;
import com.project.shopapp.dto.request.LogoutRequest;
import com.project.shopapp.dto.request.RefreshRequest;
import com.project.shopapp.dto.response.AuthenticationResponse;
import com.project.shopapp.dto.response.IntrospectResponse;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;
    private IntrospectRequest introspectRequest;
    private IntrospectResponse introspectResponse;
    private RefreshRequest refreshRequest;
    private LogoutRequest logoutRequest;

    @BeforeEach
    void initData() {
        authenticationRequest = AuthenticationRequest.builder()
                .phoneNumber("1234567890")
                .password("12345678")
                .build();

        authenticationResponse =
                AuthenticationResponse.builder().token("eyJhbGciOiJIUzUxMiJ9").build();

        introspectRequest =
                IntrospectRequest.builder().token("eyJhbGciOiJIUzUxMiJ9").build();

        introspectResponse = IntrospectResponse.builder().valid(true).build();

        refreshRequest = RefreshRequest.builder().token("eyJhbGciOiJIUzUxMiJ9").build();

        logoutRequest = LogoutRequest.builder().token("eyJhbGciOiJIUzUxMiJ9").build();
    }

    @Test
    void authenticate_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        when(authenticationService.authenticate(any())).thenReturn(authenticationResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.token").value("eyJhbGciOiJIUzUxMiJ9"));
    }

    @Test
    void authenticate_userNotFound_fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        when(authenticationService.authenticate(any())).thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(1406))
                .andExpect(jsonPath("message").value("User not existed"));
    }

    @Test
    void authenticate_deactivatedUser_fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        when(authenticationService.authenticate(any())).thenThrow(new AppException(ErrorCode.DEACTIVATED_USER));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1408))
                .andExpect(jsonPath("message").value("User is deactivated"));
    }

    @Test
    void authenticate_unauthenticated_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        when(authenticationService.authenticate(any())).thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1901))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    @Test
    void introspect_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(introspectRequest);

        when(authenticationService.introspect(any())).thenReturn(introspectResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.valid").value(true));
    }

    @Test
    void introspect_invalidRequest_fail() throws Exception {
        // GIVEN
        introspectResponse.setValid(false);
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(introspectRequest);

        when(authenticationService.introspect(any())).thenReturn(introspectResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.valid").value(false));
    }

    @Test
    void refresh_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(refreshRequest);

        when(authenticationService.refreshToken(any())).thenReturn(authenticationResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.token").value("eyJhbGciOiJIUzUxMiJ9"));
    }

    @Test
    void refresh_unauthenticated_fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(refreshRequest);

        when(authenticationService.refreshToken(any())).thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1901))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    @Test
    void refresh_userNotFound_fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(refreshRequest);

        when(authenticationService.refreshToken(any())).thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(1406))
                .andExpect(jsonPath("message").value("User not existed"));
    }

    @Test
    void logout_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(logoutRequest);

        doNothing().when(authenticationService).logout(any());

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("message").doesNotExist());
    }
}
