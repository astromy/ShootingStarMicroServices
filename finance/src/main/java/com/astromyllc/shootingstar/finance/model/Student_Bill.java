package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "studentbill",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_student_institution",
                columnNames = {"studentId", "institutionCode"}
        )
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
@EqualsAndHashCode(of = "studentBillId")
public class Student_Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentBillId;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String institutionCode;

    private String studentClass;
    private String term;

    private String academicYear;

    private Double amountDue;

    private Double amountPaid;

    private Double amountBalance;

    private Double oldBalance;
}
