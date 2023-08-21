package com.astromyllc.shootingstar.finance.dto.request;

import jakarta.annotation.Nonnull;
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
public class BillingsRequest {
    private Long billingId;
    private String billingDate;
    @Nonnull
    private String term;
    @Nonnull
    private String studentClass;
    @Nonnull
    private String studentId;
    private String BillDisc;
    @Nonnull
    private Double billamnt;
    @Nonnull
    private String billname;
    private Double billamntbal;
    @Nonnull
    private String institutionCode;
}
