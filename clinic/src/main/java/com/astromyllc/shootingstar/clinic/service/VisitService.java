package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.config.ParentNotificationClient;
import com.astromyllc.shootingstar.clinic.dto.request.DischargeVisitRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PatientVisitFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.UpdateVisitClinicalRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VisitFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VisitRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VisitResponse;
import com.astromyllc.shootingstar.clinic.event.ParentNotificationPayload;
import com.astromyllc.shootingstar.clinic.model.Diagnosis;
import com.astromyllc.shootingstar.clinic.model.Prescription;
import com.astromyllc.shootingstar.clinic.model.Visit;
import com.astromyllc.shootingstar.clinic.model.VitalRecords;
import com.astromyllc.shootingstar.clinic.repository.DiagnosisRepository;
import com.astromyllc.shootingstar.clinic.repository.PrescriptionRepository;
import com.astromyllc.shootingstar.clinic.repository.VisitRepository;
import com.astromyllc.shootingstar.clinic.repository.VitalRecordsRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.VisitServiceInterface;
import com.astromyllc.shootingstar.clinic.util.DiagnosisUtil;
import com.astromyllc.shootingstar.clinic.util.PrescriptionUtil;
import com.astromyllc.shootingstar.clinic.util.VisitUtil;
import com.astromyllc.shootingstar.clinic.util.VitalRecordsUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ties a clinic visit together end-to-end: check-in, the vitals/diagnosis/
 * prescription captured at the same time (each optional), discharge, and —
 * the actual point of this module — telling the patient's parent their
 * child was seen.
 * <p>
 * Deliberately talks to DiagnosisRepository/PrescriptionRepository/
 * VitalRecordsRepository directly rather than through DiagnosisService /
 * PrescriptionService / VitalRecordsService: those services' record*
 * methods return void, so there'd be no way to get the generated id back
 * to link onto the Visit. Changing those signatures would ripple into
 * existing callers for no reason — going around them here is the smaller,
 * safer change.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VisitService implements VisitServiceInterface {

    private final VisitRepository visitRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final VitalRecordsRepository vitalRecordsRepository;
    private final VisitUtil visitUtil;
    private final ParentNotificationClient parentNotificationClient;

    @Override
    public VisitResponse recordVisit(VisitRequest visitRequest) {
        Visit visit = visitUtil.mapVisitRequest_ToVisit(visitRequest);

        applyClinicalNotes(visit,
                visitRequest.getVitalsRecordType(), visitRequest.getVitalsValue(),
                visitRequest.getDiagnosisText(), visitRequest.getPrescriptionText());

        visit = visitRepository.save(visit);
        log.info("Visit recorded: patient {} at institution {} (visit {})",
                visit.getPatientId(), visit.getInstitutionCode(), visit.getId());

        notifyParent(visit);

        return visitUtil.mapVisit_ToVisitResponse(visit);
    }

    @Override
    public VisitResponse updateVisitClinicalNotes(Long visitId, UpdateVisitClinicalRequest request) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("No visit found with id " + visitId));

        applyClinicalNotes(visit,
                request.getVitalsRecordType(), request.getVitalsValue(),
                request.getDiagnosisText(), request.getPrescriptionText());

        if (request.getRecordedBy() != null && !request.getRecordedBy().isBlank()) {
            visit.setRecordedBy(request.getRecordedBy());
        }

        visit = visitRepository.save(visit);
        log.info("Visit {} updated with clinical notes for patient {} at institution {}",
                visit.getId(), visit.getPatientId(), visit.getInstitutionCode());

        return visitUtil.mapVisit_ToVisitResponse(visit);
    }

    // Shared by recordVisit (at check-in) and updateVisitClinicalNotes
    // (added later, e.g. from a separate "Diagnosis Recording" desk).
    // Each of vitals/diagnosis/prescription is independently optional and
    // only ever ADDS a new record — it never overwrites an id the visit
    // already has, since Visit only keeps one pointer per record type
    // (see Visit's own doc comment on why these are plain Long refs).
    private void applyClinicalNotes(Visit visit, String vitalsRecordType, String vitalsValue,
                                     String diagnosisText, String prescriptionText) {
        LocalDateTime now = LocalDateTime.now();

        if (vitalsRecordType != null && !vitalsRecordType.isBlank() && visit.getVitalRecordsId() == null) {
            VitalRecords vitals = VitalRecords.builder()
                    .dateTime(now)
                    .recordType(vitalsRecordType)
                    .value(vitalsValue)
                    .patientId(visit.getPatientId())
                    .patientType(visit.getPatientType())
                    .institutionCode(visit.getInstitutionCode())
                    .build();
            vitals = vitalRecordsRepository.save(vitals);
            VitalRecordsUtil.vitalRecordsList.add(vitals);
            visit.setVitalRecordsId(vitals.getId());
        }

        if (diagnosisText != null && !diagnosisText.isBlank() && visit.getDiagnosisId() == null) {
            Diagnosis diagnosis = Diagnosis.builder()
                    .dateTime(now)
                    .diagnosis(diagnosisText)
                    .patientId(visit.getPatientId())
                    .patientType(visit.getPatientType())
                    .institutionCode(visit.getInstitutionCode())
                    .build();
            diagnosis = diagnosisRepository.save(diagnosis);
            DiagnosisUtil.diagnosisGlobalList.add(diagnosis);
            visit.setDiagnosisId(diagnosis.getId());
        }

        if (prescriptionText != null && !prescriptionText.isBlank() && visit.getPrescriptionId() == null) {
            Prescription prescription = Prescription.builder()
                    .dateTime(now)
                    .prescription(prescriptionText)
                    .patientId(visit.getPatientId())
                    .patientType(visit.getPatientType())
                    .institutionCode(visit.getInstitutionCode())
                    .build();
            prescription = prescriptionRepository.save(prescription);
            PrescriptionUtil.prescriptionGlobalList.add(prescription);
            visit.setPrescriptionId(prescription.getId());
        }
    }

    @Override
    public VisitResponse dischargeVisit(Long visitId, DischargeVisitRequest dischargeVisitRequest) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("No visit found with id " + visitId));

        visit.setCheckOutTime(LocalDateTime.now());
        visit.setStatus("DISCHARGED");
        if (dischargeVisitRequest.getNotes() != null && !dischargeVisitRequest.getNotes().isBlank()) {
            String combined = (visit.getNotes() == null || visit.getNotes().isBlank())
                    ? dischargeVisitRequest.getNotes()
                    : visit.getNotes() + " | Discharge: " + dischargeVisitRequest.getNotes();
            visit.setNotes(combined);
        }
        if (dischargeVisitRequest.getRecordedBy() != null && !dischargeVisitRequest.getRecordedBy().isBlank()) {
            visit.setRecordedBy(dischargeVisitRequest.getRecordedBy());
        }

        visit = visitRepository.save(visit);
        log.info("Visit {} discharged for patient {} at institution {}",
                visit.getId(), visit.getPatientId(), visit.getInstitutionCode());

        return visitUtil.mapVisit_ToVisitResponse(visit);
    }

    @Override
    public List<VisitResponse> fetchVisitsByInstitution(VisitFetchRequest visitFetchRequest) {
        List<Visit> visits = (visitFetchRequest.getStatus() == null || visitFetchRequest.getStatus().isBlank())
                ? visitRepository.findByInstitutionCodeOrderByCheckInTimeDesc(visitFetchRequest.getInstitutionCode())
                : visitRepository.findByInstitutionCodeAndStatusOrderByCheckInTimeDesc(
                        visitFetchRequest.getInstitutionCode(), visitFetchRequest.getStatus());

        return visits.stream().map(visitUtil::mapVisit_ToVisitResponse).toList();
    }

    @Override
    public List<VisitResponse> fetchVisitsByPatient(PatientVisitFetchRequest patientVisitFetchRequest) {
        return visitRepository.findByInstitutionCodeAndPatientIdOrderByCheckInTimeDesc(
                        patientVisitFetchRequest.getInstitutionCode(), patientVisitFetchRequest.getPatientId())
                .stream().map(visitUtil::mapVisit_ToVisitResponse).toList();
    }

    @Override
    public int retryFailedParentNotifications() {
        List<Visit> pending = visitRepository.findByParentNotifiedFalse();
        int successCount = 0;
        for (Visit visit : pending) {
            if (notifyParent(visit)) {
                successCount++;
            }
        }
        log.info("Retried {} pending parent notifications, {} succeeded", pending.size(), successCount);
        return successCount;
    }

    // Best-effort — a failed notification never blocks or rolls back the
    // visit record itself. parentNotified stays false so retryFailedParentNotifications()
    // (and findByParentNotifiedFalse) can pick it back up later.
    private boolean notifyParent(Visit visit) {
        ParentNotificationPayload payload = ParentNotificationPayload.builder()
                .institutionCode(visit.getInstitutionCode())
                .studentId(visit.getPatientId())
                .category("HEALTH_VISIT")
                .sourceModule("clinic")
                .title("Clinic Visit")
                .message(visitUtil.buildParentMessage(visit))
                .build();

        boolean sent = "STUDENT".equalsIgnoreCase(visit.getPatientType())
                && parentNotificationClient.notifyParentsOfStudentEvent(payload);

        if (sent) {
            visit.setParentNotified(true);
            visit.setParentNotifiedAt(LocalDateTime.now());
            visitRepository.save(visit);
        }
        return sent;
    }
}
