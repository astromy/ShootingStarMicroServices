/**
 * financeSalary.js  —  UI renderer for Salary / Payroll Management.
 *
 * Features:
 *   - Payroll run list with PENDING / APPROVED / PAID status management
 *   - Create individual salary run with allowances / deductions
 *   - Batch payroll run for all staff in a pay period
 *   - Approve and Mark Paid transitions with confirmation
 *   - Summary stats strip
 *   - Settings panel (SSNIT rates, default allowances)
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financeSalary') !== -1 && tags[i].src.indexOf('Payslip') === -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    (function () {
        if (document.getElementById('salaryCSS')) return;
        var l = document.createElement('link');
        l.id = 'salaryCSS';
        l.rel = 'stylesheet';
        l.href = _base + '../../styles/style.css';
        document.head.appendChild(l);
    })();

    var _currentRun = null; // run open in detail modal

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        var s = window.salaryState;

        var yearOpts = '<option value="all">All Years</option>' +
            s.academicYears.map(function (y) {
                return '<option ' + (y === s.selectedYear ? 'selected' : '') + '>' + y + '</option>';
            }).join('');

        var statusOpts = ['all', 'PENDING', 'APPROVED', 'PAID'].map(function (v) {
            return '<option value="' + v + '">' + (v === 'all' ? 'All Statuses' : v) + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = [
            '<div class="sal-page">',

            '<header class="fc-header">',
            '<div><h1><i class="fas fa-money-check-alt"></i> Payroll Management</h1>',
            '<p>Create, approve and process staff salary runs</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="salSettingsBtn"><i class="fas fa-cog"></i> Settings</button>',
            '<button class="fc-btn fc-btn-primary"   id="salCreateBtn"><i class="fas fa-plus"></i> New Run</button>',
            '</div>',
            '</header>',

            // Stats
            '<div class="ph-stats">',
            '<div class="ph-stat ph-stat-blue"><div class="ph-stat-icon"><i class="fas fa-users"></i></div><div><h3 id="salCount">0</h3><p>Total Runs</p></div></div>',
            '<div class="ph-stat ph-stat-green"><div class="ph-stat-icon"><i class="fas fa-coins"></i></div><div><h3 id="salNet">GH₵ 0</h3><p>Net Payroll</p></div></div>',
            '<div class="ph-stat ph-stat-amber"><div class="ph-stat-icon"><i class="fas fa-clock"></i></div><div><h3 id="salPending">0</h3><p>Pending</p></div></div>',
            '<div class="ph-stat ph-stat-navy"><div class="ph-stat-icon"><i class="fas fa-check-circle"></i></div><div><h3 id="salPaid">0</h3><p>Paid</p></div></div>',
            '</div>',

            // Controls
            '<div class="ph-controls">',
            '<div class="ph-search-wrap"><i class="fas fa-search"></i><input type="text" id="salSearch" placeholder="Staff ID or name…"></div>',
            '<select id="salYearSel">' + yearOpts + '</select>',
            '<select id="salPeriodSel"><option value="all">All Periods</option></select>',
            '<select id="salStatusSel">' + statusOpts + '</select>',
            '<button class="fc-btn fc-btn-primary" id="salFilterBtn"><i class="fas fa-filter"></i> Filter</button>',
            '</div>',

            // Table
            '<div class="ph-table-wrap">',
            '<table class="fc-table" id="salTable">',
            '<thead><tr>',
            '<th>Staff ID</th><th>Name</th><th>Designation</th>',
            '<th>Period</th><th>Basic</th><th>Allowances</th>',
            '<th>Deductions</th><th>Net Pay</th><th>Status</th><th></th>',
            '</tr></thead>',
            '<tbody id="salTableBody"><tr><td colspan="10" class="fc-empty"><i class="fas fa-circle-notch fa-spin"></i> Loading…</td></tr></tbody>',
            '</table>',
            '</div>',

            // ── CREATE / DETAIL MODAL ────────────────────────────────────────
            '<div class="fc-modal-overlay" id="salRunModal">',
            '<div class="fc-modal fc-modal-lg">',
            '<div class="fc-modal-head"><h5 id="salRunModalTitle"><i class="fas fa-money-check-alt"></i> New Salary Run</h5>',
            '<button class="fc-modal-close" id="salRunClose">&times;</button></div>',
            '<div class="fc-modal-body" id="salRunBody"></div>',
            '<div class="fc-modal-foot" id="salRunFoot"></div>',
            '</div></div>',

            // ── SETTINGS MODAL ───────────────────────────────────────────────
            '<div class="fc-modal-overlay" id="salSettingsModal">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head"><h5><i class="fas fa-cog"></i> Payroll Settings</h5>',
            '<button class="fc-modal-close" id="salSettingsClose">&times;</button></div>',
            '<div class="fc-modal-body" id="salSettingsBody"></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="salSettingsCancelBtn">Cancel</button>',
            '<button class="fc-btn fc-btn-primary"   id="salSettingsSaveBtn"><i class="fas fa-save"></i> Save Settings</button>',
            '</div>',
            '</div></div>',

            // ── CONFIRM MODAL ────────────────────────────────────────────────
            '<div class="fc-modal-overlay" id="salConfirmModal">',
            '<div class="fc-modal" style="max-width:380px">',
            '<div class="fc-modal-head"><h5 id="salConfirmTitle">Confirm</h5>',
            '<button class="fc-modal-close" id="salConfirmClose">&times;</button></div>',
            '<div class="fc-modal-body" id="salConfirmBody"></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="salConfirmCancelBtn">Cancel</button>',
            '<button class="fc-btn" id="salConfirmOkBtn">Confirm</button>',
            '</div>',
            '</div></div>',

            '<div class="fc-toast" id="salToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="salYear"></span> | Payroll</footer>',
            '</div>',
        ].join('');

        document.getElementById('salYear').textContent = new Date().getFullYear();
        populatePeriods();
    }

    function populatePeriods() {
        var sel = document.getElementById('salPeriodSel');
        if (!sel) return;
        sel.innerHTML = '<option value="all">All Periods</option>' +
            window.salaryState.payPeriods.map(function (p) {
                return '<option value="' + p.value + '">' + p.label + '</option>';
            }).join('');
    }

    // ── RENDER TABLE ─────────────────────────────────────────────────────────
    function renderTable(runs) {
        var fmt = window.salaryFmt;
        var body = document.getElementById('salTableBody');
        if (!body) return;

        if (!runs || !runs.length) {
            body.innerHTML = '<tr><td colspan="10" class="fc-empty"><i class="fas fa-inbox"></i> No salary runs found</td></tr>';
            return;
        }

        body.innerHTML = runs.map(function (r) {
            var statusMeta = {
                PENDING: {cls: 'sal-pending', label: 'Pending'},
                APPROVED: {cls: 'sal-approved', label: 'Approved'},
                PAID: {cls: 'sal-paid', label: 'Paid'},
            };
            var sm = statusMeta[r.status] || {cls: '', label: r.status};

            return '<tr>' +
                '<td>' + (r.staffId || '—') + '</td>' +
                '<td>' + (r.staffName || '—') + '</td>' +
                '<td>' + (r.designation || '—') + '</td>' +
                '<td>' + (r.payPeriod || '—') + '</td>' +
                '<td class="fc-money">' + fmt.money(r.basicSalary) + '</td>' +
                '<td class="fc-money fc-paid">' + fmt.money(r.totalAllowances) + '</td>' +
                '<td class="fc-money fc-owing">' + fmt.money(r.totalDeductions) + '</td>' +
                '<td class="fc-money"><strong>' + fmt.money(r.netSalary) + '</strong></td>' +
                '<td><span class="sal-status ' + sm.cls + '">' + sm.label + '</span></td>' +
                '<td><button class="fc-btn-icon sal-detail-btn" data-id="' + r.salaryId + '" title="View / Act"><i class="fas fa-ellipsis-v"></i></button></td>' +
                '</tr>';
        }).join('');

        body.querySelectorAll('.sal-detail-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var id = parseInt(this.dataset.id);
                var run = window.salaryState.allRuns.find(function (r) {
                    return r.salaryId === id;
                });
                if (run) openRunModal(run, 'view');
            });
        });
    }

    // ── RENDER STATS ─────────────────────────────────────────────────────────
    function renderStats(runs) {
        var s = window.salaryStats(runs);
        var fmt = window.salaryFmt;
        setText('salCount', s.count);
        setText('salNet', fmt.money(s.totalNet));
        setText('salPending', s.pending);
        setText('salPaid', s.paid);
    }

    // ── RUN MODAL ────────────────────────────────────────────────────────────
    function openRunModal(run, mode) {
        _currentRun = run;
        var fmt = window.salaryFmt;
        var body = document.getElementById('salRunBody');
        var foot = document.getElementById('salRunFoot');
        var title = document.getElementById('salRunModalTitle');

        if (mode === 'create') {
            title.innerHTML = '<i class="fas fa-plus-circle"></i> New Salary Run';
            body.innerHTML = buildCreateForm();
            foot.innerHTML = '<button class="fc-btn fc-btn-secondary" id="salRunCancelBtn">Cancel</button>' +
                '<button class="fc-btn fc-btn-primary" id="salRunSaveBtn"><i class="fas fa-save"></i> Create Run</button>';
            document.getElementById('salRunSaveBtn').addEventListener('click', handleCreate);
            document.getElementById('salRunCancelBtn').addEventListener('click', closeRunModal);
        } else {
            title.innerHTML = '<i class="fas fa-money-check-alt"></i> Salary Run — ' + (run.staffName || run.staffId);
            body.innerHTML = buildDetailView(run);

            var actions = '';
            if (run.status === 'PENDING') actions += '<button class="fc-btn fc-btn-success" id="salApproveBtn"><i class="fas fa-check"></i> Approve</button>';
            if (run.status === 'APPROVED') actions += '<button class="fc-btn fc-btn-primary" id="salMarkPaidBtn"><i class="fas fa-money-bill"></i> Mark as Paid</button>';
            foot.innerHTML = '<button class="fc-btn fc-btn-secondary" id="salRunCancelBtn">Close</button>' + actions;

            document.getElementById('salRunCancelBtn').addEventListener('click', closeRunModal);
            var approveBtn = document.getElementById('salApproveBtn');
            var paidBtn = document.getElementById('salMarkPaidBtn');
            if (approveBtn) approveBtn.addEventListener('click', function () {
                confirm_('Approve Run', 'Approve salary run for <strong>' + run.staffName + '</strong>?', 'fc-btn-success', 'Approve', async function () {
                    await doApprove(run.salaryId);
                });
            });
            if (paidBtn) paidBtn.addEventListener('click', function () {
                confirm_('Mark as Paid', 'Mark this run as PAID for <strong>' + run.staffName + '</strong>?', 'fc-btn-primary', 'Mark Paid', async function () {
                    await doMarkPaid(run.salaryId);
                });
            });
        }

        document.getElementById('salRunModal').classList.add('open');
    }

    function closeRunModal() {
        document.getElementById('salRunModal').classList.remove('open');
    }

    function buildCreateForm() {
        var yearOpts = window.salaryState.academicYears.map(function (y) {
            return '<option ' + (y === window.salaryState.selectedYear ? 'selected' : '') + '>' + y + '</option>';
        }).join('');
        var periodOpts = window.salaryState.payPeriods.map(function (p) {
            return '<option value="' + p.value + '">' + p.label + '</option>';
        }).join('');

        return [
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Staff ID <span class="req">*</span></label><input type="text" id="cStaffId" placeholder="STF-001"></div>',
            '<div class="fc-field"><label>Staff Name</label><input type="text" id="cStaffName" placeholder="Full name"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Designation</label><input type="text" id="cDesignation" placeholder="e.g. Class Teacher"></div>',
            '<div class="fc-field"><label>Basic Salary (GH₵) <span class="req">*</span></label><input type="number" id="cBasic" min="0" step="0.01" placeholder="0.00"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Academic Year</label><select id="cYear">' + yearOpts + '</select></div>',
            '<div class="fc-field"><label>Pay Period</label><select id="cPeriod">' + periodOpts + '</select></div>',
            '</div>',
            '<div class="fc-field" style="margin-top:12px">',
            '<label>Payment Method</label>',
            '<select id="cPayMethod"><option value="BANK">Bank Transfer</option><option value="MOMO">Mobile Money</option><option value="CASH">Cash</option></select>',
            '</div>',
            '<div class="sal-items-section">',
            '<div class="sal-items-title"><i class="fas fa-list"></i> Allowances & Deductions</div>',
            '<div id="salItemsList"></div>',
            '<button class="fc-btn fc-btn-secondary fc-btn-sm" id="salAddItemBtn" type="button"><i class="fas fa-plus"></i> Add Line Item</button>',
            '</div>',
        ].join('');
    }

    function addItemRow() {
        var list = document.getElementById('salItemsList');
        if (!list) return;
        var idx = list.children.length;
        var row = document.createElement('div');
        row.className = 'sal-item-row';
        row.innerHTML =
            '<input type="text"   class="sal-item-name"    placeholder="Item name">' +
            '<select class="sal-item-type"><option value="ALLOWANCE">Allowance</option><option value="DEDUCTION">Deduction</option></select>' +
            '<input type="number" class="sal-item-amount"  placeholder="Amount" min="0" step="0.01">' +
            '<label class="sal-pct-label"><input type="checkbox" class="sal-item-pct"> %</label>' +
            '<input type="number" class="sal-item-pct-rate" placeholder="Rate %" min="0" max="100" step="0.01" style="display:none">' +
            '<button class="fc-btn-icon sal-item-remove" type="button"><i class="fas fa-trash"></i></button>';
        list.appendChild(row);

        row.querySelector('.sal-item-pct').addEventListener('change', function () {
            row.querySelector('.sal-item-pct-rate').style.display = this.checked ? '' : 'none';
            row.querySelector('.sal-item-amount').style.display = this.checked ? 'none' : '';
        });
        row.querySelector('.sal-item-remove').addEventListener('click', function () {
            list.removeChild(row);
        });
    }

    function collectItems() {
        var rows = document.querySelectorAll('.sal-item-row');
        var items = [];
        rows.forEach(function (row) {
            var isPct = row.querySelector('.sal-item-pct').checked;
            items.push({
                itemName: row.querySelector('.sal-item-name').value.trim(),
                itemType: row.querySelector('.sal-item-type').value,
                amount: isPct ? 0 : parseFloat(row.querySelector('.sal-item-amount').value) || 0,
                isPercentage: isPct,
                percentageRate: isPct ? parseFloat(row.querySelector('.sal-item-pct-rate').value) || 0 : null,
            });
        });
        return items.filter(function (i) {
            return i.itemName;
        });
    }

    async function handleCreate() {
        var staffId = document.getElementById('cStaffId').value.trim();
        var basic = parseFloat(document.getElementById('cBasic').value);
        if (!staffId) {
            toast('Staff ID is required.', 'error');
            return;
        }
        if (!basic || basic <= 0) {
            toast('Enter a valid basic salary.', 'error');
            return;
        }

        var req = {
            staffId: staffId,
            staffName: document.getElementById('cStaffName').value.trim(),
            designation: document.getElementById('cDesignation').value.trim(),
            basicSalary: basic,
            academicYear: document.getElementById('cYear').value,
            payPeriod: document.getElementById('cPeriod').value,
            paymentMethod: document.getElementById('cPayMethod').value,
            salaryItems: collectItems(),
        };
        try {
            await window.salaryCreate(req);
            closeRunModal();
            applyAndRender();
            toast('Salary run created.', 'success');
        } catch (e) {
            toast('Create failed: ' + e.message, 'error');
        }
    }

    function buildDetailView(r) {
        var fmt = window.salaryFmt;
        var itemsHtml = (r.salaryItems && r.salaryItems.length)
            ? r.salaryItems.map(function (i) {
                return '<div class="sal-detail-item ' + (i.itemType === 'ALLOWANCE' ? 'sal-allow' : 'sal-deduct') + '">' +
                    '<span>' + i.itemName + '</span>' +
                    '<strong>' + (i.isPercentage ? i.percentageRate + '%' : fmt.money(i.amount)) + '</strong>' +
                    '</div>';
            }).join('')
            : '<div class="fc-empty">No line items</div>';

        return [
            '<div class="sal-detail-grid">',
            '<div class="sal-detail-row"><span>Staff ID</span><strong>' + r.staffId + '</strong></div>',
            '<div class="sal-detail-row"><span>Name</span><strong>' + (r.staffName || '—') + '</strong></div>',
            '<div class="sal-detail-row"><span>Designation</span><strong>' + (r.designation || '—') + '</strong></div>',
            '<div class="sal-detail-row"><span>Period</span><strong>' + r.payPeriod + ' · ' + r.academicYear + '</strong></div>',
            '<div class="sal-detail-row"><span>Basic Salary</span><strong>' + fmt.money(r.basicSalary) + '</strong></div>',
            '<div class="sal-detail-row"><span>Total Allowances</span><strong class="fc-paid">' + fmt.money(r.totalAllowances) + '</strong></div>',
            '<div class="sal-detail-row"><span>Total Deductions</span><strong class="fc-owing">' + fmt.money(r.totalDeductions) + '</strong></div>',
            '<div class="sal-detail-row sal-detail-net"><span>NET PAY</span><strong>' + fmt.money(r.netSalary) + '</strong></div>',
            r.processedBy ? '<div class="sal-detail-row"><span>Processed By</span><strong>' + r.processedBy + '</strong></div>' : '',
            r.paymentDate ? '<div class="sal-detail-row"><span>Payment Date</span><strong>' + fmt.date(r.paymentDate) + '</strong></div>' : '',
            '</div>',
            '<div class="sal-items-section"><div class="sal-items-title">Line Items</div>' + itemsHtml + '</div>',
        ].join('');
    }

    // ── STATUS ACTIONS ───────────────────────────────────────────────────────
    async function doApprove(id) {
        var by = prompt('Enter approver name:');
        if (!by) return;
        try {
            var r = await window.salaryApprove(id, by);
            closeRunModal();
            applyAndRender();
            toast('Salary run approved.', 'success');
        } catch (e) {
            toast('Approve failed: ' + e.message, 'error');
        }
    }

    async function doMarkPaid(id) {
        var by = prompt('Enter processor name:');
        if (!by) return;
        var ref = prompt('External reference (leave blank if N/A):');
        try {
            await window.salaryMarkPaid(id, by, ref);
            closeRunModal();
            applyAndRender();
            toast('Marked as paid.', 'success');
        } catch (e) {
            toast('Failed: ' + e.message, 'error');
        }
    }

    // ── SETTINGS MODAL ───────────────────────────────────────────────────────
    function openSettings() {
        var s = window.salaryState.settings || {};
        document.getElementById('salSettingsBody').innerHTML = [
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>SSNIT Employee Rate (%)</label><input type="number" id="sSSNITEmp" min="0" max="100" step="0.01" value="' + (s.ssnitEmployeeRate || 5.5) + '"></div>',
            '<div class="fc-field"><label>SSNIT Employer Rate (%)</label><input type="number" id="sSSNITEmr" min="0" max="100" step="0.01" value="' + (s.ssnitEmployerRate || 13.0) + '"></div>',
            '</div>',
            '<div class="fc-field"><label>Income Tax Bands (JSON)</label><textarea id="sTaxBands" class="field-textarea" rows="3" placeholder=\'[{"min":0,"max":4800,"rate":0},{"min":4800,"max":9600,"rate":5}]\'>' + (s.incomeTaxBands || '') + '</textarea></div>',
        ].join('');
        document.getElementById('salSettingsModal').classList.add('open');
    }

    // ── CONFIRM DIALOG ───────────────────────────────────────────────────────
    function confirm_(title, body, btnClass, btnLabel, onConfirm) {
        document.getElementById('salConfirmTitle').textContent = title;
        document.getElementById('salConfirmBody').innerHTML = '<p style="padding:16px 0">' + body + '</p>';
        var okBtn = document.getElementById('salConfirmOkBtn');
        okBtn.className = 'fc-btn ' + btnClass;
        okBtn.textContent = btnLabel;
        var modal = document.getElementById('salConfirmModal');
        modal.classList.add('open');
        var handler = async function () {
            modal.classList.remove('open');
            okBtn.removeEventListener('click', handler);
            await onConfirm();
        };
        okBtn.addEventListener('click', handler);
    }

    // ── FILTER & RENDER ──────────────────────────────────────────────────────
    function applyAndRender() {
        var runs = window.salaryFilter({
            year: document.getElementById('salYearSel')?.value,
            period: document.getElementById('salPeriodSel')?.value,
            status: document.getElementById('salStatusSel')?.value,
            search: document.getElementById('salSearch')?.value,
        });
        renderTable(runs);
        renderStats(runs);
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('salFilterBtn').addEventListener('click', applyAndRender);
        document.getElementById('salSearch').addEventListener('keydown', function (e) {
            if (e.key === 'Enter') applyAndRender();
        });
        document.getElementById('salCreateBtn').addEventListener('click', function () {
            openRunModal(null, 'create');
        });

        // add item row in create form (delegated)
        document.addEventListener('click', function (e) {
            if (e.target.closest('#salAddItemBtn')) addItemRow();
        });

        document.getElementById('salRunClose').addEventListener('click', closeRunModal);
        document.getElementById('salRunModal').addEventListener('click', function (e) {
            if (e.target === this) closeRunModal();
        });

        document.getElementById('salSettingsBtn').addEventListener('click', openSettings);
        document.getElementById('salSettingsClose').addEventListener('click', function () {
            document.getElementById('salSettingsModal').classList.remove('open');
        });
        document.getElementById('salSettingsCancelBtn').addEventListener('click', function () {
            document.getElementById('salSettingsModal').classList.remove('open');
        });
        document.getElementById('salSettingsSaveBtn').addEventListener('click', async function () {
            try {
                await window.salarySaveSettings({
                    ssnitEmployeeRate: parseFloat(document.getElementById('sSSNITEmp').value) || 5.5,
                    ssnitEmployerRate: parseFloat(document.getElementById('sSSNITEmr').value) || 13.0,
                    incomeTaxBands: document.getElementById('sTaxBands').value.trim(),
                });
                document.getElementById('salSettingsModal').classList.remove('open');
                toast('Settings saved.', 'success');
            } catch (e) {
                toast('Save failed: ' + e.message, 'error');
            }
        });

        document.getElementById('salConfirmClose').addEventListener('click', function () {
            document.getElementById('salConfirmModal').classList.remove('open');
        });
        document.getElementById('salConfirmCancelBtn').addEventListener('click', function () {
            document.getElementById('salConfirmModal').classList.remove('open');
        });
    }

    function setText(id, v) {
        var el = document.getElementById(id);
        if (el) el.textContent = v;
    }

    function toast(msg, type) {
        var el = document.getElementById('salToast');
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
        var t = setInterval(function () {
            if (window.salaryState && window.salaryState._loaded) {
                clearInterval(t);
                applyAndRender();
            }
        }, 30);
    }

    function boot() {
        if (window.salaryState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financeSalary.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();
})();