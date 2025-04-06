package com.project.shopapp.dto.request;

import com.project.shopapp.validator.DobConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String fullName;

    @NotBlank(message = "PASSWORD_NOT_BLANK")
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    String address;

    @DobConstraint(min = 16, message = "INVALID_DOB")
    LocalDate dateOfBirth;
    int facebookAccountId;
    int googleAccountId;
}
