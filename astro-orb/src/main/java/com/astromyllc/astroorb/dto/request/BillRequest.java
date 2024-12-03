package com.astromyllc.astroorb.dto.request;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
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
