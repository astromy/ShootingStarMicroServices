package com.astromyllc.shootingstar.clinic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A real clinic visit is one event, not four separate forms — this request
 * carries the vitals/diagnosis/prescription captured DURING the visit
 * together, so the web/mobile UI can be a single "record a visit" screen
 * rather than making a nurse fill out Diagnosis, VitalRecords, and
 * Prescription as unrelated CRUD forms.
 * <p>
 * Each of vitals/diagnosisText/prescriptionText is optional — a visit might
 * just be "checked temperature, sent back to class" with no prescription.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VisitRequest {
    private String institutionCode;
    private String patientId;
    private String patientType;     // STUDENT | STAFF
    private String patientName;
    private String reason;
    private String notes;
    private String recordedBy;

    // Optional — captured at check-in if available
    private String vitalsRecordType;   // e.g. "Temperature", "Blood Pressure" — kept generic since VitalRecords itself is generic
    private String vitalsValue;

    private String diagnosisText;
    private String prescriptionText;
}