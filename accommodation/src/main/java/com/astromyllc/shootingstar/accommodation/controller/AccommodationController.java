package com.astromyllc.shootingstar.accommodation.controller;

import com.astromyllc.shootingstar.accommodation.dto.request.AddRoomRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.AssignBlockMasterRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.AssignBlockPrefectRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.BlockRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.BlockRoomStudentsRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleIdRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockMasterServiceInterface;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockPrefectServiceInterface;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockRoomStudentsServiceInterface;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    private final BlockServiceInterface blockServiceInterface;
    private final BlockMasterServiceInterface blockMasterServiceInterface;
    private final BlockPrefectServiceInterface blockPrefectServiceInterface;
    private final BlockRoomStudentsServiceInterface blockRoomStudentsServiceInterface;

    // ==================== Blocks ====================
    // The two endpoint URLs below are unchanged from before, so ORB's
    // existing proxy controller keeps working without changes. Everything
    // past this point is new.

    @PostMapping("/api/accommodation/addInstitutionAccommodation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addInstitutionAccommodation(@RequestBody BlockRequest blockRequest) {
        log.info("Block Received");
        return ResponseEntity.ok(blockServiceInterface.addInstitutionAccommodation(blockRequest));
    }

    @PostMapping("/api/accommodation/getInstitutionAccommodation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInstitutionAccommodation(@RequestBody SingleStringRequest request) {
        log.info("Block Received");
        return ResponseEntity.ok(blockServiceInterface.getInstitutionAccommodation(request));
    }

    @PostMapping("/api/accommodation/updateBlock")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateBlock(@RequestBody BlockRequest blockRequest) {
        return ResponseEntity.ok(blockServiceInterface.updateBlock(blockRequest));
    }

    @PostMapping("/api/accommodation/deleteBlock")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteBlock(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockServiceInterface.deleteBlock(request.getVal()));
    }

    // ==================== Rooms ====================

    @PostMapping("/api/accommodation/addRoomToBlock")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addRoomToBlock(@RequestBody AddRoomRequest request) {
        return ResponseEntity.ok(blockServiceInterface.addRoomToBlock(request.getIdBlock(), request.getRoom()));
    }

    @PostMapping("/api/accommodation/deleteRoom")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteRoom(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockServiceInterface.deleteRoom(request.getVal()));
    }

    // ==================== Block Masters ====================

    @PostMapping("/api/accommodation/assignBlockMaster")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> assignBlockMaster(@RequestBody AssignBlockMasterRequest request) {
        return ResponseEntity.ok(
                blockMasterServiceInterface.assignBlockMaster(request.getIdBlock(), request.getBlockMaster()));
    }

    @PostMapping("/api/accommodation/endBlockMasterAssignment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> endBlockMasterAssignment(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockMasterServiceInterface.endBlockMasterAssignment(request.getVal()));
    }

    @PostMapping("/api/accommodation/getBlockMasters")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBlockMasters(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockMasterServiceInterface.getBlockMasters(request.getVal()));
    }

    // ==================== Block Prefects ====================

    @PostMapping("/api/accommodation/assignBlockPrefect")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> assignBlockPrefect(@RequestBody AssignBlockPrefectRequest request) {
        return ResponseEntity.ok(
                blockPrefectServiceInterface.assignBlockPrefect(request.getIdBlock(), request.getBlockPrefect()));
    }

    @PostMapping("/api/accommodation/endBlockPrefectAssignment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> endBlockPrefectAssignment(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockPrefectServiceInterface.endBlockPrefectAssignment(request.getVal()));
    }

    @PostMapping("/api/accommodation/getBlockPrefects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBlockPrefects(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockPrefectServiceInterface.getBlockPrefects(request.getVal()));
    }

    // ==================== Room Assignments (students) ====================

    @PostMapping("/api/accommodation/assignStudentToRoom")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> assignStudentToRoom(@RequestBody BlockRoomStudentsRequest request) {
        return ResponseEntity.ok(blockRoomStudentsServiceInterface.assignStudentToRoom(request));
    }

    @PostMapping("/api/accommodation/unassignStudent")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> unassignStudent(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockRoomStudentsServiceInterface.unassignStudent(request.getVal()));
    }

    @PostMapping("/api/accommodation/getRoomOccupants")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getRoomOccupants(@RequestBody SingleIdRequest request) {
        return ResponseEntity.ok(blockRoomStudentsServiceInterface.getRoomOccupants(request.getVal()));
    }

    @PostMapping("/api/accommodation/getInstitutionRoomAssignments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInstitutionRoomAssignments(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(blockRoomStudentsServiceInterface.getInstitutionAssignments(request.getVal()));
    }
}
