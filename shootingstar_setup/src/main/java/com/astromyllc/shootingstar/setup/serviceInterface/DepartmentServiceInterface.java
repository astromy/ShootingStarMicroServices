package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.DepartmentRequest;
import com.astromyllc.shootingstar.setup.dto.response.DepartmentResponse;

import java.util.List;
import java.util.Optional;

public interface DepartmentServiceInterface {
    void createDepartments(DepartmentRequest departmentRequest);
    List<Optional<DepartmentResponse>> getDepartmentByInstitution(String beceCode);
}
