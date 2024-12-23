package com.astromyllc.shootingstar.hr.dto.request;

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
public class StaffPermissionsRequest {
    private String id;
    private String staffCode;
    private String permissionCode;
    private String permission;
    private String institutionCode;
    private String state;

}
