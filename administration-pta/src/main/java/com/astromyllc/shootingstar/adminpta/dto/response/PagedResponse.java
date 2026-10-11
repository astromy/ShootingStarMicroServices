package com.astromyllc.shootingstar.adminpta.dto.response;

import java.util.List;

/**
 * One page of a list.
 *
 * @param total    how many records match the class / status / gender filters
 * @param filtered how many of those also match the search box (what the pager counts)
 * @param page     the page returned, starting at 0
 * @param size     the page size used (after limits are applied)
 * @param rows     the rows of this page
 */
public record PagedResponse<T>(long total, long filtered, int page, int size, List<T> rows) {
}
