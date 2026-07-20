window.copyrights();

var v;
var assignmentData = [];   // fetched assignments
var questionPool = [];   // available questions from bank
var selectedQIds = [];   // selected question IDs (Step 2)
var editingAssignmentId = null;
var currentViewAssignment = null;

var optionLabels = ['A', 'B', 'C', 'D'];
var selectionModeLabels = {
    SAME_ORDER: 'Same for All',
    SHUFFLED_ORDER: 'Shuffled Order',
    DIFFERENT_PER_STUDENT: 'Different per Student'
};

// ─── INIT ─────────────────────────────────────────────────────────────────────

fetchLookup(instId.split(",")[0]);

function fetchLookup(instId) {
    showSplash();
    v = instId.replace(/[\[\]'\/]+/g, "").replace(/\//g, "");
    return fetchPost("getLookUpByType", {val: "ClassGroup"}).then(function (result) {
        populateClassGroup(result);
        hideSplash();
    });
}

function populateClassGroup(data) {
    $(".classGroupSelect option:not(:eq(0))").remove();
    data.forEach(function (d) {
        $(".classGroupSelect").append($("<option>").val(d.name).text(d.name));
    });
}

function populateClasses(data) {
    $(".classSelect option:not(:eq(0))").remove();
    $("#modalClassSelect option:not(:eq(0))").remove();
    data.forEach(function (d) {
        $(".classSelect").append($("<option>").val(d.name).text(d.name));
        $("#modalClassSelect").append($("<option>").val(d.name).text(d.name));
    });
}

function populateSubjectsOptions(data) {
    $(".subjectSelect option:not(:eq(0))").remove();
    $("#modalSubjectSelect option:not(:eq(0))").remove();
    data.forEach(function (d) {
        $(".subjectSelect").append($("<option>").val(d.id).text(d.name));
        $("#modalSubjectSelect").append($("<option>").val(d.id).text(d.name));
    });
}

// ─── CLASS GROUP CHANGE ───────────────────────────────────────────────────────

document.querySelector(".classGroupSelect").addEventListener("change", async function () {
    showSplash();
    var classGroup = this.value;
    var instRequest = {id: 0, name: v, classGroup: classGroup, preference: 0};
    var instRequest2 = {institution: v, classGroup: classGroup};
    try {
        const [subjects, classes] = await Promise.all([
            fetchPost("getInstitutionSubjectsAndClassGroup", instRequest),
            fetchPost("getInstitutionClassesByClassGroup", instRequest2),
        ]);
        populateSubjectsOptions(subjects);
        populateClasses(classes);
    } catch (e) {
        console.error("Error loading classes/subjects:", e);
    }
    hideSplash();
});

// ─── FETCH ASSIGNMENTS ────────────────────────────────────────────────────────

document.getElementById("fetchAssignmentsBtn").addEventListener("click", async function () {
    var cls = document.querySelector(".classSelect").value;
    var subject = document.querySelector(".subjectSelect").options[document.querySelector(".subjectSelect").selectedIndex]?.text || '';
    var term = document.querySelector(".termSelect").value;

    if (!cls) {
        swal({title: "Required", text: "Please select at least a class.", type: "warning"});
        return;
    }
    showSplash();
    try {
        var payload = {institutionCode: v, classId: cls, subjectId: subject, term: term};
        var endpoint = (subject) ? "fetchAssignmentsByClassAndSubject" : "fetchAssignmentsByClass";
        var result = await fetchPost(endpoint, payload);
        assignmentData = (result || []).map(function (r) {
            return r && r.id !== undefined ? r : (r && typeof r === 'object' ? Object.values(r)[0] : r);
        }).filter(Boolean);
        destroyTable();
        $("#assignmentTableBody").empty();
        buildTable(assignmentData);
    } catch (e) {
        console.error("Error fetching assignments:", e);
        swal({title: "Error", text: "Failed to fetch assignments.", type: "error"});
    }
    hideSplash();
});

// ─── BUILD ASSIGNMENTS TABLE ──────────────────────────────────────────────────

function buildTable(data) {
    data.forEach(function (a, idx) {
        var modeLabel = selectionModeLabels[a.selectionMode] || a.selectionMode || '—';
        var deliveryIcon = a.deliveryMode === 'ONLINE'
            ? '<i class="fa fa-laptop text-info"></i> Online'
            : '<i class="fa fa-print text-warning"></i> Print';
        var statusLabel = a.status === 'PUBLISHED'
            ? '<span class="label label-success">Published</span>'
            : '<span class="label label-default">Draft</span>';
        var deadline = a.deadline ? new Date(a.deadline).toLocaleString() : '—';

        var row = `
            <tr>
                <td>${idx + 1}</td>
                <td><strong>${a.title || ''}</strong></td>
                <td>${a.classId || ''}</td>
                <td>${a.subjectId || ''}</td>
                <td>${a.term || ''}</td>
                <td>${deliveryIcon}</td>
                <td>${modeLabel}</td>
                <td>${a.questionCount || (a.selectedQuestions || []).length}</td>
                <td>${deadline}</td>
                <td>${statusLabel}</td>
                <td>
                    <button class="btn btn-xs btn-info view-assignment-btn" data-id="${a.id}">
                        <i class="fa fa-eye"></i>
                    </button>
                    <button class="btn btn-xs btn-warning edit-assignment-btn m-l-xs" data-id="${a.id}">
                        <i class="fa fa-pencil"></i>
                    </button>
                    <button class="btn btn-xs btn-danger delete-assignment-btn m-l-xs" data-id="${a.id}">
                        <i class="fa fa-trash"></i>
                    </button>
                </td>
            </tr>`;
        $("#assignmentTableBody").append(row);
    });

    initTable();
    bindTableButtons();
}

function initTable() {
    $("#assignmentTable").dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            {extend: "copy", className: "btn-sm"},
            {extend: "csv", title: "Assignments", className: "btn-sm"},
            {extend: "print", className: "btn-sm"},
        ],
        columnDefs: [{orderable: false, targets: [5, 6, 10]}]
    });
}

