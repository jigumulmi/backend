package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.PlaceCategoryMapping;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlaceCategoryDto {

    private PlaceCategoryGroup categoryGroup;
    private PlaceCategory category;

    public static PlaceCategoryDto fromPlaceCategoryMapping(PlaceCategoryMapping mapping) {
        return PlaceCategoryDto.builder().categoryGroup(mapping.getCategoryGroup())
            .category(mapping.getCategory()).build();
    }
}
