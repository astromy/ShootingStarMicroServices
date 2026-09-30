/**
 * financeSalary.js  —  UI for Finance -> Salary Setup.
 *
 * Three tabs:
 *   Staff profiles  every HR staff member, with their standing salary profile
 *                   (basic, regular allowances/deductions, how they're paid)
 *   Payroll runs    monthly runs; "Run payroll" creates one per active profile
 *   Settings        SSNIT rates and PAYE tax bands
 *
 * Approving and paying runs happens on Finance -> Salary Approvals.
 * Uses the shared design system in styles/style.css. Loads _financeSalary.js
 * (data layer); _payrollShared.js is loaded before this file by common.js.
 */
(function () {
    'use strict';

    // Set once _payrollShared.js is available (see loadScripts below).
    var P, esc, money;

    var METHOD_LABEL = {BANK: 'Bank', MOMO: 'Mobile money', CASH: 'Cash'};

    // Ghana PAYE monthly bands (Income Tax Act as amended in 2023). Offered as
    // a starting point only - confirm against current GRA rates before saving.
    var GHANA_BANDS = [
        {min: 0, max: 490, rate: 0},
        {min: 490, max: 600, rate: 5},
        {min: 600, max: 730, rate: 10},
        {min: 730, max: 3896.67, rate: 17.5},
        {min: 3896.67, max: 19896.67, rate: 25},
        {min: 19896.67, max: 50416.67, rate: 30},
        {min: 50416.67, max: null, rate: 35},
    ];

    var activeTab = 'profiles';
    var editingStaff = null;

    // ─── DOM ─────────────────────────────────────────────────────────────────
    function options(list, selected) {
        return list.map(function (o) {
            var value = typeof o === 'object' ? o.value : o;
            var label = typeof o === 'object' ? o.label : o;
            return '<option value="' + esc(value) + '"' + (String(value) === String(selected) ? ' selected' : '') + '>' + esc(label) + '</option>';
        }).join('');
    }

    function monthOptions(selected) {
        return options(P.months.map(function (m, i) { return {value: i, label: m}; }), selected);
    }

    function buildDOM() {
        var now = P.current();
        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-money-check-alt"></i> Salary Setup</h1>',
            '<p>Staff salary profiles, monthly payroll runs and payroll settings</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="spRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '<button class="fc-btn fc-btn-primary" id="spRunBtn"><i class="fas fa-play"></i> Run payroll</button>',
            '</div>',
            '</header>',

            '<div class="ph-controls" role="tablist" style="gap:8px">',
            '<button class="fc-btn fc-btn-primary sp-tab" data-tab="profiles" role="tab">Staff profiles</button>',
            '<button class="fc-btn fc-btn-secondary sp-tab" data-tab="runs" role="tab">Payroll runs</button>',
            '<button class="fc-btn fc-btn-secondary sp-tab" data-tab="settings" role="tab">Settings</button>',
            '</div>',
            '<p class="fc-empty" id="spLoadError" style="display:none"></p>',

            // ── Profiles tab
            '<section id="spProfiles" class="sp-panel">',
            '<div class="ph-stats" id="spProfileStats"></div>',
            '<div class="ph-controls">',
            '<div class="ph-search-wrap"><i class="fas fa-search"></i>',
            '<input type="text" id="spProfileSearch" placeholder="Search staff by name or ID…"></div>',
            '<select id="spProfileFilter">',
            '<option value="">All staff</option><option value="none">No salary profile</option>',
            '<option value="active">Active profiles</option><option value="inactive">Inactive profiles</option>',
            '</select>',
            '</div>',
            '<div class="ph-table-wrap"><table class="fc-table" id="spProfileTable">',
            '<thead><tr><th>Staff ID</th><th>Name</th><th>Designation</th><th>Basic salary</th><th>Paid by</th><th>Status</th><th></th></tr></thead>',
            '<tbody></tbody></table></div>',
            '</section>',

            // ── Runs tab
            '<section id="spRuns" class="sp-panel" style="display:none">',
            '<div class="ph-controls">',
            '<select id="spRunYear" aria-label="Academic year"><option value="">All academic years</option>' + options(P.academicYears(), '') + '</select>',
            '<select id="spRunPeriod" aria-label="Pay period"><option value="">All months</option></select>',
            '<select id="spRunStatus" aria-label="Status"><option value="">All statuses</option>',
            '<option value="PENDING">Pending approval</option><option value="APPROVED">Approved</option><option value="PAID">Paid</option></select>',
            '<button class="fc-btn fc-btn-secondary" id="spDeleteBtn" disabled><i class="fas fa-trash"></i> Remove selected pending runs</button>',
            '</div>',
            '<div class="ph-stats" id="spRunStats"></div>',
            '<div class="ph-table-wrap"><table class="fc-table" id="spRunTable">',
            '<thead><tr><th><input type="checkbox" id="spRunAll" aria-label="Select all pending runs"></th>',
            '<th>Staff</th><th>Period</th><th>Gross</th><th>Deductions</th><th>Net pay</th><th>Status</th><th>Created by</th><th></th></tr></thead>',
            '<tbody></tbody></table></div>',
            '</section>',

            // ── Settings tab
            '<section id="spSettings" class="sp-panel" style="display:none">',
            '<div class="fc-card"><div class="fc-card-head"><i class="fas fa-percent"></i> SSNIT</div><div class="fc-card-body">',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="spSsnitEmp">Employee rate (%) — deducted from pay</label><input type="number" id="spSsnitEmp" min="0" max="100" step="0.01"></div>',
            '<div class="fc-field"><label for="spSsnitEmr">Employer rate (%) — paid by the school</label><input type="number" id="spSsnitEmr" min="0" max="100" step="0.01"></div>',
            '</div></div></div>',
            '<div class="fc-card" style="margin-top:16px"><div class="fc-card-head"><i class="fas fa-landmark"></i> Income tax (PAYE) — monthly bands</div><div class="fc-card-body">',
            '<p style="margin:0 0 10px;color:#6b7280;font-size:13px">Tax is worked out on taxable income: gross pay minus employee SSNIT. Each band starts where the one before it ends; leave the last band\'s "Up to" empty.</p>',
            '<div class="ph-table-wrap"><table class="fc-table" id="spBandTable">',
            '<thead><tr><th>From (GH₵)</th><th>Up to (GH₵)</th><th>Rate (%)</th><th></th></tr></thead><tbody></tbody></table></div>',
            '<div style="display:flex;gap:8px;margin-top:10px;flex-wrap:wrap">',
            '<button class="fc-btn fc-btn-secondary fc-btn-sm" id="spAddBandBtn" type="button"><i class="fas fa-plus"></i> Add band</button>',
            '<button class="fc-btn fc-btn-secondary fc-btn-sm" id="spGhanaBandsBtn" type="button"><i class="fas fa-magic"></i> Fill in Ghana PAYE bands</button>',
            '</div>',
            '<p style="margin:8px 0 0;color:#6b7280;font-size:12px">The Ghana bands are a starting point. Check them against the current GRA rates before saving.</p>',
            '</div></div>',
            '<div style="margin-top:16px"><button class="fc-btn fc-btn-primary" id="spSaveSettingsBtn"><i class="fas fa-check"></i> Save settings</button></div>',
            '</section>',

            // ── Profile modal
            '<div class="fc-modal-overlay" id="spProfileModal">',
            '<div class="fc-modal" style="max-width:760px">',
            '<div class="fc-modal-head"><h5 id="spProfileTitle">Salary profile</h5>',
            '<button class="fc-modal-close" data-close="spProfileModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body">',
            '<p id="spProfileSub" style="margin:0 0 12px;color:#6b7280"></p>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="spBasic">Basic salary (GH₵) <span class="req">*</span></label><input type="number" id="spBasic" min="0" step="0.01"></div>',
            '<div class="fc-field"><label for="spMethod">Paid by <span class="req">*</span></label>',
            '<select id="spMethod"><option value="BANK">Bank</option><option value="MOMO">Mobile money</option><option value="CASH">Cash</option></select></div>',
            '</div>',
            '<div id="spBankFields">',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="spBankName">Bank <span class="req">*</span></label><input type="text" id="spBankName" placeholder="e.g. GCB Bank"></div>',
            '<div class="fc-field"><label for="spBankBranch">Branch</label><input type="text" id="spBankBranch"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="spAccountName">Account name</label><input type="text" id="spAccountName"></div>',
            '<div class="fc-field"><label for="spAccountNumber">Account number <span class="req">*</span></label><input type="text" id="spAccountNumber"></div>',
            '</div>',
            '</div>',
            '<div id="spMomoFields" style="display:none"><div class="fc-fields-row">',
            '<div class="fc-field"><label for="spMomoNumber">Mobile money number <span class="req">*</span></label><input type="text" id="spMomoNumber" placeholder="e.g. 024 000 0000"></div>',
            '</div></div>',
            '<label style="display:flex;gap:8px;align-items:center;margin:4px 0 8px"><input type="checkbox" id="spActive" checked> Include in payroll runs</label>',
            '<div class="sal-items-section">',
            '<div class="sal-items-title"><i class="fas fa-list"></i> Regular allowances & deductions</div>',
            '<div id="spItems"></div>',
            '<button class="fc-btn fc-btn-secondary fc-btn-sm" id="spAddItemBtn" type="button"><i class="fas fa-plus"></i> Add allowance or deduction</button>',
            '<p style="margin:8px 0 0;color:#6b7280;font-size:12px">SSNIT and income tax are calculated automatically each time payroll runs — don\'t add them here.</p>',
            '</div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="spProfileModal">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="spSaveProfileBtn"><i class="fas fa-check"></i> Save profile</button>',
            '</div></div></div>',

            // ── Run payroll modal
            '<div class="fc-modal-overlay" id="spRunModal">',
            '<div class="fc-modal" style="max-width:640px">',
            '<div class="fc-modal-head"><h5><i class="fas fa-play"></i> Run payroll</h5>',
            '<button class="fc-modal-close" data-close="spRunModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body" id="spRunBody">',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="spRunMonth">Month</label><select id="spRunMonth">' + monthOptions(now.month) + '</select></div>',
            '<div class="fc-field"><label for="spRunCalYear">Year</label><select id="spRunCalYear">' + options(P.years(), now.year) + '</select></div>',
            '<div class="fc-field"><label for="spRunAcademicYear">Academic year</label><select id="spRunAcademicYear">' + options(P.academicYears(), now.academicYear) + '</select></div>',
            '</div>',
            '<p id="spRunInfo" style="margin:4px 0 0;color:#374151"></p>',
            '</div>',
            '<div id="spRunResult" class="fc-modal-body" style="display:none"></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="spRunModal" id="spRunCancel">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="spRunConfirm"><i class="fas fa-play"></i> Run payroll</button>',
            '</div></div></div>',

            // ── Run detail modal
            '<div class="fc-modal-overlay" id="spDetailModal">',
            '<div class="fc-modal" style="max-width:600px">',
            '<div class="fc-modal-head"><h5 id="spDetailTitle">Salary run</h5>',
            '<button class="fc-modal-close" data-close="spDetailModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body" id="spDetailBody"></div>',
            '<div class="fc-modal-foot"><button class="fc-btn fc-btn-secondary" data-close="spDetailModal">Close</button></div>',
            '</div></div>',

            '<div class="fc-toast" id="spToast" role="status" aria-live="polite"></div>',
        ].join('') + '</div>';
    }

    // ─── SMALL HELPERS ───────────────────────────────────────────────────────
    var toastTimer = null;

    function toast(message, type) {
        var el = document.getElementById('spToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 4000);
    }

    function open(id) { document.getElementById(id).classList.add('open'); }

    function close(id) { document.getElementById(id).classList.remove('open'); }

    function statCard(icon, label, value, cls) {
        return '<div class="ph-stat ' + cls + '"><i class="fas ' + icon + ' ph-stat-icon"></i>' +
            '<div><h3>' + value + '</h3><p>' + esc(label) + '</p></div></div>';
    }

    function profileFor(staffId) {
        return window.salaryState.profiles.find(function (p) {
            return String(p.staffId).toLowerCase() === String(staffId).toLowerCase();
        });
    }

    function statusBadge(status) { return P.statusBadge(status); }

    // ─── TABS ────────────────────────────────────────────────────────────────
    function showTab(tab) {
        activeTab = tab;
        document.querySelectorAll('.sp-tab').forEach(function (b) {
            var on = b.dataset.tab === tab;
            b.classList.toggle('fc-btn-primary', on);
            b.classList.toggle('fc-btn-secondary', !on);
            b.setAttribute('aria-selected', on ? 'true' : 'false');
        });
        document.getElementById('spProfiles').style.display = tab === 'profiles' ? '' : 'none';
        document.getElementById('spRuns').style.display = tab === 'runs' ? '' : 'none';
        document.getElementById('spSettings').style.display = tab === 'settings' ? '' : 'none';
    }

    // ─── PROFILES ────────────────────────────────────────────────────────────
    function renderProfiles() {
        var s = window.salaryState;
        var withProfile = s.staff.filter(function (st) { return profileFor(st.staffId); });
        var activeBasic = s.profiles.filter(function (p) { return p.active; })
            .reduce(function (t, p) { return t + (p.basicSalary || 0); }, 0);
        document.getElementById('spProfileStats').innerHTML = [
            statCard('fa-users', 'Staff in HR', s.staff.length, 'ph-stat-blue'),
            statCard('fa-id-card', 'With salary profile', withProfile.length, 'ph-stat-green'),
            statCard('fa-user-clock', 'Without a profile', s.staff.length - withProfile.length, 'ph-stat-amber'),
            statCard('fa-coins', 'Monthly basic (active)', money(activeBasic), 'ph-stat-navy'),
        ].join('');

        var q = (document.getElementById('spProfileSearch').value || '').toLowerCase();
        var f = document.getElementById('spProfileFilter').value;
        var rows = s.staff.filter(function (st) {
            var p = profileFor(st.staffId);
            if (q && (String(st.staffName) + ' ' + st.staffId).toLowerCase().indexOf(q) < 0) return false;
            if (f === 'none') return !p;
            if (f === 'active') return p && p.active;
            if (f === 'inactive') return p && !p.active;
            return true;
        });

        var tbody = document.querySelector('#spProfileTable tbody');
        if (!s.staff.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="fc-empty">No staff found in HR. Add staff under Human Resource first.</td></tr>';
            return;
        }
        if (!rows.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="fc-empty">No staff match your filters.</td></tr>';
            return;
        }
        tbody.innerHTML = rows.map(function (st) {
            var p = profileFor(st.staffId);
            var status = !p ? '<span class="badge badge-warning">No profile</span>'
                : p.active ? '<span class="badge badge-success">Active</span>'
                    : '<span class="badge badge-danger">Inactive</span>';
            return '<tr>' +
                '<td>' + esc(st.staffId) + '</td>' +
                '<td>' + esc(st.staffName) + '</td>' +
                '<td>' + esc(st.designation || '—') + '</td>' +
                '<td class="fc-money">' + (p ? money(p.basicSalary) : '—') + '</td>' +
                '<td>' + (p ? esc(METHOD_LABEL[p.paymentMethod] || p.paymentMethod) : '—') + '</td>' +
                '<td>' + status + '</td>' +
                '<td><button class="fc-btn fc-btn-sm ' + (p ? 'fc-btn-secondary' : 'fc-btn-primary') + ' sp-edit" data-staff="' + esc(st.staffId) + '">' +
                (p ? '<i class="fas fa-edit"></i> Edit' : '<i class="fas fa-plus"></i> Set up') + '</button></td>' +
                '</tr>';
        }).join('');
    }

    function addItemRow(item) {
        item = item || {};
        var row = document.createElement('div');
        row.className = 'sal-item-row';
        row.innerHTML =
            '<input type="text" class="sal-item-name" placeholder="e.g. Transport allowance" aria-label="Name">' +
            '<select class="sal-item-type" aria-label="Type"><option value="ALLOWANCE">Allowance</option><option value="DEDUCTION">Deduction</option></select>' +
            '<input type="number" class="sal-item-amount" placeholder="Amount (GH₵)" min="0" step="0.01" aria-label="Amount">' +
            '<label class="sal-pct-label"><input type="checkbox" class="sal-item-pct"> % of basic</label>' +
            '<input type="number" class="sal-item-pct-rate" placeholder="Rate %" min="0" max="100" step="0.01" aria-label="Percentage" style="display:none">' +
            '<button class="fc-btn-icon sal-item-remove" type="button" title="Remove" aria-label="Remove"><i class="fas fa-trash"></i></button>';
        row.querySelector('.sal-item-name').value = item.itemName || '';
        row.querySelector('.sal-item-type').value = item.itemType || 'ALLOWANCE';
        var pct = row.querySelector('.sal-item-pct');
        pct.checked = !!item.isPercentage;
        row.querySelector('.sal-item-amount').value = item.isPercentage ? '' : (item.amount != null ? item.amount : '');
        row.querySelector('.sal-item-pct-rate').value = item.isPercentage ? (item.percentageRate || '') : '';
        function syncPct() {
            row.querySelector('.sal-item-pct-rate').style.display = pct.checked ? '' : 'none';
            row.querySelector('.sal-item-amount').style.display = pct.checked ? 'none' : '';
        }
        pct.addEventListener('change', syncPct);
        syncPct();
        row.querySelector('.sal-item-remove').addEventListener('click', function () { row.remove(); });
        document.getElementById('spItems').appendChild(row);
    }

    function syncMethodFields() {
        var m = document.getElementById('spMethod').value;
        document.getElementById('spBankFields').style.display = m === 'BANK' ? '' : 'none';
        document.getElementById('spMomoFields').style.display = m === 'MOMO' ? '' : 'none';
    }

    function openProfile(staffId) {
        var st = window.salaryState.staff.find(function (x) { return x.staffId === staffId; });
        if (!st) return;
        editingStaff = st;
        var p = profileFor(staffId) || {};
        document.getElementById('spProfileTitle').textContent = 'Salary profile — ' + st.staffName;
        document.getElementById('spProfileSub').textContent = st.staffId + (st.designation ? ' · ' + st.designation : '') +
            (p.updatedBy ? ' · Last updated by ' + p.updatedBy + ' on ' + P.date(p.updatedAt) : '');
        document.getElementById('spBasic').value = p.basicSalary || '';
        document.getElementById('spMethod').value = p.paymentMethod || 'BANK';
        document.getElementById('spBankName').value = p.bankName || '';
        document.getElementById('spBankBranch').value = p.bankBranch || '';
        document.getElementById('spAccountName').value = p.accountName || st.staffName || '';
        document.getElementById('spAccountNumber').value = p.accountNumber || '';
        document.getElementById('spMomoNumber').value = p.momoNumber || '';
        document.getElementById('spActive').checked = p.active !== false;
        document.getElementById('spItems').innerHTML = '';
        (p.items || []).forEach(addItemRow);
        syncMethodFields();
        open('spProfileModal');
        document.getElementById('spBasic').focus();
    }

    function saveProfile() {
        var items = Array.prototype.map.call(document.querySelectorAll('#spItems .sal-item-row'), function (row) {
            var pct = row.querySelector('.sal-item-pct').checked;
            return {
                itemName: row.querySelector('.sal-item-name').value.trim(),
                itemType: row.querySelector('.sal-item-type').value,
                isPercentage: pct,
                amount: pct ? 0 : parseFloat(row.querySelector('.sal-item-amount').value) || 0,
                percentageRate: pct ? parseFloat(row.querySelector('.sal-item-pct-rate').value) || 0 : null,
            };
        }).filter(function (i) { return i.itemName; });

        var profile = {
            staffId: editingStaff.staffId,
            basicSalary: parseFloat(document.getElementById('spBasic').value) || 0,
            paymentMethod: document.getElementById('spMethod').value,
            bankName: document.getElementById('spBankName').value.trim(),
            bankBranch: document.getElementById('spBankBranch').value.trim(),
            accountName: document.getElementById('spAccountName').value.trim(),
            accountNumber: document.getElementById('spAccountNumber').value.trim(),
            momoNumber: document.getElementById('spMomoNumber').value.trim(),
            active: document.getElementById('spActive').checked,
            items: items,
        };
        if (profile.basicSalary <= 0) {
            toast('Enter a basic salary above zero.', 'warning');
            return;
        }

        var btn = document.getElementById('spSaveProfileBtn');
        btn.disabled = true;
        window.salarySaveProfile(profile).then(function () {
            close('spProfileModal');
            toast('Salary profile saved for ' + editingStaff.staffName + '.', 'success');
            renderProfiles();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () { btn.disabled = false; });
    }

    // ─── RUNS ────────────────────────────────────────────────────────────────
    function refreshPeriodFilter() {
        var select = document.getElementById('spRunPeriod');
        var current = select.value;
        var periods = [];
        window.salaryState.runs.forEach(function (r) {
            if (r.payPeriod && periods.indexOf(r.payPeriod) < 0) periods.push(r.payPeriod);
        });
        periods.sort(function (a, b) { return new Date('1 ' + b) - new Date('1 ' + a); });
        select.innerHTML = '<option value="">All months</option>' + options(periods, current);
    }

    function filteredRuns() {
        var year = document.getElementById('spRunYear').value;
        var period = document.getElementById('spRunPeriod').value;
        var status = document.getElementById('spRunStatus').value;
        return window.salaryState.runs.filter(function (r) {
            return (!year || r.academicYear === year) && (!period || r.payPeriod === period) && (!status || r.status === status);
        }).sort(function (a, b) {
            return new Date('1 ' + b.payPeriod) - new Date('1 ' + a.payPeriod) || String(a.staffName).localeCompare(String(b.staffName));
        });
    }

    function renderRuns() {
        refreshPeriodFilter();
        var runs = filteredRuns();
        var sum = function (f) { return runs.reduce(function (t, r) { return t + (r[f] || 0); }, 0); };
        var gross = runs.reduce(function (t, r) { return t + (r.grossPay != null ? r.grossPay : (r.basicSalary || 0) + (r.totalAllowances || 0)); }, 0);
        document.getElementById('spRunStats').innerHTML = [
            statCard('fa-list', 'Runs', runs.length, 'ph-stat-blue'),
            statCard('fa-coins', 'Gross pay', money(gross), 'ph-stat-navy'),
            statCard('fa-minus-circle', 'Deductions', money(sum('totalDeductions')), 'ph-stat-amber'),
            statCard('fa-wallet', 'Net pay', money(sum('netSalary')), 'ph-stat-green'),
        ].join('');

        var tbody = document.querySelector('#spRunTable tbody');
        if (!runs.length) {
            tbody.innerHTML = '<tr><td colspan="9" class="fc-empty">No payroll runs yet. Use <strong>Run payroll</strong> to create this month\'s.</td></tr>';
        } else {
            tbody.innerHTML = runs.map(function (r) {
                var g = r.grossPay != null ? r.grossPay : (r.basicSalary || 0) + (r.totalAllowances || 0);
                return '<tr>' +
                    '<td>' + (r.status === 'PENDING' ? '<input type="checkbox" class="sp-run-check" value="' + r.salaryId + '" aria-label="Select ' + esc(r.staffName) + '">' : '') + '</td>' +
                    '<td>' + esc(r.staffName || r.staffId) + '<br><small style="color:#6b7280">' + esc(r.staffId) + '</small></td>' +
                    '<td>' + esc(r.payPeriod) + '<br><small style="color:#6b7280">' + esc(r.academicYear || '') + '</small></td>' +
                    '<td class="fc-money">' + money(g) + '</td>' +
                    '<td class="fc-money">' + money(r.totalDeductions) + '</td>' +
                    '<td class="fc-money"><strong>' + money(r.netSalary) + '</strong></td>' +
                    '<td>' + statusBadge(r.status) + '</td>' +
                    '<td>' + esc(r.createdBy || '—') + '</td>' +
                    '<td><button class="fc-btn-icon sp-view" data-id="' + r.salaryId + '" title="View details"><i class="fas fa-eye"></i></button></td>' +
                    '</tr>';
            }).join('');
        }
        document.getElementById('spRunAll').checked = false;
        syncDeleteButton();
    }

    function selectedPending() {
        return Array.prototype.map.call(document.querySelectorAll('.sp-run-check:checked'), function (c) { return Number(c.value); });
    }

    function syncDeleteButton() {
        var n = selectedPending().length;
        var btn = document.getElementById('spDeleteBtn');
        btn.disabled = n === 0;
        btn.innerHTML = '<i class="fas fa-trash"></i> Remove ' + (n ? n + ' ' : 'selected ') + 'pending run' + (n === 1 ? '' : 's');
    }

    function deleteSelected() {
        var ids = selectedPending();
        if (!ids.length) return;
        if (!confirm('Remove ' + ids.length + ' pending run' + (ids.length === 1 ? '' : 's') + '? You can run payroll again for those staff afterwards.')) return;
        window.salaryDeletePending(ids).then(function (res) {
            toast((res.updated || []).length + ' pending run(s) removed.', 'success');
            return window.salaryFetchRuns({});
        }).then(renderRuns).catch(function (e) { toast(P.errorMessage(e), 'error'); });
    }

    function showRun(id) {
        var r = window.salaryState.runs.find(function (x) { return x.salaryId === id; });
        if (!r) return;
        document.getElementById('spDetailTitle').textContent = (r.staffName || r.staffId) + ' — ' + r.payPeriod;
        document.getElementById('spDetailBody').innerHTML = P.runDetailHtml(r);
        open('spDetailModal');
    }

    // ─── RUN PAYROLL ─────────────────────────────────────────────────────────
    function runPeriod() {
        var month = Number(document.getElementById('spRunMonth').value);
        var year = Number(document.getElementById('spRunCalYear').value);
        return {period: P.period(month, year), month: month, year: year};
    }

    function updateRunInfo(syncAcademicYear) {
        var p = runPeriod();
        if (syncAcademicYear) document.getElementById('spRunAcademicYear').value = P.academicYearFor(p.month, p.year);
        var active = window.salaryState.profiles.filter(function (x) { return x.active; }).length;
        var already = window.salaryState.runs.filter(function (r) { return r.payPeriod === p.period; }).length;
        document.getElementById('spRunInfo').innerHTML =
            'Creates a <strong>pending</strong> run for <strong>' + p.period + '</strong> for each active salary profile (' + active + ').' +
            (already ? ' ' + already + ' staff already have a run for this month and will be skipped.' : '') +
            ' SSNIT and income tax are calculated automatically. Runs then go to <strong>Salary Approvals</strong>.';
    }

    function openRunModal() {
        document.getElementById('spRunBody').style.display = '';
        document.getElementById('spRunResult').style.display = 'none';
        document.getElementById('spRunConfirm').style.display = '';
        document.getElementById('spRunCancel').textContent = 'Cancel';
        updateRunInfo(false);
        open('spRunModal');
    }

    function confirmRun() {
        var p = runPeriod();
        var academicYear = document.getElementById('spRunAcademicYear').value;
        var btn = document.getElementById('spRunConfirm');
        btn.disabled = true;
        window.salaryRunPayroll(academicYear, p.period).then(function (res) {
            var created = res.updated || [];
            var skipped = res.skipped || [];
            document.getElementById('spRunBody').style.display = 'none';
            var result = document.getElementById('spRunResult');
            result.style.display = '';
            result.innerHTML =
                '<p style="margin:0 0 10px"><strong>' + created.length + '</strong> run' + (created.length === 1 ? '' : 's') +
                ' created for ' + esc(p.period) + ', waiting for approval.</p>' +
                (skipped.length
                    ? '<p style="margin:0 0 6px;color:#374151">' + skipped.length + ' staff skipped:</p>' +
                    '<div class="ph-table-wrap" style="max-height:260px;overflow-y:auto"><table class="fc-table"><thead><tr><th>Staff</th><th>Reason</th></tr></thead><tbody>' +
                    skipped.map(function (s) {
                        return '<tr><td>' + esc(s.staffName || s.staffId || '—') + '</td><td>' + esc(s.reason) + '</td></tr>';
                    }).join('') + '</tbody></table></div>'
                    : '');
            document.getElementById('spRunConfirm').style.display = 'none';
            document.getElementById('spRunCancel').textContent = 'Done';
            return window.salaryFetchRuns({});
        }).then(function () {
            // Show the month just run.
            refreshPeriodFilter();
            document.getElementById('spRunPeriod').value = p.period;
            renderRuns();
            showTab('runs');
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () { btn.disabled = false; });
    }

    // ─── SETTINGS ────────────────────────────────────────────────────────────
    function renderSettings() {
        var s = window.salaryState.settings || {};
        document.getElementById('spSsnitEmp').value = s.ssnitEmployeeRate != null ? s.ssnitEmployeeRate : 5.5;
        document.getElementById('spSsnitEmr').value = s.ssnitEmployerRate != null ? s.ssnitEmployerRate : 13;
        var bands = [];
        try { bands = s.incomeTaxBands ? JSON.parse(s.incomeTaxBands) : []; } catch (ignored) { bands = []; }
        renderBands(bands.length ? bands : [{min: 0, max: null, rate: 0}]);
    }

    function renderBands(bands) {
        var tbody = document.querySelector('#spBandTable tbody');
        tbody.innerHTML = '';
        bands.forEach(function (b) { addBandRow(b.max, b.rate); });
        syncBandStarts();
    }

    function addBandRow(max, rate) {
        var tbody = document.querySelector('#spBandTable tbody');
        var tr = document.createElement('tr');
        tr.innerHTML =
            '<td class="sp-band-from fc-money"></td>' +
            '<td><input type="number" class="sp-band-to" min="0" step="0.01" placeholder="No limit" aria-label="Up to"></td>' +
            '<td><input type="number" class="sp-band-rate" min="0" max="100" step="0.01" aria-label="Rate"></td>' +
            '<td><button class="fc-btn-icon sp-band-remove" type="button" title="Remove band" aria-label="Remove band"><i class="fas fa-trash"></i></button></td>';
        tr.querySelector('.sp-band-to').value = max == null ? '' : max;
        tr.querySelector('.sp-band-rate').value = rate == null ? '' : rate;
        tr.querySelector('.sp-band-to').addEventListener('input', syncBandStarts);
        tr.querySelector('.sp-band-remove').addEventListener('click', function () {
            tr.remove();
            syncBandStarts();
        });
        tbody.appendChild(tr);
    }

    /** Each band starts where the one above ends - shown, not typed, so gaps can't happen. */
    function syncBandStarts() {
        var from = 0;
        document.querySelectorAll('#spBandTable tbody tr').forEach(function (tr) {
            tr.querySelector('.sp-band-from').textContent = money(from);
            var to = parseFloat(tr.querySelector('.sp-band-to').value);
            if (!isNaN(to)) from = to;
        });
    }

    function readBands() {
        var bands = [], from = 0, error = null;
        var rows = document.querySelectorAll('#spBandTable tbody tr');
        rows.forEach(function (tr, i) {
            var toText = tr.querySelector('.sp-band-to').value.trim();
            var rate = parseFloat(tr.querySelector('.sp-band-rate').value);
            var last = i === rows.length - 1;
            var to = toText === '' ? null : parseFloat(toText);
            if (isNaN(rate) || rate < 0 || rate > 100) error = error || 'Every tax band needs a rate from 0 to 100%.';
            if (!last && to == null) error = error || 'Only the last tax band can be left without an "Up to" amount.';
            if (last && to != null) error = error || 'Leave the last tax band\'s "Up to" empty, so all income above it is taxed.';
            if (to != null && to <= from) error = error || 'Each band\'s "Up to" must be higher than where it starts.';
            bands.push({min: from, max: to, rate: rate});
            if (to != null) from = to;
        });
        if (!bands.length) error = 'Add at least one tax band.';
        return {bands: bands, error: error};
    }

    function saveSettings() {
        var read = readBands();
        if (read.error) {
            toast(read.error, 'warning');
            return;
        }
        var btn = document.getElementById('spSaveSettingsBtn');
        btn.disabled = true;
        window.salarySaveSettings({
            ssnitEmployeeRate: parseFloat(document.getElementById('spSsnitEmp').value) || 0,
            ssnitEmployerRate: parseFloat(document.getElementById('spSsnitEmr').value) || 0,
            incomeTaxBands: JSON.stringify(read.bands),
        }).then(function () {
            toast('Payroll settings saved.', 'success');
            renderSettings();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () { btn.disabled = false; });
    }

    // ─── WIRING ──────────────────────────────────────────────────────────────
    function renderAll() {
        var s = window.salaryState;
        var err = document.getElementById('spLoadError');
        err.style.display = s.loadError ? '' : 'none';
        err.textContent = s.loadError || '';
        renderProfiles();
        renderRuns();
        renderSettings();
    }

    function wireEvents() {
        document.querySelectorAll('.sp-tab').forEach(function (b) {
            b.addEventListener('click', function () { showTab(this.dataset.tab); });
        });
        document.querySelectorAll('[data-close]').forEach(function (b) {
            b.addEventListener('click', function () { close(this.dataset.close); });
        });
        ['spProfileModal', 'spRunModal', 'spDetailModal'].forEach(function (id) {
            document.getElementById(id).addEventListener('click', function (e) { if (e.target === this) close(id); });
        });

        document.getElementById('spRefreshBtn').addEventListener('click', function () {
            window.salaryLoad().then(renderAll);
        });
        document.getElementById('spRunBtn').addEventListener('click', openRunModal);
        document.getElementById('spRunConfirm').addEventListener('click', confirmRun);
        document.getElementById('spRunMonth').addEventListener('change', function () { updateRunInfo(true); });
        document.getElementById('spRunCalYear').addEventListener('change', function () { updateRunInfo(true); });

        document.getElementById('spProfileSearch').addEventListener('input', renderProfiles);
        document.getElementById('spProfileFilter').addEventListener('change', renderProfiles);
        document.querySelector('#spProfileTable tbody').addEventListener('click', function (e) {
            var btn = e.target.closest('.sp-edit');
            if (btn) openProfile(btn.dataset.staff);
        });
        document.getElementById('spMethod').addEventListener('change', syncMethodFields);
        document.getElementById('spAddItemBtn').addEventListener('click', function () { addItemRow(); });
        document.getElementById('spSaveProfileBtn').addEventListener('click', saveProfile);

        ['spRunYear', 'spRunPeriod', 'spRunStatus'].forEach(function (id) {
            document.getElementById(id).addEventListener('change', renderRuns);
        });
        var runTable = document.querySelector('#spRunTable tbody');
        runTable.addEventListener('change', function (e) { if (e.target.classList.contains('sp-run-check')) syncDeleteButton(); });
        runTable.addEventListener('click', function (e) {
            var btn = e.target.closest('.sp-view');
            if (btn) showRun(Number(btn.dataset.id));
        });
        document.getElementById('spRunAll').addEventListener('change', function () {
            var on = this.checked;
            document.querySelectorAll('.sp-run-check').forEach(function (c) { c.checked = on; });
            syncDeleteButton();
        });
        document.getElementById('spDeleteBtn').addEventListener('click', deleteSelected);

        document.getElementById('spAddBandBtn').addEventListener('click', function () {
            // The new band goes last; the previous last band needs an "Up to" now.
            addBandRow(null, '');
            syncBandStarts();
        });
        document.getElementById('spGhanaBandsBtn').addEventListener('click', function () {
            renderBands(GHANA_BANDS);
            toast('Ghana PAYE bands filled in. Check them, then save.', 'info');
        });
        document.getElementById('spSaveSettingsBtn').addEventListener('click', saveSettings);
    }

    // ─── INIT ────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        showTab(activeTab);
        document.querySelector('#spProfileTable tbody').innerHTML = '<tr><td colspan="7" class="fc-empty">Loading staff…</td></tr>';
        window.salaryLoad().then(renderAll);
    }

    var currentScript = document.currentScript;

    function loadScript(src, onload) {
        var s = document.createElement('script');
        s.src = src;
        s.setAttribute('data-dynamic', 'true');
        s.onload = onload;
        s.onerror = function () { console.error('[financeSalary] Could not load ' + src); };
        document.body.appendChild(s);
    }

    // Loads the shared payroll helper if the page loader didn't (it normally
    // does - see common.js), then the data layer, then starts the page.
    function loadScripts() {
        var base = currentScript && currentScript.src
            ? currentScript.src.substring(0, currentScript.src.lastIndexOf('/') + 1)
            : 'scripts/subscripts/';
        var withShared = function () {
            P = window.payrollShared;
            esc = P.escape;
            money = P.money;
            loadScript(base + '../_financeSalary.js', init);
        };
        if (window.payrollShared) withShared();
        else loadScript(base + '../_payrollShared.js', withShared);
    }

    loadScripts();
})();
