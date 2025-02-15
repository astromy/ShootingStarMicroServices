package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationRequestDetails {
    private Long idDesignation;
    @NonNull
    private String name;
    @NonNull
    private String code;
    private int totalSlots;
    private int availableSlots;
    private List<JobDescriptionRequestDetails> jobDescriptionList;
}
