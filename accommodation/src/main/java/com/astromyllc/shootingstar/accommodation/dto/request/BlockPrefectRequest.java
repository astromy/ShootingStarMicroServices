package com.astromyllc.shootingstar.accommodation.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockPrefectRequest {
    private Long idBlockPrefect;
    private String studentName;
    private String studentId;
    private LocalDate appointmentDate;
    private LocalDate exitDate;
}
