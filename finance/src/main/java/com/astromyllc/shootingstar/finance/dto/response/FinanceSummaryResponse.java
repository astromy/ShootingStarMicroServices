package com.astromyllc.shootingstar.finance.dto.response;

import java.util.List;

/**
 * What the finance and executive dashboards show about fees, counted on the server from the
 * in-memory bills, billings and payments - instead of sending every payment to the browser.
 *
 * @param billCount        bill items set up for the school (tuition, PTA, ...)
 * @param paymentCount     payments received
 * @param totalCollected   sum of all payments received
 * @param totalBilled      sum of what students were billed
 * @param totalOutstanding sum of what students still owe
 * @param studentsBilled   students who have been billed
 * @param studentsOwing    students who still owe something
 * @param billingsOwing    student bills with a balance left
 * @param billingsPaid     student bills that are fully paid
 * @param collectedByClass money collected, by the class the student is billed in
 * @param paymentMethods   number of payments by method (cash, mobile money, ...)
 */
public record FinanceSummaryResponse(
        long billCount,
        long paymentCount,
        double totalCollected,
        double totalBilled,
        double totalOutstanding,
        long studentsBilled,
        long studentsOwing,
        long billingsOwing,
        long billingsPaid,
        List<LabelAmount> collectedByClass,
        List<LabelCount> paymentMethods) {
}
