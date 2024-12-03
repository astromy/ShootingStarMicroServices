package com.astromyllc.shootingstar.setup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GradingResponse {
    private Long id;
    private Double lowerLimit;
    private int grade;
    private String comment;
}
