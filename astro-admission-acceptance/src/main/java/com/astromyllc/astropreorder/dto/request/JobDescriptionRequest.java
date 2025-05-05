package com.astromyllc.astropreorder.dto.request;

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
