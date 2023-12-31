package com.astromyllc.shootingstar.onlineapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GradingRequest {
    private Long id;
    private Double lowerLimit;
    private int grade;
    private String comment;
}
