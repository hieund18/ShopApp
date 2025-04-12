package com.project.shopapp.repository;

import com.project.shopapp.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderRepositoryCustom {
    Page<Order> findOrdersByQuerydsl(String keyword, Float totalMoneyFrom, Float totalMoneyTo, LocalDate startDate,
                                     LocalDate endDate, Boolean isActive, String status, Long userId, Pageable pageable);
}
