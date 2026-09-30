package com.astromyllc.shootingstar.accommodation.util;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockMasterRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.BlockPrefectRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.BlockRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.BlockRoomRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockMasterResponse;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockPrefectResponse;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockResponse;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockRoomResponse;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockRoomStudentsResponse;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.BlockMaster;
import com.astromyllc.shootingstar.accommodation.model.BlockPrefect;
import com.astromyllc.shootingstar.accommodation.model.BlockRoom;
import com.astromyllc.shootingstar.accommodation.model.BlockRoomStudents;
import com.astromyllc.shootingstar.accommodation.dto.request.DutyTypeRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.DutyTypeResponse;
import com.astromyllc.shootingstar.accommodation.dto.response.DutyAssignmentResponse;
import com.astromyllc.shootingstar.accommodation.model.DutyType;
import com.astromyllc.shootingstar.accommodation.model.DutyAssignment;
import com.astromyllc.shootingstar.accommodation.dto.request.AmenityRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.AmenityResponse;
import com.astromyllc.shootingstar.accommodation.model.Amenity;
import com.astromyllc.shootingstar.accommodation.dto.request.ConsumableRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.ConsumableResponse;
import com.astromyllc.shootingstar.accommodation.model.Consumable;
import com.astromyllc.shootingstar.accommodation.dto.request.TicketRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.TicketResponse;
import com.astromyllc.shootingstar.accommodation.model.Ticket;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccommodationMapper {

    // ==================== Block ====================

    public static Block mapRequestToBlock(BlockRequest request) {
        return Block.builder()
                .idBlock(request.getIdBlock())
                .institutionCode(request.getInstitutionCode())
                .name(request.getName())
                .slogan(request.getSlogan())
                .gender(request.getGender())
                .roomsList(request.getRoomsList() == null
                        ? null
                        : request.getRoomsList().stream()
                        .map(AccommodationMapper::mapRequestToRoom)
                        .collect(Collectors.toList()))
                .build();
    }

    public static BlockResponse mapBlockToResponse(Block block) {
        if (block == null) return null;
        return BlockResponse.builder()
                .idBlock(block.getIdBlock())
                .institutionCode(block.getInstitutionCode())
                .name(block.getName())
                .slogan(block.getSlogan())
                .gender(block.getGender())
                .roomsList(block.getRoomsList() == null
                        ? Collections.emptyList()
                        : block.getRoomsList().stream()
                        .map(AccommodationMapper::mapRoomToResponse)
                        .collect(Collectors.toList()))
                .blockMasters(block.getBlockMasters() == null
                        ? Collections.emptyList()
                        : block.getBlockMasters().stream()
                        .map(AccommodationMapper::mapMasterToResponse)
                        .collect(Collectors.toList()))
                .blockPrefects(block.getBlockPrefects() == null
                        ? Collections.emptyList()
                        : block.getBlockPrefects().stream()
                        .map(AccommodationMapper::mapPrefectToResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<BlockResponse> mapBlocksToResponses(List<Block> blocks) {
        if (blocks == null) return Collections.emptyList();
        return blocks.stream().map(AccommodationMapper::mapBlockToResponse).collect(Collectors.toList());
    }

    // ==================== BlockRoom ====================

    public static BlockRoom mapRequestToRoom(BlockRoomRequest request) {
        return BlockRoom.builder()
                .idBlockRoom(request.getIdBlockRoom())
                .name(request.getName())
                .reservedBeds(request.getReservedBeds())
                .generalBeds(request.getGeneralBeds())
                .build();
    }

    public static BlockRoomResponse mapRoomToResponse(BlockRoom room) {
        if (room == null) return null;
        return BlockRoomResponse.builder()
                .idBlockRoom(room.getIdBlockRoom())
                .name(room.getName())
                .reservedBeds(room.getReservedBeds())
                .generalBeds(room.getGeneralBeds())
                .build();
    }

    // ==================== BlockMaster ====================

    public static BlockMaster mapRequestToMaster(BlockMasterRequest request) {
        return BlockMaster.builder()
                .idBlockMaster(request.getIdBlockMaster())
                .staffName(request.getStaffName())
                .staffId(request.getStaffId())
                .appointmentDate(request.getAppointmentDate())
                .exitDate(request.getExitDate())
                .build();
    }

    public static BlockMasterResponse mapMasterToResponse(BlockMaster master) {
        if (master == null) return null;
        return BlockMasterResponse.builder()
                .idBlockMaster(master.getIdBlockMaster())
                .staffName(master.getStaffName())
                .staffId(master.getStaffId())
                .appointmentDate(master.getAppointmentDate())
                .exitDate(master.getExitDate())
                .build();
    }

    // ==================== BlockPrefect ====================

    public static BlockPrefect mapRequestToPrefect(BlockPrefectRequest request) {
        return BlockPrefect.builder()
                .idBlockPrefect(request.getIdBlockPrefect())
                .studentName(request.getStudentName())
                .studentId(request.getStudentId())
                .appointmentDate(request.getAppointmentDate())
                .exitDate(request.getExitDate())
                .build();
    }

    public static BlockPrefectResponse mapPrefectToResponse(BlockPrefect prefect) {
        if (prefect == null) return null;
        return BlockPrefectResponse.builder()
                .idBlockPrefect(prefect.getIdBlockPrefect())
                .studentName(prefect.getStudentName())
                .studentId(prefect.getStudentId())
                .appointmentDate(prefect.getAppointmentDate())
                .exitDate(prefect.getExitDate())
                .build();
    }

    // ==================== BlockRoomStudents ====================

    public static BlockRoomStudentsResponse mapRoomStudentToResponse(BlockRoomStudents entry) {
        if (entry == null) return null;
        return BlockRoomStudentsResponse.builder()
                .idBlockRoomStudent(entry.getIdBlockRoomStudent())
                .studentID(entry.getStudentID())
                .institutionID(entry.getInstitutionID())
                .blockRoom(mapRoomToResponse(entry.getBlockRoom()))
                .build();
    }

    public static List<BlockRoomStudentsResponse> mapRoomStudentsToResponses(List<BlockRoomStudents> entries) {
        if (entries == null) return Collections.emptyList();
        return entries.stream().map(AccommodationMapper::mapRoomStudentToResponse).collect(Collectors.toList());
    }

    // ==================== DutyType ====================

    public static DutyType mapRequestToDutyType(DutyTypeRequest request) {
        return DutyType.builder()
                .idDutyType(request.getIdDutyType())
                .institutionCode(request.getInstitutionCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static DutyTypeResponse mapDutyTypeToResponse(DutyType dutyType) {
        if (dutyType == null) return null;
        return DutyTypeResponse.builder()
                .idDutyType(dutyType.getIdDutyType())
                .institutionCode(dutyType.getInstitutionCode())
                .name(dutyType.getName())
                .description(dutyType.getDescription())
                .build();
    }

    public static List<DutyTypeResponse> mapDutyTypesToResponses(List<DutyType> dutyTypes) {
        if (dutyTypes == null) return Collections.emptyList();
        return dutyTypes.stream().map(AccommodationMapper::mapDutyTypeToResponse).collect(Collectors.toList());
    }

    // ==================== DutyAssignment ====================
    // blockName/dutyTypeName are filled in by the service layer, which has
    // the repositories needed to look them up - the mapper itself stays a
    // pure, dependency-free conversion.

    public static DutyAssignmentResponse mapDutyAssignmentToResponse(
            DutyAssignment assignment, String blockName, String dutyTypeName) {
        if (assignment == null) return null;
        return DutyAssignmentResponse.builder()
                .idDutyAssignment(assignment.getIdDutyAssignment())
                .idBlock(assignment.getIdBlock())
                .blockName(blockName)
                .idDutyType(assignment.getIdDutyType())
                .dutyTypeName(dutyTypeName)
                .studentId(assignment.getStudentId())
                .studentName(assignment.getStudentName())
                .weekStartDate(assignment.getWeekStartDate())
                .status(assignment.getStatus())
                .rotationId(assignment.getRotationId())
                .build();
    }

    // ==================== Amenity ====================

    public static Amenity mapRequestToAmenity(AmenityRequest request) {
        return Amenity.builder()
                .idAmenity(request.getIdAmenity())
                .institutionCode(request.getInstitutionCode())
                .idBlock(request.getIdBlock())
                .idBlockRoom(request.getIdBlockRoom())
                .name(request.getName())
                .condition(request.getCondition() == null ? "GOOD" : request.getCondition())
                .notes(request.getNotes())
                .lastCheckedDate(request.getLastCheckedDate())
                .build();
    }

    public static AmenityResponse mapAmenityToResponse(Amenity amenity, String blockName, String roomName) {
        if (amenity == null) return null;
        return AmenityResponse.builder()
                .idAmenity(amenity.getIdAmenity())
                .idBlock(amenity.getIdBlock())
                .blockName(blockName)
                .idBlockRoom(amenity.getIdBlockRoom())
                .roomName(roomName)
                .name(amenity.getName())
                .condition(amenity.getCondition())
                .notes(amenity.getNotes())
                .lastCheckedDate(amenity.getLastCheckedDate())
                .build();
    }

    // ==================== Consumable ====================

    public static Consumable mapRequestToConsumable(ConsumableRequest request) {
        return Consumable.builder()
                .idConsumable(request.getIdConsumable())
                .institutionCode(request.getInstitutionCode())
                .idBlock(request.getIdBlock())
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .stockStatus(request.getStockStatus() == null ? "IN_STOCK" : request.getStockStatus())
                .lastRestockedDate(request.getLastRestockedDate())
                .build();
    }

    public static ConsumableResponse mapConsumableToResponse(Consumable consumable, String blockName) {
        if (consumable == null) return null;
        return ConsumableResponse.builder()
                .idConsumable(consumable.getIdConsumable())
                .idBlock(consumable.getIdBlock())
                .blockName(blockName)
                .name(consumable.getName())
                .quantity(consumable.getQuantity())
                .unit(consumable.getUnit())
                .stockStatus(consumable.getStockStatus())
                .lastRestockedDate(consumable.getLastRestockedDate())
                .build();
    }

    // ==================== Ticket ====================

    public static Ticket mapRequestToTicket(TicketRequest request) {
        return Ticket.builder()
                .idTicket(request.getIdTicket())
                .institutionCode(request.getInstitutionCode())
                .idBlock(request.getIdBlock())
                .idBlockRoom(request.getIdBlockRoom())
                .title(request.getTitle())
                .description(request.getDescription())
                .raisedByStaffId(request.getRaisedByStaffId())
                .raisedByStaffName(request.getRaisedByStaffName())
                .priority(request.getPriority() == null ? "MEDIUM" : request.getPriority())
                .build();
    }

    public static TicketResponse mapTicketToResponse(Ticket ticket, String blockName, String roomName) {
        if (ticket == null) return null;
        return TicketResponse.builder()
                .idTicket(ticket.getIdTicket())
                .idBlock(ticket.getIdBlock())
                .blockName(blockName)
                .idBlockRoom(ticket.getIdBlockRoom())
                .roomName(roomName)
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .raisedByStaffId(ticket.getRaisedByStaffId())
                .raisedByStaffName(ticket.getRaisedByStaffName())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .dateRaised(ticket.getDateRaised())
                .dateResolved(ticket.getDateResolved())
                .resolutionNotes(ticket.getResolutionNotes())
                .build();
    }
}
