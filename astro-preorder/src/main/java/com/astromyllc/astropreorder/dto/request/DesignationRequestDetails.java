package com.astromyllc.astropreorder.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationRequestDetails {
    private Long idDesignation;
    @NonNull
    private String name;
    @NonNull
    private String code;
    private List<JobDescriptionRequestDetails> jobDescriptionList;
}
