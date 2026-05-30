/**
 * timetable.js  —  UI builder for the Class Timetable Scheduler.
 *
 * Loads first (registered by common.js / host page).
 * Dynamically injects timetable.css then _timetable.js at runtime.
 * Waits for ttData._loaded before rendering.
 *
 * Three tabs:
 *   1. Setup      — settings, breaks, subjects, teachers, classes
 *   2. Class View — timetable grid per class (drag & drop + edit)
 *   3. Teacher View — teacher schedule grid
 */

(function () {
    "use strict";

    // ─── Resolve sibling file base path ──────────────────────────────────────
    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName("script");
            for (var i = tags.length - 1; i >= 0; i--) {
                if (tags[i].src && tags[i].src.indexOf("timetable") !== -1 &&
                    tags[i].src.indexOf("_timetable") === -1) return tags[i];
            }
            return null;
        })();
        if (!el || !el.src) return "";
        return el.src.substring(0, el.src.lastIndexOf("/") + 1);
    })();

    // ─── Inject CSS ───────────────────────────────────────────────────────────
    if (!document.getElementById("ttCSS")) {
        var link = document.createElement("link");
        link.id = "ttCSS";
        link.rel = "stylesheet";
        // timetable.css lives in scripts/ (one level up from scripts/subscripts/)
        link.href = _base + "../timetable.css";
        document.head.appendChild(link);
    }

    // ─── Inject Font Awesome if needed ────────────────────────────────────────
    if (!document.querySelector('link[href*="font-awesome"], link[href*="fontawesome"]')) {
        var fa = document.createElement("link");
        fa.rel = "stylesheet";
        fa.href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css";
        document.head.appendChild(fa);
    }

    // ─── BUILD DOM ────────────────────────────────────────────────────────────
    function buildDOM() {
        document.getElementById("wrapper").innerHTML = '<div class="tt-page">' + [

            // HEADER
            '<header class="tt-header">',
            '<div class="tt-header-row">',
            '<div>',
            '<h1><i class="fas fa-calendar-alt"></i> Class Timetable Scheduler</h1>',
            '<p>Auto-generate and manually adjust class timetables</p>',
            '</div>',
            '<div class="tt-header-actions">',
            '<button class="tt-btn tt-btn-secondary" id="ttLoadBtn"><i class="fas fa-download"></i> Load Saved</button>',
            '<button class="tt-btn tt-btn-success"   id="ttSaveBtn"><i class="fas fa-save"></i> Save Timetable</button>',
            '<button class="tt-btn tt-btn-primary"   id="ttExportPdfBtn"><i class="fas fa-file-pdf"></i> Export PDF</button>',
            '<button class="tt-btn tt-btn-warning"   id="ttExportXlsBtn"><i class="fas fa-file-excel"></i> Export Excel</button>',
            '</div>',
            '</div>',
            '</header>',

            // BODY
            '<div class="tt-body">',

            // TABS
            '<div class="tt-tabs">',
            '<button class="tt-tab active" data-tab="setup"><i class="fas fa-cog"></i> Setup</button>',
            '<button class="tt-tab" data-tab="class-view"><i class="fas fa-chalkboard"></i> Class View</button>',
            '<button class="tt-tab" data-tab="teacher-view"><i class="fas fa-user-tie"></i> Teacher View</button>',
            '</div>',

            // ── TAB: SETUP ────────────────────────────────────────────────
            '<div id="tab-setup" class="tt-tab-content">',

            // Class group selector
            '<div class="tt-panel">',
            '<div class="tt-panel-head"><span><i class="fas fa-layer-group"></i>Class Group</span></div>',
            '<div class="tt-panel-body">',
            '<div class="tt-settings-grid" style="max-width:400px">',
            '<div class="tt-field">',
            '<label>Select Class Group</label>',
            '<select id="ttGroupSelect"><option value="">Loading…</option></select>',
            '</div>',
            '</div>',
            '</div>',
            '</div>',

            // Timing settings
            '<div class="tt-panel">',
            '<div class="tt-panel-head"><span><i class="fas fa-clock"></i>Timing Settings</span></div>',
            '<div class="tt-panel-body">',
            '<div class="tt-settings-grid">',
            '<div class="tt-field">',
            '<label>School Start Time</label>',
            '<input type="time" id="ttStartTime" value="07:30">',
            '</div>',
            '<div class="tt-field">',
            '<label>School End Time</label>',
            '<input type="time" id="ttEndTime" value="15:00">',
            '</div>',
            '<div class="tt-field">',
            '<label>Period Duration (minutes)</label>',
            '<input type="number" id="ttPeriodDur" value="40" min="10" max="120">',
            '</div>',
            '</div>',

            // Breaks
            '<div style="margin-top:20px">',
            '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px">',
            '<strong style="font-size:.9rem">Breaks</strong>',
            '<button class="tt-btn tt-btn-secondary tt-btn-sm" id="ttAddBreakBtn"><i class="fas fa-plus"></i> Add Break</button>',
            '</div>',
            '<div class="tt-breaks-list" id="ttBreaksList"></div>',
            '</div>',

            // Computed output
            '<div style="margin-top:16px">',
            '<button class="tt-btn tt-btn-primary tt-btn-sm" id="ttComputeBtn"><i class="fas fa-calculator"></i> Compute Periods</button>',
            '</div>',
            '<div class="tt-computed" id="ttComputed" style="display:none;margin-top:12px">',
            '<div class="tt-computed-item"><span class="val" id="cmpPeriods">0</span><span class="lbl">Teaching Periods/Day</span></div>',
            '<div class="tt-computed-item"><span class="val" id="cmpBreaks">0</span><span class="lbl">Breaks</span></div>',
            '<div class="tt-computed-item"><span class="val" id="cmpFirst">—</span><span class="lbl">First Period</span></div>',
            '<div class="tt-computed-item"><span class="val" id="cmpLast">—</span><span class="lbl">Last Period Ends</span></div>',
            '</div>',
            '</div>',
            '</div>',

            // Subjects & Teachers
            '<div class="tt-panel">',
            '<div class="tt-panel-head"><span><i class="fas fa-book"></i>Subjects &amp; Teachers</span></div>',
            '<div class="tt-panel-body">',
            '<div class="tt-two-col">',

            // Subjects
            '<div>',
            '<strong style="font-size:.9rem;display:block;margin-bottom:10px">Subjects</strong>',
            '<div class="tt-item-list" id="ttSubjectList"></div>',
            '<div class="tt-add-row">',
            '<input type="text" id="ttNewSubject" placeholder="Add subject…">',
            '<button class="tt-btn tt-btn-secondary tt-btn-sm" id="ttAddSubjectBtn"><i class="fas fa-plus"></i></button>',
            '</div>',
            '</div>',

            // Teachers
            '<div>',
            '<strong style="font-size:.9rem;display:block;margin-bottom:10px">Teachers</strong>',
            '<div class="tt-item-list" id="ttTeacherList"></div>',
            '<div class="tt-add-row">',
            '<input type="text" id="ttNewTeacher" placeholder="Add teacher…">',
            '<button class="tt-btn tt-btn-secondary tt-btn-sm" id="ttAddTeacherBtn"><i class="fas fa-plus"></i></button>',
            '</div>',
            '</div>',
            '</div>',
            '</div>',
            '</div>',

            // Generate button
            '<div style="display:flex;gap:12px;flex-wrap:wrap">',
            '<button class="tt-btn tt-btn-primary" id="ttGenerateBtn"><i class="fas fa-magic"></i> Auto-Generate Timetable</button>',
            '</div>',

            '</div>', // end tab-setup

            // ── TAB: CLASS VIEW ───────────────────────────────────────────
            '<div id="tab-class-view" class="tt-tab-content" style="display:none">',
            '<div class="tt-panel">',
            '<div class="tt-panel-head">',
            '<span><i class="fas fa-chalkboard"></i>Class Timetable</span>',
            '</div>',
            '<div class="tt-panel-body">',
            '<div class="tt-view-bar" style="margin-bottom:18px">',
            '<div class="tt-field">',
            '<label>Select Class</label>',
            '<select id="ttClassSelect"><option value="">Select class…</option></select>',
            '</div>',
            '</div>',
            '<div class="tt-grid-wrapper" id="ttClassGrid">',
            '<div class="tt-empty"><i class="fas fa-calendar-alt"></i>Generate or load a timetable to view</div>',
            '</div>',
            '</div>',
            '</div>',
            '</div>', // end tab-class-view

            // ── TAB: TEACHER VIEW ─────────────────────────────────────────
            '<div id="tab-teacher-view" class="tt-tab-content" style="display:none">',
            '<div class="tt-panel">',
            '<div class="tt-panel-head">',
            '<span><i class="fas fa-user-tie"></i>Teacher Schedule</span>',
            '</div>',
            '<div class="tt-panel-body">',
            '<div class="tt-view-bar" style="margin-bottom:18px">',
            '<div class="tt-field">',
            '<label>Select Teacher</label>',
            '<select id="ttTeacherSelect"><option value="">Select teacher…</option></select>',
            '</div>',
            '</div>',
            '<div class="tt-grid-wrapper" id="ttTeacherGrid">',
            '<div class="tt-empty"><i class="fas fa-user-tie"></i>Select a teacher to view their schedule</div>',
            '</div>',
            '</div>',
            '</div>',
            '</div>', // end tab-teacher-view

            '</div>', // end tt-body

            // FOOTER
            '<footer class="tt-footer">',
            '<i class="far fa-copyright"></i> Astromy LLC 2013–<span id="ttYear"></span>',
            ' &nbsp;|&nbsp; Timetable Scheduler v1.0',
            '</footer>',

            // ASSIGN MODAL
            '<div class="tt-modal-overlay" id="ttAssignModal">',
            '<div class="tt-modal">',
            '<div class="tt-modal-header">',
            '<h5><i class="fas fa-pencil-alt"></i> Assign Period</h5>',
            '<button class="tt-modal-close" id="ttModalClose">&times;</button>',
            '</div>',
            '<div class="tt-modal-body">',
            '<div class="tt-field">',
            '<label>Subject</label>',
            '<select id="ttModalSubject"><option value="">Select subject…</option></select>',
            '</div>',
            '<div class="tt-field">',
            '<label>Teacher</label>',
            '<select id="ttModalTeacher"><option value="">Select teacher…</option></select>',
            '</div>',
            '<div id="ttModalInfo" style="font-size:.85rem;color:var(--muted)"></div>',
            '</div>',
            '<div class="tt-modal-footer">',
            '<button class="tt-btn tt-btn-secondary tt-btn-sm" id="ttModalCancel">Cancel</button>',
            '<button class="tt-btn tt-btn-danger tt-btn-sm" id="ttModalClear"><i class="fas fa-trash"></i> Clear</button>',
            '<button class="btn btn-primary   btn-sm" id="ttModalSave"><i class="fas fa-check"></i> Assign</button>',
            '</div>',
            '</div>',
            '</div>',

            // TOAST
            '<div class="tt-toast" id="ttToast"></div>',

        ].join("") + '</div>';

        document.getElementById("ttYear").textContent = new Date().getFullYear();
    }

    // ─── RENDER: BREAKS LIST ──────────────────────────────────────────────────
    function renderBreaks() {
        var breaks = window.ttState.breaks;
        var html = "";
        breaks.forEach(function (b, i) {
            html +=
                '<div class="tt-break-row" data-break-index="' + i + '">' +
                '<span class="lbl">After period</span>' +
                '<input type="number" class="break-after" value="' + b.after + '" min="1" max="20" style="width:60px">' +
                '<span class="lbl">Name</span>' +
                '<input type="text" class="break-name" value="' + b.name + '" placeholder="Break name">' +
                '<span class="lbl">Duration (min)</span>' +
                '<input type="number" class="break-dur" value="' + b.duration + '" min="5" max="120" style="width:65px">' +
                '<button class="tt-break-remove" data-idx="' + i + '"><i class="fas fa-times"></i></button>' +
                '</div>';
        });
        if (!html) html = '<div style="color:var(--muted);font-size:.88rem;padding:8px 0">No breaks added.</div>';
        document.getElementById("ttBreaksList").innerHTML = html;

        // Wire remove buttons
        document.querySelectorAll(".tt-break-remove").forEach(function (btn) {
            btn.addEventListener("click", function () {
                var idx = parseInt(this.getAttribute("data-idx"), 10);
                window.ttState.breaks.splice(idx, 1);
                renderBreaks();
            });
        });

        // Wire live input sync
        document.querySelectorAll(".tt-break-row").forEach(function (row) {
            var idx = parseInt(row.getAttribute("data-break-index"), 10);
            row.querySelector(".break-after").addEventListener("change", function () {
                window.ttState.breaks[idx].after = parseInt(this.value, 10);
            });
            row.querySelector(".break-name").addEventListener("change", function () {
                window.ttState.breaks[idx].name = this.value;
            });
            row.querySelector(".break-dur").addEventListener("change", function () {
                window.ttState.breaks[idx].duration = parseInt(this.value, 10);
            });
        });
    }

    // ─── RENDER: SUBJECT LIST ─────────────────────────────────────────────────
    function renderSubjects() {
        var subjects = window.ttState.subjects || [];
        var html = subjects.map(function (s, i) {
            return '<div class="tt-item">' +
                '<div><div class="tt-item-name">' + s.name + '</div></div>' +
                '<button class="tt-item-remove" data-type="subject" data-idx="' + i + '"><i class="fas fa-times"></i></button>' +
                '</div>';
        }).join("") || '<div style="color:var(--muted);font-size:.88rem">No subjects loaded.</div>';

        document.getElementById("ttSubjectList").innerHTML = html;
        document.querySelectorAll('[data-type="subject"].tt-item-remove').forEach(function (btn) {
            btn.addEventListener("click", function () {
                window.ttState.subjects.splice(parseInt(this.getAttribute("data-idx"), 10), 1);
                renderSubjects();
            });
        });
    }

    // ─── RENDER: TEACHER LIST ─────────────────────────────────────────────────
    function renderTeachers() {
        var teachers = window.ttState.teachers || [];
        var html = teachers.map(function (t, i) {
            return '<div class="tt-item">' +
                '<div><div class="tt-item-name">' + t.name + '</div></div>' +
                '<button class="tt-item-remove" data-type="teacher" data-idx="' + i + '"><i class="fas fa-times"></i></button>' +
                '</div>';
        }).join("") || '<div style="color:var(--muted);font-size:.88rem">No teachers loaded.</div>';

        document.getElementById("ttTeacherList").innerHTML = html;
        document.querySelectorAll('[data-type="teacher"].tt-item-remove').forEach(function (btn) {
            btn.addEventListener("click", function () {
                window.ttState.teachers.splice(parseInt(this.getAttribute("data-idx"), 10), 1);
                renderTeachers();
            });
        });
    }

    // ─── RENDER: CLASS TIMETABLE GRID ─────────────────────────────────────────
    function renderClassGrid(className) {
        var container = document.getElementById("ttClassGrid");
        if (!className) {
            container.innerHTML = '<div class="tt-empty"><i class="fas fa-calendar-alt"></i>Select a class to view its timetable</div>';
            return;
        }
        var table = window.ttGetClassTimetable(className);
        if (!table) {
            container.innerHTML = '<div class="tt-empty"><i class="fas fa-magic"></i>No timetable generated yet. Use Auto-Generate in Setup.</div>';
            return;
        }

        var days = window.TT_DAYS;
        var periods = window.ttState.periods;

        var html = '<table class="tt-grid"><thead><tr>' +
            '<th class="day-head">Period</th>' +
            days.map(function (d) {
                return '<th>' + d + '</th>';
            }).join("") +
            '</tr></thead><tbody>';

        periods.forEach(function (period, pi) {
            html += '<tr>';
            html += '<td class="tt-period-label">' +
                '<div>' + period.label + '</div>' +
                '<div style="font-size:.72rem;color:var(--muted)">' + period.start + ' – ' + period.end + '</div>' +
                '</td>';

            days.forEach(function (day) {
                if (period.isBreak) {
                    html += '<td><div class="tt-cell break-cell">' +
                        '<span class="cell-break"><i class="fas fa-coffee"></i> ' + period.breakName + '</span>' +
                        '</div></td>';
                } else {
                    var entry = table[day] && table[day][pi];
                    var filled = entry ? "filled" : "";
                    html += '<td>' +
                        '<div class="tt-cell ' + filled + '" ' +
                        'draggable="' + (entry ? "true" : "false") + '" ' +
                        'data-class="' + className + '" ' +
                        'data-day="' + day + '" ' +
                        'data-period="' + pi + '">' +
                        (entry
                            ? '<span class="cell-subject">' + entry.subject + '</span>' +
                            '<span class="cell-teacher"><i class="fas fa-user"></i> ' + (entry.teacher || "TBA") + '</span>' +
                            '<button class="cell-remove" data-class="' + className + '" data-day="' + day + '" data-period="' + pi + '"><i class="fas fa-times"></i></button>'
                            : '<span style="color:var(--muted);font-size:.78rem">+ Assign</span>') +
                        '</div>' +
                        '</td>';
                }
            });

            html += '</tr>';
        });

        html += '</tbody></table>';
        container.innerHTML = html;

        wireCellEvents(className);
    }

    // ─── RENDER: TEACHER GRID ─────────────────────────────────────────────────
    function renderTeacherGrid(teacherName) {
        var container = document.getElementById("ttTeacherGrid");
        if (!teacherName) {
            container.innerHTML = '<div class="tt-empty"><i class="fas fa-user-tie"></i>Select a teacher</div>';
            return;
        }

        var schedule = window.ttGetTeacherTimetable(teacherName);
        var days = window.TT_DAYS;
        var periods = window.ttState.periods;

        var html = '<table class="tt-grid"><thead><tr>' +
            '<th class="day-head">Period</th>' +
            days.map(function (d) {
                return '<th>' + d + '</th>';
            }).join("") +
            '</tr></thead><tbody>';

        periods.forEach(function (period, pi) {
            html += '<tr><td class="tt-period-label">' +
                '<div>' + period.label + '</div>' +
                '<div style="font-size:.72rem;color:var(--muted)">' + period.start + ' – ' + period.end + '</div>' +
                '</td>';

            days.forEach(function (day) {
                var slot = schedule[day] && schedule[day][pi];
                if (period.isBreak || (slot && slot.isBreak)) {
                    html += '<td><div class="tt-cell break-cell">' +
                        '<span class="cell-break"><i class="fas fa-coffee"></i> ' + period.breakName + '</span>' +
                        '</div></td>';
                } else {
                    var filled = slot ? "filled" : "";
                    html += '<td><div class="tt-cell ' + filled + '">' +
                        (slot
                            ? '<span class="cell-subject">' + slot.subject + '</span>' +
                            '<span class="cell-teacher"><i class="fas fa-chalkboard"></i> ' + slot.className + '</span>'
                            : '<span style="color:var(--muted);font-size:.78rem">Free</span>') +
                        '</div></td>';
                }
            });

            html += '</tr>';
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    }

    // ─── CELL EVENTS (click to assign, drag to swap, remove) ─────────────────
    var _pendingCell = null;

    function wireCellEvents(className) {
        // Click → open assign modal
        document.querySelectorAll(".tt-cell:not(.break-cell)").forEach(function (cell) {
            cell.addEventListener("click", function (e) {
                if (e.target.closest(".cell-remove")) return;
                _pendingCell = {
                    className: this.getAttribute("data-class"),
                    day: this.getAttribute("data-day"),
                    periodIndex: parseInt(this.getAttribute("data-period"), 10),
                };
                openAssignModal(_pendingCell);
            });
        });

        // Remove button
        document.querySelectorAll(".cell-remove").forEach(function (btn) {
            btn.addEventListener("click", function (e) {
                e.stopPropagation();
                var cls = this.getAttribute("data-class");
                var day = this.getAttribute("data-day");
                var pi = parseInt(this.getAttribute("data-period"), 10);
                window.ttClearPeriod(cls, day, pi);
                renderClassGrid(className);
                toast("Period cleared.");
            });
        });

        // Drag & drop
        document.querySelectorAll(".tt-cell[draggable='true']").forEach(function (cell) {
            cell.addEventListener("dragstart", function () {
                window.ttState._drag = {
                    className: this.getAttribute("data-class"),
                    day: this.getAttribute("data-day"),
                    periodIndex: parseInt(this.getAttribute("data-period"), 10),
                };
                this.style.opacity = ".5";
            });
            cell.addEventListener("dragend", function () {
                this.style.opacity = "";
            });
        });

        document.querySelectorAll(".tt-cell:not(.break-cell)").forEach(function (cell) {
            cell.addEventListener("dragover", function (e) {
                e.preventDefault();
                this.classList.add("drag-over");
            });
            cell.addEventListener("dragleave", function () {
                this.classList.remove("drag-over");
            });
            cell.addEventListener("drop", function (e) {
                e.preventDefault();
                this.classList.remove("drag-over");
                var dst = {
                    className: this.getAttribute("data-class"),
                    day: this.getAttribute("data-day"),
                    periodIndex: parseInt(this.getAttribute("data-period"), 10),
                };
                if (window.ttState._drag) {
                    window.ttSwapPeriods(window.ttState._drag, dst);
                    window.ttState._drag = null;
                    renderClassGrid(className);
                    toast("Periods swapped.");
                }
            });
        });
    }

    // ─── ASSIGN MODAL ─────────────────────────────────────────────────────────
    function openAssignModal(cell) {
        var subjects = window.ttState.subjects || [];
        var teachers = window.ttState.teachers || [];

        // Populate selects
        var subOpts = '<option value="">Select subject…</option>' +
            subjects.map(function (s) {
                return '<option>' + s.name + '</option>';
            }).join("");
        var tOpts = '<option value="">Select teacher…</option>' +
            teachers.map(function (t) {
                return '<option>' + t.name + '</option>';
            }).join("");

        document.getElementById("ttModalSubject").innerHTML = subOpts;
        document.getElementById("ttModalTeacher").innerHTML = tOpts;

        // Pre-fill if already assigned
        var entry = window.ttState.timetables[cell.className] &&
            window.ttState.timetables[cell.className][cell.day] &&
            window.ttState.timetables[cell.className][cell.day][cell.periodIndex];
        if (entry) {
            document.getElementById("ttModalSubject").value = entry.subject || "";
            document.getElementById("ttModalTeacher").value = entry.teacher || "";
        }

        var period = window.ttState.periods[cell.periodIndex];
        document.getElementById("ttModalInfo").textContent =
            cell.className + " — " + cell.day + " — " +
            (period ? period.label + " (" + period.start + "–" + period.end + ")" : "");

        document.getElementById("ttAssignModal").classList.add("open");
    }

    function closeModal() {
        document.getElementById("ttAssignModal").classList.remove("open");
        _pendingCell = null;
    }

    // ─── REPOPULATE SELECTS ───────────────────────────────────────────────────
    function repopulateGroupSelect() {
        var sel = document.getElementById("ttGroupSelect");
        var groups = window.ttData.classGroups || [];
        sel.innerHTML = '<option value="">Select group…</option>' +
            groups.map(function (g) {
                return '<option value="' + g.value + '">' + g.name + '</option>';
            }).join("");
    }

    function repopulateClassSelect() {
        var sel = document.getElementById("ttClassSelect");
        var classes = window.ttData.classes || [];
        sel.innerHTML = '<option value="">Select class…</option>' +
            classes.map(function (c) {
                return '<option value="' + c.name + '">' + c.name + '</option>';
            }).join("");
    }

    function repopulateTeacherSelect() {
        var sel = document.getElementById("ttTeacherSelect");
        var teachers = window.ttState.teachers || [];
        sel.innerHTML = '<option value="">Select teacher…</option>' +
            teachers.map(function (t) {
                return '<option value="' + t.name + '">' + t.name + '</option>';
            }).join("");
    }

    // ─── EXPORT PDF ───────────────────────────────────────────────────────────
    function exportPDF() {
        var className = document.getElementById("ttClassSelect").value;
        if (!className) {
            toast("Select a class first.");
            return;
        }

        if (!window.jspdf) {
            toast("jsPDF not loaded.");
            return;
        }

        var {jsPDF} = window.jspdf;
        var doc = new jsPDF("landscape");
        var days = window.TT_DAYS;
        var periods = window.ttState.periods;
        var table = window.ttGetClassTimetable(className);

        doc.setFontSize(14);
        doc.setFont("helvetica", "bold");
        doc.text(className + " — Timetable", 14, 16);
        doc.setFontSize(9);
        doc.setFont("helvetica", "normal");
        doc.text("Generated: " + new Date().toLocaleDateString(), 14, 22);

        var head = [["Period / Time"].concat(days)];
        var body = periods.map(function (period, pi) {
            var row = [period.label + "\n" + period.start + "–" + period.end];
            days.forEach(function (day) {
                if (period.isBreak) {
                    row.push(period.breakName);
                } else {
                    var entry = table && table[day] && table[day][pi];
                    row.push(entry ? entry.subject + "\n" + (entry.teacher || "") : "");
                }
            });
            return row;
        });

        doc.autoTable({
            head: head, body: body, startY: 28,
            styles: {fontSize: 8, cellPadding: 4},
            headStyles: {fillColor: [47, 84, 212]},
            alternateRowStyles: {fillColor: [238, 241, 255]},
        });

        doc.save(className + "_Timetable.pdf");
        toast("PDF exported.");
    }

    // ─── EXPORT EXCEL ─────────────────────────────────────────────────────────
    function exportExcel() {
        var className = document.getElementById("ttClassSelect").value;
        if (!className) {
            toast("Select a class first.");
            return;
        }
        if (typeof XLSX === "undefined") {
            toast("XLSX library not loaded.");
            return;
        }

        var rows = window.ttBuildExcelData(className);
        var ws = XLSX.utils.aoa_to_sheet(rows);
        var wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, className.substring(0, 31));
        XLSX.writeFile(wb, className + "_Timetable.xlsx");
        toast("Excel exported.");
    }

    // ─── TOAST ────────────────────────────────────────────────────────────────
    function toast(msg) {
        var el = document.getElementById("ttToast");
        if (!el) return;
        el.textContent = msg;
        el.classList.add("show");
        setTimeout(function () {
            el.classList.remove("show");
        }, 3000);
    }

    // ─── WIRE ALL EVENTS ──────────────────────────────────────────────────────
    function wireEvents() {

        // Tabs
        document.querySelectorAll(".tt-tab").forEach(function (tab) {
            tab.addEventListener("click", function () {
                document.querySelectorAll(".tt-tab").forEach(function (t) {
                    t.classList.remove("active");
                });
                document.querySelectorAll(".tt-tab-content").forEach(function (c) {
                    c.style.display = "none";
                });
                this.classList.add("active");
                var target = this.getAttribute("data-tab");
                document.getElementById("tab-" + target).style.display = "block";
                window.ttState.activeTab = target;
            });
        });

        // Group select → fetch classes + subjects
        document.getElementById("ttGroupSelect").addEventListener("change", function () {
            var group = this.value;
            if (!group) return;
            window.ttFetchByGroup(group).then(function () {
                renderSubjects();
                renderTeachers();
                repopulateClassSelect();
                repopulateTeacherSelect();
                toast("Classes and subjects loaded.");
            });
        });

        // Add break
        document.getElementById("ttAddBreakBtn").addEventListener("click", function () {
            window.ttState.breaks.push({name: "Break", after: 3, duration: 15});
            renderBreaks();
        });

        // Compute periods
        document.getElementById("ttComputeBtn").addEventListener("click", function () {
            window.ttState.startTime = document.getElementById("ttStartTime").value;
            window.ttState.endTime = document.getElementById("ttEndTime").value;
            window.ttState.periodDuration = parseInt(document.getElementById("ttPeriodDur").value, 10);

            var periods = window.computePeriods(
                window.ttState.startTime,
                window.ttState.endTime,
                window.ttState.periodDuration,
                window.ttState.breaks
            );

            var teaching = periods.filter(function (p) {
                return !p.isBreak;
            });
            var breaks = periods.filter(function (p) {
                return p.isBreak;
            });

            document.getElementById("cmpPeriods").textContent = teaching.length;
            document.getElementById("cmpBreaks").textContent = breaks.length;
            document.getElementById("cmpFirst").textContent = teaching.length ? teaching[0].start : "—";
            document.getElementById("cmpLast").textContent = teaching.length ? teaching[teaching.length - 1].end : "—";
            document.getElementById("ttComputed").style.display = "flex";

            toast("Periods computed: " + teaching.length + " teaching, " + breaks.length + " breaks.");
        });

        // Add subject
        document.getElementById("ttAddSubjectBtn").addEventListener("click", function () {
            var val = document.getElementById("ttNewSubject").value.trim();
            if (!val) return;
            window.ttState.subjects.push({id: null, name: val});
            document.getElementById("ttNewSubject").value = "";
            renderSubjects();
        });
        document.getElementById("ttNewSubject").addEventListener("keydown", function (e) {
            if (e.key === "Enter") document.getElementById("ttAddSubjectBtn").click();
        });

        // Add teacher
        document.getElementById("ttAddTeacherBtn").addEventListener("click", function () {
            var val = document.getElementById("ttNewTeacher").value.trim();
            if (!val) return;
            window.ttState.teachers.push({id: null, name: val});
            document.getElementById("ttNewTeacher").value = "";
            renderTeachers();
            repopulateTeacherSelect();
        });
        document.getElementById("ttNewTeacher").addEventListener("keydown", function (e) {
            if (e.key === "Enter") document.getElementById("ttAddTeacherBtn").click();
        });

        // Auto-generate
        document.getElementById("ttGenerateBtn").addEventListener("click", function () {
            if (!window.ttState.periods.length) {
                toast("Compute periods first.");
                return;
            }
            if (!window.ttData.classes.length) {
                toast("Select a class group first.");
                return;
            }
            if (!window.ttState.subjects.length) {
                toast("Add at least one subject.");
                return;
            }
            window.ttInitTimetable();
            var ok = window.ttAutoGenerate();
            if (ok) {
                repopulateClassSelect();
                toast("Timetable generated! Switch to Class View.");
            } else {
                toast("Generation failed — check subjects and classes.");
            }
        });

        // Class view select
        document.getElementById("ttClassSelect").addEventListener("change", function () {
            window.ttState.selectedClass = this.value;
            renderClassGrid(this.value);
        });

        // Teacher view select
        document.getElementById("ttTeacherSelect").addEventListener("change", function () {
            window.ttState.selectedTeacher = this.value;
            renderTeacherGrid(this.value);
        });

        // Save
        document.getElementById("ttSaveBtn").addEventListener("click", function () {
            window.ttSave().then(function () {
                if (typeof swal === "function") {
                    swal({title: "Saved!", text: "Timetable saved successfully.", type: "success"});
                } else {
                    toast("Timetable saved.");
                }
            }).catch(function () {
                toast("Save failed. Check console.");
            });
        });

        // Load saved
        document.getElementById("ttLoadBtn").addEventListener("click", function () {
            var group = document.getElementById("ttGroupSelect").value;
            if (!group) {
                toast("Select a class group first.");
                return;
            }
            window.ttLoad(group).then(function (result) {
                if (result) {
                    repopulateClassSelect();
                    renderClassGrid(window.ttState.selectedClass);
                    renderBreaks();
                    toast("Timetable loaded.");
                } else {
                    toast("No saved timetable found for this group.");
                }
            });
        });

        // Export
        document.getElementById("ttExportPdfBtn").addEventListener("click", exportPDF);
        document.getElementById("ttExportXlsBtn").addEventListener("click", exportExcel);

        // Modal
        document.getElementById("ttModalClose").addEventListener("click", closeModal);
        document.getElementById("ttModalCancel").addEventListener("click", closeModal);

        document.getElementById("ttModalClear").addEventListener("click", function () {
            if (!_pendingCell) return;
            window.ttClearPeriod(_pendingCell.className, _pendingCell.day, _pendingCell.periodIndex);
            renderClassGrid(window.ttState.selectedClass);
            closeModal();
            toast("Period cleared.");
        });

        document.getElementById("ttModalSave").addEventListener("click", function () {
            if (!_pendingCell) return;
            var subject = document.getElementById("ttModalSubject").value;
            var teacher = document.getElementById("ttModalTeacher").value;
            if (!subject) {
                toast("Select a subject.");
                return;
            }
            window.ttAssignPeriod(
                _pendingCell.className,
                _pendingCell.day,
                _pendingCell.periodIndex,
                subject,
                teacher
            );
            renderClassGrid(window.ttState.selectedClass);
            closeModal();
            toast("Period assigned.");
        });

        document.getElementById("ttAssignModal").addEventListener("click", function (e) {
            if (e.target === e.currentTarget) closeModal();
        });
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        renderBreaks();
        renderSubjects();
        renderTeachers();
        waitForData();
    }

    function waitForData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.ttData && window.ttData._loaded) {
                clearInterval(timer);
                repopulateGroupSelect();
                renderTeachers();
                repopulateTeacherSelect();
            } else if (attempts > 200) {
                clearInterval(timer);
                console.warn("[timetable] Data load timeout.");
            }
        }, 30);
    }

    // ─── LOAD LOGIC SCRIPT ────────────────────────────────────────────────────
    function loadLogicScript() {
        if (window.ttData && window.ttState) {
            init();
            return;
        }

        var s = document.createElement("script");
        s.type = "text/javascript";
        // _timetable.js lives in scripts/ (one level up from scripts/subscripts/)
        s.src = _base + "../_timetable.js";

        s.onload = function () {
            if (window.ttData && window.ttState) {
                init();
            } else {
                console.error("[timetable] _timetable.js loaded but ttData missing.");
            }
        };
        s.onerror = function () {
            console.error("[timetable] Could not load: " + _base + "../_timetable.js");
        };

        document.body.appendChild(s);
    }

    // ─── BOOT ─────────────────────────────────────────────────────────────────
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initTimetable = init;

})();