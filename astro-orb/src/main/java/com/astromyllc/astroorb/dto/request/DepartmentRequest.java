package com.astromyllc.astroorb.dto.request;

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
