package com.jigumulmi.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jigumulmi.aws.S3Manager;
import com.jigumulmi.common.FileUtils;
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

    @Schema(description = "확장자 제외한 메뉴 이미지 파일 이름")
    private String imageFilename;

    @JsonProperty(access = Access.READ_ONLY)
    private String imageS3Key;

    public static MenuDto from(Menu menu) {
        String imageS3Key = menu.getImageS3Key();
        String imageFilename = FileUtils.getFilenameFromPath(imageS3Key);

        return MenuDto.builder()
            .name(menu.getName())
            .isMain(menu.getIsMain())
            .price(menu.getPrice())
            .description(menu.getDescription())
            .imageFilename(imageFilename)
            .imageS3Key(imageS3Key)
            .build();
    }

    public static Menu toMenu(MenuDto menuDto, Place place) {
        String imageS3Key = null;
        if (menuDto.getImageFilename() != null) {
            String filenameWithoutExtension = FileUtils.getFilenameWithoutExtension(
                menuDto.getImageFilename());
            imageS3Key =
                S3Manager.MENU_IMAGE_S3_PREFIX + place.getId() + "/" + filenameWithoutExtension;
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
