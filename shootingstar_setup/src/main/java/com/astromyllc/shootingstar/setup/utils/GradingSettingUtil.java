package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.GradingRequest;
import com.astromyllc.shootingstar.setup.dto.request.GradingSettingRequest;
import com.astromyllc.shootingstar.setup.dto.response.GradingResponse;
import com.astromyllc.shootingstar.setup.dto.response.GradingSettingResponse;
import com.astromyllc.shootingstar.setup.model.Grading;
import com.astromyllc.shootingstar.setup.model.GradingSetting;
import com.astromyllc.shootingstar.setup.repository.GradingRepository;
import com.astromyllc.shootingstar.setup.repository.GradingSettingRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GradingSettingUtil {
    private final GradingSettingRepository gradingSettingRepository;
    public static List<GradingSetting> gradingSettingsGlobalList = null;


    @Bean
    private void getAllGradingSettings() {
        gradingSettingsGlobalList = gradingSettingRepository.findAll();
        log.info("Global Grading Setting List Populated with {} records", gradingSettingsGlobalList.stream().count());
    }

    public static GradingSetting mapGradeSettingRequest_ToGradeSetting(GradingSettingRequest gradingSetting) {
        return GradingSetting.builder()
                .examsPercentage(gradingSetting.getExamsPercentage())
                .allowedTrails(gradingSetting.getAllowedTrails())
                .classPercentage(gradingSetting.getClassPercentage())
                .trailingMark(gradingSetting.getTrailingMark())
                .gradingList((List<Grading>) gradingSetting.getGradingList().stream().map(gL -> {
                    return mapGradeListRequest_ToGradeList(gL);
                }).toList())
                .build();
    }

    public static GradingSetting mapGradeSettingRequest_ToGradeSetting(GradingSettingRequest gradingSetting, GradingSetting gs) {
        gs.setExamsPercentage(gradingSetting.getExamsPercentage());
        gs.setAllowedTrails(gradingSetting.getAllowedTrails());
        gs.setClassPercentage(gradingSetting.getClassPercentage());
        gs.setTrailingMark(gradingSetting.getTrailingMark());
        gs.setGradingList((List<Grading>) gradingSetting.getGradingList().stream().map(gL -> {
            return mapGradeListRequest_ToGradeList(gL,gs.getGradingList().stream().filter(g-> gL.getId().equals(g.getIdGrading())).findFirst().get());
        }).collect(Collectors.toList()));
        return gs;
    }

    public static GradingSettingResponse mapGradeSetting_ToGradeSettingResponse(GradingSetting gradingSetting) {
        return GradingSettingResponse.builder()
                .id(gradingSetting.getIdGradingSetting())
                .examsPercentage(gradingSetting.getExamsPercentage())
                .allowedTrails(gradingSetting.getAllowedTrails())
                .classPercentage(gradingSetting.getClassPercentage())
                .trailingMark(gradingSetting.getTrailingMark())
                .gradingList((List<GradingResponse>) gradingSetting.getGradingList().stream().map(gL -> {
                    return mapGradeList_ToGradeListResponse(gL);
                }).toList())
                .build();
    }

    private static GradingResponse mapGradeList_ToGradeListResponse(Grading gL) {
        return GradingResponse.builder()
                .id(gL.getIdGrading())
                .grade(gL.getGrade())
                .comment(gL.getComment())
                .lowerLimit(gL.getLowerLimit())
                .build();
    }

    private static Grading mapGradeListRequest_ToGradeList(GradingRequest gL) {
        return Grading.builder()
                .grade(gL.getGrade())
                .comment(gL.getComment())
                .lowerLimit(gL.getLowerLimit())
                .build();
    }

    private static Grading mapGradeListRequest_ToGradeList(GradingRequest gL, Grading grading) {
        grading.setGrade(gL.getGrade());
        grading.setComment(gL.getComment());
        grading.setLowerLimit(gL.getLowerLimit());
        return grading;
    }

}
