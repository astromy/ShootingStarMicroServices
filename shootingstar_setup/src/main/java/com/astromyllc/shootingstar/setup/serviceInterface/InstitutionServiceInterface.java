package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.request.UpgradeQuoteRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.PreOrderInstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.SkimpInstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.UpgradeQuoteResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface InstitutionServiceInterface {
    public InstitutionResponse createInstitution(InstitutionRequest institutionRequest) throws IOException;

    public String createPreOrderInstitution(PreOrderInstitutionRequest institutionRequest) throws IOException;

    public Optional<InstitutionResponse> getInstitutionByBeceCode(SingleStringRequest beceCode) throws IOException;

    public Optional<List<InstitutionResponse>> getAllInstitution();

    public Optional<List<InstitutionResponse>> getAllInstitutionByPopulation(int population);

    public Optional<List<InstitutionResponse>> getAllInstitutionByCountry(String country);

    public Optional<List<InstitutionResponse>> getAllInstitutionByStreams(int stream);

    public Optional<List<InstitutionResponse>> getAllInstitutionByCity(String city);

    public Optional<List<InstitutionResponse>> getAllInstitutionByRegion(String region);

    public Optional<List<InstitutionResponse>> getAllInstitutionByPackage(String subscription);

    public InstitutionResponse migratePreOrder(String institutionCode) throws IOException;

    Optional<List<PreOrderInstitutionResponse>> getAllPreOrderedInstitution();

    Optional<SkimpInstitutionResponse> getInstitutionStatus(SingleStringRequest beceCode) throws IOException;

    Optional<String> reactivateInstitutionalAccount(PaystackPaymentResponse paystack);

    Optional<List<InstitutionResponse>> getAllSubscribedInstitution();

    Optional<List<InstitutionResponse>> getAllinstitutionForWeb();

    UpgradeQuoteResponse getUpgradeQuote(UpgradeQuoteRequest request) throws IOException;

    Optional<String> upgradeSubscriptionPaymentStatus(PaystackPaymentResponse paystack) throws IOException;

    /**
     * Live student count for an institution, straight from the administration-pta
     * service (same source getUpgradeQuote uses internally). Unlike getUpgradeQuote,
     * this has no "must be an actual upgrade" restriction, so it's safe to call any
     * time the review step just needs to display the current population.
     */
    Long getInstitutionPopulation(SingleStringRequest institutionCode) throws IOException;
}
