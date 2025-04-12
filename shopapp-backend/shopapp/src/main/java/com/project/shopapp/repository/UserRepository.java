package com.project.shopapp.repository;

import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.User;
import com.project.shopapp.repository.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, UserRepositoryCustom {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Page<User> findAll(Pageable pageable);

    boolean existsByPhoneNumberAndIsActive(String phoneNumber, boolean isActive);
}
