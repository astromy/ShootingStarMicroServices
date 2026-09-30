package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockRoomStudentsRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockRoomStudentsResponse;
import com.astromyllc.shootingstar.accommodation.model.BlockRoom;
import com.astromyllc.shootingstar.accommodation.model.BlockRoomStudents;
import com.astromyllc.shootingstar.accommodation.repository.BlockRoomRepository;
import com.astromyllc.shootingstar.accommodation.repository.BlockRoomStudentsRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockRoomStudentsServiceInterface;
import com.astromyllc.shootingstar.accommodation.util.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockRoomStudentsService implements BlockRoomStudentsServiceInterface {

    private final BlockRoomRepository blockRoomRepository;
    private final BlockRoomStudentsRepository blockRoomStudentsRepository;

    @Override
    public BlockRoomStudentsResponse assignStudentToRoom(BlockRoomStudentsRequest request) {
        if (request.getStudentID() == null || request.getStudentID().isBlank()) {
            throw new IllegalArgumentException("studentID is required");
        }
        if (request.getInstitutionID() == null || request.getInstitutionID().isBlank()) {
            throw new IllegalArgumentException("institutionID is required");
        }
        if (request.getIdBlockRoom() == null) {
            throw new IllegalArgumentException("idBlockRoom is required");
        }

        BlockRoom room = blockRoomRepository.findById(request.getIdBlockRoom())
                .orElseThrow(() -> new IllegalArgumentException("No room found with id " + request.getIdBlockRoom()));

        // A student already assigned somewhere at this institution needs to
        // be unassigned first - this isn't a transfer operation, so moving
        // a student is two explicit calls (unassign, then assign), not one
        // implicit one.
        blockRoomStudentsRepository
                .findByStudentIDAndInstitutionID(request.getStudentID(), request.getInstitutionID())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Student " + request.getStudentID() + " is already assigned to room "
                                    + existing.getBlockRoom().getName() + " - unassign first before reassigning");
                });

        int capacity = (room.getReservedBeds() == null ? 0 : room.getReservedBeds())
                + (room.getGeneralBeds() == null ? 0 : room.getGeneralBeds());
        int currentOccupants = blockRoomStudentsRepository.findByBlockRoom_IdBlockRoom(room.getIdBlockRoom()).size();

        if (currentOccupants >= capacity) {
            throw new IllegalStateException(
                    "Room " + room.getName() + " is at capacity (" + currentOccupants + "/" + capacity + ")");
        }

        BlockRoomStudents entry = BlockRoomStudents.builder()
                .studentID(request.getStudentID())
                .institutionID(request.getInstitutionID())
                .blockRoom(room)
                .build();

        BlockRoomStudents saved = blockRoomStudentsRepository.save(entry);
        return AccommodationMapper.mapRoomStudentToResponse(saved);
    }

    @Override
    public boolean unassignStudent(Long idBlockRoomStudent) {
        if (idBlockRoomStudent == null || !blockRoomStudentsRepository.existsById(idBlockRoomStudent)) {
            return false;
        }
        blockRoomStudentsRepository.deleteById(idBlockRoomStudent);
        return true;
    }

    @Override
    public List<BlockRoomStudentsResponse> getRoomOccupants(Long idBlockRoom) {
        List<BlockRoomStudents> entries = blockRoomStudentsRepository.findByBlockRoom_IdBlockRoom(idBlockRoom);
        return AccommodationMapper.mapRoomStudentsToResponses(entries);
    }

    @Override
    public List<BlockRoomStudentsResponse> getInstitutionAssignments(String institutionID) {
        List<BlockRoomStudents> entries = blockRoomStudentsRepository.findByInstitutionID(institutionID);
        return AccommodationMapper.mapRoomStudentsToResponses(entries);
    }
}
