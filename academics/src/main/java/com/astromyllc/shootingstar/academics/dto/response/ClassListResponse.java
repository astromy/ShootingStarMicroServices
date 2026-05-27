package com.astromyllc.shootingstar.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClassListResponse {
    private String StudentID;
    private String Name;
    private String StudentClass;
    private String Subject;
    private String Score;
    private String TotalScore;
}
