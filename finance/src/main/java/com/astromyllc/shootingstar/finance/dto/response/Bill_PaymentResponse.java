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
public class Bill_PaymentResponse {
    private Long billPaymentId;
    private Double paymentAmount;
    private LocalDateTime paymentDate;
    private String studentId;
    private String paidBy;
    private String recieptNum;
    private String institutionCode;
}
