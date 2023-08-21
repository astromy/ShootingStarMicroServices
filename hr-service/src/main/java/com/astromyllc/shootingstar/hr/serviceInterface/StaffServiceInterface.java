package com.astromyllc.shootingstar.hr.serviceInterface;

import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.api.StaffCodeRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface StaffServiceInterface {
    public StaffResponse createStaff(StaffRequest staffRequest) throws IOException, URISyntaxException;
    public List<StaffResponse> createStaffs(List<StaffRequest> staffRequestList);
    public StaffResponse getStaffByCode(StaffCodeRequest staffCode) throws URISyntaxException, IOException;
    public List<StaffResponse> getStaffByInstitution(String beceCode);
    public List<StaffResponse> getStaffByInstitutionAndDesignation(String beceCode, String designation);
}
