package com.astromyllc.astroadmissions.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepartmentResponse {
    private Long idDepartment;
    private String name;
    private List<Optional<DesignationResponse>> designationList;
}
