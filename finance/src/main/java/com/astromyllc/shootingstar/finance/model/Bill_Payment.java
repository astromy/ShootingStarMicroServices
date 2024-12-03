package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "billpayment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Bill_Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billPaymentId;
    private Double paymentAmount;
    private LocalDateTime paymentDate;
    private String studentId;
    private String paidBy;
    private String recieptNum;
    private String institutionCode;
}
