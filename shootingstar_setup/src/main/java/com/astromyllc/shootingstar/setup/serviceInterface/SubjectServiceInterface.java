package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SubjectRequest;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.model.Classes;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Component
public interface SubjectServiceInterface {
    public void createSubject(SubjectRequest subjectRequest);
    public  void createSubjects(List<SubjectRequest> subjectRequestList);
    public Optional<List<SubjectResponse>> getAllSubjects();
    public Optional<List<SubjectResponse>> getAllSubjectsByClass(ClassesRequest classesRequest);
    public Optional<List<SubjectResponse>> getAllSubjectsByInstitution(String institutionRequest);
    public Optional<List<SubjectResponse>> getAllSubjectsByClassGroup(String classGroup);
}
