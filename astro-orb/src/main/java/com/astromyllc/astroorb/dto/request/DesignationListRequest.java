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
public class DesignationListRequest {
    private String id;
    private String department;
    private String designation;
    private String institutionCode;

    private List<DesignationUnitRequest> designationUnits;
}
