package com.project.shopapp.dto.response;

import java.time.LocalDateTime;

import com.project.shopapp.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    Float price;
    String thumbnail;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer quantity;
    Boolean isActive;
    Category category;
}
