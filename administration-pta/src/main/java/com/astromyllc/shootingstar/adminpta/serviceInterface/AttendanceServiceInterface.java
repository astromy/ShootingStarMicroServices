package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.config.StudentNotEligibleException;
import com.astromyllc.shootingstar.adminpta.dto.request.AttendanceStatsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.MarkAttendanceRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.AttendanceResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.AttendanceStatsResponse;

public interface AttendanceServiceInterface {
    // Creates or updates the student's record for the given date (today if
    // omitted). Throws StudentNotEligibleException if the student doesn't
    // exist or isn't in the given institution, and IllegalArgumentException
    // for a bad status or date. Both messages are safe to show the user.
    AttendanceResponse markAttendance(MarkAttendanceRequest request) throws StudentNotEligibleException;

    AttendanceStatsResponse getAttendanceStats(AttendanceStatsRequest request);
}
