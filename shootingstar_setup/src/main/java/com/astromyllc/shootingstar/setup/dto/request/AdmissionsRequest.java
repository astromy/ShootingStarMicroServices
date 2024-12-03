package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.AdmissionCriteria;
import com.astromyllc.shootingstar.setup.model.ApplicationCategory;
import jakarta.persistence.OneToMany;
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
