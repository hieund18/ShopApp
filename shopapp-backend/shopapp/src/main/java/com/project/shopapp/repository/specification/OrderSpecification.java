package com.project.shopapp.repository.specification;

import com.project.shopapp.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            String likePattern = "%" + keyword.toLowerCase() + "%";

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));// = where ... or ...
        };
    }
}
