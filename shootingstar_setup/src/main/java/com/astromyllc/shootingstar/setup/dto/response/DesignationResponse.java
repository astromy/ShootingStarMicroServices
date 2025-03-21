package com.astromyllc.shootingstar.setup.dto.response;

import com.astromyllc.shootingstar.setup.dto.request.JobDescriptionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationResponse {
    private Long idDesignation;
    private String name;
    private String code;
    private int totalSlots;
    private int availableSlots;
    private List<Optional<JobDescriptionResponse>> jobDescriptionList;
}
