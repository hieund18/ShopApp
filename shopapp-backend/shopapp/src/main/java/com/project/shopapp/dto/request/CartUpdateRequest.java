package com.project.shopapp.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartUpdateRequest {

    @NotNull(message = "QUANTITY_NOT_BLANK")
    @Min(value = 0, message = "MIN_PRODUCT_QUANTITY")
    Integer quantity;

    String color;
}
