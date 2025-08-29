package com.vietnguyen.ums.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ViewQueryRequest(
        String tbl,
        String schema,
        @JsonProperty("default_sorts") List<ViewSort> defaultSorts,
        int page,
        @JsonProperty("page_size") int pageSize,
        List<ViewFilter> filters
) {}
