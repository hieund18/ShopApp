package com.project.shopapp.mapper;

import com.project.shopapp.dto.request.CategoryRequest;
import com.project.shopapp.dto.response.CategoryResponse;
import com.project.shopapp.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);

    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}
