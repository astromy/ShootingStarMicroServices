package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.PromotionsRequestDetails;
import com.astromyllc.shootingstar.setup.dto.response.PromotionsResponse;
import com.astromyllc.shootingstar.setup.model.Promotions;
import com.astromyllc.shootingstar.setup.repository.PromotionsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionsUtil {
    public static List<Promotions> promotionsGlobalList = null;
    private final PromotionsRepository promotionsRepository;

    public static Promotions mapPromotionRequestToPromotionSettings(PromotionsRequestDetails ps) {
        Promotions ps2 = Promotions.builder()
                .academicYear(ps.getAcademicYear())
                .currentClass(ps.getCurrentClass())
                .targetClass(ps.getTargetClass())
                .transactionDate(LocalDate.now())
                .build();
        return ps2;
    }

    public static Promotions mapPromotionRequestToPromotionsSettings(PromotionsRequestDetails psd, Promotions ps) {
        ps.setCurrentClass(psd.getCurrentClass());
        ps.setTargetClass(psd.getTargetClass());
        ps.setAcademicYear(psd.getAcademicYear());
        ps.setTransactionDate(LocalDate.now());
        return ps;
    }

    public static Optional<PromotionsResponse> mapPromotionsToPromotionResponse(Promotions ps) {
        return Optional.ofNullable(PromotionsResponse.builder()
                .promotionId(ps.getPromotionId())
                .targetClass(ps.getTargetClass())
                .currentClass(ps.getCurrentClass())
                .academicYear(ps.getAcademicYear())
                .transactionDate(ps.getTransactionDate())
                .build());
    }

    @PostConstruct
    private void getAllClasses() {
        promotionsGlobalList = promotionsRepository.findAll();
        log.info("Global List of Promotions Populated with {} records", promotionsGlobalList.stream().count());
    }


}
