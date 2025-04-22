package com.project.shopapp.repository;

import java.util.List;

import com.project.shopapp.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrderId(Long orderId);

    Page<OrderDetail> findAllByOrderId(Long orderId, Pageable pageable);
}
