package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.request.AdmissionRequest;

import java.util.Optional;

public interface StudentServiceInterface {

    public void fetchCurrentApplications(AdmissionRequest admissionRequest);
}
