$(function () {

    let header = `
        <div class="panel-body">
            <div class="panel-body row">
                <div class="pull-right col-lg-4">
                    <div class="col-lg-6">
                        <button class="btn btn-success col-lg-12" type="button" id="fetchStudentsBtn">
                            <i class="fa fa-search"></i> Fetch Students
                        </button>
                    </div>
                    <div class="col-lg-6">
                        <button class="btn btn-primary col-lg-12" type="button" id="exportStudentsBtn">
                            <i class="fa fa-download"></i> Export
                        </button>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-8 pull-left">
                        <div class="form-group col-lg-3">
                            <select class="form-control classGroupSelect">
                                <option value="">Select Class Group</option>
                            </select>
                        </div>
                        <div class="form-group col-lg-3">
                            <select class="form-control classSelect">
                                <option value="">Select Class</option>
                            </select>
                        </div>
                        <div class="form-group col-lg-3">
                            <select class="form-control studentStatusSelect">
                                <option value="">All Statuses</option>
                                <option value="Active">Active</option>
                                <option value="Completed">Completed</option>
                                <option value="Suspended">Suspended</option>
                                <option value="Dismissed">Dismissed</option>
                            </select>
                        </div>
                        <div class="form-group col-lg-3">
                            <select class="form-control genderSelect">
                                <option value="">All Genders</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                            </select>
                        </div>
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
                    <a class="closebox"><i class="fa fa-times"></i></a>
                </div>
                Student Records
            </div>
            <div class="panel-body">
                <table id="studentRecordsTable" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Last Name</th>
                            <th>First Name</th>
                            <th>Other Name</th>
                            <th>Gender</th>
                            <th>Class</th>
                            <th>Date of Birth</th>
                            <th>Date of Admission</th>
                            <th>Place of Birth</th>
                            <th>Country of Birth</th>
                            <th>Nationality</th>
                            <th>Denomination</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="studentRecordsBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- View / Edit Student Modal -->
    <div class="modal fade hmodal-info" id="studentModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg" style="margin: 60px auto; width: 90%;">
            <div class="modal-content">
                <div class="color-line"></div>
                <div class="modal-header">
                    <h4 class="modal-title" id="studentModalTitle">Edit Student Record</h4>
                    <small class="font-bold" id="studentModalSubtitle">Update and save student details below</small>
                </div>
                <div class="panel-body modalbody" style="border-bottom: 1px solid #a8bede;">

                    <div class="text-center m-b-md" id="studentModalTabs">
                        <a class="btn btn-primary studentTabBtn active-tab" data-tab="bioTab">Bio Data</a>
                        <a class="btn btn-default studentTabBtn" data-tab="parentsTab">Parents</a>
                    </div>

                    <!-- BIO DATA TAB -->
                    <div id="bioTab" class="student-tab-pane">
                        <form id="studentEditForm" role="form">
                            <div class="row">
                                <div class="col-lg-3 text-center">
                                    <label>Student Picture</label>
                                    <br/>
                                    <output class="imageOutput studentPicOutput"
                                            style="height:180px;width:180px;border-radius:10px;display:inline-block;overflow:hidden;">
                                        <div class="placeholder-avatar"
                                             style="height:180px;width:180px;background:#ccc;display:flex;align-items:center;justify-content:center;border-radius:10px;">
                                            <i class="fa fa-user fa-4x" style="color:#fff;"></i>
                                        </div>
                                    </output>
                                    <br/>
                                    <input class="imageInput studentPicInput" type="file"
                                           style="width:180px;padding:8px;display:inline;"
                                           accept="image/jpeg,image/png,image/jpg"
                                           onchange="imageChange(this)">
                                </div>
                                <div class="col-lg-9">
                                    <div class="row">
                                        <div class="form-group col-lg-4">
                                            <label>Student ID</label>
                                            <input type="text" id="editStudentId" class="form-control" readonly>
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>First Name</label>
                                            <input type="text" id="editFirstName" class="form-control" placeholder="First Name">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Other Name</label>
                                            <input type="text" id="editOtherName" class="form-control" placeholder="Other Name">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Last Name</label>
                                            <input type="text" id="editLastName" class="form-control" placeholder="Last Name">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Date of Birth</label>
                                            <input type="date" id="editDOB" class="form-control">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Date of Admission</label>
                                            <input type="date" id="editDOA" class="form-control">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Gender</label>
                                            <select id="editGender" class="form-control">
                                                <option value="">Select Gender</option>
                                                <option value="Male">Male</option>
                                                <option value="Female">Female</option>
                                            </select>
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Place of Birth</label>
                                            <input type="text" id="editPlaceOfBirth" class="form-control" placeholder="Place of Birth">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Country of Birth</label>
                                            <input type="text" id="editCountryOfBirth" class="form-control" placeholder="Country of Birth">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Nationality</label>
                                            <input type="text" id="editNationality" class="form-control" placeholder="Nationality">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Denomination</label>
                                            <input type="text" id="editDenomination" class="form-control" placeholder="Denomination">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Residential Locality</label>
                                            <input type="text" id="editResidentialLocality" class="form-control" placeholder="Residential Locality">
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Student Class</label>
                                            <select id="editStudentClass" class="form-control">
                                                <option value="">Select Class</option>
                                            </select>
                                        </div>
                                        <div class="form-group col-lg-4">
                                            <label>Status</label>
                                            <select id="editStatus" class="form-control">
                                                <option value="Active">Active</option>
                                                <option value="Completed">Completed</option>
                                                <option value="Suspended">Suspended</option>
                                                <option value="Dismissed">Dismissed</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Birth Certificate -->
                            <div class="row m-t-sm">
                                <div class="col-lg-6">
                                    <label>Birth Certificate [PDF]</label>
                                    <div class="fileOutput birthCertOutput"
                                         style="height:120px;width:100%;border:1px dashed #ccc;border-radius:6px;display:block;overflow:hidden;"></div>
                                    <input class="pdfInput birthCertInput" type="file"
                                           style="width:100%;padding:8px;display:inline;"
                                           accept=".pdf" onchange="uploadPDFSafe(this, this.parentElement.querySelector('.birthCertError'))">
                                    <p class="birthCertError fileError" style="color:red;display:none;">Only PDF files are allowed!</p>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- PARENTS TAB -->
                    <div id="parentsTab" class="student-tab-pane" style="display:none;">
                        <table class="table table-striped table-bordered" id="parentsTable">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Type</th>
                                    <th>Contact 1</th>
                                    <th>Contact 2</th>
                                    <th>Email</th>
                                    <th>Occupation</th>
                                </tr>
                            </thead>
                            <tbody id="parentsTableBody"></tbody>
                        </table>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" id="saveStudentBtn">Save Changes</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
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
    scriptLogic.setAttribute("src", "scripts/_studentRecords.js");
    scriptLogic.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(scriptLogic);
});