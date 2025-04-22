package com.project.shopapp.repository;

import java.util.List;
import java.util.Optional;

import com.project.shopapp.entity.Cart;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Page<Cart> findAllByUser(User user, Pageable pageable);

    void deleteAllByUser(User user);

    Optional<Cart> findByUserAndProductAndColor(User user, Product product, String color);

    Optional<Cart> findByUserAndProductAndColorAndIdNot(User user, Product product, String color, Long id);

    List<Cart> findAllByProduct(Product product);

    List<Cart> findAllByUserId(Long userId);
}
