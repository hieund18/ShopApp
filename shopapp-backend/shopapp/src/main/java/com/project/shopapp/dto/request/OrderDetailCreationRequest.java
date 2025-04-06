package com.project.shopapp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailCreationRequest {
    Long orderId;

    Long productId;

    @DecimalMin(value = "0", message = "MIN_PRODUCT_PRICE")
    Float price;

    @NotNull(message = "ORDER_QUANTITY_NOT_BLANK")
    @Min(value = 1, message = "MIN_ORDER_QUANTITY")
    Integer numberOfProducts;

    @DecimalMin(value = "0", message = "MIN_TOTAL_MONEY")
    Float totalMoney;

    String color;
}
