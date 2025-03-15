package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ContinuousAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.repository.ContinuousAssessmentRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.ContinuousAssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.ContinuousAssessmentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContinuousAssessmentService implements ContinuousAssessmentServiceInterface {
    private final ContinuousAssessmentRepository continuousAssessmentRepository;
    private final ContinuousAssessmentUtil continuousAssessmentUtil;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Optional<ContinuousAssessmentResponse> submitContinuousAssessment(ContinuousAssessmentRequest continuousAssessmentRequest) {
        ContinuousAssessment ca = continuousAssessmentUtil.mapContinuousAssessmentRequest_ToContinuousAssessment(continuousAssessmentRequest);
        continuousAssessmentRepository.save(ca);
        ContinuousAssessmentUtil.continuousAssessmentGlobalList.add(ca);
        return Optional.empty();
    }

    @Override
    public Optional<ContinuousAssessmentResponse> submitContinuousAssessments(List<ContinuousAssessmentRequest> continuousAssessmentRequest) {

   /*     List<ContinuousAssessment> ca = continuousAssessmentRequest.stream()
                .map(continuousAssessmentUtil::mapContinuousAssessmentRequest_ToContinuousAssessment).toList();*/

        Map<Boolean, List<ContinuousAssessment>> partitioned = continuousAssessmentRequest.stream()
                .map(continuousAssessmentUtil::mapContinuousAssessmentRequest_ToContinuousAssessment)
                .collect(Collectors.partitioningBy(ca -> ContinuousAssessmentUtil.continuousAssessmentGlobalList.stream()
                        .anyMatch(existingCa -> existingCa.getStudentId().equals(ca.getStudentId()) &&
                                existingCa.getInstitutionCode().equals(ca.getInstitutionCode()) &&
                                existingCa.getTerm().equals(ca.getTerm()) &&
                                existingCa.getSubject().equals(ca.getSubject()) &&
                                existingCa.getAcademicYear().equals(ca.getAcademicYear()))));

        List<ContinuousAssessment> existingRecords = partitioned.get(true);  // Existing records
        List<ContinuousAssessment> newRecords = partitioned.get(false);     // New records

        continuousAssessmentRepository.saveAll(existingRecords);
        continuousAssessmentRepository.saveAll(newRecords);
        ContinuousAssessmentUtil.continuousAssessmentGlobalList.addAll(newRecords);
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
