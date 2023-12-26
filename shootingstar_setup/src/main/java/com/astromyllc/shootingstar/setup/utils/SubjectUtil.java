package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.SubjectDetails;
import com.astromyllc.shootingstar.setup.dto.request.SubjectRequest;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.model.Subject;
import com.astromyllc.shootingstar.setup.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubjectUtil {
    private final SubjectRepository subjectRepository;
    public static List<Subject> subjectGlobalList = null;

    @Bean
    private void findAllSubjects() {
        subjectGlobalList = subjectRepository.findAll();
        log.info("Global list of Subjects Populated with {} Record", subjectGlobalList.stream().count());
    }

    public static Subject mapSubjectRequest_ToSubject(SubjectDetails s) {
        return Subject.builder()
                .classGroup(s.getClassGroup())
                .name(s.getName())
                .preference(s.getPreference())
                .build();
    }

    public static Subject mapSubjectRequest_ToSubject(SubjectDetails s, Subject subject) {
        subject.setClassGroup(s.getClassGroup());
        subject.setName(s.getName());
        subject.setPreference(s.getPreference());
        return subject;
    }


    public static Optional<SubjectResponse> mapSubject_ToSubjectResponse(Subject s) {
        return Optional.ofNullable(SubjectResponse.builder()
                .id(s.getIdSubject())
                .classGroup(s.getClassGroup())
                .name(s.getName())
                .preference(s.getPreference())
                .build());
    }

}
