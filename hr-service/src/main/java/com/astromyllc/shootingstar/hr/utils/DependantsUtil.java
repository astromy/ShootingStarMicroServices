package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.DependantsRequest;
import com.astromyllc.shootingstar.hr.dto.response.DependantsResponse;
import com.astromyllc.shootingstar.hr.model.Dependants;
import com.astromyllc.shootingstar.hr.repository.DependantRepository;
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
public class DependantsUtil {
    private final DependantRepository dependantRepository;
    public static List<Dependants> dependantsGlobalList;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Bean
    private void fetchAllDependant() {

        dependantsGlobalList = dependantRepository.findAll();
        log.info("{} RECORDS OF DEPENDANT FETCHED",dependantsGlobalList.size());
    }

    public static Dependants mapDependantsRequest_ToDependants(DependantsRequest d,String staffCode) {
        return Dependants.builder()
                .name(d.getName())
                .dateOfBirth(LocalDate.parse(d.getDateOfBirth(), formatter))
                .gender(d.getGender())
                .birthCertificate(d.getBirthCertificate())
                .dependantPicture(d.getDependantPicture())
                .relationType(d.getRelationType())
                .staffDependant(staffCode)
                .institutionCode(d.getInstitutionCode())
                .build();
    }

    public static DependantsResponse mapDependants_ToDependantResponse(Dependants d) {
        return DependantsResponse.builder()
                .id(String.valueOf(d.getId()))
                .name(d.getName())
                .dateOfBirth(d.getDateOfBirth())
                .gender(d.getGender())
                .birthCertificate(d.getBirthCertificate())
                .dependantPicture(d.getDependantPicture())
                .relationType(d.getRelationType())
                .build();
    }

    public void saveAll(List<Dependants> dp) {
        dependantRepository.saveAll(dp);
        dependantsGlobalList.addAll(dp);
    }
}
