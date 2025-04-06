package com.project.shopapp.dto.request;

import com.project.shopapp.validator.DobConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String fullName;

    @NotBlank(message = "PHONE_NUMBER_NOT_BLANK")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    String phoneNumber;

    @NotBlank(message = "PASSWORD_NOT_BLANK")
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    String address;

    @DobConstraint(min = 16, message = "INVALID_DOB")
    LocalDate dateOfBirth;
    int facebookAccountId;
    int googleAccountId;
}
