package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DependantsResponse {
    private String id;
    private String name;
    private LocalDate dateOfBirth;
    private String relationType;
    private String gender;
    private String birthCertificate;
    private String dependantPicture;
}
