package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.StaffDocumentsRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.api.StaffCodeRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;
import com.astromyllc.shootingstar.hr.model.*;
import com.astromyllc.shootingstar.hr.repository.ProfessionalRecordsRepository;
import com.astromyllc.shootingstar.hr.repository.StaffDocumentsRepository;
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
    public StaffResponse createStaff(StaffRequest staffRequest) throws IOException, URISyntaxException {
        Optional<Staff> staff = staffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffRequest.getInstitutionCode())).findFirst();
        if (staff.isEmpty()) {
            Staff newStaff = staffUtil.mapStaffRequest_ToStaff(staffRequest);
            staffRepository.save(newStaff);

            List<ProfessionalRecords> pr = staffRequest.getProfessionalRecords().stream().map(p -> professionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(p, newStaff.getStaffCode())).collect(Collectors.toList());
            professionalRecordsUtil.saveAll(pr);

            List<StaffDocuments> sds = staffRequest.getStaffDocuments().stream().map(sd -> staffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(sd, newStaff.getStaffCode())).collect(Collectors.toList());
            staffDocumentsUtil.saveAll(sds);
            List<Dependants> dp = staffRequest.getDependants().stream().map(d -> DependantsUtil.mapDependantsRequest_ToDependants(d, newStaff.getStaffCode())).collect(Collectors.toList());
            dependantsUtil.saveAll(dp);
            List<AcademicRecords> ac = staffRequest.getAcademicRecords().stream().map(a -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(a, newStaff.getStaffCode())).collect(Collectors.toList());
            academicRecordsUtil.saveAll(ac);

            staffUtil.staffGlobalList.add(newStaff);
            return staffUtil.mapStaff_ToStaffResponse(newStaff);
        } else {
            staffRepository.save(staffUtil.mapStaffRequest_ToStaff(staffRequest, staff.get()));
        }
        return null;
    }

    @Override
    public List<StaffResponse> createStaffs(List<StaffRequest> staffRequestList) {
        return null;
    }

    @Override
    public StaffResponse getStaffByCode(StaffCodeRequest staffCode) throws URISyntaxException, IOException {
        Optional<Staff> staff = staffUtil.staffGlobalList.stream().filter(s -> s.getInstitutionCode().equalsIgnoreCase(staffCode.getInstitutionCode()) && s.getStaffCode().equalsIgnoreCase(staffCode.getStaffCode())).findFirst();
       if(!staff.isEmpty()) {
         return  staffUtil.mapStaff_ToStaffResponse(staff.get());
       }
        return null;
    }

    @Override
    public List<StaffResponse> getStaffByInstitution(String beceCode) {
        return null;
    }

    @Override
    public List<StaffResponse> getStaffByInstitutionAndDesignation(String beceCode, String designation) {
        return null;
    }
}
