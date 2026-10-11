package com.astromyllc.shootingstar.adminpta.dto.response;

import java.util.List;

/**
 * Everything the dashboards show about students, worked out on the server in one
 * pass over the in-memory student list. A few hundred bytes instead of every record.
 */
public record StudentSummaryResponse(
        long total,
        long active,
        long suspended,
        long newAdmissionsThisYear,
        int year,
        List<LabelCount> byClass,
        List<LabelCount> byGender,
        List<LabelCount> byStatus,
        List<LabelCount> topLocalities,
        List<LabelCount> admissionsByMonth,
        List<LabelCount> admissionsByYear) {
}
