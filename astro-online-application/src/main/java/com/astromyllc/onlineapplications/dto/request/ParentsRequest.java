package com.astromyllc.onlineapplications.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParentsRequest {

    private String id;
    private String firstNames;
    private String lastName;
    private String email;
    private String contact1;
    private String contact2;
    private String occupation;
    private String placeOfWork;
    private String parentType;
    private String studentId;
    private String institutionCode;
}
