package place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TogglePlaceApproveRequestDto {

    @Schema(description = "승인 요청 -> true, 미승인 요청 -> false", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    private Boolean approve;
}
