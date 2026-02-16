package com.healnexus.dto.response;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationResponse<T> {

    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean first;
    private boolean last;

}

