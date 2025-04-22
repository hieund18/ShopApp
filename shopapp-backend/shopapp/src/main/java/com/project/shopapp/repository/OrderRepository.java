package com.project.shopapp.repository;

import com.project.shopapp.entity.Order;
import com.project.shopapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order>, OrderRepositoryCustom {
    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByUser(User user, Pageable pageable);
}
