package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffPermissionsRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffSubjectsRequest;
import com.astromyllc.shootingstar.hr.dto.request.api.StaffCodeRequest;
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
        for (StaffRequest staffRequest:staffRequests) {
            Optional<Staff> staff = StaffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffRequest.getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffRequest.getStaffCode())).findFirst();
            if (staff.isEmpty()) {
                Staff newStaff = staffUtil.mapStaffRequest_ToStaff(staffRequest);
                //staffRepository.save(newStaff);

                if (staffRequest.getProfessionalRecords()!= null && !staffRequest.getProfessionalRecords().isEmpty()){
                    List<ProfessionalRecords> pr = staffRequest.getProfessionalRecords().stream().map(p -> ProfessionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(p, newStaff.getStaffCode())).toList();
                    professionalRecordsUtil.saveAll(pr);
                    newStaff.setProfessionalRecords(pr);
                }

                if ( staffRequest.getStaffDocuments()!= null && !staffRequest.getStaffDocuments().isEmpty()){
                List<StaffDocuments> sds = staffRequest.getStaffDocuments().stream().map(sd -> StaffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(sd, newStaff.getStaffCode())).toList();
                staffDocumentsUtil.saveAll(sds);
                newStaff.setStaffDocuments(sds);
                }

                if (staffRequest.getDependants()!= null && !staffRequest.getDependants().isEmpty()){
                List<Dependants> dp = staffRequest.getDependants().stream().map(d -> DependantsUtil.mapDependantsRequest_ToDependants(d, newStaff.getStaffCode())).toList();
                dependantsUtil.saveAll(dp);
                newStaff.setDependants(dp);
                }

                if (staffRequest.getAcademicRecords()!= null && !staffRequest.getAcademicRecords().isEmpty()){
                List<AcademicRecords> ac = staffRequest.getAcademicRecords().stream().map(a -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(a, newStaff.getStaffCode())).toList();
                academicRecordsUtil.saveAll(ac);
                newStaff.setAcademicRecords(ac);
                }

                if (staffRequest.getStaffDesignations()!= null && !staffRequest.getStaffDesignations().isEmpty()){
                    List<DesignationList> dl = staffRequest.getStaffDesignations().stream().map(StaffDesignationUtil::mapStaffDesignationListRequest_ToStaffDesignationList).toList();
                    staffDesignationUtil.saveAll(dl);
                    newStaff.setStaffDesignations(dl);
                }

                if (staffRequest.getStaffSubjects()!= null && !staffRequest.getStaffSubjects().isEmpty()){
                    List<StaffSubjects> ss = staffRequest.getStaffSubjects().stream().map(StaffSubjectsUtil::mapStaffSubjectRequest_ToStaffSubjects).toList();
                    staffSubjectsUtil.saveAll(ss);
                    newStaff.setStaffSubjects(ss);
                }

                staffRepository.save(newStaff);

                StaffUtil.staffGlobalList.add(newStaff);
                return Optional.of(staffUtil.mapStaff_ToStaffResponse(newStaff));
            } else {

                Staff s = staffUtil.mapStaffRequest_ToStaff(staffRequest, staff.get());

                List<ProfessionalRecords> updatedProfessionalRecords = staffRequest.getProfessionalRecords().stream()
                        .map(requestRecord -> {
                            // Check if the professional record already exists
                            Optional<ProfessionalRecords> existingRecord = staff.get().getProfessionalRecords().stream()
                                    .filter(existing -> existing.getNameOfInstitution().equalsIgnoreCase(requestRecord.getNameOfInstitution()) &&
                                            existing.getDateOfEmployment().equals(requestRecord.getDateOfEmployment()))
                                    .findFirst();

                            // If exists, update the record; otherwise, add as new
                            return existingRecord.map(existing -> {
                                professionalRecordsUtil.updateProfessionalRecord(existing, requestRecord,s.getStaffCode()); // Assuming an update method exists
                                return existing;
                            }).orElseGet(() -> ProfessionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(requestRecord, staff.get().getStaffCode()));
                        })
                        .toList();

                // Save the updated list of professional records
                professionalRecordsUtil.saveAll(updatedProfessionalRecords);

                // Set the updated list on the staff object
                s.setProfessionalRecords(updatedProfessionalRecords);



                List<Dependants> updatedDependants = staffRequest.getDependants().stream()
                        .map(requestDependant -> {
                            // Check if the dependant already exists
                            Optional<Dependants> existingDependant = staff.get().getDependants().stream()
                                    .filter(existing -> existing.getName().equalsIgnoreCase(requestDependant.getName()) &&
                                            existing.getDateOfBirth().equals(requestDependant.getDateOfBirth()))
                                    .findFirst();

                            // If exists, update the record; otherwise, add as new
                            return existingDependant.map(existing -> {
                                DependantsUtil.updateDependants(existing, requestDependant,s.getStaffCode()); // Assuming an update method exists
                                return existing;
                            }).orElseGet(() -> DependantsUtil.mapDependantsRequest_ToDependants(requestDependant, staff.get().getStaffCode()));
                        })
                        .toList();

                // Save the updated list of dependants
                dependantsUtil.saveAll(updatedDependants);

                // Set the updated list on the staff object
                s.setDependants(updatedDependants);


                List<AcademicRecords> updatedAcademicRecords = staffRequest.getAcademicRecords().stream()
                        .map(requestRecord -> {
                            // Check if the academic record already exists
                            Optional<AcademicRecords> existingRecord = staff.get().getAcademicRecords().stream()
                                    .filter(existing -> existing.getNameOfInstitution().equalsIgnoreCase(requestRecord.getNameOfInstitution()) &&
                                            existing.getDateOfGraduation().equals(requestRecord.getDateOfGraduation()))
                                    .findFirst();

                            // If exists, update the record; otherwise, add as new
                            return existingRecord.map(existing -> {
                                academicRecordsUtil.updateAcademicRecord(existing, requestRecord,s.getStaffCode()); // Assuming an update method exists
                                return existing;
                            }).orElseGet(() -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(requestRecord, staff.get().getStaffCode()));
                        })
                        .toList();

                // Save the updated list of academic records
                academicRecordsUtil.saveAll(updatedAcademicRecords);

                // Set the updated list on the staff object
                s.setAcademicRecords(updatedAcademicRecords);


                List<DesignationList> staffDesignationList = staffRequest.getStaffDesignations().stream()
                        .map(requestRecord -> {
                            // Check if the academic record already exists
                            Optional<DesignationList> existingRecord = staff.get().getStaffDesignations().stream()
                                    .filter(existing -> existing.getInstitutionCode().equalsIgnoreCase(requestRecord.getInstitutionCode()) &&
                                            existing.getDesignation().equals(requestRecord.getDesignation()))
                                    .findFirst();

                            // If exists, update the record; otherwise, add as new
                            return existingRecord.map(existing -> {
                                staffDesignationUtil.updateStaffDesignation(existing, requestRecord); // Assuming an update method exists
                                return existing;
                            }).orElseGet(() -> StaffDesignationUtil.mapStaffDesignationListRequest_ToStaffDesignationList(requestRecord));
                        })
                        .toList();

                // Save the updated list of academic records
                staffDesignationUtil.saveAll(staffDesignationList);

                // Set the updated list on the staff object
                s.setStaffDesignations(staffDesignationList);


                List<StaffSubjects> staffSubjects = staffRequest.getStaffSubjects().stream()
                        .map(requestRecord -> {
                            // Check if the academic record already exists
                            Optional<StaffSubjects> existingRecord = staff.get().getStaffSubjects().stream()
                                    .filter(existing -> existing.getInstitutionCode().equalsIgnoreCase(requestRecord.getInstitutionCode()) &&
                                            existing.getSubject().equals(requestRecord.getSubject()))
                                    .findFirst();

                            // If exists, update the record; otherwise, add as new
                            return existingRecord.map(existing -> {
                                staffSubjectsUtil.updateStaffSubjects(existing, requestRecord); // Assuming an update method exists
                                return existing;
                            }).orElseGet(() -> StaffSubjectsUtil.mapStaffSubjectRequest_ToStaffSubjects(requestRecord));
                        })
                        .toList();

                // Save the updated list of academic records
                staffSubjectsUtil.saveAll(staffSubjects);

                // Set the updated list on the staff object
                s.setStaffSubjects(staffSubjects);

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
                                    (s.getSnnitNumber().equalsIgnoreCase(staffRequest.getSnnitNumber()))|| s.getNationalID().equalsIgnoreCase(staffRequest.getNationalID()))
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
       if(!staff.isEmpty()) {
         return  Optional.ofNullable(staffUtil.mapStaff_ToStaffResponse(staff.get()));
       }
        return null;
    }

    @Override
    public Optional<List<StaffResponse>> getStaffByInstitution(SingleStringRequest beceCode) {
        return Optional.ofNullable((StaffUtil.staffGlobalList.stream().filter(s->s.getInstitutionCode().equalsIgnoreCase(beceCode.getVal()))
                .map(staff-> {
                    try {
                        return staffUtil.mapStaff_ToStaffResponse(staff);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList()));
    }

    @Override
    public Optional<List<StaffResponse>> getStaffByInstitutionAndDesignation(String beceCode, String designation) {
        return null;
    }

    @Override
    public Optional<StaffResponse> addStaffPermissions(List<StaffPermissionsRequest> staffPermissionsRequests) throws IOException, URISyntaxException {
        staffPermissionsUtil.KeyclaokCreateUserCredentials(staffPermissionsRequests);
        Optional<Staff> staff = StaffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffPermissionsRequests.get(0).getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffPermissionsRequests.get(0).getStaffCode())).findFirst();
        if (staff.isPresent()) {

            List<StaffPermissions> existingPermissions = Optional.ofNullable(staff.get().getStaffPermissions())
                    .orElse(Collections.emptyList());

            Map<String, StaffPermissions> existingPermissionsMap = existingPermissions.stream()
                    .collect(Collectors.toMap(
                            permission -> permission.getPermissionCode().toLowerCase(), // Use lowercase for case-insensitive matching
                            permission -> permission
                    ));

            List<StaffPermissions> updatedStaffPermissions = staffPermissionsRequests.stream()
                    .map(requestRecord -> {
                        String permissionCode = requestRecord.getPermissionCode().toLowerCase();
                        StaffPermissions existing = existingPermissionsMap.get(permissionCode);

                        if (existing != null) {
                            StaffPermissionsUtil.updateStaffPermissions(existing, requestRecord);
                            return existing;
                        } else {
                            return StaffPermissionsUtil.mapStaffPermissionsRequest_ToStaffPermissions(requestRecord, staff.get().getStaffCode());
                        }
                    })
                    .toList();

            // Save the updated list of professional records
            staffPermissionsUtil.saveAll(updatedStaffPermissions);

            // Set the updated list on the staff object
            staff.get().setStaffPermissions(updatedStaffPermissions);

            staffRepository.save(staff.get());
            return Optional.of(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        }
        return Optional.empty();
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
        if(!staff.isEmpty()) {
            return  Optional.ofNullable(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        }
        return null;
    }


}
