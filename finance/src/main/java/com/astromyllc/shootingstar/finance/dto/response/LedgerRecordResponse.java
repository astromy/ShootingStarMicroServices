package com.astromyllc.shootingstar.finance.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LedgerRecordResponse {
    private Long ledgerRecordId;
    private String institutionCode;
    private Long ledgerBookId;
    private String ledgerBookName;
    private String entryType;
    private Double amount;
    private Double runningBalance;
    private String source;
    private String referenceId;
    private String externalReference;
    private String description;
    private String postedBy;
    private LocalDateTime transactionDate;
    private LocalDateTime postedAt;
}
