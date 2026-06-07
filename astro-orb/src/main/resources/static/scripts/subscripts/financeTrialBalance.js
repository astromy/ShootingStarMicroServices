/**
 * financeTrialBalance.js  —  UI renderer for Trial Balance.
 *
 * Shows all ledger accounts with their DEBIT and CREDIT balances.
 * Totals row validates that debits = credits (the fundamental double-entry check).
 * A red warning banner is shown if the trial balance does not balance.
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financeTrialBalance') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    (function () {
        if (document.getElementById('trialBalCSS')) return;
        var l = document.createElement('link');
        l.id = 'trialBalCSS';
        l.rel = 'stylesheet';
        l.href = _base + '../../styles/style.css';
        document.head.appendChild(l);
    })();

    var TYPE_LABELS = {ASSET: 'Asset', LIABILITY: 'Liability', INCOME: 'Income', EXPENSE: 'Expense', EQUITY: 'Equity'};

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        var s = window.trialBalState;
        var yearOpts = s.academicYears.map(function (y) {
            return '<option ' + (y === s.selectedYear ? 'selected' : '') + '>' + y + '</option>';
        }).join('');
        var termOpts = ['all', '1st Term', '2nd Term', '3rd Term'].map(function (t) {
            return '<option value="' + t + '">' + (t === 'all' ? 'Full Year' : t) + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = [
            '<div class="tb-page">',

            '<header class="fc-header">',
            '<div><h1><i class="fas fa-balance-scale"></i> Trial Balance</h1>',
            '<p>Verify that total debits equal total credits — the double-entry check</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="tbPrintBtn"><i class="fas fa-print"></i> Print</button>',
            '</div>',
            '</header>',

            '<div class="ps-generate-bar">',
            '<div class="ps-generate-controls">',
            '<div class="fc-field"><label>Academic Year</label><select id="tbYear">' + yearOpts + '</select></div>',
            '<div class="fc-field"><label>Term</label><select id="tbTerm">' + termOpts + '</select></div>',
            '</div>',
            '<button class="fc-btn fc-btn-primary" id="tbLoadBtn"><i class="fas fa-sync-alt"></i> Load Trial Balance</button>',
            '</div>',

            '<div id="tbContent">',
            '<div class="fc-empty tb-placeholder"><i class="fas fa-balance-scale"></i><p>Select a period and click Load Trial Balance</p></div>',
            '</div>',

            '<div class="fc-toast" id="tbToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="tbYear2"></span> | Trial Balance</footer>',
            '</div>',
        ].join('');

        document.getElementById('tbYear2').textContent = new Date().getFullYear();
    }

    // ── RENDER ───────────────────────────────────────────────────────────────
    function renderTrialBalance(data) {
        var el = document.getElementById('tbContent');
        if (!data) {
            el.innerHTML = '<div class="fc-empty"><i class="fas fa-exclamation-circle"></i> No data available.</div>';
            return;
        }

        var fmt = window.trialBalFmt;
        var periodLabel = (data.term && data.term !== 'all') ? data.term + ' — ' : 'Year ';
        periodLabel += data.year || '';

        var balanceWarning = !data.isBalanced
            ? '<div class="tb-imbalance-banner"><i class="fas fa-exclamation-triangle"></i> ' +
            'TRIAL BALANCE DOES NOT BALANCE — Debits and credits differ by GH₵ ' +
            Math.abs(data.totalDebits - data.totalCredits).toLocaleString('en-GH', {minimumFractionDigits: 2}) +
            '. Check for posting errors.</div>'
            : '<div class="tb-balanced-banner"><i class="fas fa-check-circle"></i> Trial balance is balanced.</div>';

        var rows = (data.accounts || []).map(function (a) {
            return '<tr>' +
                '<td><code>' + (a.accountCode || '') + '</code></td>' +
                '<td>' + a.accountName + '</td>' +
                '<td><span class="lg-type-badge lg-' + (a.accountType || '').toLowerCase() + '">' +
                (TYPE_LABELS[a.accountType] || a.accountType) + '</span></td>' +
                '<td class="tb-debit">' + fmt.money(a.debit) + '</td>' +
                '<td class="tb-credit">' + fmt.money(a.credit) + '</td>' +
                '</tr>';
        }).join('') || '<tr><td colspan="5" class="fc-empty">No accounts found.</td></tr>';

        el.innerHTML = [
            '<div id="tbDoc">',

            '<div class="is-doc-header">',
            '<div class="is-doc-org"><i class="fas fa-star"></i> Shooting Star</div>',
            '<div class="is-doc-title">TRIAL BALANCE</div>',
            '<div class="is-doc-period">' + periodLabel + '</div>',
            '</div>',

            balanceWarning,

            '<div class="ph-table-wrap">',
            '<table class="fc-table tb-table">',
            '<thead><tr>',
            '<th>Code</th><th>Account Name</th><th>Type</th>',
            '<th class="tb-debit">Debit (GH₵)</th><th class="tb-credit">Credit (GH₵)</th>',
            '</tr></thead>',
            '<tbody>' + rows + '</tbody>',
            '<tfoot>',
            '<tr class="tb-totals">',
            '<td colspan="3"><strong>TOTALS</strong></td>',
            '<td class="tb-debit"><strong>' +
            (data.totalDebits || 0).toLocaleString('en-GH', {minimumFractionDigits: 2}) + '</strong></td>',
            '<td class="tb-credit"><strong>' +
            (data.totalCredits || 0).toLocaleString('en-GH', {minimumFractionDigits: 2}) + '</strong></td>',
            '</tr>',
            '</tfoot>',
            '</table>',
            '</div>',

            '</div>',
        ].join('');
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('tbLoadBtn').addEventListener('click', async function () {
            var year = document.getElementById('tbYear').value;
            var term = document.getElementById('tbTerm').value;
            var result = await window.trialBalFetch(year, term);
            renderTrialBalance(result);
        });

        document.getElementById('tbPrintBtn').addEventListener('click', function () {
            var doc = document.getElementById('tbDoc');
            if (!doc) {
                toast('Load a trial balance first.', 'warning');
                return;
            }
            var win = window.open('', '_blank');
            win.document.write('<html><head><title>Trial Balance</title><style>' +
                'body{font-family:Georgia,serif;padding:24px;max-width:700px;margin:0 auto;}' +
                '.is-doc-header{text-align:center;border-bottom:2px solid #1A3A5C;padding-bottom:12px;margin-bottom:16px;}' +
                '.is-doc-org{font-size:18px;font-weight:700;color:#1A3A5C;letter-spacing:2px;}' +
                '.is-doc-title{font-size:12px;letter-spacing:3px;color:#555;}' +
                'table{width:100%;border-collapse:collapse;}' +
                'th,td{padding:7px 10px;font-size:13px;border-bottom:1px solid #eee;}' +
                'thead th{background:#1A3A5C;color:#fff;}' +
                '.tb-totals td{background:#f5f5f5;border-top:2px solid #1A3A5C;font-weight:700;}' +
                '.tb-debit{text-align:right;color:#1A3A5C;}.tb-credit{text-align:right;color:#0a7a42;}' +
                '.tb-balanced-banner{background:#e6f9f0;color:#0a7a42;padding:8px 12px;border-radius:4px;margin-bottom:12px;}' +
                '.tb-imbalance-banner{background:#fdecea;color:#c0392b;padding:8px 12px;border-radius:4px;margin-bottom:12px;}' +
                '</style></head><body>' + doc.outerHTML + '</body></html>');
            win.document.close();
            win.print();
        });
    }

    function toast(msg, type) {
        var el = document.getElementById('tbToast');
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
            if (window.trialBalState && window.trialBalState._loaded) {
                clearInterval(t);
            }
        }, 30);
    }

    function boot() {
        if (window.trialBalState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financeTrialBalance.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();
})();