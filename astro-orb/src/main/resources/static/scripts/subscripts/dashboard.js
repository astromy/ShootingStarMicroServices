// dashboard.js — subscripts/dashboard.js
// Detects logged-in user role from permissionGroups (cd4 meta tag),
// fetches their staff profile, then renders the correct BI dashboard.

(function () {

    var instCode = window.instId
        ? window.instId.split(",")[0].replace(/[\[\]'\/]+/g, "")
        : "";

    var staffId = (document.querySelector(".staffLoginId") || {}).innerHTML || "";

    // ── Determine role from institutionId meta tag ────────────────────────────
    // institutionId content = "[/00147, /00147/Admin, /00147/User]"
    // Keycloak subgroups sit after the institution code: /00147/Finance → "Finance"
    // This is the correct source for role — cd4 contains permissions not roles.
    function detectRole() {
        var instMeta = $("meta[name='institutionId']").attr("content") || "";
        var parts = instMeta.replace("[", "").replace("]", "").split(",");

        // Extract subgroup names — segments of the form /instCode/GroupName
        var subgroups = [];
        parts.forEach(function (p) {
            var segs = p.trim().split("/").filter(Boolean);
            if (segs.length === 2) {   // ["00147", "Finance"]
                subgroups.push(segs[1].toLowerCase().replace("_", ""));
            }
        });

        // Map Keycloak subgroup names to dashboard roles
        // Priority: most specific first; "user" and "offline_access" are ignored
        var roleMap = {
            "admin": "superadmin",
            "finance": "finance",
            "humanresource": "hr",
            "hr": "hr",
            "teaching": "teacher",
            "academics": "teacher",
            "administration": "admin",
            "stores": "stores",
            "infirmary": "health",
            "health": "health",
        };

        // Priority order — check most privileged first
        var priority = ["admin", "finance", "humanresource", "hr", "stores", "infirmary", "health", "administration", "teaching", "academics"];
        for (var i = 0; i < priority.length; i++) {
            if (subgroups.includes(priority[i])) {
                return roleMap[priority[i]];
            }
        }

        // Fallback: check cd4 permission groups as secondary signal
        var pg = [...permissionGroups];
        if (pg.includes("finance")) return "finance";
        if (pg.includes("humanresource")) return "hr";
        if (pg.includes("stores")) return "stores";
        if (pg.includes("teaching")) return "teacher";
        if (pg.includes("administration")) return "admin";

        return "admin";
    }

    var role = detectRole();

    // ── Stat card builder ──────────────────────────────────────────────────────
    function statCard(icon, color, title, valueId, sub, col) {
        col = col || 3;
        return `
        <div class="col-lg-${col}">
            <div class="hpanel stats">
                <div class="panel-body">
                    <div class="stats-title pull-left"><h4>${title}</h4></div>
                    <div class="stats-icon pull-right">
                        <i class="fa ${icon} fa-2x text-${color}"></i>
                    </div>
                    <div class="m-t-xl">
                        <h1 class="font-extra-bold" id="${valueId}">—</h1>
                        <small>${sub}</small>
                    </div>
                </div>
            </div>
        </div>`;
    }

    // ── Chart panel builder ────────────────────────────────────────────────────
    function chartPanel(title, chartId, height) {
        height = height || 350;
        return `
        <div class="hpanel">
            <div class="panel-heading">
                <div class="panel-tools">
                    <a class="showhide"><i class="fa fa-chevron-up"></i></a>
                </div>
                ${title}
            </div>
            <div class="panel-body">
                <div id="${chartId}" style="height:${height}px;"></div>
            </div>
        </div>`;
    }

    // ── Role HTML definitions ──────────────────────────────────────────────────
    var layouts = {

        superadmin: {
            title: "Executive Dashboard",
            subtitle: "Full institution overview — students, staff, finance &amp; operations",
            stats: [
                statCard("fa-users", "success", "Total Students", "stat-students", "All enrolled", 2),
                statCard("fa-id-badge", "info", "Total Staff", "stat-staff", "Teaching &amp; non-teaching", 2),
                statCard("fa-money", "success", "Fees Collected", "stat-fees-collected", "This term", 2),
                statCard("fa-exclamation-circle", "danger", "Outstanding", "stat-outstanding", "Owing students", 2),
                statCard("fa-graduation-cap", "warning", "Active Classes", "stat-classes", "Current term", 2),
                statCard("fa-building", "primary", "Departments", "stat-depts", "Operational units", 2),
            ],
            charts: [
                {col: 8, title: "📊 Students by Class", id: "chart-students-by-class"},
                {col: 4, title: "⚧ Gender Distribution", id: "chart-gender-dist"},
                {col: 4, title: "👨‍💼 Staff by Department", id: "chart-staff-by-dept"},
                {col: 4, title: "💰 Fee Collection by Class", id: "chart-fee-by-class"},
                {col: 4, title: "📋 Student Status", id: "chart-student-status"},
                {col: 6, title: "📈 Enrollment Trend by Class", id: "chart-enrollment-trend"},
                {col: 6, title: "🆕 New Admissions This Term", id: "chart-new-admissions"},
            ]
        },

        admin: {
            title: "Administration Dashboard",
            subtitle: "Student enrollment, attendance, and records overview",
            stats: [
                statCard("fa-users", "success", "Total Students", "stat-students", "All enrolled students"),
                statCard("fa-user-plus", "info", "New Admissions", "stat-admissions", "This term"),
                statCard("fa-user-times", "warning", "Suspended", "stat-suspended", "Pending review"),
                statCard("fa-hourglass-half", "danger", "Pending Applicants", "stat-applicants", "Awaiting processing"),
            ],
            charts: [
                {col: 6, title: "📊 Students by Class", id: "chart-students-by-class"},
                {col: 6, title: "⚧ Gender Distribution", id: "chart-gender-dist"},
                {col: 6, title: "📈 Enrollment by Status", id: "chart-enrollment-status"},
                {col: 6, title: "🏠 Students by Locality", id: "chart-locality"},
            ]
        },

        teacher: {
            title: "Teacher Dashboard",
            subtitle: "Your classes, subjects, and student performance",
            stats: [
                statCard("fa-book", "success", "My Subjects", "stat-subjects", "Assigned subjects"),
                statCard("fa-users", "info", "My Students", "stat-students", "Across all classes"),
                statCard("fa-question-circle", "warning", "Questions in Bank", "stat-questions", "Total questions uploaded"),
                statCard("fa-bar-chart", "danger", "Avg Score", "stat-avg-score", "Last assessment"),
            ],
            charts: [
                {col: 6, title: "📚 My Subjects", id: "chart-my-subjects"},
                {col: 6, title: "👥 Students per Class", id: "chart-students-per-class"},
                {col: 12, title: "📊 Assessment Score Distribution", id: "chart-score-dist"},
            ]
        },

        finance: {
            title: "Finance Dashboard",
            subtitle: "Fee collection, billing, and payroll overview",
            stats: [
                statCard("fa-money", "success", "Fees Collected", "stat-fees-collected", "This term"),
                statCard("fa-exclamation-circle", "danger", "Outstanding", "stat-outstanding", "Owing students"),
                statCard("fa-file-text", "info", "Bills Created", "stat-bills", "Active billing items"),
                statCard("fa-users", "warning", "Staff on Payroll", "stat-payroll", "Current cycle"),
            ],
            charts: [
                {col: 6, title: "💰 Fee Collection by Class", id: "chart-fee-by-class"},
                {col: 6, title: "📉 Owing vs Paid Students", id: "chart-owing-paid"},
                {col: 6, title: "💳 Payment Methods", id: "chart-payment-methods"},
                {col: 6, title: "📊 Salary Distribution", id: "chart-salary-dist"},
            ]
        },

        hr: {
            title: "HR Dashboard",
            subtitle: "Staff records, leave, designations, and appraisals",
            stats: [
                statCard("fa-id-badge", "success", "Total Staff", "stat-staff", "All staff members"),
                statCard("fa-plane", "info", "On Leave", "stat-on-leave", "Currently approved"),
                statCard("fa-briefcase", "warning", "Designations", "stat-designations", "Active designations"),
                statCard("fa-star", "danger", "Appraisals Due", "stat-appraisals", "This period"),
            ],
            charts: [
                {col: 6, title: "👥 Staff by Department", id: "chart-staff-by-dept"},
                {col: 6, title: "⚧ Staff Gender Ratio", id: "chart-staff-gender"},
                {col: 6, title: "📋 Leave by Type", id: "chart-leave-type"},
                {col: 6, title: "🏅 Designation Distribution", id: "chart-designation"},
            ]
        },

        stores: {
            title: "Stores & Inventory Dashboard",
            subtitle: "Stock levels, movements, and order status",
            stats: [
                statCard("fa-cubes", "success", "Total Items", "stat-items", "In inventory"),
                statCard("fa-exclamation-triangle", "danger", "Low Stock", "stat-low-stock", "Below threshold"),
                statCard("fa-shopping-cart", "info", "Open Orders", "stat-orders", "Pending fulfillment"),
                statCard("fa-truck", "warning", "Movements Today", "stat-movements", "In/out today"),
            ],
            charts: [
                {col: 6, title: "📦 Stock Levels by Category", id: "chart-stock-levels"},
                {col: 6, title: "🔄 Inventory Movements", id: "chart-movements"},
                {col: 6, title: "🛒 Order Status", id: "chart-order-status"},
                {col: 6, title: "⚠️ Low Stock Items", id: "chart-low-stock"},
            ]
        },

        health: {
            title: "Health Dashboard",
            subtitle: "Student and staff health records overview",
            stats: [
                statCard("fa-heartbeat", "success", "Health Records", "stat-records", "Total records"),
                statCard("fa-stethoscope", "info", "Visits This Term", "stat-visits", "Clinic visits"),
                statCard("fa-ambulance", "danger", "Referrals", "stat-referrals", "External referrals"),
                statCard("fa-pills", "warning", "Treatments", "stat-treatments", "Administered"),
            ],
            charts: [
                {col: 6, title: "🏥 Visits by Type", id: "chart-visit-type"},
                {col: 6, title: "📅 Monthly Visit Trend", id: "chart-visit-trend"},
                {col: 12, title: "📊 Common Diagnoses", id: "chart-diagnoses"},
            ]
        },
    };

    var layout = layouts[role] || layouts["admin"];

    // ── Build HTML ─────────────────────────────────────────────────────────────
    var statsHTML = '<div class="row">' + layout.stats.join('') + '</div>';

    var chartsHTML = '';
    var row = '';
    var colCount = 0;
    layout.charts.forEach(function (c, idx) {
        row += '<div class="col-lg-' + c.col + '">' + chartPanel(c.title, c.id) + '</div>';
        colCount += c.col;
        if (colCount >= 12 || idx === layout.charts.length - 1) {
            chartsHTML += '<div class="row">' + row + '</div>';
            row = '';
            colCount = 0;
        }
    });

    document.getElementById('wrapper').innerHTML = `
    <div class="normalheader transition animated fadeIn">
        <div class="hpanel">
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right m-t-lg">
                    <ol class="hbreadcrumb breadcrumb">
                        <li>Home</li>
                        <li class="active"><span>${layout.title}</span></li>
                    </ol>
                </div>
                <h2 class="font-light m-b-xs">${layout.title}</h2>
                <small>${layout.subtitle}</small>
            </div>
        </div>
    </div>
    <div class="content animate-panel">
        ${statsHTML}
        ${chartsHTML}
    </div>
    <footer class="footer">
        <span class="pull-right">ORB</span>
        <span class="fa fa-copyright"></span>
        Astromy LLC 2013-<span id="copyrightYear"></span>
    </footer>`;

    window.copyrights();

    // ── Load logic script which fetches real data and renders charts ───────────
    var scriptLogic = document.createElement("script");
    scriptLogic.setAttribute("type", "text/javascript");
    scriptLogic.setAttribute("src", "scripts/_dashboard.js");
    scriptLogic.setAttribute("data-dashboard-role", role);
    scriptLogic.setAttribute("data-dashboard-inst", instCode);
    scriptLogic.setAttribute("data-dashboard-staff", staffId);
    scriptLogic.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(scriptLogic);

})();