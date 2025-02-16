package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
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
import java.util.List;
import java.util.Optional;
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
        examsAssessmentUtil.examsAssessmentGlobalList.add(ea);
        return Optional.empty();
    }

    @Override
    public Optional<ExamsAssessmentResponse> submitExamsAssessments(List<ExamsAssessmentRequest> examsAssessmentRequests) {
        List <ExamsAssessment> ea= examsAssessmentRequests.stream().map(examsAssessmentUtil::mapExamsAssessmentRequest_ToExamsAssessment).collect(Collectors.toList());
        examsAssessmentRepository.saveAll(ea);
        examsAssessmentUtil.examsAssessmentGlobalList.addAll(ea);
        return Optional.empty();
    }

    @Override
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByStudent(ExamsAssessmentRequest examsAssessmentRequest) {
        List <ExamsAssessment> ea=examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                car->car.getStudentId().equalsIgnoreCase(examsAssessmentRequest.getStudentId())
                        && car.getSubject().equals(examsAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(examsAssessmentRequest.getTerm())
                        && car.getDateTime().toLocalDate().equals(LocalDateTime.parse(examsAssessmentRequest.getDateTime(),formatter).toLocalDate())).collect(Collectors.toList());
        return ea.stream().map(car->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(car)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByClass(ExamsAssessmentRequest examsAssessmentRequest) {
        List <ExamsAssessment> ea=examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                car->car.getSubject().equals(examsAssessmentRequest.getSubject())
                        && car.getTerm().equalsIgnoreCase(examsAssessmentRequest.getTerm())
                        && car.getDateTime().toLocalDate().equals(LocalDateTime.parse(examsAssessmentRequest.getDateTime(),formatter).toLocalDate())).collect(Collectors.toList());
        return ea.stream().map(ear->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(ear)).collect(Collectors.toList());
    }

    /**
     *
     * @param examsAssessmentRequest
     * @return a List of Exams results for all subjects of a given Student in a given Academic Term
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e->examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentId().equalsIgnoreCase(ea.getStudentId())
                && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
                && e.getTerm().equalsIgnoreCase(ea.getTerm())
        ).map(r->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(r)).collect(Collectors.toList())).collect(Collectors.toList());
        return eax;
    }

    /**
     *
     * @param examsAssessmentRequest
     * @return a List of Exams results for all subjects of a given Student in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e->examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentId().equalsIgnoreCase(ea.getStudentId())
                        && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
        ).map(r->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(r)).collect(Collectors.toList())).collect(Collectors.toList());
        return eax;
    }

    /**
     *
     * @param examsAssessmentRequest
     * @return a List of Exams results for all subjects of a given Student in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchStudentProgressionReport(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e->examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentId().equalsIgnoreCase(ea.getStudentId())
        ).map(r->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(r)).collect(Collectors.toList())).collect(Collectors.toList());
        return eax;
    }

    /**
     *
     * @param examsAssessmentRequest
     * @return a List of Exams results for all subjects of a given Class in a given Academic Term
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e->examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentClass().equalsIgnoreCase(ea.getStudentClass())
                        && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
                        && e.getTerm().equalsIgnoreCase(ea.getTerm())
        ).map(r->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(r)).collect(Collectors.toList())).collect(Collectors.toList());
        return eax;
    }

    /**
     *
     * @param examsAssessmentRequest
     * @return a List of Exams results for all subjects of a given Class in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e->examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentClass().equalsIgnoreCase(ea.getStudentClass())
                        && e.getAcademicYear().equalsIgnoreCase(ea.getAcademicYear())
        ).map(r->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(r)).collect(Collectors.toList())).collect(Collectors.toList());
        return eax;
    }

    /**
     *
     * @param examsAssessmentRequest
     * @return a List of Exams results for all subjects of a given Class in a given Academic Year
     */
    @Override
    public List<List<Optional<ExamsAssessmentResponse>>> fetchPerformance(List<ExamsAssessmentRequest> examsAssessmentRequest) {
        List<List<Optional<ExamsAssessmentResponse>>> eax= examsAssessmentRequest.stream().map(e->examsAssessmentUtil.examsAssessmentGlobalList.stream().filter(
                ea->e.getStudentClass().equalsIgnoreCase(ea.getStudentClass())
        ).map(r->examsAssessmentUtil.mapExamsAssessment_ToExamsAssessmentResponse(r)).collect(Collectors.toList())).collect(Collectors.toList());
        return eax;
    }
}