function destroyTable() {
    if ($.fn.DataTable.isDataTable("#assignmentTable")) {
        $("#assignmentTable").DataTable().destroy();
    }
}

// ─── TABLE ACTION BUTTONS ─────────────────────────────────────────────────────

function bindTableButtons() {
    $(".view-assignment-btn").off("click").on("click", function () {
        var a = assignmentData.find(function (x) {
            return x.id == $(this).data("id");
        }.bind(this));
        if (a) openViewModal(a);
    });

    $(".edit-assignment-btn").off("click").on("click", function () {
        var a = assignmentData.find(function (x) {
            return x.id == $(this).data("id");
        }.bind(this));
        if (a) openCreateModal(a);
    });

    $(".delete-assignment-btn").off("click").on("click", function () {
        var id = $(this).data("id");
        var a = assignmentData.find(function (x) {
            return x.id == id;
        });
        if (!a) return;
        swal({
            title: "Delete Assignment?",
            text: '"' + a.title + '" will be permanently removed.',
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Yes, delete it!",
            closeOnConfirm: false
        }, async function () {
            showSplash();
            try {
                await fetchPost("deleteAssignment", {id: a.id, institutionCode: v});
                assignmentData = assignmentData.filter(function (x) {
                    return x.id != id;
                });
                destroyTable();
                $("#assignmentTableBody").empty();
                buildTable(assignmentData);
                swal({title: "Deleted!", text: "Assignment removed.", type: "success"});
            } catch (e) {
                swal({title: "Error", text: "Failed to delete.", type: "error"});
            }
            hideSplash();
        });
    });
}

// ─── CREATE / EDIT MODAL ─────────────────────────────────────────────────────

document.getElementById("createAssignmentBtn").addEventListener("click", function () {
    openCreateModal(null);
});

