package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.JobDescriptionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;
import com.astromyllc.shootingstar.setup.dto.response.JobDescriptionResponse;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.JobDescriptionServiceInterface;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import com.astromyllc.shootingstar.setup.utils.JobDescriptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobDescriptionService implements JobDescriptionServiceInterface {
    private final InstitutionRepository institutionRepository;
    private final InstitutionUtils institutionUtils;
    private final JobDescriptionUtil jobDescriptionUtil;


    @Override
    public Optional<JobDescriptionResponse> createJobDescriptions(JobDescriptionRequest jobDescriptionRequest) {
        return Optional.empty();
    }

    @Override
    public List<List<List<Optional<JobDescriptionResponse>>>> getAllJobDescriptionsByInstitution(SingleStringRequest beceCode) {
        String finalBeceCode= beceCode.getVal();
        return institutionUtils.institutionGlobalList.stream()
                .filter(i->i.getBececode().equalsIgnoreCase(finalBeceCode)).findFirst().get()
                .getDepartmentList().stream().map(dep->dep.getDesignationList().stream().map(des->des.getJobDescriptionList().stream().map(jb->jobDescriptionUtil.mapJobDescription_ToJobDescriptionResponse(jb)).collect(Collectors.toList())).collect(Collectors.toList())).collect(Collectors.toList());
    }
}
