package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.DepartmentDetails;
import com.astromyllc.shootingstar.setup.dto.response.DepartmentResponse;
import com.astromyllc.shootingstar.setup.model.Department;
import com.astromyllc.shootingstar.setup.repository.DepartmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepartmentUtil {

    private final DepartmentRepository departmentRepository;
    public static List<Department> departmentGlobalList=null;
    @PostConstruct
    private void fetAllDepartment(){
        departmentGlobalList=departmentRepository.findAll();
        log.info("Global Department List populated with {} records", (long) departmentGlobalList.size());
    }

    public static Department mapDepartmentRequest_ToDepartment(DepartmentDetails dr) {
        return Department.builder()
                .name(dr.getName())
                .designationList(dr.getDesignationList().stream().map(DesignationUtil::mapDesignationRequest_ToDesignation).toList())
                .build();
    }

    public static Department mapDepartmentRequest_ToDepartment(DepartmentDetails dr, Department d) {
                d.setName(dr.getName());
                d.setDesignationList(dr.getDesignationList().stream().map(des-> DesignationUtil.mapDesignationRequest_ToDesignation(des,d.getDesignationList().stream().filter(des1->des.getIdDesignation().equals(des1.getIdDesignation())).findFirst().get())).toList());
                return d;
    }

    public static Optional<DepartmentResponse> mapDepartment_ToDepartmentResponse(Department d) {
        return Optional.ofNullable(DepartmentResponse.builder()
                .idDepartment(d.getIdDepartment())
                .name(d.getName())
                .designationList(d.getDesignationList().stream().map(DesignationUtil::mapDesignation_ToDesignationResponse).toList())
                .build());
    }


}
