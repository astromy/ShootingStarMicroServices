package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.ProfessionalRecordsRequest;
import com.astromyllc.shootingstar.hr.dto.response.ProfessionalRecordsResponse;
import com.astromyllc.shootingstar.hr.model.ProfessionalRecords;
import com.astromyllc.shootingstar.hr.repository.ProfessionalRecordsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfessionalRecordsUtil {
    private final ProfessionalRecordsRepository professionalRecordsRepository;
    public static List<ProfessionalRecords> professionalRecordsGlobalList;
    @Bean
    private void fetchAllProfessionalRecords() {
        professionalRecordsGlobalList = professionalRecordsRepository.findAll();
        log.info("{} PROFESSIONAL RECORDS FETCHED",professionalRecordsGlobalList.size());
    }
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static ProfessionalRecords mapProfessionalRecordRequest_ToProfessionalRecords(ProfessionalRecordsRequest p,String staffCode) {
        return ProfessionalRecords.builder()
                .dateOfEmployment(LocalDate.parse(p.getDateOfEmployment(),formatter))
                .dateOfDeparture(LocalDate.parse(p.getDateOfDeparture(),formatter))
                .designationAtInstitution(p.getDesignationAtInstitution())
                .nameOfInstitution(p.getNameOfInstitution())
                .employmentTypeAtInstitution(p.getEmploymentTypeAtInstitution())
                .staffProfessionalRecords(staffCode)
                .institutionCode(p.getInstitutionCode())
                .build();
    }
    public static ProfessionalRecordsResponse mapProfessionalRecord_ToProfessionalRecordsResponse(ProfessionalRecords p) {
        return ProfessionalRecordsResponse.builder()
                .id(String.valueOf(p.getId()))
                .dateOfEmployment(p.getDateOfEmployment())
                .dateOfDeparture(p.getDateOfDeparture())
                .designationAtInstitution(p.getDesignationAtInstitution())
                .nameOfInstitution(p.getNameOfInstitution())
                .employmentTypeAtInstitution(p.getEmploymentTypeAtInstitution())
                .build();
    }

    public void saveAll(List<ProfessionalRecords> pr) {
        professionalRecordsRepository.saveAll(pr);
        professionalRecordsGlobalList.addAll(pr);
    }
}
