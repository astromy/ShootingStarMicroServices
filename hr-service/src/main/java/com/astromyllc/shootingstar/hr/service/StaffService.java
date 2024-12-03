package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
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
import java.util.List;
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
    private final AcademicRecordsUtil academicRecordsUtil;

    @Override
    public Optional<StaffResponse> createStaff(StaffRequest staffRequest) throws IOException, URISyntaxException {
        Optional<Staff> staff = staffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffRequest.getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffRequest.getStaffCode())).findFirst();
        if (staff.isEmpty()) {
            Staff newStaff = staffUtil.mapStaffRequest_ToStaff(staffRequest);
            //staffRepository.save(newStaff);

            List<ProfessionalRecords> pr = staffRequest.getProfessionalRecords().stream().map(p -> professionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(p, newStaff.getStaffCode())).collect(Collectors.toList());
           // professionalRecordsUtil.saveAll(pr);
            newStaff.setProfessionalRecords(pr);

            List<StaffDocuments> sds = staffRequest.getStaffDocuments().stream().map(sd -> staffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(sd, newStaff.getStaffCode())).collect(Collectors.toList());
            //staffDocumentsUtil.saveAll(sds);
            newStaff.setStaffDocuments(sds);

            List<Dependants> dp = staffRequest.getDependants().stream().map(d -> DependantsUtil.mapDependantsRequest_ToDependants(d, newStaff.getStaffCode())).collect(Collectors.toList());
            //dependantsUtil.saveAll(dp);
            newStaff.setDependants(dp);

            List<AcademicRecords> ac = staffRequest.getAcademicRecords().stream().map(a -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(a, newStaff.getStaffCode())).collect(Collectors.toList());
            //academicRecordsUtil.saveAll(ac);
            newStaff.setAcademicRecords(ac);

            staffRepository.save(newStaff);

            staffUtil.staffGlobalList.add(newStaff);
            return Optional.of(staffUtil.mapStaff_ToStaffResponse(newStaff));
        } else {

            Staff s =staffUtil.mapStaffRequest_ToStaff(staffRequest, staff.get());

            List<ProfessionalRecords> newRecords = staffRequest.getProfessionalRecords().stream()
                    .map(requestRecord -> professionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(requestRecord, staff.get().getStaffCode()))
                    .collect(Collectors.toList());
                s.setProfessionalRecords(newRecords);
            //professionalRecordsUtil.saveAll(newRecords);



            List<StaffDocuments> newDocs = staffRequest.getStaffDocuments().stream()
                    .map(requestDoc -> staffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(requestDoc, staff.get().getStaffCode()))
                    .collect(Collectors.toList());

            s.setStaffDocuments(newDocs);


            List<Dependants> newDependants = staffRequest.getDependants().stream()
                    .map(requestDependants -> dependantsUtil.mapDependantsRequest_ToDependants(requestDependants, staff.get().getStaffCode()))
                    .collect(Collectors.toList());

            // Save the new records
            s.setDependants(newDependants);


            List<AcademicRecords> existingAcademicRecords = staff.get().getAcademicRecords();
            List<AcademicRecords> newAcademicRecords = staffRequest.getAcademicRecords().stream()
                    .map(requestRecord -> academicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(requestRecord, staff.get().getStaffCode()))
                    .collect(Collectors.toList());

                // Save the new records
            s.setAcademicRecords(newAcademicRecords);
            staffRepository.save(s);
            return Optional.of(staffUtil.mapStaff_ToStaffResponse(s));
        }
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
}