function openCreateModal(assignment) {
    editingAssignmentId = assignment ? assignment.id : null;
    selectedQIds = [];
    questionPool = [];

    resetCreateModal();

    if (assignment) {
        // Edit mode — pre-fill
        $("#assignmentModalTitle").text("Edit Assignment");
        $("#assignmentTitle").val(assignment.title || '');
        $("#assignmentStatus").val(assignment.status || 'PUBLISHED');
        $("#modalClassSelect").val(assignment.classId || '');
        $("#modalSubjectSelect").val(assignment.subjectId || '');
        $("#modalTermSelect").val(assignment.term || '');
        $("#questionCount").val(assignment.questionCount || '');
        if (assignment.deadline) {
            // Format for datetime-local input
            var d = new Date(assignment.deadline);
            var iso = d.getFullYear() + '-' +
                String(d.getMonth() + 1).padStart(2, '0') + '-' +
                String(d.getDate()).padStart(2, '0') + 'T' +
                String(d.getHours()).padStart(2, '0') + ':' +
                String(d.getMinutes()).padStart(2, '0');
            $("#assignmentDeadline").val(iso);
        }
        selectModeCard(assignment.deliveryMode || 'ONLINE', 'delivery');
        selectModeCard(assignment.selectionMode || 'SAME_ORDER', 'selection');

        if (assignment.selectedQuestions && assignment.selectedQuestions.length > 0) {
            selectedQIds = assignment.selectedQuestions.map(function (q) {
                return q.id;
            });
        }
        $("#publishBtnText").text("Save Changes");
    } else {
        $("#assignmentModalTitle").text("Create Assignment");
        $("#publishBtnText").text("Publish Assignment");
        // Pre-fill class/subject/term from filter header if set
        var headerClass = document.querySelector(".classSelect").value;
        var headerSubject = document.querySelector(".subjectSelect").value;
        var headerTerm = document.querySelector(".termSelect").value;
        if (headerClass) $("#modalClassSelect").val(headerClass);
        if (headerSubject) $("#modalSubjectSelect").val(headerSubject);
        if (headerTerm) $("#modalTermSelect").val(headerTerm);
    }

    showStep("step1");
    $("#assignmentModal").modal("show");
}

function resetCreateModal() {
    $("#assignmentTitle").val('');
    $("#assignmentStatus").val('PUBLISHED');
    $("#questionCount").val('');
    $("#assignmentDeadline").val('');
    $("#deliveryMode").val('ONLINE');
    $("#selectionMode").val('SAME_ORDER');
    $(".assignment-mode-card").removeClass("selected");
    $("[data-mode='ONLINE'][data-group='delivery']").addClass("selected");
    $("[data-mode='SAME_ORDER'][data-group='selection']").addClass("selected");
    $("#questionPoolContainer").html('<div class="text-center text-muted" id="questionPoolEmpty"><i class="fa fa-info-circle"></i> Complete Step 1 and click "Next: Select Questions" to load the question bank.</div>');
    $("#previewQuestionList").empty();
    selectedQIds = [];
}

// ─── MODE CARD SELECTION ──────────────────────────────────────────────────────

document.querySelectorAll(".assignment-mode-card").forEach(function (card) {
    card.addEventListener("click", function () {
        selectModeCard(this.dataset.mode, this.dataset.group);
    });
});

function selectModeCard(mode, group) {
    document.querySelectorAll('[data-group="' + group + '"]').forEach(function (c) {
        c.classList.remove("selected");
    });
    var target = document.querySelector('[data-mode="' + mode + '"][data-group="' + group + '"]');
    if (target) target.classList.add("selected");

    if (group === 'delivery') $("#deliveryMode").val(mode);
    if (group === 'selection') $("#selectionMode").val(mode);
}

// ─── STEP NAVIGATION ─────────────────────────────────────────────────────────

document.querySelectorAll(".assignmentStepBtn").forEach(function (btn) {
    btn.addEventListener("click", function () {
        showStep(this.dataset.step);
    });
});

document.getElementById("loadQuestionsBtn").addEventListener("click", async function () {
    if (!validateStep1()) return;
    showStep("step2");
    await loadQuestionPool();
});

document.getElementById("backToStep1Btn").addEventListener("click", function () {
    showStep("step1");
});
document.getElementById("backToStep2Btn").addEventListener("click", function () {
    showStep("step2");
});

document.getElementById("goToPreviewBtn").addEventListener("click", function () {
    var required = parseInt($("#questionCount").val()) || 0;
    if (selectedQIds.length < required) {
        swal({title: "Not Enough", text: "Please select at least " + required + " questions.", type: "warning"});
        return;
    }
    buildPreview();
    showStep("step3");
});

