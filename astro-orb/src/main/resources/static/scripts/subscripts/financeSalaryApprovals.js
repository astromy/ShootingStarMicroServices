/**
 * financeSalaryApprovals.js  —  UI for Finance -> Salary Approvals.
 *
 * Lists payroll runs by month and lets an authorised user approve them
 * (PENDING -> APPROVED) and record payment (APPROVED -> PAID), singly or in
 * bulk. The approver / payer is always the signed-in user, recorded by the
 * server. Access is controlled by the "Salary Approvals" permission.
 *
 * Loads _financeSalaryApprovals.js; _payrollShared.js is loaded before this
 * file by common.js.
 */
(function () {
    'use strict';

    // Set once _payrollShared.js is available (see loadScripts below).
    var P, esc, money;

    function buildDOM() {
        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-clipboard-check"></i> Salary Approvals</h1>',
            '<p>Approve payroll runs and record payments. Your name is recorded from your login.</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="saRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '</div>',
            '</header>',

            '<div class="ph-stats" id="saStats"></div>',

            '<div class="ph-controls">',
            '<select id="saPeriod" aria-label="Pay period"><option value="">All months</option></select>',
            '<select id="saStatus" aria-label="Status">',
            '<option value="PENDING">Waiting for approval</option>',
            '<option value="APPROVED">Approved — waiting for payment</option>',
            '<option value="PAID">Paid</option>',
            '<option value="">All</option>',
            '</select>',
            '<button class="fc-btn fc-btn-success" id="saApproveBtn" disabled><i class="fas fa-check"></i> Approve selected</button>',
            '<button class="fc-btn fc-btn-primary" id="saPayBtn" disabled><i class="fas fa-money-bill"></i> Mark selected as paid</button>',
            '</div>',

            '<div class="ph-table-wrap"><table class="fc-table" id="saTable">',
            '<thead><tr><th><input type="checkbox" id="saAll" aria-label="Select all"></th>',
            '<th>Staff</th><th>Period</th><th>Gross</th><th>Deductions</th><th>Net pay</th><th>Paid to</th><th>Status</th><th>Created by</th><th></th></tr></thead>',
            '<tbody></tbody></table></div>',

            // Pay modal
            '<div class="fc-modal-overlay" id="saPayModal">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head"><h5><i class="fas fa-money-bill"></i> Mark as paid</h5>',
            '<button class="fc-modal-close" data-close="saPayModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body">',
            '<p id="saPaySummary" style="margin:0 0 12px"></p>',
            '<div class="fc-field"><label for="saPayRef">Payment reference (optional)</label>',
            '<input type="text" id="saPayRef" placeholder="e.g. bank batch or transfer reference"></div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="saPayModal">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="saPayConfirm"><i class="fas fa-check"></i> Mark as paid</button>',
            '</div></div></div>',

            // Detail modal
            '<div class="fc-modal-overlay" id="saDetailModal">',
            '<div class="fc-modal" style="max-width:600px">',
            '<div class="fc-modal-head"><h5 id="saDetailTitle">Salary run</h5>',
            '<button class="fc-modal-close" data-close="saDetailModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body" id="saDetailBody"></div>',
            '<div class="fc-modal-foot"><button class="fc-btn fc-btn-secondary" data-close="saDetailModal">Close</button></div>',
            '</div></div>',

            '<div class="fc-toast" id="saToast" role="status" aria-live="polite"></div>',
        ].join('') + '</div>';
    }

    var toastTimer = null;

    function toast(message, type) {
        var el = document.getElementById('saToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 4500);
    }

    function open(id) { document.getElementById(id).classList.add('open'); }

    function close(id) { document.getElementById(id).classList.remove('open'); }

    function statCard(icon, label, value, cls) {
        return '<div class="ph-stat ' + cls + '"><i class="fas ' + icon + ' ph-stat-icon"></i>' +
            '<div><h3>' + value + '</h3><p>' + esc(label) + '</p></div></div>';
    }

    function runs() { return window.approvalState.runs; }

    function inPeriod(r) {
        var period = document.getElementById('saPeriod').value;
        return !period || r.payPeriod === period;
    }

    function refreshPeriods() {
        var select = document.getElementById('saPeriod');
        var current = select.value;
        var periods = [];
        runs().forEach(function (r) { if (r.payPeriod && periods.indexOf(r.payPeriod) < 0) periods.push(r.payPeriod); });
        periods.sort(function (a, b) { return new Date('1 ' + b) - new Date('1 ' + a); });
        select.innerHTML = '<option value="">All months</option>' + periods.map(function (p) {
            return '<option value="' + esc(p) + '"' + (p === current ? ' selected' : '') + '>' + esc(p) + '</option>';
        }).join('');
    }

    function render() {
        refreshPeriods();
        var status = document.getElementById('saStatus').value;
        var scoped = runs().filter(inPeriod);
        var count = function (st) { return scoped.filter(function (r) { return r.status === st; }); };
        var net = function (list) { return money(list.reduce(function (t, r) { return t + (r.netSalary || 0); }, 0)); };
        var pending = count('PENDING'), approved = count('APPROVED'), paid = count('PAID');
        document.getElementById('saStats').innerHTML = [
            statCard('fa-hourglass-half', 'Waiting for approval · ' + net(pending), pending.length, 'ph-stat-amber'),
            statCard('fa-check', 'Approved, not yet paid · ' + net(approved), approved.length, 'ph-stat-blue'),
            statCard('fa-wallet', 'Paid · ' + net(paid), paid.length, 'ph-stat-green'),
        ].join('');

        var rows = scoped.filter(function (r) { return !status || r.status === status; })
            .sort(function (a, b) { return String(a.staffName).localeCompare(String(b.staffName)); });
        var tbody = document.querySelector('#saTable tbody');
        var state = window.approvalState;
        if (state.loadError) {
            tbody.innerHTML = '<tr><td colspan="10" class="fc-empty">' + esc(state.loadError) + '</td></tr>';
        } else if (!rows.length) {
            tbody.innerHTML = '<tr><td colspan="10" class="fc-empty">Nothing here. Payroll runs created under Salary Setup appear here for approval.</td></tr>';
        } else {
            tbody.innerHTML = rows.map(function (r) {
                var selectable = r.status === 'PENDING' || r.status === 'APPROVED';
                var gross = r.grossPay != null ? r.grossPay : (r.basicSalary || 0) + (r.totalAllowances || 0);
                var paidTo = r.paymentMethod === 'BANK' ? (r.bankName || 'Bank') : r.paymentMethod === 'MOMO' ? 'Mobile money' : r.paymentMethod === 'CASH' ? 'Cash' : '—';
                return '<tr>' +
                    '<td>' + (selectable ? '<input type="checkbox" class="sa-check" value="' + r.salaryId + '" data-status="' + r.status + '" aria-label="Select ' + esc(r.staffName) + '">' : '') + '</td>' +
                    '<td>' + esc(r.staffName || r.staffId) + '<br><small style="color:#6b7280">' + esc(r.staffId) + '</small></td>' +
                    '<td>' + esc(r.payPeriod) + '</td>' +
                    '<td class="fc-money">' + money(gross) + '</td>' +
                    '<td class="fc-money">' + money(r.totalDeductions) + '</td>' +
                    '<td class="fc-money"><strong>' + money(r.netSalary) + '</strong></td>' +
                    '<td>' + esc(paidTo) + '</td>' +
                    '<td>' + P.statusBadge(r.status) + '</td>' +
                    '<td>' + esc(r.createdBy || '—') + '</td>' +
                    '<td><button class="fc-btn-icon sa-view" data-id="' + r.salaryId + '" title="View details"><i class="fas fa-eye"></i></button></td>' +
                    '</tr>';
            }).join('');
        }
        document.getElementById('saAll').checked = false;
        syncButtons();
    }

    function selected(status) {
        return Array.prototype.filter.call(document.querySelectorAll('.sa-check:checked'), function (c) {
            return !status || c.dataset.status === status;
        }).map(function (c) { return Number(c.value); });
    }

    function syncButtons() {
        var toApprove = selected('PENDING').length;
        var toPay = selected('APPROVED').length;
        var a = document.getElementById('saApproveBtn');
        var p = document.getElementById('saPayBtn');
        a.disabled = toApprove === 0;
        p.disabled = toPay === 0;
        var runs = function (n) { return n + (n === 1 ? ' run' : ' runs'); };
        a.innerHTML = '<i class="fas fa-check"></i> ' + (toApprove ? 'Approve ' + runs(toApprove) : 'Approve selected');
        p.innerHTML = '<i class="fas fa-money-bill"></i> ' + (toPay ? 'Mark ' + runs(toPay) + ' as paid' : 'Mark selected as paid');
    }

    function reportResult(res, verb) {
        var done = (res.updated || []).length;
        var skipped = res.skipped || [];
        var msg = done + ' run' + (done === 1 ? '' : 's') + ' ' + verb + '.';
        if (skipped.length) msg += ' ' + skipped.length + ' skipped: ' + skipped.map(function (s) {
            return (s.staffName || s.staffId || 'run') + ' (' + s.reason + ')';
        }).join('; ');
        toast(msg, skipped.length ? 'warning' : 'success');
    }

    function approveSelected() {
        var ids = selected('PENDING');
        if (!ids.length) return;
        var total = runs().filter(function (r) { return ids.indexOf(r.salaryId) >= 0; })
            .reduce(function (t, r) { return t + (r.netSalary || 0); }, 0);
        if (!confirm('Approve ' + ids.length + ' run' + (ids.length === 1 ? '' : 's') + ' with a total net pay of ' + money(total) + '?')) return;
        var btn = document.getElementById('saApproveBtn');
        btn.disabled = true;
        window.approvalApprove(ids).then(function (res) {
            reportResult(res, 'approved');
            return window.approvalLoad();
        }).then(render).catch(function (e) {
            toast(P.errorMessage(e), 'error');
            syncButtons();
        });
    }

    function openPay() {
        var ids = selected('APPROVED');
        if (!ids.length) return;
        var total = runs().filter(function (r) { return ids.indexOf(r.salaryId) >= 0; })
            .reduce(function (t, r) { return t + (r.netSalary || 0); }, 0);
        document.getElementById('saPaySummary').innerHTML = 'Record payment of <strong>' + ids.length + '</strong> run' +
            (ids.length === 1 ? '' : 's') + ', total net pay <strong>' + money(total) + '</strong>.';
        document.getElementById('saPayRef').value = '';
        open('saPayModal');
    }

    function confirmPay() {
        var ids = selected('APPROVED');
        var btn = document.getElementById('saPayConfirm');
        btn.disabled = true;
        window.approvalMarkPaid(ids, document.getElementById('saPayRef').value.trim()).then(function (res) {
            close('saPayModal');
            reportResult(res, 'marked as paid');
            return window.approvalLoad();
        }).then(render).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () { btn.disabled = false; });
    }

    function showRun(id) {
        var r = runs().find(function (x) { return x.salaryId === id; });
        if (!r) return;
        document.getElementById('saDetailTitle').textContent = (r.staffName || r.staffId) + ' — ' + r.payPeriod;
        document.getElementById('saDetailBody').innerHTML = P.runDetailHtml(r);
        open('saDetailModal');
    }

    function wireEvents() {
        document.getElementById('saRefreshBtn').addEventListener('click', function () { window.approvalLoad().then(render); });
        document.getElementById('saPeriod').addEventListener('change', render);
        document.getElementById('saStatus').addEventListener('change', render);
        document.getElementById('saApproveBtn').addEventListener('click', approveSelected);
        document.getElementById('saPayBtn').addEventListener('click', openPay);
        document.getElementById('saPayConfirm').addEventListener('click', confirmPay);
        document.getElementById('saAll').addEventListener('change', function () {
            var on = this.checked;
            document.querySelectorAll('.sa-check').forEach(function (c) { c.checked = on; });
            syncButtons();
        });
        var tbody = document.querySelector('#saTable tbody');
        tbody.addEventListener('change', function (e) { if (e.target.classList.contains('sa-check')) syncButtons(); });
        tbody.addEventListener('click', function (e) {
            var btn = e.target.closest('.sa-view');
            if (btn) showRun(Number(btn.dataset.id));
        });
        document.querySelectorAll('[data-close]').forEach(function (b) {
            b.addEventListener('click', function () { close(this.dataset.close); });
        });
        ['saPayModal', 'saDetailModal'].forEach(function (id) {
            document.getElementById(id).addEventListener('click', function (e) { if (e.target === this) close(id); });
        });
    }

    function init() {
        buildDOM();
        wireEvents();
        document.querySelector('#saTable tbody').innerHTML = '<tr><td colspan="10" class="fc-empty">Loading payroll runs…</td></tr>';
        window.approvalLoad().then(render);
    }

    var currentScript = document.currentScript;

    function loadScript(src, onload) {
        var s = document.createElement('script');
        s.src = src;
        s.setAttribute('data-dynamic', 'true');
        s.onload = onload;
        s.onerror = function () { console.error('[financeSalaryApprovals] Could not load ' + src); };
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
            loadScript(base + '../_financeSalaryApprovals.js', init);
        };
        if (window.payrollShared) withShared();
        else loadScript(base + '../_payrollShared.js', withShared);
    }

    loadScripts();
})();
