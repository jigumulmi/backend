package com.jigumulmi.place.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlaceCategory {
    KOREAN_FOOD("한식"),
    JAPANESE_FOOD("일식"),
    CHINESE_FOOD("중식"),
    WESTERN_FOOD("양식"),
    ASIAN_FOOD("아시안"),
    SANDWICH("샌드위치"),
    SALAD("샐러드"),
    //
    BEVERAGE("음료"),
    SNACK("간식"),
    //
    ZERO_WASTE_SHOP("제로웨이스트샵"),
    RECYCLING_CENTER("재활용센터"),
    ;

    @JsonValue
    private final String title;

    private static final Map<String, PlaceCategory> BY_TITLE = new HashMap<>();

    static {
        for (PlaceCategory category: values()) {
            BY_TITLE.put(category.getTitle(), category);
        }
    }

    @JsonCreator
    public static PlaceCategory ofTitle(String title) {
        return BY_TITLE.get(title);
    }
}
