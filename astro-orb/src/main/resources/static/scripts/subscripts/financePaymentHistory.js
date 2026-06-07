/**
 * financePaymentHistory.js  —  UI renderer for Payment History.
 * Shows all institution payments with filters, summary stats, drill-down,
 * and CSV export.
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financePaymentHistory') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    (function () {
        if (document.getElementById('pyHistCSS')) return;
        var l = document.createElement('link');
        l.id = 'pyHistCSS';
        l.rel = 'stylesheet';
        l.href = _base + '../../styles/style.css';
        document.head.appendChild(l);
    })();

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        var s = window.pyHistState;

        var yearOpts = '<option value="all">All Years</option>' +
            s.academicYears.map(function (y) {
                return '<option ' + (y === s.selectedYear ? 'selected' : '') + '>' + y + '</option>';
            }).join('');

        var termOpts = '<option value="all">All Terms</option>' +
            window.pyHistData.terms.map(function (t) {
                return '<option value="' + t.value + '">' + t.label + '</option>';
            }).join('');

        document.getElementById('wrapper').innerHTML = [
            '<div class="ph-page">',

            '<header class="fc-header">',
            '<div>',
            '<h1><i class="fas fa-history"></i> Payment History</h1>',
            '<p>All fee payments recorded for this institution</p>',
            '</div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="phRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '<button class="fc-btn fc-btn-success"   id="phExportBtn"><i class="fas fa-file-csv"></i> Export CSV</button>',
            '</div>',
            '</header>',

            // Stats strip
            '<div class="ph-stats" id="phStats">',
            '<div class="ph-stat ph-stat-blue"><div class="ph-stat-icon"><i class="fas fa-receipt"></i></div><div><h3 id="phCount">0</h3><p>Total Transactions</p></div></div>',
            '<div class="ph-stat ph-stat-green"><div class="ph-stat-icon"><i class="fas fa-coins"></i></div><div><h3 id="phTotal">GH₵ 0</h3><p>Total Collected</p></div></div>',
            '<div class="ph-stat ph-stat-amber"><div class="ph-stat-icon"><i class="fas fa-mobile-alt"></i></div><div><h3 id="phMomo">GH₵ 0</h3><p>Mobile Money</p></div></div>',
            '<div class="ph-stat ph-stat-navy"><div class="ph-stat-icon"><i class="fas fa-money-bill"></i></div><div><h3 id="phCash">GH₵ 0</h3><p>Cash</p></div></div>',
            '</div>',

            // Control bar
            '<div class="ph-controls">',
            '<div class="ph-search-wrap"><i class="fas fa-search"></i><input type="text" id="phSearch" placeholder="Student ID, receipt or cashier…"></div>',
            '<select id="phYearSel">' + yearOpts + '</select>',
            '<select id="phTermSel">' + termOpts + '</select>',
            '<select id="phMethodSel">',
            '<option value="all">All Methods</option>',
            '<option value="CASH">Cash</option>',
            '<option value="MOMO">Mobile Money</option>',
            '<option value="BANK">Bank Transfer</option>',
            '<option value="ONLINE">Online</option>',
            '</select>',
            '<button class="fc-btn fc-btn-primary" id="phFilterBtn"><i class="fas fa-filter"></i> Filter</button>',
            '</div>',

            // Table
            '<div class="ph-table-wrap">',
            '<table class="fc-table" id="phTable">',
            '<thead><tr>',
            '<th>Receipt No.</th><th>Student ID</th><th>Amount</th>',
            '<th>Method</th><th>Term</th><th>Year</th>',
            '<th>Date & Time</th><th>Received By</th><th></th>',
            '</tr></thead>',
            '<tbody id="phTableBody">',
            '<tr><td colspan="9" class="fc-empty"><i class="fas fa-circle-notch fa-spin"></i> Loading payments…</td></tr>',
            '</tbody>',
            '</table>',
            '</div>',

            // Student drill-down modal
            '<div class="fc-modal-overlay" id="phDrillModal">',
            '<div class="fc-modal fc-modal-lg">',
            '<div class="fc-modal-head"><h5><i class="fas fa-user-circle"></i> Student Account</h5><button class="fc-modal-close" id="phDrillClose">&times;</button></div>',
            '<div class="fc-modal-body" id="phDrillBody"></div>',
            '<div class="fc-modal-foot"><button class="fc-btn fc-btn-secondary" id="phDrillCloseBtn">Close</button></div>',
            '</div></div>',

            '<div class="fc-toast" id="phToast"></div>',

            '<footer class="footer">',
            '<i class="far fa-copyright"></i> Astromy LLC 2013–<span id="phYear"></span> | Payment History',
            '</footer>',
            '</div>',
        ].join('');

        document.getElementById('phYear').textContent = new Date().getFullYear();
    }

    // ── RENDER TABLE ─────────────────────────────────────────────────────────
    function renderTable(payments) {
        var fmt = window.pyHistFmt;
        var body = document.getElementById('phTableBody');
        if (!body) return;

        if (!payments || !payments.length) {
            body.innerHTML = '<tr><td colspan="9" class="fc-empty"><i class="fas fa-inbox"></i> No payment records found</td></tr>';
            return;
        }

        body.innerHTML = payments.map(function (p) {
            return '<tr>' +
                '<td><code>' + (p.recieptNum || '—') + '</code></td>' +
                '<td>' + (p.studentId || '—') + '</td>' +
                '<td class="fc-money">' + fmt.money(p.paymentAmount) + '</td>' +
                '<td><span class="ph-method-badge ph-' + (p.paymentMethod || 'CASH').toLowerCase() + '">' + (p.paymentMethod || 'CASH') + '</span></td>' +
                '<td>' + (p.term || '—') + '</td>' +
                '<td>' + (p.academicYear || '—') + '</td>' +
                '<td>' + fmt.dateTime(p.paymentDate) + '</td>' +
                '<td>' + (p.paidBy || '—') + '</td>' +
                '<td><button class="fc-btn-icon ph-drill-btn" data-id="' + (p.studentId || '') + '" title="View account"><i class="fas fa-eye"></i></button></td>' +
                '</tr>';
        }).join('');

        // Wire drill-down buttons
        body.querySelectorAll('.ph-drill-btn').forEach(function (btn) {
            btn.addEventListener('click', async function () {
                var studentId = this.dataset.id;
                if (!studentId) return;
                await openDrillDown(studentId, payments);
            });
        });
    }

    // ── RENDER STATS ─────────────────────────────────────────────────────────
    function renderStats(payments) {
        var s = window.pyHistStats(payments);
        var fmt = window.pyHistFmt;

        setText('phCount', s.count);
        setText('phTotal', fmt.money(s.total));
        setText('phMomo', fmt.money(s.byMethod['MOMO'] || 0));
        setText('phCash', fmt.money(s.byMethod['CASH'] || 0));
    }

    // ── DRILL-DOWN MODAL ─────────────────────────────────────────────────────
    async function openDrillDown(studentId, allPayments) {
        var fmt = window.pyHistFmt;
        document.getElementById('phDrillBody').innerHTML =
            '<div class="fc-empty"><i class="fas fa-circle-notch fa-spin"></i> Loading account…</div>';
        document.getElementById('phDrillModal').classList.add('open');

        var [bill, studentPayments] = await Promise.all([
            window.pyHistFetchStudentBill(studentId),
            Promise.resolve(allPayments.filter(function (p) {
                return p.studentId === studentId;
            })),
        ]);

        var billHtml = bill ? [
            '<div class="ph-drill-bill">',
            '<div class="ph-drill-bill-item"><span>Total Billed</span><strong>' + fmt.money(bill.amountDue) + '</strong></div>',
            '<div class="ph-drill-bill-item"><span>Total Paid</span><strong class="fc-paid">' + fmt.money(bill.amountPaid) + '</strong></div>',
            '<div class="ph-drill-bill-item"><span>Balance</span><strong class="' + (bill.amountBalance > 0 ? 'fc-owing' : 'fc-clear') + '">' + fmt.money(bill.amountBalance) + '</strong></div>',
            '<div class="ph-drill-bill-item"><span>Old Balance</span><strong>' + fmt.money(bill.oldBalance || 0) + '</strong></div>',
            '</div>',
        ].join('') : '<p class="fc-empty">No bill record found for this student.</p>';

        var histRows = studentPayments.map(function (p) {
            return '<tr><td>' + fmt.dateTime(p.paymentDate) + '</td>' +
                '<td class="fc-money">' + fmt.money(p.paymentAmount) + '</td>' +
                '<td>' + (p.paymentMethod || 'CASH') + '</td>' +
                '<td>' + (p.term || '—') + '</td>' +
                '<td>' + (p.academicYear || '—') + '</td>' +
                '<td>' + (p.recieptNum || '—') + '</td>' +
                '<td>' + (p.paidBy || '—') + '</td></tr>';
        }).join('') || '<tr><td colspan="7" class="fc-empty">No payment records</td></tr>';

        document.getElementById('phDrillBody').innerHTML =
            '<div class="ph-drill-student"><i class="fas fa-user-graduate"></i> ' + studentId + '</div>' +
            billHtml +
            '<div class="ph-drill-hist-title"><i class="fas fa-history"></i> All Payments</div>' +
            '<table class="fc-table"><thead><tr>' +
            '<th>Date</th><th>Amount</th><th>Method</th><th>Term</th><th>Year</th><th>Receipt</th><th>By</th>' +
            '</tr></thead><tbody>' + histRows + '</tbody></table>';
    }

    // ── FILTER & RENDER ──────────────────────────────────────────────────────
    function applyAndRender() {
        var payments = window.pyHistFilter({
            year: document.getElementById('phYearSel')?.value,
            term: document.getElementById('phTermSel')?.value,
            method: document.getElementById('phMethodSel')?.value,
            search: document.getElementById('phSearch')?.value,
        });
        renderTable(payments);
        renderStats(payments);
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('phFilterBtn').addEventListener('click', applyAndRender);

        document.getElementById('phSearch').addEventListener('keydown', function (e) {
            if (e.key === 'Enter') applyAndRender();
        });

        document.getElementById('phRefreshBtn').addEventListener('click', async function () {
            await window.pyHistLoad();
            applyAndRender();
        });

        document.getElementById('phExportBtn').addEventListener('click', function () {
            var filtered = window.pyHistState.filtered.length
                ? window.pyHistState.filtered
                : window.pyHistState.allPayments;
            window.pyHistExport(filtered);
        });

        document.getElementById('phDrillClose').addEventListener('click', function () {
            document.getElementById('phDrillModal').classList.remove('open');
        });
        document.getElementById('phDrillCloseBtn').addEventListener('click', function () {
            document.getElementById('phDrillModal').classList.remove('open');
        });
        document.getElementById('phDrillModal').addEventListener('click', function (e) {
            if (e.target === this) this.classList.remove('open');
        });
    }

    function setText(id, v) {
        var el = document.getElementById(id);
        if (el) el.textContent = v;
    }

    // ── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        waitForData();
    }

    function waitForData() {
        var t = setInterval(function () {
            if (window.pyHistState && window.pyHistState._loaded) {
                clearInterval(t);
                applyAndRender();
            }
        }, 30);
    }

    function boot() {
        if (window.pyHistState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financePaymentHistory.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();
})();