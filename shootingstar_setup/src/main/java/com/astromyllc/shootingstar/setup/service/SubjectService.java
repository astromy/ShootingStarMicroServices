package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SubjectRequest;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.serviceInterface.SubjectServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectService implements SubjectServiceInterface {
    @Override
    public void createSubject(SubjectRequest subjectRequest) {

    }

    @Override
    public void createSubjects(List<SubjectRequest> subjectRequestList) {

    }

    @Override
    public Optional<List<SubjectResponse>> getAllSubjects() {
        return Optional.empty();
    }

    @Override
    public Optional<List<SubjectResponse>> getAllSubjectsByClass(ClassesRequest classesRequest) {
        return Optional.empty();
    }

    @Override
    public Optional<List<SubjectResponse>> getAllSubjectsByInstitution(String institutionRequest) {
        return Optional.empty();
    }

    @Override
    public Optional<List<SubjectResponse>> getAllSubjectsByClassGroup(String classGroup) {
        return Optional.empty();
    }
}
