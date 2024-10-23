package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data


public class BillingsResponse {
    private Long billingId;
    private LocalDateTime billingDate;
    private String term;
    private String studentClass;
    private String studentId;
    private String BillDisc;
    private Double billamnt;
    private String billname;
    private Double billamntbal;
    private String institutionCode;
}
