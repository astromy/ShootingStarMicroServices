window.copyrights();

var v;                          // institution code
var questionBankData = [];      // holds last fetched questions
var editingQuestionId = null;   // null = add mode, number = edit mode
var currentClass = '';
var currentSubject = '';
var currentTerm = '';

var optionLabels = ['A', 'B', 'C', 'D'];

// ─── INIT ────────────────────────────────────────────────────────────────────

fetchLookup(instId.split(",")[0]);

// ─── CLASS GROUP → CLASS → SUBJECT CHAIN ─────────────────────────────────────

function fetchLookup(instId) {
    showSplash();
    v = instId.replace(/[\[\]']+/g, "").replace(/\//g, "");
    var instRequest = {val: "ClassGroup"};
    return fetchPost("getLookUpByType", instRequest).then(function (result) {
        populateClassGroup(result);
        hideSplash();
    });
}

function populateClassGroup(data) {
    $(".classGroupSelect option:not(:eq(0))").remove();
    data.forEach(function (d) {
        var details = $("<option>").val(d.name).text(d.name);
        $(".classGroupSelect").append(details);
    });
}

function populateClasses(data) {
    $(".classSelect option:not(:eq(0))").remove();
    data.forEach(function (d) {
        var details = $("<option>").val(d.name).text(d.name);
        $(".classSelect").append(details);
    });
}

function populateSubjectsOptions(data) {
    $(".subjectSelect option:not(:eq(0))").remove();
    data.forEach(function (d) {
        var details = $("<option>").val(d.id).text(d.name);
        $(".subjectSelect").append(details);
    });
}

document.querySelector(".classGroupSelect").addEventListener("change", async function () {
    showSplash();
    var classGroup = this.value;

    // Subject request payload
    var instRequest = {
        id: 0,
        name: v,
        classGroup: classGroup,
        preference: 0,
    };
    // Class request payload
    var instRequest2 = {
        institution: v,
        classGroup: classGroup,
    };

    try {
        // Fetch subjects and classes simultaneously — exact pattern from _scoreUpload.js
        const result = await fetchPost("getInstitutionSubjectsAndClassGroup", instRequest);
        const result2 = await fetchPost("getInstitutionClassesByClassGroup", instRequest2);
        populateSubjectsOptions(result);
        populateClasses(result2);
    } catch (e) {
        console.error("Error fetching classes/subjects:", e);
    }
    hideSplash();
});

document.querySelector(".classSelect").addEventListener("change", function () {
    currentClass = this.value;
});

document.querySelector(".subjectSelect").addEventListener("change", function () {
    currentSubject = this.options[this.selectedIndex].text;  // name used as subjectId in backend
    currentSubjectId = this.value;                              // numeric id
});

document.querySelector(".termSelect").addEventListener("change", function () {
    currentTerm = this.value;
});

// ─── FETCH QUESTIONS ──────────────────────────────────────────────────────────

document.getElementById("fetchQuestionsBtn").addEventListener("click", async function () {
    if (!currentClass) {
        swal({title: "Required", text: "Please select a class first.", type: "warning"});
        return;
    }
    showSplash();
    try {
        var payload = {
            institutionCode: v,
            classId: currentClass,
            subjectId: currentSubject || '',
            term: currentTerm || ''
        };

        // Use most specific endpoint available
        var endpoint = "fetchQuestionsByInstAndClass";
        if (currentSubject && currentTerm) endpoint = "fetchQuestionsByInstAndClassAndSubjAndTerm";
        else if (currentSubject) endpoint = "fetchQuestionsByInstAndClassAndSubj";

        var result = await fetchPost(endpoint, payload);

        if (!result || result.length === 0) {
            swal({title: "No Questions", text: "No questions found for the selected filters.", type: "info"});
            hideSplash();
            return;
        }

        // Unwrap Optional wrappers if present
        questionBankData = result.map(function (r) {
            return r && r.id !== undefined ? r : (r && typeof r === 'object' ? Object.values(r)[0] : r);
        }).filter(Boolean);

        destroyTable();
        $("#questionBankBody").empty();
        buildTable(questionBankData);
    } catch (e) {
        console.error("Error fetching questions:", e);
        swal({title: "Error", text: "Failed to fetch questions.", type: "error"});
    }
    hideSplash();
});

// ─── BUILD TABLE ──────────────────────────────────────────────────────────────

function buildTable(data) {
    data.forEach(function (q, idx) {
        var answers = q.examsAnswersResponses || [];
        var optionsList = answers.map(function (a, i) {
            var isCorrect = a.isQuestionAnswer === true;
            return '<span class="label ' + (isCorrect ? 'label-success' : 'label-default') + '" style="margin:2px;display:inline-block;">'
                + optionLabels[i] + '. ' + (a.answer || '') + '</span>';
        }).join(' ');

        var row = `
            <tr>
                <td>${idx + 1}</td>
                <td>${q.questionDetail || ''}</td>
                <td>${q.subjectId || ''}</td>
                <td>${q.classId || ''}</td>
                <td>${q.term || ''}</td>
                <td>${optionsList}</td>
                <td>
                    <button class="btn btn-xs btn-warning edit-question-btn" data-id="${q.id}">
                        <i class="fa fa-pencil"></i>
                    </button>
                    <button class="btn btn-xs btn-danger delete-question-btn" data-id="${q.id}">
                        <i class="fa fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
        $("#questionBankBody").append(row);
    });

    initTable();
    bindActionButtons();
}

function initTable() {
    $("#questionBankTable").dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            {extend: "copy", className: "btn-sm"},
            {extend: "csv", title: "Question Bank", className: "btn-sm"},
            {extend: "print", className: "btn-sm"},
        ],
        columnDefs: [{orderable: false, targets: [5, 6]}]
    });
    document.querySelector("#questionBankTable_wrapper")
        .setAttribute("style", "overflow: auto;");
}

function destroyTable() {
    if ($.fn.DataTable.isDataTable("#questionBankTable")) {
        $("#questionBankTable").DataTable().destroy();
    }
}

// ─── ADD QUESTION ─────────────────────────────────────────────────────────────

document.getElementById("addQuestionBtn").addEventListener("click", function () {
    if (!currentClass || !currentSubject || !currentTerm) {
        swal({
            title: "Required",
            text: "Please select Class, Subject and Term before adding a question.",
            type: "warning"
        });
        return;
    }
    editingQuestionId = null;
    resetQuestionForm();
    $("#questionModalTitle").text("Add Question");
    $("#questionModalSubtitle").text("Class: " + currentClass + " | Subject: " + currentSubject + " | " + currentTerm);
    $("#questionModal").modal("show");
});

// ─── EDIT QUESTION ────────────────────────────────────────────────────────────

function bindActionButtons() {
    $(".edit-question-btn").off("click").on("click", function () {
        var id = $(this).data("id");
        var question = questionBankData.find(function (q) {
            return q.id == id;
        });
        if (!question) return;

        editingQuestionId = question.id;
        resetQuestionForm();

        $("#questionDetail").val(question.questionDetail || '');
        var answers = question.examsAnswersResponses || [];
        answers.forEach(function (a, i) {
            $("#option" + i).val(a.answer || '');
            if (a.isQuestionAnswer === true) {
                $("#correct" + i).prop("checked", true);
            }
        });

        $("#questionModalTitle").text("Edit Question #" + (questionBankData.indexOf(question) + 1));
        $("#questionModalSubtitle").text("Class: " + (question.classId || '') + " | Subject: " + (question.subjectId || '') + " | " + (question.term || ''));
        $("#questionModal").modal("show");
    });

    $(".delete-question-btn").off("click").on("click", function () {
        var id = $(this).data("id");
        var question = questionBankData.find(function (q) {
            return q.id == id;
        });
        if (!question) return;

        swal({
            title: "Delete Question?",
            text: "This cannot be undone.",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Yes, delete it!",
            closeOnConfirm: false
        }, async function () {
            showSplash();
            try {
                await fetchPost("deleteExamsQuestion", {id: question.id, institutionCode: v});
                questionBankData = questionBankData.filter(function (q) {
                    return q.id != id;
                });
                destroyTable();
                $("#questionBankBody").empty();
                buildTable(questionBankData);
                swal({title: "Deleted!", text: "Question removed from the bank.", type: "success"});
            } catch (e) {
                console.error("Error deleting question:", e);
                swal({title: "Error", text: "Failed to delete question.", type: "error"});
            }
            hideSplash();
        });
    });
}

// ─── SAVE QUESTION ────────────────────────────────────────────────────────────

document.getElementById("saveQuestionBtn").addEventListener("click", async function () {
    var questionDetail = $("#questionDetail").val().trim();
    var options = [
        $("#option0").val().trim(),
        $("#option1").val().trim(),
        $("#option2").val().trim(),
        $("#option3").val().trim()
    ];
    var correctIndex = $("input[name='correctAnswer']:checked").val();

    // Validation
    $("#questionFormError").hide();
    if (!questionDetail) {
        showFormError("Please enter the question.");
        return;
    }
    if (options.some(function (o) {
        return !o;
    })) {
        showFormError("Please fill in all four options.");
        return;
    }
    if (correctIndex === undefined) {
        showFormError("Please mark the correct answer.");
        return;
    }

    var answers = options.map(function (opt, i) {
        return {
            id: null,
            answer: opt,
            isQuestionAnswer: parseInt(correctIndex) === i
        };
    });

    var payload = {
        id: editingQuestionId || null,
        questionDetail: questionDetail,
        classId: currentClass,
        subjectId: currentSubject,
        term: currentTerm,
        institutionCode: v,
        examsAnswersRequests: answers
    };

    showSplash();
    try {
        var endpoint = editingQuestionId ? "updateExamsQuestion" : "submitExamsQuestion";
        var result = await fetchPost(endpoint, payload);

        if (editingQuestionId) {
            // Update local data
            var idx = questionBankData.findIndex(function (q) {
                return q.id == editingQuestionId;
            });
            if (idx !== -1) {
                questionBankData[idx] = Object.assign({}, questionBankData[idx], {
                    questionDetail: questionDetail,
                    examsAnswersResponses: answers.map(function (a) {
                        return {answer: a.answer, isQuestionAnswer: a.isQuestionAnswer};
                    })
                });
            }
        } else {
            // Add to local data — use server response if available
            var newQ = (result && result.id) ? result : Object.assign({id: Date.now()}, payload, {
                examsAnswersResponses: answers.map(function (a) {
                    return {answer: a.answer, isQuestionAnswer: a.isQuestionAnswer};
                })
            });
            questionBankData.push(newQ);
        }

        destroyTable();
        $("#questionBankBody").empty();
        buildTable(questionBankData);

        $("#questionModal").modal("hide");
        swal({
            title: "Saved!",
            text: editingQuestionId ? "Question updated successfully." : "Question added to the bank.",
            type: "success"
        });
    } catch (e) {
        console.error("Error saving question:", e);
        swal({title: "Error", text: "Failed to save question.", type: "error"});
    }
    hideSplash();
});

// ─── PRINT ────────────────────────────────────────────────────────────────────

document.getElementById("printQuestionsBtn").addEventListener("click", function () {
    if (!questionBankData.length) {
        swal({title: "No Questions", text: "Fetch questions first.", type: "info"});
        return;
    }
    printQuestions(false);
});

document.getElementById("printWithAnswersBtn").addEventListener("click", function () {
    if (!questionBankData.length) {
        swal({title: "No Questions", text: "Fetch questions first.", type: "info"});
        return;
    }
    printQuestions(true);
});

document.getElementById("downloadQuestionsBtn").addEventListener("click", function () {
    if (!questionBankData.length) {
        swal({title: "No Questions", text: "Fetch questions first.", type: "info"});
        return;
    }
    downloadPDF();
});

function buildPrintHTML(withAnswers) {
    var institution = (typeof StaffUtil !== 'undefined' && StaffUtil.institutionRequest)
        ? StaffUtil.institutionRequest.getName() : '';

    var questionsHTML = questionBankData.map(function (q, idx) {
        var answers = q.examsAnswersResponses || [];
        var optionsHTML = answers.map(function (a, i) {
            var correctMark = (withAnswers && a.isQuestionAnswer) ? ' <strong style="color:green;">✓</strong>' : '';
            return '<div style="margin:4px 0;padding-left:20px;">'
                + '<strong>' + optionLabels[i] + '.</strong> ' + (a.answer || '') + correctMark
                + '</div>';
        }).join('');

        return '<div style="margin-bottom:18px;page-break-inside:avoid;">'
            + '<p style="margin:0 0 6px 0;"><strong>' + (idx + 1) + '. ' + (q.questionDetail || '') + '</strong></p>'
            + optionsHTML
            + '</div>';
    }).join('');

    var answerKeyHTML = '';
    if (withAnswers) {
        answerKeyHTML = '<div style="margin-top:30px;border-top:2px solid #333;padding-top:10px;">'
            + '<h3>Answer Key</h3>'
            + '<table style="width:100%;border-collapse:collapse;">'
            + '<thead><tr><th style="border:1px solid #ccc;padding:4px;width:60px;">Q#</th>'
            + '<th style="border:1px solid #ccc;padding:4px;">Answer</th></tr></thead><tbody>'
            + questionBankData.map(function (q, idx) {
                var answers = q.examsAnswersResponses || [];
                var correct = answers.find(function (a) {
                    return a.isQuestionAnswer === true;
                });
                var correctIdx = answers.indexOf(correct);
                return '<tr>'
                    + '<td style="border:1px solid #ccc;padding:4px;text-align:center;">' + (idx + 1) + '</td>'
                    + '<td style="border:1px solid #ccc;padding:4px;">'
                    + (correct ? optionLabels[correctIdx] + '. ' + correct.answer : '-')
                    + '</td></tr>';
            }).join('')
            + '</tbody></table></div>';
    }

    return `
        <!DOCTYPE html>
        <html>
        <head>
            <title>Question Bank</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 30px; font-size: 13px; color: #111; }
                h2   { margin: 0; }
                .header { border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 20px; }
                .meta   { color: #555; font-size: 12px; margin-top: 4px; }
                @media print { button { display: none; } }
            </style>
        </head>
        <body>
            <div class="header">
                <h2>${institution || 'Question Bank'}</h2>
                <div class="meta">
                    Class: <strong>${currentClass}</strong> &nbsp;|&nbsp;
                    Subject: <strong>${currentSubject}</strong> &nbsp;|&nbsp;
                    Term: <strong>${currentTerm}</strong> &nbsp;|&nbsp;
                    Total Questions: <strong>${questionBankData.length}</strong>
                    ${withAnswers ? '&nbsp;|&nbsp;<strong style="color:green;">MARKING SCHEME</strong>' : ''}
                </div>
            </div>
            ${questionsHTML}
            ${answerKeyHTML}
        </body>
        </html>
    `;
}

function printQuestions(withAnswers) {
    var html = buildPrintHTML(withAnswers);
    var printWin = window.open('', '_blank', 'width=800,height=600');
    printWin.document.write(html);
    printWin.document.close();
    printWin.focus();
    printWin.print();
}

function downloadPDF() {
    var html = buildPrintHTML(true);
    var blob = new Blob([html], {type: 'text/html'});
    var url = URL.createObjectURL(blob);
    var a = document.createElement('a');
    a.href = url;
    a.download = 'QuestionBank_' + currentClass + '_' + currentSubject + '_' + currentTerm + '.html';
    a.click();
    URL.revokeObjectURL(url);
    swal({
        title: "Downloaded!",
        text: "Open the downloaded file in your browser and use Ctrl+P / Cmd+P to save as PDF.",
        type: "success"
    });
}


// ─── BULK UPLOAD ─────────────────────────────────────────────────────────────

// Download empty CSV/Excel template
document.getElementById("downloadTemplateBtn").addEventListener("click", function () {
    if (!currentClass || !currentSubject || !currentTerm) {
        swal({
            title: "Required",
            text: "Please select Class, Subject and Term before downloading the template.",
            type: "warning"
        });
        return;
    }

    // Build template using SheetJS
    var wb = XLSX.utils.book_new();

    // Questions sheet — headers match our parsing logic exactly
    var questionsHeaders = [["question", "optionA", "optionB", "optionC", "optionD", "correctAnswer"]];
    // Add 10 blank example rows
    for (var i = 0; i < 10; i++) {
        questionsHeaders.push(["", "", "", "", "", "A"]);  // correctAnswer hint: A, B, C or D
    }
    var ws = XLSX.utils.aoa_to_sheet(questionsHeaders);

    // Column widths
    ws["!cols"] = [
        {wch: 60}, // question
        {wch: 25}, // optionA
        {wch: 25}, // optionB
        {wch: 25}, // optionC
        {wch: 25}, // optionD
        {wch: 15}, // correctAnswer
    ];

    XLSX.utils.book_append_sheet(wb, ws, "Questions");

    // Instructions sheet
    var instructions = [
        ["QUESTION BANK BULK UPLOAD TEMPLATE"],
        [""],
        ["Instructions:"],
        ["1. Fill in the 'Questions' sheet only. Do not rename or delete any columns."],
        ["2. 'question'       — the full question text"],
        ["3. 'optionA' to 'optionD' — the four answer choices"],
        ["4. 'correctAnswer'  — must be exactly A, B, C or D (uppercase)"],
        ["5. Do not change the sheet name 'Questions'."],
        ["6. Save as .xlsx before uploading."],
        [""],
        ["Pre-filled parameters (do not change these in the file):"],
        ["Class:   " + currentClass],
        ["Subject: " + currentSubject],
        ["Term:    " + currentTerm],
        ["Institution: " + v],
    ];
    var wsInstr = XLSX.utils.aoa_to_sheet(instructions);
    wsInstr["!cols"] = [{wch: 70}];
    XLSX.utils.book_append_sheet(wb, wsInstr, "Instructions");

    XLSX.writeFile(wb, "QuestionBank_Template_" + currentClass + "_" + currentSubject + "_" + currentTerm + ".xlsx");
});

// Trigger file input on bulk upload button click
document.getElementById("bulkUploadBtn").addEventListener("click", function () {
    if (!currentClass || !currentSubject || !currentTerm) {
        swal({title: "Required", text: "Please select Class, Subject and Term before uploading.", type: "warning"});
        return;
    }
    document.getElementById("bulkUploadInput").click();
});

// Parse the uploaded Excel file
document.getElementById("bulkUploadInput").addEventListener("change", async function () {
    var file = this.files[0];
    if (!file) return;

    showSplash();
    try {
        var base64 = await fileToBase64(file);
        var parsedQuestions = parseBulkTemplate(base64);

        if (!parsedQuestions || parsedQuestions.length === 0) {
            swal({
                title: "Empty File",
                text: "No questions found in the uploaded file. Check the template format.",
                type: "warning"
            });
            hideSplash();
            return;
        }

        renderBulkPreview(parsedQuestions);
    } catch (e) {
        console.error("Error parsing bulk file:", e);
        swal({title: "Error", text: "Failed to parse the file: " + e.message, type: "error"});
    }
    hideSplash();
    // Reset input so same file can be re-uploaded
    this.value = '';
});

function parseBulkTemplate(base64String) {
    var binaryString = atob(base64String);
    var bytes = new Uint8Array(binaryString.length);
    for (var i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }

    var workbook = XLSX.read(bytes, {type: "array"});
    var sheetName = workbook.SheetNames[0]; // Always use first sheet "Questions"
    var sheet = XLSX.utils.sheet_to_json(workbook.Sheets[sheetName]);

    var answerMap = {'A': 0, 'B': 1, 'C': 2, 'D': 3};

    return sheet
        .filter(function (row) {
            return row.question && row.question.toString().trim();
        })
        .map(function (row) {
            var correctLetter = (row.correctAnswer || 'A').toString().trim().toUpperCase();
            var correctIndex = answerMap[correctLetter] !== undefined ? answerMap[correctLetter] : 0;
            var options = [
                (row.optionA || '').toString().trim(),
                (row.optionB || '').toString().trim(),
                (row.optionC || '').toString().trim(),
                (row.optionD || '').toString().trim(),
            ];
            return {
                questionDetail: row.question.toString().trim(),
                classId: currentClass,
                subjectId: currentSubject,
                term: currentTerm,
                institutionCode: v,
                examsAnswersRequests: options.map(function (opt, i) {
                    return {
                        id: null,
                        answer: opt,
                        isQuestionAnswer: i === correctIndex
                    };
                })
            };
        });
}

var bulkQuestionsData = [];

function renderBulkPreview(questions) {
    bulkQuestionsData = questions;
    $("#bulkPreviewBody").empty();

    questions.forEach(function (q, idx) {
        var correct = q.examsAnswersRequests.find(function (a) {
            return a.isQuestionAnswer;
        });
        var correctIdx = q.examsAnswersRequests.indexOf(correct);
        var correctLabel = optionLabels[correctIdx] + '. ' + (correct ? correct.answer : '');

        var row = '<tr>'
            + '<td>' + (idx + 1) + '</td>'
            + '<td>' + q.questionDetail + '</td>'
            + '<td>' + (q.examsAnswersRequests[0].answer || '') + '</td>'
            + '<td>' + (q.examsAnswersRequests[1].answer || '') + '</td>'
            + '<td>' + (q.examsAnswersRequests[2].answer || '') + '</td>'
            + '<td>' + (q.examsAnswersRequests[3].answer || '') + '</td>'
            + '<td><span class="label label-success">' + correctLabel + '</span></td>'
            + '</tr>';
        $("#bulkPreviewBody").append(row);
    });

    $("#bulkPreviewCount").text(" — " + questions.length + " question(s) ready to submit");
    $("#bulkPreviewSection").show();

    // Init DataTable for preview
    if ($.fn.DataTable.isDataTable("#bulkPreviewTable")) {
        $("#bulkPreviewTable").DataTable().destroy();
    }
    $("#bulkPreviewTable").dataTable({
        dom: "<'row'<'col-sm-6'l><'col-sm-6'f>>tp",
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        columnDefs: [{orderable: false, targets: [2, 3, 4, 5, 6]}]
    });
}

// Submit all bulk questions
document.getElementById("submitBulkBtn").addEventListener("click", async function () {
    if (!bulkQuestionsData.length) return;

    showSplash();
    try {
        await fetchPost("submitExamsQuestions", bulkQuestionsData);

        // Merge into local data store
        bulkQuestionsData.forEach(function (q) {
            questionBankData.push(Object.assign({id: Date.now() + Math.random()}, q, {
                examsAnswersResponses: q.examsAnswersRequests.map(function (a) {
                    return {answer: a.answer, isQuestionAnswer: a.isQuestionAnswer};
                })
            }));
        });

        // Refresh main table
        destroyTable();
        $("#questionBankBody").empty();
        buildTable(questionBankData);

        // Hide bulk section
        cancelBulk();

        swal({
            title: "Success!",
            text: bulkQuestionsData.length + " question(s) uploaded successfully.",
            type: "success"
        });
        bulkQuestionsData = [];
    } catch (e) {
        console.error("Bulk submit error:", e);
        swal({title: "Error", text: "Failed to submit questions.", type: "error"});
    }
    hideSplash();
});

// Cancel bulk preview
document.getElementById("cancelBulkBtn").addEventListener("click", cancelBulk);

function cancelBulk() {
    bulkQuestionsData = [];
    $("#bulkPreviewBody").empty();
    if ($.fn.DataTable.isDataTable("#bulkPreviewTable")) {
        $("#bulkPreviewTable").DataTable().destroy();
    }
    $("#bulkPreviewSection").hide();
}

// Convert file to base64
function fileToBase64(file) {
    return new Promise(function (resolve, reject) {
        var reader = new FileReader();
        reader.onload = function () {
            resolve(reader.result.split(",")[1]);
        };
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
}

// ─── HELPERS ─────────────────────────────────────────────────────────────────

function resetQuestionForm() {
    $("#questionDetail").val('');
    $("#option0, #option1, #option2, #option3").val('');
    $("input[name='correctAnswer']").prop("checked", false);
    $("#questionFormError").hide();
}

function showFormError(msg) {
    $("#questionFormError").text(msg).show();
}

function showSplash() {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
}

function hideSplash() {
    $('.splash').css('display', 'none');
}