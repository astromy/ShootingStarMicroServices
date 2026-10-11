package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.ConsumableRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.ConsumableResponse;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.Consumable;
import com.astromyllc.shootingstar.accommodation.repository.BlockRepository;
import com.astromyllc.shootingstar.accommodation.repository.ConsumableRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.ConsumableServiceInterface;
import com.astromyllc.shootingstar.accommodation.util.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumableService implements ConsumableServiceInterface {

    private final ConsumableRepository consumableRepository;
    private final BlockRepository blockRepository;

    @Override
    public ConsumableResponse addConsumable(ConsumableRequest request) {
        if (request.getIdBlock() == null) {
            throw new IllegalArgumentException("idBlock is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (request.getQuantity() == null) {
            throw new IllegalArgumentException("quantity is required");
        }
        if (!blockRepository.existsById(request.getIdBlock())) {
            throw new IllegalArgumentException("No block found with id " + request.getIdBlock());
        }
        Consumable saved = consumableRepository.save(AccommodationMapper.mapRequestToConsumable(request));
        return enrich(saved);
    }

    @Override
    public ConsumableResponse updateConsumable(ConsumableRequest request) {
        if (request.getIdConsumable() == null) {
            throw new IllegalArgumentException("idConsumable is required to update a consumable");
        }
        Consumable existing = consumableRepository.findById(request.getIdConsumable())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No consumable found with id " + request.getIdConsumable()));

        if (request.getName() != null && !request.getName().isBlank()) {
            existing.setName(request.getName());
        }
        if (request.getQuantity() != null) {
            existing.setQuantity(request.getQuantity());
        }
        if (request.getUnit() != null) {
            existing.setUnit(request.getUnit());
        }
        if (request.getStockStatus() != null) {
            existing.setStockStatus(request.getStockStatus());
        }
        if (request.getLastRestockedDate() != null) {
            existing.setLastRestockedDate(request.getLastRestockedDate());
        }

        Consumable saved = consumableRepository.save(existing);
        return enrich(saved);
    }

    @Override
    public List<ConsumableResponse> getBlockConsumables(Long idBlock) {
        if (idBlock == null) return new ArrayList<>();
        List<Consumable> consumables = consumableRepository.findByIdBlock(idBlock);
        List<ConsumableResponse> responses = new ArrayList<>();
        for (Consumable c : consumables) {
            responses.add(enrich(c));
        }
        return responses;
    }

    @Override
    public boolean deleteConsumable(Long idConsumable) {
        if (idConsumable == null || !consumableRepository.existsById(idConsumable)) {
            return false;
        }
        consumableRepository.deleteById(idConsumable);
        return true;
    }

    private ConsumableResponse enrich(Consumable consumable) {
        String blockName = blockRepository.findById(consumable.getIdBlock())
                .map(Block::getName).orElse(null);
        return AccommodationMapper.mapConsumableToResponse(consumable, blockName);
    }
}
