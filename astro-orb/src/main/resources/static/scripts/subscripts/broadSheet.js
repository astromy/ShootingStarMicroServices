$(function () {

    let header = `
            <div class="panel-body">
                <div class=" panel-body row">
                    <div class="pull-right col-lg-8">

                                                <div class="form-group col-lg-3">
                                                    <select class="form-control gradingSettingSelect">
                                                        <option value="0">Select Grading Setting</option>
                                                     </select>
                                                </div>

                            <div id="reportExport" class="col-lg-3">
                                <button class="btn btn-primary col-lg-12" type="button" id="reportExportBtn">Export Broadsheet</button>
                            </div>
                            <div id="hbreadcrumb" class="col-lg-3">
                                <button class="btn btn-info reportPublishBtn col-lg-12" type="button" id="reportPublishBtn">Publish Report</button>
                            </div>
                            <div id="reportGenerate" class="col-lg-3">
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
                                    <option value="0">Select Semester</option>
                                    <option value="First Semester">First Term</option>
                                    <option value="Second Semester">Second Term</option>
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
            <table id="reportTable" class="table table-striped table-bordered table-hover" width="100%" style="text-align: center;">
                <thead id="reportTableHead">
                    <!-- Will be built dynamically -->
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
                    loadScript("scripts/_broadSheet.js", function () {
                        console.log("✅ _broadSheet.js Loaded, all dependencies are ready.");
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

