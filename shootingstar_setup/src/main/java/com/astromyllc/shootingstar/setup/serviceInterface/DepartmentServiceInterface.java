package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.DepartmentRequest;
import com.astromyllc.shootingstar.setup.dto.response.DepartmentResponse;

import java.util.List;
import java.util.Optional;

public interface DepartmentServiceInterface {
    void createDepartments(List<DepartmentRequest> departmentRequest);
    Optional<List<DepartmentResponse>> getDepartmentByInstitution(String beceCode);
}
