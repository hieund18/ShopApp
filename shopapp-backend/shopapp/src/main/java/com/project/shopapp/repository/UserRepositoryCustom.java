package com.project.shopapp.repository;

import java.time.LocalDate;

import com.project.shopapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<User> findUsersByQuerydsl(
            String keyword, Boolean isActive, LocalDate startDate, LocalDate endDate, Long roleId, Pageable pageable);
}
