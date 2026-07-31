package com.astromyllc.shootingstar.library.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LoanFetchRequest {
    private String institutionCode;

    /** Optional filter — ACTIVE | RETURNED | LOST. Null/blank = all statuses. */
    private String status;
}
