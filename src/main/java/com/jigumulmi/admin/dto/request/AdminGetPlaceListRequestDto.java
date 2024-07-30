package com.jigumulmi.admin.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@ToString
@NoArgsConstructor
@ParameterObject
public class AdminGetPlaceListRequestDto {

    @Parameter(description = "1부터 시작하는 페이지 번호")
    @Schema(defaultValue = "1")
    private int page = 1;

    @Parameter(description = "id 기준 오름차순/내림차순")
    @Schema(defaultValue = "ASC")
    private Direction direction = Direction.ASC;
    
    @Parameter(description = "장소 검색어, 검색어로 시작하는 장소 조회")
    private String placeName;
}
