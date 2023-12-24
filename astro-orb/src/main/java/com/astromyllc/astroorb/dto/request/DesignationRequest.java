package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationRequest {
    private Long idDesignation;
    private String name;
    private String code;
    private List<JobDescriptionRequest> jobDescriptionList;
}