function showStep(stepId) {
    document.querySelectorAll(".assignment-step").forEach(function (s) {
        s.style.display = "none";
    });
    document.getElementById(stepId).style.display = "block";

    document.querySelectorAll(".assignmentStepBtn").forEach(function (b) {
        b.classList.remove("btn-primary");
        b.classList.add("btn-default");
    });
    var activeBtn = document.querySelector('[data-step="' + stepId + '"]');
    if (activeBtn) {
        activeBtn.classList.remove("btn-default");
        activeBtn.classList.add("btn-primary");
    }
}

// ─── STEP 1 VALIDATION ────────────────────────────────────────────────────────

function validateStep1() {
    if (!$("#assignmentTitle").val().trim()) {
        swal({title: "Required", text: "Please enter an assignment title.", type: "warning"});
        return false;
    }
    if (!$("#modalClassSelect").val()) {
        swal({title: "Required", text: "Please select a class.", type: "warning"});
        return false;
    }
    if (!$("#modalSubjectSelect").val()) {
        swal({title: "Required", text: "Please select a subject.", type: "warning"});
        return false;
    }
    if (!$("#modalTermSelect").val()) {
        swal({title: "Required", text: "Please select a term.", type: "warning"});
        return false;
    }
    var qCount = parseInt($("#questionCount").val());
    if (!qCount || qCount < 1) {
        swal({title: "Required", text: "Please enter the number of questions.", type: "warning"});
        return false;
    }
    if (!$("#assignmentDeadline").val()) {
        swal({title: "Required", text: "Please set a submission deadline.", type: "warning"});
        return false;
    }
    return true;
}

// ─── STEP 2: LOAD QUESTION POOL ──────────────────────────────────────────────

async function loadQuestionPool() {
    showSplash();
    var cls = $("#modalClassSelect").val();
    var subject = $("#modalSubjectSelect option:selected").text();
    var term = $("#modalTermSelect").val();
    var required = parseInt($("#questionCount").val()) || 0;

    $("#requiredCount").text(required);
    $("#selectedCount").text(selectedQIds.length);

    try {
        var result = await fetchPost("fetchQuestionsByInstAndClassAndSubjAndTerm", {
            institutionCode: v,
            classId: cls,
            subjectId: subject,
            term: term
        });

        questionPool = (result || []).map(function (r) {
            return r && r.id !== undefined ? r : (r && typeof r === 'object' ? Object.values(r)[0] : r);
        }).filter(Boolean);

        if (questionPool.length === 0) {
            $("#questionPoolContainer").html(
                '<div class="text-center text-warning" style="padding:20px;">' +
                '<i class="fa fa-exclamation-triangle fa-2x"></i><br><br>' +
                'No questions found in the bank for this class/subject/term.<br>' +
                '<small>Add questions via the Question Bank first.</small></div>'
            );
            hideSplash();
            return;
        }

        renderQuestionPool(questionPool);
        $("#availableCount").text(questionPool.length);

        if (questionPool.length < required) {
            swal({
                title: "Warning",
                text: "Only " + questionPool.length + " questions available but " + required + " required. You may need to add more to the question bank.",
                type: "warning"
            });
        }
    } catch (e) {
        console.error("Error loading question pool:", e);
        $("#questionPoolContainer").html('<div class="text-center text-danger">Failed to load questions. Please try again.</div>');
    }
    hideSplash();
}

function renderQuestionPool(questions) {
    var html = '';
    questions.forEach(function (q, idx) {
        var isSelected = selectedQIds.includes(q.id);
        html += '<div class="question-pool-item ' + (isSelected ? 'selected-q' : '') + '" data-qid="' + q.id + '">' +
            '<span class="q-number">' + (idx + 1) + '</span>' +
            '<strong>' + (q.questionDetail || '') + '</strong>' +
            '</div>';
    });
    $("#questionPoolContainer").html(html);
    bindQuestionPoolItems();
}

function bindQuestionPoolItems() {
    $(".question-pool-item").off("click").on("click", function () {
        var qid = parseInt($(this).data("qid"));
        if (selectedQIds.includes(qid)) {
            selectedQIds = selectedQIds.filter(function (id) {
                return id !== qid;
            });
            $(this).removeClass("selected-q");
        } else {
            selectedQIds.push(qid);
            $(this).addClass("selected-q");
        }
        updateSelectedCount();
    });
}

