package com.github.konstantyn111.crashapi.dto.solution;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionPageDto {
    private List<SolutionDTO> content;
    private PageableInfo pageable;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private int numberOfElements;
    private int size;
    private int number;
    private SortInfo sort;
    private boolean empty;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
        private SortInfo sort;
        private int offset;
        private boolean paged;
        private boolean unpaged;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortInfo {
        private boolean sorted;
        private boolean empty;
        private boolean unsorted;
    }

    public static SolutionPageDto fromPage(Page<SolutionDTO> page) {
        PageableInfo pageableInfo = PageableInfo.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .sort(SortInfo.builder()
                        .sorted(false)
                        .empty(true)
                        .unsorted(true)
                        .build())
                .offset((int) page.getPageable().getOffset())
                .paged(true)
                .unpaged(false)
                .build();

        return SolutionPageDto.builder()
                .content(page.getContent())
                .pageable(pageableInfo)
                .last(page.isLast())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .numberOfElements(page.getNumberOfElements())
                .size(page.getSize())
                .number(page.getNumber())
                .sort(pageableInfo.getSort())
                .empty(page.isEmpty())
                .build();
    }
}
