package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ParentServiceInterface {

    Optional<String> activateStudentAccount(DynamicStringRequest request);
}
