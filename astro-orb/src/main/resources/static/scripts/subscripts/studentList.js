$(function () {

    let header = `
            <div class="panel-body">
                <div class=" panel-body row">
                    <div class="pull-right col-lg-5">

                            <div id="classStudentListExport" class="col-lg-4">
                                <button class="btn btn-primary col-lg-12" type="button" id="classStudentListExportBtn">Export Class List</button>
                            </div>
                            <div id="hbreadcrumb" class="col-lg-4">
                                    <div class="row"  style="display: none;">
                                        <div class="col-md-12">
                                            <div>
                                                <label for="">Score File [Excel]</label>
                                            </div>
                                            <input class="studentsInput" type="file" id="studentsInput" style="width: 200px;padding: 12px;display: inline; visibility: hidden;" accept=".xls,.xlsx,.csv"/>
                                            <p id="error" class="fileError" style="color: red; display: none;">Only PDF files are allowed!</p>
                                        </div>
                                    </div>
                                <button class="btn btn-info studentsUploadBtn" type="button" id="studentsUploadBtn">Upload Student List</button>
                            </div>
                            <div id="studentsSubmitBtn" class="col-lg-4">
                                    <button class="btn btn-success col-lg-12" type="button" id="studentsSubmitBtn">Submit</button>
                            </div>

                        </div>
                    </div>

                    <div class="row">
                        <div class="col-lg-4 pull-left">
                            <div class="form-group col-lg-4 ">
                                <select class="form-control classGroupSelect">
                                    <option>Select Class Group</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-4 ">
                                <select class="form-control classSelect">
                                    <option>Select Class</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-4">
                                <select class="form-control studentStatus">
                                    <option>Select Status</option>
                                 </select>
                            </div>
                        </div>
                    </div>
            </div>
    `

    //function institutionBuild() {
    let scoreUpload = `
    
    
    <div class="content animate-panel" id="pagecontent">
    <div class="hpanel">

        <div class="panel-heading">
            <div class="panel-tools">
                <a class="showhide"><i class="fa fa-chevron-up"></i></a>
                <a class="closebox"><i class="fa fa-times"></i></a>
            </div>
            Student List
        </div>
        <div class="panel-body">
            <table id="studentsListTable" class="table table-striped table-bordered table-hover" width="150%">
                <thead>
                <tr id="studentsTableHead">
                    <th>Student ID</th>
                    <th>Student Last Name</th>
                    <th>Student First Name</th>
                    <th>Student Other Names</th>
                    <th>Student Date of Birth</th>
                    <th>Student Date of Admission</th>
                    <th>Student Place of Birth</th>
                    <th>Student Gender</th>
                    <th>Student Country of Birth</th>
                    <th>Student Nationality</th>
                    <th>Student Denomination</th>
                    <th>Student Status</th>
                    <th>Student Class</th>
                </tr>
                </thead>
                <tbody id="studentsTableBody"></tbody>
            </table>

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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', scoreUpload);
  /*  document.getElementsByClassName("test")[0].addEventListener("click", scoreUploadIndut);
    document.getElementById("modalopn").addEventListener("click", modalopn);*/

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_studentList.js");

    var script15 = document.createElement("script");
    script15.setAttribute("type", "text/javascript");
    script15.setAttribute("src", "scripts/xlsx.full.min.js");
    document.getElementsByTagName("body")[0].appendChild(script15);
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    scoreUploadIndut();
}

function scoreUploadIndut() {
    let div = `
    <div class="row"><label class="col-sm-3 control-label">Group Name</label>
        <div class="col-sm-9">
            <div class="row">
                <div class="col-md-12"><input type="text" placeholder="Enter Class Group Name" class="form-control newscoreUploadtxt"></div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[0].insertAdjacentHTML('beforeend', div);
}

