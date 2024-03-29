package com.jigumulmi.place.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreatePlaceRequestDto {
    private String name;
    private Long subway_station_id;
    private List<String> menuList;
    private String registrantComment;
}
