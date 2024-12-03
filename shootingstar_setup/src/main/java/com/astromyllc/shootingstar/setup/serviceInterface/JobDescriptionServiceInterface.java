package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.JobDescriptionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.JobDescriptionResponse;

import java.util.List;
import java.util.Optional;

public interface JobDescriptionServiceInterface {
    Optional<JobDescriptionResponse> createJobDescriptions(JobDescriptionRequest jobDescriptionRequest);

    List<List<List<Optional<JobDescriptionResponse>>>> getAllJobDescriptionsByInstitution(SingleStringRequest beceCode);
}
