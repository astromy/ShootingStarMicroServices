$(function () {

    let header = `
        <div class="panel-body">
            <div class="panel-body">
                <!-- Row 1: Filters -->
                <div class="row m-b-sm">
                    <div class="col-lg-3">
                        <select class="form-control classGroupSelect">
                            <option value="">Select Class Group</option>
                        </select>
                    </div>
                    <div class="col-lg-2">
                        <select class="form-control classSelect">
                            <option value="">Select Class</option>
                        </select>
                    </div>
                    <div class="col-lg-3">
                        <select class="form-control subjectSelect">
                            <option value="">Select Subject</option>
                        </select>
                    </div>
                    <div class="col-lg-2">
                        <select class="form-control termSelect">
                            <option value="">Select Term</option>
                            <option value="Term 1">Term 1</option>
                            <option value="Term 2">Term 2</option>
                            <option value="Term 3">Term 3</option>
                        </select>
                    </div>
                </div>
                <!-- Row 2: Action buttons -->
                <div class="row">
                    <div class="col-lg-12">
                        <button class="btn btn-success" id="fetchAssignmentsBtn" type="button">
                            <i class="fa fa-search"></i> Fetch Assignments
                        </button>
                        <button class="btn btn-primary m-l-sm" id="createAssignmentBtn" type="button">
                            <i class="fa fa-plus"></i> Create Assignment
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;

    let pageContent = `
    <div class="content animate-panel" id="pagecontent">
        <div class="hpanel">
            <div class="panel-heading">
                <div class="panel-tools">
                    <a class="showhide"><i class="fa fa-chevron-up"></i></a>
                </div>
                Assignment Generator
            </div>
            <div class="panel-body">
                <table id="assignmentTable" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Title</th>
                            <th>Class</th>
                            <th>Subject</th>
                            <th>Term</th>
                            <th>Delivery</th>
                            <th>Selection Mode</th>
                            <th>Questions</th>
                            <th>Deadline</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="assignmentTableBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- ═══ CREATE / EDIT ASSIGNMENT MODAL ═══ -->
    <div class="modal fade hmodal-info" id="assignmentModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg" style="width:90%;margin:40px auto;">
            <div class="modal-content">
                <div class="color-line"></div>
                <div class="modal-header">
                    <h4 class="modal-title" id="assignmentModalTitle">Create Assignment</h4>
                    <small class="font-bold" id="assignmentModalSubtitle">Configure assignment settings and select questions</small>
                </div>
                <div class="panel-body modalbody" style="border-bottom:1px solid #a8bede;">

                    <!-- Step tabs -->
                    <div class="text-center m-b-md">
                        <a class="btn btn-primary assignmentStepBtn active-tab" data-step="step1">
                            1. Settings
                        </a>
                        <a class="btn btn-default assignmentStepBtn" data-step="step2">
                            2. Select Questions
                        </a>
                        <a class="btn btn-default assignmentStepBtn" data-step="step3">
                            3. Preview &amp; Publish
                        </a>
                    </div>

                    <!-- ── STEP 1: Assignment Settings ── -->
                    <div id="step1" class="assignment-step">
                        <div class="row">
                            <div class="form-group col-lg-8">
                                <label>Assignment Title <span class="text-danger">*</span></label>
                                <input type="text" id="assignmentTitle" class="form-control"
                                       placeholder="e.g. Mid-Term Mathematics Assignment">
                            </div>
                            <div class="form-group col-lg-4">
                                <label>Status</label>
                                <select id="assignmentStatus" class="form-control">
                                    <option value="PUBLISHED">Published</option>
                                    <option value="DRAFT">Save as Draft</option>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-group col-lg-4">
                                <label>Target Class <span class="text-danger">*</span></label>
                                <select id="modalClassSelect" class="form-control">
                                    <option value="">Select Class</option>
                                </select>
                            </div>
                            <div class="form-group col-lg-4">
                                <label>Subject <span class="text-danger">*</span></label>
                                <select id="modalSubjectSelect" class="form-control">
                                    <option value="">Select Subject</option>
                                </select>
                            </div>
                            <div class="form-group col-lg-4">
                                <label>Term <span class="text-danger">*</span></label>
                                <select id="modalTermSelect" class="form-control">
                                    <option value="">Select Term</option>
                                    <option value="Term 1">Term 1</option>
                                    <option value="Term 2">Term 2</option>
                                    <option value="Term 3">Term 3</option>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <!-- Delivery Mode -->
                            <div class="form-group col-lg-4">
                                <label>Delivery Mode <span class="text-danger">*</span></label>
                                <div class="row m-t-xs">
                                    <div class="col-lg-6">
                                        <div class="assignment-mode-card" data-mode="ONLINE" data-group="delivery">
                                            <i class="fa fa-laptop fa-2x text-info"></i>
                                            <div><strong>Online</strong></div>
                                            <small>Students submit digitally</small>
                                        </div>
                                    </div>
                                    <div class="col-lg-6">
                                        <div class="assignment-mode-card" data-mode="PRINT" data-group="delivery">
                                            <i class="fa fa-print fa-2x text-warning"></i>
                                            <div><strong>Print</strong></div>
                                            <small>Physical paper copy</small>
                                        </div>
                                    </div>
                                </div>
                                <input type="hidden" id="deliveryMode" value="ONLINE">
                            </div>

                            <!-- Selection Mode -->
                            <div class="form-group col-lg-8">
                                <label>Question Selection Mode <span class="text-danger">*</span></label>
                                <div class="row m-t-xs">
                                    <div class="col-lg-4">
                                        <div class="assignment-mode-card" data-mode="SAME_ORDER" data-group="selection">
                                            <i class="fa fa-list-ol fa-2x text-success"></i>
                                            <div><strong>Same for All</strong></div>
                                            <small>Identical questions &amp; order for every student</small>
                                        </div>
                                    </div>
                                    <div class="col-lg-4">
                                        <div class="assignment-mode-card" data-mode="SHUFFLED_ORDER" data-group="selection">
                                            <i class="fa fa-random fa-2x text-warning"></i>
                                            <div><strong>Shuffled Order</strong></div>
                                            <small>Same questions, different numbering per student</small>
                                        </div>
                                    </div>
                                    <div class="col-lg-4">
                                        <div class="assignment-mode-card" data-mode="DIFFERENT_PER_STUDENT" data-group="selection">
                                            <i class="fa fa-users fa-2x text-danger"></i>
                                            <div><strong>Different per Student</strong></div>
                                            <small>Each student gets a unique random subset</small>
                                        </div>
                                    </div>
                                </div>
                                <input type="hidden" id="selectionMode" value="SAME_ORDER">
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-group col-lg-4">
                                <label>Number of Questions <span class="text-danger">*</span></label>
                                <input type="number" id="questionCount" class="form-control"
                                       min="1" placeholder="e.g. 20">
                            </div>
                            <div class="form-group col-lg-4">
                                <label>Submission Deadline <span class="text-danger">*</span></label>
                                <input type="datetime-local" id="assignmentDeadline" class="form-control">
                            </div>
                            <div class="col-lg-4" style="padding-top:24px;">
                                <button class="btn btn-primary col-lg-12" id="loadQuestionsBtn" type="button">
                                    <i class="fa fa-arrow-right"></i> Next: Select Questions
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- ── STEP 2: Select Questions ── -->
                    <div id="step2" class="assignment-step" style="display:none;">
                        <div class="row m-b-sm">
                            <div class="col-lg-6">
                                <h5>
                                    Available Questions
                                    <span id="availableCount" class="label label-default m-l-sm">0</span>
                                </h5>
                            </div>
                            <div class="col-lg-6 text-right">
                                <button class="btn btn-xs btn-default" id="selectAllBtn">Select All</button>
                                <button class="btn btn-xs btn-default m-l-xs" id="clearSelectionBtn">Clear</button>
                                <span class="m-l-sm">
                                    Selected: <strong id="selectedCount">0</strong>
                                    / <strong id="requiredCount">0</strong> required
                                </span>
                            </div>
                        </div>

                        <div id="questionPoolContainer"
                             style="max-height:400px;overflow-y:auto;border:1px solid #ddd;border-radius:4px;padding:10px;">
                            <div class="text-center text-muted" id="questionPoolEmpty">
                                <i class="fa fa-info-circle"></i>
                                Complete Step 1 and click "Next: Select Questions" to load the question bank.
                            </div>
                        </div>

                        <div class="row m-t-sm">
                            <div class="col-lg-12 text-right">
                                <button class="btn btn-default" id="backToStep1Btn">
                                    <i class="fa fa-arrow-left"></i> Back
                                </button>
                                <button class="btn btn-primary m-l-sm" id="goToPreviewBtn">
                                    <i class="fa fa-arrow-right"></i> Next: Preview
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- ── STEP 3: Preview & Publish ── -->
                    <div id="step3" class="assignment-step" style="display:none;">
                        <div class="row m-b-sm">
                            <div class="col-lg-12">
                                <div class="hpanel" style="margin:0;">
                                    <div class="panel-body" style="background:#f7f9fa;">
                                        <div class="row">
                                            <div class="col-lg-6">
                                                <strong>Title:</strong>
                                                <span id="preview-title">—</span><br>
                                                <strong>Class:</strong>
                                                <span id="preview-class">—</span> &nbsp;|&nbsp;
                                                <strong>Subject:</strong>
                                                <span id="preview-subject">—</span> &nbsp;|&nbsp;
                                                <strong>Term:</strong>
                                                <span id="preview-term">—</span>
                                            </div>
                                            <div class="col-lg-6">
                                                <strong>Delivery:</strong>
                                                <span id="preview-delivery">—</span><br>
                                                <strong>Mode:</strong>
                                                <span id="preview-mode">—</span> &nbsp;|&nbsp;
                                                <strong>Questions:</strong>
                                                <span id="preview-qcount">—</span><br>
                                                <strong>Deadline:</strong>
                                                <span id="preview-deadline">—</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Preview question list -->
                        <div id="previewQuestionList"
                             style="max-height:350px;overflow-y:auto;border:1px solid #ddd;border-radius:4px;padding:10px;">
                        </div>

                        <!-- Print / Download -->
                        <div class="row m-t-sm">
                            <div class="col-lg-12">
                                <button class="btn btn-default btn-sm" id="printAssignmentBtn">
                                    <i class="fa fa-print"></i> Print Assignment
                                </button>
                                <button class="btn btn-default btn-sm m-l-sm" id="printWithAnswersAssignBtn">
                                    <i class="fa fa-print"></i> Print with Answer Key
                                </button>
                                <button class="btn btn-info btn-sm m-l-sm" id="downloadAssignmentBtn">
                                    <i class="fa fa-download"></i> Download PDF
                                </button>
                            </div>
                        </div>

                        <div class="row m-t-sm">
                            <div class="col-lg-12 text-right">
                                <button class="btn btn-default" id="backToStep2Btn">
                                    <i class="fa fa-arrow-left"></i> Back
                                </button>
                                <button class="btn btn-success m-l-sm" id="publishAssignmentBtn">
                                    <i class="fa fa-cloud-upload"></i>
                                    <span id="publishBtnText">Publish Assignment</span>
                                </button>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <!-- ═══ VIEW ASSIGNMENT MODAL ═══ -->
    <div class="modal fade hmodal-success" id="viewAssignmentModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg" style="width:85%;margin:50px auto;">
            <div class="modal-content">
                <div class="color-line"></div>
                <div class="modal-header">
                    <h4 class="modal-title" id="viewAssignmentTitle">Assignment Details</h4>
                    <small class="font-bold" id="viewAssignmentMeta"></small>
                </div>
                <div class="panel-body modalbody" style="border-bottom:1px solid #a8bede;">
                    <div class="row m-b-sm">
                        <div class="col-lg-12">
                            <button class="btn btn-default btn-sm" id="viewPrintBtn">
                                <i class="fa fa-print"></i> Print
                            </button>
                            <button class="btn btn-default btn-sm m-l-sm" id="viewPrintAnswersBtn">
                                <i class="fa fa-print"></i> Print with Answers
                            </button>
                            <button class="btn btn-info btn-sm m-l-sm" id="viewDownloadBtn">
                                <i class="fa fa-download"></i> Download
                            </button>
                        </div>
                    </div>
                    <div id="viewQuestionList"
                         style="max-height:500px;overflow-y:auto;border:1px solid #ddd;border-radius:4px;padding:12px;">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <style>
        .assignment-mode-card {
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            padding: 12px 8px;
            text-align: center;
            cursor: pointer;
            transition: all 0.2s;
            margin-bottom: 8px;
            background: #fff;
        }
        .assignment-mode-card:hover {
            border-color: #3498db;
            background: #f0f8ff;
        }
        .assignment-mode-card.selected {
            border-color: #3498db;
            background: #e8f4fd;
            box-shadow: 0 0 0 3px rgba(52,152,219,0.2);
        }
        .assignment-mode-card small {
            display: block;
            color: #888;
            font-size: 10px;
            margin-top: 4px;
        }
        .question-pool-item {
            border: 1px solid #e8e8e8;
            border-radius: 6px;
            padding: 10px 12px;
            margin-bottom: 8px;
            cursor: pointer;
            transition: background 0.15s;
        }
        .question-pool-item:hover { background: #f5f5f5; }
        .question-pool-item.selected-q {
            background: #e8f4fd;
            border-color: #3498db;
        }
        .question-pool-item .q-number {
            display: inline-block;
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background: #3498db;
            color: #fff;
            text-align: center;
            line-height: 28px;
            font-size: 12px;
            font-weight: bold;
            margin-right: 8px;
        }
        .question-pool-item.selected-q .q-number { background: #62cb31; }
        .preview-question-item {
            border-bottom: 1px solid #eee;
            padding: 10px 0;
        }
        .preview-question-item:last-child { border-bottom: none; }
        .answer-option { margin: 3px 0; color: #555; }
        .answer-option.correct { color: #62cb31; font-weight: bold; }
    </style>

    <footer class="footer">
        <span class="pull-right">ORB</span>
        <span class="fa fa-copyright"></span>
        Astromy LLC 2013-<span id="copyrightYear"></span>
    </footer>
    `;

    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', pageContent);

    var scriptLogic = document.createElement("script");
    scriptLogic.setAttribute("type", "text/javascript");
    scriptLogic.setAttribute("src", "scripts/_staffAssignment.js");
    scriptLogic.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(scriptLogic);
});