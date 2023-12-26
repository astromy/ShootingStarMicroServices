package com.astromyllc.shootingstar.setup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepartmentDetails {
    private Long idDepartment;
    private String name;
    private List<DesignationRequest> designationList;
}
