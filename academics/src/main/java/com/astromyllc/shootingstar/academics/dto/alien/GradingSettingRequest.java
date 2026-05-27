package com.astromyllc.shootingstar.academics.dto.alien;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GradingSettingRequest {
    private Long id;
    private Double ClassPercentage;
    private Double ExamsPercentage;
    private Double TrailingMark;
    private int AllowedTrails;
    private List<GradingRequest> gradingList;
}
