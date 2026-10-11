package com.astromyllc.shootingstar.adminpta.dto.response;

import java.time.LocalDate;

/**
 * One line of the student tables: the columns on screen and nothing else.
 * No picture, birth certificate, parents or subjects - those are fetched for one
 * student at a time when somebody opens the record.
 */
public record StudentRowResponse(
        String studentId,
        String firstName,
        String otherName,
        String lastName,
        String gender,
        String studentClass,
        LocalDate dateOfBirth,
        LocalDate dateOfAdmission,
        String placeOfBirth,
        String countryOfBirth,
        String nationality,
        String denomination,
        String status,
        boolean hasPicture) {
}
