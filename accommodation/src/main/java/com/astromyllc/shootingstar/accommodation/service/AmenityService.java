package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.AmenityRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.AmenityResponse;
import com.astromyllc.shootingstar.accommodation.model.Amenity;
import com.astromyllc.shootingstar.accommodation.model.Block;
import com.astromyllc.shootingstar.accommodation.model.BlockRoom;
import com.astromyllc.shootingstar.accommodation.repository.AmenityRepository;
import com.astromyllc.shootingstar.accommodation.repository.BlockRepository;
import com.astromyllc.shootingstar.accommodation.repository.BlockRoomRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.AmenityServiceInterface;
import com.astromyllc.shootingstar.accommodation.util.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmenityService implements AmenityServiceInterface {

    private final AmenityRepository amenityRepository;
    private final BlockRepository blockRepository;
    private final BlockRoomRepository blockRoomRepository;

    @Override
    public AmenityResponse addAmenity(AmenityRequest request) {
        if (request.getIdBlock() == null) {
            throw new IllegalArgumentException("idBlock is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (!blockRepository.existsById(request.getIdBlock())) {
            throw new IllegalArgumentException("No block found with id " + request.getIdBlock());
        }
        Amenity saved = amenityRepository.save(AccommodationMapper.mapRequestToAmenity(request));
        return enrich(saved);
    }

    @Override
    public AmenityResponse updateAmenity(AmenityRequest request) {
        if (request.getIdAmenity() == null) {
            throw new IllegalArgumentException("idAmenity is required to update an amenity");
        }
        Amenity existing = amenityRepository.findById(request.getIdAmenity())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No amenity found with id " + request.getIdAmenity()));

        if (request.getName() != null && !request.getName().isBlank()) {
            existing.setName(request.getName());
        }
        if (request.getCondition() != null) {
            existing.setCondition(request.getCondition());
        }
        if (request.getNotes() != null) {
            existing.setNotes(request.getNotes());
        }
        if (request.getLastCheckedDate() != null) {
            existing.setLastCheckedDate(request.getLastCheckedDate());
        }
        if (request.getIdBlockRoom() != null) {
            existing.setIdBlockRoom(request.getIdBlockRoom());
        }

        Amenity saved = amenityRepository.save(existing);
        return enrich(saved);
    }

    @Override
    public List<AmenityResponse> getBlockAmenities(Long idBlock) {
        if (idBlock == null) return new ArrayList<>();
        List<Amenity> amenities = amenityRepository.findByIdBlock(idBlock);
        List<AmenityResponse> responses = new ArrayList<>();
        for (Amenity a : amenities) {
            responses.add(enrich(a));
        }
        return responses;
    }

    @Override
    public boolean deleteAmenity(Long idAmenity) {
        if (idAmenity == null || !amenityRepository.existsById(idAmenity)) {
            return false;
        }
        amenityRepository.deleteById(idAmenity);
        return true;
    }

    private AmenityResponse enrich(Amenity amenity) {
        String blockName = blockRepository.findById(amenity.getIdBlock())
                .map(Block::getName).orElse(null);
        String roomName = amenity.getIdBlockRoom() == null ? null
                : blockRoomRepository.findById(amenity.getIdBlockRoom())
                .map(BlockRoom::getName).orElse(null);
        return AccommodationMapper.mapAmenityToResponse(amenity, blockName, roomName);
    }
}
