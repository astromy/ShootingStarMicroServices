package com.astromyllc.shootingstar.clinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A single clinic visit — the umbrella event this whole module exists for.
 * Recording one, at minimum, is what triggers the parent notification
 * requirement ("every student who visits, the parent is kept in the loop").
 * <p>
 * Diagnosis / VitalRecords / Prescription created during the same visit are
 * linked by plain Long id references rather than a JPA relationship — that
 * matches this codebase's existing style (nothing else here uses
 *
 * @ManyToOne/@OneToMany; Diagnosis/Prescription/VitalRecords already
 * correlate only loosely, via shared patientId + institutionCode + dateTime).
 */
@Entity
@Table(name = "visit")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String institutionCode;
    private String patientId;
    private String patientType;      // STUDENT | STAFF — mirrors Diagnosis/Prescription/VitalRecords' existing patientType field
    private String patientName;      // denormalized at check-in time for display without a cross-service call on every read

    private String reason;
    private String notes;
    private String recordedBy;       // nurse/staff who checked the patient in

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;   // null while still IN_PROGRESS

    private String status;           // IN_PROGRESS | DISCHARGED

    private Long diagnosisId;        // nullable
    private Long prescriptionId;      // nullable
    private Long vitalRecordsId;      // nullable

    private Boolean parentNotified;
    private LocalDateTime parentNotifiedAt;
}