package com.astromyllc.shootingstar.clinic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DischargeVisitRequest {
    private String recordedBy;    // who discharged the patient
    private String notes;          // optional — appended to the visit's notes on discharge
}