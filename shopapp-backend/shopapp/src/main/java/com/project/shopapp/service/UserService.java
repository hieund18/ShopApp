package com.project.shopapp.service;

import com.project.shopapp.dto.request.UserCreationRequest;
import com.project.shopapp.dto.request.UserRolesUpdateRequest;
import com.project.shopapp.dto.request.UserUpdateRequest;
import com.project.shopapp.dto.response.UserResponse;
import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.UserMapper;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.repository.specification.UserSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber()))
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);

        User user = userMapper.toUser(request);

        Role role = roleRepository.findByName(com.project.shopapp.enums.Role.USER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        user.setIsActive(true);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<UserResponse> getUsers(int page, int size) {
        log.info("In method get users");

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<User> userSpecification = UserSpecification.searchByKeyword(keyword);

        return userRepository.findAll(userSpecification, pageable).map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> findUsersByQuerydsl(String keyword, Boolean isActive, LocalDate startDate,
                                                  LocalDate endDate, Long roleId, Pageable pageable
    ) {
        return userRepository.findUsersByQuerydsl(keyword, isActive, startDate, endDate, roleId, pageable)
                .map(userMapper::toUserResponse);
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.phoneNumber == authentication.name")
    public UserResponse getUser(Long userId) {
        log.info("In method get user by userId");
        return userMapper.toUserResponse(userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        var phoneNumber = context.getAuthentication().getName();

        return userMapper.toUserResponse(userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var authenticate = SecurityContextHolder.getContext().getAuthentication();
        var phoneNumber = authenticate.getName();

        if (!phoneNumber.equals(user.getPhoneNumber()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserStatus(Long userId) {
        log.info("In method updateUserStatus");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setIsActive(!user.getIsActive());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserRoles(Long userId, UserRolesUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
