package com.vietnguyen.ums.service;

import com.vietnguyen.ums.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ViewService {
    private final JdbcTemplate jdbcTemplate;
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z0-9_]+");

    public ViewService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PagedResponse<Map<String, Object>> query(ViewQueryRequest req) {
        String table = safeIdentifier(req.tbl());
        String schema = safeIdentifier(req.schema());
        int page = Math.max(1, req.page());
        int size = Math.max(1, req.pageSize());

        List<Object> whereParams = new ArrayList<>();
        String whereClause = buildWhere(req.filters(), whereParams);

        String selectSql = "SELECT * FROM " + schema + "." + table;
        String countSql = "SELECT COUNT(*) FROM " + schema + "." + table;

        if (!whereClause.isEmpty()) {
            selectSql += " WHERE " + whereClause;
            countSql += " WHERE " + whereClause;
        }

        if (req.defaultSorts() != null && !req.defaultSorts().isEmpty()) {
            String order = req.defaultSorts().stream()
                    .map(s -> safeIdentifier(s.field()) + (s.asc() ? " ASC" : " DESC"))
                    .collect(Collectors.joining(", "));
            selectSql += " ORDER BY " + order;
        }

        selectSql += " LIMIT ? OFFSET ?";

        List<Object> selectParams = new ArrayList<>(whereParams);
        selectParams.add(size);
        selectParams.add((page - 1) * size);

        List<Map<String, Object>> items = jdbcTemplate.queryForList(selectSql, selectParams.toArray());
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, whereParams.toArray());
        return new PagedResponse<>(items, page, size, total);
    }

    private String buildWhere(List<ViewFilter> filters, List<Object> params) {
        if (filters == null || filters.isEmpty()) return "";
        return filters.stream().map(f -> {
            String field = safeIdentifier(f.field());
            String op = f.op() != null ? f.op().sql() : "=";
            params.add(f.value());
            return field + " " + op + " ?";
        }).collect(Collectors.joining(" AND "));
    }

    private String safeIdentifier(String name) {
        if (name == null || !IDENTIFIER.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid identifier: " + name);
        }
        return name;
    }
}
