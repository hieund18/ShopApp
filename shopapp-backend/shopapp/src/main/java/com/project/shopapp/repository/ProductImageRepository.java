package com.project.shopapp.repository;

import java.util.List;

import com.project.shopapp.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findAllByProductId(Long productId);

    void deleteAllByProductId(Long productId);
}
