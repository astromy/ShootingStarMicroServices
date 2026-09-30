package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TicketRequest {
    private Long idTicket;
    private String institutionCode;
    private Long idBlock;
    private Long idBlockRoom;
    private String title;
    private String description;
    private String raisedByStaffId;
    private String raisedByStaffName;
    private String priority;
}
