package com.jigumulmi.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageDto {

    private Long totalCount;
    private int currentPage;
    private int totalPage;
}
