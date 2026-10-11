package com.astromyllc.shootingstar.accommodation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DutyAssignmentResponse {
    private Long idDutyAssignment;
    private Long idBlock;
    private String blockName;
    private Long idDutyType;
    private String dutyTypeName;
    private String studentId;
    private String studentName;
    private LocalDate weekStartDate;
    private String status;
    private String rotationId;
}
