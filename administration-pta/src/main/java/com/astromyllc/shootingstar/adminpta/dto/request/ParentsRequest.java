package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
