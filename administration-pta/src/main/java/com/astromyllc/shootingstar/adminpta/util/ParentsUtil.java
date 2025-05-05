package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.ParentsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentSubjectsRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.ParentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.model.StudentSubjects;
import com.astromyllc.shootingstar.adminpta.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParentsUtil {

    private final ParentRepository parentRepository;
    public static List<Parents> parentGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Parents mapParentRequest_ToParent(ParentsRequest r, String studentId) {
      return  Parents.builder()
              .parentType(r.getParentType())
              .email(r.getEmail())
              .lastName(r.getLastName())
              .institutionCode(r.getInstitutionCode())
              .occupation(r.getOccupation())
              .placeOfWork(r.getPlaceOfWork())
              .firstNames(r.getFirstNames())
              .contact1(r.getContact1())
              .contact2(r.getContact2())
              .studentId(r.getStudentId())
              .build();
    }


    public void updateParents(Parents p, ParentsRequest pr, String studId) {
        p.setParentType(pr.getParentType());
        p.setEmail(pr.getEmail());
        p.setContact1(pr.getContact1());
        p.setContact2(pr.getContact2());
        p.setFirstNames(pr.getFirstNames());
        p.setLastName(pr.getLastName());
        p.setOccupation(pr.getOccupation());
        p.setPlaceOfWork(pr.getPlaceOfWork());
        p.setInstitutionCode(pr.getInstitutionCode());
    }

    public void saveAll(List<Parents> p) {
        parentRepository.saveAll(p);
        parentGlobalList.addAll(p);
    }

    @Bean
    private void fetAllParents() {
        parentGlobalList = parentRepository.findAll();
        log.info("Global Parents List populated with {} records", parentGlobalList.size());
    }

    public static ParentsResponse mapParents_ToParentsResponse(Parents parents) {
        return ParentsResponse.builder()
                .parentType(parents.getParentType())
                .email(parents.getEmail())
                .lastName(parents.getLastName())
                .institutionCode(parents.getInstitutionCode())
                .occupation(parents.getOccupation())
                .placeOfWork(parents.getPlaceOfWork())
                .firstNames(parents.getFirstNames())
                .contact1(parents.getContact1())
                .contact2(parents.getContact2())
                .studentId(parents.getStudentId())
                .build();
    }
}
