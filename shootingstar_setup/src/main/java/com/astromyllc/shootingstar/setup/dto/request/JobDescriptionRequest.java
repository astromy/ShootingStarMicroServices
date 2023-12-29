package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.JobDescription;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JobDescriptionRequest {

    @NonNull
    private String institutionBECECOde;
    @NonNull
    private String departmentId;
    @NonNull
    private String designationId;
    private List<JobDescriptionRequestDetails> jobDescriptionRequestDetails;
}
