package com.project.shopapp.repository.specification;

import java.util.ArrayList;
import java.util.List;

import com.project.shopapp.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            String likePattern = "%" + keyword.toLowerCase() + "%";

            // predicate = where
            if (keyword != null && !keyword.trim().isEmpty()) {
                Predicate namePredicate =
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern);
                Predicate phonePredicate =
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), likePattern);
                predicates.add(criteriaBuilder.or(namePredicate, phonePredicate)); // = where (... or ..)
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])); // neu co them dk .. : where (... or ...) and ...
        };
    }
}
