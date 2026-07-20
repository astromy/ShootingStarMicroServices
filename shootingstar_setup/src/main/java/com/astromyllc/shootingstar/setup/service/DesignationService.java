package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.DesignationRequest;
import com.astromyllc.shootingstar.setup.dto.request.DesignationRequestDetails;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.DesignationResponse;
import com.astromyllc.shootingstar.setup.model.Department;
import com.astromyllc.shootingstar.setup.model.Designation;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.DepartmentRepository;
import com.astromyllc.shootingstar.setup.repository.DesignationRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.DesignationServiceInterface;
import com.astromyllc.shootingstar.setup.utils.DesignationUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DesignationService implements DesignationServiceInterface {
    private final InstitutionRepository institutionRepository;
    private final InstitutionUtils institutionUtils;
    private final DesignationUtil designationUtil;
    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Optional<List<Optional<DesignationResponse>>> createDesignation(DesignationRequest designationRequest) {
        Institution inst = InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(designationRequest.getInstitutionBECECode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Institution not found"));

        Department department = inst.getDepartmentList().stream()
                .filter(dep -> dep.getIdDepartment().toString().equalsIgnoreCase(designationRequest.getDepartmentId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // IMPORTANT: Create a mutable copy of the designation list
        List<Designation> des = new ArrayList<>(department.getDesignationList());

        List<DesignationRequestDetails> requestDetails = designationRequest.getDesignationRequestDetails();

        // Build a map of existing Designations for quick lookup by code
        Map<String, Designation> designationMap = des.stream()
                .collect(Collectors.toMap(
                        Designation::getCode,
                        designation -> designation,
                        (existing, replacement) -> existing // Keeps the first occurrence
                ));

        // Iterate through the requestDetails and handle replacements
        requestDetails.forEach(reqDetail -> {
            if (designationMap.containsKey(reqDetail.getCode())) {
                // Update existing designation
                Designation existingDesignation = designationMap.get(reqDetail.getCode());
                Designation updatedDesignation = DesignationUtil.mapDesignationRequest_ToDesignation(reqDetail, existingDesignation);
                int index = des.indexOf(existingDesignation);
                des.set(index, updatedDesignation);
            } else {
                // Create new designations from request details
                List<Designation> newDesignations = designationRequest.getDesignationRequestDetails().stream()
                        .map(DesignationUtil::mapDesignationRequest_ToDesignation)
                        .collect(Collectors.toList()); // ← Use toList() which returns mutable list

                des.addAll(newDesignations);
            }

            // Save after each update
            department.setDesignationList(des);
            departmentRepository.save(department);
        });

        // Return response
        return Optional.of(inst.getDepartmentList().stream()
                .flatMap(dep -> dep.getDesignationList().stream())
                .map(DesignationUtil::mapDesignation_ToDesignationResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<List<Optional<DesignationResponse>>>> getAllDesignationByInstitution(SingleStringRequest designationRequest) {
        Optional<List<List<Optional<DesignationResponse>>>> desList =
                InstitutionUtils.institutionGlobalList.stream()
                        .filter(i -> i.getBececode().equalsIgnoreCase(designationRequest.getVal()))
                        .findFirst()
                        .map(dept -> dept.getDepartmentList().stream()
                                .map(dl -> dl.getDesignationList().stream()
                                        .map(DesignationUtil::mapDesignation_ToDesignationResponse)
                                        .toList())
                                .toList());
        return desList;
    }

    @Override
    public Optional<List<List<Optional<DesignationResponse>>>> getAllDesignationByInstitutionX(DesignationRequest designationRequest) {
        Optional<List<List<Optional<DesignationResponse>>>> desList = Optional.of(InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(designationRequest.getInstitutionBECECode())).findFirst().get()
                .getDepartmentList().stream().map(dl -> dl.getDesignationList().stream()
                        .map(DesignationUtil::mapDesignation_ToDesignationResponse).toList())
                .toList());
        return desList;
    }
}
