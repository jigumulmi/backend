package com.jigumulmi.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminSavePlaceBasicRequestDto {

    private Long placeId;
    private String googlePlaceId;
}
