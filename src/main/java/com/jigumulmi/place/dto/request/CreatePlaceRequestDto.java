package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CreatePlaceRequestDto {

    @NotBlank
    private String name;
    @NotNull
    private Long subwayStationId;
    private List<String> menuList;
    private String registrantComment;
}
