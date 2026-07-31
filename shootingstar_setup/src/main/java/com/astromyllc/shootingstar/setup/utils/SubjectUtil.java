package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.SubjectDetails;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.model.Lookup;
import com.astromyllc.shootingstar.setup.model.Subject;
import com.astromyllc.shootingstar.setup.repository.SubjectRepository;
import com.astromyllc.shootingstar.setup.service.LookUpService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubjectUtil {
    private static final LookupUtil lookupUtil = null;
    public static List<Subject> subjectGlobalList = null;
    private final SubjectRepository subjectRepository;
    private final LookUpService l;

    public static Subject mapSubjectRequest_ToSubject(SubjectDetails s) {
        Lookup classGroupLookup = null;

        if (s.getClassGroup() != null) {
            // Find the lookup from the global list or repository
            classGroupLookup = lookupUtil.lookupGlobalList.stream().filter(cg -> cg.getIdLookup()
                            .equals(Long.parseLong(s.getClassGroup())))
                    .findFirst()
                    .orElse(null);
        }
        return Subject.builder()
                .classGroup(classGroupLookup)
                .name(s.getName())
                .preference(s.getPreference())
                .subjectType(s.getSubjectType())
                .build();
    }

    public static Subject mapSubjectRequest_ToSubject(SubjectDetails s, Subject subject) {
        Lookup classGroupLookup = null;

        if (s.getClassGroup() != null) {
            // Find the lookup from the global list or repository
            classGroupLookup = lookupUtil.lookupGlobalList.stream().filter(cg -> cg.getIdLookup()
                            .equals(Long.parseLong(s.getClassGroup())))
                    .findFirst()
                    .orElse(null);
        }
        subject.setClassGroup(classGroupLookup);
        subject.setName(s.getName());
        subject.setPreference(s.getPreference());
        return subject;
    }

    @PostConstruct
    private void findAllSubjects() {
        subjectGlobalList = subjectRepository.findAll();
        log.info("Global list of Subjects Populated with {} Record", subjectGlobalList.stream().count());
    }


    public Optional<SubjectResponse> mapSubject_ToSubjectResponse(Subject s) {

        String lc = s.getClassGroup().getName();
        return Optional.ofNullable(SubjectResponse.builder()
                .id(s.getIdSubject())
                .classGroup(String.valueOf(s.getClassGroup().getIdLookup()))
                .name(s.getName())
                .classGroupName(s.getClassGroup().getName())/*(l.getLookUpById(s.getClassGroup()).get().get().getName())*/
                .preference(s.getPreference())
                .build());
    }

}
