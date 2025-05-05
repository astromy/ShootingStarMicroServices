package com.astromyllc.shootingstar.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationUnitResponse {
    private ObjectId id;
    private String unitName;
    private String designation;
    private String institutionCode;

}
