package com.astromyllc.shootingstar.academics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicReportRequest {
    private String institutionCode;
    private String academicYear;
    private String targetClass;
    private String classGroup;
    private String term;
    private Long gradingSetting;
}
