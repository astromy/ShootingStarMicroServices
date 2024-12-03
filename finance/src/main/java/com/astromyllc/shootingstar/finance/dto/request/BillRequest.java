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
public class BillRequest {
    private Long billId;
    @Nonnull
    private String bill_Name;
    @Nonnull
    private Double bill_Amount;
    private String creation_Date;
    private String bill_Description;
    @Nonnull
    private String bill_Cat;
    @Nonnull
    private String institutionCode;
}
