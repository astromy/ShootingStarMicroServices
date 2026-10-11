package com.astromyllc.shootingstar.accommodation.service;

import com.astromyllc.shootingstar.accommodation.dto.request.DutyTypeRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.DutyTypeResponse;
import com.astromyllc.shootingstar.accommodation.model.DutyType;
import com.astromyllc.shootingstar.accommodation.repository.DutyTypeRepository;
import com.astromyllc.shootingstar.accommodation.serviceInterface.DutyTypeServiceInterface;
import com.astromyllc.shootingstar.accommodation.util.AccommodationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DutyTypeService implements DutyTypeServiceInterface {

    private final DutyTypeRepository dutyTypeRepository;

    @Override
    public DutyTypeResponse addDutyType(DutyTypeRequest request) {
        if (request.getInstitutionCode() == null || request.getInstitutionCode().isBlank()) {
            throw new IllegalArgumentException("institutionCode is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        DutyType saved = dutyTypeRepository.save(AccommodationMapper.mapRequestToDutyType(request));
        return AccommodationMapper.mapDutyTypeToResponse(saved);
    }

    @Override
    public List<DutyTypeResponse> getDutyTypes(String institutionCode) {
        if (institutionCode == null || institutionCode.isBlank()) {
            return new ArrayList<>();
        }
        return AccommodationMapper.mapDutyTypesToResponses(
                dutyTypeRepository.findByInstitutionCode(institutionCode));
    }

    @Override
    public boolean deleteDutyType(Long idDutyType) {
        if (idDutyType == null || !dutyTypeRepository.existsById(idDutyType)) {
            return false;
        }
        dutyTypeRepository.deleteById(idDutyType);
        return true;
    }
}
