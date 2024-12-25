
package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
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
    private PlaceCategory category;

    @Enumerated(EnumType.STRING)
    private PlaceCategoryGroup categoryGroup;

    @Builder
    public PlaceCategoryMapping(Place place, PlaceCategory category,
        PlaceCategoryGroup categoryGroup) {
        this.place = place;
        this.category = category;
        this.categoryGroup = categoryGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlaceCategoryMapping that = (PlaceCategoryMapping) o;
        return getCategory() == that.getCategory() && getCategoryGroup() == that.getCategoryGroup();
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCategory());
        result = 31 * result + Objects.hashCode(getCategoryGroup());
        return result;
    }

    public static List<PlaceCategoryMapping> getIntersectionWithElementsFromLeft(
        List<PlaceCategoryMapping> leftList, List<PlaceCategoryMapping> rightList) {
        List<PlaceCategoryMapping> intersection = new ArrayList<>();
        for (PlaceCategoryMapping leftEntity : leftList) {
            if (rightList.contains(leftEntity)) {
                intersection.add(leftEntity);
            }
        }
        return intersection;
    }
}