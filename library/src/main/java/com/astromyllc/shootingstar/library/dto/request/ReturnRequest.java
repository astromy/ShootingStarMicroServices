package com.astromyllc.shootingstar.library.dto.request;

import lombok.*;

/**
 * Returns the most recent ACTIVE loan matching institutionCode + bookCode + studentIndex.
 * If the caller already knows the specific loanId (e.g. from a loan-history screen),
 * pass it instead and it takes precedence over the bookCode/studentIndex lookup.
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class ReturnRequest {
    private String institutionCode;
    private String bookCode;
    private String studentIndex;
    private String processedBy;
    private String loanId;          // optional — precise return by loan ID
}
