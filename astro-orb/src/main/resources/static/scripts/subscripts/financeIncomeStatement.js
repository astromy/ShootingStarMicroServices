/**
 * financeIncomeStatement.js  —  UI renderer for Income Statement.
 *
 * Shows a P&L: Revenue minus Expenses = Net Income/Loss.
 * Period selectable by year and term.
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financeIncomeStatement') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    (function () {
        if (document.getElementById('incStmtCSS')) return;
        var l = document.createElement('link');
        l.id = 'incStmtCSS';
        l.rel = 'stylesheet';
        l.href = _base + '../../style.css';
        document.head.appendChild(l);
    })();

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        var s = window.incomeStmtState;
        var yearOpts = s.academicYears.map(function (y) {
            return '<option ' + (y === s.selectedYear ? 'selected' : '') + '>' + y + '</option>';
        }).join('');
        var termOpts = window.incomeStmtTerms.map(function (t) {
            return '<option value="' + t + '">' + (t === 'all' ? 'Full Year' : t) + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = [
            '<div class="is-page">',

            '<header class="fc-header">',
            '<div><h1><i class="fas fa-chart-line"></i> Income Statement</h1>',
            '<p>Revenue, expenses and net income for the selected period</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="isExportBtn"><i class="fas fa-print"></i> Print</button>',
            '</div>',
            '</header>',

            // Period selector
            '<div class="ps-generate-bar">',
            '<div class="ps-generate-controls">',
            '<div class="fc-field"><label>Academic Year</label><select id="isYear">' + yearOpts + '</select></div>',
            '<div class="fc-field"><label>Term</label><select id="isTerm">' + termOpts + '</select></div>',
            '</div>',
            '<button class="fc-btn fc-btn-primary" id="isLoadBtn"><i class="fas fa-sync-alt"></i> Load Statement</button>',
            '</div>',

            // Statement area
            '<div class="is-statement" id="isStatement">',
            '<div class="fc-empty is-placeholder"><i class="fas fa-chart-line"></i><p>Select a period and click Load Statement</p></div>',
            '</div>',

            '<div class="fc-toast" id="isToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="isYear2"></span> | Income Statement</footer>',
            '</div>',
        ].join('');

        document.getElementById('isYear2').textContent = new Date().getFullYear();
    }

    // ── RENDER STATEMENT ──────────────────────────────────────────────────────
    function renderStatement(data) {
        var el = document.getElementById('isStatement');
        if (!data) {
            el.innerHTML = '<div class="fc-empty"><i class="fas fa-exclamation-circle"></i> No data available for this period.</div>';
            return;
        }

        var fmt = window.incomeStmtFmt;
        var periodLabel = (data.term && data.term !== 'all') ? data.term + ' — ' : '';
        periodLabel += data.year || '';

        var incomeRows = (data.income || []).map(function (a) {
            return '<tr><td>' + a.accountName + '</td>' +
                '<td><code>' + (a.accountCode || '') + '</code></td>' +
                '<td class="is-amt fc-paid">' + fmt.money(a.currentBalance) + '</td></tr>';
        }).join('') || '<tr><td colspan="3" class="fc-empty">No income accounts</td></tr>';

        var expenseRows = (data.expenses || []).map(function (a) {
            return '<tr><td>' + a.accountName + '</td>' +
                '<td><code>' + (a.accountCode || '') + '</code></td>' +
                '<td class="is-amt fc-owing">' + fmt.money(a.currentBalance) + '</td></tr>';
        }).join('') || '<tr><td colspan="3" class="fc-empty">No expense accounts</td></tr>';

        var isProfit = data.netIncome >= 0;

        el.innerHTML = [
            '<div class="is-doc" id="isDoc">',

            '<div class="is-doc-header">',
            '<div class="is-doc-org"><i class="fas fa-star"></i> Shooting Star</div>',
            '<div class="is-doc-title">INCOME STATEMENT</div>',
            '<div class="is-doc-period">' + periodLabel + '</div>',
            '</div>',

            // Revenue
            '<div class="is-section-title is-revenue-title"><i class="fas fa-arrow-circle-up"></i> Revenue</div>',
            '<table class="is-table">',
            '<thead><tr><th>Account</th><th>Code</th><th>Amount</th></tr></thead>',
            '<tbody>' + incomeRows + '</tbody>',
            '<tfoot><tr class="is-subtotal">',
            '<td colspan="2"><strong>Total Revenue</strong></td>',
            '<td class="is-amt fc-paid"><strong>' + fmt.money(data.totalRevenue) + '</strong></td>',
            '</tr></tfoot>',
            '</table>',

            // Expenses
            '<div class="is-section-title is-expense-title" style="margin-top:24px"><i class="fas fa-arrow-circle-down"></i> Expenses</div>',
            '<table class="is-table">',
            '<thead><tr><th>Account</th><th>Code</th><th>Amount</th></tr></thead>',
            '<tbody>' + expenseRows + '</tbody>',
            '<tfoot><tr class="is-subtotal">',
            '<td colspan="2"><strong>Total Expenses</strong></td>',
            '<td class="is-amt fc-owing"><strong>' + fmt.money(data.totalExpenses) + '</strong></td>',
            '</tr></tfoot>',
            '</table>',

            // Net income
            '<div class="is-net ' + (isProfit ? 'is-net-profit' : 'is-net-loss') + '">',
            '<span>' + (isProfit ? 'NET INCOME (Surplus)' : 'NET LOSS (Deficit)') + '</span>',
            '<strong>' + fmt.money(Math.abs(data.netIncome)) + '</strong>',
            '</div>',

            '</div>',
        ].join('');
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('isLoadBtn').addEventListener('click', async function () {
            var year = document.getElementById('isYear').value;
            var term = document.getElementById('isTerm').value;
            var result = await window.incomeStmtFetch(year, term);
            renderStatement(result);
        });

        document.getElementById('isExportBtn').addEventListener('click', function () {
            var doc = document.getElementById('isDoc');
            if (!doc) {
                toast('Load a statement first.', 'warning');
                return;
            }
            var win = window.open('', '_blank');
            win.document.write('<html><head><title>Income Statement</title><style>' +
                'body{font-family:Georgia,serif;padding:24px;max-width:600px;margin:0 auto;}' +
                '.is-doc-header{text-align:center;border-bottom:2px solid #1A3A5C;padding-bottom:12px;margin-bottom:16px;}' +
                '.is-doc-org{font-size:18px;font-weight:700;color:#1A3A5C;letter-spacing:2px;}' +
                '.is-doc-title{font-size:12px;letter-spacing:3px;color:#555;}' +
                '.is-section-title{font-weight:700;font-size:11px;letter-spacing:1px;text-transform:uppercase;padding:12px 0 4px;border-bottom:1px solid #ddd;}' +
                'table{width:100%;border-collapse:collapse;margin-bottom:8px;}' +
                'th,td{padding:6px 8px;font-size:13px;}thead th{background:#f5f5f5;}' +
                '.is-amt{text-align:right;}.is-subtotal td{background:#fafafa;border-top:1px solid #ddd;}' +
                '.is-net{display:flex;justify-content:space-between;padding:12px 8px;font-size:16px;font-weight:700;margin-top:16px;border-radius:4px;}' +
                '.is-net-profit{background:#e6f9f0;color:#0a7a42;}.is-net-loss{background:#fdecea;color:#c0392b;}' +
                '</style></head><body>' + doc.outerHTML + '</body></html>');
            win.document.close();
            win.print();
        });
    }

    function toast(msg, type) {
        var el = document.getElementById('isToast');
        if (!el) return;
        el.textContent = msg;
        el.className = 'fc-toast show ' + (type || 'info');
        clearTimeout(el._t);
        el._t = setTimeout(function () {
            el.classList.remove('show');
        }, 3000);
    }

    // ── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        var t = setInterval(function () {
            if (window.incomeStmtState && window.incomeStmtState._loaded) {
                clearInterval(t);
            }
        }, 30);
    }

    function boot() {
        if (window.incomeStmtState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financeIncomeStatement.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();
})();