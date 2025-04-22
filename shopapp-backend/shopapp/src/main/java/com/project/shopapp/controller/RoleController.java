package com.project.shopapp.controller;

import java.util.List;

import com.project.shopapp.dto.request.RoleRequest;
import com.project.shopapp.dto.response.ApiResponse;
import com.project.shopapp.dto.response.RoleResponse;
import com.project.shopapp.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getRoles())
                .build();
    }

    @PutMapping("/{roleId}")
    ApiResponse<RoleResponse> updateRole(@PathVariable Long roleId, @RequestBody @Valid RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRole(roleId, request))
                .build();
    }

    @DeleteMapping("/{roleId}")
    ApiResponse<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ApiResponse.<Void>builder().build();
    }
}
