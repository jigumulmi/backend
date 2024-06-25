package com.jigumulmi.config.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

public class Utils {

    /**
     * Pageable 객체를  Querydsl의 orberBy()에 사용할 수 있게 해주는 메서드
     *
     * @param sort Pageable.getSort()
     * @param path 정렬 대상이 되는 JPA 엔티티에 대응되는 Querydsl의 Path 객체
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

}
