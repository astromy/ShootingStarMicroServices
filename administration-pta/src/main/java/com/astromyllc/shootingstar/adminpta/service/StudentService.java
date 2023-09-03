package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.AdmissionRequest;
import com.astromyllc.shootingstar.adminpta.repository.StudentRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.StudentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentService implements StudentServiceInterface {
    private  final StudentRepository studentRepository;
    private final StudentUtil studentUtil;
    @Override
    public void fetchCurrentApplications(AdmissionRequest admissionRequest) {

    }
}
