package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffPermissionsRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffSubjectsRequest;
import com.astromyllc.shootingstar.hr.dto.request.api.StaffCodeRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffContactResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffPermissionsResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;
import com.astromyllc.shootingstar.hr.model.*;
import com.astromyllc.shootingstar.hr.repository.StaffRepository;
import com.astromyllc.shootingstar.hr.serviceInterface.StaffServiceInterface;
import com.astromyllc.shootingstar.hr.utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffService implements StaffServiceInterface {
    private final StaffRepository staffRepository;
    private final StaffUtil staffUtil;
    private final ProfessionalRecordsUtil professionalRecordsUtil;
    private final StaffDocumentsUtil staffDocumentsUtil;
    private final DependantsUtil dependantsUtil;
    private final StaffPermissionsUtil staffPermissionsUtil;
    private final AcademicRecordsUtil academicRecordsUtil;
    private final StaffDesignationUtil staffDesignationUtil;
    private final StaffSubjectsUtil staffSubjectsUtil;

    @Override
    public Optional<StaffResponse> createStaff(List<StaffRequest> staffRequests) throws IOException, URISyntaxException {
        for (StaffRequest staffRequest : staffRequests) {
            Optional<Staff> staff = StaffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffRequest.getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffRequest.getStaffCode())).findFirst();
            if (staff.isEmpty()) {
                Staff newStaff = staffUtil.mapStaffRequest_ToStaff(staffRequest);
                //staffRepository.save(newStaff);

                if (staffRequest.getProfessionalRecords() != null && !staffRequest.getProfessionalRecords().isEmpty()) {
                    List<ProfessionalRecords> pr = staffRequest.getProfessionalRecords().stream().map(p -> ProfessionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(p, newStaff.getStaffCode())).toList();
                    professionalRecordsUtil.saveAll(pr);
                    newStaff.setProfessionalRecords(pr);
                }

                if (staffRequest.getStaffDocuments() != null && !staffRequest.getStaffDocuments().isEmpty()) {
                    List<StaffDocuments> sds = staffRequest.getStaffDocuments().stream().map(sd -> StaffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(sd, newStaff.getStaffCode())).toList();
                    staffDocumentsUtil.saveAll(sds);
                    newStaff.setStaffDocuments(sds);
                }

                if (staffRequest.getDependants() != null && !staffRequest.getDependants().isEmpty()) {
                    List<Dependants> dp = staffRequest.getDependants().stream().map(d -> DependantsUtil.mapDependantsRequest_ToDependants(d, newStaff.getStaffCode())).toList();
                    dependantsUtil.saveAll(dp);
                    newStaff.setDependants(dp);
                }

                if (staffRequest.getAcademicRecords() != null && !staffRequest.getAcademicRecords().isEmpty()) {
                    List<AcademicRecords> ac = staffRequest.getAcademicRecords().stream().map(a -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(a, newStaff.getStaffCode())).toList();
                    academicRecordsUtil.saveAll(ac);
                    newStaff.setAcademicRecords(ac);
                }

                if (staffRequest.getStaffDesignations() != null && !staffRequest.getStaffDesignations().isEmpty()) {
                    List<DesignationList> dl = staffRequest.getStaffDesignations().stream().map(StaffDesignationUtil::mapStaffDesignationListRequest_ToStaffDesignationList).toList();
                    staffDesignationUtil.saveAll(dl);
                    newStaff.setStaffDesignations(dl);
                }

                if (staffRequest.getStaffSubjects() != null && !staffRequest.getStaffSubjects().isEmpty()) {
                    List<StaffSubjects> ss = staffRequest.getStaffSubjects().stream().map(StaffSubjectsUtil::mapStaffSubjectRequest_ToStaffSubjects).toList();
                    staffSubjectsUtil.saveAll(ss);
                    newStaff.setStaffSubjects(ss);
                }

                staffRepository.save(newStaff);

                StaffUtil.staffGlobalList.add(newStaff);
                return Optional.of(staffUtil.mapStaff_ToStaffResponse(newStaff));
            } else {

                Staff s = staffUtil.mapStaffRequest_ToStaff(staffRequest, staff.get());

                // ── Professional Records ──────────────────────────────────────────────
                if (staffRequest.getProfessionalRecords() != null && !staffRequest.getProfessionalRecords().isEmpty()) {
                    List<ProfessionalRecords> updatedProfessionalRecords = staffRequest.getProfessionalRecords().stream()
                            .map(requestRecord -> {
                                Optional<ProfessionalRecords> existingRecord = staff.get().getProfessionalRecords() == null ? Optional.empty() :
                                        staff.get().getProfessionalRecords().stream()
                                        .filter(existing -> existing.getNameOfInstitution().equalsIgnoreCase(requestRecord.getNameOfInstitution()) &&
                                                existing.getDateOfEmployment().equals(requestRecord.getDateOfEmployment()))
                                        .findFirst();
                                return existingRecord.map(existing -> {
                                    professionalRecordsUtil.updateProfessionalRecord(existing, requestRecord, s.getStaffCode());
                                    return existing;
                                }).orElseGet(() -> ProfessionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(requestRecord, staff.get().getStaffCode()));
                            }).toList();
                    professionalRecordsUtil.saveAll(updatedProfessionalRecords);
                    s.setProfessionalRecords(updatedProfessionalRecords);
                }

                // ── Dependants ────────────────────────────────────────────────────────
                if (staffRequest.getDependants() != null && !staffRequest.getDependants().isEmpty()) {
                    List<Dependants> updatedDependants = staffRequest.getDependants().stream()
                            .map(requestDependant -> {
                                Optional<Dependants> existingDependant = staff.get().getDependants() == null ? Optional.empty() :
                                        staff.get().getDependants().stream()
                                        .filter(existing -> existing.getName().equalsIgnoreCase(requestDependant.getName()) &&
                                                existing.getDateOfBirth().equals(requestDependant.getDateOfBirth()))
                                        .findFirst();
                                return existingDependant.map(existing -> {
                                    DependantsUtil.updateDependants(existing, requestDependant, s.getStaffCode());
                                    return existing;
                                }).orElseGet(() -> DependantsUtil.mapDependantsRequest_ToDependants(requestDependant, staff.get().getStaffCode()));
                            }).toList();
                    dependantsUtil.saveAll(updatedDependants);
                    s.setDependants(updatedDependants);
                }

                // ── Academic Records ──────────────────────────────────────────────────
                if (staffRequest.getAcademicRecords() != null && !staffRequest.getAcademicRecords().isEmpty()) {
                    List<AcademicRecords> updatedAcademicRecords = staffRequest.getAcademicRecords().stream()
                            .map(requestRecord -> {
                                Optional<AcademicRecords> existingRecord = staff.get().getAcademicRecords() == null ? Optional.empty() :
                                        staff.get().getAcademicRecords().stream()
                                        .filter(existing -> existing.getNameOfInstitution().equalsIgnoreCase(requestRecord.getNameOfInstitution()) &&
                                                existing.getDateOfGraduation().equals(requestRecord.getDateOfGraduation()))
                                        .findFirst();
                                return existingRecord.map(existing -> {
                                    academicRecordsUtil.updateAcademicRecord(existing, requestRecord, s.getStaffCode());
                                    return existing;
                                }).orElseGet(() -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(requestRecord, staff.get().getStaffCode()));
                            }).toList();
                    academicRecordsUtil.saveAll(updatedAcademicRecords);
                    s.setAcademicRecords(updatedAcademicRecords);
                }

                // ── Staff Designations ────────────────────────────────────────────────
                if (staffRequest.getStaffDesignations() != null && !staffRequest.getStaffDesignations().isEmpty()) {
                    List<DesignationList> staffDesignationList = staffRequest.getStaffDesignations().stream()
                            .map(requestRecord -> {
                                Optional<DesignationList> existingRecord = staff.get().getStaffDesignations() == null ? Optional.empty() :
                                        staff.get().getStaffDesignations().stream()
                                        .filter(existing -> existing.getInstitutionCode().equalsIgnoreCase(requestRecord.getInstitutionCode()) &&
                                                existing.getDesignation().equals(requestRecord.getDesignation()))
                                        .findFirst();
                                return existingRecord.map(existing -> {
                                    staffDesignationUtil.updateStaffDesignation(existing, requestRecord);
                                    return existing;
                                }).orElseGet(() -> StaffDesignationUtil.mapStaffDesignationListRequest_ToStaffDesignationList(requestRecord));
                            }).toList();
                    staffDesignationUtil.saveAll(staffDesignationList);
                    s.setStaffDesignations(staffDesignationList);
                }

                // ── Staff Subjects ────────────────────────────────────────────────────
                if (staffRequest.getStaffSubjects() != null && !staffRequest.getStaffSubjects().isEmpty()) {
                    List<StaffSubjects> staffSubjects = staffRequest.getStaffSubjects().stream()
                            .map(requestRecord -> {
                                Optional<StaffSubjects> existingRecord = staff.get().getStaffSubjects() == null ? Optional.empty() :
                                        staff.get().getStaffSubjects().stream()
                                        .filter(existing -> existing.getInstitutionCode().equalsIgnoreCase(requestRecord.getInstitutionCode()) &&
                                                existing.getSubject().equals(requestRecord.getSubject()))
                                        .findFirst();
                                return existingRecord.map(existing -> {
                                    staffSubjectsUtil.updateStaffSubjects(existing, requestRecord);
                                    return existing;
                                }).orElseGet(() -> StaffSubjectsUtil.mapStaffSubjectRequest_ToStaffSubjects(requestRecord));
                            }).toList();
                    staffSubjectsUtil.saveAll(staffSubjects);
                    s.setStaffSubjects(staffSubjects);
                }

                staffRepository.save(s);
                return Optional.of(staffUtil.mapStaff_ToStaffResponse(s));
            }
        }
        return null;
    }

    public Optional<List<StaffResponse>> createStaffList(List<StaffRequest> staffRequests) {
        Optional<List<StaffResponse>> staffResponses = staffRequests.stream()
                .map(staffRequest -> {
                    Optional<Staff> existingStaff = StaffUtil.staffGlobalList.stream()
                            .filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffRequest.getInstitutionCode()) &&
                                    (s.getSnnitNumber().equalsIgnoreCase(staffRequest.getSnnitNumber())) || s.getNationalID().equalsIgnoreCase(staffRequest.getNationalID()))
                            .findFirst();

                    return existingStaff
                            .map(staff -> {
                                try {
                                    return staffUtil.updateExistingStaff(staffRequest, staff);
                                } catch (URISyntaxException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .orElseGet(() -> {
                                try {
                                    return staffUtil.createNewStaff(staffRequest);
                                } catch (URISyntaxException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                })
                .filter(Optional::isPresent) // Keep only present Optionals
                .map(Optional::get)          // Unwrap them
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.isEmpty() ? Optional.empty() : Optional.of(list)
                ));
        return staffResponses;
    }

    @Override
    public Optional<List<StaffResponse>> createStaffs(List<StaffRequest> staffRequestList) {
        return null;
    }

    @Override
    public Optional<StaffResponse> getStaffByCode(StaffCodeRequest staffCode) throws URISyntaxException, IOException {
        Optional<Staff> staff = StaffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffCode.getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffCode.getStaffCode())).findFirst();
        if (!staff.isEmpty()) {
            return Optional.ofNullable(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        }
        return null;
    }

    @Override
    public Optional<List<StaffResponse>> getStaffByInstitution(SingleStringRequest beceCode) {
        Optional<List<StaffResponse>> filteredStaff = Optional.ofNullable((StaffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(beceCode.getVal()))
                .map(staff -> {
                    try {
                        return staffUtil.mapStaff_ToStaffResponse(staff);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList()));
        return filteredStaff;
    }

    @Override
    public Optional<List<StaffResponse>> getStaffByInstitutionAndDesignation(String beceCode, String designation) {
        return null;
    }

    @Override
    public Optional<StaffResponse> addStaffPermissions(List<StaffPermissionsRequest> staffPermissionsRequests) throws IOException, URISyntaxException {
        // Nothing changed in the modal (e.g. Save clicked on an untouched,
        // pre-checked list) - nothing to send to Keycloak or to store.
        if (staffPermissionsRequests == null || staffPermissionsRequests.isEmpty()) {
            return Optional.empty();
        }
        staffPermissionsUtil.KeyclaokCreateUserCredentials(staffPermissionsRequests);
        Optional<Staff> staff = StaffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffPermissionsRequests.get(0).getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffPermissionsRequests.get(0).getStaffCode())).findFirst();
        if (staff.isPresent()) {

            List<StaffPermissions> existingPermissions = Optional.ofNullable(staff.get().getStaffPermissions())
                    .orElse(Collections.emptyList());

            // Start from everything this staff member already has, keyed by
            // lowercase code. The request only carries what was toggled in
            // this session (see permissionBuilder in _permissions.js), so the
            // stored list must be existing + changes - not just the changes,
            // which is what used to be saved and dropped earlier grants.
            // Later duplicates win instead of throwing, and a record with no
            // code is skipped instead of causing an NPE.
            Map<String, StaffPermissions> merged = new LinkedHashMap<>();
            for (StaffPermissions p : existingPermissions) {
                if (p != null && p.getPermissionCode() != null) {
                    merged.put(p.getPermissionCode().toLowerCase(), p);
                }
            }

            // Only the records touched by this request go to the standalone
            // collection, as before.
            List<StaffPermissions> changed = new ArrayList<>();
            for (StaffPermissionsRequest requestRecord : staffPermissionsRequests) {
                if (requestRecord.getPermissionCode() == null) {
                    continue;
                }
                String permissionCode = requestRecord.getPermissionCode().toLowerCase();
                StaffPermissions existing = merged.get(permissionCode);
                if (existing != null) {
                    StaffPermissionsUtil.updateStaffPermissions(existing, requestRecord);
                    changed.add(existing);
                } else {
                    StaffPermissions created = StaffPermissionsUtil.mapStaffPermissionsRequest_ToStaffPermissions(requestRecord, staff.get().getStaffCode());
                    merged.put(permissionCode, created);
                    changed.add(created);
                }
            }

            staffPermissionsUtil.saveAll(changed);

            // Full merged list goes on the staff document
            staff.get().setStaffPermissions(new ArrayList<>(merged.values()));

            staffRepository.save(staff.get());
            return Optional.of(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        }
        return Optional.empty();
    }

    // The frontend's "Manage Permissions" modal calls this to pre-check
    // whichever permissions a staff member already has. Each permission
    // code has exactly one record (see addStaffPermissions above, which
    // overwrites state in place rather than appending), so a removed
    // permission still has a record - just with state "delete" - which is
    // why this filters to state "add" rather than returning everything.
    @Override
    public List<StaffPermissionsResponse> getPermissionsByStaff(String staffCode) {
        Optional<Staff> staff = staffRepository.findByStaffCode(staffCode);
        if (staff.isEmpty() || staff.get().getStaffPermissions() == null) {
            return Collections.emptyList();
        }
        return staff.get().getStaffPermissions().stream()
                .filter(p -> p.getState() == null || "add".equalsIgnoreCase(p.getState()))
                .map(p -> StaffPermissionsResponse.builder()
                        .id(p.getId() == null ? null : p.getId().toString())
                        .staffCode(p.getStaffCode())
                        .permissionCode(p.getPermissionCode())
                        .permission(p.getPermission())
                        .institutionCode(p.getInstitutionCode())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StaffResponse> addStaffSubjects(List<StaffSubjectsRequest> staffSubjectsRequests)
            throws IOException, URISyntaxException {

        // 1. Find the staff member
        Optional<Staff> staff = StaffUtil.staffGlobalList.stream()
                .filter(s ->
                        s.getInstitutionCode().equalsIgnoreCase(staffSubjectsRequests.get(0).getInstitutionCode()) &&
                                s.getStaffCode().equalsIgnoreCase(staffSubjectsRequests.get(0).getStaffId()))
                .findFirst();

        if (staff.isPresent()) {
            // 2. Get existing subjects (or empty list if null)
            List<StaffSubjects> existingStaffSubjects =
                    Optional.ofNullable(staff.get().getStaffSubjects())
                            .orElse(Collections.emptyList());

            // 3. Create a map with composite key: "subject_subjectClass" (case-insensitive)
            Map<String, StaffSubjects> existingStaffSubjectsMap = existingStaffSubjects.stream()
                    .collect(Collectors.toMap(
                            ss -> (ss.getSubject().toLowerCase() + "_" + ss.getSubjectClass().toLowerCase()),
                            ss -> ss
                    ));

            // 4. Process each request
            List<StaffSubjects> staffSubjects = Stream.concat(
                    existingStaffSubjects.stream()
                            .peek(existing -> staffSubjectsRequests.stream()
                                    .filter(req ->
                                            req.getSubject().equalsIgnoreCase(existing.getSubject()) &&
                                                    req.getSubjectClass().equalsIgnoreCase(existing.getSubjectClass()))
                                    .findFirst()
                                    .ifPresent(req -> StaffSubjectsUtil.updateStaffSubjects(existing, req))),
                    staffSubjectsRequests.stream()
                            .filter(req -> existingStaffSubjects.stream()
                                    .noneMatch(existing ->
                                            existing.getSubject().equalsIgnoreCase(req.getSubject()) &&
                                                    existing.getSubjectClass().equalsIgnoreCase(req.getSubjectClass())))
                            .map(StaffSubjectsUtil::mapStaffSubjectRequest_ToStaffSubjects)
            ).collect(Collectors.toList());

            // 5. Save updates
            staffSubjectsUtil.saveAll(staffSubjects);
            staff.get().setStaffSubjects(staffSubjects);
            staffRepository.save(staff.get());

            return Optional.of(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        }
        return Optional.empty();
    }


    @Override
    public Optional<StaffResponse> getStaffByStaffCode(SingleStringRequest staffcodeRequest) throws IOException, URISyntaxException {
        Optional<Staff> staff = StaffUtil.staffGlobalList.stream().filter(s -> s.getStaffCode().equalsIgnoreCase(staffcodeRequest.getVal())).findFirst();
        if (!staff.isEmpty()) {
            return Optional.ofNullable(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        }
        return null;
    }

    @Override
    public List<StaffContactResponse> getAdminStaffByInstitution(SingleStringRequest institutionCode) {
        return StaffUtil.staffGlobalList.stream()
                .filter(s -> s.getInstitutionCode().equalsIgnoreCase(institutionCode.getVal()))
                .filter(s -> "Admin".equalsIgnoreCase(s.getDesignation()))
                .map(s -> StaffContactResponse.builder()
                        .staffCode(s.getStaffCode())
                        .staffEmail(s.getStaffEmail())
                        .designation(s.getDesignation())
                        .build())
                .collect(Collectors.toList());
    }

}