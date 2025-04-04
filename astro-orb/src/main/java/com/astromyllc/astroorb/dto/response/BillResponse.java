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

public class BillResponse {
    private Long billId;
    private String bill_Name;
    private Double bill_Amount;
    private LocalDateTime creation_Date;
    private String bill_Description;
    private String bill_Cat;
    private String institutionCode;
}
