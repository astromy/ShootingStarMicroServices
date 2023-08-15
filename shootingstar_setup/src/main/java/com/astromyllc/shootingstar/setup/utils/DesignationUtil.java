package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.DesignationRequest;
import com.astromyllc.shootingstar.setup.dto.response.DesignationResponse;
import com.astromyllc.shootingstar.setup.model.Designation;
import com.astromyllc.shootingstar.setup.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DesignationUtil {

    private final DesignationRepository designationRepository;
    public static List<Designation> designationGlobalList=null;

    @Bean
    private void fetAllDesignation(){
        designationGlobalList=designationRepository.findAll();
        log.info("Global Designation List populated with {} records",designationGlobalList.stream().count());
    }

    public static Designation mapDesignationRequest_ToDesignation(DesignationRequest des) {
        return Designation.builder()
                .code(des.getCode())
                .name(des.getName())
                .jobDescriptionList(des.getJobDescriptionList().stream().map(jb-> JobDescriptionUtil.mapJobDescriptionRequest_ToJobDescription(jb)).toList())
                .build();
    }

    public static Designation mapDesignationRequest_ToDesignation(DesignationRequest des,Designation d) {
                d.setCode(des.getCode());
                d.setName(des.getName());
                d.setJobDescriptionList(des.getJobDescriptionList().stream().map(jb-> JobDescriptionUtil.mapJobDescriptionRequest_ToJobDescription(jb,d.getJobDescriptionList().stream().filter(jb1->jb.getIdJobDescription().equals(jb1.getIdJobDescription())).findFirst().get())).collect(Collectors.toList()));
               return d;
    }

    public static DesignationResponse mapDesignation_ToDesignationResponse(Designation des) {
        return DesignationResponse.builder()
                .idDesignation(des.getIdDesignation())
                .code(des.getCode())
                .name(des.getName())
                .jobDescriptionList(des.getJobDescriptionList().stream().map(jb-> JobDescriptionUtil.mapJobDescription_ToJobDescriptionResponse(jb)).toList())
                .build();
    }


}
