package com.project.shopapp.mapper;

import com.project.shopapp.dto.request.RoleRequest;
import com.project.shopapp.dto.response.RoleResponse;
import com.project.shopapp.entity.Order;
import com.project.shopapp.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    Role updateRole(@MappingTarget Role role, RoleRequest request);
}
