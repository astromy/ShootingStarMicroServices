package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.AcademicRecordsRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffDocumentsRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffDocumentsResponse;
import com.astromyllc.shootingstar.hr.model.AcademicRecords;
import com.astromyllc.shootingstar.hr.model.ProfessionalRecords;
import com.astromyllc.shootingstar.hr.model.StaffDesignation;
import com.astromyllc.shootingstar.hr.model.StaffDocuments;
import com.astromyllc.shootingstar.hr.repository.StaffDocumentsRepository;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffDocumentsUtil {
    private final StaffDocumentsRepository staffDocumentsRepository;
    public static List<StaffDocuments> staffDocumentsGlobalList;

    @Bean
    private void fetchAllStaffDocs() {
        staffDocumentsGlobalList = staffDocumentsRepository.findAll();
        log.info("{} RECORDS OF STAFF DOCUMENTS FETCHED",staffDocumentsGlobalList.size());
    }

    public static StaffDocuments mapStaffDocumentsRequest_ToStaffDocuents(StaffDocumentsRequest sd, String staffCode) {
        return StaffDocuments.builder()
                .document(sd.getDocument())
                .documentExtension(sd.getDocumentExtension())
                .documentType(sd.getDocumentType())
                .staffDocs(staffCode)
                .institutionCode(sd.getInstitutionCode())
                .build();
    }

    public static StaffDocumentsResponse mapStaffDocuments_ToStaffDocuentsResponse(StaffDocuments sd) {
        return StaffDocumentsResponse.builder()
                .id(String.valueOf(sd.getId()))
                .document(sd.getDocument())
                .documentExtension(sd.getDocumentExtension())
                .documentType(sd.getDocumentType())
                .build();
    }

    public void saveAll(List<StaffDocuments> sds) {
        staffDocumentsRepository.saveAll(sds);
        staffDocumentsGlobalList.addAll(sds);
    }


    public void updateStaffDocuments(StaffDocuments existing, StaffDocumentsRequest requestRecord, String staffCode) {
        existing.setDocument(requestRecord.getDocument());
        existing.setDocumentType(requestRecord.getDocumentType());
        existing.setDocumentExtension(requestRecord.getDocumentExtension());
        existing.setInstitutionCode(requestRecord.getInstitutionCode());
        existing.setStaffDocs(requestRecord.getStaffDocs());
    }
}
