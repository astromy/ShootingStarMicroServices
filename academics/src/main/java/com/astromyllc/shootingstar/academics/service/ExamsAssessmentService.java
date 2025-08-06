package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ExamsAssessment;
import com.astromyllc.shootingstar.academics.repository.ExamsAssessmentRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsAssessmentServiceInterface;
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
public class ExamsAssessmentService implements ExamsAssessmentServiceInterface {
    private final ExamsAssessmentRepository examsAssessmentRepository;
    private final ExamsAssessmentUtil examsAssessmentUtil;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Optional<ExamsAssessmentResponse> submitExamsAssessment(ExamsAssessmentRequest examsAssessmentRequest) {
        ExamsAssessment ea= examsAssessmentUtil.mapExamsAssessmentRequest_ToExamsAssessment(examsAssessmentRequest);
        examsAssessmentRepository.save(ea);
        ExamsAssessmentUtil.examsAssessmentGlobalList.add(ea);
        return Optional.empty();
    }

    @Override
    public Optional<ExamsAssessmentResponse> submitExamsAssessments(List<ExamsAssessmentRequest> examsAssessmentRequests) {
        // Fast empty check
        if (examsAssessmentRequests == null || examsAssessmentRequests.isEmpty()) {
            return Optional.empty();
        }

        // Extract first request parameters for batch query
        ExamsAssessmentRequest firstRequest = examsAssessmentRequests.get(0);
        String institutionCode = firstRequest.getInstitutionCode();
        String studentClass = firstRequest.getStudentClass();

        // Batch fetch students using parallel stream
        Set<String> validStudentIds = examsAssessmentUtil.fetchStudentsByClass(studentClass, institutionCode)
                .parallelStream()
                .map(ClassListResponse::getStudentID)
                .collect(Collectors.toCollection(HashSet::new));

        // Parallel processing of valid requests
        List<ExamsAssessment> newAssessments = examsAssessmentRequests.parallelStream()
                .filter(request -> validStudentIds.contains(request.getStudentId()))
                .map(examsAssessmentUtil::mapExamsAssessmentRequest_ToExamsAssessment)
                .collect(Collectors.toList());

        // Batch save with size optimization
        List<ExamsAssessment> savedAssessments = examsAssessmentRepository.saveAll(newAssessments);

        // Get current timestamp once
        LocalDateTime now = LocalDateTime.now();

        // Optimized map update with batching
        Map<String, ExamsAssessment> latestMap = ExamsAssessmentUtil.examsAssessmentLatestMap;
        List<ExamsAssessment> latestRecords = new ArrayList<>(savedAssessments.size());

        for (ExamsAssessment newCA : savedAssessments) {
            String key = examsAssessmentUtil.buildCAKey(newCA);
            ExamsAssessment existing = latestMap.get(key);

            if (existing == null || newCA.getDateTime().isAfter(existing.getDateTime())) {
                latestMap.put(key, newCA);
                latestRecords.add(newCA);
            }
        }

        // Atomic global list update (minimize synchronization)
        if (!latestRecords.isEmpty()) {
            synchronized (ExamsAssessmentUtil.class) {
                ExamsAssessmentUtil.examsAssessmentGlobalList.addAll(latestRecords);
            }
        }

        return Optional.empty();
    }
    


    @Override
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByStudent(ExamsAssessmentRequest examsAssessmentRequest) {
        List <ExamsAssessment> ea= ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                car->car.getStudentId().equalsIgnoreCase(examsAssessmentRequest.getStudentId())
                        && car.getSubject().equals(examsAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(examsAssessmentRequest.getTerm())
                        && car.getDateTime().toLocalDate().equals(LocalDateTime.parse(examsAssessmentRequest.getDateTime(),formatter).toLocalDate())).toList();
        return ea.stream().map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList();
    }

    @Override
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByClass(ExamsAssessmentRequest examsAssessmentRequest) {
        List <ExamsAssessment> ea= ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                car->car.getSubject().equals(examsAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(examsAssessmentRequest.getTerm())
                        && car.getDateTime().toLocalDate().equals(LocalDateTime.parse(examsAssessmentRequest.getDateTime(),formatter).toLocalDate())).toList();
        return ea.stream().map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList();
    }

    /**
     *
     * @return a List of Exams results for all subjects of a given Student in a given Academic Term
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e-> ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentId().equalsIgnoreCase(ea.getStudentId())
                && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
                && e.getTerm().equalsIgnoreCase(ea.getTerm())
        ).map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList()).toList();
        return eax;
    }

    /**
     *
     * @return a List of Exams results for all subjects of a given Student in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e-> ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentId().equalsIgnoreCase(ea.getStudentId())
                        && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
        ).map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList()).toList();
        return eax;
    }

    /**
     *
     * @return a List of Exams results for all subjects of a given Student in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchStudentProgressionReport(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e-> ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentId().equalsIgnoreCase(ea.getStudentId())
        ).map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList()).toList();
        return eax;
    }

    /**
     *
     * @return a List of Exams results for all subjects of a given Class in a given Academic Term
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e-> ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentClass().equalsIgnoreCase(ea.getStudentClass())
                        && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
                        && e.getTerm().equalsIgnoreCase(ea.getTerm())
        ).map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList()).toList();
        return eax;
    }

    /**
     *
     * @return a List of Exams results for all subjects of a given Class in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e-> ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentClass().equalsIgnoreCase(ea.getStudentClass())
                        && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
        ).map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList()).toList();
        return eax;
    }

    /**
     *
     * @return a List of Exams results for all subjects of a given Class in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchPerformance(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e-> ExamsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentClass().equalsIgnoreCase(ea.getStudentClass())
        ).map(examsAssessmentUtil::mapExamsAssessment_ToExamsAssessmentResponse).toList()).toList();
        return eax;
    }
}
