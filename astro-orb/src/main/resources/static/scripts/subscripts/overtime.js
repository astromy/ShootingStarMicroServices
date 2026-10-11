/**
 * overtime.js  —  Human Resource -> Overtime.
 *
 * Approvals: overtime found from staff clock-in/clock-out (Pulse or the Leave
 * screen), waiting for someone to approve or reject it. Only approved overtime
 * reaches payroll. The approver can allow fewer minutes than were found, never
 * more. The approver is always the signed-in user, recorded by the server.
 *
 * Man-hours: hours expected against hours worked for a month, with public
 * holidays, rest days and approved leave taken out of what was expected.
 *
 * Access is controlled by the "Overtime" permission. _payrollShared.js is
 * loaded before this file by common.js.
 */
(function () {
    'use strict';

    var P = window.payrollShared;
    var esc = P.escape;
    var toastTimer;
    var now = P.current();
    var state = {tab: 'approvals', rows: [], mhMonth: now.month, mhYear: now.year};

    function $(id) { return document.getElementById(id); }

    function buildDOM() {
        var monthOptions = P.months.map(function (m, i) {
            return '<option value="' + i + '"' + (i === state.mhMonth ? ' selected' : '') + '>' + m + '</option>';
        }).join('');
        var yearOptions = P.years().map(function (y) {
            return '<option value="' + y + '"' + (y === state.mhYear ? ' selected' : '') + '>' + y + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-business-time"></i> Overtime</h1>',
            '<p>Review overtime found from clock-ins and approve it for payroll. Rates are set under Setup &rarr; Working Hours.</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="otRefresh"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '</div>',
            '</header>',

            '<div style="display:flex;gap:8px;margin-bottom:14px">',
            '<button class="fc-btn fc-btn-primary" id="otTabApprovals" data-tab="approvals"><i class="fas fa-clipboard-check"></i> Approvals</button>',
            '<button class="fc-btn fc-btn-secondary" id="otTabHours" data-tab="manhours"><i class="fas fa-user-clock"></i> Man-hours</button>',
            '</div>',

            // ── Approvals ──
            '<div id="otPaneApprovals">',
            '<div class="ph-stats" id="otStats"></div>',
            '<div class="ph-controls">',
            '<select id="otStatus" aria-label="Status">',
            '<option value="PENDING">Waiting for approval</option>',
            '<option value="APPROVED">Approved</option>',
            '<option value="REJECTED">Rejected</option>',
            '<option value="ALL">All</option>',
            '</select>',
            '<label style="font-size:13px">From <input type="date" id="otFrom"></label>',
            '<label style="font-size:13px">To <input type="date" id="otTo"></label>',
            '<button class="fc-btn fc-btn-secondary" id="otCheck" title="Look through clock-in records for overtime not yet listed"><i class="fas fa-search"></i> Check for new overtime</button>',
            '<button class="fc-btn fc-btn-success" id="otApprove" disabled><i class="fas fa-check"></i> Approve selected</button>',
            '<button class="fc-btn fc-btn-secondary" id="otReject" disabled><i class="fas fa-xmark"></i> Reject selected</button>',
            '</div>',
            '<p id="otHint" style="margin:0 0 10px;font-size:12px;color:#8a93a0">New overtime appears automatically when staff clock out. ',
            '<em>Check for new overtime</em> looks back through the dates above (default: the last 30 days) - use it after switching overtime on or fixing holidays.</p>',
            '<div class="ph-table-wrap"><table class="fc-table" id="otTable">',
            '<thead><tr><th><input type="checkbox" id="otAll" aria-label="Select all waiting"></th>',
            '<th>Staff</th><th>Date</th><th>Kind</th><th>Clocked</th><th>Found</th><th>Approve</th><th>Status</th><th>Decided by</th></tr></thead>',
            '<tbody></tbody></table></div>',
            '</div>',

            // ── Man-hours ──
            '<div id="otPaneHours" style="display:none">',
            '<div class="ph-controls">',
            '<select id="otMonth" aria-label="Month">' + monthOptions + '</select>',
            '<select id="otYear" aria-label="Year">' + yearOptions + '</select>',
            '</div>',
            '<p style="margin:0 0 10px;font-size:12px;color:#8a93a0">Expected hours leave out weekends, public holidays and approved leave. ',
            'Worked hours are from clock-in and clock-out.</p>',
            '<div class="ph-table-wrap"><table class="fc-table" id="otHoursTable">',
            '<thead><tr><th>Staff</th><th>Working days</th><th>Holidays</th><th>Leave</th><th>Expected days</th><th>Expected hours</th>',
            '<th>Days present</th><th>Worked hours</th><th>Overtime approved</th><th>Overtime waiting</th></tr></thead>',
            '<tbody></tbody></table></div>',
            '</div>',

            // Reject
            '<div class="fc-modal-overlay" id="otRejectModal"><div class="fc-modal">',
            '<div class="fc-modal-head"><h5><i class="fas fa-xmark"></i> Reject overtime</h5>',
            '<button class="fc-modal-close" data-close="otRejectModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body">',
            '<p id="otRejectSummary" style="margin:0 0 12px"></p>',
            '<div class="fc-field"><label for="otRejectNote">Reason (optional)</label>',
            '<input type="text" id="otRejectNote" placeholder="e.g. not authorised"></div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="otRejectModal">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="otRejectConfirm"><i class="fas fa-xmark"></i> Reject</button>',
            '</div></div></div>',

            '<div class="fc-toast" id="otToast" role="status" aria-live="polite"></div>',
            '</div>'
        ].join('');
    }

    function toast(message, type) {
        var el = $('otToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 6000);
    }

    function open(id) { $(id).classList.add('open'); }

    function close(id) { $(id).classList.remove('open'); }

    // ── formatting ──────────────────────────────────────────────────────────

    function fmtDate(ymd) {
        var p = String(ymd).split('-');
        return new Date(+p[0], +p[1] - 1, +p[2]).toLocaleDateString('en-GB', {weekday: 'short', day: '2-digit', month: 'short', year: 'numeric'});
    }

    function fmtTime(iso) {
        return iso ? new Date(iso).toLocaleTimeString('en-GB', {hour: '2-digit', minute: '2-digit'}) : '—';
    }

    function fmtMinutes(m) {
        var h = Math.floor(m / 60), r = m % 60;
        return ((h ? h + 'h ' : '') + (r || !h ? r + 'm' : '')).trim();
    }

    function hours(h) { return (Math.round(h * 100) / 100).toString(); }

    function statusBadge(r) {
        var badge;
        if (r.status === 'APPROVED') badge = '<span class="sal-status sal-approved">Approved</span>';
        else if (r.status === 'REJECTED') badge = '<span class="sal-status" style="background:#fde8e8;color:#b42318">Rejected</span>';
        else badge = '<span class="sal-status sal-pending">Waiting</span>';
        if (r.payrollRef) badge += ' <span style="font-size:11px;background:#e6f4ea;color:#1e6b3a;border-radius:10px;padding:2px 8px">In payroll</span>';
        return badge;
    }

    function kindBadge(r) {
        var colour = r.category === 'PUBLIC_HOLIDAY' ? '#fde8e8;color:#b42318'
            : r.category === 'WEEKEND' ? '#e8f0fe;color:#1a56c4' : '#fff4d6;color:#8a6100';
        return '<span style="font-size:12px;background:' + colour + ';border-radius:10px;padding:2px 9px">' + esc(r.categoryLabel) + '</span>' +
            (r.holidayName ? '<div style="font-size:11px;color:#8a93a0;margin-top:2px">' + esc(r.holidayName) + '</div>' : '');
    }

    // ── approvals ───────────────────────────────────────────────────────────

    function renderStats() {
        var waiting = 0, approved = 0, inPayroll = 0, waitingCount = 0;
        state.rows.forEach(function (r) {
            if (r.status === 'PENDING') { waiting += r.minutes; waitingCount++; }
            else if (r.status === 'APPROVED') {
                var m = r.approvedMinutes != null ? r.approvedMinutes : r.minutes;
                if (r.payrollRef) inPayroll += m; else approved += m;
            }
        });
        function card(icon, label, value, cls) {
            return '<div class="ph-stat ' + cls + '"><i class="fas ' + icon + ' ph-stat-icon"></i>' +
                '<div><h3>' + value + '</h3><p>' + esc(label) + '</p></div></div>';
        }
        $('otStats').innerHTML =
            card('fa-hourglass-half', 'Waiting for approval (' + waitingCount + ')', hours(waiting / 60) + ' h', 'ph-stat-amber') +
            card('fa-check', 'Approved, not yet in payroll', hours(approved / 60) + ' h', 'ph-stat-blue') +
            card('fa-money-bill', 'Already in payroll', hours(inPayroll / 60) + ' h', 'ph-stat-green');
    }

    function renderTable() {
        var tbody = document.querySelector('#otTable tbody');
        if (!state.rows.length) {
            tbody.innerHTML = '<tr><td colspan="9" class="fc-empty">Nothing here. If overtime was only just switched on, use <strong>Check for new overtime</strong>.</td></tr>';
            updateButtons();
            return;
        }
        tbody.innerHTML = state.rows.map(function (r) {
            var waiting = r.status === 'PENDING';
            var approve = waiting
                ? '<input type="number" class="otMinutes" data-id="' + esc(r.id) + '" data-max="' + r.minutes + '" value="' + r.minutes +
                  '" min="1" max="' + r.minutes + '" step="5" style="width:74px" aria-label="Minutes to approve"> min'
                : (r.status === 'APPROVED' ? esc(fmtMinutes(r.approvedMinutes != null ? r.approvedMinutes : r.minutes)) : '—');
            return '<tr>' +
                '<td><input type="checkbox" class="otPick" data-id="' + esc(r.id) + '"' + (waiting ? '' : ' disabled') + ' aria-label="Select"></td>' +
                '<td><strong>' + esc(r.staffName || r.staffCode) + '</strong><div style="font-size:11px;color:#8a93a0">' + esc(r.staffCode) + '</div></td>' +
                '<td>' + esc(fmtDate(r.date)) + '</td>' +
                '<td>' + kindBadge(r) + '</td>' +
                '<td>' + esc(fmtTime(r.firstIn)) + ' – ' + esc(fmtTime(r.lastOut)) + '</td>' +
                '<td><strong>' + esc(fmtMinutes(r.minutes)) + '</strong></td>' +
                '<td>' + approve + '</td>' +
                '<td>' + statusBadge(r) + (r.note ? '<div style="font-size:11px;color:#8a93a0;margin-top:2px">' + esc(r.note) + '</div>' : '') + '</td>' +
                '<td>' + esc(r.decidedBy || '') + '</td>' +
                '</tr>';
        }).join('');
        updateButtons();
    }

    function picked() {
        return [].map.call(document.querySelectorAll('.otPick:checked'), function (b) { return b.getAttribute('data-id'); });
    }

    function updateButtons() {
        var any = picked().length > 0;
        $('otApprove').disabled = !any;
        $('otReject').disabled = !any;
    }

    function loadApprovals() {
        $('otAll').checked = false;
        return fetchPost('getOvertime', {
            status: $('otStatus').value,
            fromDate: $('otFrom').value || null,
            toDate: $('otTo').value || null
        }).then(function (rows) {
            state.rows = rows || [];
            renderStats();
            renderTable();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        });
    }

    function check() {
        var btn = $('otCheck');
        btn.disabled = true;
        fetchPost('detectOvertime', {fromDate: $('otFrom').value || null, toDate: $('otTo').value || null}).then(function (r) {
            toast(r.message, r.created || r.updated || r.removed ? 'success' : '');
            return loadApprovals();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    function approve() {
        var items = [];
        var ok = true;
        picked().forEach(function (id) {
            var input = document.querySelector('.otMinutes[data-id="' + id + '"]');
            var max = parseInt(input.getAttribute('data-max'), 10);
            var value = parseInt(input.value, 10);
            if (isNaN(value) || value < 1 || value > max) {
                ok = false;
                input.style.outline = '2px solid #b42318';
            }
            // Only send a figure when the approver changed it; otherwise the full amount is approved.
            items.push({id: id, approvedMinutes: value === max ? null : value});
        });
        if (!ok) return toast('Each approved time must be between 1 minute and the time found.', 'error');

        var btn = $('otApprove');
        btn.disabled = true;
        fetchPost('approveOvertime', {items: items}).then(function (done) {
            toast('Approved ' + done.length + ' overtime entr' + (done.length === 1 ? 'y' : 'ies') + '. It will be included in the next payroll run.', 'success');
            return loadApprovals();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
            updateButtons();
        });
    }

    function openReject() {
        var ids = picked();
        $('otRejectSummary').textContent = 'Reject ' + ids.length + ' overtime entr' + (ids.length === 1 ? 'y' : 'ies') +
            '? Rejected overtime is not paid and cannot be approved later.';
        $('otRejectNote').value = '';
        open('otRejectModal');
    }

    function confirmReject() {
        var btn = $('otRejectConfirm');
        btn.disabled = true;
        fetchPost('rejectOvertime', {ids: picked(), note: $('otRejectNote').value.trim() || null}).then(function (done) {
            close('otRejectModal');
            toast('Rejected ' + done.length + '.', 'success');
            return loadApprovals();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    // ── man-hours ───────────────────────────────────────────────────────────

    function loadManHours() {
        return fetchPost('getManHours', {month: state.mhMonth, year: state.mhYear}).then(function (rows) {
            var tbody = document.querySelector('#otHoursTable tbody');
            if (!rows || !rows.length) {
                tbody.innerHTML = '<tr><td colspan="10" class="fc-empty">No staff found.</td></tr>';
                return;
            }
            tbody.innerHTML = rows.map(function (r) {
                var short = r.workedHours < r.expectedHours;
                return '<tr>' +
                    '<td><strong>' + esc(r.staffName || r.staffCode) + '</strong><div style="font-size:11px;color:#8a93a0">' + esc(r.staffCode) + '</div></td>' +
                    '<td>' + r.workingDays + '</td>' +
                    '<td>' + r.holidayDays + '</td>' +
                    '<td>' + r.leaveDays + '</td>' +
                    '<td>' + r.expectedDays + '</td>' +
                    '<td>' + hours(r.expectedHours) + '</td>' +
                    '<td>' + r.daysPresent + '</td>' +
                    '<td' + (short ? ' style="color:#b42318"' : '') + '><strong>' + hours(r.workedHours) + '</strong></td>' +
                    '<td>' + hours(r.overtimeApprovedHours) + '</td>' +
                    '<td>' + hours(r.overtimePendingHours) + '</td>' +
                    '</tr>';
            }).join('');
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        });
    }

    function showTab(tab) {
        state.tab = tab;
        $('otPaneApprovals').style.display = tab === 'approvals' ? '' : 'none';
        $('otPaneHours').style.display = tab === 'manhours' ? '' : 'none';
        $('otTabApprovals').className = 'fc-btn ' + (tab === 'approvals' ? 'fc-btn-primary' : 'fc-btn-secondary');
        $('otTabHours').className = 'fc-btn ' + (tab === 'manhours' ? 'fc-btn-primary' : 'fc-btn-secondary');
        refresh();
    }

    function refresh() {
        return state.tab === 'approvals' ? loadApprovals() : loadManHours();
    }

    function bind() {
        $('otRefresh').addEventListener('click', refresh);
        $('otTabApprovals').addEventListener('click', function () { showTab('approvals'); });
        $('otTabHours').addEventListener('click', function () { showTab('manhours'); });

        $('otStatus').addEventListener('change', loadApprovals);
        $('otFrom').addEventListener('change', loadApprovals);
        $('otTo').addEventListener('change', loadApprovals);
        $('otCheck').addEventListener('click', check);
        $('otApprove').addEventListener('click', approve);
        $('otReject').addEventListener('click', openReject);
        $('otRejectConfirm').addEventListener('click', confirmReject);

        $('otAll').addEventListener('change', function () {
            var on = this.checked;
            document.querySelectorAll('.otPick:not(:disabled)').forEach(function (b) { b.checked = on; });
            updateButtons();
        });
        $('otTable').addEventListener('change', function (e) {
            if (e.target.classList.contains('otPick')) updateButtons();
        });

        $('otMonth').addEventListener('change', function () { state.mhMonth = parseInt(this.value, 10); loadManHours(); });
        $('otYear').addEventListener('change', function () { state.mhYear = parseInt(this.value, 10); loadManHours(); });

        document.querySelectorAll('[data-close]').forEach(function (b) {
            b.addEventListener('click', function () { close(b.getAttribute('data-close')); });
        });
        document.querySelectorAll('.fc-modal-overlay').forEach(function (o) {
            o.addEventListener('click', function (e) { if (e.target === o) o.classList.remove('open'); });
        });
    }

    buildDOM();
    bind();
    loadApprovals();
})();
