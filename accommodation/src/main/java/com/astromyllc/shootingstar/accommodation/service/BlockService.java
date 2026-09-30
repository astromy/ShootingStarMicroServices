package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.BlockRoomRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockResponse;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockRoomResponse;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.BlockRoom;
import com.astromyllc.shootingstar.accommodation.repository.BlockRepository;
import com.astromyllc.shootingstar.accommodation.repository.BlockRoomRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockServiceInterface;
import com.astromyllc.shootingstar.accommodation.util.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService implements BlockServiceInterface {

    private final BlockRepository blockRepository;
    private final BlockRoomRepository blockRoomRepository;

    @Override
    public BlockResponse addInstitutionAccommodation(BlockRequest blockRequest) {
        if (blockRequest.getInstitutionCode() == null || blockRequest.getInstitutionCode().isBlank()) {
            throw new IllegalArgumentException("institutionCode is required to create a block");
        }
        if (blockRequest.getName() == null || blockRequest.getName().isBlank()) {
            throw new IllegalArgumentException("name is required to create a block");
        }

        Block block = AccommodationMapper.mapRequestToBlock(blockRequest);
        Block saved = blockRepository.save(block);
        return AccommodationMapper.mapBlockToResponse(saved);
    }

    @Override
    public List<BlockResponse> getInstitutionAccommodation(SingleStringRequest request) {
        if (request == null || request.getVal() == null || request.getVal().isBlank()) {
            return new ArrayList<>();
        }
        List<Block> blocks = blockRepository.findByInstitutionCode(request.getVal());
        return AccommodationMapper.mapBlocksToResponses(blocks);
    }

    @Override
    public BlockResponse updateBlock(BlockRequest blockRequest) {
        if (blockRequest.getIdBlock() == null) {
            throw new IllegalArgumentException("idBlock is required to update a block");
        }

        Block existing = blockRepository.findByIdBlockAndInstitutionCode(
                        blockRequest.getIdBlock(), blockRequest.getInstitutionCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No block found with id " + blockRequest.getIdBlock() + " for this institution"));

        // Only the block's own fields update here - rooms, masters, and
        // prefects are managed through their own dedicated endpoints, so a
        // block update never accidentally touches them.
        if (blockRequest.getName() != null && !blockRequest.getName().isBlank()) {
            existing.setName(blockRequest.getName());
        }
        if (blockRequest.getSlogan() != null) {
            existing.setSlogan(blockRequest.getSlogan());
        }
        if (blockRequest.getGender() != null) {
            existing.setGender(blockRequest.getGender());
        }

        Block saved = blockRepository.save(existing);
        return AccommodationMapper.mapBlockToResponse(saved);
    }

    @Override
    public boolean deleteBlock(Long idBlock) {
        if (idBlock == null || !blockRepository.existsById(idBlock)) {
            return false;
        }
        // cascade = CascadeType.ALL on Block's relationships means this
        // also removes the block's rooms, masters, and prefects.
        blockRepository.deleteById(idBlock);
        return true;
    }

    @Override
    public BlockRoomResponse addRoomToBlock(Long idBlock, BlockRoomRequest roomRequest) {
        Block block = blockRepository.findById(idBlock)
                .orElseThrow(() -> new IllegalArgumentException("No block found with id " + idBlock));

        if (roomRequest.getName() == null || roomRequest.getName().isBlank()) {
            throw new IllegalArgumentException("Room name is required");
        }
        if (roomRequest.getReservedBeds() == null || roomRequest.getGeneralBeds() == null) {
            throw new IllegalArgumentException("reservedBeds and generalBeds are required");
        }

        BlockRoom room = AccommodationMapper.mapRequestToRoom(roomRequest);
        if (block.getRoomsList() == null) {
            block.setRoomsList(new ArrayList<>());
        }
        block.getRoomsList().add(room);

        Block saved = blockRepository.save(block);
        // The newly-added room is the last one in the saved list - cascade
        // persistence assigns it an ID at this point.
        BlockRoom savedRoom = saved.getRoomsList().get(saved.getRoomsList().size() - 1);
        return AccommodationMapper.mapRoomToResponse(savedRoom);
    }

    @Override
    public boolean deleteRoom(Long idBlockRoom) {
        if (idBlockRoom == null || !blockRoomRepository.existsById(idBlockRoom)) {
            return false;
        }
        blockRoomRepository.deleteById(idBlockRoom);
        return true;
    }
}
