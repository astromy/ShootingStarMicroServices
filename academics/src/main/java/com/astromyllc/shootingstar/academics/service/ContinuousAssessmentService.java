package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.academics.dto.response.ContinuousAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.repository.ContinuousAssessmentRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.ContinuousAssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.ContinuousAssessmentUtil;
import com.astromyllc.shootingstar.academics.util.ExamsAssessmentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContinuousAssessmentService implements ContinuousAssessmentServiceInterface {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ContinuousAssessmentRepository continuousAssessmentRepository;
    private final ContinuousAssessmentUtil continuousAssessmentUtil;
    private final ExamsAssessmentUtil examsAssessmentUtil;

    @Override
    public Optional<ContinuousAssessmentResponse> submitContinuousAssessment(ContinuousAssessmentRequest continuousAssessmentRequest) {
        ContinuousAssessment ca = continuousAssessmentUtil.mapContinuousAssessmentRequest_ToContinuousAssessment(continuousAssessmentRequest);
        continuousAssessmentRepository.save(ca);
        ContinuousAssessmentUtil.continuousAssessmentGlobalList.add(ca);
        return Optional.empty();
    }

    @Override
    public Optional<ContinuousAssessmentResponse> submitContinuousAssessments(List<ContinuousAssessmentRequest> continuousAssessmentRequest) {
        // Fast empty check
        if (continuousAssessmentRequest == null || continuousAssessmentRequest.isEmpty()) {
            return Optional.empty();
        }

        // Extract first request parameters for batch query
        ContinuousAssessmentRequest firstRequest = continuousAssessmentRequest.get(0);
        String institutionCode = firstRequest.getInstitutionCode();
        String studentClass = firstRequest.getStudentClass();

        // Batch fetch students using parallel stream
        Set<String> validStudentIds = examsAssessmentUtil.fetchStudentsByClass(studentClass, institutionCode)
                .parallelStream()
                .map(ClassListResponse::getStudentID)
                .collect(Collectors.toCollection(HashSet::new));

        // Parallel processing of valid requests
        List<ContinuousAssessment> newAssessments = continuousAssessmentRequest.parallelStream()
                .filter(request -> validStudentIds.contains(request.getStudentId()))
                .map(continuousAssessmentUtil::mapContinuousAssessmentRequest_ToContinuousAssessment)
                .collect(Collectors.toList());

        // Batch save with size optimization
        List<ContinuousAssessment> savedAssessments = continuousAssessmentRepository.saveAll(newAssessments);

        // Get current timestamp once
        LocalDateTime now = LocalDateTime.now();

        // Optimized map update with batching
        Map<String, ContinuousAssessment> latestMap = ContinuousAssessmentUtil.continuousAssessmentLatestMap;
        List<ContinuousAssessment> latestRecords = new ArrayList<>(savedAssessments.size());

        for (ContinuousAssessment newCA : savedAssessments) {
            String key = continuousAssessmentUtil.buildCAKey(newCA);
            ContinuousAssessment existing = latestMap.get(key);

            if (existing == null || newCA.getDateTime().isAfter(existing.getDateTime())) {
                latestMap.put(key, newCA);
                latestRecords.add(newCA);
            }
        }

        // Atomic global list update (minimize synchronization)
        if (!latestRecords.isEmpty()) {
            synchronized (ContinuousAssessmentUtil.class) {
                ContinuousAssessmentUtil.continuousAssessmentGlobalList.addAll(latestRecords);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByStudent(ContinuousAssessmentRequest continuousAssessmentRequest) {
        List<ContinuousAssessment> ca = ContinuousAssessmentUtil.continuousAssessmentGlobalList.stream().filter(
                car -> car.getStudentId().equalsIgnoreCase(continuousAssessmentRequest.getStudentId())
                        && car.getSubject().equals(continuousAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(continuousAssessmentRequest.getTerm())
                        && car.getStudentClass().equalsIgnoreCase(continuousAssessmentRequest.getStudentClass())
                        && car.getAcademicYear().equalsIgnoreCase(continuousAssessmentRequest.getAcademicYear())
                // && car.getDateTime().toLocalDate().equalsIgnoreCase(LocalDateTime.parse(continuousAssessmentRequest.getDateTime(),formatter).toLocalDate())
        ).toList();
        return ca.stream().map(continuousAssessmentUtil::mapContinuousAssessment_ToContinuousAssessmentResponse).toList();

    }

    @Override
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByClass(ContinuousAssessmentRequest continuousAssessmentRequest) {
        List<ContinuousAssessment> ca = ContinuousAssessmentUtil.continuousAssessmentGlobalList.stream().filter(
                car -> car.getSubject().equals(continuousAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(continuousAssessmentRequest.getTerm())
                        && car.getStudentClass().equalsIgnoreCase(continuousAssessmentRequest.getStudentClass())
                        && car.getAcademicYear().equalsIgnoreCase(continuousAssessmentRequest.getAcademicYear())
                //&& car.getDateTime().toLocalDate().equalsIgnoreCase(LocalDateTime.parse(continuousAssessmentRequest.getDateTime(),formatter).toLocalDate())
        ).toList();
        return ca.stream().map(continuousAssessmentUtil::mapContinuousAssessment_ToContinuousAssessmentResponse).toList();

    }
}
