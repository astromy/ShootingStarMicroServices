package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.AssignmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentQuestionsResponse;
import com.astromyllc.shootingstar.academics.dto.response.AssignmentResponse;
import com.astromyllc.shootingstar.academics.model.Assignment;
import com.astromyllc.shootingstar.academics.model.AssignmentQuestions;
import com.astromyllc.shootingstar.academics.model.SelectedAssignmentQuestionAnswers;
import com.astromyllc.shootingstar.academics.model.SelectedAssignmentQuestions;
import com.astromyllc.shootingstar.academics.repository.AssignmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignmentUtil {

    public static List<Assignment> assignmentGlobalList;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final AssignmentRepository assignmentRepository;

    @PostConstruct
    private void fetchAllAssignments() {
        assignmentGlobalList = assignmentRepository.findAll();
        log.info("Global Assignment List populated with {} records", assignmentGlobalList.size());
    }

    // ── Map Request → Model ───────────────────────────────────────────────────

    public Assignment mapRequestToAssignment(AssignmentRequest req) {
        // Resolve selected questions from the question bank global list
        List<SelectedAssignmentQuestions> selectedQuestions = resolveSelectedQuestions(req);

        return Assignment.builder()
                .title(req.getTitle())
                .subjectId(req.getSubjectId())
                .classId(req.getClassId())
                .term(req.getTerm())
                .institutionCode(req.getInstitutionCode())
                .staffId(req.getStaffId())
                .deliveryMode(req.getDeliveryMode())
                .selectionMode(req.getSelectionMode())
                .questionCount(req.getQuestionCount())
                .deadline(req.getDeadline() != null
                        ? LocalDateTime.parse(req.getDeadline(), isoFormatter)
                        : null)
                .createdAt(LocalDateTime.now())
                .status(req.getStatus() != null ? req.getStatus() : "PUBLISHED")
                .selectedQuestions(selectedQuestions)
                .build();
    }

    // ── Map Model → Response ─────────────────────────────────────────────────

    public AssignmentResponse mapAssignmentToResponse(Assignment a) {
        List<AssignmentQuestionsResponse> qResponses = (a.getSelectedQuestions() != null)
                ? a.getSelectedQuestions().stream()
                  .map(this::mapSelectedToResponse)
                  .collect(Collectors.toList())
                : new ArrayList<>();

        return AssignmentResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .subjectId(a.getSubjectId())
                .classId(a.getClassId())
                .term(a.getTerm())
                .institutionCode(a.getInstitutionCode())
                .staffId(a.getStaffId())
                .deliveryMode(a.getDeliveryMode())
                .selectionMode(a.getSelectionMode())
                .questionCount(a.getQuestionCount())
                .deadline(a.getDeadline() != null ? a.getDeadline().toString() : null)
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().format(formatter) : null)
                .status(a.getStatus())
                .selectedQuestions(qResponses)
                .build();
    }

    // ── Question Selection Logic ──────────────────────────────────────────────

    private List<SelectedAssignmentQuestions> resolveSelectedQuestions(AssignmentRequest req) {
        if (req.getSelectedQuestionIds() == null || req.getSelectedQuestionIds().isEmpty()) {
            return new ArrayList<>();
        }

        // Get the source questions from the assignment question bank
        List<AssignmentQuestions> sourcePool = AssignmentQuestionsUtil.assignmentQuestionsGlobalList
                .stream()
                .filter(q -> req.getSelectedQuestionIds().contains(q.getId()))
                .collect(Collectors.toList());

        int count = req.getQuestionCount() != null
                ? Math.min(req.getQuestionCount(), sourcePool.size())
                : sourcePool.size();

        String mode = req.getSelectionMode() != null ? req.getSelectionMode() : "SAME_ORDER";

        switch (mode) {
            case "SHUFFLED_ORDER":
                // Same questions, randomised order
                Collections.shuffle(sourcePool);
                return sourcePool.stream()
                        .limit(count)
                        .map(this::mapToSelectedAssignmentQuestion)
                        .collect(Collectors.toList());

            case "DIFFERENT_PER_STUDENT":
                // Random subset — the full pool is saved; the frontend/student app
                // draws a unique random subset per student at delivery time.
                // We store the full pool here and mark mode so student service can handle it.
                Collections.shuffle(sourcePool);
                return sourcePool.stream()
                        .limit(count)
                        .map(this::mapToSelectedAssignmentQuestion)
                        .collect(Collectors.toList());

            case "SAME_ORDER":
            default:
                // Same questions, same order for all students
                return sourcePool.stream()
                        .limit(count)
                        .map(this::mapToSelectedAssignmentQuestion)
                        .collect(Collectors.toList());
        }
    }

    private SelectedAssignmentQuestions mapToSelectedAssignmentQuestion(AssignmentQuestions q) {
        List<SelectedAssignmentQuestionAnswers> answers = q.getAssignmentAnswers()
                .stream()
                .map(a -> SelectedAssignmentQuestionAnswers.builder()
                        .answer(a.getAnswer())
                        .isQuestionAnswer(a.getIsQuestionAnswer())
                        .build())
                .collect(Collectors.toList());

        return SelectedAssignmentQuestions.builder()
                .questionDetail(q.getQuestionDetail())
                .subjectId(q.getSubjectId())
                .classId(q.getClassId())
                .term(q.getTerm())
                .institutionCode(q.getInstitutionCode())
                .selectedAssignmentQuestionAnswers(answers)
                .build();
    }

    private AssignmentQuestionsResponse mapSelectedToResponse(SelectedAssignmentQuestions sq) {
        return AssignmentQuestionsResponse.builder()
                .id(sq.getId())
                .questionDetail(sq.getQuestionDetail())
                .subjectId(sq.getSubjectId())
                .classId(sq.getClassId())
                .term(sq.getTerm())
                .institutionCode(sq.getInstitutionCode())
                .assignmentAnswersResponses(
                        sq.getSelectedAssignmentQuestionAnswers() != null
                                ? sq.getSelectedAssignmentQuestionAnswers().stream()
                                  .map(a -> com.astromyllc.shootingstar.academics.dto.response.AssignmentAnswersResponse.builder()
                                            .answer(a.getAnswer())
                                            .isQuestionAnswer(a.getIsQuestionAnswer())
                                            .build())
                                  .collect(Collectors.toList())
                                : new ArrayList<>()
                )
                .build();
    }
}