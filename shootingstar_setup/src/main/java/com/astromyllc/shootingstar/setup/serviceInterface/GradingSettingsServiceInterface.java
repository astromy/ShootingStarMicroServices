package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.GradingSettingResponse;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;

import java.util.List;
import java.util.Optional;

public interface GradingSettingsServiceInterface {
    public void createGradingSetting(GradingSettingRequest gradingSettingRequest);
    public  Optional<List<GradingSettingResponse>> createGradingSettingDetails(GradingSettingRequest gradingSettingRequestList);
    public List<Optional<GradingSettingResponse>> getAllGradingSettings();
    public Optional<List<GradingSettingResponse>> getAllGradingSettingsByInstitution(SingleStringRequest beceCode);
}
