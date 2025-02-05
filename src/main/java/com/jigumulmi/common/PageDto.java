package com.jigumulmi.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class PageDto {

    private Long totalCount;
    private int currentPage;
    private int totalPage;

    public static PageDto of(Page<?> data, Pageable pageable) {
        return PageDto.builder()
            .totalCount(data.getTotalElements())
            .currentPage(pageable.getPageNumber() + 1)
            .totalPage(data.getTotalPages())
            .build();
    }
}
