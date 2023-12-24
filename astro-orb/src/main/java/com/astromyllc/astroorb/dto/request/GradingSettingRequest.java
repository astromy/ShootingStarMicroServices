package com.astromyllc.astroorb.dto.request;

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
    private Double classPercentage;
    private Double examsPercentage;
    private Double trailingMark;
    private int allowedTrails;
    private List<GradingRequest> gradingList;
}
