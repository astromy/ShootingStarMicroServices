package com.astromyllc.shootingstar.finance.dto.response;

/** One bar / slice of a chart: a label and how many. */
public record LabelCount(String label, long count) {
}
