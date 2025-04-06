package com.project.shopapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    Long id;
    Long orderId;
    Long productId;
    Float price;
    Integer numberOfProducts;
    Float totalMoney;
    String color;
}
