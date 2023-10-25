package com.astromyllc.shootingstar.onlineapplication.dto.response.alien;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParentsResponse {

    private String id;
    @NonNull
    private String firstNames;
    @NonNull
    private String lastName;
    @NonNull
    private String email;
    @NonNull
    private String contact1;
    private String contact2;
    @NonNull
    private String occupation;
    @NonNull
    private String placeOfWork;
    @NonNull
    private String parentType;
    private String studentId;
    @NonNull
    private String institutionCode;
}
