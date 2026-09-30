package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MarkAttendanceRequest {
    private String studentId;
    private String institutionCode;
    private String status;     // "PRESENT" | "LATE" | "ABSENT"
    private String date;       // "YYYY-MM-DD"; defaults to today (UTC) if omitted
    private String recordedBy; // staffCode of the marker (optional)
}
