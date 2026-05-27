package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.DesignationRequestDetails;
import com.astromyllc.shootingstar.setup.dto.response.DesignationResponse;
import com.astromyllc.shootingstar.setup.model.Designation;
import com.astromyllc.shootingstar.setup.repository.DesignationRepository;
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
public class DesignationUtil {

    private final DesignationRepository designationRepository;
    public static List<Designation> designationGlobalList=null;

    @PostConstruct
    private void fetAllDesignation(){
        designationGlobalList=designationRepository.findAll();
        log.info("Global Designation List populated with {} records", (long) designationGlobalList.size());
    }

    public static Designation mapDesignationRequest_ToDesignation(DesignationRequestDetails des) {
        return Designation.builder()
                .code(des.getCode())
                .name(des.getName())
                .availableSlots(des.getAvailableSlots())
                .totalSlots(des.getTotalSlots())
                .jobDescriptionList(des.getJobDescriptionList().stream().map(JobDescriptionUtil::mapJobDescriptionRequest_ToJobDescription).toList())
                .build();
    }

    public static Designation mapDesignationRequest_ToDesignation(DesignationRequestDetails des,Designation d) {
                d.setCode(des.getCode());
                d.setName(des.getName());
                d.setTotalSlots(des.getTotalSlots());
                d.setAvailableSlots(des.getAvailableSlots());
                d.setJobDescriptionList(des.getJobDescriptionList().stream()
                        .map(JobDescriptionUtil::mapJobDescriptionRequest_ToJobDescription) // Map all to new ones
                        .toList());
               return d;
    }

    public static Optional<DesignationResponse> mapDesignation_ToDesignationResponse(Designation des) {
        return Optional.of(DesignationResponse.builder()
                .idDesignation(des.getIdDesignation())
                .code(des.getCode())
                .name(des.getName())
                .availableSlots(des.getAvailableSlots())
                .totalSlots(des.getTotalSlots())
                .jobDescriptionList(des.getJobDescriptionList().stream().map(JobDescriptionUtil::mapJobDescription_ToJobDescriptionResponse).toList())
                .build());
    }


}
