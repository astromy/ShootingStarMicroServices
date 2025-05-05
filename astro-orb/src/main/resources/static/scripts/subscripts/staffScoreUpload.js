$(function () {

    let header = `
            <div class="panel-body">
                <div class=" panel-body row">
                    <div class="pull-right col-lg-5">

                            <div id="classListExport" class="col-lg-4">
                                <button class="btn btn-primary col-lg-12" type="button" id="classListExport">Export Class List</button>
                            </div>
                            <div id="hbreadcrumb" class="col-lg-4">
                                    <div class="row"  style="display: none;">
                                        <div class="col-md-12">
                                            <div>
                                                <label for="">Score File [Excel]</label>
                                            </div>
                                            <input class="scoreInput" type="file" id="scoreInput" style="width: 200px;padding: 12px;display: inline; visibility: hidden;" accept=".xls,.xlsx,.csv"/>
                                            <p id="error" class="fileError" style="color: red; display: none;">Only PDF files are allowed!</p>
                                        </div>
                                    </div>
                                <button class="btn btn-info staffScoreUploadBtn" type="button" id="staffScoreUploadBtn">Upload Subject Score</button>
                            </div>
                            <div id="scoreSubmitExport" class="col-lg-4">
                                    <button class="btn btn-success col-lg-12" type="button" id="scoreSubmitExport">Submit</button>
                            </div>

                        </div>
                    </div>

                    <div class="row">
                        <div class="col-lg-12 pull-left">
                            <div class="form-group col-lg-2 ">
                                <select class="form-control classGroupSelect">
                                    <option>Select Class Group</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-2 ">
                                <select class="form-control classSelect">
                                    <option>Select Class</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-2">
                                <select class="form-control subjectSelect">
                                    <option>Select Subject</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-2">
                                <select class="form-control termSelect">
                                    <option value="0">Select Semester</option>
                                    <option value="First Semester">First Semester</option>
                                    <option value="Second Semester">Second Semester</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-2">
                                <select class="form-control academicYearSelect" >
                                    <option>Select Academic Year</option>
                                </select>
                            </div>
                            <div class="form-group col-sm-2">
                                <div class="form-check form-switch">
                                   <input class="form-check-input" id="scoreTypeControl" value="Teaching score_type" type="checkbox" />
                                   <label class="check-label" for="scoreTypeControl">Class Score</label>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>
    `

    //function institutionBuild() {
    let staffScoreUpload = `
    
    
    <div class="content animate-panel" id="pagecontent">
    <div class="hpanel">

        <div class="panel-heading">
            <div class="panel-tools">
                <a class="showhide"><i class="fa fa-chevron-up"></i></a>
                <a class="closebox"><i class="fa fa-times"></i></a>
            </div>
            Subject Scores
        </div>
        <div class="panel-body">
            <table id="subjectScoreTable" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr id="scoreTableHead">
                    <th>Student ID</th>
                    <th>Student Last Name</th>
                    <th>Student First Names</th>
                    <th>Score</th>
                </tr>
                </thead>
                <tbody id="scoreTableBody"></tbody>
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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', staffScoreUpload);
  /*  document.getElementsByClassName("test")[0].addEventListener("click", staffScoreUploadIndut);
    document.getElementById("modalopn").addEventListener("click", modalopn);*/

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_staffScoreUpload.js");

    var script15 = document.createElement("script");
    script15.setAttribute("type", "text/javascript");
    script15.setAttribute("src", "scripts/xlsx.full.min.js");
    document.getElementsByTagName("body")[0].appendChild(script15);
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    staffScoreUploadIndut();
}

function staffScoreUploadIndut() {
    let div = `
    <div class="row"><label class="col-sm-3 control-label">Group Name</label>
        <div class="col-sm-9">
            <div class="row">
                <div class="col-md-12"><input type="text" placeholder="Enter Class Group Name" class="form-control newstaffScoreUploadtxt"></div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[0].insertAdjacentHTML('beforeend', div);
}


$(function () {

    // Initialize Example 1
    $('#subjectScoreTable').dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            { extend: 'copy', className: 'btn-sm' },
            { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'print', className: 'btn-sm' }
        ]
    });
});
