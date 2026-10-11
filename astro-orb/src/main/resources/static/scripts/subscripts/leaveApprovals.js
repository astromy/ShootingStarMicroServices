/**
 * leaveApprovals.js  —  Human Resource -> Leave Approvals.
 *
 * Everyone's leave requests, for the people allowed to approve or reject them. This is
 * its own permission ("Leave Approvals"), separate from the "Leave" permission that lets
 * a staff member apply for their own leave and see their own balance - the same split as
 * Salary Setup / Salary Approvals. The server enforces it too, not just this menu.
 * The approver is always the signed-in user, recorded by the server.
 *
 * _payrollShared.js is loaded before this file by common.js.
 */
(function () {
    'use strict';

    var P = window.payrollShared;
    var esc = P.escape;
    var toastTimer;
    var state = {rows: [], deciding: null, decision: null};

    function $(id) { return document.getElementById(id); }

    function buildDOM() {
        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-clipboard-check"></i> Leave Approvals</h1>',
            '<p>Approve or reject staff leave requests. Your name is recorded from your login.</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="laRefresh"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '</div>',
            '</header>',

            '<div class="ph-stats" id="laStats"></div>',

            '<div class="ph-controls">',
            '<select id="laStatus" aria-label="Status">',
            '<option value="Pending">Waiting for approval</option>',
            '<option value="Approved">Approved</option>',
            '<option value="Rejected">Rejected</option>',
            '<option value="">All</option>',
            '</select>',
            '<select id="laType" aria-label="Leave type"><option value="">All leave types</option>',
            '<option>Casual</option><option>Sick</option><option>Earned</option><option>Maternity</option></select>',
            '<input type="search" id="laSearch" placeholder="Search staff…" aria-label="Search staff" style="min-width:200px">',
            '</div>',

            '<div class="ph-table-wrap"><table class="fc-table" id="laTable">',
            '<thead><tr><th>Staff</th><th>Leave</th><th>Period</th><th>Days</th><th>Reason</th><th>Applied</th><th>Status</th><th></th></tr></thead>',
            '<tbody></tbody></table></div>',

            '<div class="fc-modal-overlay" id="laModal"><div class="fc-modal">',
            '<div class="fc-modal-head"><h5 id="laModalTitle"></h5>',
            '<button class="fc-modal-close" data-close="laModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body">',
            '<div id="laDetail" style="background:#eef1f8;border-radius:8px;padding:12px 14px;font-size:14px;line-height:1.8;margin-bottom:14px"></div>',
            '<div class="fc-field"><label for="laRemarks" id="laRemarksLabel">Remarks (optional)</label>',
            '<input type="text" id="laRemarks" placeholder="Shown to the staff member"></div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="laModal">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="laConfirm"></button>',
            '</div></div></div>',

            '<div class="fc-toast" id="laToast" role="status" aria-live="polite"></div>',
            '</div>'
        ].join('');
    }

    function toast(message, type) {
        var el = $('laToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 5000);
    }

    function open(id) { $(id).classList.add('open'); }

    function close(id) { $(id).classList.remove('open'); }

    function fmtDate(ymd) {
        var p = String(ymd).split('-');
        return new Date(+p[0], +p[1] - 1, +p[2]).toLocaleDateString('en-GB', {weekday: 'short', day: '2-digit', month: 'short', year: 'numeric'});
    }

    function badge(status) {
        if (status === 'Approved') return '<span class="sal-status sal-approved">Approved</span>';
        if (status === 'Rejected') return '<span class="sal-status" style="background:#fde8e8;color:#b42318">Rejected</span>';
        return '<span class="sal-status sal-pending">Waiting</span>';
    }

    function visible() {
        var status = $('laStatus').value, type = $('laType').value, q = $('laSearch').value.trim().toLowerCase();
        return state.rows.filter(function (r) {
            return (!status || r.status === status) && (!type || r.leaveType === type) &&
                (!q || String(r.employeeName || '').toLowerCase().indexOf(q) >= 0 || String(r.employeeId || '').toLowerCase().indexOf(q) >= 0);
        }).sort(function (a, b) {
            // oldest waiting request first; otherwise newest first
            var da = new Date(a.appliedOn || 0), db = new Date(b.appliedOn || 0);
            return a.status === 'Pending' && b.status === 'Pending' ? da - db : db - da;
        });
    }

    function renderStats() {
        var waiting = state.rows.filter(function (r) { return r.status === 'Pending'; });
        var days = waiting.reduce(function (n, r) { return n + (r.days || 0); }, 0);
        function card(icon, label, value, cls) {
            return '<div class="ph-stat ' + cls + '"><i class="fas ' + icon + ' ph-stat-icon"></i><div><h3>' + value + '</h3><p>' + esc(label) + '</p></div></div>';
        }
        $('laStats').innerHTML = card('fa-hourglass-half', 'Requests waiting', waiting.length, 'ph-stat-amber') +
            card('fa-calendar-day', 'Leave days waiting', days, 'ph-stat-blue');
    }

    function render() {
        renderStats();
        var rows = visible();
        var tbody = document.querySelector('#laTable tbody');
        if (!rows.length) {
            tbody.innerHTML = '<tr><td colspan="8" class="fc-empty">' +
                ($('laStatus').value === 'Pending' ? 'No leave requests are waiting for approval.' : 'No leave requests match.') + '</td></tr>';
            return;
        }
        tbody.innerHTML = rows.map(function (r) {
            var actions = r.status === 'Pending'
                ? '<button class="fc-btn fc-btn-success fc-btn-sm" data-act="Approved" data-id="' + esc(r.leaveId) + '"><i class="fas fa-check"></i> Approve</button> ' +
                  '<button class="fc-btn fc-btn-secondary fc-btn-sm" data-act="Rejected" data-id="' + esc(r.leaveId) + '"><i class="fas fa-xmark"></i> Reject</button>'
                : '<span style="font-size:12px;color:#8a93a0">' + esc(r.decidedBy || '') + (r.remarks ? '<br>' + esc(r.remarks) : '') + '</span>';
            return '<tr>' +
                '<td><strong>' + esc(r.employeeName || r.employeeId) + '</strong><div style="font-size:11px;color:#8a93a0">' + esc(r.employeeId) + '</div></td>' +
                '<td>' + esc(r.leaveType) + '</td>' +
                '<td>' + esc(fmtDate(r.startDate)) + '<br><span style="color:#8a93a0">to</span> ' + esc(fmtDate(r.endDate)) + '</td>' +
                '<td><strong>' + (r.days != null ? r.days : '—') + '</strong></td>' +
                '<td style="max-width:240px">' + esc(r.reason || '—') + '</td>' +
                '<td>' + esc(r.appliedOn ? P.date(r.appliedOn) : '—') + '</td>' +
                '<td>' + badge(r.status) + '</td>' +
                '<td style="white-space:nowrap">' + actions + '</td>' +
                '</tr>';
        }).join('');
    }

    function load() {
        // A blank employee asks for everyone in the school; the server allows that only with this permission.
        return fetchPost('getLeaveRequests', {employeeId: ''}).then(function (rows) {
            state.rows = rows || [];
            render();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
            // A blank table would look like "nothing is waiting", which is wrong when the list simply couldn't be loaded.
            state.rows = [];
            renderStats();
            document.querySelector('#laTable tbody').innerHTML =
                '<tr><td colspan="8" class="fc-empty"><i class="fas fa-triangle-exclamation"></i> The leave requests couldn\'t be loaded. ' + esc(P.errorMessage(e)) + '</td></tr>';
        });
    }

    function openDecision(id, decision) {
        var r = state.rows.filter(function (x) { return x.leaveId === id; })[0];
        if (!r) return;
        state.deciding = r;
        state.decision = decision;
        var approving = decision === 'Approved';
        $('laModalTitle').innerHTML = approving ? '<i class="fas fa-check"></i> Approve leave' : '<i class="fas fa-xmark"></i> Reject leave';
        $('laDetail').innerHTML =
            '<strong>' + esc(r.employeeName || r.employeeId) + '</strong> &middot; ' + esc(r.leaveType) + ' leave<br>' +
            esc(fmtDate(r.startDate)) + ' &ndash; ' + esc(fmtDate(r.endDate)) + ' (<strong>' + (r.days != null ? r.days : '?') + ' day' + (r.days === 1 ? '' : 's') + '</strong>)<br>' +
            '<span style="color:#5b6573">' + esc(r.reason || 'No reason given') + '</span>';
        $('laRemarksLabel').textContent = approving ? 'Remarks (optional)' : 'Reason for rejecting (optional, shown to the staff member)';
        $('laRemarks').value = '';
        $('laConfirm').innerHTML = approving ? '<i class="fas fa-check"></i> Approve' : '<i class="fas fa-xmark"></i> Reject';
        open('laModal');
    }

    function confirmDecision() {
        var btn = $('laConfirm');
        btn.disabled = true;
        fetchPost('approveLeave', {
            leaveId: state.deciding.leaveId,
            status: state.decision,
            remarks: $('laRemarks').value.trim()
        }).then(function () {
            close('laModal');
            toast('Leave ' + (state.decision === 'Approved' ? 'approved' : 'rejected') + '.', 'success');
            return load();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    function bind() {
        $('laRefresh').addEventListener('click', load);
        $('laStatus').addEventListener('change', render);
        $('laType').addEventListener('change', render);
        $('laSearch').addEventListener('input', render);
        $('laConfirm').addEventListener('click', confirmDecision);
        $('laTable').addEventListener('click', function (e) {
            var b = e.target.closest('[data-act]');
            if (b) openDecision(b.getAttribute('data-id'), b.getAttribute('data-act'));
        });
        document.querySelectorAll('[data-close]').forEach(function (b) {
            b.addEventListener('click', function () { close(b.getAttribute('data-close')); });
        });
        document.querySelectorAll('.fc-modal-overlay').forEach(function (o) {
            o.addEventListener('click', function (e) { if (e.target === o) o.classList.remove('open'); });
        });
    }

    buildDOM();
    bind();
    load();
})();
