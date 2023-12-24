package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApplicationCategoryResponse {
    private Long id;
    private BigDecimal applicationFormAmount;
    private String applicationFormType;
    private int applicationFormQNT;
    private String paymentMedium;
    private LocalDateTime commencement;
    private LocalDateTime closure;
    private int appointmentPerDay;
    private LocalDateTime appointmentCommencement;
    private LocalDateTime appointmentClosure;
}
