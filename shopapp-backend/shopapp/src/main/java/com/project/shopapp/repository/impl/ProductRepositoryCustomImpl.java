package com.project.shopapp.repository.impl;

import java.util.List;

import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.QCategory;
import com.project.shopapp.entity.QProduct;
import com.project.shopapp.repository.ProductRepositoryCustom;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Product> findProductsByQuerydsl(
            String keyword, Float fromPrice, Float toPrice, Long categoryId, Boolean isActive, Pageable pageable) {

        QProduct product = QProduct.product;
        QCategory category = QCategory.category;

        JPAQuery<Product> query = jpaQueryFactory.selectFrom(product);

        if (keyword != null && !keyword.isEmpty()) {
            query.where(product.name.containsIgnoreCase(keyword).or(product.description.containsIgnoreCase(keyword)));
        }

        if (fromPrice != null) query.where(product.price.goe(fromPrice));

        if (toPrice != null) query.where(product.price.loe(toPrice));

        if (isActive != null) {
            query.where(product.isActive.eq(isActive));
        }

        // leftJoin hay innerJoin nhu nhau vi chi join khi id != null -> co where
        if (categoryId != null) {
            query.innerJoin(product.category, category).where(category.id.eq(categoryId));
        }

        long total = query.fetchCount();

        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                Path<?> path =
                        switch (order.getProperty()) {
                            case "price" -> product.price;
                            default -> product.createdAt;
                        };

                query.orderBy(
                        order.isAscending()
                                ? ((ComparableExpressionBase<?>) path).asc()
                                : ((ComparableExpressionBase<?>) path).desc());
            });
        }

        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        List<Product> content = query.fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
