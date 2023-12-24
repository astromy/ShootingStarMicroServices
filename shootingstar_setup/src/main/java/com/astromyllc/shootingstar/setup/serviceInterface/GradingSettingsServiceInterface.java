package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.request.GradingSettingRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SubjectRequest;
import com.astromyllc.shootingstar.setup.dto.response.GradingSettingResponse;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;

import java.util.List;
import java.util.Optional;

public interface GradingSettingsServiceInterface {
    public void createGradingSetting(GradingSettingRequest gradingSettingRequest);
    public  void createGradingSetting(List<GradingSettingRequest> gradingSettingRequestList);
    public Optional<List<GradingSettingRequest>> getAllGradingSettings();
    public Optional<List<GradingSettingResponse>> getAllGradingSettingsByInstitution(String beceCode);
}
