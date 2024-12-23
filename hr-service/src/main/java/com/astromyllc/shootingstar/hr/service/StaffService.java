package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffPermissionsRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
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

    @Override
    public Optional<StaffResponse> createStaff(List<StaffRequest> staffRequests) throws IOException, URISyntaxException {
        for (StaffRequest staffRequest:staffRequests) {
            Optional<Staff> staff = staffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffRequest.getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffRequest.getStaffCode())).findFirst();
            if (staff.isEmpty()) {
                Staff newStaff = staffUtil.mapStaffRequest_ToStaff(staffRequest);
                //staffRepository.save(newStaff);

                List<ProfessionalRecords> pr = staffRequest.getProfessionalRecords().stream().map(p -> professionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(p, newStaff.getStaffCode())).collect(Collectors.toList());
                professionalRecordsUtil.saveAll(pr);
                newStaff.setProfessionalRecords(pr);

                List<StaffDocuments> sds = staffRequest.getStaffDocuments().stream().map(sd -> staffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(sd, newStaff.getStaffCode())).collect(Collectors.toList());
                staffDocumentsUtil.saveAll(sds);
                newStaff.setStaffDocuments(sds);

                List<Dependants> dp = staffRequest.getDependants().stream().map(d -> DependantsUtil.mapDependantsRequest_ToDependants(d, newStaff.getStaffCode())).collect(Collectors.toList());
                dependantsUtil.saveAll(dp);
                newStaff.setDependants(dp);

                List<AcademicRecords> ac = staffRequest.getAcademicRecords().stream().map(a -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(a, newStaff.getStaffCode())).collect(Collectors.toList());
                academicRecordsUtil.saveAll(ac);
                newStaff.setAcademicRecords(ac);

                staffRepository.save(newStaff);

                staffUtil.staffGlobalList.add(newStaff);
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
                            }).orElseGet(() -> professionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(requestRecord, staff.get().getStaffCode()));
                        })
                        .collect(Collectors.toList());

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
                                dependantsUtil.updateDependants(existing, requestDependant,s.getStaffCode()); // Assuming an update method exists
                                return existing;
                            }).orElseGet(() -> dependantsUtil.mapDependantsRequest_ToDependants(requestDependant, staff.get().getStaffCode()));
                        })
                        .collect(Collectors.toList());

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
                            }).orElseGet(() -> academicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(requestRecord, staff.get().getStaffCode()));
                        })
                        .collect(Collectors.toList());

                // Save the updated list of academic records
                academicRecordsUtil.saveAll(updatedAcademicRecords);

                // Set the updated list on the staff object
                s.setAcademicRecords(updatedAcademicRecords);


                staffRepository.save(s);
                return Optional.of(staffUtil.mapStaff_ToStaffResponse(s));
            }
        }
       return null;
    }

    @Override
    public Optional<List<StaffResponse>> createStaffs(List<StaffRequest> staffRequestList) {
        return null;
    }

    @Override
    public Optional<StaffResponse> getStaffByCode(StaffCodeRequest staffCode) throws URISyntaxException, IOException {
        Optional<Staff> staff = staffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffCode.getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffCode.getStaffCode())).findFirst();
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
                .collect(Collectors.toList())));
    }

    @Override
    public Optional<List<StaffResponse>> getStaffByInstitutionAndDesignation(String beceCode, String designation) {
        return null;
    }

    @Override
    public Optional<StaffResponse> addStaffPermissions(List<StaffPermissionsRequest> staffPermissionsRequests) throws IOException, URISyntaxException {
        StaffPermissionsUtil.KeyclaokCreateUserCredentials(staffPermissionsRequests);
        Optional<Staff> staff = staffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffPermissionsRequests.get(0).getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffPermissionsRequests.get(0).getStaffCode())).findFirst();
        if (!staff.isEmpty()) {

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
                            staffPermissionsUtil.updateStaffPermissions(existing, requestRecord);
                            return existing;
                        } else {
                            return staffPermissionsUtil.mapStaffPermissionsRequest_ToStaffPermissions(requestRecord, staff.get().getStaffCode());
                        }
                    })
                    .collect(Collectors.toList());

            // Save the updated list of professional records
            staffPermissionsUtil.saveAll(updatedStaffPermissions);

            // Set the updated list on the staff object
            staff.get().setStaffPermissions(updatedStaffPermissions);

            staffRepository.save(staff.get());
            return Optional.of(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        }
        return null;
    }



}
