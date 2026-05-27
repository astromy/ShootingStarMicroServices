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
public class BlockMasterRequest {
    private Long idBlockMaster;
    private String staffName;
    private String staffId;
    private LocalDate appointmentDate;
    private LocalDate exitDate;
}
