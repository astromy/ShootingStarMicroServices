package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;
import com.astromyllc.shootingstar.setup.serviceInterface.ClassesServiceInterface;
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
public class ClassService implements ClassesServiceInterface {
    @Override
    public void createClass(ClassesRequest classesRequest) {

    }

    @Override
    public void createClasses(List<ClassesRequest> classesRequestList) {

    }

    @Override
    public Optional<List<ClassesRequest>> getAllClasses() {
        return Optional.empty();
    }

    @Override
    public Optional<List<ClassesRequest>> getAllClassesByClassGroup() {
        return Optional.empty();
    }

    @Override
    public Optional<List<ClassesResponse>> getAllClassesByInstitution(String institutionRequest) {
        return Optional.empty();
    }
}
