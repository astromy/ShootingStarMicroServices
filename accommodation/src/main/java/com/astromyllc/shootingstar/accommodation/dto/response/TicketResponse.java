package com.astromyllc.shootingstar.accommodation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TicketResponse {
    private Long idTicket;
    private Long idBlock;
    private String blockName;
    private Long idBlockRoom;
    private String roomName;
    private String title;
    private String description;
    private String raisedByStaffId;
    private String raisedByStaffName;
    private String status;
    private String priority;
    private LocalDate dateRaised;
    private LocalDate dateResolved;
    private String resolutionNotes;
}
