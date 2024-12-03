package com.astromyllc.shootingstar.academics.dto.alien;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Data
public class GradingRequest {
    private Long idGrading;
    private Double lowerLimit;
    private String grade;
    private String comment;
}
