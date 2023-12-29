package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.GradingSettingRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.GradingSettingResponse;
import com.astromyllc.shootingstar.setup.model.GradingSetting;
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
import java.util.stream.Collectors;

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
      Institution ins= institutionUtils.institutionGlobalList.stream().filter(i->i.getBececode().equals(gradingSettingRequestList.getInstitution())).findFirst().get();
      ins.setGradingSetting((GradingSetting) gradingSettingRequestList.getGradingSettingDetails().stream().map(gd->gradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(gd)));
    institutionRepository.save(ins);
    return gradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(ins.getGradingSetting());
    }

    @Override
    public List<Optional<GradingSettingResponse>> getAllGradingSettings() {
        return gradingSettingUtil.gradingSettingsGlobalList.stream().map(g->gradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(g)).collect(Collectors.toList());
    }

    @Override
    public Optional<GradingSettingResponse> getAllGradingSettingsByInstitution(SingleStringRequest institutionRequest1) {
        String institutionRequest= institutionRequest1.getVal();
        return  gradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(institutionUtils.institutionGlobalList.stream().filter(i->i.getBececode().equals(institutionRequest)).findFirst().get().getGradingSetting());
    }
}
