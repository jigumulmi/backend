package com.jigumulmi.config.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.data.domain.Sort;

public class Utils {

    /**
     * Pageable 객체를  QueryDSL orderBy()에 사용할 수 있게 해주는 메서드
     *
     * @param sort Pageable.getSort()
     * @param path 정렬 대상이 되는 JPA 엔티티에 대응되는 QueryDSL Path 객체
     * @return OrderSpecifier[]
     */
    @SuppressWarnings({"rawtypes", "unchecked", "cast"})
    public static OrderSpecifier[] getOrderSpecifier(Sort sort, Path<?> path) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach(
            s -> {
                Order direction = s.isAscending() ? Order.ASC : Order.DESC;
                SimplePath<Object> fieldPath = Expressions.path(Object.class, path,
                    s.getProperty());
                orders.add(new OrderSpecifier(direction, fieldPath));
            }
        );

        return orders.toArray(OrderSpecifier[]::new);
    }

    /**
     * QueryDSL 조건절 동적 쿼리할 때 조건에 사용되는 인자가 null인 경우 사용
     * <p>
     * 조건들이 체이닝될 때 NPE를 회피할 수 있도록 빈 BooleanBuilder 인스턴스 반환
     */
    public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException | NullPointerException e) {
            return new BooleanBuilder();
        }
    }

}
