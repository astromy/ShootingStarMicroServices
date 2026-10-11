package com.astromyllc.shootingstar.hr.dto.response;

/** One bar / slice of a chart: a label and how many. */
public record LabelCount(String label, long count) {
}
