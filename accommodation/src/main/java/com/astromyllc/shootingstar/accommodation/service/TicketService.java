package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.TicketRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.UpdateTicketStatusRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.TicketResponse;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.BlockRoom;
import com.astromyllc.shootingstar.accommodation.model.Ticket;
import com.astromyllc.shootingstar.accommodation.repository.BlockRepository;
import com.astromyllc.shootingstar.accommodation.repository.BlockRoomRepository;
import com.astromyllc.shootingstar.accommodation.repository.TicketRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.TicketServiceInterface;
import com.astromyllc.shootingstar.accommodation.util.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService implements TicketServiceInterface {

    private final TicketRepository ticketRepository;
    private final BlockRepository blockRepository;
    private final BlockRoomRepository blockRoomRepository;

    @Override
    public TicketResponse raiseTicket(TicketRequest request) {
        if (request.getIdBlock() == null) {
            throw new IllegalArgumentException("idBlock is required");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (!blockRepository.existsById(request.getIdBlock())) {
            throw new IllegalArgumentException("No block found with id " + request.getIdBlock());
        }
        Ticket saved = ticketRepository.save(AccommodationMapper.mapRequestToTicket(request));
        return enrich(saved);
    }

    @Override
    public TicketResponse updateStatus(UpdateTicketStatusRequest request) {
        if (request.getIdTicket() == null || request.getStatus() == null) {
            throw new IllegalArgumentException("idTicket and status are required");
        }
        Ticket existing = ticketRepository.findById(request.getIdTicket())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No ticket found with id " + request.getIdTicket()));

        existing.setStatus(request.getStatus());
        if (request.getResolutionNotes() != null) {
            existing.setResolutionNotes(request.getResolutionNotes());
        }
        if ("RESOLVED".equals(request.getStatus())) {
            existing.setDateResolved(LocalDate.now());
        }

        Ticket saved = ticketRepository.save(existing);
        return enrich(saved);
    }

    @Override
    public List<TicketResponse> getBlockTickets(Long idBlock) {
        if (idBlock == null) return new ArrayList<>();
        List<Ticket> tickets = ticketRepository.findByIdBlock(idBlock);
        List<TicketResponse> responses = new ArrayList<>();
        for (Ticket t : tickets) {
            responses.add(enrich(t));
        }
        return responses;
    }

    @Override
    public List<TicketResponse> getInstitutionTickets(String institutionCode) {
        if (institutionCode == null || institutionCode.isBlank()) return new ArrayList<>();
        List<Ticket> tickets = ticketRepository.findByInstitutionCode(institutionCode);
        List<TicketResponse> responses = new ArrayList<>();
        for (Ticket t : tickets) {
            responses.add(enrich(t));
        }
        return responses;
    }

    private TicketResponse enrich(Ticket ticket) {
        String blockName = blockRepository.findById(ticket.getIdBlock())
                .map(Block::getName).orElse(null);
        String roomName = ticket.getIdBlockRoom() == null ? null
                : blockRoomRepository.findById(ticket.getIdBlockRoom())
                .map(BlockRoom::getName).orElse(null);
        return AccommodationMapper.mapTicketToResponse(ticket, blockName, roomName);
    }
}
