package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.request.SubjectDetails;
import com.astromyllc.shootingstar.setup.dto.request.SubjectRequest;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface SubjectServiceInterface {
    public List<Optional<SubjectResponse>> createSubject(SubjectRequest subjectRequest);
    public  void createSubjects(List<SubjectRequest> subjectRequestList);
    public List<Optional<SubjectResponse>> getAllSubjects();
    public List<Optional<SubjectResponse>> getAllSubjectsByClass(ClassesRequest classesRequest);
    public List<Optional<SubjectResponse>> getAllSubjectsByInstitution(SingleStringRequest institutionRequest);
    public Optional<List<Optional<SubjectResponse>>> getAllSubjectsByInstitutionAndClassGroup(SubjectDetails institutionRequest);
    public List<Optional<SubjectResponse>> getAllSubjectsByClassGroup(SingleStringRequest classGroup);
}
