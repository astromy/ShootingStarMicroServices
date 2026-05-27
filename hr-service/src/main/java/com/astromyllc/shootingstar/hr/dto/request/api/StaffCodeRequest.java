package com.astromyllc.shootingstar.hr.dto.request.api;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class StaffCodeRequest {
    private String institutionCode;
    private String staffCode;
}
