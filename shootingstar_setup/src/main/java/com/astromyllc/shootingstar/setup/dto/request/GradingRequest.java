package com.astromyllc.shootingstar.setup.dto.request;

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
    private String grade;
    private String comment;
}
