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
                                <button class="btn btn-primary col-lg-12" type="button" id="reportExportBtn">Export Class List</button>
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
                                    <option value="0">Select Term</option>
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
    `;

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
    `;

  // 1. Optimized DOM manipulation (cache element, use efficient methods)
  const wrapper = document.getElementById("wrapper");
  if (wrapper) {
    wrapper.innerHTML = header;
    wrapper.insertAdjacentHTML("beforeend", reportPublish);
  }

  // 2. Preload critical scripts for faster loading
  const preloadScripts = [
    "scripts/jspdf.umd.min.js",
    "scripts/jspdf.plugin.autotable.min.js",
    "scripts/xlsx.full.min.js",
    "scripts/_terminalReport.js",
  ];

  // Create preload links without blocking
  requestIdleCallback(() => {
    preloadScripts.forEach((src) => {
      const link = document.createElement("link");
      link.rel = "preload";
      link.as = "script";
      link.href = src;
      document.head.appendChild(link);
    });
  });

  // 3. Modern Promise-based script loader with error handling
  function loadScript(src) {
    return new Promise((resolve, reject) => {
      const script = document.createElement("script");
      script.src = src;
      script.async = true;
      script.onload = () => {
        performance.mark(`${src}-loaded`);
        console.log(
          `✅ ${src.split("/").pop()} loaded in ${performance
            .measure(`${src}-duration`, "navigationStart", `${src}-loaded`)
            .duration.toFixed(2)}ms`
        );
        resolve();
      };
      script.onerror = () => reject(new Error(`Failed to load ${src}`));
      document.body.appendChild(script);
    });
  }

  // 4. Optimized loading sequence with parallel loading where possible
  (async function () {
    try {
      // Start loading jsPDF and XLSX in parallel (no dependencies between them)
      const [jspdfLoaded] = await Promise.all([
        loadScript("scripts/jspdf.umd.min.js"),
        loadScript("scripts/xlsx.full.min.js"), // Load in parallel
      ]);

      // Initialize jsPDF
      window.jspdf = window.jspdf || window["jspdf"];

      // Load autoTable (depends on jsPDF)
      await loadScript("scripts/jspdf.plugin.autotable.min.js");
      window.jspdf.autoTable = window.jspdf.autoTable || window["autoTable"];

      // Load terminal report (depends on all previous)
      await loadScript("scripts/_terminalReport.js");

      console.log("🚀 All dependencies loaded and ready");

      // Optional: Dispatch custom event when everything is loaded
      document.dispatchEvent(new CustomEvent("terminalReportReady"));
    } catch (error) {
      console.error("⚠️ Script loading error:", error);
      // Implement your error recovery logic here
    }
  })();

  // 5. Bonus: Add performance monitoring
  performance.mark("scriptLoadingStart");
  window.addEventListener("terminalReportReady", () => {
    performance.mark("scriptLoadingEnd");
    const measure = performance.measure(
      "totalScriptLoading",
      "scriptLoadingStart",
      "scriptLoadingEnd"
    );
    console.log(
      `⏱ Total script loading time: ${measure.duration.toFixed(2)}ms`
    );
  });
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn() {
  document.getElementsByClassName("modalbody")[0].innerHTML = "";
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
    <div class="hr-line-dashed"></div>`;
  document
    .getElementsByClassName("modalbody")[0]
    .insertAdjacentHTML("beforeend", div);
}

$(function () {
  // Initialize Example 1
  $("#reportTable").dataTable({
    dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
    lengthMenu: [
      [10, 25, 50, -1],
      [10, 25, 50, "All"],
    ],
    buttons: [
      { extend: "copy", className: "btn-sm" },
      { extend: "csv", title: "ExampleFile", className: "btn-sm" },
      { extend: "pdf", title: "ExampleFile", className: "btn-sm" },
      { extend: "print", className: "btn-sm" },
    ],
  });
});