function updateSelectedCount() {
    $("#selectedCount").text(selectedQIds.length);
}

document.getElementById("selectAllBtn").addEventListener("click", function () {
    selectedQIds = questionPool.map(function (q) {
        return q.id;
    });
    $(".question-pool-item").addClass("selected-q");
    updateSelectedCount();
});

document.getElementById("clearSelectionBtn").addEventListener("click", function () {
    selectedQIds = [];
    $(".question-pool-item").removeClass("selected-q");
    updateSelectedCount();
});

// ─── STEP 3: PREVIEW ─────────────────────────────────────────────────────────

function buildPreview() {
    var title = $("#assignmentTitle").val();
    var cls = $("#modalClassSelect option:selected").text();
    var subject = $("#modalSubjectSelect option:selected").text();
    var term = $("#modalTermSelect").val();
    var delivery = $("#deliveryMode").val();
    var mode = selectionModeLabels[$("#selectionMode").val()] || $("#selectionMode").val();
    var qcount = $("#questionCount").val();
    var deadline = $("#assignmentDeadline").val()
        ? new Date($("#assignmentDeadline").val()).toLocaleString()
        : '—';

    $("#preview-title").text(title);
    $("#preview-class").text(cls);
    $("#preview-subject").text(subject);
    $("#preview-term").text(term);
    $("#preview-delivery").html(delivery === 'ONLINE'
        ? '<i class="fa fa-laptop text-info"></i> Online'
        : '<i class="fa fa-print text-warning"></i> Print');
    $("#preview-mode").text(mode);
    $("#preview-qcount").text(qcount);
    $("#preview-deadline").text(deadline);

    // Build question preview
    var selected = questionPool.filter(function (q) {
        return selectedQIds.includes(q.id);
    });
    var html = '';
    selected.forEach(function (q, idx) {
        var answers = q.examsAnswersResponses || q.assignmentAnswersResponses || [];
        html += '<div class="preview-question-item">' +
            '<strong>' + (idx + 1) + '. ' + (q.questionDetail || '') + '</strong><br>';
        answers.forEach(function (a, i) {
            html += '<div class="answer-option">' +
                optionLabels[i] + '. ' + (a.answer || '') + '</div>';
        });
        html += '</div>';
    });
    $("#previewQuestionList").html(html || '<div class="text-muted">No questions selected.</div>');
}

// ─── PRINT / DOWNLOAD ─────────────────────────────────────────────────────────

document.getElementById("printAssignmentBtn").addEventListener("click", function () {
    printAssignment(false, getSelectedQuestions());
});
document.getElementById("printWithAnswersAssignBtn").addEventListener("click", function () {
    printAssignment(true, getSelectedQuestions());
});
document.getElementById("downloadAssignmentBtn").addEventListener("click", function () {
    downloadAssignment(getSelectedQuestions());
});

document.getElementById("viewPrintBtn").addEventListener("click", function () {
    if (currentViewAssignment) printAssignment(false, currentViewAssignment.selectedQuestions || []);
});
document.getElementById("viewPrintAnswersBtn").addEventListener("click", function () {
    if (currentViewAssignment) printAssignment(true, currentViewAssignment.selectedQuestions || []);
});
document.getElementById("viewDownloadBtn").addEventListener("click", function () {
    if (currentViewAssignment) downloadAssignment(currentViewAssignment.selectedQuestions || []);
});

function getSelectedQuestions() {
    return questionPool.filter(function (q) {
        return selectedQIds.includes(q.id);
    });
}

