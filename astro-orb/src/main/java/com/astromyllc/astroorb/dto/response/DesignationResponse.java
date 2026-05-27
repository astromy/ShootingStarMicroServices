package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationResponse {
    private Long idDesignation;
    private String name;
    private String code;
    private int totalSlots;
    private int availableSlots;
    private List<JobDescriptionResponse> jobDescriptionList;
}
