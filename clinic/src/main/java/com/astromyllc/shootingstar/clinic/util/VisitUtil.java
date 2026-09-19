package com.astromyllc.shootingstar.clinic.util;

import com.astromyllc.shootingstar.clinic.dto.request.VisitRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VisitResponse;
import com.astromyllc.shootingstar.clinic.model.Visit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VisitUtil {

    public Visit mapVisitRequest_ToVisit(VisitRequest visitRequest) {
        return Visit.builder()
                .institutionCode(visitRequest.getInstitutionCode())
                .patientId(visitRequest.getPatientId())
                .patientType(visitRequest.getPatientType())
                .patientName(visitRequest.getPatientName())
                .reason(visitRequest.getReason())
                .notes(visitRequest.getNotes())
                .recordedBy(visitRequest.getRecordedBy())
                .checkInTime(LocalDateTime.now())
                .status("IN_PROGRESS")
                .parentNotified(false)
                .build();
    }

    public VisitResponse mapVisit_ToVisitResponse(Visit visit) {
        return VisitResponse.builder()
                .id(visit.getId())
                .institutionCode(visit.getInstitutionCode())
                .patientId(visit.getPatientId())
                .patientType(visit.getPatientType())
                .patientName(visit.getPatientName())
                .reason(visit.getReason())
                .notes(visit.getNotes())
                .recordedBy(visit.getRecordedBy())
                .checkInTime(visit.getCheckInTime())
                .checkOutTime(visit.getCheckOutTime())
                .status(visit.getStatus())
                .diagnosisId(visit.getDiagnosisId())
                .prescriptionId(visit.getPrescriptionId())
                .vitalRecordsId(visit.getVitalRecordsId())
                .parentNotified(visit.getParentNotified())
                .parentNotifiedAt(visit.getParentNotifiedAt())
                .build();
    }

    // "Jane Doe visited the clinic (Headache). Recorded by Nurse Ama." —
    // kept short since this is what shows up as the notification body on a
    // parent's phone, not a full clinical note.
    public String buildParentMessage(Visit visit) {
        StringBuilder sb = new StringBuilder();
        sb.append(visit.getPatientName() != null ? visit.getPatientName() : "Your child")
                .append(" visited the school clinic");
        if (visit.getReason() != null && !visit.getReason().isBlank()) {
            sb.append(" (").append(visit.getReason()).append(")");
        }
        sb.append(".");
        if (visit.getRecordedBy() != null && !visit.getRecordedBy().isBlank()) {
            sb.append(" Recorded by ").append(visit.getRecordedBy()).append(".");
        }
        return sb.toString();
    }
}
