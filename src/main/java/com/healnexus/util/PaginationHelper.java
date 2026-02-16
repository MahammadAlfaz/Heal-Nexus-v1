package com.healnexus.util;

import com.healnexus.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginationHelper {

    public static <E, D> PaginationResponse<D> buildPaginationResponse(Page<E> page, List<D> data) {
        return PaginationResponse.<D>builder()
                .data(data)
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public static Pageable paginationNormalize(Pageable pageable) {
        int maxSize = 50;
        if (pageable.getPageSize() > maxSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("appointmentTime").ascending());
        }

        return pageable;
    }
}