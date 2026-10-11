package com.astromyllc.shootingstar.finance.dto.response;

/** One bar / slice of a chart: a label and an amount of money. */
public record LabelAmount(String label, double amount) {
}
