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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    public void submitContinuousAssessment(ContinuousAssessmentRequest continuousAssessmentRequest) {
        ContinuousAssessment ca = continuousAssessmentUtil.mapContinuousAssessmentRequest_ToContinuousAssessment(continuousAssessmentRequest);
        continuousAssessmentRepository.save(ca);
        continuousAssessmentUtil.continuousAssessmentGlobalList.add(ca);
    }

    @Override
    public void submitContinuousAssessments(List<ContinuousAssessmentRequest> continuousAssessmentRequest) {
        List<ContinuousAssessment> ca = continuousAssessmentRequest.stream().map(car -> continuousAssessmentUtil.mapContinuousAssessmentRequest_ToContinuousAssessment(car)).collect(Collectors.toList());
        continuousAssessmentRepository.saveAll(ca);
        continuousAssessmentUtil.continuousAssessmentGlobalList.addAll(ca);
    }

    @Override
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByStudent(ContinuousAssessmentRequest continuousAssessmentRequest) {
        List<ContinuousAssessment> ca = continuousAssessmentUtil.continuousAssessmentGlobalList.stream().filter(
                car -> car.getStudentId().equalsIgnoreCase(continuousAssessmentRequest.getStudentId())
                        && car.getSubject().equals(continuousAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(continuousAssessmentRequest.getTerm())
                        && car.getStudentClass().equalsIgnoreCase(continuousAssessmentRequest.getStudentClass())
                        && car.getAcademicYear().equalsIgnoreCase(continuousAssessmentRequest.getAcademicYear())
                // && car.getDateTime().toLocalDate().equalsIgnoreCase(LocalDateTime.parse(continuousAssessmentRequest.getDateTime(),formatter).toLocalDate())
        ).collect(Collectors.toList());
        return ca.stream().map(car -> continuousAssessmentUtil.mapContinuousAssessment_ToContinuousAssessmentResponse(car)).collect(Collectors.toList());

    }

    @Override
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByClass(ContinuousAssessmentRequest continuousAssessmentRequest) {
        List<ContinuousAssessment> ca = continuousAssessmentUtil.continuousAssessmentGlobalList.stream().filter(
                car -> car.getSubject().equals(continuousAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(continuousAssessmentRequest.getTerm())
                        && car.getStudentClass().equalsIgnoreCase(continuousAssessmentRequest.getStudentClass())
                        && car.getAcademicYear().equalsIgnoreCase(continuousAssessmentRequest.getAcademicYear())
                //&& car.getDateTime().toLocalDate().equalsIgnoreCase(LocalDateTime.parse(continuousAssessmentRequest.getDateTime(),formatter).toLocalDate())
        ).collect(Collectors.toList());
        return ca.stream().map(car -> continuousAssessmentUtil.mapContinuousAssessment_ToContinuousAssessmentResponse(car)).collect(Collectors.toList());

    }
}
