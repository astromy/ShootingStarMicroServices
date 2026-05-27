package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClassListResponse {
    private String id;
    private String name;
    private String studentClass;
    private String subject;
    private String score;
    private String totalScore;
}
