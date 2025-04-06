package com.project.shopapp.service;

import com.project.shopapp.dto.request.UserCreationRequest;
import com.project.shopapp.dto.request.UserRolesUpdateRequest;
import com.project.shopapp.dto.request.UserUpdateRequest;
import com.project.shopapp.dto.response.RoleResponse;
import com.project.shopapp.dto.response.UserResponse;
import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.repository.specification.UserSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    private UserCreationRequest request;
    private UserUpdateRequest userUpdateRequest;
    private UserResponse userResponse;
    private RoleResponse roleResponse;
    private User user;
    private Role role;
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
                .id(1L)
                .fullName("Nguyen Hoa")
                .phoneNumber("1234567890")
                .address("Ha Noi")
                .dateOfBirth(dob)
                .googleAccountId(123)
                .facebookAccountId(123)
                .isActive(true)
                .build();

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

        role = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        roleResponse = RoleResponse.builder()
                .id(1L)
                .name("USER")
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getFullName()).isEqualTo("Nguyen Hoa");
        assertThat(response.getPhoneNumber()).isEqualTo("1234567890");
    }

    @Test
    void createUser_phoneNumberExisted_fail() {
        // GIVEN

        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(true);
        // WHEN
        var exception = assertThrows(AppException.class,
                () -> userService.createUser(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1405);
    }

    @Test
    void createUser_roleNotFound_fail() {
        // GIVEN
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1202);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_validRequest_success() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // WHEN
        var response = userService.getUsers(0, 10);

        // THEN

        assertThat(response.getContent().get(0).getId()).isEqualTo(1);
        assertThat(response.getContent().get(0).getPhoneNumber()).isEqualTo("1234567890");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUser_validRequest_success() {
        // GIVEN
        int page = 0;
        int size = 10;
        String keyword = "Nguyen";

        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        // WHEN
        var response = userService.searchUsers(keyword, page, size);

        // THEN
        assertThat(response.getContent().get(0).getId()).isEqualTo(1);
        assertThat(response.getContent().get(0).getFullName()).isEqualTo("Nguyen Hoa");

    }

    @Test
    @WithMockUser(username = "hoa")
    void getMyInfo_vailRequest_success() {
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var response = userService.getMyInfo();

        // THEN
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getFullName()).isEqualTo("Nguyen Hoa");
    }

    @Test
    @WithMockUser(username = "hoa")
    void getMyInfo_userNotFound_Error() {
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);
        assertThat(exception.getErrorCode().getMessage()).isEqualTo("User not existed");
    }

    @Test
    @WithMockUser(username = "hoa", roles = "ADMIN")
    void getUser_validRequest_success() {
        // GIVEN
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // WHEN
        var response = userService.getUser(1L);

        // THEN
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getFullName()).isEqualTo("Nguyen Hoa");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUser_userNotFound_fail() {
        // GIVEN
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.getUser(1L));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);

    }

    @Test
//    @WithMockUser(username = "1234567890")
    void updateUser_validRequest_success() {
        // GIVEN
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("1234567890", null));
        SecurityContextHolder.setContext(context);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.updateUser(1L, userUpdateRequest);

        // THEN
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getFullName()).isEqualTo("Nguyen Hoa");

        SecurityContextHolder.clearContext();
    }

    @Test
    void updateUser_userNotFound_fail() {
        // GIVEN
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.updateUser(1L, userUpdateRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);

    }

    @Test
    @WithMockUser(username = "123456789")
    void updateUser_unauthorized_fail() {
        // GIVEN
//        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("123456789", null));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.updateUser(1L, userUpdateRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1902);

        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_validRequest_success() {
        // GIVEN
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.updateUserStatus(1L);

        // THEN
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getIsActive()).isEqualTo(false);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_userNotFound_fail() {
        // GIVEN
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.updateUserStatus(1L));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRoles_validRequest_success() {
        // GIVEN
        UserRolesUpdateRequest userRolesUpdateRequest = UserRolesUpdateRequest.builder()
                .roleIds(Set.of(1L))
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(any())).thenReturn(List.of(role));
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.updateUserRoles(1L, userRolesUpdateRequest);

        // THEN
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getRoles()).extracting(RoleResponse::getName).containsExactlyInAnyOrder("USER");

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRoles_userNotFound_fail() {
        // GIVEN
        UserRolesUpdateRequest userRolesUpdateRequest = UserRolesUpdateRequest.builder()
                .roleIds(Set.of(1L))
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.updateUserRoles(1L, userRolesUpdateRequest));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1406);

    }

}
