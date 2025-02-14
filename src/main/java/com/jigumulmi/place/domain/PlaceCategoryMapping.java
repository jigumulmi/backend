package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category_group", "category", "place_id"})})
public class PlaceCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @JsonBackReference
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(30)")
    private PlaceCategory category;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(30)")
    private PlaceCategoryGroup categoryGroup;

    @Builder
    public PlaceCategoryMapping(Place place, PlaceCategory category,
        PlaceCategoryGroup categoryGroup) {
        this.place = place;
        this.category = category;
        this.categoryGroup = categoryGroup;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer()
            .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer()
            .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        PlaceCategoryMapping mapping = (PlaceCategoryMapping) o;
        return getId() != null && Objects.equals(getId(), mapping.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer()
            .getPersistentClass().hashCode() : getClass().hashCode();
    }

    // TODO 일반적으로 사용할 수 있는 공통 클래스 생성
    public static List<PlaceCategoryMapping> getIntersectionDerivedFromLeftListElements(
        List<PlaceCategoryMapping> leftList, List<PlaceCategoryMapping> rightList) {
        List<PlaceCategoryMapping> intersection = new ArrayList<>();
        for (PlaceCategoryMapping leftEntity : leftList) {
            if (rightList.contains(leftEntity)) {
                intersection.add(leftEntity);
            }
        }
        return intersection;
    }

    /**
     * JPA 더티체킹으로 데이터 덮어쓸 때 DB 고유키 제약조건에 위배되지 않게 컬렉션 조작
     * <p>
     * 실제 DB 쿼리는 어플리케이션 코드와 다르게 insert가 delete보다 먼저 실행되므로
     * 덮어쓰고자 하는 요청 데이터에 저장되어있는 데이터가 포함된 경우
     * 이미 저장된 데이터에 해당하는 엔티티로 대체하는 로직
     *
     * @param listFromDB      이미 DB에 저장된 엔티티 리스트
     * @param listFromRequest 덮어쓰고자 하는 새로운 엔티티 리스트
     * @return 중복된 데이터는 기존 저장된 데이터로 대체된 엔티티 리스트
     */
    public static List<PlaceCategoryMapping> getCategoryMappingListToOverwrite(
        List<PlaceCategoryMapping> listFromDB, List<PlaceCategoryMapping> listFromRequest) {

        List<PlaceCategoryMapping> intersectionFromDBElements = getIntersectionDerivedFromLeftListElements(
            listFromDB, listFromRequest);

        listFromRequest.removeAll(listFromDB);
        listFromRequest.addAll(intersectionFromDBElements);

        return listFromRequest;
    }
}