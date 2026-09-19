package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.paystack.CustomField;
import com.astromyllc.shootingstar.setup.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.PreOrderInstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.SkimpInstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.UpgradeQuoteResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.InstitutionAccount;
import com.astromyllc.shootingstar.setup.model.PreOrderInstitution;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.PreOrderInstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.InstitutionServiceInterface;
import com.astromyllc.shootingstar.setup.subscription.PricingResult;
import com.astromyllc.shootingstar.setup.subscription.SubscriptionPlan;
import com.astromyllc.shootingstar.setup.subscription.SubscriptionPricingCalculator;
import com.astromyllc.shootingstar.setup.utils.InstitutionAccountUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InstitutionService implements InstitutionServiceInterface {
    private final InstitutionRepository institutionRepository;
    private final PreOrderInstitutionRepository preOrderInstitutionRepository;
    private final InstitutionUtils institutionUtils;
    private final InstitutionAccountUtil institutionAccountUtil;
    private final SubscriptionPricingCalculator subscriptionPricingCalculator;

    @Override
    public InstitutionResponse createInstitution(InstitutionRequest institutionRequest) throws IOException {
        Optional<Institution> institution = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(institutionRequest.getBececode())).findFirst();
        Institution institution1 = new Institution();
        if (institution.isEmpty()) {
            institution1 = institutionUtils.mapInstitutionRequest_ToInstitution(institutionRequest);
            institutionRepository.save(institution1);

            InstitutionUtils.institutionGlobalList.add(institution1);
            log.info("Institution {} Saved Successfully", institution1.getIdInstitution());
        } else {
            Institution institution2 = institutionUtils.mapInstitutionRequestToInstitution(institution.get(), institutionRequest);
            institutionRepository.save(institution2);
            return institutionUtils.mapInstitutionToInstitutionResponse(institution2);
        }
        return institutionUtils.mapInstitutionToInstitutionResponse(institution1);
    }

    @Override
    public InstitutionResponse migratePreOrder(String institutionCode) throws IOException {
        Optional<PreOrderInstitution> institution = InstitutionUtils.preOrderInstitutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(institutionCode)).findFirst();
        Institution institution1 = new Institution();
        if (institution.isPresent()) {
            log.info("NAME OF NEWEST INSTITUTION IS ..." + institution.get());
            Institution institution2 = institutionUtils.mapPreorderInstitutionToInstitution(institution.get());
            institutionRepository.save(institution2);
            InstitutionUtils.institutionGlobalList.add(institution2);
            return institutionUtils.mapInstitutionToInstitutionResponse(institution2);
        }
        return institutionUtils.mapInstitutionToInstitutionResponse(institution1);
    }

    @Override
    public String createPreOrderInstitution(PreOrderInstitutionRequest institutionRequest) throws IOException {
        // Optional <PreOrderInstitution> institution = institutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(institutionRequest.getBececode())).findFirst();
        PreOrderInstitution institution1 = new PreOrderInstitution();
        institution1 = institutionUtils.mapPreOrderInstitutionRequest_ToPreOrderInstitution(institutionRequest);
        preOrderInstitutionRepository.save(institution1);
        institutionUtils.createKeycloakCredentials(institution1);
        InstitutionUtils.preOrderInstitutionGlobalList.add(institution1);
        log.info("Institution {} Saved Successfully", institution1.getIdInstitution());

        return "Request Processed";
    }

    @Override
    public Optional<InstitutionResponse> getInstitutionByBeceCode(SingleStringRequest beceCode) throws IOException {
        String finalBeceCode = beceCode.getVal();
        List<Institution> ii = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(finalBeceCode)).toList();
        if (ii.size() == 0) {
            log.info("NEWEST INSTITUTION CODE = " + finalBeceCode);
            migratePreOrder(finalBeceCode);
            return Optional.ofNullable(institutionUtils.mapInstitutionToInstitutionResponse(InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(finalBeceCode)).findFirst().get()));
        }
        return Optional.ofNullable(institutionUtils.mapInstitutionToInstitutionResponse(ii.get(0)));
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        try {
            return Optional.of(InstitutionUtils.institutionGlobalList.stream()
                    .filter(i -> i.getGradingSetting() != null && i.getClassList() != null && i.getSubjectList() != null && i.getDepartmentList() != null)
                    .map(institution -> {
                        try {
                            return institutionUtils.mapInstitutionToInstitutionResponse(institution);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to map institution: " + institution.getIdInstitution(), e);
                        }
                    })
                    .toList());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                // Handle the IOException case specifically
                log.error("IO error processing institutions: {}", e.getMessage());
                return Optional.empty(); // or return some default value
            }
            throw e; // Re-throw if it's a different RuntimeException
        }
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllinstitutionForWeb() {
        try {
            return Optional.of(InstitutionUtils.institutionGlobalList.stream()
                    .filter(i -> i.getGradingSetting() != null && i.getClassList() != null && i.getSubjectList() != null && i.getDepartmentList() != null)
                    .map(institution -> {
                        try {
                            return institutionUtils.mapInstitutionToInstitutionResponseForWeb(institution);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to map institution: " + institution.getIdInstitution(), e);
                        }
                    })
                    .toList());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                // Handle the IOException case specifically
                log.error("IO error processing institutions: {}", e.getMessage());
                return Optional.empty(); // or return some default value
            }
            throw e; // Re-throw if it's a different RuntimeException
        }
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllSubscribedInstitution() {
        try {
            return Optional.of(InstitutionUtils.institutionGlobalList.stream()
                    .filter(i -> i.getGradingSetting() != null && i.getClassList() != null && i.getSubjectList() != null && i.getDepartmentList() != null)
                    .map(institution -> {
                        try {
                            return institutionUtils.mapInstitutionToInstitutionResponse(institution, " ");
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to map institution: " + institution.getIdInstitution(), e);
                        }
                    })
                    .toList());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                // Handle the IOException case specifically
                log.error("IO error processing institutions: {}", e.getMessage());
                return Optional.empty(); // or return some default value
            }
            throw e; // Re-throw if it's a different RuntimeException
        }
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByPopulation(int population) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByCountry(String country) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByStreams(int stream) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByCity(String city) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByRegion(String region) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByPackage(String subscription) {
        return Optional.empty();
    }

    @Override
    public Optional<List<PreOrderInstitutionResponse>> getAllPreOrderedInstitution() {
        return Optional.of(InstitutionUtils.preOrderInstitutionGlobalList.stream()
                .map(institution -> {
                    try {
                        return institutionUtils.mapPreOrderInstitutionToPreOrderInstitutionResponse(institution);
                    } catch (IOException e) {
                        // Log and return null for failed mappings
                        log.warn("Skipping pre-order institution {} due to IO error: {}", institution.getIdInstitution(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull) // Remove null values from the stream
                .toList());
    }

    @Override
    public Optional<SkimpInstitutionResponse> getInstitutionStatus(SingleStringRequest beceCode) throws IOException {
        String finalBeceCode = beceCode.getVal();
        List<Institution> ii = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(finalBeceCode)).toList();
        if (ii.size() < 1) {
            migratePreOrder(finalBeceCode);
        }
        List<Institution> iii = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(finalBeceCode)).toList();
        ii = iii;
        return Optional.ofNullable(institutionUtils.mapInstitutionToSkimpInstitutionResponse(ii.get(0)));
    }

    @Override
    public Optional<String> reactivateInstitutionalAccount(PaystackPaymentResponse paystack) {
        String paymentStatus = paystack.getData().getStatus();
        Double paymentAmount = paystack.getData().getAmount();
        String reference = paystack.getData().getReference();
        String institutionCode = paystack.getData().getMetadata().getCustomFields().get(0).getValue();

        Institution institution = InstitutionUtils.institutionGlobalList.stream()
                .filter(inst -> inst.getBececode().equalsIgnoreCase(institutionCode))
                .findFirst()
                .orElse(null);

        if (institution == null) {
            log.warn("Webhook for unknown institution code {} (reference={})", institutionCode, reference);
            return Optional.empty();
        }

        if ("active".equalsIgnoreCase(institution.getStatus())) {
            log.info("Ignoring duplicate webhook — institution {} already active (reference={})", institutionCode, reference);
            return Optional.of("Account already active for " + institutionCode);
        }

        SkimpInstitutionResponse si = institutionUtils.mapInstitutionToSkimpInstitutionResponse(institution);

        if (paymentStatus == null || paymentAmount == null
                || !paymentStatus.equalsIgnoreCase("success")
                || paymentAmount.compareTo(si.getPendingBill()) < 0) {
            log.warn("Ignoring webhook: status={}, amount={}, expected={}, reference={}",
                    paymentStatus, paymentAmount, si.getPendingBill(), reference);
            return Optional.empty();
        }

        List<InstitutionAccount> sa = new ArrayList<>();
        sa.add(InstitutionAccountUtil.mapInstitutionAccountRequest_ToInstitutionAccount(
                new InstitutionAccountRequest(institutionCode, "active"), institutionCode));
        institutionAccountUtil.saveAll(sa);
        institution.setStatus("active");
        institutionRepository.save(institution);

        return Optional.of("Account Reactivated for " + institutionCode);
    }

    @Override
    public Long getInstitutionPopulation(SingleStringRequest institutionCode) throws IOException {
        Institution institution = InstitutionUtils.institutionGlobalList.stream()
                .filter(inst -> inst.getBececode().equalsIgnoreCase(institutionCode.getVal()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown institution code " + institutionCode.getVal()));

        return institutionUtils.getPopulation(institution.getBececode());
    }

    @Override
    public UpgradeQuoteResponse getUpgradeQuote(UpgradeQuoteRequest request) throws IOException {
        Institution institution = InstitutionUtils.institutionGlobalList.stream()
                .filter(inst -> inst.getBececode().equalsIgnoreCase(request.getInstitutionCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown institution code " + request.getInstitutionCode()));

        SubscriptionPlan currentPlan = SubscriptionPlan.fromLabel(institution.getSubscription());
        SubscriptionPlan targetPlan = SubscriptionPlan.fromLabel(request.getTargetPlan());

        if (!targetPlan.isHigherThan(currentPlan)) {
            throw new IllegalArgumentException(
                    "Institution " + request.getInstitutionCode() + " is already on " + currentPlan
                            + "; cannot \"upgrade\" to " + targetPlan + ".");
        }

        long population = institutionUtils.getPopulation(institution.getBececode());
        PricingResult pricing = subscriptionPricingCalculator.calculate(population, targetPlan);

        return UpgradeQuoteResponse.builder()
                .institutionCode(institution.getBececode())
                .currentPlan(currentPlan.name())
                .targetPlan(targetPlan.name())
                .population(pricing.population())
                .ratePerStudent(pricing.ratePerStudent())
                .totalAmount(pricing.totalAmount())
                .currency("GHS")
                .amountInSubunit(pricing.totalAmount().multiply(BigDecimal.valueOf(100)).longValueExact())
                .build();
    }

    @Override
    public Optional<String> upgradeSubscriptionPaymentStatus(PaystackPaymentResponse paystack) throws IOException {
        String paymentStatus = paystack.getData().getStatus();
        Double paymentAmount = paystack.getData().getAmount();
        String reference = paystack.getData().getReference();
        List<CustomField> customFields =
                paystack.getData().getMetadata() != null ? paystack.getData().getMetadata().getCustomFields() : null;

        String institutionCode = findCustomFieldValue(customFields, "institutionCode");
        String targetPlanRaw = findCustomFieldValue(customFields, "targetPlan");

        if (institutionCode == null || targetPlanRaw == null) {
            log.warn("Upgrade webhook missing institutionCode/targetPlan in metadata (reference={})", reference);
            return Optional.empty();
        }

        Institution institution = InstitutionUtils.institutionGlobalList.stream()
                .filter(inst -> inst.getBececode().equalsIgnoreCase(institutionCode))
                .findFirst()
                .orElse(null);

        if (institution == null) {
            log.warn("Upgrade webhook for unknown institution code {} (reference={})", institutionCode, reference);
            return Optional.empty();
        }

        SubscriptionPlan currentPlan = SubscriptionPlan.fromLabel(institution.getSubscription());
        SubscriptionPlan targetPlan = SubscriptionPlan.fromLabel(targetPlanRaw);

        if (!targetPlan.isHigherThan(currentPlan)) {
            log.info("Ignoring duplicate/stale upgrade webhook — institution {} already on {} (reference={})",
                    institutionCode, currentPlan, reference);
            return Optional.of("Institution " + institutionCode + " is already on " + currentPlan + " or higher");
        }

        // Recompute population and the expected charge fresh, server-side - never trust a
        // client-supplied amount, same principle as reactivateInstitutionalAccount above.
        long population;
        PricingResult expected;
        try {
            population = institutionUtils.getPopulation(institutionCode);
            expected = subscriptionPricingCalculator.calculate(population, targetPlan);
        } catch (IllegalArgumentException e) {
            log.warn("Cannot verify upgrade charge for {} (population invalid, e.g. zero/negative): {}",
                    institutionCode, e.getMessage());
            return Optional.empty();
        }

        long expectedSubunit = expected.totalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();
        long paidSubunit = paymentAmount == null ? -1 : Math.round(paymentAmount);

        if (paymentStatus == null || !paymentStatus.equalsIgnoreCase("success") || paidSubunit < expectedSubunit) {
            log.warn("Ignoring upgrade webhook: status={}, paidSubunit={}, expectedSubunit={}, reference={}",
                    paymentStatus, paidSubunit, expectedSubunit, reference);
            return Optional.empty();
        }

        institution.setSubscription(targetPlan.storageLabel());
        institutionRepository.save(institution);

        // Fuse this into the same annual-payment tracking reactivateInstitutionalAccount
        // already uses (see Cron#updateInstitutionStatus): it looks for an
        // InstitutionAccount record dated within the current Sept-Aug payment period to
        // decide whether an institution has paid this year. Without logging one here, a
        // mid-year upgrade payment would be invisible to that check, and the institution
        // could still get suspended in September/March despite having just paid.
        List<InstitutionAccount> sa = new ArrayList<>();
        sa.add(InstitutionAccountUtil.mapInstitutionAccountRequest_ToInstitutionAccount(
                new InstitutionAccountRequest(institutionCode, "active"), institutionCode));
        institutionAccountUtil.saveAll(sa);

        log.info("Institution {} upgraded {} -> {} (population={}, amount={} GHS, reference={})",
                institutionCode, currentPlan, targetPlan, population, expected.totalAmount(), reference);

        return Optional.of("Plan upgraded to " + targetPlan + " for " + institutionCode);
    }

    private String findCustomFieldValue(List<CustomField> customFields, String variableName) {
        if (customFields == null) {
            return null;
        }
        return customFields.stream()
                .filter(cf -> variableName.equalsIgnoreCase(cf.getVariableName()))
                .map(CustomField::getValue)
                .findFirst()
                .orElse(null);
    }

}
