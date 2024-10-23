package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.DesignationRequest;
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

import java.util.List;
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
        Institution inst = institutionUtils.institutionGlobalList.stream()
         .filter(i -> i.getBececode().equalsIgnoreCase(designationRequest.getInstitutionBECECode())).findFirst().get();
        Department department=inst.getDepartmentList().stream()
                .filter(dep -> dep.getIdDepartment().toString().equalsIgnoreCase(designationRequest.getDepartmentId())).findFirst().get();
        List<Designation>des=department.getDesignationList();
        des.addAll(designationRequest.getDesignationRequestDetails().stream()
                .map(d -> designationUtil.mapDesignationRequest_ToDesignation(d)).collect(Collectors.toList()));
        department.setDesignationList(des);
        departmentRepository.save(department);
        return Optional.ofNullable(inst.getDepartmentList() .stream()
                .flatMap(dep -> dep.getDesignationList().stream())
                .map(d -> designationUtil.mapDesignation_ToDesignationResponse(d)).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<List<Optional<DesignationResponse>>>> getAllDesignationByInstitution(SingleStringRequest designationRequest) {
        Optional<List<List<Optional<DesignationResponse>>>> desList =
                institutionUtils.institutionGlobalList.stream()
                        .filter(i -> i.getBececode().equalsIgnoreCase(designationRequest.getVal()))
                        .findFirst()
                        .map(dept -> dept.getDepartmentList().stream()
                                .map(dl -> dl.getDesignationList().stream()
                                        .map(designation -> designationUtil.mapDesignation_ToDesignationResponse(designation))
                                        .collect(Collectors.toList()))
                                .collect(Collectors.toList()));
        return desList;
    }
    @Override
    public Optional<List<List<Optional<DesignationResponse>>>> getAllDesignationByInstitutionX(DesignationRequest designationRequest) {
        Optional<List<List<Optional<DesignationResponse>>>> desList =  Optional.ofNullable(institutionUtils.institutionGlobalList.stream()
                .filter(i->i.getBececode().equalsIgnoreCase(designationRequest.getInstitutionBECECode())).findFirst().get()
                .getDepartmentList().stream().map(dl->dl.getDesignationList().stream()
                        .map(z->designationUtil.mapDesignation_ToDesignationResponse(z)).collect(Collectors.toList()))
                .collect(Collectors.toList()));
        return desList;
    }
}
