package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.GradingSettingRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.serviceInterface.GradingSettingsServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradingSystemService implements GradingSettingsServiceInterface {
    @Override
    public void createGradingSetting(GradingSettingRequest gradingSettingRequest) {

    }

    @Override
    public void createGradingSetting(List<GradingSettingRequest> gradingSettingRequestList) {

    }

    @Override
    public Optional<List<GradingSettingRequest>> getAllGradingSettings() {
        return Optional.empty();
    }

    @Override
    public Optional<List<GradingSettingRequest>> getAllGradingSettingsByInstitution(InstitutionRequest institutionRequest) {
        return Optional.empty();
    }
}
