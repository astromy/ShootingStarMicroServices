package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.JobDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JobDescriptionRequest {

    private String institutionBECECOde;
    private String departmentId;
    private String designationId;
    private List<JobDescriptionRequestDetails> jobDescriptionRequestDetails;
}
