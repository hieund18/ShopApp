package com.project.shopapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.project.shopapp.dto.request.AuthenticationRequest;
import com.project.shopapp.dto.request.IntrospectRequest;
import com.project.shopapp.dto.request.LogoutRequest;
import com.project.shopapp.dto.request.RefreshRequest;
import com.project.shopapp.dto.response.AuthenticationResponse;
import com.project.shopapp.dto.response.IntrospectResponse;
import com.project.shopapp.entity.InvalidatedToken;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.repository.InvalidatedTokenRepository;
import com.project.shopapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/test.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationServiceTest {
    @Autowired
    AuthenticationService authenticationService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    InvalidatedTokenRepository invalidatedTokenRepository;

    AuthenticationRequest request;
    AuthenticationResponse authenticationResponse;
    IntrospectRequest introspectRequest;
    IntrospectResponse introspectResponse;
    LogoutRequest logoutRequest;
    RefreshRequest refreshRequest;
    InvalidatedToken invalidatedToken;
    User user;
    LocalDate dob;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @BeforeEach
    void initData() {
        dob = LocalDate.of(2000, 10, 20);

        request = AuthenticationRequest.builder()
                .phoneNumber("1234567890")
                .password("12345678")
                .build();

        authenticationResponse =
                AuthenticationResponse.builder().token("eyJhbGciOiJIUzUxMiJ9").build();

        introspectRequest = IntrospectRequest.builder()
                .token("eyJhbGciOiJIUzUxMiJ9"
                        + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQ0NzI4NzU1LCJpYXQiOjE3NDQ3MjUxNTUsImp0aSI6IjIxYzQwMjQzLWJjMDUtNDIwMC1hZTFhLWIyMzI1ODJlNmJiYSIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                        + ".01kv5NiMmKUAnbJkGUSOFBtlfeRKLoL_LDd4-MP5Hz2oUx6GskxZj4mpHsU08CM-urz5eDFC17qCismDnFbpGQ")
                .build();

        introspectResponse = IntrospectResponse.builder().valid(true).build();

        logoutRequest = LogoutRequest.builder()
                .token("eyJhbGciOiJIUzUxMiJ9"
                        + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQ0NzI4NzU1LCJpYXQiOjE3NDQ3MjUxNTUsImp0aSI6IjIxYzQwMjQzLWJjMDUtNDIwMC1hZTFhLWIyMzI1ODJlNmJiYSIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                        + ".01kv5NiMmKUAnbJkGUSOFBtlfeRKLoL_LDd4-MP5Hz2oUx6GskxZj4mpHsU08CM-urz5eDFC17qCismDnFbpGQ")
                .build();

        refreshRequest = RefreshRequest.builder()
                .token("eyJhbGciOiJIUzUxMiJ9"
                        + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQ0NzI4NzU1LCJpYXQiOjE3NDQ3MjUxNTUsImp0aSI6IjIxYzQwMjQzLWJjMDUtNDIwMC1hZTFhLWIyMzI1ODJlNmJiYSIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                        + ".01kv5NiMmKUAnbJkGUSOFBtlfeRKLoL_LDd4-MP5Hz2oUx6GskxZj4mpHsU08CM-urz5eDFC17qCismDnFbpGQ")
                .build();

        invalidatedToken = InvalidatedToken.builder()
                .id("2e735e8f-fdcd-4063-b811-c89bb059bd96")
                .expiryTime(new Date(2025, 1, 1))
                .build();

        user = User.builder()
                .id(1L)
                .fullName("Nguyen Hoa")
                .phoneNumber("1234567890")
                .password(passwordEncoder.encode("12345678"))
                .address("Ha Noi")
                .dateOfBirth(dob)
                .isActive(true)
                .build();
    }

    @Test
    void authenticate_validRequest_success() throws ParseException {
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var response = authenticationService.authenticate(request);

        // THEN
        assertThat(response.getToken()).isNotNull();

        SignedJWT signedJWT = SignedJWT.parse(response.getToken());

        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo("1234567890");
        assertThat(signedJWT.getJWTClaimsSet().getExpirationTime()).isNotNull();
    }

    @Test
    void authenticate_userNotFound_fail() {
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);
    }

    @Test
    void authenticate_userDeactivated_fail() {
        // GIVEN
        user.setIsActive(false);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1408);
    }

    @Test
    void authenticate_invalidPassword_fail() {
        // GIVEN
        request.setPassword("1234567");
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1901);
    }

    @Test
    void introspect_validRequest_success() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(false);
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);

        // WHEN
        var response = authenticationService.introspect(introspectRequest);

        // THEN
        assertThat(response.isValid()).isTrue();
    }

    @Test
    void introspect_expiredToken_fail() throws ParseException, JOSEException {
        // GIVEN
        introspectRequest.setToken("eyJhbGciOiJIUzUxMiJ9"
                + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQzODUxMDUxLCJpYXQiOjE3NDM4NDc0NTEsImp0aSI6ImYzZWI4OWJmLTk3MzItNGM1NC1hMDA4LTMyYjA0NjJmODNlZiIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                + ".XBKqVK84KI12A9EzBAgWUj_92SVuZtr6mIftsF8NFbzEEqHAvy_k8z7dEKe176qhusit9U2s3WmFKGN0TFvGDw");

        // WHEN
        var response = authenticationService.introspect(introspectRequest);

        // THEN
        assertThat(response.isValid()).isFalse();
    }

    @Test
    void introspect_invalidToken_fail() throws ParseException, JOSEException {
        // GIVEN
        introspectRequest.setToken("eyJhbGciOiJIUzUxMiJ9"
                + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQzOTA4MjkwLCJpYXQiOjE3NDM5MDQ2OTAsImp0aSI6IjVhNDBiNzVjLTJkMzctNGEzYi05NDEwLTQ0NmU1NWZmYjM0MSIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                + ".zm");

        // WHEN
        var response = authenticationService.introspect(introspectRequest);

        // THEN
        assertThat(response.isValid()).isFalse();
    }

    @Test
    void introspect_deactivatedUser_fail() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(true);

        // WHEN
        var response = authenticationService.introspect(introspectRequest);

        // THEN
        assertThat(response.isValid()).isFalse();
    }

    @Test
    void introspect_revokedToken_fail() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(false);
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(true);

        // WHEN
        var response = authenticationService.introspect(introspectRequest);

        // THEN
        assertThat(response.isValid()).isFalse();
    }

    @Test
    void logout_validRequest_success() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(false);
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);
        when(invalidatedTokenRepository.save(any())).thenReturn(invalidatedToken);

        // WHEN
        authenticationService.logout(logoutRequest);

        // THEN
        verify(invalidatedTokenRepository, times(1)).save(any());
    }

    @Test
    void logout_invalidToken_fail() throws ParseException, JOSEException {
        // GIVEN
        logoutRequest.setToken("eyJhbGciOiJIUzUxMiJ9"
                + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQzOTA4MjkwLCJpYXQiOjE3NDM5MDQ2OTAsImp0aSI6IjVhNDBiNzVjLTJkMzctNGEzYi05NDEwLTQ0NmU1NWZmYjM0MSIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                + ".zm");

        // WHEN
        authenticationService.logout(logoutRequest);

        // THEN
        verify(invalidatedTokenRepository, never()).save(any());
    }

    @Test
    void logout_expiredToken_fail() throws ParseException, JOSEException {
        // GIVEN
        logoutRequest.setToken("eyJhbGciOiJIUzUxMiJ9"
                + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQzODUxMDUxLCJpYXQiOjE3NDM4NDc0NTEsImp0aSI6ImYzZWI4OWJmLTk3MzItNGM1NC1hMDA4LTMyYjA0NjJmODNlZiIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                + ".XBKqVK84KI12A9EzBAgWUj_92SVuZtr6mIftsF8NFbzEEqHAvy_k8z7dEKe176qhusit9U2s3WmFKGN0TFvGDw");

        // WHEN
        authenticationService.logout(logoutRequest);

        // THEN
        verify(invalidatedTokenRepository, never()).save(any());
    }

    @Test
    void logout_deactivatedUser_fail() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(true);

        // WHEN
        authenticationService.logout(logoutRequest);

        // THEN
        verify(invalidatedTokenRepository, never()).save(any());
    }

    @Test
    void logout_revokedToken_fail() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(false);
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(true);

        // WHEN
        authenticationService.logout(logoutRequest);

        // THEN
        verify(invalidatedTokenRepository, never()).save(any());
    }

    @Test
    void refresh_validRequest_success() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(false);
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);
        when(invalidatedTokenRepository.save(any())).thenReturn(invalidatedToken);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var response = authenticationService.refreshToken(refreshRequest);

        // THEN
        assertThat(response.getToken()).isNotNull();

        SignedJWT signedJWT = SignedJWT.parse(response.getToken());

        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo("1234567890");
        assertThat(signedJWT.getJWTClaimsSet().getExpirationTime()).isNotNull();
    }

    @Test
    void refresh_invalidToken_fail() {
        // GIVEN
        refreshRequest.setToken("eyJhbGciOiJIUzUxMiJ9"
                + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQzOTA4MjkwLCJpYXQiOjE3NDM5MDQ2OTAsImp0aSI6IjVhNDBiNzVjLTJkMzctNGEzYi05NDEwLTQ0NmU1NWZmYjM0MSIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                + ".zm");

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.refreshToken(refreshRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1901);
    }

    @Test
    void refresh_expiredToken_fail() {
        // GIVEN
        refreshRequest.setToken("eyJhbGciOiJIUzUxMiJ9"
                + ".eyJpc3MiOiJoaWV1LmNvbSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzQzODUxMDUxLCJpYXQiOjE3NDM4NDc0NTEsImp0aSI6ImYzZWI4OWJmLTk3MzItNGM1NC1hMDA4LTMyYjA0NjJmODNlZiIsInNjb3BlIjoiUk9MRV9VU0VSIFJPTEVfQURNSU4ifQ"
                + ".XBKqVK84KI12A9EzBAgWUj_92SVuZtr6mIftsF8NFbzEEqHAvy_k8z7dEKe176qhusit9U2s3WmFKGN0TFvGDw");

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.refreshToken(refreshRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1901);
    }

    @Test
    void refresh_deactivatedUser_fail() {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.refreshToken(refreshRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1901);
    }

    @Test
    void refresh_revokedToken_fail() {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(false);
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.refreshToken(refreshRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1901);
    }

    @Test
    void refresh_userNotFound_fail() {
        // GIVEN
        when(userRepository.existsByPhoneNumberAndIsActive(anyString(), anyBoolean()))
                .thenReturn(false);
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);
        when(invalidatedTokenRepository.save(any())).thenReturn(invalidatedToken);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> authenticationService.refreshToken(refreshRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);
    }
}
