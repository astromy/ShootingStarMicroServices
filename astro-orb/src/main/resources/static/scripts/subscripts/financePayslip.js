/**
 * financePayslip.js  —  UI renderer for Payslip Generation.
 *
 * Loaded by the Orb host page.  Injects CSS, loads _financePayslip.js,
 * then renders the payslip search and print interface once data is ready.
 *
 * Features:
 *   - Staff search / select from loaded staff list
 *   - Pay-period + academic-year cascade
 *   - Payslip preview card (earnings, deductions, net pay)
 *   - Print-to-new-window via window.payslipPrint()
 *   - All-slips history table per staff member
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financePayslip') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    // ── CSS ──────────────────────────────────────────────────────────────────
    (function () {
        if (document.getElementById('payslipCSS')) return;
        var l = document.createElement('link');
        l.id = 'payslipCSS';
        l.rel = 'stylesheet';
        l.href = _base + '../../styles/style.css';
        document.head.appendChild(l);
    })();

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        var s = window.payslipState;

        var yearOpts = s.academicYears.map(function (y) {
            return '<option>' + y + '</option>';
        }).join('');

        var periodOpts = s.payPeriods.map(function (p) {
            return '<option value="' + p.value + '">' + p.label + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = [
            '<div class="ps-page">',

            // HEADER
            '<header class="fc-header">',
            '<div>',
            '<h1><i class="fas fa-file-invoice"></i> Payslip Generation</h1>',
            '<p>Retrieve and print official payslips for any staff member</p>',
            '</div>',
            '</header>',

            // BODY — two-column
            '<div class="fc-body">',

            // LEFT: search panel
            '<div class="fc-col fc-col-left">',

            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-search"></i> Find Staff</div>',
            '<div class="fc-card-body">',

            '<div class="fc-field">',
            '<label>Staff ID</label>',
            '<div class="ps-id-wrap">',
            '<input type="text" id="psStaffId" placeholder="Enter or select staff ID…" autocomplete="off">',
            '<button class="fc-btn fc-btn-secondary fc-btn-sm" id="psStaffPickBtn" type="button">',
            '<i class="fas fa-list"></i>',
            '</button>',
            '</div>',
            '<div class="ps-staff-dropdown" id="psStaffDropdown" style="display:none"></div>',
            '</div>',

            '<div class="fc-field">',
            '<label>Pay Period <span class="req">*</span></label>',
            '<select id="psPeriod">' + periodOpts + '</select>',
            '</div>',

            '<div class="fc-field">',
            '<label>Academic Year <span class="req">*</span></label>',
            '<select id="psYear">' + yearOpts + '</select>',
            '</div>',

            '<div class="fc-actions" style="margin-top:16px">',
            '<button class="fc-btn fc-btn-primary" id="psFetchBtn">',
            '<i class="fas fa-sync-alt"></i> Load Payslip',
            '</button>',
            '<button class="fc-btn fc-btn-secondary" id="psAllSlipsBtn" style="display:none">',
            '<i class="fas fa-history"></i> All Slips',
            '</button>',
            '</div>',

            '</div></div>',  // end card

            // Summary card (hidden until loaded)
            '<div class="fc-card ps-summary-card" id="psSummaryCard" style="display:none">',
            '<div class="fc-card-head"><i class="fas fa-user-tie"></i> Staff Summary</div>',
            '<div class="fc-card-body">',
            '<div class="fc-student-name" id="psStaffName"></div>',
            '<div class="fc-bill-grid">',
            '<div class="fc-bill-item"><div class="fc-bill-label">Basic Salary</div><div class="fc-bill-val" id="psBasic">—</div></div>',
            '<div class="fc-bill-item"><div class="fc-bill-label">Allowances</div><div class="fc-bill-val fc-paid" id="psAllowances">—</div></div>',
            '<div class="fc-bill-item"><div class="fc-bill-label">Deductions</div><div class="fc-bill-val fc-owing" id="psDeductions">—</div></div>',
            '<div class="fc-bill-item"><div class="fc-bill-label">Net Pay</div><div class="fc-bill-val fc-balance" id="psNetPay">—</div></div>',
            '</div>',
            '</div></div>',

            '</div>',  // end left col

            // RIGHT: payslip preview + history
            '<div class="fc-col fc-col-right">',

            '<div class="fc-card" id="psPreviewCard" style="display:none">',
            '<div class="fc-card-head">',
            '<span><i class="fas fa-file-invoice"></i> Payslip Preview</span>',
            '<div class="ps-preview-actions">',
            '<span class="ps-status-badge" id="psStatusBadge"></span>',
            '<button class="fc-btn fc-btn-primary" id="psPrintBtn">',
            '<i class="fas fa-print"></i> Print',
            '</button>',
            '</div>',
            '</div>',
            '<div class="fc-card-body" id="psPreviewBody"></div>',
            '</div>',

            // No-slip placeholder
            '<div class="ps-idle-card" id="psIdleCard">',
            '<div class="ps-idle"><i class="fas fa-file-alt"></i>',
            '<p>Select a staff member and pay period, then click <strong>Load Payslip</strong></p>',
            '</div>',
            '</div>',

            // History card (hidden by default)
            '<div class="fc-card fc-history-card" id="psHistoryCard" style="display:none">',
            '<div class="fc-card-head">',
            '<span><i class="fas fa-history"></i> All Payslips</span>',
            '<button class="fc-btn-icon" id="psHistCloseBtn" title="Close"><i class="fas fa-times"></i></button>',
            '</div>',
            '<div class="fc-card-body">',
            '<table class="fc-table">',
            '<thead><tr>',
            '<th>Period</th><th>Year</th><th>Basic</th>',
            '<th>Allowances</th><th>Deductions</th><th>Net Pay</th><th>Status</th><th></th>',
            '</tr></thead>',
            '<tbody id="psHistBody"><tr><td colspan="8" class="fc-empty">Loading…</td></tr></tbody>',
            '</table>',
            '</div></div>',

            '</div>',  // end right col
            '</div>',  // end body

            // All-slips modal
            '<div class="fc-modal-overlay" id="psHistModal">',
            '<div class="fc-modal fc-modal-lg">',
            '<div class="fc-modal-head">',
            '<h5><i class="fas fa-history"></i> Payslip History</h5>',
            '<button class="fc-modal-close" id="psHistModalClose">&times;</button>',
            '</div>',
            '<div class="fc-modal-body" id="psHistModalBody"></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="psHistModalCloseBtn">Close</button>',
            '</div>',
            '</div>',
            '</div>',

            '<div class="fc-toast" id="psToast"></div>',

            '<footer class="footer">',
            '<i class="far fa-copyright"></i> Astromy LLC 2013–<span id="psYear2"></span> | Payslip Generation',
            '</footer>',

            '</div>',  // end ps-page
        ].join('');

        document.getElementById('psYear2').textContent = new Date().getFullYear();
    }

    // ── RENDER PAYSLIP PREVIEW ────────────────────────────────────────────────
    function renderPreview(slip) {
        if (!slip) {
            document.getElementById('psPreviewCard').style.display = 'none';
            document.getElementById('psIdleCard').style.display = '';
            document.getElementById('psSummaryCard').style.display = 'none';
            toast('No payslip found for this staff / period combination.', 'warning');
            return;
        }

        var fmt = window.payslipFmt;

        // Summary card
        document.getElementById('psStaffName').textContent = slip.staffName || slip.staffId;
        document.getElementById('psBasic').textContent = fmt.money(slip.basicSalary || 0);
        document.getElementById('psAllowances').textContent = fmt.money(slip.totalAllowances || 0);
        document.getElementById('psDeductions').textContent = fmt.money(slip.totalDeductions || 0);
        document.getElementById('psNetPay').textContent = fmt.money(slip.netSalary || 0);
        document.getElementById('psSummaryCard').style.display = '';

        // Status badge
        var statusMeta = {
            PENDING: {cls: 'sal-pending', label: 'Pending'},
            APPROVED: {cls: 'sal-approved', label: 'Approved'},
            PAID: {cls: 'sal-paid', label: 'Paid'},
        };
        var sm = statusMeta[slip.status] || {cls: 'sal-pending', label: slip.status || 'Draft'};
        var badge = document.getElementById('psStatusBadge');
        badge.textContent = sm.label;
        badge.className = 'ps-status-badge sal-status ' + sm.cls;

        // Build inline preview
        var allowances = (slip.salaryItems || []).filter(function (i) {
            return i.itemType === 'ALLOWANCE';
        });
        var deductions = (slip.salaryItems || []).filter(function (i) {
            return i.itemType === 'DEDUCTION';
        });

        function itemRowHtml(item, isAllowance) {
            var val = item.isPercentage
                ? (item.percentageRate || 0) + '% of basic'
                : fmt.money(item.amount || 0);
            return '<div class="ps-line-item ' + (isAllowance ? 'ps-allow' : 'ps-deduct') + '">' +
                '<span>' + (item.itemName || '—') + '</span>' +
                '<strong>' + val + '</strong>' +
                '</div>';
        }

        var earningsHtml = [
            '<div class="ps-line-item ps-basic">',
            '<span>Basic Salary</span>',
            '<strong>' + fmt.money(slip.basicSalary || 0) + '</strong>',
            '</div>',
        ].join('');

        if (allowances.length) {
            earningsHtml += allowances.map(function (i) {
                return itemRowHtml(i, true);
            }).join('');
        }

        var deductionsHtml = deductions.length
            ? deductions.map(function (i) {
                return itemRowHtml(i, false);
            }).join('')
            : '<div class="fc-empty" style="font-size:12px;padding:8px 0">No deductions</div>';

        document.getElementById('psPreviewBody').innerHTML = [
            // Employee details section
            '<div class="ps-preview-section">',
            '<div class="ps-preview-section-title"><i class="fas fa-user"></i> Employee</div>',
            '<div class="ps-preview-row"><span>Staff ID</span><strong>' + (slip.staffId || '—') + '</strong></div>',
            '<div class="ps-preview-row"><span>Name</span><strong>' + (slip.staffName || '—') + '</strong></div>',
            '<div class="ps-preview-row"><span>Designation</span><strong>' + (slip.designation || '—') + '</strong></div>',
            '</div>',

            // Period
            '<div class="ps-preview-section">',
            '<div class="ps-preview-section-title"><i class="fas fa-calendar-alt"></i> Pay Period</div>',
            '<div class="ps-preview-row"><span>Period</span><strong>' + (slip.payPeriod || '—') + '</strong></div>',
            '<div class="ps-preview-row"><span>Academic Year</span><strong>' + (slip.academicYear || '—') + '</strong></div>',
            slip.paymentDate
                ? '<div class="ps-preview-row"><span>Payment Date</span><strong>' + fmt.date(slip.paymentDate) + '</strong></div>'
                : '',
            slip.processedBy
                ? '<div class="ps-preview-row"><span>Processed By</span><strong>' + slip.processedBy + '</strong></div>'
                : '',
            '</div>',

            // Earnings
            '<div class="ps-preview-section">',
            '<div class="ps-preview-section-title"><i class="fas fa-arrow-circle-up" style="color:#0a7a42"></i> Earnings</div>',
            earningsHtml,
            '<div class="ps-preview-subtotal">',
            '<span>Gross Pay</span><strong class="fc-paid">' + fmt.money((slip.basicSalary || 0) + (slip.totalAllowances || 0)) + '</strong>',
            '</div>',
            '</div>',

            // Deductions
            '<div class="ps-preview-section">',
            '<div class="ps-preview-section-title"><i class="fas fa-arrow-circle-down" style="color:#c0392b"></i> Deductions</div>',
            deductionsHtml,
            '<div class="ps-preview-subtotal">',
            '<span>Total Deductions</span><strong class="fc-owing">' + fmt.money(slip.totalDeductions || 0) + '</strong>',
            '</div>',
            '</div>',

            // Net pay
            '<div class="ps-net-bar">',
            '<span>NET PAY</span>',
            '<strong>' + fmt.money(slip.netSalary || 0) + '</strong>',
            '</div>',
        ].join('');

        document.getElementById('psIdleCard').style.display = 'none';
        document.getElementById('psPreviewCard').style.display = '';
        document.getElementById('psHistoryCard').style.display = 'none';

        // Store for print
        window._psCurrentSlip = slip;
        document.getElementById('psAllSlipsBtn').style.display = '';
    }

    // ── RENDER HISTORY TABLE ─────────────────────────────────────────────────
    function renderHistory(slips) {
        var fmt = window.payslipFmt;
        var body = document.getElementById('psHistBody');
        if (!body) return;

        if (!slips || !slips.length) {
            body.innerHTML = '<tr><td colspan="8" class="fc-empty">No payslip records found for this staff member</td></tr>';
            return;
        }

        var statusMeta = {
            PENDING: {cls: 'sal-pending', label: 'Pending'},
            APPROVED: {cls: 'sal-approved', label: 'Approved'},
            PAID: {cls: 'sal-paid', label: 'Paid'},
        };

        body.innerHTML = slips.map(function (s) {
            var sm = statusMeta[s.status] || {cls: '', label: s.status || '—'};
            return '<tr>' +
                '<td>' + (s.payPeriod || '—') + '</td>' +
                '<td>' + (s.academicYear || '—') + '</td>' +
                '<td class="fc-money">' + fmt.money(s.basicSalary) + '</td>' +
                '<td class="fc-money fc-paid">' + fmt.money(s.totalAllowances) + '</td>' +
                '<td class="fc-money fc-owing">' + fmt.money(s.totalDeductions) + '</td>' +
                '<td class="fc-money"><strong>' + fmt.money(s.netSalary) + '</strong></td>' +
                '<td><span class="sal-status ' + sm.cls + '">' + sm.label + '</span></td>' +
                '<td>' +
                '<button class="fc-btn fc-btn-primary fc-btn-sm ps-hist-print-btn" data-idx="' + slips.indexOf(s) + '" title="Print this payslip">' +
                '<i class="fas fa-print"></i></button>' +
                '</td>' +
                '</tr>';
        }).join('');

        // Wire print buttons in history table
        body.querySelectorAll('.ps-hist-print-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var idx = parseInt(this.dataset.idx);
                var slip = slips[idx];
                if (slip) window.payslipPrint(slip);
            });
        });
    }

    // ── RENDER HISTORY MODAL ─────────────────────────────────────────────────
    function renderHistoryModal(slips) {
        var fmt = window.payslipFmt;
        var modal = document.getElementById('psHistModal');
        var body = document.getElementById('psHistModalBody');

        if (!slips || !slips.length) {
            body.innerHTML = '<div class="fc-empty"><i class="fas fa-inbox"></i> No payslip records found.</div>';
            modal.classList.add('open');
            return;
        }

        var statusMeta = {
            PENDING: {cls: 'sal-pending', label: 'Pending'},
            APPROVED: {cls: 'sal-approved', label: 'Approved'},
            PAID: {cls: 'sal-paid', label: 'Paid'},
        };

        var rows = slips.map(function (s, idx) {
            var sm = statusMeta[s.status] || {cls: '', label: s.status || '—'};
            return '<tr>' +
                '<td>' + (s.payPeriod || '—') + '</td>' +
                '<td>' + (s.academicYear || '—') + '</td>' +
                '<td class="fc-money">' + fmt.money(s.basicSalary) + '</td>' +
                '<td class="fc-money fc-paid">' + fmt.money(s.totalAllowances) + '</td>' +
                '<td class="fc-money fc-owing">' + fmt.money(s.totalDeductions) + '</td>' +
                '<td class="fc-money"><strong>' + fmt.money(s.netSalary) + '</strong></td>' +
                '<td><span class="sal-status ' + sm.cls + '">' + sm.label + '</span></td>' +
                '<td>' +
                '<button class="fc-btn fc-btn-primary fc-btn-sm ps-modal-print-btn" data-idx="' + idx + '">' +
                '<i class="fas fa-print"></i> Print</button>' +
                '</td>' +
                '</tr>';
        }).join('');

        body.innerHTML =
            '<table class="fc-table">' +
            '<thead><tr><th>Period</th><th>Year</th><th>Basic</th><th>Allowances</th><th>Deductions</th><th>Net Pay</th><th>Status</th><th></th></tr></thead>' +
            '<tbody>' + rows + '</tbody>' +
            '</table>';

        body.querySelectorAll('.ps-modal-print-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var idx = parseInt(this.dataset.idx);
                if (slips[idx]) window.payslipPrint(slips[idx]);
            });
        });

        modal.classList.add('open');
    }

    // ── STAFF AUTOCOMPLETE / PICKER ──────────────────────────────────────────
    function setupStaffPicker() {
        var input = document.getElementById('psStaffId');
        var dropdown = document.getElementById('psStaffDropdown');

        function filterStaff(q) {
            var staff = window.payslipState.staff || [];
            if (!q) return staff.slice(0, 20);
            q = q.toLowerCase();
            return staff.filter(function (s) {
                return s.id.toLowerCase().includes(q) || s.name.toLowerCase().includes(q);
            }).slice(0, 20);
        }

        function showDropdown(items) {
            if (!items.length) {
                dropdown.style.display = 'none';
                return;
            }
            dropdown.innerHTML = items.map(function (s) {
                return '<div class="ps-staff-option" data-id="' + s.id + '" data-name="' + (s.name || '') + '">' +
                    '<strong>' + s.id + '</strong>' +
                    (s.name ? ' — ' + s.name : '') +
                    (s.designation ? '<small> · ' + s.designation + '</small>' : '') +
                    '</div>';
            }).join('');
            dropdown.style.display = '';

            dropdown.querySelectorAll('.ps-staff-option').forEach(function (opt) {
                opt.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    input.value = this.dataset.id;
                    window._psSelectedStaffName = this.dataset.name;
                    dropdown.style.display = 'none';
                });
            });
        }

        input.addEventListener('input', function () {
            showDropdown(filterStaff(this.value.trim()));
        });

        input.addEventListener('blur', function () {
            setTimeout(function () {
                dropdown.style.display = 'none';
            }, 200);
        });

        input.addEventListener('focus', function () {
            if (this.value.length >= 1) showDropdown(filterStaff(this.value.trim()));
        });

        // Pick button — show all staff
        document.getElementById('psStaffPickBtn').addEventListener('click', function () {
            input.focus();
            showDropdown(filterStaff(''));
        });
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {

        setupStaffPicker();

        // Load payslip button
        document.getElementById('psFetchBtn').addEventListener('click', async function () {
            var staffId = document.getElementById('psStaffId').value.trim();
            var period = document.getElementById('psPeriod').value;
            var year = document.getElementById('psYear').value;

            if (!staffId) {
                toast('Enter a Staff ID.', 'error');
                return;
            }
            if (!period) {
                toast('Select a pay period.', 'error');
                return;
            }

            var slip = await window.payslipFetch(staffId, period, year);
            renderPreview(slip);
        });

        // Print button
        document.getElementById('psPrintBtn').addEventListener('click', function () {
            if (!window._psCurrentSlip) {
                toast('No payslip loaded.', 'warning');
                return;
            }
            window.payslipPrint(window._psCurrentSlip);
        });

        // All slips button (inline history card)
        document.getElementById('psAllSlipsBtn').addEventListener('click', async function () {
            var staffId = document.getElementById('psStaffId').value.trim();
            if (!staffId) {
                toast('Load a payslip first.', 'warning');
                return;
            }
            var slips = await window.payslipFetchAll(staffId);
            renderHistoryModal(slips);
        });

        // History close (inline card)
        document.getElementById('psHistCloseBtn').addEventListener('click', function () {
            document.getElementById('psHistoryCard').style.display = 'none';
            if (window._psCurrentSlip) {
                document.getElementById('psPreviewCard').style.display = '';
            }
        });

        // History modal close
        document.getElementById('psHistModalClose').addEventListener('click', function () {
            document.getElementById('psHistModal').classList.remove('open');
        });
        document.getElementById('psHistModalCloseBtn').addEventListener('click', function () {
            document.getElementById('psHistModal').classList.remove('open');
        });
        document.getElementById('psHistModal').addEventListener('click', function (e) {
            if (e.target === this) this.classList.remove('open');
        });

        // Enter key on staff ID field triggers load
        document.getElementById('psStaffId').addEventListener('keydown', function (e) {
            if (e.key === 'Enter') document.getElementById('psFetchBtn').click();
        });
    }

    // ── TOAST ────────────────────────────────────────────────────────────────
    function toast(msg, type) {
        var el = document.getElementById('psToast');
        if (!el) return;
        el.textContent = msg;
        el.className = 'fc-toast show ' + (type || 'info');
        clearTimeout(el._t);
        el._t = setTimeout(function () {
            el.classList.remove('show');
        }, 3500);
    }

    // ── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        waitForData();
    }

    function waitForData() {
        var t = setInterval(function () {
            if (window.payslipState && window.payslipState._loaded) {
                clearInterval(t);
                // Nothing to auto-populate — user must pick a staff member
            }
        }, 30);
    }

    function boot() {
        if (window.payslipState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financePayslip.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();

})();