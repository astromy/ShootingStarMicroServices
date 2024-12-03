package com.astromyllc.astropreorder.dto.request;

import lombok.*;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepartmentDetails {
    private Long idDepartment;
    @NonNull
    private String name;
    private List<DesignationRequestDetails> designationList;
}
