package com.astromyllc.shootingstar.library.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class LoanResponse {
    private String id;
    private String institutionCode;
    private String bookCode;
    private String bookTitle;
    private String studentIndex;
    private String studentName;
    private String processedBy;
    private String status;
    private LocalDateTime checkoutDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnedDate;
    private String notes;

    /** true if status == ACTIVE and dueDate is before now — computed, not stored. */
    private Boolean overdue;
}
