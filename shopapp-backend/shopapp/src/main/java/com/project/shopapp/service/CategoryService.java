package com.project.shopapp.service;

import com.project.shopapp.dto.request.CategoryRequest;
import com.project.shopapp.dto.response.CategoryResponse;
import com.project.shopapp.entity.Category;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.mapper.CategoryMapper;
import com.project.shopapp.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);

        Category category = categoryMapper.toCategory(request);

        category = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    public CategoryResponse getCategory(Long categoryId) {

        return categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)));
    }

    public List<CategoryResponse> getCategories(){
        return categoryRepository.findAll().stream().map(categoryMapper::toCategoryResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long categoryId,CategoryRequest request){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        if(categoryRepository.existsByNameAndIdNot(request.getName(), categoryId))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);

        categoryMapper.updateCategory(category, request);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long categoryId){
        categoryRepository.deleteById(categoryId);
    }
}
