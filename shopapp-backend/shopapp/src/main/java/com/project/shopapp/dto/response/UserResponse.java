package com.project.shopapp.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String githubAccountId;
    String googleAccountId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Boolean noPassword;
    Set<RoleResponse> roles;
}
