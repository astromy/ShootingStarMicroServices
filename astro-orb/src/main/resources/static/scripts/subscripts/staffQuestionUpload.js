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
                <!-- Row 2: Action Buttons -->
                <div class="row">
                    <div class="col-lg-12">
                        <button class="btn btn-success" id="fetchQuestionsBtn" type="button">
                            <i class="fa fa-search"></i> Fetch Questions
                        </button>
                        <button class="btn btn-primary m-l-sm" id="addQuestionBtn" type="button">
                            <i class="fa fa-plus"></i> Add Question
                        </button>
                        <button class="btn btn-default m-l-sm" id="downloadTemplateBtn" type="button">
                            <i class="fa fa-download"></i> Download Template
                        </button>
                        <button class="btn btn-warning m-l-sm" id="bulkUploadBtn" type="button">
                            <i class="fa fa-upload"></i> Bulk Upload
                        </button>
                        <input type="file" id="bulkUploadInput" accept=".xlsx,.xls,.csv" style="display:none;">
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
                Question Bank
            </div>
            <div class="panel-body">
                <div class="row m-b-sm">
                    <div class="col-lg-12">
                        <button class="btn btn-default btn-sm" id="printQuestionsBtn">
                            <i class="fa fa-print"></i> Print Questions
                        </button>
                        <button class="btn btn-default btn-sm" id="printWithAnswersBtn">
                            <i class="fa fa-print"></i> Print with Answers
                        </button>
                        <button class="btn btn-info btn-sm" id="downloadQuestionsBtn">
                            <i class="fa fa-download"></i> Download PDF
                        </button>
                    </div>
                </div>

                <!-- Bulk upload preview — shown only after file is selected -->
                <div id="bulkPreviewSection" style="display:none;">
                    <div class="row m-b-sm">
                        <div class="col-lg-12">
                            <h5 style="margin:0 0 8px 0;">
                                <i class="fa fa-table"></i> Bulk Upload Preview
                                <small class="text-muted" id="bulkPreviewCount"></small>
                            </h5>
                        </div>
                    </div>
                    <table id="bulkPreviewTable" class="table table-striped table-bordered table-hover table-condensed" width="100%">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Question</th>
                                <th>Option A</th>
                                <th>Option B</th>
                                <th>Option C</th>
                                <th>Option D</th>
                                <th>Correct Answer</th>
                            </tr>
                        </thead>
                        <tbody id="bulkPreviewBody"></tbody>
                    </table>
                    <div class="row m-t-sm">
                        <div class="col-lg-12">
                            <button class="btn btn-success" id="submitBulkBtn">
                                <i class="fa fa-cloud-upload"></i> Submit All Questions
                            </button>
                            <button class="btn btn-default m-l-sm" id="cancelBulkBtn">
                                <i class="fa fa-times"></i> Cancel
                            </button>
                        </div>
                    </div>
                </div>
                <table id="questionBankTable" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Question</th>
                            <th>Subject</th>
                            <th>Class</th>
                            <th>Term</th>
                            <th>Options</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="questionBankBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Add / Edit Question Modal -->
    <div class="modal fade hmodal-info" id="questionModal" tabindex="-1" role="dialog">
        <div class="modal-dialog" style="margin: 60px auto; width: 700px;">
            <div class="modal-content">
                <div class="color-line"></div>
                <div class="modal-header">
                    <h4 class="modal-title" id="questionModalTitle">Add Question</h4>
                    <small class="font-bold" id="questionModalSubtitle">Fill in the question and all four options</small>
                </div>
                <div class="panel-body modalbody" style="border-bottom: 1px solid #a8bede;">
                    <div class="form-group">
                        <label><strong>Question</strong></label>
                        <textarea id="questionDetail" class="form-control" rows="3"
                                  placeholder="Enter question here..."></textarea>
                    </div>

                    <div class="row">
                        <div class="col-lg-12">
                            <label><strong>Options &amp; Marking Scheme</strong>
                                <small class="text-muted"> — tick the correct answer</small>
                            </label>
                        </div>
                    </div>

                    <!-- Option A -->
                    <div class="row m-t-xs">
                        <div class="col-lg-1 text-center" style="padding-top:8px;">
                            <input type="radio" name="correctAnswer" value="0" id="correct0">
                            <label for="correct0" class="option-label">A</label>
                        </div>
                        <div class="col-lg-11">
                            <input type="text" id="option0" class="form-control" placeholder="Option A">
                        </div>
                    </div>
                    <!-- Option B -->
                    <div class="row m-t-xs">
                        <div class="col-lg-1 text-center" style="padding-top:8px;">
                            <input type="radio" name="correctAnswer" value="1" id="correct1">
                            <label for="correct1" class="option-label">B</label>
                        </div>
                        <div class="col-lg-11">
                            <input type="text" id="option1" class="form-control" placeholder="Option B">
                        </div>
                    </div>
                    <!-- Option C -->
                    <div class="row m-t-xs">
                        <div class="col-lg-1 text-center" style="padding-top:8px;">
                            <input type="radio" name="correctAnswer" value="2" id="correct2">
                            <label for="correct2" class="option-label">C</label>
                        </div>
                        <div class="col-lg-11">
                            <input type="text" id="option2" class="form-control" placeholder="Option C">
                        </div>
                    </div>
                    <!-- Option D -->
                    <div class="row m-t-xs">
                        <div class="col-lg-1 text-center" style="padding-top:8px;">
                            <input type="radio" name="correctAnswer" value="3" id="correct3">
                            <label for="correct3" class="option-label">D</label>
                        </div>
                        <div class="col-lg-11">
                            <input type="text" id="option3" class="form-control" placeholder="Option D">
                        </div>
                    </div>

                    <p id="questionFormError" class="text-danger m-t-sm" style="display:none;"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="saveQuestionBtn">Save Question</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Printable / Downloadable area (hidden from view) -->
    <div id="printArea" style="display:none;"></div>

    <footer class="footer">
        <span class="pull-right">ORB</span>
        <span class="fa fa-copyright"></span>
        Astromy LLC 2013-<span id="copyrightYear"></span>
    </footer>
    `;

    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', pageContent);

    // Load xlsx.full.min.js BEFORE the logic script — same pattern as studentBulkUpload.js
    var scriptXLSX = document.createElement("script");
    scriptXLSX.setAttribute("type", "text/javascript");
    scriptXLSX.setAttribute("src", "scripts/xlsx.full.min.js");

    var scriptLogic = document.createElement("script");
    scriptLogic.setAttribute("type", "text/javascript");
    scriptLogic.setAttribute("src", "scripts/_staffQuestionUpload.js");

    document.getElementsByTagName("body")[0].appendChild(scriptXLSX);
    document.getElementsByTagName("body")[0].appendChild(scriptLogic);
});