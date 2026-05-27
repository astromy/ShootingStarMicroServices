package com.astromyllc.shootingstar.finance.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class BillResponse {
    private Long billId;
    private String bill_Name;
    private Double bill_Amount;
    private LocalDateTime creation_Date;
    private String bill_Description;
    private String bill_Cat;
    private String institutionCode;
}
