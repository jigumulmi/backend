package com.jigumulmi.common;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@SuperBuilder
public class PagedResponseDto<T> {

    @Getter
    @Builder
    public static class PageDto {

        private Long totalCount;
        private int currentPage;
        private int totalPage;

        public static PageDto of(Page<?> page, Pageable pageable) {
            return PageDto.builder()
                .totalCount(page.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .totalPage(page.getTotalPages())
                .build();
        }
    }

    private PageDto page;
    private List<T> data;

    /**
     * Page가 DTO를 감싼 경우 사용
     */
    public static <T> PagedResponseDto<T> of(Page<T> pageData, Pageable pageable) {
        return PagedResponseDto.<T>builder()
            .page(PageDto.of(pageData, pageable))
            .data(pageData.getContent())
            .build();
    }

    /**
     * Page가 DTO가 아닌 엔티티를 감싼 경우 사용
     * 응답 데이터에 직접적으로 사용되는 data 파라미터 추가
     */
    public static <T> PagedResponseDto<T> of(List<T> data, Page<?> page, Pageable pageable) {
        return PagedResponseDto.<T>builder()
            .page(PageDto.of(page, pageable))
            .data(data)
            .build();
    }
}
