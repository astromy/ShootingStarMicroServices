package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.DepartmentRequest;
import com.astromyllc.shootingstar.setup.dto.response.DepartmentResponse;
import com.astromyllc.shootingstar.setup.model.Department;
import com.astromyllc.shootingstar.setup.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepartmentUtil {

    private final DepartmentRepository departmentRepository;
    public static List<Department> departmentGlobalList=null;
    @Bean
    private void fetAllDepartment(){
        departmentGlobalList=departmentRepository.findAll();
        log.info("Global Department List populated with {} records",departmentGlobalList.stream().count());
    }

    public static Department mapDepartmentRequest_ToDepartment(DepartmentRequest dr) {
        return Department.builder()
                .name(dr.getName())
                .designationList(dr.getDesignationList().stream().map(des-> DesignationUtil.mapDesignationRequest_ToDesignation(des)).toList())
                .build();
    }

    public static Department mapDepartmentRequest_ToDepartment(DepartmentRequest dr, Department d) {
                d.setName(dr.getName());
                d.setDesignationList(dr.getDesignationList().stream().map(des-> DesignationUtil.mapDesignationRequest_ToDesignation(des,d.getDesignationList().stream().filter(des1->des.getIdDesignation().equals(des1.getIdDesignation())).findFirst().get())).collect(Collectors.toList()));
                return d;
    }

    public static DepartmentResponse mapDepartment_ToDepartmentResponse(Department d) {
        return DepartmentResponse.builder()
                .idDepartment(d.getIdDepartment())
                .name(d.getName())
                .designationList(d.getDesignationList().stream().map(des-> DesignationUtil.mapDesignation_ToDesignationResponse(des)).toList())
                .build();
    }


}