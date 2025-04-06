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
public class OrderDetailUpdateRequest {
    @NotNull(message = "ORDER_QUANTITY_NOT_BLANK")
    @Min(value = 1, message = "MIN_ORDER_QUANTITY")
    Integer numberOfProducts;

    String color;
}
