package com.project.shopapp.repository;

import java.util.Optional;

import com.project.shopapp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Optional<Role> findByName(String name);
}
