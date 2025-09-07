package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InstitutionAccountRequest {
    @NonNull
    private String institutionCode;
    private String activationState;
}
