package com.astromyllc.shootingstar.setup.dto.request;

import com.astromyllc.shootingstar.setup.model.Department;
import com.astromyllc.shootingstar.setup.model.Designation;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepartmentRequest {
    @NonNull
    private String institution;
    private List<DepartmentDetails> departmentDetailsList;

}
