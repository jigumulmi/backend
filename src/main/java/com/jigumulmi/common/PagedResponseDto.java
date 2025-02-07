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
     * Page 인스턴스의 contents 데이터가 완벽히 구성된 경우 사용
     */
    public static <T> PagedResponseDto<T> of(Page<T> pageData, Pageable pageable) {
        return PagedResponseDto.<T>builder()
            .page(PageDto.of(pageData, pageable))
            .data(pageData.getContent())
            .build();
    }

    /**
     * Page 인스턴스의 contents 데이터가 완성되지 않은 경우 사용
     * @param data 페이지네이션 적용된 최종 응답 데이터
     */
    public static <T> PagedResponseDto<T> of(List<T> data, Page<?> page, Pageable pageable) {
        return PagedResponseDto.<T>builder()
            .page(PageDto.of(page, pageable))
            .data(data)
            .build();
    }
}
