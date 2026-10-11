package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.TicketRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.UpdateTicketStatusRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.TicketResponse;

import java.util.List;

public interface TicketServiceInterface {
    TicketResponse raiseTicket(TicketRequest request);

    TicketResponse updateStatus(UpdateTicketStatusRequest request);

    List<TicketResponse> getBlockTickets(Long idBlock);

    List<TicketResponse> getInstitutionTickets(String institutionCode);
}
