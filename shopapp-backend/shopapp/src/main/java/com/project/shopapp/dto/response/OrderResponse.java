package com.project.shopapp.dto.response;

import com.project.shopapp.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    String fullName;
    String email;
    String phoneNumber;
    String address;
    String note;
    String status;
    Float totalMoney;
    String shippingMethod;
    String shippingAddress;
    LocalDate shippingDate;
    String trackingNumber;
    String paymentMethod;
    Boolean isActive;
    Long userId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
