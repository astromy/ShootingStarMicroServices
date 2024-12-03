package com.astromyllc.astroorb.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GradingSettingDetails {
    private Long id;
    @NonNull
    private Double classPercentage;
    @NonNull
    private Double examsPercentage;
    @NonNull
    private Double trailingMark;
    private int allowedTrails;
    private List<GradingRequest> gradingList;
}
