$(function () {

    let header = `
    <div class="panel-body">
        <div id="hbreadcrumb" class="pull-right">
        <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
        data-target="#mystaffpermissionsModal">Set Permissions</button>
        </div>
    </div>
    `

    //function institutionBuild() {
    let staffpermissions = `
    <div class="content animate-panel" id="pagecontent">
        <div class="hpanel">
            <div class="panel-heading">
                <div class="panel-tools">
                </div>
                User Permissions
            </div>
            <div class="panel-body">
                <table id="permissionsTable" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Position</th>
                        <th>Office</th>
                        <th width="15%">Age</th>
                        <th width="15%">Start date</th>
                        <th width="15%">Salary</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    

    
        <div class="modal fade hmodal-info" id="mystaffpermissionsModal" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="color-line"></div>
                    <div class="modal-header">
                        <h4 class="modal-title">Set Permissions</h4>
                        <small class="font-bold">A class group is a collection of classes eg. Creche, Nursery, KG, Lower Primary, Upper Primary etc.</small>
                    </div>
                    <div class="panel-body modalbody">
                    </div>
                    <div class="modal-footer">
                    <div class="form-group col-lg-4">
                       <select id="StaffList" class="form-control"
                              name="StaffList">
                              <option>Select Staff</option>
                       </select>
                      </div>
                        <button type="button" class="btn btn-default dismissPermissions" data-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary savePermissions">Save changes</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

      <!-- Footer-->
        <footer class="footer">
            <span class="pull-right">
                ORB
            </span>
            <span class="fa fa-copyright"></span>
            Astromy LLC 2013-<span id="copyrightYear"></span>
        </footer>
    `




    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', staffpermissions);
    document.getElementById("modalopn").addEventListener("click", modalopn);


    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_permissions.js");
    script14.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(script14);

});

function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    staffpermissionsIndut();
    permissionBuilder();

        const el=document.querySelector('.tabs');
        el.style.pointerEvents = 'none'; // Disable all clicks/interactions
        el.style.opacity = '0.5';        // Make it appear disabled (optional)
}

