package com.astromyllc.astroorb.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AcademicRecordsRequest {
    private String id;
    private String nameOfInstitution;
    private String dateOfAdmission;
    private String dateOfGraduation;
    private String programOffered;
    private String certificateType;
    private String supportingDocs;
    private String institutionCode;

}
