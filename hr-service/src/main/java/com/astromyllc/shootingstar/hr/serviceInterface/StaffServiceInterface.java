package com.astromyllc.shootingstar.hr.serviceInterface;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffPermissionsRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffSubjectsRequest;
import com.astromyllc.shootingstar.hr.dto.request.api.StaffCodeRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public interface StaffServiceInterface {
    public Optional<StaffResponse> createStaff(List<StaffRequest> staffRequest) throws IOException, URISyntaxException;
    Optional<List<StaffResponse>> createStaffList(List<StaffRequest> staffRequest);
    public Optional<List<StaffResponse>> createStaffs(List<StaffRequest> staffRequestList);
    public Optional<StaffResponse> getStaffByCode(StaffCodeRequest staffCode) throws URISyntaxException, IOException;
    public Optional<List<StaffResponse>> getStaffByInstitution(SingleStringRequest beceCode);
    public Optional<List<StaffResponse>> getStaffByInstitutionAndDesignation(String beceCode, String designation);
    Optional<StaffResponse> addStaffPermissions(List<StaffPermissionsRequest> staffPermissionsRequests) throws IOException, URISyntaxException;
    Optional<StaffResponse> addStaffSubjects(List<StaffSubjectsRequest> staffSubjectsRequests) throws IOException, URISyntaxException;
    Optional<StaffResponse> getStaffByStaffCode(SingleStringRequest staffcodeRequest) throws IOException, URISyntaxException;
}
