package com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

// Modeled as a flat, high-volume table (not embedded inside Block, unlike
// BlockMaster/BlockPrefect) since a recurring roster generates many rows -
// one per student per duty per week - rather than the handful of records
// a block's master/prefect list holds.
@Entity
@Table(name = "duty_assignment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DutyAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDutyAssignment;

    @NonNull
    private String institutionCode;

    @NonNull
    private Long idBlock;

    @NonNull
    private Long idDutyType;

    // Denormalized for display without a join - matches the block/room
    // student-facing responses elsewhere in this service, which carry
    // studentID directly rather than a full relation.
    @NonNull
    private String studentId;
    private String studentName;

    @NonNull
    private LocalDate weekStartDate;

    // PENDING, COMPLETED, MISSED
    @Builder.Default
    private String status = "PENDING";

    // Links every row generated from one "assign this rotation" action, so
    // the whole rotation can be found/cancelled together later without
    // affecting weeks that belong to a different rotation for the same
    // student.
    private String rotationId;
}
