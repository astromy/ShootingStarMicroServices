package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.Designation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepartmentRequest {
    private Long idDepartment;
    private String name;
    private List<DesignationRequest> designationList;
}