package com.astromyllc.shootingstar.onlineapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SettingsRequest {
    private ObjectId id;
    private String applicationFormType;
    private int applicationFormQNT;
    private double paymentAmount;
    private String paymentMedium;
    private long institutionId;
    private LocalDateTime commencement;
    private LocalDateTime closure;
    private int appointmentPerDay;
    private LocalDateTime appointmentCommencement;
    private LocalDateTime appointmentClosure;
}
