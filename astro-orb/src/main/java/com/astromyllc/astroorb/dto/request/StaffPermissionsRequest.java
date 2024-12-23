package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffPermissionsRequest {
    private String id;
    private String staffCode;
    private String permissionCode;
    private String permission;
    private String institutionCode;
    private String state;

}
