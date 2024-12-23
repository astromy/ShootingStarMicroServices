package com.astromyllc.shootingstar.hr.dto.response;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class StaffPermissionsResponse {
    private String id;
    private String staffCode;
    private String permissionCode;
    private String permission;
    private String institutionCode;
}
