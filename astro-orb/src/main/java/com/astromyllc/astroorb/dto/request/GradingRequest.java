package com.astromyllc.astroorb.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GradingRequest {
    private Long id;
    @NonNull
    private Double lowerLimit;
    @NonNull
    private int grade;
    private String comment;
}
