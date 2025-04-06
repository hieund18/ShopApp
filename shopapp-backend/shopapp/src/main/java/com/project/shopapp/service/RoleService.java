package com.project.shopapp.service;

import com.project.shopapp.dto.request.RoleRequest;
import com.project.shopapp.dto.response.RoleResponse;
import com.project.shopapp.entity.Role;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.RoleMapper;
import com.project.shopapp.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse createRole(RoleRequest request){
        if(roleRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROLE_EXISTED);

        Role role = roleMapper.toRole(request);

        role = roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getRoles(){
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse updateRole(Long roleId, RoleRequest request){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        if(roleRepository.existsByNameAndIdNot(request.getName(), roleId))
            throw new AppException(ErrorCode.ROLE_EXISTED);

        roleMapper.updateRole(role, request);

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(Long roleId){
        roleRepository.deleteById(roleId);
    }
}
