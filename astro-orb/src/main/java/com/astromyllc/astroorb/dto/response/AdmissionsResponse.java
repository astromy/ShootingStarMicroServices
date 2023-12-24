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
public class AdmissionsResponse {
    private Long id;
    private List<AdmissionCriteriaResponse> admissionCriteriaList;
    private List<ApplicationCategoryResponse> applicationCategoryList;
}
