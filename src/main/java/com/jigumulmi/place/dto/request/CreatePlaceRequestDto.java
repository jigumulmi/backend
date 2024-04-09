package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class CreatePlaceRequestDto {
    @NotBlank
    private String name;
    @NotNull
    private Long subwayStationId;
    @NotEmpty
    private List<String> menuList;
    @NotBlank
    private String registrantComment;
}
