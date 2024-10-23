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
public class Bill_PaymentRequest {
    private Long billPaymentId;
    @Nonnull
    private Double paymentAmount;
    private String paymentDate;
    @Nonnull
    private String studentId;
    @Nonnull
    private String paidBy;
    private String recieptNum;
    @Nonnull
    private String institutionCode;
}
