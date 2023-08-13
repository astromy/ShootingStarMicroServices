package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.request.GradingSettingRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;

import java.util.List;
import java.util.Optional;

public interface ClassesServiceInterface {
    public void createClass(ClassesRequest classesRequest);
    public  void createClasses(List<ClassesRequest> classesRequestList);
    public Optional<List<ClassesRequest>> getAllClasses();
    public Optional<List<ClassesRequest>> getAllClassesByClassGroup();
    public Optional<List<ClassesRequest>> getAllClassesByInstitution(InstitutionRequest institutionRequest);
}
