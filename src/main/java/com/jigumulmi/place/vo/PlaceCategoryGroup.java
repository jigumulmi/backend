package com.jigumulmi.place.vo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlaceCategoryGroup {

    RESTAURANT("음식점", Arrays.asList(PlaceCategory.KOREAN_FOOD, PlaceCategory.JAPANESE_FOOD, PlaceCategory.CHINESE_FOOD, PlaceCategory.WESTERN_FOOD)),
    CAFE("카페", Arrays.asList(PlaceCategory.BEVERAGE, PlaceCategory.BAKERY, PlaceCategory.DESSERT)),
    ZERO_WASTE_SHOP("제로웨이스트샵", List.of(PlaceCategory.ZERO_WASTE_SHOP)),
    RECYCLING_CENTER("재활용센터", List.of(PlaceCategory.ZERO_WASTE_SHOP)),
    EMPTY("없음", Collections.EMPTY_LIST)
    ;

    private final String title;
    private final List<PlaceCategory> placeCategoryList;

    public static PlaceCategoryGroup findByPlaceCategory(PlaceCategory placeCategory){
        return Arrays.stream(PlaceCategoryGroup.values())
            .filter(categoryGroup -> categoryGroup.hasPayCode(placeCategory))
            .findAny()
            .orElse(EMPTY);
    }

    public boolean hasPayCode(PlaceCategory placeCategory){
        return placeCategoryList.stream()
            .anyMatch(x -> x == placeCategory);
    }
}
