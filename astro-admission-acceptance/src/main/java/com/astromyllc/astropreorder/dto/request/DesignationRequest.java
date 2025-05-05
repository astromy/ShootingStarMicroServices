package com.astromyllc.astropreorder.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationRequest {
    private String institutionBECECode;
    private String departmentId;
    private List<DesignationRequestDetails> designationRequestDetails;
}
