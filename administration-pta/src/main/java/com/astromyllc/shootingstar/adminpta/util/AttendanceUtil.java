package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.response.AttendanceResponse;
import com.astromyllc.shootingstar.adminpta.model.Attendance;

public class AttendanceUtil {

    public static AttendanceResponse mapAttendance_ToAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .studentId(attendance.getStudentId())
                .institutionCode(attendance.getInstitutionCode())
                .studentClass(attendance.getStudentClass())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .build();
    }
}