function buildPrintHTML(withAnswers, questions, meta) {
    var optLabels = ['A', 'B', 'C', 'D'];
    var qHTML = questions.map(function (q, idx) {
        var answers = q.examsAnswersResponses || q.assignmentAnswersResponses || [];
        var optHTML = answers.map(function (a, i) {
            var correct = withAnswers && a.isQuestionAnswer
                ? ' <strong style="color:green;">✓</strong>' : '';
            return '<div style="margin:3px 0;padding-left:20px;">' +
                '<strong>' + optLabels[i] + '.</strong> ' + (a.answer || '') + correct + '</div>';
        }).join('');
        return '<div style="margin-bottom:16px;page-break-inside:avoid;">' +
            '<p style="margin:0 0 4px;"><strong>' + (idx + 1) + '. ' + (q.questionDetail || '') + '</strong></p>' +
            optHTML + '</div>';
    }).join('');

    var answerKey = '';
    if (withAnswers) {
        answerKey = '<div style="margin-top:30px;border-top:2px solid #333;padding-top:10px;">' +
            '<h3>Answer Key</h3><table style="width:100%;border-collapse:collapse;">' +
            '<thead><tr>' +
            '<th style="border:1px solid #ccc;padding:4px;width:60px;">Q#</th>' +
            '<th style="border:1px solid #ccc;padding:4px;">Answer</th></tr></thead><tbody>' +
            questions.map(function (q, idx) {
                var answers = q.examsAnswersResponses || q.assignmentAnswersResponses || [];
                var correct = answers.find(function (a) {
                    return a.isQuestionAnswer === true;
                });
                var ci = answers.indexOf(correct);
                return '<tr>' +
                    '<td style="border:1px solid #ccc;padding:4px;text-align:center;">' + (idx + 1) + '</td>' +
                    '<td style="border:1px solid #ccc;padding:4px;">' +
                    (correct ? optLabels[ci] + '. ' + correct.answer : '—') + '</td></tr>';
            }).join('') +
            '</tbody></table></div>';
    }

    return '<!DOCTYPE html><html><head><title>' + (meta.title || 'Assignment') + '</title>' +
        '<style>body{font-family:Arial,sans-serif;margin:30px;font-size:13px;color:#111;}' +
        'h2{margin:0;}.header{border-bottom:2px solid #333;padding-bottom:10px;margin-bottom:20px;}' +
        '.meta{color:#555;font-size:12px;margin-top:4px;}@media print{button{display:none;}}</style>' +
        '</head><body>' +
        '<div class="header"><h2>' + (meta.title || 'Assignment') + '</h2>' +
        '<div class="meta">Class: <strong>' + (meta.cls || '') + '</strong> &nbsp;|&nbsp; ' +
        'Subject: <strong>' + (meta.subject || '') + '</strong> &nbsp;|&nbsp; ' +
        'Term: <strong>' + (meta.term || '') + '</strong> &nbsp;|&nbsp; ' +
        'Questions: <strong>' + questions.length + '</strong>' +
        (meta.deadline ? ' &nbsp;|&nbsp; Deadline: <strong>' + meta.deadline + '</strong>' : '') +
        (withAnswers ? ' &nbsp;|&nbsp; <strong style="color:green;">MARKING SCHEME</strong>' : '') +
        '</div></div>' + qHTML + answerKey + '</body></html>';
}

function getMeta() {
    return {
        title: $("#assignmentTitle").val() || (currentViewAssignment && currentViewAssignment.title) || 'Assignment',
        cls: $("#modalClassSelect option:selected").text() || (currentViewAssignment && currentViewAssignment.classId) || '',
        subject: $("#modalSubjectSelect option:selected").text() || (currentViewAssignment && currentViewAssignment.subjectId) || '',
        term: $("#modalTermSelect").val() || (currentViewAssignment && currentViewAssignment.term) || '',
        deadline: $("#assignmentDeadline").val()
            ? new Date($("#assignmentDeadline").val()).toLocaleString()
            : (currentViewAssignment && currentViewAssignment.deadline
                ? new Date(currentViewAssignment.deadline).toLocaleString()
                : ''),
    };
}

function printAssignment(withAnswers, questions) {
    var html = buildPrintHTML(withAnswers, questions, getMeta());
    var win = window.open('', '_blank', 'width=800,height=600');
    win.document.write(html);
    win.document.close();
    win.focus();
    win.print();
}

function downloadAssignment(questions) {
    var meta = getMeta();
    var html = buildPrintHTML(true, questions, meta);
    var blob = new Blob([html], {type: 'text/html'});
    var url = URL.createObjectURL(blob);
    var a = document.createElement('a');
    a.href = url;
    a.download = 'Assignment_' + (meta.title || 'file').replace(/\s+/g, '_') + '.html';
    a.click();
    URL.revokeObjectURL(url);
    swal({title: "Downloaded!", text: "Open the file and use Ctrl+P to save as PDF.", type: "success"});
}

