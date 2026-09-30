package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AttendanceResponse {
    private String studentId;
    private String institutionCode;
    private String studentClass;
    private String date;
    private String status;
}
