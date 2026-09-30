package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateDutyRotationRequest {
    private String institutionCode;
    private Long idBlock;
    private Long idDutyType;
    private String studentId;
    private String studentName;
    private LocalDate startWeek;
    private Integer numberOfWeeks;
}
