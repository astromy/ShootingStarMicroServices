/**
 * financeBilling.js  —  UI renderer for the Student Billing System.
 *
 * This file loads first (registered by the host page).
 * It dynamically injects financeBilling.css, then loads _financeBilling.js
 * at runtime. Once the logic file has set window.billingData and
 * window.billingState (and the initial API fetches complete), the UI renders.
 *
 * Flow:
 *   1. Inject CSS link
 *   2. Append <script src="scripts/_financeBilling.js">
 *   3. onload → init() → buildDOM() → waitForRealData()
 *   4. waitForRealData polls until classes + bills arrive from API
 *   5. Repopulate dropdowns, render bill list, fetch students for first class
 */

(function () {
    "use strict";

    // ─── INJECT CSS ───────────────────────────────────────────────────────────
    (function injectCSS() {
        if (document.getElementById("financeBillingCSS")) return;
        var link = document.createElement("link");
        link.id = "financeBillingCSS";
        link.rel = "stylesheet";
        // Adjust path to match your project layout
        link.href = "scripts/subscripts/financeBilling.css";
        document.head.appendChild(link);
    })();

    // ─── INJECT FONT AWESOME (if not already present) ─────────────────────────
    if (!document.querySelector('link[href*="font-awesome"], link[href*="fontawesome"]')) {
        var fa = document.createElement("link");
        fa.rel = "stylesheet";
        fa.href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css";
        document.head.appendChild(fa);
    }

    // ─── BUILD DOM ────────────────────────────────────────────────────────────
    function buildDOM() {
        var data = window.billingData;
        var state = window.billingState;

        // Year options
        var yearOpts = (data.academicYears || []).map(function (y) {
            return '<option ' + (y === state.selectedYear ? "selected" : "") + '>' + y + '</option>';
        }).join("");

        // Term options
        var termOpts = (data.terms || []).map(function (t) {
            return '<option value="' + t.value + '" ' +
                (t.value === state.selectedTerm ? "selected" : "") + '>' + t.label + '</option>';
        }).join("");

        document.getElementById("wrapper").innerHTML = [
            // ── HEADER ──────────────────────────────────────────────────────
            '<header class="bs-header">',
            '<div class="bs-header-row">',
            '<div>',
            '<h1><i class="fas fa-receipt"></i> Student Billing System</h1>',
            '<p>Manage class-wide and individual student billing</p>',
            '</div>',
            '<div class="bs-header-actions">',
            '<button class="btn btn-secondary" id="refreshBtn">',
            '<i class="fas fa-sync-alt"></i> Refresh',
            '</button>',
            '<button class="btn btn-success" id="exportBtn">',
            '<i class="fas fa-file-csv"></i> Export CSV',
            '</button>',
            '</div>',
            '</div>',
            '</header>',

            // ── FILTERS ──────────────────────────────────────────────────────
            '<div class="bs-filters">',

            // Class Group
            '<div class="bs-filter-group">',
            '<label><i class="fas fa-layer-group"></i> Class Group</label>',
            '<select id="classGroupFilter" disabled>',
            '<option value="">Loading…</option>',
            '</select>',
            '</div>',

            // Class (populated after group selection)
            '<div class="bs-filter-group">',
            '<label><i class="fas fa-chalkboard"></i> Class</label>',
            '<select id="classFilter" disabled>',
            '<option value="">Select group first</option>',
            '</select>',
            '</div>',

            // Academic Year
            '<div class="bs-filter-group">',
            '<label><i class="fas fa-calendar-alt"></i> Academic Year</label>',
            '<select id="yearFilter">' + yearOpts + '</select>',
            '</div>',

            // Term
            '<div class="bs-filter-group">',
            '<label><i class="fas fa-book"></i> Term</label>',
            '<select id="termFilter">' + termOpts + '</select>',
            '</div>',

            // Search
            '<div class="bs-filter-group">',
            '<label><i class="fas fa-search"></i> Search Student</label>',
            '<div class="search-wrap">',
            '<input type="text" id="searchInput" placeholder="Name or ID…">',
            '<button class="btn btn-primary btn-sm" id="searchBtn">',
            '<i class="fas fa-search"></i>',
            '</button>',
            '</div>',
            '</div>',
            '</div>',

            // ── BODY ─────────────────────────────────────────────────────────
            '<main class="bs-body">',

            // Stat cards
            '<div class="bs-stats">',
            '<div class="stat-card c-students">',
            '<div class="stat-icon"><i class="fas fa-users"></i></div>',
            '<div><h3 id="statTotal">0</h3><p>Students in Class</p></div>',
            '</div>',
            '<div class="stat-card c-selected">',
            '<div class="stat-icon"><i class="fas fa-check-circle"></i></div>',
            '<div><h3 id="statSelected">0</h3><p>Selected Students</p></div>',
            '</div>',
            '<div class="stat-card c-bills">',
            '<div class="stat-icon"><i class="fas fa-tags"></i></div>',
            '<div><h3 id="statBills">0</h3><p>Bills Selected</p></div>',
            '</div>',
            '<div class="stat-card c-amount">',
            '<div class="stat-icon"><i class="fas fa-coins"></i></div>',
            '<div><h3 id="statAmount">GH₵ 0</h3><p>Total Per Student</p></div>',
            '</div>',
            '</div>',

            // Main grid
            '<div class="bs-grid">',

            // Bill panel
            '<div class="panel">',
            '<div class="panel-head">',
            '<span><i class="fas fa-money-bill-wave"></i>Bill Items</span>',
            '<span class="bill-tally" id="billTally"></span>',
            '</div>',
            '<div class="panel-body">',
            // Select-all row
            '<div class="bill-select-all-row">',
            '<label for="selectAllBills">',
            '<input type="checkbox" id="selectAllBills">',
            'Select / Deselect All',
            '</label>',
            '<span id="billSelectedCount" class="bill-tally">0 selected</span>',
            '</div>',
            // Scrollable list
            '<div class="bill-panel-scroll" id="billSelectionPanel">',
            '<div class="loading-state"><i class="fas fa-circle-notch"></i>Loading bills…</div>',
            '</div>',
            '</div>',
            '</div>',

            // Students panel
            '<div class="panel">',
            '<div class="panel-head">',
            '<span><i class="fas fa-user-graduate"></i>Students</span>',
            '<button class="btn btn-primary btn-sm" id="selectAllStudentsBtn">',
            '<i class="fas fa-check-double"></i>',
            '<span id="selectAllLabel">Select All</span>',
            '</button>',
            '</div>',
            '<div class="panel-body">',
            '<div id="studentsContainer">',
            '<div class="empty-state">',
            '<i class="fas fa-chalkboard-teacher"></i>',
            'Select a class group and class to see students',
            '</div>',
            '</div>',
            '</div>',
            '</div>',
            '</div>',

            // Summary bar
            '<div class="bs-summary" id="billingSummary"></div>',

            '</main>',

            // ── FOOTER ───────────────────────────────────────────────────────
            '<footer class="bs-footer">',
            '<i class="far fa-copyright"></i> Astromy LLC 2013–<span id="copyrightYear"></span>',
            ' &nbsp;|&nbsp; Student Billing System v3.0',
            '</footer>',

            // ── INVOICE MODAL ────────────────────────────────────────────────
            '<div class="bs-modal-overlay" id="invoiceModal">',
            '<div class="bs-modal">',
            '<div class="bs-modal-header">',
            '<h5><i class="fas fa-file-invoice"></i> Student Invoice</h5>',
            '<button class="bs-modal-close" id="closeInvoiceModal">&times;</button>',
            '</div>',
            '<div class="bs-modal-body" id="invoiceModalBody"></div>',
            '<div class="bs-modal-footer">',
            '<button class="btn btn-secondary" id="closeInvoiceBtn">Close</button>',
            '<button class="btn btn-primary"   id="downloadInvoiceBtn">',
            '<i class="fas fa-download"></i> Download',
            '</button>',
            '<button class="btn btn-success"   id="printInvoiceBtn">',
            '<i class="fas fa-print"></i> Print',
            '</button>',
            '</div>',
            '</div>',
            '</div>',

            // ── TOAST ────────────────────────────────────────────────────────
            '<div class="bs-toast" id="bsToast"></div>'

        ].join("");

        document.getElementById("copyrightYear").textContent = new Date().getFullYear();
    }

    // ─── RENDER: BILL SELECTION ───────────────────────────────────────────────
    function renderBillSelection() {
        var data = window.billingData;
        var selected = window.billingState.selectedBills;
        var sections = data.billSections;
        var html = "";
        var total = 0;

        for (var key in sections) {
            var section = sections[key];
            if (!section.bills.length) continue;
            total += section.bills.length;

            html += '<div class="bill-section">' +
                '<div class="bill-section-title">' +
                '<i class="' + section.icon + '"></i>' + section.name +
                ' <small style="font-weight:500;margin-left:4px;">(' + section.bills.length + ')</small>' +
                '</div>';

            section.bills.forEach(function (bill) {
                var chk = selected.has(bill.id) ? "checked" : "";
                var cls = selected.has(bill.id) ? "bill-item is-checked" : "bill-item";
                html +=
                    '<div class="' + cls + '">' +
                    '<label for="bill_' + bill.id + '">' +
                    '<input type="checkbox" id="bill_' + bill.id + '" ' +
                    'data-bill-id="' + bill.id + '" ' + chk + '>' +
                    bill.name +
                    (bill.isMandatory
                        ? ' <span class="badge-mandatory">Mandatory</span>'
                        : '') +
                    '</label>' +
                    '<span class="bill-price">GH&#8373; ' + (bill.price || 0).toLocaleString() + '</span>' +
                    '</div>';
            });

            html += '</div>';
        }

        if (!html) {
            html = '<div class="loading-state"><i class="fas fa-circle-notch"></i>Loading bills…</div>';
        }

        var panel = document.getElementById("billSelectionPanel");
        if (panel) panel.innerHTML = html;

        // Update tally
        updateBillTally();

        // Wire bill checkboxes
        var cbs = document.querySelectorAll("[data-bill-id]");
        cbs.forEach(function (cb) {
            cb.addEventListener("change", function (e) {
                var id = e.target.getAttribute("data-bill-id");
                if (e.target.checked) {
                    window.billingState.selectedBills.add(id);
                    e.target.closest(".bill-item").classList.add("is-checked");
                } else {
                    window.billingState.selectedBills.delete(id);
                    e.target.closest(".bill-item").classList.remove("is-checked");
                }
                syncSelectAllBills();
                updateBillTally();
                renderSummary();
                renderStats();
            });
        });

        // Wire select-all-bills checkbox
        var saBtn = document.getElementById("selectAllBills");
        if (saBtn) {
            saBtn.checked = false;
            saBtn.addEventListener("change", function () {
                var allBills = getAllBills();
                allBills.forEach(function (b) {
                    if (saBtn.checked) {
                        window.billingState.selectedBills.add(b.id);
                    } else {
                        window.billingState.selectedBills.delete(b.id);
                    }
                });
                // Re-render to reflect checked states
                renderBillSelection();
                // Restore selectAll state (renderBillSelection resets it)
                var sa = document.getElementById("selectAllBills");
                if (sa) sa.checked = saBtn.checked;
                renderSummary();
                renderStats();
            });
        }
    }

    function getAllBills() {
        var out = [];
        var s = window.billingData.billSections;
        for (var k in s) s[k].bills.forEach(function (b) {
            out.push(b);
        });
        return out;
    }

    function syncSelectAllBills() {
        var allBills = getAllBills();
        var sa = document.getElementById("selectAllBills");
        if (!sa || !allBills.length) return;
        sa.checked = allBills.every(function (b) {
            return window.billingState.selectedBills.has(b.id);
        });
    }

    function updateBillTally() {
        var el = document.getElementById("billSelectedCount");
        if (el) {
            var n = window.billingState.selectedBills.size;
            el.textContent = n + " selected";
        }
    }

    // ─── RENDER: STUDENTS ─────────────────────────────────────────────────────
    function renderStudents() {
        var classData = window.billingData.classes[window.billingState.selectedClass];
        var container = document.getElementById("studentsContainer");
        if (!container) return;

        if (!classData) {
            container.innerHTML =
                '<div class="empty-state"><i class="fas fa-chalkboard-teacher"></i>' +
                'Select a class group and class to see students</div>';
            return;
        }

        if (classData._loading) {
            container.innerHTML =
                '<div class="loading-state"><i class="fas fa-circle-notch"></i>Loading students…</div>';
            return;
        }

        var students = window.filterStudentsBySearch(classData.students || []);
        var sel = window.billingState.selectedStudents;

        if (!students.length) {
            container.innerHTML =
                '<div class="empty-state"><i class="fas fa-search"></i>No students found</div>';
            return;
        }

        var html = "";
        students.forEach(function (s) {
            var selected = sel.has(s.id);
            html +=
                '<div class="student-item ' + (selected ? "is-selected" : "") + '">' +
                '<input type="checkbox" class="student-cb" data-student-id="' + s.id + '"' +
                (selected ? " checked" : "") + '>' +
                '<div>' +
                '<div class="student-name">' + s.name +
                (selected ? '<span class="badge-selected">&#10003; Selected</span>' : '') +
                '</div>' +
                '<div class="student-meta">' +
                '<span><i class="fas fa-id-card"></i>' + s.id + '</span>' +
                (s.email ? '<span><i class="fas fa-envelope"></i>' + s.email + '</span>' : '') +
                (s.parentPhone ? '<span><i class="fas fa-phone"></i>' + s.parentPhone + '</span>' : '') +
                '</div>' +
                '</div>' +
                '</div>';
        });

        container.innerHTML = html;

        container.querySelectorAll(".student-cb").forEach(function (cb) {
            cb.addEventListener("change", function (e) {
                var id = e.target.getAttribute("data-student-id");
                if (e.target.checked) {
                    window.billingState.selectedStudents.add(id);
                } else {
                    window.billingState.selectedStudents.delete(id);
                }
                var total = (classData.students || []).length;
                window.billingState.selectAllChecked =
                    window.billingState.selectedStudents.size === total;
                renderStudents();   // re-render to update badges
                renderStats();
                renderSummary();
            });
        });
    }

    // ─── RENDER: STATS ────────────────────────────────────────────────────────
    function renderStats() {
        var classData = window.billingData.classes[window.billingState.selectedClass];
        var total = classData ? (classData.students || []).length : 0;
        var selStu = window.billingState.selectedStudents.size;
        var selBills = window.billingState.selectedBills.size;
        var amount = window.calculateTotalAmount();

        setText("statTotal", total);
        setText("statSelected", selStu);
        setText("statBills", selBills);
        setText("statAmount", "GH\u20B5 " + amount.toLocaleString());

        var label = document.getElementById("selectAllLabel");
        if (label) {
            label.textContent = window.billingState.selectAllChecked
                ? "Deselect All" : "Select All";
        }
    }

    // ─── RENDER: SUMMARY ──────────────────────────────────────────────────────
    function renderSummary() {
        var count = window.billingState.selectedStudents.size;
        var perStudent = window.calculateTotalAmount();
        var grandTotal = perStudent * count;
        var billCount = window.billingState.selectedBills.size;

        var el = document.getElementById("billingSummary");
        if (!el) return;

        el.innerHTML =
            '<div class="summary-figures">' +
            '<div class="summary-item">' +
            '<div class="summary-label">Students Selected</div>' +
            '<div class="summary-value">' + count + '</div>' +
            '</div>' +
            '<div class="summary-item">' +
            '<div class="summary-label">Bills Selected</div>' +
            '<div class="summary-value">' + billCount + '</div>' +
            '</div>' +
            '<div class="summary-item">' +
            '<div class="summary-label">Per Student</div>' +
            '<div class="summary-value">GH&#8373; ' + perStudent.toLocaleString() + '</div>' +
            '</div>' +
            '<div class="summary-item">' +
            '<div class="summary-label">Grand Total</div>' +
            '<div class="summary-value">GH&#8373; ' + grandTotal.toLocaleString() + '</div>' +
            '</div>' +
            '</div>' +
            '<button class="process-btn" id="processBtn">' +
            '<i class="fas fa-receipt"></i> Generate Invoice' +
            '</button>';

        var pb = document.getElementById("processBtn");
        if (pb) pb.addEventListener("click", handleProcess);
    }

    // ─── REFRESH ──────────────────────────────────────────────────────────────
    function refresh() {
        renderStats();
        renderStudents();
        renderSummary();
    }

    // ─── INVOICE ──────────────────────────────────────────────────────────────
    function handleProcess() {
        if (!window.billingState.selectedStudents.size || !window.billingState.selectedBills.size) {
            showInvoiceModal(
                '<div class="alert-warning-inv"><i class="fas fa-exclamation-triangle"></i> ' +
                (!window.billingState.selectedStudents.size
                    ? "Please select at least one student."
                    : "Please select at least one bill item.") +
                '</div>'
            );
            return;
        }

        var students = window.getSelectedStudentsDetails();
        var bills = window.getSelectedBillsDetails();
        var perStudent = window.calculateTotalAmount();
        var grandTotal = perStudent * students.length;
        var classData = window.billingData.classes[window.billingState.selectedClass];
        var invoiceNo = "INV-" + Date.now();
        var today = new Date();
        var due = new Date(today.getTime() + 30 * 24 * 60 * 60 * 1000);

        var billRows = bills.map(function (b) {
                return '<tr><td>' + b.name + '</td>' +
                    '<td class="text-right">GH&#8373; ' + (b.price || 0).toLocaleString() + '</td></tr>';
            }).join("") +
            '<tr><td><strong>Subtotal per student</strong></td>' +
            '<td class="text-right"><strong>GH&#8373; ' + perStudent.toLocaleString() + '</strong></td></tr>';

        var studentRows = students.map(function (s) {
                return '<tr><td>' + s.id + '</td><td>' + s.name + '</td><td>' + (s.email || "—") + '</td>' +
                    '<td class="text-right">GH&#8373; ' + perStudent.toLocaleString() + '</td></tr>';
            }).join("") +
            '<tr><td colspan="3"><strong>Grand Total</strong></td>' +
            '<td class="text-right"><strong>GH&#8373; ' + grandTotal.toLocaleString() + '</strong></td></tr>';

        showInvoiceModal(
            '<div class="invoice-meta">' +
            '<strong>Institution:</strong> Astromy University &nbsp;|&nbsp; ' +
            '<strong>Class:</strong> ' + (classData ? classData.className : "—") + '<br>' +
            '<strong>Term:</strong> ' + window.billingState.selectedTerm + ' &nbsp;|&nbsp; ' +
            '<strong>Year:</strong> ' + window.billingState.selectedYear + '<br>' +
            '<strong>Invoice #:</strong> ' + invoiceNo + ' &nbsp;|&nbsp; ' +
            '<strong>Date:</strong> ' + today.toLocaleDateString() + ' &nbsp;|&nbsp; ' +
            '<strong>Due:</strong> ' + due.toLocaleDateString() +
            '</div>' +

            '<div class="invoice-section-title"><i class="fas fa-receipt"></i>Billed Items</div>' +
            '<table class="inv-table">' +
            '<thead><tr><th>Description</th><th class="text-right">Amount</th></tr></thead>' +
            '<tbody>' + billRows + '</tbody>' +
            '</table>' +

            '<div class="invoice-section-title"><i class="fas fa-users"></i>Students Billed (' + students.length + ')</div>' +
            '<table class="inv-table">' +
            '<thead><tr><th>ID</th><th>Name</th><th>Email</th><th class="text-right">Amount</th></tr></thead>' +
            '<tbody>' + studentRows + '</tbody>' +
            '</table>' +

            '<div class="alert-success-inv"><i class="fas fa-check-circle"></i> Status: <strong>Pending Payment</strong></div>'
        );

        window.currentInvoiceData = {
            students: students, bills: bills,
            perStudent: perStudent, grandTotal: grandTotal, invoiceNo: invoiceNo,
            className: classData ? classData.className : "",
            term: window.billingState.selectedTerm,
            year: window.billingState.selectedYear
        };
    }

    function showInvoiceModal(html) {
        var body = document.getElementById("invoiceModalBody");
        var modal = document.getElementById("invoiceModal");
        if (body) body.innerHTML = html;
        if (modal) modal.classList.add("open");
    }

    function closeInvoiceModal() {
        var modal = document.getElementById("invoiceModal");
        if (modal) modal.classList.remove("open");
    }

    // ─── DOWNLOAD / PRINT ─────────────────────────────────────────────────────
    function downloadInvoice() {
        var d = window.currentInvoiceData;
        if (!d) return;
        var txt = "ASTROMY UNIVERSITY – STUDENT INVOICE\n" + "=".repeat(44) + "\n\n";
        txt += "Invoice # : " + d.invoiceNo + "\nDate      : " + new Date().toLocaleDateString() + "\n";
        txt += "Class     : " + d.className + "\nTerm      : " + d.term + " (" + d.year + ")\n\nBILLED ITEMS:\n";
        d.bills.forEach(function (b) {
            txt += "  - " + b.name + ": GH₵ " + b.price + "\n";
        });
        txt += "\nTotal per student: GH₵ " + d.perStudent + "\n\nSTUDENTS (" + d.students.length + "):\n";
        d.students.forEach(function (s) {
            txt += "  - " + s.name + " (" + s.id + "): GH₵ " + d.perStudent + "\n";
        });
        txt += "\nGRAND TOTAL: GH₵ " + d.grandTotal + "\n" + "=".repeat(44) + "\nThank you!\n";

        var a = document.createElement("a");
        a.href = URL.createObjectURL(new Blob([txt], {type: "text/plain"}));
        a.download = d.invoiceNo + ".txt";
        a.click();
        URL.revokeObjectURL(a.href);
    }

    function printInvoice() {
        var content = document.getElementById("invoiceModalBody");
        if (!content) return;
        var win = window.open("", "_blank");
        win.document.write(
            '<!DOCTYPE html><html><head><title>Invoice</title>' +
            '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">' +
            '<style>' +
            'body{font-family:sans-serif;padding:28px;font-size:14px;}' +
            'table{width:100%;border-collapse:collapse;}' +
            'th{background:#2f54d4;color:#fff;padding:10px;text-align:left;font-size:13px;}' +
            'td{padding:9px 10px;border-bottom:1px solid #eee;}' +
            '.text-right{text-align:right;}' +
            '</style>' +
            '</head><body>' + content.innerHTML + '</body></html>'
        );
        win.document.close();
        win.print();
    }

    // ─── EXPORT CSV ───────────────────────────────────────────────────────────
    function exportCSV() {
        var students = window.getSelectedStudentsDetails();
        if (!students.length) {
            toast("No students selected to export.");
            return;
        }

        var bills = window.getSelectedBillsDetails();
        var total = window.calculateTotalAmount();
        var csv = "Student ID,Name,Email,Parent Phone,Billed Items,Total (GHS)\n";
        students.forEach(function (s) {
            csv += '"' + s.id + '","' + s.name + '","' + (s.email || "") + '","' +
                (s.parentPhone || "") + '","' +
                bills.map(function (b) {
                    return b.name;
                }).join("; ") +
                '","GH₵ ' + total + '"\n';
        });

        var a = document.createElement("a");
        a.href = URL.createObjectURL(new Blob([csv], {type: "text/csv"}));
        a.download = "billing_export_" + Date.now() + ".csv";
        a.click();
        URL.revokeObjectURL(a.href);
        toast("CSV exported.");
    }

    // ─── TOAST ────────────────────────────────────────────────────────────────
    function toast(msg) {
        var el = document.getElementById("bsToast");
        if (!el) return;
        el.textContent = msg;
        el.classList.add("show");
        setTimeout(function () {
            el.classList.remove("show");
        }, 3000);
    }

    // ─── UTILITY ──────────────────────────────────────────────────────────────
    function setText(id, val) {
        var el = document.getElementById(id);
        if (el) el.textContent = val;
    }

    function setOptions(selectId, options, valueFn, textFn, selectedVal) {
        var sel = document.getElementById(selectId);
        if (!sel) return;
        sel.innerHTML = options.map(function (o) {
            var v = valueFn(o), t = textFn(o);
            return '<option value="' + v + '" ' + (v === selectedVal ? "selected" : "") + '>' + t + '</option>';
        }).join("");
    }

    // ─── EVENT WIRING ─────────────────────────────────────────────────────────
    function wireEvents() {

        // ── Class Group → fetch classes in that group ────────────────────────
        document.getElementById("classGroupFilter").addEventListener("change", function (e) {
            var groupId = e.target.value;
            window.billingState.selectedGroup = groupId;

            var classSel = document.getElementById("classFilter");
            classSel.innerHTML = '<option value="">Loading…</option>';
            classSel.disabled = true;

            // Clear students
            window.billingState.selectedStudents.clear();
            window.billingState.selectAllChecked = false;
            window.billingState.selectedClass = "";
            refresh();

            if (!groupId) {
                classSel.innerHTML = '<option value="">Select group first</option>';
                return;
            }

            if (typeof window.fetchClassesByGroup === "function") {
                window.fetchClassesByGroup(groupId).then(function () {
                    repopulateClassFilter();
                    classSel.disabled = false;
                });
            }
        });

        // ── Class → fetch students ───────────────────────────────────────────
        document.getElementById("classFilter").addEventListener("change", function (e) {
            var className = e.target.value;
            if (!className) return;
            window.billingState.selectedClass = className;
            window.billingState.selectedStudents.clear();
            window.billingState.selectAllChecked = false;
            loadStudentsForClass(className);
        });

        // ── Year / Term ──────────────────────────────────────────────────────
        document.getElementById("yearFilter").addEventListener("change", function (e) {
            window.billingState.selectedYear = e.target.value;
            renderSummary();
        });
        document.getElementById("termFilter").addEventListener("change", function (e) {
            window.billingState.selectedTerm = e.target.value;
            renderSummary();
        });

        // ── Search ───────────────────────────────────────────────────────────
        var doSearch = function () {
            window.billingState.searchQuery = document.getElementById("searchInput").value;
            renderStudents();
        };
        document.getElementById("searchBtn").addEventListener("click", doSearch);
        document.getElementById("searchInput").addEventListener("keydown", function (e) {
            if (e.key === "Enter") doSearch();
        });

        // ── Select All Students ──────────────────────────────────────────────
        document.getElementById("selectAllStudentsBtn").addEventListener("click", function () {
            var classData = window.billingData.classes[window.billingState.selectedClass];
            if (!classData || !classData.students) return;
            if (window.billingState.selectAllChecked) {
                window.billingState.selectedStudents.clear();
                window.billingState.selectAllChecked = false;
            } else {
                classData.students.forEach(function (s) {
                    window.billingState.selectedStudents.add(s.id);
                });
                window.billingState.selectAllChecked = true;
            }
            refresh();
        });

        // ── Refresh ──────────────────────────────────────────────────────────
        document.getElementById("refreshBtn").addEventListener("click", function () {
            window.billingState.selectedBills.clear();
            window.billingState.selectedStudents.clear();
            window.billingState.selectAllChecked = false;
            window.billingState.searchQuery = "";
            document.getElementById("searchInput").value = "";
            renderBillSelection();
            refresh();
            toast("Refreshed.");
        });

        // ── Export ───────────────────────────────────────────────────────────
        document.getElementById("exportBtn").addEventListener("click", exportCSV);

        // ── Invoice modal ────────────────────────────────────────────────────
        document.getElementById("closeInvoiceModal").addEventListener("click", closeInvoiceModal);
        document.getElementById("closeInvoiceBtn").addEventListener("click", closeInvoiceModal);
        document.getElementById("downloadInvoiceBtn").addEventListener("click", downloadInvoice);
        document.getElementById("printInvoiceBtn").addEventListener("click", printInvoice);
        document.getElementById("invoiceModal").addEventListener("click", function (e) {
            if (e.target === e.currentTarget) closeInvoiceModal();
        });
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    function repopulateClassFilter() {
        var sel = document.getElementById("classFilter");
        var classes = window.billingData.classes;
        if (!sel) return;
        var keys = Object.keys(classes);
        if (!keys.length) {
            sel.innerHTML = '<option value="">No classes found</option>';
            return;
        }
        sel.innerHTML = '<option value="">Select class…</option>' +
            keys.map(function (k) {
                return '<option value="' + k + '">' + classes[k].className + '</option>';
            }).join("");
    }

    function repopulateGroupFilter(groups) {
        var sel = document.getElementById("classGroupFilter");
        if (!sel) return;
        sel.innerHTML = '<option value="">Select group…</option>' +
            groups.map(function (g) {
                return '<option value="' + g.id + '">' + g.name + '</option>';
            }).join("");
        sel.disabled = false;
    }

    function loadStudentsForClass(className) {
        if (!className) return;
        // Mark as loading so renderStudents shows the spinner
        if (window.billingData.classes[className]) {
            window.billingData.classes[className]._loading = true;
            renderStudents();
        }
        if (typeof window.fetchStudentsForClass === "function") {
            window.fetchStudentsForClass(className).then(function () {
                if (window.billingData.classes[className]) {
                    window.billingData.classes[className]._loading = false;
                }
                refresh();
            });
        }
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        renderBillSelection();
        renderSummary();
        renderStats();
        // Poll until the API data is ready (fetched by _financeBilling.js)
        waitForRealData();
    }

    function waitForRealData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            var hasGroups = window.billingData._classGroupsLoaded;
            var hasBills =
                window.billingData.billSections.general.bills.length > 0 ||
                window.billingData.billSections.specific.bills.length > 0;

            if (hasGroups && hasBills) {
                clearInterval(timer);
                afterAPIDataLoaded();
            } else if (attempts > 200) {   // ~6 s
                clearInterval(timer);
                afterAPIDataLoaded();       // render whatever arrived
                console.warn("[financeBilling] API data slow — rendering partial data.");
            }
        }, 30);
    }

    function afterAPIDataLoaded() {
        // Populate class-group dropdown
        repopulateGroupFilter(window.billingData.classGroups || []);
        // Render bills (now populated from API)
        renderBillSelection();
        // Year options may have been set by _financeBilling.js
        var yearSel = document.getElementById("yearFilter");
        if (yearSel && window.billingData.academicYears.length) {
            yearSel.innerHTML = window.billingData.academicYears.map(function (y) {
                return '<option ' + (y === window.billingState.selectedYear ? "selected" : "") + '>' + y + '</option>';
            }).join("");
        }
        renderStats();
        renderSummary();
    }

    // ─── DYNAMIC SCRIPT LOADER ────────────────────────────────────────────────
    function loadLogicScript() {
        if (window.billingData && window.billingState) {
            init();
            return;
        }

        var s = document.createElement("script");
        s.type = "text/javascript";
        s.src = "scripts/_financeBilling.js";

        s.onload = function () {
            if (window.billingData && window.billingState) {
                init();
            } else {
                console.error("[financeBilling] _financeBilling.js loaded but " +
                    "window.billingData is not defined.");
            }
        };

        s.onerror = function () {
            console.error("[financeBilling] Could not load scripts/_financeBilling.js");
        };

        document.body.appendChild(s);
    }

    // ─── BOOT ─────────────────────────────────────────────────────────────────
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initBillingSystem = init;

})();