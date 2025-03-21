package com.astromyllc.astroorb.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DependantsRequest {
    private String id;
    private String name;
    private String dateOfBirth;
    private String relationType;
    private String gender;
    private String birthCertificate;
    private String dependantPicture;
    private String institutionCode;

}
