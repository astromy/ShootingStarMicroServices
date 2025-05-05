package com.astromyllc.shootingstar.hr.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationListRequest {
    private ObjectId id;
    private String department;
    private String designation;
    private String institutionCode;

    private List<DesignationUnitRequest> designationUnits;
}
