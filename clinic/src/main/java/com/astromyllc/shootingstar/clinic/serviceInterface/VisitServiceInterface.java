package com.astromyllc.shootingstar.clinic.serviceInterface;

import com.astromyllc.shootingstar.clinic.dto.request.DischargeVisitRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PatientVisitFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.UpdateVisitClinicalRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VisitFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VisitRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VisitResponse;

import java.util.List;

public interface VisitServiceInterface {

    // Checks a patient in, optionally captures vitals/diagnosis/prescription
    // taken at check-in time, and — this is the point of the whole module —
    // notifies the patient's parent that their child is at the clinic.
    VisitResponse recordVisit(VisitRequest visitRequest);

    // Adds diagnosis/prescription/vitals to a visit that's already
    // IN_PROGRESS — for when clinical notes are captured separately from
    // the initial check-in (e.g. a "Diagnosis Recording" desk).
    VisitResponse updateVisitClinicalNotes(Long visitId, UpdateVisitClinicalRequest updateVisitClinicalRequest);

    VisitResponse dischargeVisit(Long visitId, DischargeVisitRequest dischargeVisitRequest);

    List<VisitResponse> fetchVisitsByInstitution(VisitFetchRequest visitFetchRequest);

    List<VisitResponse> fetchVisitsByPatient(PatientVisitFetchRequest patientVisitFetchRequest);

    // For any visit whose parent-notification call failed at check-in time
    // (gateway hiccup, administration-pta briefly down, etc.) — re-attempts
    // delivery rather than silently leaving a parent never informed.
    int retryFailedParentNotifications();
}
