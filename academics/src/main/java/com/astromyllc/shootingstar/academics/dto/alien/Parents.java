package com.astromyllc.shootingstar.academics.dto.alien;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Parents {
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
    private String parentType;
    @NonNull
    private String studentId;
    @NonNull
    private String institutionCode;
}
