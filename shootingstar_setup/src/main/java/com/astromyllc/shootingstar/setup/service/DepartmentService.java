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
    public Optional<List<Optional<DepartmentResponse>>> createDepartments(DepartmentRequest departmentRequest) {
        Optional<Institution> inst = InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(departmentRequest.getInstitution()))
                .findFirst();

        if (inst.isEmpty()) {
            log.warn("Institution not found!");
            return Optional.empty();
        }

        List<Department> dl = inst.get().getDepartmentList();
        if (dl == null) {
            dl = new ArrayList<>();
        }

        // Convert existing departments into a Set for quick lookup
        Set<String> existingDepartments = dl.stream()
                .map(d -> d.getName().toLowerCase()) // Unique key: department name
                .collect(Collectors.toSet());

        // Filter out departments that already exist
        List<Department> newDepartments = departmentRequest.getDepartmentDetailsList().stream()
                .map(DepartmentUtil::mapDepartmentRequest_ToDepartment)
                .filter(d -> !existingDepartments.contains(d.getName().toLowerCase()))
                .toList();

        // Add only unique departments
        dl.addAll(newDepartments);
        inst.get().setDepartmentList(dl);

        institutionRepository.save(inst.get());

        Optional<List<Optional<DepartmentResponse>>> dr = Optional.of(
                inst.get().getDepartmentList().stream()
                        .map(DepartmentUtil::mapDepartment_ToDepartmentResponse)
                        .toList()
        );

        return dr;
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
