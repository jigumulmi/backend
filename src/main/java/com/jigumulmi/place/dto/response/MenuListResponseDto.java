package com.jigumulmi.place.dto.response;

import com.jigumulmi.common.PageDto;
import com.jigumulmi.place.dto.MenuDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuListResponseDto {

    private PageDto page;
    private List<MenuDto> data;
}
