package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.DepartmentRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.DepartmentResponse;
import com.astromyllc.shootingstar.setup.model.Department;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.DepartmentRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.DepartmentServiceInterface;
import com.astromyllc.shootingstar.setup.utils.DepartmentUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentService implements DepartmentServiceInterface {
    private final InstitutionUtils institutionUtils;
    private final InstitutionRepository institutionRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentUtil departmentUtil;
    @Override
    public List<Optional<DepartmentResponse>> createDepartments(DepartmentRequest departmentRequest) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(departmentRequest.getInstitution()))
                .findFirst()
                .map(inst -> {
                    // Handle the department list directly without introducing a new variable
                    inst.setDepartmentList(
                            new ArrayList<>(Optional.ofNullable(inst.getDepartmentList()).orElse(new ArrayList<>())) // to make sure you have a valid List reference
                    );

                    // Filter new departments and add them to the department list
                   List<Department> newDepartments=
                            departmentRequest.getDepartmentDetailsList().stream()
                                    .map(d->{
                                        Department nd= DepartmentUtil.mapDepartmentRequest_ToDepartment(d);
                                    nd.setInstitution(inst);
                                    return nd;
                                    })
                                    .filter(d -> inst.getDepartmentList().stream().noneMatch(existing -> existing.getName().equalsIgnoreCase(d.getName())))
                                    .toList();


                    // Save the updated institution with the new department list
                    departmentRepository.saveAll(newDepartments);
                    inst.getDepartmentList().addAll(newDepartments);

                    // Return the mapped list of department responses, each wrapped in an Optional
                    return inst.getDepartmentList().stream()
                            .map(DepartmentUtil::mapDepartment_ToDepartmentResponse)
                            .collect(Collectors.toList());
                })
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<Optional<DepartmentResponse>>();  // Return an empty list if institution is not found
                });
    }

    @Override
    public List<Optional<DepartmentResponse>> getDepartmentByInstitution(SingleStringRequest beceCode) {
        String finalBeceCode= beceCode.getVal();
        return Optional.ofNullable(InstitutionUtils.institutionGlobalList)  // Ensure the list is not null
                .flatMap(list -> list.stream()  // Convert to stream if the list is not null
                        .filter(i -> i.getBececode().equalsIgnoreCase(finalBeceCode))  // Filter by BECE code
                        .findFirst()  // Find the first matching institution
                        .flatMap(i -> Optional.ofNullable(i.getDepartmentList()))  // Safely get department list (check for null)
                        .map(departmentList -> departmentList.stream()  // Convert department list to stream if it's not null
                                .map(DepartmentUtil::mapDepartment_ToDepartmentResponse)  // Map each department
                                .collect(Collectors.toList())))  // Collect the results to a list
                .orElse(Collections.emptyList());  // Return an empty list if any part is null
    }
}
