package com.astromyllc.shootingstar.setup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GradingSettingDetails {
    private Long id;
    private Double classPercentage;
    private Double examsPercentage;
    private Double trailingMark;
    private int allowedTrails;
    private List<GradingRequest> gradingList;
}
