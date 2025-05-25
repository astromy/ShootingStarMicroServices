package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.JobDescriptionRequest;
import com.astromyllc.shootingstar.setup.dto.request.JobDescriptionRequestDetails;
import com.astromyllc.shootingstar.setup.dto.response.JobDescriptionResponse;
import com.astromyllc.shootingstar.setup.model.JobDescription;
import com.astromyllc.shootingstar.setup.repository.JobDescriptionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDescriptionUtil {

    private final JobDescriptionRepository jobDescriptionRepository;
    public static List<JobDescription> jobDescriptionGlobalList=new ArrayList<>();

    @PostConstruct
    private void fetAllJobDescription(){
        jobDescriptionGlobalList=jobDescriptionRepository.findAll();
        log.info("Global JobDescription List populated with {} records",jobDescriptionGlobalList.stream().count());
    }
    public static JobDescription mapJobDescriptionRequest_ToJobDescription(JobDescriptionRequestDetails jb) {
        return JobDescription.builder()
                .jobDescription(jb.getJobDescription())
                .build();
    }
    public static JobDescription mapJobDescriptionRequest_ToJobDescription(JobDescriptionRequestDetails jb,JobDescription j) {
                j.setJobDescription(jb.getJobDescription());
                return j;
    }
    public static Optional<JobDescriptionResponse> mapJobDescription_ToJobDescriptionResponse(JobDescription jb) {
        return Optional.of(JobDescriptionResponse.builder()
                .idJobDescription(jb.getIdJobDescription())
                .jobDescription(jb.getJobDescription())
                .build());
    }
}
