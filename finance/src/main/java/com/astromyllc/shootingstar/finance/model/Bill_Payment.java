package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "billpayment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "billPaymentId")
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
