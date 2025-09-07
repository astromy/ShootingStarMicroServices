package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentAccountRequest {
    @NonNull
    private String studentId;
    private String activationState;
}
