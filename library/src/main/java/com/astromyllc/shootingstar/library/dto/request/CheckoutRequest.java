package com.astromyllc.shootingstar.library.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class CheckoutRequest {
    private String institutionCode;
    private String bookCode;
    private String studentIndex;
    private String studentName;     // optional — denormalized onto the loan if provided
    private String processedBy;     // staff/device identifier processing the checkout

    /** Optional — defaults to checkoutDate + 14 days if not supplied (see LibraryLoanServiceImpl). */
    private Integer loanPeriodDays;
}
