package com.astromyllc.shootingstar.hr.serviceInterface;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.api.StaffCodeRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public interface StaffServiceInterface {
    public Optional<StaffResponse> createStaff(StaffRequest staffRequest) throws IOException, URISyntaxException;
    public Optional<List<StaffResponse>> createStaffs(List<StaffRequest> staffRequestList);
    public Optional<StaffResponse> getStaffByCode(StaffCodeRequest staffCode) throws URISyntaxException, IOException;
    public Optional<List<StaffResponse>> getStaffByInstitution(SingleStringRequest beceCode);
    public Optional<List<StaffResponse>> getStaffByInstitutionAndDesignation(String beceCode, String designation);
}
