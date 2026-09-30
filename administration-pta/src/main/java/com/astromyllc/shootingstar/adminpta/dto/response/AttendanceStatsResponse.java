package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AttendanceStatsResponse {
    private long totalRecords;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    // (present + late) / total * 100, one decimal place. Late students were
    // in school, so they count as attending. 0 when there are no records.
    private double attendanceRate;
    private String dateFrom;
    private String dateTo;
}
