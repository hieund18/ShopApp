package com.project.shopapp.repository.impl;

import java.time.LocalDate;
import java.util.List;

import com.project.shopapp.entity.Order;
import com.project.shopapp.entity.QOrder;
import com.project.shopapp.entity.QUser;
import com.project.shopapp.repository.OrderRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Order> findOrdersByQuerydsl(
            String keyword,
            Float totalMoneyFrom,
            Float totalMoneyTo,
            LocalDate startDate,
            LocalDate endDate,
            Boolean isActive,
            String status,
            Long userId,
            Pageable pageable) {

        QOrder order = QOrder.order;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(order.fullName
                    .containsIgnoreCase(keyword)
                    .or(order.phoneNumber.containsIgnoreCase(keyword))
                    .or(order.address.containsIgnoreCase(keyword)));
        }

        if (totalMoneyFrom != null) builder.and(order.totalMoney.goe(totalMoneyFrom));

        if (totalMoneyTo != null) builder.and(order.totalMoney.loe(totalMoneyTo));

        if (startDate != null) builder.and(order.createdAt.goe(startDate.atStartOfDay()));

        if (endDate != null) builder.and(order.createdAt.loe(endDate.atStartOfDay()));

        if (status != null && !status.isEmpty()) builder.and(order.status.eq(status));

        if (isActive != null) builder.and(order.isActive.eq(isActive));

        if (userId != null) builder.and(order.user.id.eq(userId));

        JPAQuery<Order> query =
                jpaQueryFactory.selectFrom(order).leftJoin(order.user, user).where(builder);

        pageable.getSort().forEach(order1 -> {
            Path<?> path =
                    switch (order1.getProperty()) {
                        case "totalMoney" -> order.totalMoney;
                        default -> order.createdAt;
                    };

            query.orderBy(
                    order1.isAscending()
                            ? ((ComparableExpressionBase<?>) path).asc()
                            : ((ComparableExpressionBase<?>) path).desc());
        });

        // sau khi dung fetchCount bi xoa mat off ser va limit ->loi khi goi fetch sau do
        long total = jpaQueryFactory
                .selectFrom(order)
                .leftJoin(order.user, user)
                .where(builder)
                .fetchCount();

        List<Order> content =
                query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
