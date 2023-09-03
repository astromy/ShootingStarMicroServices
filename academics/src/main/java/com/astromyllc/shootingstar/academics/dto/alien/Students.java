package com.astromyllc.shootingstar.academics.dto.alien;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Students {
    private String studentId;
    private String firstName;
    private String otherName;
    private String lastName;
    private String gender;
    private String institutionCode;
    private String status;
    private String studentClass;
}
