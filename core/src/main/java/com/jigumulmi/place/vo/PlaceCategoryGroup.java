package com.jigumulmi.place.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlaceCategoryGroup {

    RESTAURANT("음식점",
        Arrays.asList(
            PlaceCategory.KOREAN_FOOD, PlaceCategory.JAPANESE_FOOD, PlaceCategory.CHINESE_FOOD,
            PlaceCategory.WESTERN_FOOD, PlaceCategory.ASIAN_FOOD, PlaceCategory.SANDWICH,
            PlaceCategory.SALAD
        )
    ),
    CAFE("카페", Arrays.asList(PlaceCategory.BEVERAGE, PlaceCategory.SNACK)),
    ZERO_WASTE_SHOP("제로웨이스트샵", List.of(PlaceCategory.ZERO_WASTE_SHOP)),
    RECYCLING_CENTER("재활용센터", List.of(PlaceCategory.RECYCLING_CENTER)),
    ;

    @JsonValue
    private final String title;
    private final List<PlaceCategory> placeCategoryList;

    private static final Map<String, PlaceCategoryGroup> BY_TITLE = new HashMap<>();

    static {
        for (PlaceCategoryGroup categoryGroup: values()) {
            BY_TITLE.put(categoryGroup.getTitle(), categoryGroup);
        }
    }

    @JsonCreator
    public static PlaceCategoryGroup ofTitle(String title) {
        return BY_TITLE.get(title);
    }
}