// ─── PUBLISH / SAVE ──────────────────────────────────────────────────────────

document.getElementById("publishAssignmentBtn").addEventListener("click", async function () {
    var required = parseInt($("#questionCount").val()) || 0;
    if (selectedQIds.length < required) {
        swal({
            title: "Not Enough Questions",
            text: "Go back and select at least " + required + " questions.",
            type: "warning"
        });
        return;
    }
    showSplash();

    var staffId = (document.querySelector(".staffLoginId") || {}).innerHTML || '';

    var payload = {
        id: editingAssignmentId || null,
        title: $("#assignmentTitle").val().trim(),
        classId: $("#modalClassSelect").val(),
        subjectId: $("#modalSubjectSelect option:selected").text(),
        term: $("#modalTermSelect").val(),
        institutionCode: v,
        staffId: staffId,
        deliveryMode: $("#deliveryMode").val(),
        selectionMode: $("#selectionMode").val(),
        questionCount: parseInt($("#questionCount").val()),
        deadline: $("#assignmentDeadline").val(),
        status: $("#assignmentStatus").val(),
        selectedQuestionIds: selectedQIds,
    };

    try {
        var endpoint = editingAssignmentId ? "updateAssignment" : "createAssignment";
        var result = await fetchPost(endpoint, payload);

        if (editingAssignmentId) {
            var idx = assignmentData.findIndex(function (a) {
                return a.id == editingAssignmentId;
            });
            if (idx !== -1 && result) assignmentData[idx] = result;
        } else {
            if (result) assignmentData.push(result);
        }

        destroyTable();
        $("#assignmentTableBody").empty();
        buildTable(assignmentData);

        $("#assignmentModal").modal("hide");
        swal({
            title: "Success!",
            text: editingAssignmentId ? "Assignment updated." : "Assignment published successfully.",
            type: "success"
        });
    } catch (e) {
        console.error("Error publishing assignment:", e);
        swal({title: "Error", text: "Failed to save assignment.", type: "error"});
    }
    hideSplash();
});

// ─── VIEW MODAL ───────────────────────────────────────────────────────────────

function openViewModal(assignment) {
    currentViewAssignment = assignment;
    var questions = assignment.selectedQuestions || [];
    var meta = {
        title: assignment.title,
        cls: assignment.classId,
        subject: assignment.subjectId,
        term: assignment.term,
        deadline: assignment.deadline ? new Date(assignment.deadline).toLocaleString() : '—',
    };

    $("#viewAssignmentTitle").text(assignment.title);
    $("#viewAssignmentMeta").html(
        'Class: <strong>' + assignment.classId + '</strong> &nbsp;|&nbsp; ' +
        'Subject: <strong>' + assignment.subjectId + '</strong> &nbsp;|&nbsp; ' +
        'Term: <strong>' + assignment.term + '</strong> &nbsp;|&nbsp; ' +
        'Delivery: <strong>' + (assignment.deliveryMode || '—') + '</strong> &nbsp;|&nbsp; ' +
        'Mode: <strong>' + (selectionModeLabels[assignment.selectionMode] || '—') + '</strong>'
    );

    var html = '';
    questions.forEach(function (q, idx) {
        var answers = q.assignmentAnswersResponses || q.examsAnswersResponses || [];
        html += '<div class="preview-question-item">' +
            '<strong>' + (idx + 1) + '. ' + (q.questionDetail || '') + '</strong><br>';
        answers.forEach(function (a, i) {
            html += '<div class="answer-option">' + optionLabels[i] + '. ' + (a.answer || '') + '</div>';
        });
        html += '</div>';
    });
    $("#viewQuestionList").html(html || '<div class="text-muted">No questions attached.</div>');
    $("#viewAssignmentModal").modal("show");
}

// ─── HELPERS ─────────────────────────────────────────────────────────────────

function showSplash() {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
}

function hideSplash() {
    $('.splash').css('display', 'none');
}