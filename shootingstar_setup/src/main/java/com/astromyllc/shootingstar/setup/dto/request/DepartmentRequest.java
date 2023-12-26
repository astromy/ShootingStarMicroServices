package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.Department;
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
    private String institution;
    private List<DepartmentDetails> departmentDetailsList;

}
