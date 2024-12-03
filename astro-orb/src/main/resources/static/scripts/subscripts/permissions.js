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
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
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

});

function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    staffpermissionsIndut();
}

function staffpermissionsIndut() {
    let div = `
    <div class="row">
        <div class="col-lg-12">
            <div class="hpanel">
                <ul class="nav nav-tabs">
                    <li class="active"><a data-toggle="tab" href="#setuptab"> SETUP </a></li>
                    <li class=""><a data-toggle="tab" href="#hrtab">HR </a></li>
                    <li class=""><a data-toggle="tab" href="#financetab"> FINANCE </a></li>
                    <li class=""><a data-toggle="tab" href="#academicstab"> ACADEMICS </a></li>
                    <li class=""><a data-toggle="tab" href="#administrationtab"> ADMINISTRATION </a></li>
                    <li class=""><a data-toggle="tab" href="#infairmarytab"> INFAIRMARY </a></li>
                    <li class=""><a data-toggle="tab" href="#storestab"> STORES </a></li>
                    <li class=""><a data-toggle="tab" href="#teachingtab"> TEACHING </a></li>
                </ul>
                <div class="tab-content">
                    <div id="setuptab" class="tab-pane active">
                        <div class="panel-body">
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="institutionPermission" type="checkbox" />
                                <label class="check-label" for="institutionPermission"> Institution </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classgrouppermission" type="checkbox" />
                                <label class="check-label" for="classgrouppermission"> Class Groups </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classpermission" type="checkbox" />
                                <label class="check-label" for="classpermission"> Classes </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="subjectpermission" type="checkbox" />
                                <label class="check-label" for="subjectpermission"> Subjects </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="departmentpermission" type="checkbox" />
                                <label class="check-label" for="departmentpermission"> Departments </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="admissionspermission" type="checkbox" />
                                <label class="check-label" for="admissionspermission"> Admissions </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="gradingpermission" type="checkbox" />
                                <label class="check-label" for="gradingpermission"> Grading </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="permissionpermission" type="checkbox" />
                                <label class="check-label" for="permissionpermission"> Permissions </label>
                            </div>
                        </div>
                    </div>
                    
                    <div id="hrtab" class="tab-pane">
                        <div class="panel-body">
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="onloadingPermission" type="checkbox" />
                                <label class="check-label" for="onloadingPermission"> On Loading </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="recordspermission" type="checkbox" />
                                <label class="check-label" for="recordspermission"> Records </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="leavepermission" type="checkbox" />
                                <label class="check-label" for="leavepermission"> Leave </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="apraisalpermission" type="checkbox" />
                                <label class="check-label" for="apraisalpermission"> Apraisal </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="designationpermission" type="checkbox" />
                                <label class="check-label" for="designationpermission"> Designation </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="offloadingpermission" type="checkbox" />
                                <label class="check-label" for="offloadingpermission"> Off Loading </label>
                            </div>
                        </div>
                    </div>
                
                    <div id="financetab" class="tab-pane">
                        <div class="panel-body">
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="onloadingPermission" type="checkbox" />
                                <label class="check-label" for="onloadingPermission"> Bill Creation </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="recordspermission" type="checkbox" />
                                <label class="check-label" for="recordspermission"> Billing </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="leavepermission" type="checkbox" />
                                <label class="check-label" for="leavepermission"> Fee Collection </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="apraisalpermission" type="checkbox" />
                                <label class="check-label" for="apraisalpermission"> Payment History </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="designationpermission" type="checkbox" />
                                <label class="check-label" for="designationpermission"> Payment Checker </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="offloadingpermission" type="checkbox" />
                                <label class="check-label" for="offloadingpermission"> Salary Setup </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="onloadingPermission" type="checkbox" />
                                <label class="check-label" for="onloadingPermission"> Payslip Generation </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="recordspermission" type="checkbox" />
                                <label class="check-label" for="recordspermission"> Ledger Books </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="leavepermission" type="checkbox" />
                                <label class="check-label" for="leavepermission"> Income Statement </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="apraisalpermission" type="checkbox" />
                                <label class="check-label" for="apraisalpermission"> Cash Flow </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="designationpermission" type="checkbox" />
                                <label class="check-label" for="designationpermission"> Trail Balance </label>
                            </div>                            
                        </div>
                    </div>
                
                    <div id="academicstab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="approvalPermission" type="checkbox" />
                                <label class="check-label" for="approvalPermission"> Questions Approval </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="transcriptPermission" type="checkbox" />
                                <label class="check-label" for="transcriptPermission"> Transcript </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="broadsheetPermission" type="checkbox" />
                                <label class="check-label" for="broadsheetPermission"> Broadsheet </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="graduationListPermission" type="checkbox" />
                                <label class="check-label" for="graduationListPermission"> Graduation List </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classTimetablePermission" type="checkbox" />
                                <label class="check-label" for="classTimetablePermission"> Class Timetable </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="promotionPermission" type="checkbox" />
                                <label class="check-label" for="promotionPermission"> Promotion </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="terminalReportPermission" type="checkbox" />
                                <label class="check-label" for="terminalReportPermission"> Terminal Report </label>
                            </div>

                        </div>
                    </div>
                
                    <div id="administrationtab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="enrolmentPermission" type="checkbox" />
                                <label class="check-label" for="enrolmentPermission"> Student Enrollment </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="suspensionPermission" type="checkbox" />
                                <label class="check-label" for="suspensionPermission"> Suspension </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="dismissalPermission" type="checkbox" />
                                <label class="check-label" for="dismissalPermission"> Dismissal </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="studentListPermission" type="checkbox" />
                                <label class="check-label" for="studentListPermission"> Student List </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="classListPermission" type="checkbox" />
                                <label class="check-label" for="classListPermission"> Class List </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="academicTimetablePermission" type="checkbox" />
                                <label class="check-label" for="academicTimetablePermission"> Academic Timetable </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="idCardPermission" type="checkbox" />
                                <label class="check-label" for="idCardPermission"> ID Cards </label>
                            </div>

                        </div>
                    </div>
                
                    <div id="infairmarytab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="vitalsRecordPermission" type="checkbox" />
                                <label class="check-label" for="vitalsRecordPermission"> Vitals Record </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="diagnosisPermission" type="checkbox" />
                                <label class="check-label" for="diagnosisPermission"> Diagnosis </label>
                            </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="medicalHistoryPermission" type="checkbox" />
                                <label class="check-label" for="medicalHistoryPermission"> Medical History </label>
                            </div>

                        </div>
                    </div>
                
                    <div id="storestab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="salesPermission" type="checkbox" />
                                <label class="check-label" for="salesPermission"> Sales </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="inventoryPermission" type="checkbox" />
                                <label class="check-label" for="inventoryPermission"> Inventory </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="insightPermission" type="checkbox" />
                                <label class="check-label" for="insightPermission"> Insight Report </label>
                             </div>

                        </div>
                    </div>
                
                    <div id="teachingtab" class="tab-pane">
                        <div class="panel-body">

                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="questionUploadPermission" type="checkbox" />
                                <label class="check-label" for="questionUploadPermission"> Question Upload </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="examsPermission" type="checkbox" />
                                <label class="check-label" for="examsPermission"> Exams </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="assignmentPermission" type="checkbox" />
                                <label class="check-label" for="assignmentPermission"> Assignment </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="scoreUploadPermission" type="checkbox" />
                                <label class="check-label" for="scoreUploadPermission"> Score Upload </label>
                             </div>
                            <div class="form-check form-switch col-md-3">
                                <input class="form-check-input" id="assignmentReviewPermission" type="checkbox" />
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

