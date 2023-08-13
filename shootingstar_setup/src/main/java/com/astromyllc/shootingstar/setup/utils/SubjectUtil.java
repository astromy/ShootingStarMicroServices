package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.SubjectRequest;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.model.Subject;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubjectUtil {
    private final SubjectRepository subjectRepository;
    public static List<Subject> subjectGlobalList=null;

    @Bean
    private void findAllSubjects(){
        subjectGlobalList=subjectRepository.findAll();
        log.info("Global list of Subjects Populated with {} Record",subjectGlobalList.stream().count());
    }

    public static Subject mapSubjectRequest_ToSubject(SubjectRequest s) {
        return Subject.builder()
                .classGroup(s.getClassGroup())
                .name(s.getName())
                .preference(s.getPreference())
                .build();
    }

    public static SubjectResponse mapSubject_ToSubjectResponse(Subject s) {
        return SubjectResponse.builder()
                .id(s.getIdSubject())
                .classGroup(s.getClassGroup())
                .name(s.getName())
                .preference(s.getPreference())
                .build();
    }

}
