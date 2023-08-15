package com.astromyllc.shootingstar.setup.dto.response;

import com.astromyllc.shootingstar.setup.dto.request.DesignationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepartmentResponse {
    private Long idDepartment;
    private String name;
    private List<DesignationResponse> designationList;
}
