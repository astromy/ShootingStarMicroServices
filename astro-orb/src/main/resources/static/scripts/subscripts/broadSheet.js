// Use immediate function execution and cache all DOM elements
(function () {
  // 1. Optimized HTML strings (minified, single quotes, no extra whitespace)
  const header = `<div class="panel-body"><div class="panel-body row"><div class="pull-right col-lg-8"><div class="form-group col-lg-3"><select class="form-control gradingSettingSelect"><option value="0">Select Grading Setting</option></select></div><div id="reportExport" class="col-lg-3"><button class="btn btn-primary col-lg-12" type="button" id="reportExportBtn">Export Broadsheet</button></div><div id="hbreadcrumb" class="col-lg-3"><button class="btn btn-info reportPublishBtn col-lg-12" type="button" id="reportPublishBtn">Publish Report</button></div><div id="reportGenerate" class="col-lg-3"><button class="btn btn-success col-lg-12" type="button" id="reportGenerateBtn">Generate</button></div></div></div><div class="row"><div class="col-lg-12 pull-left"><div class="form-group col-lg-2"><select class="form-control classGroupSelect"><option>Select Class Group</option></select></div><div class="form-group col-lg-2"><select class="form-control classSelect"><option>Select Class</option></select></div><div class="form-group col-lg-2"><select class="form-control termSelect"><option value="0">Select Semester</option><option value="First Semester">First Term</option><option value="Second Semester">Second Term</option></select></div><div class="form-group col-lg-2"><select class="form-control academicYearSelect"><option>Select Academic Year</option></select></div><div class="form-group col-sm-2"><div class="form-check form-switch"><input class="form-check-input" id="scoreTypeControl" value="Teaching score_type" type="checkbox"/><label class="check-label" for="scoreTypeControl">Class Score</label></div></div></div></div></div>`;

  const reportPublish = `<div class="content animate-panel" id="pagecontent"><div class="hpanel"><div class="panel-heading"><div class="panel-tools"><a class="showhide"><i class="fa fa-chevron-up"></i></a><a class="closebox"><i class="fa fa-times"></i></a></div>Subject Scores</div><div class="panel-body"><table id="reportTable" class="table table-striped table-bordered table-hover" width="100%" style="text-align:center;"><thead id="reportTableHead"></thead><tbody id="reportTableBody"></tbody></table></div></div></div><footer class="footer"><span class="pull-right">ORB</span><span class="fa fa-copyright"></span>Astromy LLC 2013-<span id="copyrightYear"></span></footer>`;

  // 2. Ultra-fast DOM injection using documentFragment
  const wrapper = document.getElementById("wrapper");
  if (wrapper) {
    const fragment = document.createDocumentFragment();
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = header;
    while (tempDiv.firstChild) {
      fragment.appendChild(tempDiv.firstChild);
    }
    tempDiv.innerHTML = reportPublish;
    while (tempDiv.firstChild) {
      fragment.appendChild(tempDiv.firstChild);
    }
    wrapper.appendChild(fragment);
  }

  // 3. Lightweight script loader with preloading and parallel execution
  const scripts = [
    { src: "scripts/jspdf.umd.min.js", priority: 1 },
    { src: "scripts/xlsx.full.min.js", priority: 1 }, // Can load in parallel
    {
      src: "scripts/jspdf.plugin.autotable.min.js",
      priority: 2,
      depends: "jspdf",
    },
    { src: "scripts/_broadSheet.js", priority: 3, depends: "autotable" },
  ];

  // 4. Preload all scripts immediately
  scripts.forEach((script) => {
    const link = document.createElement("link");
    link.rel = "preload";
    link.as = "script";
    link.href = script.src;
    document.head.appendChild(link);
  });

  // 5. Optimized parallel loading with dependency management
  const loadScript = (() => {
    const loaded = {};
    return function load(src) {
      if (loaded[src]) return loaded[src];

      const promise = new Promise((resolve, reject) => {
        const script = document.createElement("script");
        script.src = src;
        script.async = true;
        script.onload = () => {
          loaded[src] = true;
          resolve();
        };
        script.onerror = reject;
        document.body.appendChild(script);
      });

      loaded[src] = promise;
      return promise;
    };
  })();

  // 6. Execute loading in optimal order
  (async function () {
    try {
      // Load independent scripts in parallel
      await Promise.all([
        loadScript("scripts/jspdf.umd.min.js").then(() => {
          window.jspdf = window.jspdf || window["jspdf"];
        }),
        loadScript("scripts/xlsx.full.min.js"),
      ]);

      // Load dependent scripts in sequence
      await loadScript("scripts/jspdf.plugin.autotable.min.js");
      window.jspdf.autoTable = window.jspdf.autoTable || window["autoTable"];

      await loadScript("scripts/_broadSheet.js");

      // Mark completion
      performance.mark("scriptsLoaded");
    } catch (e) {
      console.error("Script loading failed:", e);
    }
  })();

  // 7. Optimized modal functions
  const modalFunctions = {
    modalopn: function () {
      const modal = document.getElementsByClassName("modalbody")[0];
      if (modal) {
        modal.innerHTML = "";
        this.reportPublishIndut();
      }
    },
    reportPublishIndut: function () {
      const div = `<div class="row"><label class="col-sm-3 control-label">Group Name</label><div class="col-sm-9"><div class="row"><div class="col-md-12"><input type="text" placeholder="Enter Class Group Name" class="form-control newreportPublishtxt"></div></div></div></div><div class="hr-line-dashed"></div>`;
      const modal = document.getElementsByClassName("modalbody")[0];
      if (modal) {
        modal.insertAdjacentHTML("beforeend", div);
      }
    },
  };

  // Expose modal functions
  window.modalopn = modalFunctions.modalopn.bind(modalFunctions);
})();
