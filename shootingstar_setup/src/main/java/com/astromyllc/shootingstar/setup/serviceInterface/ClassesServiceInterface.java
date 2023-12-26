package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.ClassGroupRequest;
import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;

import java.util.List;
import java.util.Optional;

public interface ClassesServiceInterface {
    public void createClass(ClassesRequest classesRequest);
    public  void createClasses(ClassesRequest classesRequestList);
    public List<Optional<ClassesResponse>> getAllClasses();
    public List<Optional<ClassesResponse>> getAllClassesByClassGroup(ClassGroupRequest classGroupRequest);
    public Optional<List<Optional<ClassesResponse>>> getAllClassesByInstitution(String institutionRequest);
}
