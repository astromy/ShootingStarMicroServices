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
