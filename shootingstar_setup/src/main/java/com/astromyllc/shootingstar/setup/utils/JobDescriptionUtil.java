package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.JobDescriptionRequest;
import com.astromyllc.shootingstar.setup.dto.response.JobDescriptionResponse;
import com.astromyllc.shootingstar.setup.model.JobDescription;
import com.astromyllc.shootingstar.setup.repository.JobDescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDescriptionUtil {

    private final JobDescriptionRepository jobDescriptionRepository;
    public static List<JobDescription> jobDescriptionGlobalList=null;

    @Bean
    private void fetAllJobDescription(){
        jobDescriptionGlobalList=jobDescriptionRepository.findAll();
        log.info("Global JobDescription List populated with {} records",jobDescriptionGlobalList.stream().count());
    }
    public static JobDescription mapJobDescriptionRequest_ToJobDescription(JobDescriptionRequest jb) {
        return JobDescription.builder()
                .jobDescription(jb.getJobDescription())
                .build();
    }
    public static JobDescription mapJobDescriptionRequest_ToJobDescription(JobDescriptionRequest jb,JobDescription j) {
                j.setJobDescription(jb.getJobDescription());
                return j;
    }
    public static JobDescriptionResponse mapJobDescription_ToJobDescriptionResponse(JobDescription jb) {
        return JobDescriptionResponse.builder()
                .idJobDescription(jb.getIdJobDescription())
                .jobDescription(jb.getJobDescription())
                .build();
    }
}
