/**
 * _hrLeave.js  —  Data & logic layer for HR Phase 2
 * (Attendance, Leave, Documents)
 *
 * Loaded at runtime by hrLeave.js.
 * Uses the existing fetchPost helper.
 *
 * API calls:
 *   getLookUpByType          → departments / employee types
 *   getEmployeeAttendance    → attendance records
 *   checkInEmployee          → web check-in
 *   checkOutEmployee         → web check-out
 *   getLeaveBalance          → leave balance per employee
 *   applyLeave               → submit leave request
 *   getLeaveRequests         → list leave requests
 *   approveLeave             → approve / reject leave
 *   getEmployeeDocuments     → list documents
 *   uploadEmployeeDocument   → upload document
 */

(function () {
    "use strict";

    // ─── Institution code ─────────────────────────────────────────────────────
    var _rawInst = (typeof instId !== "undefined" ? instId : "").split(",")[0];
    var _instCode = _rawInst.replace(/[\[\]']+/g, "").replace(/\//g, "");
    var v;

    // ─── STATE ────────────────────────────────────────────────────────────────
    window.hrState = {
        institutionCode: _instCode,
        currentEmployee: null,    // logged-in employee object
        activeTab: "attendance",
        selectedMonth: new Date().getMonth(),
        selectedYear: new Date().getFullYear(),
        calendarMonth: new Date().getMonth(),
        calendarYear: new Date().getFullYear(),
        _loaded: false,
    };

    // ─── DATA ─────────────────────────────────────────────────────────────────
    window.hrData = {
        departments: [],
        employees: [],
        attendance: [],   // today's attendance list (for HR view)
        myAttendance: [],   // current employee's attendance history
        leaveBalance: {},   // { casual: n, sick: n, earned: n, maternity: n }
        leaveRequests: [],   // leave requests for approval (manager view)
        myLeaveRequests: [],   // current employee's leave history
        documents: [],   // current employee's documents
        _loaded: false,
    };

    // ─── LEAVE TYPES ──────────────────────────────────────────────────────────
    window.HR_LEAVE_TYPES = [
        {value: "Casual", label: "Casual Leave", days: 12},
        {value: "Sick", label: "Sick Leave", days: 10},
        {value: "Earned", label: "Earned Leave", days: 15},
        {value: "Maternity", label: "Maternity / Paternity", days: 84},
    ];

    // ─── SPLASH ───────────────────────────────────────────────────────────────
    function showSplash() {
        if (typeof $ !== "undefined") {
            $(".splash").css({display: "block", background: "#ffffff3d"}).find("h1, p").remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== "undefined") $(".splash").css("display", "none");
    }

    // ─── INITIAL LOAD ─────────────────────────────────────────────────────────
    async function fetchLookup(rawInstId) {
        showSplash();
        v = rawInstId.replace(/[\[\]']+/g, "").replace(/\//g, "");
        try {
            var result = await fetchPost("getLookUpByType", {val: "Department"});
            window.hrData.departments = (result || []).map(function (d) {
                return {id: d.id, name: d.name};
            });
            window.hrData._loaded = true;
            hideSplash();
            console.log("[_hrLeave] Departments loaded.", window.hrData.departments);
        } catch (err) {
            hideSplash();
            window.hrData._loaded = true;
            console.error("[_hrLeave] fetchLookup failed:", err);
        }
    }

    // ─── ATTENDANCE ───────────────────────────────────────────────────────────

    window.hrCheckIn = async function () {
        try {
            showSplash();
            var result = await fetchPost("checkInEmployee", {
                institutionCode: _instCode,
                employeeId: window.hrState.currentEmployee
                    ? window.hrState.currentEmployee.id : "",
                checkInTime: new Date().toISOString(),
                location: "Web",
            });
            hideSplash();
            return result;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] checkIn failed:", err);
            throw err;
        }
    };

    window.hrCheckOut = async function () {
        try {
            showSplash();
            var result = await fetchPost("checkOutEmployee", {
                institutionCode: _instCode,
                employeeId: window.hrState.currentEmployee
                    ? window.hrState.currentEmployee.id : "",
                checkOutTime: new Date().toISOString(),
            });
            hideSplash();
            return result;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] checkOut failed:", err);
            throw err;
        }
    };

    window.hrFetchAttendance = async function (employeeId, month, year) {
        try {
            showSplash();
            var result = await fetchPost("getEmployeeAttendance", {
                institutionCode: _instCode,
                employeeId: employeeId || "",
                month: month !== undefined ? month : window.hrState.selectedMonth,
                year: year !== undefined ? year : window.hrState.selectedYear,
            });
            window.hrData.myAttendance = result || [];
            hideSplash();
            return window.hrData.myAttendance;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] fetchAttendance failed:", err);
            return [];
        }
    };

    // Fetch today's attendance for the whole institution (HR/manager view)
    window.hrFetchTodayAttendance = async function () {
        try {
            showSplash();
            var today = new Date().toISOString().split("T")[0];
            var result = await fetchPost("getEmployeeAttendance", {
                institutionCode: _instCode,
                employeeId: "",
                date: today,
            });
            window.hrData.attendance = result || [];
            hideSplash();
            return window.hrData.attendance;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] fetchTodayAttendance failed:", err);
            return [];
        }
    };

    // ─── LEAVE ────────────────────────────────────────────────────────────────

    window.hrFetchLeaveBalance = async function (employeeId) {
        try {
            showSplash();
            var result = await fetchPost("getLeaveBalance", {
                institutionCode: _instCode,
                employeeId: employeeId || "",
                year: new Date().getFullYear(),
            });
            // Normalise response into { Casual: n, Sick: n, Earned: n, Maternity: n }
            window.hrData.leaveBalance = normaliseBalance(result);
            hideSplash();
            return window.hrData.leaveBalance;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] fetchLeaveBalance failed:", err);
            return {};
        }
    };

    function normaliseBalance(result) {
        if (!result) return {};
        // Support both array and object responses
        if (Array.isArray(result)) {
            var out = {};
            result.forEach(function (r) {
                var key = r.leaveType || r.type || r.name || "";
                out[key] = r.balance || r.remaining || r.days || 0;
            });
            return out;
        }
        return result;
    }

    window.hrApplyLeave = async function (payload) {
        try {
            showSplash();
            var result = await fetchPost("applyLeave", {
                institutionCode: _instCode,
                employeeId: payload.employeeId,
                leaveType: payload.leaveType,
                startDate: payload.startDate,
                endDate: payload.endDate,
                reason: payload.reason,
                appliedOn: new Date().toISOString(),
            });
            hideSplash();
            return result;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] applyLeave failed:", err);
            throw err;
        }
    };

    window.hrFetchLeaveRequests = async function (employeeId) {
        try {
            showSplash();
            var result = await fetchPost("getLeaveRequests", {
                institutionCode: _instCode,
                employeeId: employeeId || "",
            });
            if (employeeId) {
                window.hrData.myLeaveRequests = result || [];
            } else {
                window.hrData.leaveRequests = result || [];
            }
            hideSplash();
            return result || [];
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] fetchLeaveRequests failed:", err);
            return [];
        }
    };

    window.hrApproveLeave = async function (leaveId, status, remarks) {
        try {
            showSplash();
            var result = await fetchPost("approveLeave", {
                institutionCode: _instCode,
                leaveId: leaveId,
                status: status,   // "Approved" | "Rejected"
                remarks: remarks || "",
                actionDate: new Date().toISOString(),
            });
            hideSplash();
            return result;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] approveLeave failed:", err);
            throw err;
        }
    };

    // ─── DOCUMENTS ────────────────────────────────────────────────────────────

    window.hrFetchDocuments = async function (employeeId) {
        try {
            showSplash();
            var result = await fetchPost("getEmployeeDocuments", {
                institutionCode: _instCode,
                employeeId: employeeId || "",
            });
            window.hrData.documents = result || [];
            hideSplash();
            return window.hrData.documents;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] fetchDocuments failed:", err);
            return [];
        }
    };

    window.hrUploadDocument = async function (employeeId, docType, docName, base64Data, fileName) {
        try {
            showSplash();
            var result = await fetchPost("uploadEmployeeDocument", {
                institutionCode: _instCode,
                employeeId: employeeId,
                documentType: docType,
                documentName: docName,
                fileName: fileName,
                fileContent: base64Data,
                uploadedOn: new Date().toISOString(),
            });
            hideSplash();
            return result;
        } catch (err) {
            hideSplash();
            console.error("[_hrLeave] uploadDocument failed:", err);
            throw err;
        }
    };

    // ─── CALENDAR HELPERS ─────────────────────────────────────────────────────

    // Returns a 2D array (weeks × days) for the given month/year
    window.hrBuildCalendar = function (month, year) {
        var firstDay = new Date(year, month, 1).getDay(); // 0=Sun
        var daysInMonth = new Date(year, month + 1, 0).getDate();
        var daysInPrev = new Date(year, month, 0).getDate();

        var weeks = [];
        var day = 1 - firstDay; // may be negative (prev month)

        for (var w = 0; w < 6; w++) {
            var week = [];
            for (var d = 0; d < 7; d++) {
                if (day < 1) {
                    week.push({day: daysInPrev + day, currentMonth: false});
                } else if (day > daysInMonth) {
                    week.push({day: day - daysInMonth, currentMonth: false});
                } else {
                    week.push({day: day, currentMonth: true});
                }
                day++;
            }
            weeks.push(week);
            if (day > daysInMonth) break;
        }
        return weeks;
    };

    // Returns leave events for a given date (YYYY-MM-DD)
    window.hrGetLeaveEventsForDate = function (dateStr) {
        return (window.hrData.myLeaveRequests || []).filter(function (req) {
            if (req.status === "Rejected") return false;
            var start = new Date(req.startDate);
            var end = new Date(req.endDate);
            var date = new Date(dateStr);
            return date >= start && date <= end;
        });
    };

    // ─── UTILITY ──────────────────────────────────────────────────────────────

    window.hrFormatDate = function (dateStr) {
        if (!dateStr) return "—";
        var d = new Date(dateStr);
        return d.toLocaleDateString("en-GB", {day: "2-digit", month: "short", year: "numeric"});
    };

    window.hrDaysBetween = function (start, end) {
        var s = new Date(start);
        var e = new Date(end);
        var diff = Math.ceil((e - s) / (1000 * 60 * 60 * 24)) + 1;
        return diff > 0 ? diff : 0;
    };

    window.hrGetLeaveTypeClass = function (type) {
        var map = {
            "Casual": "casual",
            "Sick": "sick",
            "Earned": "earned",
            "Maternity": "maternity",
            "Paternity": "maternity",
        };
        return map[type] || "casual";
    };

    // ─── KICK OFF ─────────────────────────────────────────────────────────────
    if (window.copyrights) window.copyrights();

    fetchLookup(instId.split(",")[0]);

})();