function staffpermissionsIndut() {
    let div = `
    <div class="row tabs">
        <div class="col-lg-12">
            <div class="hpanel">
                <ul class="nav nav-tabs">
                    <li class="active"><a data-toggle="tab" href="#setuptab"> SETUP </a></li>
                    <li class=""><a data-toggle="tab" href="#hrtab">HR </a></li>
                    <li class=""><a data-toggle="tab" href="#financetab"> FINANCE </a></li>
                    <li class=""><a data-toggle="tab" href="#academicstab"> ACADEMICS </a></li>
                    <li class=""><a data-toggle="tab" href="#administrationtab"> ADMINISTRATION </a></li>
                    <li class=""><a data-toggle="tab" href="#infirmarytab"> INFIRMARY </a></li>
                    <li class=""><a data-toggle="tab" href="#storestab"> STORES </a></li>
                    <li class=""><a data-toggle="tab" href="#teachingtab"> TEACHING </a></li>
                </ul>
                <div class="tab-content">
                    <div id="setuptab" class="tab-pane active">
                        <div class="panel-body">
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="institutionPermission" value="setup institution" type="checkbox" />
                                <label class="check-label" for="institutionPermission"> Institution </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classgrouppermission" value="setup classgroup" type="checkbox" />
                                <label class="check-label" for="classgrouppermission"> Class Groups </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classpermission" value="setup classes" type="checkbox" />
                                <label class="check-label" for="classpermission"> Classes </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="subjectpermission" value="setup subject" type="checkbox" />
                                <label class="check-label" for="subjectpermission"> Subjects </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="departmentpermission" value="setup department" type="checkbox" />
                                <label class="check-label" for="departmentpermission"> Departments </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="admissionspermission" value="setup admission" type="checkbox" />
                                <label class="check-label" for="admissionspermission"> Admissions </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="gradingpermission" value="setup grading" type="checkbox" />
                                <label class="check-label" for="gradingpermission"> Grading </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="permissionpermission" value="setup permissions" type="checkbox" />
                                <label class="check-label" for="permissionpermission"> Permissions </label>
                            </div>
                        </div>
                    </div>
                    
                    <div id="hrtab" class="tab-pane">
                        <div class="panel-body">
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="onloadingPermission" value="Human_Resource onboarding" type="checkbox" />
                                <label class="check-label" for="onloadingPermission"> On Loading </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="recordspermission" value="Human_Resource records" type="checkbox" />
                                <label class="check-label" for="recordspermission"> Records </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="leavepermission" value="Human_Resource leave" type="checkbox" />
                                <label class="check-label" for="leavepermission"> Leave </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="apraisalpermission" value="Human_Resource appraisals" type="checkbox" />
                                <label class="check-label" for="apraisalpermission"> Apraisal </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="designationpermission" value="Human_Resource designation" type="checkbox" />
                                <label class="check-label" for="designationpermission"> Designation </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="offloadingpermission" value="Human_Resource offboarding" type="checkbox" />
                                <label class="check-label" for="offloadingpermission"> Off Loading </label>
                            </div>
                        </div>
                    </div>
                
                    <div id="financetab" class="tab-pane">
                        <div class="panel-body">
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="billCreationPermission" value="Finance bill_creation" type="checkbox" />
                                <label class="check-label" for="billCreationPermission"> Bill Creation </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="billingPermission" value="Finance billing" type="checkbox" />
                                <label class="check-label" for="billingPermission"> Billing </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="feeCollectionPermission" value="Finance fee_collection" type="checkbox" />
                                <label class="check-label" for="feeCollectionPermission"> Fee Collection </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="paymentHistoryPermission" value="Finance payment_history" type="checkbox" />
                                <label class="check-label" for="paymentHistoryPermission"> Payment History </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="paymentCheckerPermission" value="Finance payment_checker" type="checkbox" />
                                <label class="check-label" for="paymentCheckerPermission"> Payment Checker </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="offloadingpermission" value="Finance salary_setup" type="checkbox" />
                                <label class="check-label" for="offloadingpermission"> Salary Setup </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="payslipGenerationPermission" value="Finance payslip" type="checkbox" />
                                <label class="check-label" for="payslipGenerationPermission"> Payslip Generation </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="ledgerBooksPermission" value="Finance ledger_books" type="checkbox" />
                                <label class="check-label" for="ledgerBooksPermission"> Ledger Books </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="incomeStatementPermission" value="Finance income_statement" type="checkbox" />
                                <label class="check-label" for="incomeStatementPermission"> Income Statement </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="cashFlowPermission" value="Finance cash_flow" type="checkbox" />
                                <label class="check-label" for="cashFlowPermission"> Cash Flow </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="trialBalancePermission" value="Finance trial_balance" type="checkbox" />
                                <label class="check-label" for="trialBalancePermission"> Trail Balance </label>
                            </div>                            
                        </div>
                    </div>
                
                    <div id="academicstab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="approvalPermission" value="Academics question_upload_approval" type="checkbox" />
                                <label class="check-label" for="approvalPermission"> Questions Approval </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="transcriptPermission" value="Academics transcript" type="checkbox" />
                                <label class="check-label" for="transcriptPermission"> Transcript </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="broadsheetPermission" value="Academics broadsheet" type="checkbox" />
                                <label class="check-label" for="broadsheetPermission"> Broadsheet </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="graduationListPermission" value="Academics graduation_list" type="checkbox" />
                                <label class="check-label" for="graduationListPermission"> Graduation List </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classTimetablePermission" value="Academics class_timetable" type="checkbox" />
                                <label class="check-label" for="classTimetablePermission"> Class Timetable </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="promotionPermission" value="Academics promotions_and_demotions" type="checkbox" />
                                <label class="check-label" for="promotionPermission"> Promotion </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="terminalReportPermission" value="Academics terminal_report" type="checkbox" />
                                <label class="check-label" for="terminalReportPermission"> Terminal Report </label>
                            </div>
                        </div>
                    </div>
                
                    <div id="administrationtab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="enrolmentPermission" value="Administration student_enrollment" type="checkbox" />
                                <label class="check-label" for="enrolmentPermission"> Student Enrollment </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="suspensionPermission" value="Administration suspended" type="checkbox" />
                                <label class="check-label" for="suspensionPermission"> Suspension </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="dismissalPermission" value="Administration dismissed" type="checkbox" />
                                <label class="check-label" for="dismissalPermission"> Dismissal </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="studentListPermission" value="Administration student_list" type="checkbox" />
                                <label class="check-label" for="studentListPermission"> Student List </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="studentRecordPermission" value="Administration student_record" type="checkbox" />
                                <label class="check-label" for="studentRecordPermission"> Student Records </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classListPermission" value="Administration class_list" type="checkbox" />
                                <label class="check-label" for="classListPermission"> Class List </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="academicTimetablePermission" value="Administration academic_timetable" type="checkbox" />
                                <label class="check-label" for="academicTimetablePermission"> Academic Timetable </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="idCardPermission" value="Administration id_card_generation" type="checkbox" />
                                <label class="check-label" for="idCardPermission"> ID Cards </label>
                            </div>
                        </div>
                    </div>
                
                    <div id="infirmarytab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="vitalsRecordPermission" value="Infirmary vitals_recording" type="checkbox" />
                                <label class="check-label" for="vitalsRecordPermission"> Vitals Record </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="diagnosisPermission" value="Infirmary diagnosis_recording" type="checkbox" />
                                <label class="check-label" for="diagnosisPermission"> Diagnosis </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="medicalHistoryPermission" value="Infirmary medical_history" type="checkbox" />
                                <label class="check-label" for="medicalHistoryPermission"> Medical History </label>
                            </div>
                        </div>
                    </div>
                
                    <div id="storestab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="salesPermission" value="Stores sales" type="checkbox" />
                                <label class="check-label" for="salesPermission"> Sales </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="inventoryPermission" value="Stores inventory" type="checkbox" />
                                <label class="check-label" for="inventoryPermission"> Inventory </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="insightPermission" value="Stores insight" type="checkbox" />
                                <label class="check-label" for="insightPermission"> Insight Report </label>
                             </div>
                        </div>
                    </div>
                
                    <div id="teachingtab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="questionUploadPermission" value="Teaching question_upload" type="checkbox" />
                                <label class="check-label" for="questionUploadPermission"> Question Upload </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="examsPermission" value="Teaching exams" type="checkbox" />
                                <label class="check-label" for="examsPermission"> Exams </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="assignmentPermission" value="Teaching assignment" type="checkbox" />
                                <label class="check-label" for="assignmentPermission"> Assignment </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="scoreUploadPermission" value="Teaching score_upload" type="checkbox" />
                                <label class="check-label" for="scoreUploadPermission"> Score Upload </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="assignmentReviewPermission" value="Teaching assignment_review" type="checkbox" />
                                <label class="check-label" for="assignmentReviewPermission"> Assignment Review </label>
                             </div>

                        </div>
                    </div>
                </div>


            </div>
        </div>
    </div>
    `
    document.getElementsByClassName("modalbody")[0].insertAdjacentHTML('beforeend', div);
}

