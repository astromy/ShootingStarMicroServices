package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.GradingSettingRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.GradingSettingResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.GradingSettingRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.GradingSettingsServiceInterface;
import com.astromyllc.shootingstar.setup.utils.GradingSettingUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
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
    private final InstitutionUtils institutionUtils;
    private final InstitutionRepository institutionRepository;
    private final GradingSettingRepository gradingSettingRepository;
    private final GradingSettingUtil gradingSettingUtil;
    @Override
    public void createGradingSetting(GradingSettingRequest gradingSettingRequest) {

    }

    @Override
    public Optional<GradingSettingResponse> createGradingSettingDetails(GradingSettingRequest gradingSettingRequestList) {
      Institution ins= InstitutionUtils.institutionGlobalList.stream().filter(i->i.getBececode().equalsIgnoreCase(gradingSettingRequestList.getInstitution())).findFirst().get();
      ins.setGradingSetting(GradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(gradingSettingRequestList.getGradingSettingDetails()));
    institutionRepository.save(ins);
    return GradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(ins.getGradingSetting());
    }

    @Override
    public List<Optional<GradingSettingResponse>> getAllGradingSettings() {
        return GradingSettingUtil.gradingSettingsGlobalList.stream().map(GradingSettingUtil::mapGradeSetting_ToGradeSettingResponse).toList();
    }

    @Override
    public Optional<GradingSettingResponse> getAllGradingSettingsByInstitution(SingleStringRequest institutionRequest1) {
        String institutionRequest= institutionRequest1.getVal();
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(institutionRequest))
                .findFirst()
                .flatMap(institution -> GradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(institution.getGradingSetting()));

    }
}
