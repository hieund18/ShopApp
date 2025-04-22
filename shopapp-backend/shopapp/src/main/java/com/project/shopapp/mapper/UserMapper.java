package com.project.shopapp.mapper;

import com.project.shopapp.dto.request.UserCreationRequest;
import com.project.shopapp.dto.request.UserUpdateRequest;
import com.project.shopapp.dto.response.UserResponse;
import com.project.shopapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
