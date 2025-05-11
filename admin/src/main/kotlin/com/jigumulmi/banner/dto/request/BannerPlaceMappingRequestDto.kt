package com.jigumulmi.banner.dto.request

import jakarta.validation.constraints.NotEmpty

data class BannerPlaceMappingRequestDto(
    val placeIdList: @NotEmpty List<Long>
) {
    constructor() : this(emptyList())
}
