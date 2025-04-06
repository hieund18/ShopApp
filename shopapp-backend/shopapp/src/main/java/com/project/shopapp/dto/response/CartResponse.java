package com.project.shopapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long id;
    Long userId;
    Long productId;
    int quantity;
    Float price;
    Float totalMoney;
    String color;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
