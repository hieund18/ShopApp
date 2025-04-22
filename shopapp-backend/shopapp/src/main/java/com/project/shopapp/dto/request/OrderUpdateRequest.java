package com.project.shopapp.dto.request;

import java.time.LocalDate;

import com.project.shopapp.validator.ShippingDateConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderUpdateRequest {
    String fullName;
    String email;

    @NotBlank(message = "PHONE_NUMBER_NOT_BLANK")
    @Pattern(regexp = "^[0-9]{10}$", message = "INVALID_PHONE_NUMBER")
    String phoneNumber;

    @NotBlank(message = "ADDRESS_NOT_BLANK")
    String address;

    String note;

    String shippingAddress;

    @ShippingDateConstraint(min = 0, message = "INVALID_SHIPPING_DATE")
    LocalDate shippingDate;

    String trackingNumber;
}
