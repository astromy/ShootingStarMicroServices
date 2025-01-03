package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GradingSettingRequest {
    @NonNull
    private String institution;
    private GradingSettingDetails gradingSettingDetails;
}
