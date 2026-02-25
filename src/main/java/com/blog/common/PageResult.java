package com.blog.common;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private Pagination pagination;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination {
        private int page;
        private int pageSize;
        private long total;
        private int totalPages;
    }

    public static <T> PageResult<T> of(List<T> list, int page, int pageSize, long total) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        Pagination pagination = Pagination.builder()
                .page(page)
                .pageSize(pageSize)
                .total(total)
                .totalPages(totalPages)
                .build();
        return PageResult.<T>builder()
                .list(list)
                .pagination(pagination)
                .build();
    }
}
