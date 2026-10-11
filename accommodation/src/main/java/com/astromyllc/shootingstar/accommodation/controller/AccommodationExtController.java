package com.astromyllc.shootingstar.accommodation.controller;

import com.astromyllc.shootingstar.accommodation.dto.request.AmenityRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.ConsumableRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.CreateDutyRotationRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.DutyTypeRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleIdRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.TicketRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.UpdateDutyStatusRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.UpdateTicketStatusRequest;
import com.astromyllc.shootingstar.accommodation.serviceInterface.AmenityServiceInterface;
import com.astromyllc.shootingstar.accommodation.serviceInterface.ConsumableServiceInterface;
import com.astromyllc.shootingstar.accommodation.serviceInterface.DutyAssignmentServiceInterface;
import com.astromyllc.shootingstar.accommodation.serviceInterface.DutyTypeServiceInterface;
import com.astromyllc.shootingstar.accommodation.serviceInterface.TicketServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Separate from AccommodationController on purpose - this covers the three
// domains added after the original module (Duty Roster, Resource Tracking,
// Faults & Complaints), so it can be dropped in without editing the
// existing controller file at all.
@RestController
@RequiredArgsConstructor
@Slf4j
public class AccommodationExtController {

    private final DutyTypeServiceInterface dutyTypeServiceInterface;
    private final DutyAssignmentServiceInterface dutyAssignmentServiceInterface;
    private final AmenityServiceInterface amenityServiceInterface;
    private final ConsumableServiceInterface consumableServiceInterface;
    private final TicketServiceInterface ticketServiceInterface;

    // ==================== Duty Types (catalog) ====================

    @PostMapping("/api/accommodation/addDutyType")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addDutyType(@RequestBody DutyTypeRequest request) {
        return ResponseEntity.ok(dutyTypeServiceInterface.addDutyType(request));
    }

    @PostMapping("/api/accommodation/getDutyTypes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getDutyTypes(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(dutyTypeServiceInterface.getDutyTypes(request.getVal()));
    }

    @PostMapping("/api/accommodation/deleteDutyType")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteDutyType(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(dutyTypeServiceInterface.deleteDutyType(request.getVal()));
    }

    // ==================== Duty Roster (rotations) ====================

    @PostMapping("/api/accommodation/createDutyRotation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> createDutyRotation(@RequestBody CreateDutyRotationRequest request) {
        return ResponseEntity.ok(dutyAssignmentServiceInterface.createRotation(request));
    }

    @PostMapping("/api/accommodation/updateDutyStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateDutyStatus(@RequestBody UpdateDutyStatusRequest request) {
        return ResponseEntity.ok(dutyAssignmentServiceInterface.updateStatus(request));
    }

    @PostMapping("/api/accommodation/getBlockDutyRoster")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBlockDutyRoster(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(dutyAssignmentServiceInterface.getBlockRoster(request.getVal()));
    }

    @PostMapping("/api/accommodation/cancelDutyRotation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> cancelDutyRotation(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(dutyAssignmentServiceInterface.cancelRotation(request.getVal()));
    }

    @PostMapping("/api/accommodation/deleteDutyAssignment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteDutyAssignment(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(dutyAssignmentServiceInterface.deleteAssignment(request.getVal()));
    }

    // ==================== Amenities ====================

    @PostMapping("/api/accommodation/addAmenity")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addAmenity(@RequestBody AmenityRequest request) {
        return ResponseEntity.ok(amenityServiceInterface.addAmenity(request));
    }

    @PostMapping("/api/accommodation/updateAmenity")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateAmenity(@RequestBody AmenityRequest request) {
        return ResponseEntity.ok(amenityServiceInterface.updateAmenity(request));
    }

    @PostMapping("/api/accommodation/getBlockAmenities")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBlockAmenities(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(amenityServiceInterface.getBlockAmenities(request.getVal()));
    }

    @PostMapping("/api/accommodation/deleteAmenity")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteAmenity(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(amenityServiceInterface.deleteAmenity(request.getVal()));
    }

    // ==================== Consumables ====================

    @PostMapping("/api/accommodation/addConsumable")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addConsumable(@RequestBody ConsumableRequest request) {
        return ResponseEntity.ok(consumableServiceInterface.addConsumable(request));
    }

    @PostMapping("/api/accommodation/updateConsumable")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateConsumable(@RequestBody ConsumableRequest request) {
        return ResponseEntity.ok(consumableServiceInterface.updateConsumable(request));
    }

    @PostMapping("/api/accommodation/getBlockConsumables")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBlockConsumables(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(consumableServiceInterface.getBlockConsumables(request.getVal()));
    }

    @PostMapping("/api/accommodation/deleteConsumable")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteConsumable(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(consumableServiceInterface.deleteConsumable(request.getVal()));
    }

    // ==================== Faults & Complaints (tickets) ====================

    @PostMapping("/api/accommodation/raiseTicket")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> raiseTicket(@RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketServiceInterface.raiseTicket(request));
    }

    @PostMapping("/api/accommodation/updateTicketStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateTicketStatus(@RequestBody UpdateTicketStatusRequest request) {
        return ResponseEntity.ok(ticketServiceInterface.updateStatus(request));
    }

    @PostMapping("/api/accommodation/getBlockTickets")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBlockTickets(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(ticketServiceInterface.getBlockTickets(request.getVal()));
    }

    @PostMapping("/api/accommodation/getInstitutionTickets")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInstitutionTickets(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(ticketServiceInterface.getInstitutionTickets(request.getVal()));
    }
}
