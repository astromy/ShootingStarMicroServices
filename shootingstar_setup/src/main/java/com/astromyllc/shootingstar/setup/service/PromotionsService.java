package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.PromotionsRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.PromotionsResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.Promotions;
import com.astromyllc.shootingstar.setup.repository.PromotionsRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.PromotionsServiceInterface;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import com.astromyllc.shootingstar.setup.utils.PromotionsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionsService implements PromotionsServiceInterface {
    private final PromotionsRepository promotionsRepository;

    private static final Map<String, Institution> INSTITUTION_MAP = InstitutionUtils.institutionGlobalList.stream()
            .collect(Collectors.toMap(
                    inst -> inst.getBececode().toLowerCase(),
                    Function.identity()
            ));

    @Override
    public List<Optional<PromotionsResponse>> createPromotions(PromotionsRequest promotionsRequest) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(promotionsRequest.getInstitution()))
                .findFirst() // Find the first matching institution
                .map(inst -> {
                    // Ensure the class list is not null, then process it directly
                    inst.setPromotions(Optional.ofNullable(inst.getPromotions()).orElse(new ArrayList<>()));

                    // Convert existing class names into a Set for quick lookup
                    Set<String> existingPromotionSetting = inst.getPromotions().stream()
                            .map(ps -> ps.getCurrentClass().toLowerCase())
                            .collect(Collectors.toSet());

                    // Filter out existing classes and add only new ones
                    List<Promotions> newPromotionSetting =
                            promotionsRequest.getPromotionsRequestDetailsList().stream()
                                    .map(ps -> {
                                        Promotions mappedPromotions = PromotionsUtil.mapPromotionRequestToPromotionSettings(ps);
                                        mappedPromotions.setInstitution(inst);  // Set the institution reference
                                        return mappedPromotions;
                                    })
                                    .filter(c -> !existingPromotionSetting.contains(c.getCurrentClass().toLowerCase()))
                                    .toList();

                    // Save the updated institution with the new class list
                    promotionsRepository.saveAll(newPromotionSetting);
                    inst.getPromotions().addAll(newPromotionSetting);

                    // Return the list of class responses wrapped in Optional
                    return inst.getPromotions().stream()
                            .map(PromotionsUtil::mapPromotionsToPromotionResponse)
                            .collect(Collectors.toList());
                })
                .map(ArrayList::new) // Collect into a List<Optional<ClassesResponse>>
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<Optional<PromotionsResponse>>(); // Return an empty list if the institution is not found
                });
    }

    @Override
    public List<Optional<PromotionsResponse>> getInstitutionPromotionSetup(SingleStringRequest beceCode) {
        if (beceCode == null || beceCode.getVal() == null) {
            return Collections.emptyList();
        }

        return Optional.ofNullable(INSTITUTION_MAP.get(beceCode.getVal().toLowerCase()))
                .map(Institution::getPromotions)
                .stream()
                .flatMap(List::stream)
                .map(PromotionsUtil::mapPromotionsToPromotionResponse)
                .filter(Objects::nonNull) // Safely handle null mappings
                .toList();
    }
}
