package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaceRequestDto {

    @NotBlank
    private String name;
    @NotNull
    private Long subwayStationId;
    private List<String> menuList = new ArrayList<>();
    private String registrantComment;
}
