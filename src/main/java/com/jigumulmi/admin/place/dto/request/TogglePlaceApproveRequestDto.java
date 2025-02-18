package com.jigumulmi.admin.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TogglePlaceApproveRequestDto {

    @Schema(title = "승인 여부", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    private Boolean isApproved;
}
