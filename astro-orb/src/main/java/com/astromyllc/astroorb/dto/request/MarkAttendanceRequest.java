package com.astromyllc.astroorb.dto.request;

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
    private String date;       // "YYYY-MM-DD"
    private String recordedBy; // staffCode of the marker (optional)
}
