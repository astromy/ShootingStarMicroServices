package com.astromyllc.shootingstar.hr.dto.response;

import com.astromyllc.shootingstar.hr.dto.request.DesignationUnitRequest;
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
public class DesignationListResponse {
    private ObjectId id;
    private String department;
    private String designation;
    private String institutionCode;

    private List<DesignationUnitResponse> designationUnits;
}
