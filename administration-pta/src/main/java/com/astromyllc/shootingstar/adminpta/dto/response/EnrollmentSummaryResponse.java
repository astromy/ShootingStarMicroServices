package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EnrollmentSummaryResponse {
    private long totalStudents;
    private List<ClassCount> byClass;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ClassCount {
        private String studentClass;
        private long count;
    }
}
