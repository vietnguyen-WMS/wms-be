package com.vietnguyen.ums.dto;

/**
 * Filter definition for querying a view.
 * <p>
 * The {@code field} is validated to contain only alphanumeric characters and underscores
 * before being interpolated into SQL statements. {@link ViewFilterOp} restricts the
 * operations that can be performed in order to avoid SQL injection through arbitrary
 * operator strings.
 */
public record ViewFilter(String field, ViewFilterOp op, Object value) {}
