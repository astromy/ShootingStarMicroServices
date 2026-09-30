package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockMasterRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockMasterResponse;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.BlockMaster;
import com.astromyllc.shootingstar.accommodation.repository.BlockMasterRepository;
import com.astromyllc.shootingstar.accommodation.repository.BlockRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockMasterServiceInterface;
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
public class BlockMasterService implements BlockMasterServiceInterface {

    private final BlockRepository blockRepository;
    private final BlockMasterRepository blockMasterRepository;

    @Override
    public BlockMasterResponse assignBlockMaster(Long idBlock, BlockMasterRequest request) {
        Block block = blockRepository.findById(idBlock)
                .orElseThrow(() -> new IllegalArgumentException("No block found with id " + idBlock));

        if (request.getStaffId() == null || request.getStaffId().isBlank()) {
            throw new IllegalArgumentException("staffId is required to assign a block master");
        }

        BlockMaster master = AccommodationMapper.mapRequestToMaster(request);
        if (master.getAppointmentDate() == null) {
            master.setAppointmentDate(LocalDate.now());
        }

        if (block.getBlockMasters() == null) {
            block.setBlockMasters(new ArrayList<>());
        }
        block.getBlockMasters().add(master);

        Block saved = blockRepository.save(block);
        BlockMaster savedMaster = saved.getBlockMasters().get(saved.getBlockMasters().size() - 1);
        return AccommodationMapper.mapMasterToResponse(savedMaster);
    }

    @Override
    public BlockMasterResponse endBlockMasterAssignment(Long idBlockMaster) {
        BlockMaster master = blockMasterRepository.findById(idBlockMaster)
                .orElseThrow(() -> new IllegalArgumentException("No block master found with id " + idBlockMaster));

        master.setExitDate(LocalDate.now());
        BlockMaster saved = blockMasterRepository.save(master);
        return AccommodationMapper.mapMasterToResponse(saved);
    }

    @Override
    public List<BlockMasterResponse> getBlockMasters(Long idBlock) {
        Block block = blockRepository.findById(idBlock)
                .orElseThrow(() -> new IllegalArgumentException("No block found with id " + idBlock));

        if (block.getBlockMasters() == null) {
            return List.of();
        }
        return block.getBlockMasters().stream()
                .map(AccommodationMapper::mapMasterToResponse)
                .toList();
    }
}
