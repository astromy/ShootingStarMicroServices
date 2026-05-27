package com.astromyllc.shootingstar.hr.dto.response;

import com.astromyllc.shootingstar.hr.dto.request.DesignationListRequest;
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
public class StaffDesignationResponse {
    private ObjectId id;
    private String institutionCode;
    private List<DesignationListResponse> staffDesignationList;
}
