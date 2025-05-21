package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.dto.alien.Students;
import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;
import com.astromyllc.shootingstar.academics.model.ExamsAssessment;
import com.astromyllc.shootingstar.academics.repository.ExamsAssessmentRepository;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsAssessmentServiceInterface;
import com.astromyllc.shootingstar.academics.util.ContinuousAssessmentUtil;
import com.astromyllc.shootingstar.academics.util.ExamsAssessmentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
      List<ClassListResponse> studentsList=examsAssessmentUtil.fetchStudentsByClass(examsAssessmentRequests.get(0).getStudentClass(),examsAssessmentRequests.get(0).getInstitutionCode());

        // Create a Set of student IDs for faster lookup
        Set<String> validStudentIds = studentsList.stream()
                .map(ClassListResponse::getStudentID)
                .collect(Collectors.toSet());

        // Filter requests to only include those with valid student IDs
        List<ExamsAssessmentRequest> validRequests = examsAssessmentRequests.stream()
                .filter(request -> validStudentIds.contains(request.getStudentId()))
                .toList();

        Map<Boolean, List<ExamsAssessment>> partitioned = validRequests.stream()
                .map(examsAssessmentUtil::mapExamsAssessmentRequest_ToExamsAssessment)
                .collect(Collectors.partitioningBy(ca -> ExamsAssessmentUtil.examsAssessmentGlobalList.stream()
                        .anyMatch(existingCa -> existingCa.getStudentId().equalsIgnoreCase(ca.getStudentId()) &&
                                existingCa.getInstitutionCode().equalsIgnoreCase(ca.getInstitutionCode()) &&
                                existingCa.getTerm().equalsIgnoreCase(ca.getTerm()) &&
                                existingCa.getSubject().equals(ca.getSubject()) &&
                                existingCa.getAcademicYear().equalsIgnoreCase(ca.getAcademicYear()))));

        List<ExamsAssessment> existingRecords = partitioned.get(true);  // Existing records
        List<ExamsAssessment> newRecords = partitioned.get(false);     // New records

        examsAssessmentRepository.saveAll(existingRecords);
        examsAssessmentRepository.saveAll(newRecords);
        ExamsAssessmentUtil.examsAssessmentGlobalList.addAll(newRecords);

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
