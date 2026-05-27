package com.astromyllc.shootingstar.onlineapplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AppointmentResponse {
    private String id;
    private String applicationCode;
    private String institutionId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
}
