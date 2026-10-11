package com.astromyllc.shootingstar.hr.dto.response;

import java.time.LocalDate;
import java.util.List;

/**
 * One line of the staff table: the columns on screen and nothing else.
 * No picture, documents, dependants or records - those come with the record of one person.
 */
public record StaffRowResponse(
        String staffCode,
        String firstNames,
        String lastName,
        String gender,
        String contact1,
        String staffEmail,
        LocalDate dateOfEmployment,
        String designation,
        String nationality,
        List<SubjectLine> subjects,
        boolean hasPicture) {

    /** A subject a staff member teaches, and the class. */
    public record SubjectLine(String subject, String subjectClass) {
    }
}
