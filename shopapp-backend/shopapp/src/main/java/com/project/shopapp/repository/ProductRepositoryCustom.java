package com.project.shopapp.repository;

import com.project.shopapp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> findProductsByQuerydsl(
            String keyword, Float fromPrice, Float toPrice, Long categoryId, Boolean isActive, Pageable pageable);
}
