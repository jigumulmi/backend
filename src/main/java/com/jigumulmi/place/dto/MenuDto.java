package com.jigumulmi.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jigumulmi.aws.S3Manager;
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

    @Schema(description = "확장자 포함한 파일 이름")
    private String fullFilename;

    @JsonProperty(access = Access.READ_ONLY)
    private String imageS3Key;

    public static MenuDto from(Menu menu) {
        String fullFilename = null;
        String imageS3Key = menu.getImageS3Key();
        if (imageS3Key != null) {
            fullFilename = imageS3Key.substring(imageS3Key.lastIndexOf("/") + 1);
        }

        return MenuDto.builder()
            .name(menu.getName())
            .isMain(menu.getIsMain())
            .price(menu.getPrice())
            .description(menu.getDescription())
            .fullFilename(fullFilename)
            .imageS3Key(imageS3Key)
            .build();
    }

    public static Menu toMenu(MenuDto menuDto, Place place) {
        String imageS3Key = null;
        if (menuDto.getFullFilename() != null) {
            imageS3Key =
                S3Manager.MENU_IMAGE_S3_PREFIX + place.getId() + "/" + menuDto.getFullFilename();
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
