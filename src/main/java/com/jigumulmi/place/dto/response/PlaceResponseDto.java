package com.jigumulmi.place.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jigumulmi.place.domain.PlaceCategoryMapping;
import com.jigumulmi.place.domain.PlaceImage;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@NoArgsConstructor
public class PlaceResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryMappingDto {

        private PlaceCategoryGroup categoryGroup;
        private PlaceCategory category;

        public static CategoryMappingDto fromPlaceCategoryMapping(PlaceCategoryMapping mapping) {
            return CategoryMappingDto.builder().categoryGroup(mapping.getCategoryGroup()).category(mapping.getCategory()).build();
        }
    }

    @Getter
    @Builder
    public static class CategoryResponseDto {

        private PlaceCategoryGroup group;
        private List<PlaceCategory> detail;

        public static List<CategoryResponseDto> fromMappingList(
            List<CategoryMappingDto> mappingList) {
            Map<PlaceCategoryGroup, List<PlaceCategory>> categoryMap = new HashMap<>();
            for (CategoryMappingDto mapping : mappingList) {
                PlaceCategoryGroup placeCategoryGroup = mapping.getCategoryGroup();
                List<PlaceCategory> categoryList = categoryMap.getOrDefault(placeCategoryGroup,
                    new ArrayList<>());

                categoryList.add(mapping.getCategory());
                categoryMap.put(placeCategoryGroup, categoryList);
            }

            List<CategoryResponseDto> categoryData = new ArrayList<>();
            for (Map.Entry<PlaceCategoryGroup, List<PlaceCategory>> element : categoryMap.entrySet()) {
                categoryData.add(
                    CategoryResponseDto.builder()
                        .group(element.getKey())
                        .detail(element.getValue())
                        .build()
                );
            }

            return categoryData;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDto {

        private Double latitude;

        private Double longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {

        private Long id;
        private String url;
        private Boolean isMain;

        public static ImageDto from(PlaceImage image) {
            return ImageDto.builder()
                .id(image.getId())
                .url(image.getUrl())
                .isMain(image.getIsMain())
                .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurroundingDateOpeningHour {

        private String yesterday;
        private String today;
        private String tomorrow;
    }


    private Long id;

    private String name;

    @Setter
    private List<ImageDto> imageList;

    private PositionDto position;

    private SubwayStationResponseDto subwayStation;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<CategoryMappingDto> categoryMappingDtoList;

    @Setter
    private List<CategoryResponseDto> category;

    private SurroundingDateOpeningHour surroundingDateOpeningHour;

    @Setter
    private String currentOpeningInfo;
}
