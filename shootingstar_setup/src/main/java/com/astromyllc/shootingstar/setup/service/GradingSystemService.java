package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.GradingSettingRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.GradingSettingResponse;
import com.astromyllc.shootingstar.setup.model.GradingSetting;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.GradingSettingRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.GradingSettingsServiceInterface;
import com.astromyllc.shootingstar.setup.utils.GradingSettingUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradingSystemService implements GradingSettingsServiceInterface {
    private final InstitutionUtils institutionUtils;
    private final InstitutionRepository institutionRepository;
    private final GradingSettingRepository gradingSettingRepository;
    private final GradingSettingUtil gradingSettingUtil;

    @Override
    public void createGradingSetting(GradingSettingRequest gradingSettingRequest) {

    }
    @Override
    public Optional<List<GradingSettingResponse>> createGradingSettingDetails(GradingSettingRequest gradingSettingRequest) {
        // Find institution safely
        Institution institution = InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(gradingSettingRequest.getInstitution()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Institution not found"));

        // 1. Create DETACHED copy of existing settings (for in-memory use only)
        List<GradingSetting> detachedExistingSettings = institution.getGradingSetting() != null
                ? new ArrayList<>(institution.getGradingSetting())
                : new ArrayList<>();

        // 2. Clear and replace persistent settings
        institution.getGradingSetting().clear(); // orphanRemoval will delete from DB

        // Create and add new persistent setting
        GradingSetting newSetting = GradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(
                gradingSettingRequest.getGradingSettingDetails());
        newSetting.setInstitution(institution);
        institution.getGradingSetting().add(newSetting);

        // 3. Save ONLY the new setting (via cascade)
        institution = institutionRepository.save(institution);

        // 4. Create in-memory combined list (not persisted)
        List<GradingSetting> combinedList = new ArrayList<>();
        combinedList.addAll(detachedExistingSettings);
        combinedList.addAll(institution.getGradingSetting()); // adds the newly persisted setting

        // 5. Convert combined list to response
        return Optional.of(
                combinedList.stream()
                        .map(GradingSettingUtil::mapGradeSetting_ToGradeSettingResponse)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList()
        );
    }



    @Override
    public List<Optional<GradingSettingResponse>> getAllGradingSettings() {
        return GradingSettingUtil.gradingSettingsGlobalList.stream().map(GradingSettingUtil::mapGradeSetting_ToGradeSettingResponse).toList();
    }

    @Override
    public Optional<List<GradingSettingResponse>> getAllGradingSettingsByInstitution(SingleStringRequest institutionRequest1) {
        String institutionRequest = institutionRequest1.getVal();
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(institutionRequest))
                .findFirst()
                .map(Institution::getGradingSetting)
                .map(list -> list.stream()
                        .map(GradingSettingUtil::mapGradeSetting_ToGradeSettingResponse)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList());

    }
}
