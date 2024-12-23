package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.AcademicRecordsRequest;
import com.astromyllc.shootingstar.hr.dto.response.AcademicRecordsResponse;
import com.astromyllc.shootingstar.hr.model.AcademicRecords;
import com.astromyllc.shootingstar.hr.model.StaffDocuments;
import com.astromyllc.shootingstar.hr.repository.AcademicRecordsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AcademicRecordsUtil {
    private final AcademicRecordsRepository academicRecordsRepository;
    public static List<AcademicRecords> academicRecordsGlobalList;
    @Value("${gateway.host}")
    private String host;
    @Bean
    private void fetchAllAcademicRecords() {
        academicRecordsGlobalList = academicRecordsRepository.findAll();
        log.info("{} ACADEMIC RECORDS FETCHED",academicRecordsGlobalList.size());
    }
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static AcademicRecords mapAcademicRecordRequest_ToAcademicRecords(AcademicRecordsRequest a, String staffCode) {
        return AcademicRecords.builder()
                .certificateType(a.getCertificateType())
                .dateOfAdmission(LocalDate.parse(a.getDateOfAdmission(),formatter))
                .dateOfGraduation(LocalDate.parse(a.getDateOfGraduation(),formatter))
                .programOffered(a.getProgramOffered())
                .nameOfInstitution(a.getNameOfInstitution())
                .staffAcademicRecords(staffCode)
                .supportingDocs(a.getSupportingDocs())
                .institutionCode(a.getInstitutionCode())
                .build();
    }
    public static AcademicRecordsResponse mapAcademicRecord_ToAcademicRecordsResponse(AcademicRecords a) {
        return AcademicRecordsResponse.builder()
                .id(String.valueOf(a.getId()))
                .certificateType(a.getCertificateType())
                .dateOfAdmission(a.getDateOfAdmission())
                .dateOfGraduation(a.getDateOfGraduation())
                .programOffered(a.getProgramOffered())
                .nameOfInstitution(a.getNameOfInstitution())
                .supportingDocs(a.getSupportingDocs())
                .institutionCode(a.getInstitutionCode())
                .build();
    }

    public void saveAll(List<AcademicRecords> ac) {
        academicRecordsRepository.saveAll(ac);
        academicRecordsGlobalList.addAll(ac);
    }

    public void updateAcademicRecord(AcademicRecords existing, AcademicRecordsRequest requestRecord,String staffCode) {
        existing.setStaffAcademicRecords(staffCode);
        existing.setCertificateType(requestRecord.getCertificateType());
        existing.setDateOfAdmission(LocalDate.parse(requestRecord.getDateOfAdmission()));
        existing.setInstitutionCode(requestRecord.getInstitutionCode());
        existing.setDateOfGraduation(LocalDate.parse(requestRecord.getDateOfGraduation()));
        existing.setProgramOffered(requestRecord.getProgramOffered());
        existing.setSupportingDocs(requestRecord.getSupportingDocs());
        existing.setNameOfInstitution(requestRecord.getNameOfInstitution());
    }
}
