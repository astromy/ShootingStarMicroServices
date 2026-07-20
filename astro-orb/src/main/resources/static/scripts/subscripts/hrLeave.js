/**
 * hrLeave.js  —  UI builder for HR Phase 2
 * (Attendance, Leave Management, Documents)
 *
 * Loads first. Dynamically injects hrLeave.css and _hrLeave.js.
 * Three tabs: Attendance | Leave | Documents
 */

(function () {
    "use strict";

    // ─── Resolve sibling base path ────────────────────────────────────────────
    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName("script");
            for (var i = tags.length - 1; i >= 0; i--) {
                if (tags[i].src && tags[i].src.indexOf("hrLeave") !== -1 &&
                    tags[i].src.indexOf("_hrLeave") === -1) return tags[i];
            }
            return null;
        })();
        if (!el || !el.src) return "";
        return el.src.substring(0, el.src.lastIndexOf("/") + 1);
    })();

    // ─── Inject CSS ───────────────────────────────────────────────────────────
    if (!document.getElementById("hrLeaveCSS")) {
        var link = document.createElement("link");
        link.id = "hrLeaveCSS";
        link.rel = "stylesheet";
        link.href = _base + "../../styles/style.css";
        document.head.appendChild(link);
    }

    // ─── Font Awesome ─────────────────────────────────────────────────────────
    if (!document.querySelector('link[href*="font-awesome"], link[href*="fontawesome"]')) {
        var fa = document.createElement("link");
        fa.rel = "stylesheet";
        fa.href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css";
        document.head.appendChild(fa);
    }

    // ─── MONTHS ───────────────────────────────────────────────────────────────
    var MONTHS = ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"];
    var DAYS = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

    // ─── BUILD DOM ────────────────────────────────────────────────────────────
    function buildDOM() {
        document.getElementById("wrapper").innerHTML = '<div class="hr-page">' + [

            // HEADER
            '<header class="hr-header">',
            '<div class="hr-header-row">',
            '<div>',
            '<h1><i class="fas fa-users-cog"></i> HR — Attendance, Leave &amp; Documents</h1>',
            '<p>Manage daily attendance, leave requests and employee documents</p>',
            '</div>',
            '<div class="hr-header-actions">',
            '<button class="hr-btn hr-btn-secondary" id="hrRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '</div>',
            '</div>',
            '</header>',

            // BODY
            '<div class="hr-body">',

            // TABS
            '<div class="hr-tabs">',
            '<button class="hr-tab active" data-tab="attendance"><i class="fas fa-clock"></i> Attendance</button>',
            '<button class="hr-tab" data-tab="leave"><i class="fas fa-calendar-minus"></i> Leave</button>',
            '<button class="hr-tab" data-tab="approval"><i class="fas fa-check-circle"></i> Approvals</button>',
            '<button class="hr-tab" data-tab="documents"><i class="fas fa-folder-open"></i> Documents</button>',
            '</div>',

            // ── ATTENDANCE TAB ────────────────────────────────────────────
            '<div id="tab-attendance" class="hr-tab-content">',

            // Check-in card
            '<div class="hr-checkin-card">',
            '<div>',
            '<div class="time-display" id="hrClock">00:00:00</div>',
            '<div class="date-display" id="hrDate"></div>',
            '<div class="status-display" id="hrCheckinStatus">Not checked in today</div>',
            '</div>',
            '<div class="hr-checkin-actions">',
            '<button class="hr-checkin-btn check-in"  id="hrCheckInBtn"><i class="fas fa-sign-in-alt"></i> Check In</button>',
            '<button class="hr-checkin-btn check-out" id="hrCheckOutBtn" disabled><i class="fas fa-sign-out-alt"></i> Check Out</button>',
            '</div>',
            '</div>',

            // Stats
            '<div class="hr-stats" id="hrAttendanceStats">',
            '<div class="hr-stat-card c-present">',
            '<div class="stat-icon"><i class="fas fa-user-check"></i></div>',
            '<div><h3 id="statPresent">—</h3><p>Present Today</p></div>',
            '</div>',
            '<div class="hr-stat-card c-absent">',
            '<div class="stat-icon"><i class="fas fa-user-times"></i></div>',
            '<div><h3 id="statAbsent">—</h3><p>Absent Today</p></div>',
            '</div>',
            '<div class="hr-stat-card c-leave">',
            '<div class="stat-icon"><i class="fas fa-umbrella-beach"></i></div>',
            '<div><h3 id="statOnLeave">—</h3><p>On Leave</p></div>',
            '</div>',
            '<div class="hr-stat-card c-balance">',
            '<div class="stat-icon"><i class="fas fa-percentage"></i></div>',
            '<div><h3 id="statRate">—</h3><p>Attendance Rate</p></div>',
            '</div>',
            '</div>',

            // Attendance history
            '<div class="hr-panel">',
            '<div class="hr-panel-head">',
            '<span><i class="fas fa-history"></i>My Attendance History</span>',
            '<div style="display:flex;gap:10px;align-items:center">',
            '<select id="hrAttMonth" class="hr-field" style="padding:7px 10px;border:2px solid #dde2ee;border-radius:8px;font-size:.85rem"></select>',
            '<select id="hrAttYear"  class="hr-field" style="padding:7px 10px;border:2px solid #dde2ee;border-radius:8px;font-size:.85rem"></select>',
            '<button class="hr-btn hr-btn-primary hr-btn-sm" id="hrAttFetchBtn"><i class="fas fa-search"></i> Load</button>',
            '</div>',
            '</div>',
            '<div class="hr-panel-body">',
            '<div class="hr-table-wrap">',
            '<table class="hr-table" id="hrAttTable">',
            '<thead><tr>',
            '<th>Date</th><th>Check In</th><th>Check Out</th>',
            '<th>Hours</th><th>Status</th>',
            '</tr></thead>',
            '<tbody id="hrAttBody">',
            '<tr><td colspan="5"><div class="hr-empty"><i class="fas fa-clock"></i>Select month and click Load</div></td></tr>',
            '</tbody>',
            '</table>',
            '</div>',
            '</div>',
            '</div>',

            '</div>', // end attendance tab

            // ── LEAVE TAB ─────────────────────────────────────────────────
            '<div id="tab-leave" class="hr-tab-content" style="display:none">',

            // Leave balances
            '<div class="hr-panel">',
            '<div class="hr-panel-head">',
            '<span><i class="fas fa-wallet"></i>Leave Balance</span>',
            '<button class="hr-btn hr-btn-primary hr-btn-sm" id="hrApplyLeaveBtn">',
            '<i class="fas fa-plus"></i> Apply Leave',
            '</button>',
            '</div>',
            '<div class="hr-panel-body">',
            '<div class="hr-balance-grid" id="hrBalanceGrid">',
            '<div class="hr-balance-card casual">',
            '<div class="bal-type">Casual</div>',
            '<div class="bal-days" id="balCasual">—</div>',
            '<div class="bal-label">days remaining</div>',
            '</div>',
            '<div class="hr-balance-card sick">',
            '<div class="bal-type">Sick</div>',
            '<div class="bal-days" id="balSick">—</div>',
            '<div class="bal-label">days remaining</div>',
            '</div>',
            '<div class="hr-balance-card earned">',
            '<div class="bal-type">Earned</div>',
            '<div class="bal-days" id="balEarned">—</div>',
            '<div class="bal-label">days remaining</div>',
            '</div>',
            '<div class="hr-balance-card maternity">',
            '<div class="bal-type">Maternity / Paternity</div>',
            '<div class="bal-days" id="balMaternity">—</div>',
            '<div class="bal-label">days remaining</div>',
            '</div>',
            '</div>',
            '</div>',
            '</div>',

            // Leave calendar
            '<div class="hr-panel">',
            '<div class="hr-panel-head">',
            '<span><i class="fas fa-calendar-alt"></i>Leave Calendar</span>',
            '<div style="display:flex;gap:10px;align-items:center">',
            '<button class="hr-btn hr-btn-secondary hr-btn-sm" id="hrCalPrevBtn"><i class="fas fa-chevron-left"></i></button>',
            '<strong id="hrCalTitle" style="min-width:140px;text-align:center"></strong>',
            '<button class="hr-btn hr-btn-secondary hr-btn-sm" id="hrCalNextBtn"><i class="fas fa-chevron-right"></i></button>',
            '</div>',
            '</div>',
            '<div class="hr-panel-body">',
            '<div class="hr-table-wrap"><table class="hr-calendar" id="hrCalendar"></table></div>',
            '</div>',
            '</div>',

            // My leave history
            '<div class="hr-panel">',
            '<div class="hr-panel-head"><span><i class="fas fa-list-alt"></i>My Leave Requests</span></div>',
            '<div class="hr-panel-body">',
            '<div class="hr-table-wrap">',
            '<table class="hr-table">',
            '<thead><tr>',
            '<th>Type</th><th>From</th><th>To</th><th>Days</th><th>Reason</th><th>Status</th><th>Applied On</th>',
            '</tr></thead>',
            '<tbody id="hrMyLeaveBody">',
            '<tr><td colspan="7"><div class="hr-empty"><i class="fas fa-calendar"></i>No leave requests yet</div></td></tr>',
            '</tbody>',
            '</table>',
            '</div>',
            '</div>',
            '</div>',

            '</div>', // end leave tab

            // ── APPROVALS TAB ─────────────────────────────────────────────
            '<div id="tab-approval" class="hr-tab-content" style="display:none">',
            '<div class="hr-panel">',
            '<div class="hr-panel-head">',
            '<span><i class="fas fa-tasks"></i>Pending Leave Approvals</span>',
            '<button class="hr-btn hr-btn-secondary hr-btn-sm" id="hrRefreshApprovalsBtn">',
            '<i class="fas fa-sync-alt"></i> Refresh',
            '</button>',
            '</div>',
            '<div class="hr-panel-body">',
            '<div class="hr-table-wrap">',
            '<table class="hr-table">',
            '<thead><tr>',
            '<th>Employee</th><th>Type</th><th>From</th><th>To</th>',
            '<th>Days</th><th>Reason</th><th>Status</th><th>Action</th>',
            '</tr></thead>',
            '<tbody id="hrApprovalBody">',
            '<tr><td colspan="8"><div class="hr-empty"><i class="fas fa-check-circle"></i>No pending approvals</div></td></tr>',
            '</tbody>',
            '</table>',
            '</div>',
            '</div>',
            '</div>',
            '</div>', // end approval tab

            // ── DOCUMENTS TAB ─────────────────────────────────────────────
            '<div id="tab-documents" class="hr-tab-content" style="display:none">',
            '<div class="hr-panel">',
            '<div class="hr-panel-head">',
            '<span><i class="fas fa-folder-open"></i>My Documents</span>',
            '<button class="hr-btn hr-btn-primary hr-btn-sm" id="hrUploadDocBtn">',
            '<i class="fas fa-upload"></i> Upload Document',
            '</button>',
            '</div>',
            '<div class="hr-panel-body">',

            // Upload zone
            '<div class="hr-upload-zone" id="hrDropZone">',
            '<input type="file" id="hrDocFileInput" accept=".pdf,.doc,.docx,.jpg,.jpeg,.png">',
            '<i class="fas fa-cloud-upload-alt"></i>',
            '<p>Drop files here or <strong>click to browse</strong></p>',
            '<p style="font-size:.8rem;margin-top:4px">PDF, Word, JPG, PNG supported</p>',
            '</div>',

            // Documents table
            '<div class="hr-table-wrap" style="margin-top:20px">',
            '<table class="hr-table">',
            '<thead><tr>',
            '<th>Document Name</th><th>Type</th><th>Uploaded On</th><th>Action</th>',
            '</tr></thead>',
            '<tbody id="hrDocBody">',
            '<tr><td colspan="4"><div class="hr-empty"><i class="fas fa-folder"></i>No documents uploaded yet</div></td></tr>',
            '</tbody>',
            '</table>',
            '</div>',
            '</div>',
            '</div>',
            '</div>', // end documents tab

            '</div>', // hr-body

            // FOOTER
            '<footer class="hr-footer">',
            '<i class="far fa-copyright"></i> Astromy LLC 2013–<span id="hrYear"></span>',
            ' &nbsp;|&nbsp; HR Suite v1.0 — Phase 2',
            '</footer>',

            // APPLY LEAVE MODAL
            '<div class="hr-modal-overlay" id="hrLeaveModal">',
            '<div class="hr-modal">',
            '<div class="hr-modal-header">',
            '<h5><i class="fas fa-calendar-plus"></i> Apply for Leave</h5>',
            '<button class="hr-modal-close" id="hrLeaveModalClose">&times;</button>',
            '</div>',
            '<div class="hr-modal-body">',
            '<div class="hr-field">',
            '<label>Leave Type</label>',
            '<select id="hrLeaveType">',
            '<option value="">Select type…</option>',
            '<option value="Casual">Casual Leave</option>',
            '<option value="Sick">Sick Leave</option>',
            '<option value="Earned">Earned Leave</option>',
            '<option value="Maternity">Maternity / Paternity</option>',
            '</select>',
            '</div>',
            '<div style="display:grid;grid-template-columns:1fr 1fr;gap:14px">',
            '<div class="hr-field">',
            '<label>From Date</label>',
            '<input type="date" id="hrLeaveFrom">',
            '</div>',
            '<div class="hr-field">',
            '<label>To Date</label>',
            '<input type="date" id="hrLeaveTo">',
            '</div>',
            '</div>',
            '<div class="hr-field">',
            '<label>Days Requested</label>',
            '<input type="text" id="hrLeaveDays" readonly placeholder="Auto-calculated">',
            '</div>',
            '<div class="hr-field">',
            '<label>Reason</label>',
            '<textarea id="hrLeaveReason" placeholder="Briefly state your reason…"></textarea>',
            '</div>',
            '</div>',
            '<div class="hr-modal-footer">',
            '<button class="hr-btn hr-btn-secondary hr-btn-sm" id="hrLeaveModalCancel">Cancel</button>',
            '<button class="hr-btn hr-btn-primary hr-btn-sm"   id="hrLeaveModalSubmit">',
            '<i class="fas fa-paper-plane"></i> Submit',
            '</button>',
            '</div>',
            '</div>',
            '</div>',

            // UPLOAD DOCUMENT MODAL
            '<div class="hr-modal-overlay" id="hrDocModal">',
            '<div class="hr-modal">',
            '<div class="hr-modal-header">',
            '<h5><i class="fas fa-file-upload"></i> Upload Document</h5>',
            '<button class="hr-modal-close" id="hrDocModalClose">&times;</button>',
            '</div>',
            '<div class="hr-modal-body">',
            '<div class="hr-field">',
            '<label>Document Type</label>',
            '<select id="hrDocType">',
            '<option value="">Select type…</option>',
            '<option value="Contract">Contract / Appointment Letter</option>',
            '<option value="ID">National ID / Passport</option>',
            '<option value="Certificate">Academic Certificate</option>',
            '<option value="Medical">Medical Certificate</option>',
            '<option value="Other">Other</option>',
            '</select>',
            '</div>',
            '<div class="hr-field">',
            '<label>Document Name</label>',
            '<input type="text" id="hrDocName" placeholder="e.g. Employment Contract 2024">',
            '</div>',
            '<div class="hr-field">',
            '<label>Select File</label>',
            '<input type="file" id="hrDocFile" accept=".pdf,.doc,.docx,.jpg,.jpeg,.png" style="padding:8px">',
            '</div>',
            '<div id="hrDocFileInfo" style="font-size:.82rem;color:#7a8094"></div>',
            '</div>',
            '<div class="hr-modal-footer">',
            '<button class="hr-btn hr-btn-secondary hr-btn-sm" id="hrDocModalCancel">Cancel</button>',
            '<button class="hr-btn hr-btn-success hr-btn-sm"   id="hrDocModalUpload">',
            '<i class="fas fa-upload"></i> Upload',
            '</button>',
            '</div>',
            '</div>',
            '</div>',

            // APPROVAL ACTION MODAL
            '<div class="hr-modal-overlay" id="hrApprovalModal">',
            '<div class="hr-modal">',
            '<div class="hr-modal-header">',
            '<h5><i class="fas fa-check-circle"></i> Leave Action</h5>',
            '<button class="hr-modal-close" id="hrApprovalModalClose">&times;</button>',
            '</div>',
            '<div class="hr-modal-body">',
            '<div id="hrApprovalDetail" style="background:#eef1f8;border-radius:8px;padding:14px;font-size:.9rem;line-height:1.9"></div>',
            '<div class="hr-field">',
            '<label>Remarks (optional)</label>',
            '<textarea id="hrApprovalRemarks" placeholder="Add remarks…"></textarea>',
            '</div>',
            '</div>',
            '<div class="hr-modal-footer">',
            '<button class="hr-btn hr-btn-secondary hr-btn-sm" id="hrApprovalModalClose2">Cancel</button>',
            '<button class="hr-btn hr-btn-danger   hr-btn-sm" id="hrRejectBtn"><i class="fas fa-times-circle"></i> Reject</button>',
            '<button class="hr-btn hr-btn-success  hr-btn-sm" id="hrApproveBtn"><i class="fas fa-check-circle"></i> Approve</button>',
            '</div>',
            '</div>',
            '</div>',

            // TOAST
            '<div class="hr-toast" id="hrToast"></div>',

        ].join("") + '</div>';

        document.getElementById("hrYear").textContent = new Date().getFullYear();
        populateMonthYearFilters();
    }

    // ─── POPULATE MONTH / YEAR FILTERS ───────────────────────────────────────
    function populateMonthYearFilters() {
        var mSel = document.getElementById("hrAttMonth");
        var ySel = document.getElementById("hrAttYear");
        if (!mSel || !ySel) return;

        MONTHS.forEach(function (m, i) {
            var o = document.createElement("option");
            o.value = i;
            o.textContent = m;
            if (i === new Date().getMonth()) o.selected = true;
            mSel.appendChild(o);
        });

        var cy = new Date().getFullYear();
        for (var y = cy; y >= cy - 4; y--) {
            var o = document.createElement("option");
            o.value = y;
            o.textContent = y;
            if (y === cy) o.selected = true;
            ySel.appendChild(o);
        }
    }

    // ─── CLOCK ────────────────────────────────────────────────────────────────
    function startClock() {
        function tick() {
            var now = new Date();
            var time = now.toLocaleTimeString("en-GB");
            var date = now.toLocaleDateString("en-GB", {
                weekday: "long",
                day: "2-digit",
                month: "long",
                year: "numeric"
            });
            var el = document.getElementById("hrClock");
            var del = document.getElementById("hrDate");
            if (el) el.textContent = time;
            if (del) del.textContent = date;
        }

        tick();
        setInterval(tick, 1000);
    }

    // ─── RENDER: ATTENDANCE TABLE ─────────────────────────────────────────────
    function renderAttendanceTable(records) {
        var tbody = document.getElementById("hrAttBody");
        if (!tbody) return;
        if (!records || !records.length) {
            tbody.innerHTML = '<tr><td colspan="5"><div class="hr-empty"><i class="fas fa-clock"></i>No records found</div></td></tr>';
            return;
        }
        tbody.innerHTML = records.map(function (r) {
            var hours = r.checkIn && r.checkOut
                ? (((new Date(r.checkOut) - new Date(r.checkIn)) / 3600000).toFixed(1) + "h")
                : "—";
            var statusClass = r.status ? r.status.toLowerCase().replace(" ", "-") : "present";
            return '<tr>' +
                '<td>' + window.hrFormatDate(r.date || r.attendanceDate) + '</td>' +
                '<td>' + (r.checkIn ? new Date(r.checkIn).toLocaleTimeString("en-GB") : "—") + '</td>' +
                '<td>' + (r.checkOut ? new Date(r.checkOut).toLocaleTimeString("en-GB") : "—") + '</td>' +
                '<td>' + hours + '</td>' +
                '<td><span class="hr-badge ' + statusClass + '">' + (r.status || "Present") + '</span></td>' +
                '</tr>';
        }).join("");
    }

    // ─── RENDER: LEAVE BALANCE ────────────────────────────────────────────────
    function renderLeaveBalance(balance) {
        var map = {
            "Casual": "balCasual",
            "Sick": "balSick",
            "Earned": "balEarned",
            "Maternity": "balMaternity",
        };
        Object.keys(map).forEach(function (type) {
            var el = document.getElementById(map[type]);
            if (el) el.textContent = (balance[type] !== undefined ? balance[type] : "—");
        });
    }

    // ─── RENDER: LEAVE CALENDAR ───────────────────────────────────────────────
    function renderCalendar() {
        var cal = document.getElementById("hrCalendar");
        var title = document.getElementById("hrCalTitle");
        if (!cal) return;

        var month = window.hrState.calendarMonth;
        var year = window.hrState.calendarYear;
        if (title) title.textContent = MONTHS[month] + " " + year;

        var today = new Date();
        var weeks = window.hrBuildCalendar(month, year);

        var html = '<thead><tr>' +
            DAYS.map(function (d) {
                return '<th>' + d + '</th>';
            }).join("") +
            '</tr></thead><tbody>';

        weeks.forEach(function (week) {
            html += '<tr>';
            week.forEach(function (cell) {
                var dateStr = year + "-" +
                    String(month + 1).padStart(2, "0") + "-" +
                    String(cell.day).padStart(2, "0");

                var isToday = cell.currentMonth &&
                    cell.day === today.getDate() &&
                    month === today.getMonth() &&
                    year === today.getFullYear();
                var isWeekend = week.indexOf(cell) === 0 || week.indexOf(cell) === 6;
                var events = cell.currentMonth ? window.hrGetLeaveEventsForDate(dateStr) : [];
                var hasLeave = events.length > 0;

                var cls = [];
                if (!cell.currentMonth) cls.push("other-month");
                if (isToday) cls.push("today");
                if (hasLeave) cls.push("has-leave");
                if (isWeekend) cls.push("weekend");

                html += '<td class="' + cls.join(" ") + '">';
                html += '<div class="cal-day-num">' + cell.day + '</div>';
                events.forEach(function (ev) {
                    html += '<div class="hr-cal-event ' + window.hrGetLeaveTypeClass(ev.leaveType) + '">' +
                        (ev.leaveType || "Leave") + '</div>';
                });
                html += '</td>';
            });
            html += '</tr>';
        });

        html += '</tbody>';
        cal.innerHTML = html;
    }

    // ─── RENDER: MY LEAVE REQUESTS ────────────────────────────────────────────
    function renderMyLeaveRequests(requests) {
        var tbody = document.getElementById("hrMyLeaveBody");
        if (!tbody) return;
        if (!requests || !requests.length) {
            tbody.innerHTML = '<tr><td colspan="7"><div class="hr-empty"><i class="fas fa-calendar"></i>No leave requests yet</div></td></tr>';
            return;
        }
        tbody.innerHTML = requests.map(function (r) {
            var days = window.hrDaysBetween(r.startDate, r.endDate);
            return '<tr>' +
                '<td><span class="hr-badge ' + window.hrGetLeaveTypeClass(r.leaveType) + '">' + (r.leaveType || "—") + '</span></td>' +
                '<td>' + window.hrFormatDate(r.startDate) + '</td>' +
                '<td>' + window.hrFormatDate(r.endDate) + '</td>' +
                '<td>' + days + '</td>' +
                '<td>' + (r.reason || "—") + '</td>' +
                '<td><span class="hr-badge ' + (r.status || "pending").toLowerCase() + '">' + (r.status || "Pending") + '</span></td>' +
                '<td>' + window.hrFormatDate(r.appliedOn) + '</td>' +
                '</tr>';
        }).join("");
    }

    // ─── RENDER: APPROVAL QUEUE ───────────────────────────────────────────────
    var _pendingApproval = null;

    function renderApprovalQueue(requests) {
        var tbody = document.getElementById("hrApprovalBody");
        if (!tbody) return;
        var pending = (requests || []).filter(function (r) {
            return r.status === "Pending" || !r.status;
        });
        if (!pending.length) {
            tbody.innerHTML = '<tr><td colspan="8"><div class="hr-empty"><i class="fas fa-check-circle"></i>No pending approvals</div></td></tr>';
            return;
        }
        tbody.innerHTML = pending.map(function (r) {
            var days = window.hrDaysBetween(r.startDate, r.endDate);
            return '<tr>' +
                '<td><strong>' + (r.employeeName || r.employeeId || "—") + '</strong></td>' +
                '<td>' + (r.leaveType || "—") + '</td>' +
                '<td>' + window.hrFormatDate(r.startDate) + '</td>' +
                '<td>' + window.hrFormatDate(r.endDate) + '</td>' +
                '<td>' + days + '</td>' +
                '<td>' + (r.reason || "—") + '</td>' +
                '<td><span class="hr-badge pending">Pending</span></td>' +
                '<td>' +
                '<button class="hr-btn hr-btn-sm hr-btn-primary" data-id="' + r.leaveId + '" ' +
                'data-json=\'' + JSON.stringify(r) + '\' onclick="hrOpenApproval(this)">' +
                '<i class="fas fa-eye"></i> Review' +
                '</button>' +
                '</td>' +
                '</tr>';
        }).join("");
    }

    // Exposed globally for inline onclick
    window.hrOpenApproval = function (btn) {
        var data = JSON.parse(btn.getAttribute("data-json"));
        _pendingApproval = data;
        var days = window.hrDaysBetween(data.startDate, data.endDate);
        document.getElementById("hrApprovalDetail").innerHTML =
            '<strong>Employee:</strong> ' + (data.employeeName || data.employeeId) + '<br>' +
            '<strong>Leave Type:</strong> ' + data.leaveType + '<br>' +
            '<strong>Period:</strong> ' + window.hrFormatDate(data.startDate) + ' – ' + window.hrFormatDate(data.endDate) + ' (' + days + ' days)<br>' +
            '<strong>Reason:</strong> ' + (data.reason || "—");
        document.getElementById("hrApprovalRemarks").value = "";
        document.getElementById("hrApprovalModal").classList.add("open");
    };

    // ─── RENDER: DOCUMENTS TABLE ──────────────────────────────────────────────
    function renderDocuments(docs) {
        var tbody = document.getElementById("hrDocBody");
        if (!tbody) return;
        if (!docs || !docs.length) {
            tbody.innerHTML = '<tr><td colspan="4"><div class="hr-empty"><i class="fas fa-folder"></i>No documents uploaded yet</div></td></tr>';
            return;
        }
        tbody.innerHTML = docs.map(function (d) {
            return '<tr>' +
                '<td><i class="fas fa-file-alt" style="color:var(--accent);margin-right:8px"></i>' + (d.documentName || d.fileName || "—") + '</td>' +
                '<td>' + (d.documentType || "—") + '</td>' +
                '<td>' + window.hrFormatDate(d.uploadedOn) + '</td>' +
                '<td>' +
                (d.fileUrl
                    ? '<a href="' + d.fileUrl + '" target="_blank" class="hr-btn hr-btn-sm hr-btn-secondary"><i class="fas fa-eye"></i> View</a>'
                    : '<span style="color:var(--muted);font-size:.82rem">No link</span>') +
                '</td>' +
                '</tr>';
        }).join("");
    }

    // ─── TOAST ────────────────────────────────────────────────────────────────
    function toast(msg, type) {
        var el = document.getElementById("hrToast");
        if (!el) return;
        el.textContent = msg;
        el.style.background = type === "error" ? "#d92d2d" : "#17192e";
        el.classList.add("show");
        setTimeout(function () {
            el.classList.remove("show");
        }, 3500);
    }

    // ─── MODAL HELPERS ────────────────────────────────────────────────────────
    function openModal(id) {
        var m = document.getElementById(id);
        if (m) m.classList.add("open");
    }

    function closeModal(id) {
        var m = document.getElementById(id);
        if (m) m.classList.remove("open");
    }

    // ─── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {

        // Tabs
        document.querySelectorAll(".hr-tab").forEach(function (tab) {
            tab.addEventListener("click", function () {
                document.querySelectorAll(".hr-tab").forEach(function (t) {
                    t.classList.remove("active");
                });
                document.querySelectorAll(".hr-tab-content").forEach(function (c) {
                    c.style.display = "none";
                });
                this.classList.add("active");
                var target = this.getAttribute("data-tab");
                document.getElementById("tab-" + target).style.display = "block";
                window.hrState.activeTab = target;

                // Load data on tab switch
                if (target === "leave") {
                    loadLeaveData();
                } else if (target === "approval") {
                    window.hrFetchLeaveRequests("").then(function (data) {
                        renderApprovalQueue(data);
                    });
                } else if (target === "documents") {
                    var empId = window.hrState.currentEmployee
                        ? window.hrState.currentEmployee.id : "";
                    window.hrFetchDocuments(empId).then(renderDocuments);
                }
            });
        });

        // Check-in
        document.getElementById("hrCheckInBtn").addEventListener("click", function () {
            window.hrCheckIn().then(function (result) {
                document.getElementById("hrCheckinStatus").textContent =
                    "Checked in at " + new Date().toLocaleTimeString("en-GB");
                document.getElementById("hrCheckInBtn").disabled = true;
                document.getElementById("hrCheckOutBtn").disabled = false;
                toast("Checked in successfully.");
                if (typeof swal === "function") {
                    swal({title: "Checked In!", text: "Have a productive day.", type: "success"});
                }
            }).catch(function () {
                toast("Check-in failed.", "error");
            });
        });

        // Check-out
        document.getElementById("hrCheckOutBtn").addEventListener("click", function () {
            window.hrCheckOut().then(function () {
                document.getElementById("hrCheckinStatus").textContent =
                    "Checked out at " + new Date().toLocaleTimeString("en-GB");
                document.getElementById("hrCheckOutBtn").disabled = true;
                toast("Checked out successfully.");
            }).catch(function () {
                toast("Check-out failed.", "error");
            });
        });

        // Load attendance history
        document.getElementById("hrAttFetchBtn").addEventListener("click", function () {
            var month = parseInt(document.getElementById("hrAttMonth").value, 10);
            var year = parseInt(document.getElementById("hrAttYear").value, 10);
            var empId = window.hrState.currentEmployee
                ? window.hrState.currentEmployee.id : "";
            window.hrFetchAttendance(empId, month, year).then(renderAttendanceTable);
        });

        // Refresh
        document.getElementById("hrRefreshBtn").addEventListener("click", function () {
            loadInitialAttendanceStats();
            toast("Refreshed.");
        });

        // Refresh approvals
        document.getElementById("hrRefreshApprovalsBtn").addEventListener("click", function () {
            window.hrFetchLeaveRequests("").then(renderApprovalQueue);
        });

        // Apply leave button
        document.getElementById("hrApplyLeaveBtn").addEventListener("click", function () {
            openModal("hrLeaveModal");
        });

        // Auto-calculate days
        ["hrLeaveFrom", "hrLeaveTo"].forEach(function (id) {
            document.getElementById(id).addEventListener("change", function () {
                var from = document.getElementById("hrLeaveFrom").value;
                var to = document.getElementById("hrLeaveTo").value;
                if (from && to) {
                    var days = window.hrDaysBetween(from, to);
                    document.getElementById("hrLeaveDays").value = days + " day(s)";
                }
            });
        });

        // Submit leave
        document.getElementById("hrLeaveModalSubmit").addEventListener("click", function () {
            var type = document.getElementById("hrLeaveType").value;
            var from = document.getElementById("hrLeaveFrom").value;
            var to = document.getElementById("hrLeaveTo").value;
            var reason = document.getElementById("hrLeaveReason").value;

            if (!type || !from || !to) {
                toast("Fill all required fields.", "error");
                return;
            }
            if (new Date(to) < new Date(from)) {
                toast("End date must be after start date.", "error");
                return;
            }

            var empId = window.hrState.currentEmployee
                ? window.hrState.currentEmployee.id : "";

            window.hrApplyLeave({
                employeeId: empId, leaveType: type,
                startDate: from, endDate: to, reason: reason,
            }).then(function () {
                closeModal("hrLeaveModal");
                toast("Leave application submitted.");
                if (typeof swal === "function") {
                    swal({
                        title: "Submitted!",
                        text: "Your leave request has been sent for approval.",
                        type: "success"
                    });
                }
                loadLeaveData();
            }).catch(function () {
                toast("Submission failed.", "error");
            });
        });

        // Leave modal close
        document.getElementById("hrLeaveModalClose").addEventListener("click", function () {
            closeModal("hrLeaveModal");
        });
        document.getElementById("hrLeaveModalCancel").addEventListener("click", function () {
            closeModal("hrLeaveModal");
        });
        document.getElementById("hrLeaveModal").addEventListener("click", function (e) {
            if (e.target === e.currentTarget) closeModal("hrLeaveModal");
        });

        // Calendar navigation
        document.getElementById("hrCalPrevBtn").addEventListener("click", function () {
            window.hrState.calendarMonth--;
            if (window.hrState.calendarMonth < 0) {
                window.hrState.calendarMonth = 11;
                window.hrState.calendarYear--;
            }
            renderCalendar();
        });
        document.getElementById("hrCalNextBtn").addEventListener("click", function () {
            window.hrState.calendarMonth++;
            if (window.hrState.calendarMonth > 11) {
                window.hrState.calendarMonth = 0;
                window.hrState.calendarYear++;
            }
            renderCalendar();
        });

        // Upload document button + drop zone
        document.getElementById("hrUploadDocBtn").addEventListener("click", function () {
            openModal("hrDocModal");
        });
        document.getElementById("hrDropZone").addEventListener("click", function () {
            document.getElementById("hrDocFileInput").click();
        });
        document.getElementById("hrDocFileInput").addEventListener("change", function () {
            if (this.files[0]) {
                document.getElementById("hrDocFile").files = this.files;
                openModal("hrDocModal");
                document.getElementById("hrDocFileInfo").textContent =
                    "Selected: " + this.files[0].name;
            }
        });

        // Drag & drop
        var dz = document.getElementById("hrDropZone");
        dz.addEventListener("dragover", function (e) {
            e.preventDefault();
            dz.classList.add("drag-over");
        });
        dz.addEventListener("dragleave", function () {
            dz.classList.remove("drag-over");
        });
        dz.addEventListener("drop", function (e) {
            e.preventDefault();
            dz.classList.remove("drag-over");
            if (e.dataTransfer.files[0]) {
                document.getElementById("hrDocFile").files = e.dataTransfer.files;
                document.getElementById("hrDocFileInfo").textContent =
                    "Selected: " + e.dataTransfer.files[0].name;
                openModal("hrDocModal");
            }
        });

        // File selected in modal
        document.getElementById("hrDocFile").addEventListener("change", function () {
            if (this.files[0]) {
                document.getElementById("hrDocFileInfo").textContent =
                    "Selected: " + this.files[0].name;
            }
        });

        // Upload submit
        document.getElementById("hrDocModalUpload").addEventListener("click", function () {
            var type = document.getElementById("hrDocType").value;
            var name = document.getElementById("hrDocName").value.trim();
            var file = document.getElementById("hrDocFile").files[0];

            if (!type || !name || !file) {
                toast("Fill all fields and select a file.", "error");
                return;
            }

            var empId = window.hrState.currentEmployee
                ? window.hrState.currentEmployee.id : "";

            // Read file as base64
            var reader = new FileReader();
            reader.onload = function (ev) {
                var base64 = ev.target.result.split(",")[1];
                window.hrUploadDocument(empId, type, name, base64, file.name)
                    .then(function () {
                        closeModal("hrDocModal");
                        toast("Document uploaded successfully.");
                        window.hrFetchDocuments(empId).then(renderDocuments);
                    })
                    .catch(function () {
                        toast("Upload failed.", "error");
                    });
            };
            reader.readAsDataURL(file);
        });

        // Doc modal close
        document.getElementById("hrDocModalClose").addEventListener("click", function () {
            closeModal("hrDocModal");
        });
        document.getElementById("hrDocModalCancel").addEventListener("click", function () {
            closeModal("hrDocModal");
        });

        // Approval actions
        document.getElementById("hrApproveBtn").addEventListener("click", function () {
            if (!_pendingApproval) return;
            var remarks = document.getElementById("hrApprovalRemarks").value;
            window.hrApproveLeave(_pendingApproval.leaveId, "Approved", remarks)
                .then(function () {
                    closeModal("hrApprovalModal");
                    toast("Leave approved.");
                    window.hrFetchLeaveRequests("").then(renderApprovalQueue);
                }).catch(function () {
                toast("Action failed.", "error");
            });
        });

        document.getElementById("hrRejectBtn").addEventListener("click", function () {
            if (!_pendingApproval) return;
            var remarks = document.getElementById("hrApprovalRemarks").value;
            window.hrApproveLeave(_pendingApproval.leaveId, "Rejected", remarks)
                .then(function () {
                    closeModal("hrApprovalModal");
                    toast("Leave rejected.");
                    window.hrFetchLeaveRequests("").then(renderApprovalQueue);
                }).catch(function () {
                toast("Action failed.", "error");
            });
        });

        document.getElementById("hrApprovalModalClose").addEventListener("click", function () {
            closeModal("hrApprovalModal");
        });
        document.getElementById("hrApprovalModalClose2").addEventListener("click", function () {
            closeModal("hrApprovalModal");
        });
        document.getElementById("hrApprovalModal").addEventListener("click", function (e) {
            if (e.target === e.currentTarget) closeModal("hrApprovalModal");
        });
    }

    // ─── DATA LOADERS ─────────────────────────────────────────────────────────
    function loadInitialAttendanceStats() {
        window.hrFetchTodayAttendance().then(function (data) {
            var present = data.filter(function (r) {
                return r.status === "Present" || r.checkIn;
            }).length;
            var onLeave = data.filter(function (r) {
                return r.status === "On Leave";
            }).length;
            var absent = data.filter(function (r) {
                return r.status === "Absent";
            }).length;
            var total = data.length || 1;
            var rate = Math.round((present / total) * 100);

            setText("statPresent", present);
            setText("statAbsent", absent);
            setText("statOnLeave", onLeave);
            setText("statRate", rate + "%");
        });
    }

    function loadLeaveData() {
        var empId = window.hrState.currentEmployee
            ? window.hrState.currentEmployee.id : "";

        window.hrFetchLeaveBalance(empId).then(renderLeaveBalance);
        window.hrFetchLeaveRequests(empId).then(function (data) {
            window.hrData.myLeaveRequests = data;
            renderMyLeaveRequests(data);
            renderCalendar();
        });
    }

    function setText(id, val) {
        var el = document.getElementById(id);
        if (el) el.textContent = val;
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        startClock();
        renderCalendar();
        waitForData();
    }

    function waitForData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.hrData && window.hrData._loaded) {
                clearInterval(timer);
                loadInitialAttendanceStats();
            } else if (attempts > 200) {
                clearInterval(timer);
                console.warn("[hrLeave] Data load timeout.");
            }
        }, 30);
    }

    // ─── LOAD LOGIC SCRIPT ────────────────────────────────────────────────────
    function loadLogicScript() {
        if (window.hrData && window.hrState) {
            init();
            return;
        }

        var s = document.createElement("script");
        s.type = "text/javascript";
        s.src = _base + "../_hrLeave.js";

        s.onload = function () {
            if (window.hrData && window.hrState) {
                init();
            } else {
                console.error("[hrLeave] _hrLeave.js loaded but hrData missing.");
            }
        };
        s.onerror = function () {
            console.error("[hrLeave] Could not load: " + _base + "../_hrLeave.js");
        };

        document.body.appendChild(s);
    }

    // ─── BOOT ─────────────────────────────────────────────────────────────────
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initHrLeave = init;

})();