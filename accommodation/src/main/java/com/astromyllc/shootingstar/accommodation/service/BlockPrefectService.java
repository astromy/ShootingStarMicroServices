package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockPrefectRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockPrefectResponse;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.BlockPrefect;
import com.astromyllc.shootingstar.accommodation.repository.BlockPrefectRepository;
import com.astromyllc.shootingstar.accommodation.repository.BlockRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockPrefectServiceInterface;
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
public class BlockPrefectService implements BlockPrefectServiceInterface {

    private final BlockRepository blockRepository;
    private final BlockPrefectRepository blockPrefectRepository;

    @Override
    public BlockPrefectResponse assignBlockPrefect(Long idBlock, BlockPrefectRequest request) {
        Block block = blockRepository.findById(idBlock)
                .orElseThrow(() -> new IllegalArgumentException("No block found with id " + idBlock));

        if (request.getStudentId() == null || request.getStudentId().isBlank()) {
            throw new IllegalArgumentException("studentId is required to assign a block prefect");
        }

        BlockPrefect prefect = AccommodationMapper.mapRequestToPrefect(request);
        if (prefect.getAppointmentDate() == null) {
            prefect.setAppointmentDate(LocalDate.now());
        }

        if (block.getBlockPrefects() == null) {
            block.setBlockPrefects(new ArrayList<>());
        }
        block.getBlockPrefects().add(prefect);

        Block saved = blockRepository.save(block);
        BlockPrefect savedPrefect = saved.getBlockPrefects().get(saved.getBlockPrefects().size() - 1);
        return AccommodationMapper.mapPrefectToResponse(savedPrefect);
    }

    @Override
    public BlockPrefectResponse endBlockPrefectAssignment(Long idBlockPrefect) {
        BlockPrefect prefect = blockPrefectRepository.findById(idBlockPrefect)
                .orElseThrow(() -> new IllegalArgumentException("No block prefect found with id " + idBlockPrefect));

        prefect.setExitDate(LocalDate.now());
        BlockPrefect saved = blockPrefectRepository.save(prefect);
        return AccommodationMapper.mapPrefectToResponse(saved);
    }

    @Override
    public List<BlockPrefectResponse> getBlockPrefects(Long idBlock) {
        Block block = blockRepository.findById(idBlock)
                .orElseThrow(() -> new IllegalArgumentException("No block found with id " + idBlock));

        if (block.getBlockPrefects() == null) {
            return List.of();
        }
        return block.getBlockPrefects().stream()
                .map(AccommodationMapper::mapPrefectToResponse)
                .toList();
    }
}
