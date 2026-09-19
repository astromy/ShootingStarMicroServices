package com.astromyllc.shootingstar.clinic.controller;

import com.astromyllc.shootingstar.clinic.dto.request.DischargeVisitRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PatientVisitFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.UpdateVisitClinicalRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VisitFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VisitRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VisitResponse;
import com.astromyllc.shootingstar.clinic.serviceInterface.VisitServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VisitController {

    private final VisitServiceInterface visitServiceInterface;

    // Called the moment a patient's ID card is scanned / their ID is looked
    // up at the clinic desk. Checks the patient in and — if any of
    // vitals/diagnosisText/prescriptionText were captured on the same
    // screen — records those too, then fires the parent notification.
    @PostMapping("/api/clinic/recordVisit")
    public ResponseEntity<VisitResponse> recordVisit(@RequestBody VisitRequest request) {
        log.info("Recording clinic visit for patient {} at institution {}",
                request.getPatientId(), request.getInstitutionCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(visitServiceInterface.recordVisit(request));
    }

    // For clinical notes captured separately from check-in — e.g. a
    // "Diagnosis Recording" desk adding a diagnosis/prescription to a
    // patient who's already IN_PROGRESS.
    @PostMapping("/api/clinic/updateVisitClinicalNotes/{visitId}")
    public ResponseEntity<?> updateVisitClinicalNotes(@PathVariable Long visitId, @RequestBody UpdateVisitClinicalRequest request) {
        try {
            return ResponseEntity.ok(visitServiceInterface.updateVisitClinicalNotes(visitId, request));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/api/clinic/dischargeVisit/{visitId}")
    public ResponseEntity<?> dischargeVisit(@PathVariable Long visitId, @RequestBody DischargeVisitRequest request) {
        try {
            return ResponseEntity.ok(visitServiceInterface.dischargeVisit(visitId, request));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/api/clinic/getVisitsByInstitution")
    public ResponseEntity<List<VisitResponse>> getVisitsByInstitution(@RequestBody VisitFetchRequest request) {
        return ResponseEntity.ok(visitServiceInterface.fetchVisitsByInstitution(request));
    }

    @PostMapping("/api/clinic/getVisitsByPatient")
    public ResponseEntity<List<VisitResponse>> getVisitsByPatient(@RequestBody PatientVisitFetchRequest request) {
        return ResponseEntity.ok(visitServiceInterface.fetchVisitsByPatient(request));
    }

    // Manual trigger for ops/support use (e.g. after the gateway or
    // administration-pta was briefly down) — sweeps every visit whose
    // parent was never successfully notified and retries.
    @PostMapping("/api/clinic/retryFailedParentNotifications")
    public ResponseEntity<String> retryFailedParentNotifications() {
        int retried = visitServiceInterface.retryFailedParentNotifications();
        return ResponseEntity.ok("Notified parents for " + retried + " previously-failed visit(s)");
    }
}
