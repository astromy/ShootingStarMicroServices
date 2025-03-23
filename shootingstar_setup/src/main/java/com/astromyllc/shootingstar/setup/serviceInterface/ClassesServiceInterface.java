package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.ClassDetail;
import com.astromyllc.shootingstar.setup.dto.request.ClassGroupRequest;
import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;

import java.util.List;
import java.util.Optional;

public interface ClassesServiceInterface {
    public void createClass(ClassesRequest classesRequest);
    public Optional<List<Optional<ClassesResponse>>> createClasses(ClassesRequest classesRequestList);
    public List<Optional<ClassesResponse>> getAllClasses();
    public Optional<List<Optional<ClassesResponse>>> getAllClassesByClassGroup(ClassGroupRequest classGroupRequest);
    public List<Optional<ClassesResponse>> getAllClassesByInstitution(SingleStringRequest institutionRequest);
}
