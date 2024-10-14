package com.jigumulmi.admin.dto.request;

import com.jigumulmi.place.dto.request.GetPlaceListRequestDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@ToString
@NoArgsConstructor
@ParameterObject
public class AdminGetPlaceListRequestDto extends GetPlaceListRequestDto {

    @Parameter(description = "유저 등록 신청 -> false, 관리자 등록 -> true")
    @Schema(defaultValue = "true")
    private Boolean isFromAdmin = true;
}
