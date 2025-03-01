$(function () {

    let header = `
            <div class="panel-body">
                <div class=" panel-body row">
                    <div class="pull-right col-lg-5">

                            <div id="reportExport" class="col-lg-4">
                                <button class="btn btn-primary col-lg-12" type="button" id="reportExportBtn">Export Class List</button>
                            </div>
                            <div id="hbreadcrumb" class="col-lg-4">
                                <button class="btn btn-info reportPublishBtn col-lg-12" type="button" id="reportPublishBtn">Publish Report</button>
                            </div>
                            <div id="reportGenerate" class="col-lg-4">
                                    <button class="btn btn-success col-lg-12" type="button" id="reportGenerateBtn">Generate</button>
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
                                <select class="form-control termSelect">
                                    <option value="0">Select Term</option>
                                    <option value="First Term">First Term</option>
                                    <option value="Second Term">Second Term</option>
                                    <option value="Third Term">Third Term</option>
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
    let reportPublish = `
    
    
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
            <table id="reportTable" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr id="scoreTableHead">
                    <th>No</th>
                    <th>Student ID</th>
                    <th>Name</th>
                    <th>Class/th>
                    <th>Subject</th>
                    <th>Class Score</th>
                    <th>Exams Score</th>
                    <th>Total Score</th>
                    <th>Grade</th>
                    <th>Position</th>
                </tr>
                </thead>
                <tbody id="reportTableBody"></tbody>
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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', reportPublish);

  function loadScript(src, callback) {
          let script = document.createElement("script");
          script.setAttribute("type", "text/javascript");
          script.setAttribute("src", src);
          script.onload = callback;
          document.body.appendChild(script);
      }

    loadScript("scripts/jspdf.umd.min.js", function () {
            console.log("✅ jsPDF Loaded:", window.jspdf);

            // Assign jsPDF explicitly
            window.jspdf = window.jspdf || window["jspdf"];

            // Load autoTable after jsPDF is available
            loadScript("scripts/jspdf.plugin.autotable.min.js", function () {
                console.log("✅ AutoTable Loaded:", window.jspdf?.autoTable);

                // Ensure autoTable is properly assigned
                window.jspdf.autoTable = window.jspdf.autoTable || window["autoTable"];

                // Load XLSX (Excel Library)
                loadScript("scripts/xlsx.full.min.js", function () {
                    console.log("✅ XLSX Loaded");

                    // Load _terminalReport.js AFTER ALL dependencies are available
                    loadScript("scripts/_terminalReport.js", function () {
                        console.log("✅ _terminalReport.js Loaded, all dependencies are ready.");
                    });
                });
            });
        });
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    reportPublishIndut();
}

function reportPublishIndut() {
    let div = `
    <div class="row"><label class="col-sm-3 control-label">Group Name</label>
        <div class="col-sm-9">
            <div class="row">
                <div class="col-md-12"><input type="text" placeholder="Enter Class Group Name" class="form-control newreportPublishtxt"></div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[0].insertAdjacentHTML('beforeend', div);
}


$(function () {

    // Initialize Example 1
    $('#reportTable').dataTable({
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
