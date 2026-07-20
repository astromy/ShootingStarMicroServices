/**
 * _timetable.js  —  Data & logic layer for the Class Timetable Scheduler.
 *
 * Loaded at runtime by timetable.js (the UI file).
 * Uses the existing fetchPost helper.
 *
 * API calls:
 *   getLookUpByType        → class groups
 *   getInstitutionClassesByClassGroup → classes per group
 *   getInstitutionSubjectsAndClassGroup → subjects per group
 *   getInstitutionTeachers → teachers
 *   saveTimetable          → persist timetable
 *   getTimetable           → load saved timetable
 */

(function () {
    "use strict";

    // ─── Institution code ─────────────────────────────────────────────────────
    var _rawInst = (typeof instId !== "undefined" ? instId : "").split(",")[0];
    var _instCode = _rawInst.replace(/[\[\]']+/g, "").replace(/\//g, "");

    // ─── STATE ────────────────────────────────────────────────────────────────
    window.ttState = {
        institutionCode: _instCode,

        // Settings
        startTime: "07:30",
        endTime: "15:00",
        periodDuration: 40,         // minutes
        breaks: [           // { name, after (period index), duration }
            {name: "Short Break", after: 2, duration: 20},
            {name: "Lunch Break", after: 4, duration: 40},
        ],

        // Computed (filled by computePeriods)
        periods: [],         // [{ label, start, end, isBreak, breakName }]

        // Data
        classGroups: [],
        classes: [],
        subjects: [],
        teachers: [],

        // Timetable storage
        // { className: { Mon: [periodEntries], Tue: [...], ... } }
        timetables: {},

        // UI
        activeTab: "setup",    // setup | class-view | teacher-view
        selectedClass: "",
        selectedTeacher: "",
        selectedGroup: "",

        // Drag state
        _drag: null,
    };

    // ─── DATA ─────────────────────────────────────────────────────────────────
    window.ttData = {
        classGroups: [],
        classes: [],
        subjects: [],
        teachers: [],
        _loaded: false,
    };

    // ─── DAYS ─────────────────────────────────────────────────────────────────
    window.TT_DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];

    // ─── SPLASH ───────────────────────────────────────────────────────────────
    function showSplash() {
        if (typeof $ !== "undefined") {
            $(".splash").css({display: "block", background: "#ffffff3d"}).find("h1, p").remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== "undefined") $(".splash").css("display", "none");
    }

    // ─── PERIOD COMPUTATION ───────────────────────────────────────────────────
    /**
     * Computes periods from:
     *   startTime      "HH:MM"
     *   endTime        "HH:MM"
     *   periodDuration minutes (number)
     *   breaks         [{ name, after, duration }]
     *     after = how many teaching periods before this break
     *
     * Returns array of { label, start, end, isBreak, breakName }
     */
    window.computePeriods = function (startTime, endTime, periodDuration, breaks) {
        var result = [];
        var current = parseTime(startTime);
        var end = parseTime(endTime);
        var dur = parseInt(periodDuration, 10) || 40;

        // Build a sorted map: after N periods → break
        var breakMap = {};
        (breaks || []).forEach(function (b) {
            breakMap[parseInt(b.after, 10)] = b;
        });

        var periodCount = 0;

        while (current < end) {
            // Insert break if scheduled after this many teaching periods
            if (breakMap[periodCount]) {
                var brk = breakMap[periodCount];
                var brkEnd = current + parseInt(brk.duration, 10);
                result.push({
                    label: brk.name,
                    start: formatTime(current),
                    end: formatTime(brkEnd),
                    isBreak: true,
                    breakName: brk.name,
                });
                current = brkEnd;
                if (current >= end) break;
            }

            var periodEnd = current + dur;
            if (periodEnd > end) break;

            periodCount++;
            result.push({
                label: "Period " + periodCount,
                start: formatTime(current),
                end: formatTime(periodEnd),
                isBreak: false,
                breakName: null,
            });
            current = periodEnd;
        }

        window.ttState.periods = result;
        return result;
    };

    function parseTime(str) {
        // returns total minutes from midnight
        var parts = (str || "00:00").split(":");
        return parseInt(parts[0], 10) * 60 + parseInt(parts[1], 10);
    }

    function formatTime(minutes) {
        var h = Math.floor(minutes / 60);
        var m = minutes % 60;
        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
    }

    window.formatTime = formatTime;

    // ─── 1. FETCH CLASS GROUPS (on load) ─────────────────────────────────────
    // Mirrors fetchLookup() pattern used across all modules.
    // v is declared at module scope so it is accessible everywhere below.
    var v;

    async function fetchLookup(rawInstId) {
        showSplash();
        v = rawInstId.replace(/[\[\]']+/g, "").replace(/\//g, "");
        return fetchPost("getLookUpByType", {val: "ClassGroup"}).then(function (result) {
            populateClassGroup(result);
            hideSplash();
            window.ttData._loaded = true;
        }).catch(function (err) {
            hideSplash();
            window.ttData._loaded = true;
            console.error("[_timetable] fetchLookup failed:", err);
        });
    }

    function populateClassGroup(data) {
        if (!Array.isArray(data)) return;
        window.ttData.classGroups = data.map(function (d) {
            // value is name — payload expects group name not id (consistent with other modules)
            return {id: d.id, name: d.name, value: d.name};
        });
    }

    // ─── 2. FETCH CLASSES + SUBJECTS on group change ──────────────────────────
    // Mirrors classGroupSelect change handler pattern used across all modules.
    window.ttFetchByGroup = async function (groupName) {
        try {
            showSplash();

            var instRequest = {id: 0, name: _instCode, classGroup: groupName, preference: 0};
            var instRequest2 = {institution: _instCode, classGroup: groupName};

            var [subjects, classes] = await Promise.all([
                fetchPost("getInstitutionSubjectsAndClassGroup", instRequest),
                fetchPost("getInstitutionClassesByClassGroup", instRequest2),
            ]);

            populateSubjects(subjects);
            populateClasses(classes);

            window.ttState.selectedGroup = groupName;
            hideSplash();

        } catch (err) {
            hideSplash();
            console.error("[_timetable] ttFetchByGroup failed:", err);
        }
    };

    function populateSubjects(data) {
        if (!Array.isArray(data)) return;
        window.ttData.subjects = data.map(function (d) {
            return {id: d.id, name: d.name || d.subjectName || "", _raw: d};
        });
        // Seed state — user can add extras manually from the UI
        window.ttState.subjects = window.ttData.subjects.slice();
    }

    function populateClasses(data) {
        if (!Array.isArray(data)) return;
        window.ttData.classes = data.map(function (d) {
            return {id: d.id, name: d.name || d.className || "", _raw: d};
        });
    }

    // ─── 3. TEACHERS ─────────────────────────────────────────────────────────
    // Teachers are loaded from the subjects fetch (getInstitutionSubjectsAndClassGroup
    // returns teacher info) or added manually via the UI.
    // No separate teacher endpoint — add via UI or extend ttFetchByGroup if your
    // backend exposes a teacher list endpoint later.
    window.ttFetchInitial = async function () {
        // Nothing to fetch on load beyond groups (handled by fetchLookup).
        // Kept for compatibility with timetable.js which calls it.
        window.ttData._loaded = true;
    };

    // ─── TIMETABLE INITIALISATION ─────────────────────────────────────────────
    // Creates empty slots for every class × day × period
    window.ttInitTimetable = function () {
        var classes = window.ttData.classes;
        var days = window.TT_DAYS;
        var periods = window.ttState.periods;

        classes.forEach(function (cls) {
            if (!window.ttState.timetables[cls.name]) {
                window.ttState.timetables[cls.name] = {};
                days.forEach(function (day) {
                    window.ttState.timetables[cls.name][day] =
                        periods.map(function () {
                            return null;
                        });
                });
            }
        });
    };

    // ─── AUTO-GENERATE TIMETABLE ──────────────────────────────────────────────
    /**
     * Simple constraint-aware scheduler:
     *  - No teacher assigned to two classes at the same time
     *  - Each subject appears roughly evenly across the week
     *  - Breaks are skipped
     */
    window.ttAutoGenerate = function () {
        var classes = window.ttData.classes;
        var subjects = window.ttState.subjects;
        var teachers = window.ttState.teachers;
        var days = window.TT_DAYS;
        var periods = window.ttState.periods;

        if (!classes.length || !subjects.length) return false;

        // Reset timetables
        window.ttState.timetables = {};
        window.ttInitTimetable();

        // Track teacher occupancy: teacherName → { "Monday-0": true }
        var teacherBusy = {};
        teachers.forEach(function (t) {
            teacherBusy[t.name] = {};
        });

        // Rotate subjects per class
        classes.forEach(function (cls) {
            var subjectQueue = shuffleArray(subjects.slice());
            var si = 0;

            days.forEach(function (day) {
                periods.forEach(function (period, pi) {
                    if (period.isBreak) return;

                    var subject = subjectQueue[si % subjectQueue.length];
                    si++;

                    // Find an available teacher for this subject at this slot
                    var slotKey = day + "-" + pi;
                    var teacher = teachers.find(function (t) {
                        return !teacherBusy[t.name][slotKey];
                    });

                    var entry = {
                        subject: subject.name,
                        teacher: teacher ? teacher.name : "TBA",
                        subjectId: subject.id,
                        teacherId: teacher ? teacher.id : null,
                    };

                    window.ttState.timetables[cls.name][day][pi] = entry;

                    if (teacher) teacherBusy[teacher.name][slotKey] = true;
                });
            });
        });

        return true;
    };

    function shuffleArray(arr) {
        for (var i = arr.length - 1; i > 0; i--) {
            var j = Math.floor(Math.random() * (i + 1));
            var tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        return arr;
    }

    // ─── ASSIGN A PERIOD MANUALLY ─────────────────────────────────────────────
    window.ttAssignPeriod = function (className, day, periodIndex, subjectName, teacherName) {
        if (!window.ttState.timetables[className]) return;
        window.ttState.timetables[className][day][periodIndex] = {
            subject: subjectName,
            teacher: teacherName || "TBA",
        };
    };

    // ─── CLEAR A PERIOD ───────────────────────────────────────────────────────
    window.ttClearPeriod = function (className, day, periodIndex) {
        if (!window.ttState.timetables[className]) return;
        window.ttState.timetables[className][day][periodIndex] = null;
    };

    // ─── SWAP TWO PERIODS (drag & drop) ───────────────────────────────────────
    window.ttSwapPeriods = function (src, dst) {
        // src/dst: { className, day, periodIndex }
        var timetables = window.ttState.timetables;
        if (!timetables[src.className] || !timetables[dst.className]) return;

        var a = timetables[src.className][src.day][src.periodIndex];
        var b = timetables[dst.className][dst.day][dst.periodIndex];

        timetables[src.className][src.day][src.periodIndex] = b;
        timetables[dst.className][dst.day][dst.periodIndex] = a;
    };

    // ─── SAVE TIMETABLE ───────────────────────────────────────────────────────
    window.ttSave = async function () {
        try {
            showSplash();
            var payload = {
                institutionCode: _instCode,
                classGroup: window.ttState.selectedGroup,
                timetables: window.ttState.timetables,
                settings: {
                    startTime: window.ttState.startTime,
                    endTime: window.ttState.endTime,
                    periodDuration: window.ttState.periodDuration,
                    breaks: window.ttState.breaks,
                },
            };
            var result = await fetchPost("saveTimetable", payload);
            hideSplash();
            return result;
        } catch (err) {
            hideSplash();
            console.error("[_timetable] ttSave failed:", err);
            throw err;
        }
    };

    // ─── LOAD SAVED TIMETABLE ────────────────────────────────────────────────
    window.ttLoad = async function (classGroup) {
        try {
            showSplash();
            var result = await fetchPost("getTimetable", {
                institutionCode: _instCode,
                classGroup: classGroup,
            });

            if (result && result.timetables) {
                window.ttState.timetables = result.timetables;
            }
            if (result && result.settings) {
                var s = result.settings;
                window.ttState.startTime = s.startTime || window.ttState.startTime;
                window.ttState.endTime = s.endTime || window.ttState.endTime;
                window.ttState.periodDuration = s.periodDuration || window.ttState.periodDuration;
                window.ttState.breaks = s.breaks || window.ttState.breaks;
                window.computePeriods(
                    window.ttState.startTime,
                    window.ttState.endTime,
                    window.ttState.periodDuration,
                    window.ttState.breaks
                );
            }

            hideSplash();
            return result;
        } catch (err) {
            hideSplash();
            console.error("[_timetable] ttLoad failed:", err);
            return null;
        }
    };

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    // Get timetable rows for a specific class
    window.ttGetClassTimetable = function (className) {
        return window.ttState.timetables[className] || null;
    };

    // Get all slots for a specific teacher across all classes
    window.ttGetTeacherTimetable = function (teacherName) {
        var result = {};
        var days = window.TT_DAYS;
        var periods = window.ttState.periods;
        var tables = window.ttState.timetables;

        days.forEach(function (day) {
            result[day] = periods.map(function (period, pi) {
                if (period.isBreak) return {isBreak: true, breakName: period.breakName};
                // Search all classes for this teacher at this slot
                var found = null;
                Object.keys(tables).forEach(function (cls) {
                    var entry = tables[cls][day] && tables[cls][day][pi];
                    if (entry && entry.teacher === teacherName) {
                        found = {subject: entry.subject, className: cls};
                    }
                });
                return found;
            });
        });

        return result;
    };

    // ─── EXPORT HELPERS ───────────────────────────────────────────────────────

    // Build a 2D array for Excel export (class view)
    window.ttBuildExcelData = function (className) {
        var days = window.TT_DAYS;
        var periods = window.ttState.periods;
        var table = window.ttState.timetables[className];
        if (!table) return [];

        var rows = [];
        // Header row
        var header = ["Period / Time"].concat(days);
        rows.push(header);

        periods.forEach(function (period, pi) {
            var row = [period.label + " (" + period.start + "-" + period.end + ")"];
            days.forEach(function (day) {
                if (period.isBreak) {
                    row.push(period.breakName);
                } else {
                    var entry = table[day] && table[day][pi];
                    row.push(entry ? entry.subject + "\n" + (entry.teacher || "") : "");
                }
            });
            rows.push(row);
        });

        return rows;
    };

    // ─── KICK OFF ─────────────────────────────────────────────────────────────
    if (window.copyrights) window.copyrights();

    // Academic years generated locally (no API call needed)
    window.ttAcademicYears = (function () {
        var y = new Date().getFullYear();
        var out = [];
        for (var i = 4; i >= 0; i--) {
            out.push((y - i) + "/" + (y - i + 1));
        }
        return out;
    })();

    // fetchLookup sets v and loads class groups — same as all other modules.
    fetchLookup(instId.split(",")[0]);

})();