package com.jigumulmi.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jigumulmi.place.PlaceService;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
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

    private String name;

    private Boolean isMain;

    private String price;

    private String description;

    @JsonProperty(access = Access.READ_ONLY)
    private String imageS3Key;

    @Schema(description = "확장자 포함한 파일 이름")
    private String fullFilename;

    public static MenuDto from(Menu menu) {
        return MenuDto.builder()
            .name(menu.getName())
            .isMain(menu.getIsMain())
            .price(menu.getPrice())
            .description(menu.getDescription())
            .imageS3Key(menu.getImageS3Key())
            .build();
    }

    public static Menu toMenu(MenuDto menuDto, Place place) {
        String imageS3Key = null;
        if (menuDto.getFullFilename() != null) {
            imageS3Key = PlaceService.MENU_IMAGE_S3_PREFIX + menuDto.getFullFilename();
        }

        return Menu.builder()
            .name(menuDto.getName())
            .place(place)
            .isMain(menuDto.getIsMain())
            .price(menuDto.getPrice())
            .description(menuDto.getDescription())
            .imageS3Key(imageS3Key)
            .build();
    }
}
