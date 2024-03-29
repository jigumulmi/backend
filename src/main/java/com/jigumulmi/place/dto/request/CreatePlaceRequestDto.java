package com.jigumulmi.place.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreatePlaceRequestDto {
    private String name;
    private Long subway_station_id;
    private List<String> menuList;
    private String registrantComment;
}
