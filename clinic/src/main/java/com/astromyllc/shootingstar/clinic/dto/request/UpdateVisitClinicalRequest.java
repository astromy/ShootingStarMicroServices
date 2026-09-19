package com.astromyllc.shootingstar.clinic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * For adding diagnosis/prescription/vitals to a visit AFTER check-in — the
 * "Diagnosis Recording" desk isn't always the same person/moment as the
 * initial check-in captured by VisitRequest. Each field is optional; only
 * the ones supplied get created and linked onto the visit.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateVisitClinicalRequest {
    private String recordedBy;

    private String diagnosisText;
    private String prescriptionText;

    private String vitalsRecordType;
    private String vitalsValue;
}
