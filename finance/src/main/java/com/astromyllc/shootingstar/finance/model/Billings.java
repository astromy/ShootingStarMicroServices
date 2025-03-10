package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "billings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "billingId")
public class Billings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingId;
    private LocalDateTime billingDate;
    private String term;
    private String studentClass;
    private String studentId;
    private String BillDisc;
    private Double billamnt;
    private String billname;
    private Double billamntbal;
    private String institutionCode;
}
