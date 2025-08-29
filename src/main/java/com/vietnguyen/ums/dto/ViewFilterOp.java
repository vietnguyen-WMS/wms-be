package com.vietnguyen.ums.dto;

/**
 * Allowed filter operations for {@link ViewFilter}.
 */
public enum ViewFilterOp {
    EQ("="),
    NE("<>"),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    LIKE("LIKE");

    private final String sql;

    ViewFilterOp(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
