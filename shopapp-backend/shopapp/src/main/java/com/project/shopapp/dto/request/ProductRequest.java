package com.project.shopapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotBlank(message = "PRODUCT_NAME_NOT_EMPTY")
    @Size(min = 3, message = "INVALID_PRODUCT_NAME")
    String name;

    @NotNull(message = "PRICE_NOT_BLANK")
    @DecimalMax(value = "10000000", message = "MAX_PRODUCT_PRICE")
    @DecimalMin(value = "0", message = "MIN_PRODUCT_PRICE")
    Float price;
    MultipartFile thumbnail;
    String description;

    @NotNull(message = "QUANTITY_NOT_BLANK")
    @Min(value = 0, message = "MIN_PRODUCT_QUANTITY")
    Integer quantity;

    Long category;
//    MultipartFile image;
}
