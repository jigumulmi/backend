package com.jigumulmi.admin.banner.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BannerPlaceMappingRequestDto {

    @NotEmpty
    private List<Long> placeIdList;
}
