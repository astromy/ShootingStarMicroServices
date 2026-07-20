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
        attendance: [],        // today's attendance list (for HR view)
        myAttendance: [],      // current employee's attendance history
        leaveBalance: {},      // { casual: n, sick: n, earned: n, maternity: n }
        leaveRequests: [],     // leave requests for approval (manager view)
        myLeaveRequests: [],   // current employee's leave history
        documents: [],         // current employee's documents
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

    // ─── GHANA PUBLIC HOLIDAYS ────────────────────────────────────────────────
    // Fixed statutory holidays. When a holiday falls on a Saturday it is
    // observed on the following Monday; Sunday holidays also shift to Monday.

    function _easterSunday(y) {
        var a = y % 19, b = Math.floor(y / 100), c = y % 100, d = Math.floor(b / 4), e = b % 4;
        var f = Math.floor((b + 8) / 25), g = Math.floor((b - f + 1) / 3);
        var h = (19 * a + b - d - g + 15) % 30;
        var i = Math.floor(c / 4), k = c % 4, l = (32 + 2 * e + 2 * i - h - k) % 7;
        var m = Math.floor((a + 11 * h + 22 * l) / 451);
        var month = Math.floor((h + l - 7 * m + 114) / 31);
        var day = ((h + l - 7 * m + 114) % 31) + 1;
        return new Date(y, month - 1, day);
    }

    function _observedDate(date) {
        var dow = date.getDay();
        var obs = new Date(date);
        if (dow === 6) obs.setDate(date.getDate() + 2); // Sat → Mon
        else if (dow === 0) obs.setDate(date.getDate() + 1); // Sun → Mon
        return obs;
    }

    // Approximate Islamic holiday dates using the lunar-cycle offset from a
    // known anchor year. Accurate to ±1 day — sufficient for leave planning.
    // Ghana officially announces exact dates close to the event; override
    // these with a live source (e.g. Calendarific) once confirmed each year.
    function _islamicApprox(year) {
        var CYCLE = 354.36708; // mean Islamic year in days
        var daysOff = Math.round((year - 2000) * CYCLE);
        var eidFitr = new Date(2000, 0, 8);
        eidFitr.setDate(eidFitr.getDate() + daysOff);
        var eidAdha = new Date(2000, 2, 16);
        eidAdha.setDate(eidAdha.getDate() + daysOff);
        return [eidFitr, eidAdha];
    }

    function _buildHolidaySet(year) {
        var fixed = [
            [1, 1],  // New Year's Day
            [1, 7],  // Constitution Day
            [3, 6],  // Independence Day
            [5, 1],  // May Day (Workers' Day)
            [7, 1],  // Republic Day
            [8, 4],  // Founders' Day
            [9, 21], // Kwame Nkrumah Memorial Day
            [12, 25], // Christmas Day
            [12, 26], // Boxing Day
        ];

        var easter = _easterSunday(year);
        var movable = [
            new Date(easter.getFullYear(), easter.getMonth(), easter.getDate() - 2), // Good Friday
            new Date(easter.getFullYear(), easter.getMonth(), easter.getDate() - 1), // Holy Saturday
            new Date(easter.getFullYear(), easter.getMonth(), easter.getDate() + 1), // Easter Monday
        ];

        var islamic = _islamicApprox(year);

        var set = {};

        function addDate(d) {
            var obs = _observedDate(d);
            set[obs.toISOString().slice(0, 10)] = true;
        }

        fixed.forEach(function (md) {
            addDate(new Date(year, md[0] - 1, md[1]));
        });
        movable.forEach(addDate);
        islamic.forEach(addDate);

        return set; // { "YYYY-MM-DD": true, … }
    }

    // Per-year cache so the set is only computed once per year encountered
    var _holidayCache = {};

    function _ghanaHolidaySet(year) {
        if (!_holidayCache[year]) _holidayCache[year] = _buildHolidaySet(year);
        return _holidayCache[year];
    }

    // Expose so other scripts can check a specific date if needed
    window.hrIsGhanaHoliday = function (dateStr) {
        var d = new Date(dateStr + "T00:00:00");
        return !!_ghanaHolidaySet(d.getFullYear())[dateStr];
    };

    // ─── WORKING DAYS ─────────────────────────────────────────────────────────
    // Counts working days between two date strings (YYYY-MM-DD), inclusive.
    // Excludes weekends and Ghana public holidays (with weekend-shift rule).

    window.hrDaysBetween = function (startStr, endStr) {
        var start = new Date(startStr + "T00:00:00");
        var end = new Date(endStr + "T00:00:00");
        if (end < start) return 0;

        var count = 0;
        var cur = new Date(start);

        while (cur <= end) {
            var dow = cur.getDay();
            var ymd = cur.toISOString().slice(0, 10);
            var isWeekend = (dow === 0 || dow === 6);
            var isHoliday = !isWeekend && !!_ghanaHolidaySet(cur.getFullYear())[ymd];
            if (!isWeekend && !isHoliday) count++;
            cur.setDate(cur.getDate() + 1);
        }

        return count;
    };

    // ─── KICK OFF ─────────────────────────────────────────────────────────────
    if (window.copyrights) window.copyrights();

    fetchLookup(instId.split(",")[0]);

})();