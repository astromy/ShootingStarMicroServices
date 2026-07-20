package com.astromyllc.astroadmissions.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Maps the response from the admission microservice:
 * POST /api/applications/getApplicationByCode
 * <p>
 * Field names match exactly what the admission service returns
 * (as observed in the debugger / network tab).
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationsResponse {

    private String idapplication;

    private String applicantFirstName;
    private String applicantOtherName;
    private String applicantLastName;

    private LocalDate applicantDateOfBirth;
    private LocalDate admissionDate;
    private String applicantPlaceOfBirth;
    private String applicantGender;
    private String applicantCountryOfBirth;
    private String applicantNationality;

    /**
     * Filename (e.g. "26-00147-1.png") — not a base64 blob
     */
    private String applicantPicture;
    private String applicantBirthCert;
    private String applicantDenomination;

    private String applicationCode;
    private String applicationStatus;

    /**
     * bece-code / institution code
     */
    private String applicationInstitution;
    private String applicationInstitutionName;

    /**
     * Maps to studentClass — e.g. "K.G", "JHS 1", etc.
     */
    private String applicationType;

    private LocalDate applicationDate;
    private LocalDateTime appointmentDate;

    private String nameOfPreviousSchool;
    private String classOfDeparture;
    private String reasonForDeparture;
    private String addressOfPreviousSchool;
    private String contactOfPreviousSchool;

    /**
     * Parent records — same structure as ParentsResponse
     */
    private List<ParentsResponse> studentParents;

    // -------------------------------------------------------------------------
    // Mapping helper — converts this admission response into the StudentsResponse
    // shape that the rest of the application (and the front-end) already knows.
    // -------------------------------------------------------------------------
    public StudentsResponse toStudentsResponse() {
        return StudentsResponse.builder()
                // Use applicationCode as the studentId on the acceptance side
                .studentId(this.applicationCode)
                .id(this.idapplication)

                .firstName(this.applicantFirstName)
                .otherName(this.applicantOtherName)
                .lastName(this.applicantLastName)

                .dateOfBirth(this.applicantDateOfBirth)
                // dateOfAdmission not present in admission response; leave null
                .dateOfAdmission(this.admissionDate)

                .placeOfBirth(this.applicantPlaceOfBirth)
                .gender(this.applicantGender)
                .countryOfBirth(this.applicantCountryOfBirth)
                .nationality(this.applicantNationality)

                .denomination(this.applicantDenomination)
                .birthCert(this.applicantBirthCert)

                // Picture is a filename in the admission service, not base64.
                // Keep it so the front-end can at least store / forward it.
                .picture(this.applicantPicture)

                .institutionCode(this.applicationInstitution)
                .status(this.applicationStatus)

                // applicationType (e.g. "K.G") used as studentClass
                .studentClass(this.applicationType)

                // residentialLocality not in admission response; leave null
                .residentialLocality(null)

                .studentParents(this.studentParents)

                // No subjects or account data at application stage
                .studentSubjectsResponse(null)
                .studentAccountResponse(null)

                .build();
    }
}