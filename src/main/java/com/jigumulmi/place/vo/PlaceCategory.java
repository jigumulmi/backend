package com.jigumulmi.place.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlaceCategory {
    KOREAN_FOOD("한식"),
    JAPANESE_FOOD("일식"),
    CHINESE_FOOD("중식"),
    WESTERN_FOOD("양식"),
    //
    BEVERAGE("음료"),
    DESSERT("디저트"),
    BAKERY("베이커리"),
    //
    ZERO_WASTE_SHOP("제로웨이스트샵"),
    RECYCLING_CENTER("재활용센터"),
    ;

    private final String title;
}
