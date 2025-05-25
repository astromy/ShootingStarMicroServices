package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.LookupRequest;
import com.astromyllc.shootingstar.setup.dto.request.SubjectDetails;
import com.astromyllc.shootingstar.setup.dto.request.SubjectRequest;
import com.astromyllc.shootingstar.setup.dto.response.LookupResponse;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.model.Lookup;
import com.astromyllc.shootingstar.setup.model.Subject;
import com.astromyllc.shootingstar.setup.repository.SubjectRepository;
import com.astromyllc.shootingstar.setup.service.LookUpService;
import com.astromyllc.shootingstar.setup.serviceInterface.LookupServiceInterface;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubjectUtil {
    private final SubjectRepository subjectRepository;

    private final LookUpService l;
    public static List<Subject> subjectGlobalList = null;

    @PostConstruct
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


    public Optional<SubjectResponse> mapSubject_ToSubjectResponse(Subject s) {
       String lc=s.getClassGroup();
        return Optional.ofNullable(SubjectResponse.builder()
                .id(s.getIdSubject())
                .classGroup(s.getClassGroup())
                .name(s.getName())
                .classGroupName(l.getLookUpById(s.getClassGroup()).get().get().getName())
                .preference(s.getPreference())
                .build());
    }

}
