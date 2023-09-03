package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
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
import java.util.List;
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
    public void submitExamsAssessment(ExamsAssessmentRequest examsAssessmentRequest) {
        ExamsAssessment ea= examsAssessmentUtil.mapExamsAssessmentRequest_ToExamsAssessment(examsAssessmentRequest);
        examsAssessmentRepository.save(ea);
        examsAssessmentUtil.examsAssessmentGlobalList.add(ea);

    }

    @Override
    public void submitExamsAssessments(List<ExamsAssessmentRequest> examsAssessmentRequests) {
        List <ExamsAssessment> ea= examsAssessmentRequests.stream().map(ear->examsAssessmentUtil.mapExamsAssessmentRequest_ToExamsAssessment(ear)).collect(Collectors.toList());
        examsAssessmentRepository.saveAll(ea);
        examsAssessmentUtil.examsAssessmentGlobalList.addAll(ea);

    }

    @Override
    public List<ExamsAssessmentResponse> getExamsAssessmentByStudent(ExamsAssessmentRequest examsAssessmentRequest) {
        List <ExamsAssessment> ea=examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                car->car.getStudentId().equals(examsAssessmentRequest.getStudentId())
                        && car.getSubject().equals(examsAssessmentRequest.getSubject())
                        && car.getTerm().equals(examsAssessmentRequest.getTerm())
                        && car.getDateTime().toLocalDate().equals(LocalDateTime.parse(examsAssessmentRequest.getDateTime(),formatter).toLocalDate())).collect(Collectors.toList());
        return ea.stream().map(car->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(car)).collect(Collectors.toList());
    }

    @Override
    public List<ExamsAssessmentResponse> getExamsAssessmentByClass(ExamsAssessmentRequest examsAssessmentRequest) {
        List <ExamsAssessment> ea=examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                car->car.getSubject().equals(examsAssessmentRequest.getSubject())
                        && car.getTerm().equals(examsAssessmentRequest.getTerm())
                        && car.getDateTime().toLocalDate().equals(LocalDateTime.parse(examsAssessmentRequest.getDateTime(),formatter).toLocalDate())).collect(Collectors.toList());
        return ea.stream().map(ear->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(ear)).collect(Collectors.toList());

    }
}
