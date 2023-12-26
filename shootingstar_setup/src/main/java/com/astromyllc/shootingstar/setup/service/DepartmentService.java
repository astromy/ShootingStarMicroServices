package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.DepartmentDetails;
import com.astromyllc.shootingstar.setup.dto.request.DepartmentRequest;
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

import java.util.List;
import java.util.Optional;
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
    public void createDepartments(DepartmentRequest departmentRequest) {
        Optional<Institution> inst=institutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equals(departmentRequest.getInstitution())).findFirst();
        List<DepartmentDetails> sl=departmentRequest.getDepartmentDetailsList().stream().filter(ci->ci.getIdDepartment()==null).collect(Collectors.toList());
        if(sl.isEmpty()){
            List<Department> dl1= (List<Department>) departmentRequest.getDepartmentDetailsList().stream().map(c -> departmentUtil.mapDepartmentRequest_ToDepartment(c, (Department) departmentUtil.departmentGlobalList.stream().filter(x->x.getIdDepartment().equals(c.getIdDepartment()))));
            departmentRepository.saveAll(dl1);
        }else {
            inst.get().setDepartmentList((List<Department>) sl.stream().map(c -> departmentUtil.mapDepartmentRequest_ToDepartment(c)));
            institutionRepository.save(inst.get());
        }

    }

    @Override
    public List<Optional<DepartmentResponse>> getDepartmentByInstitution(String beceCode) {
        return institutionUtils.institutionGlobalList.stream().filter(i->i.getBececode().equals(beceCode)).findFirst().get().getDepartmentList().stream().map(x->departmentUtil.mapDepartment_ToDepartmentResponse(x)).collect(Collectors.toList());
    }
}
