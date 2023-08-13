package com.astromyllc.shootingstar.onlineapplication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(value="appointment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Appointment {
    @Id
    private String id;
    private String applicationCode;
    private String institutionId;
    private LocalDateTime appointmentDateTime;
}
