package com.project.shopapp.repository.impl;

import java.time.LocalDate;
import java.util.List;

import com.project.shopapp.entity.QRole;
import com.project.shopapp.entity.QUser;
import com.project.shopapp.entity.User;
import com.project.shopapp.repository.UserRepositoryCustom;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public UserRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<User> findUsersByQuerydsl(
            String keyword, Boolean isActive, LocalDate startDate, LocalDate endDate, Long roleId, Pageable pageable) {

        QUser user = QUser.user;
        QRole role = QRole.role;

        JPAQuery<User> query = jpaQueryFactory.selectFrom(user).distinct();

        if (keyword != null && !keyword.isEmpty()) {
            query.where(user.fullName
                    .containsIgnoreCase(keyword)
                    .or(user.phoneNumber.contains(keyword))
                    .or(user.address.containsIgnoreCase(keyword)));
        }

        if (isActive != null) query.where(user.isActive.eq(isActive));

        if (startDate != null) query.where(user.dateOfBirth.goe(startDate));

        if (endDate != null) query.where(user.dateOfBirth.loe(endDate));

        if (roleId != null) query.innerJoin(user.roles, role).fetchJoin().where(role.id.eq(roleId));

        // fetchCount phai dat truoc offset va limit neu dat sau se xoa mat dk cua query
        long total = query.fetchCount();

        //        for (Sort.Order order : pageable.getSort()) {
        //            PathBuilder<User> pathBuilder = new PathBuilder<>(User.class, "user");
        //
        //            Expression<?> expression = pathBuilder.get(order.getProperty());
        ////            @SuppressWarnings("unchecked")
        ////            OrderSpecifier<?> orderSpecifier = new OrderSpecifier(
        ////                    order.isAscending() ? Order.ASC : Order.DESC,
        ////                    pathBuilder.get(order.getProperty())
        ////            );
        ////            query.orderBy(orderSpecifier);
        //
        //            query.orderBy(order.isAscending()
        //                    ? ((ComparableExpressionBase<?>) expression).asc()
        //                    : ((ComparableExpressionBase<?>) expression).desc());
        //        }

        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                Path<?> path =
                        switch (order.getProperty()) {
                            case "dateOfBirth" -> user.dateOfBirth;
                            default -> user.createdAt;
                        };

                query.orderBy(
                        order.isAscending()
                                ? ((ComparableExpressionBase<?>) path).asc()
                                : ((ComparableExpressionBase<?>) path).desc());
            }
        }

        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        List<User> content = query.fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
