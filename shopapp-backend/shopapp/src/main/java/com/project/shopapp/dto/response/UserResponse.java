package com.project.shopapp.dto.response;

import com.project.shopapp.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String fullName;
    String phoneNumber;
    String address;
    Boolean isActive;
    LocalDate dateOfBirth;
    Integer facebookAccountId;
    Integer googleAccountId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Set<RoleResponse> roles;
}
