package com.jigumulmi.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String name;

    private Boolean isMain;

    private String price;

    private String description;

    @JsonProperty(access = Access.READ_ONLY)
    private String imageS3Key;

    @Schema(description = "확장자 포함한 파일 이름")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String fullFilename;

    public static MenuDto from(Menu menu) {
        return MenuDto.builder()
            .id(menu.getId())
            .name(menu.getName())
            .isMain(menu.getIsMain())
            .price(menu.getPrice())
            .description(menu.getDescription())
            .imageS3Key(menu.getImageS3Key())
            .build();
    }
}
