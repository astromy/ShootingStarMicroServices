package com.astromyllc.shootingstar.hr.dto.response;

import java.util.List;

/** What the HR, finance and executive dashboards show about staff, counted on the server. */
public record StaffSummaryResponse(
        long total,
        long designationCount,
        List<LabelCount> byGender,
        List<LabelCount> byDesignation,
        List<LabelCount> byDepartment) {
}
