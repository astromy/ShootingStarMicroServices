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
public class AdmissionsRequest {
    private Long id;
    private List<AdmissionCriteriaRequest> admissionCriteriaList;
    private List<ApplicationCategoryRequest> applicationCategoryList;
}
