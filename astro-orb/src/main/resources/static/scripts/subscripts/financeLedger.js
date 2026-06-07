/**
 * financeLedgers.js  —  UI renderer for Ledger Books (Chart of Accounts).
 *
 * Features:
 *   - Chart of accounts table grouped by type (ASSET / LIABILITY / INCOME / EXPENSE / EQUITY)
 *   - Add / edit account modal
 *   - Account detail drill-down: journal entries for that account
 *   - Balance summary strip
 *   - Filter by account type
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financeLedgers') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    (function () {
        if (document.getElementById('ledgerCSS')) return;
        var l = document.createElement('link');
        l.id = 'ledgerCSS';
        l.rel = 'stylesheet';
        l.href = _base + '../../styles/style.css';
        document.head.appendChild(l);
    })();

    // ── TYPE META ─────────────────────────────────────────────────────────────
    var TYPE_META = {
        ASSET: {cls: 'lg-asset', icon: 'fa-landmark', label: 'Assets'},
        LIABILITY: {cls: 'lg-liability', icon: 'fa-file-contract', label: 'Liabilities'},
        INCOME: {cls: 'lg-income', icon: 'fa-arrow-circle-up', label: 'Income'},
        EXPENSE: {cls: 'lg-expense', icon: 'fa-arrow-circle-down', label: 'Expenses'},
        EQUITY: {cls: 'lg-equity', icon: 'fa-balance-scale', label: 'Equity'},
    };

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        var typeFilterOpts = '<option value="all">All Types</option>' +
            window.ledgerAccountTypes.map(function (t) {
                return '<option value="' + t + '">' + TYPE_META[t].label + '</option>';
            }).join('');

        document.getElementById('wrapper').innerHTML = [
            '<div class="lg-page">',

            '<header class="fc-header">',
            '<div><h1><i class="fas fa-book"></i> Ledger Books</h1>',
            '<p>Chart of accounts — all ledger accounts with running balances</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="lgRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '<button class="fc-btn fc-btn-primary"   id="lgAddBtn"><i class="fas fa-plus"></i> New Account</button>',
            '</div>',
            '</header>',

            // Summary cards (one per type)
            '<div class="lg-type-strip" id="lgTypeStrip">',
            window.ledgerAccountTypes.map(function (t) {
                var m = TYPE_META[t];
                return '<div class="lg-type-card ' + m.cls + '">' +
                    '<i class="fas ' + m.icon + '"></i>' +
                    '<div><div class="lg-type-label">' + m.label + '</div>' +
                    '<div class="lg-type-total" id="lgTotal' + t + '">GH₵ 0</div></div>' +
                    '</div>';
            }).join(''),
            '</div>',

            // Controls
            '<div class="ph-controls">',
            '<div class="ph-search-wrap"><i class="fas fa-search"></i><input type="text" id="lgSearch" placeholder="Account name or code…"></div>',
            '<select id="lgTypeSel">' + typeFilterOpts + '</select>',
            '<button class="fc-btn fc-btn-primary" id="lgFilterBtn"><i class="fas fa-filter"></i> Filter</button>',
            '</div>',

            // Table
            '<div class="ph-table-wrap">',
            '<table class="fc-table" id="lgTable">',
            '<thead><tr>',
            '<th>Code</th><th>Account Name</th><th>Type</th>',
            '<th>Description</th><th>Balance</th><th></th>',
            '</tr></thead>',
            '<tbody id="lgTableBody">',
            '<tr><td colspan="6" class="fc-empty"><i class="fas fa-circle-notch fa-spin"></i> Loading accounts…</td></tr>',
            '</tbody>',
            '</table>',
            '</div>',

            // Add/Edit modal
            '<div class="fc-modal-overlay" id="lgAccountModal">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head"><h5 id="lgModalTitle"><i class="fas fa-book-open"></i> New Ledger Account</h5>',
            '<button class="fc-modal-close" id="lgModalClose">&times;</button></div>',
            '<div class="fc-modal-body" id="lgModalBody"></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="lgModalCancelBtn">Cancel</button>',
            '<button class="fc-btn fc-btn-primary"   id="lgModalSaveBtn"><i class="fas fa-save"></i> Save</button>',
            '</div>',
            '</div></div>',

            // Journal entries drill-down modal
            '<div class="fc-modal-overlay" id="lgJournalModal">',
            '<div class="fc-modal fc-modal-lg">',
            '<div class="fc-modal-head"><h5 id="lgJournalTitle"><i class="fas fa-list-alt"></i> Journal Entries</h5>',
            '<button class="fc-modal-close" id="lgJournalClose">&times;</button></div>',
            '<div class="fc-modal-body" id="lgJournalBody"></div>',
            '<div class="fc-modal-foot"><button class="fc-btn fc-btn-secondary" id="lgJournalCloseBtn">Close</button></div>',
            '</div></div>',

            '<div class="fc-toast" id="lgToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="lgYear"></span> | Ledger Books</footer>',
            '</div>',
        ].join('');

        document.getElementById('lgYear').textContent = new Date().getFullYear();
    }

    // ── RENDER TABLE ─────────────────────────────────────────────────────────
    function renderTable(accounts) {
        var fmt = window.ledgerFmt;
        var body = document.getElementById('lgTableBody');
        if (!body) return;

        if (!accounts || !accounts.length) {
            body.innerHTML = '<tr><td colspan="6" class="fc-empty"><i class="fas fa-inbox"></i> No accounts found. Add your first ledger account.</td></tr>';
            return;
        }

        body.innerHTML = accounts.map(function (a) {
            var m = TYPE_META[a.accountType] || {cls: '', label: a.accountType};
            var bal = fmt.balance(a.currentBalance, a.accountType);
            return '<tr>' +
                '<td><code>' + (a.accountCode || '—') + '</code></td>' +
                '<td><strong>' + a.accountName + '</strong></td>' +
                '<td><span class="lg-type-badge ' + m.cls + '">' + m.label + '</span></td>' +
                '<td>' + (a.description || '—') + '</td>' +
                '<td class="fc-money' + (bal.warn ? ' fc-owing' : '') + '">' + bal.text + '</td>' +
                '<td>' +
                '<button class="fc-btn-icon lg-journal-btn" data-id="' + a.ledgerBookId + '" data-name="' + a.accountName + '" title="View entries"><i class="fas fa-list-alt"></i></button>' +
                '</td>' +
                '</tr>';
        }).join('');

        body.querySelectorAll('.lg-journal-btn').forEach(function (btn) {
            btn.addEventListener('click', async function () {
                await openJournalModal(parseInt(this.dataset.id), this.dataset.name);
            });
        });
    }

    // ── RENDER TYPE TOTALS ────────────────────────────────────────────────────
    function renderTypeTotals(accounts) {
        var fmt = window.ledgerFmt;
        var summary = window.ledgerSummary(accounts);
        window.ledgerAccountTypes.forEach(function (t) {
            var el = document.getElementById('lgTotal' + t);
            if (el) el.textContent = fmt.money(summary[t] || 0);
        });
    }

    // ── ACCOUNT FORM ──────────────────────────────────────────────────────────
    function openAccountModal(account) {
        var isEdit = !!account;
        var typeOpts = window.ledgerAccountTypes.map(function (t) {
            return '<option value="' + t + '" ' + (account && account.accountType === t ? 'selected' : '') + '>' +
                TYPE_META[t].label + '</option>';
        }).join('');

        document.getElementById('lgModalTitle').innerHTML =
            '<i class="fas fa-book-open"></i> ' + (isEdit ? 'Edit Account' : 'New Ledger Account');

        document.getElementById('lgModalBody').innerHTML = [
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Account Name <span class="req">*</span></label>',
            '<input type="text" id="lgAccName" value="' + (account ? account.accountName : '') + '" placeholder="e.g. Cash at Hand"></div>',
            '<div class="fc-field"><label>Account Code</label>',
            '<input type="text" id="lgAccCode" value="' + (account ? (account.accountCode || '') : '') + '" placeholder="e.g. 1001"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Account Type <span class="req">*</span></label>',
            '<select id="lgAccType">' + typeOpts + '</select></div>',
            '<div class="fc-field"><label>Description</label>',
            '<input type="text" id="lgAccDesc" value="' + (account ? (account.description || '') : '') + '" placeholder="Optional"></div>',
            '</div>',
        ].join('');

        document.getElementById('lgAccountModal').classList.add('open');

        document.getElementById('lgModalSaveBtn').onclick = async function () {
            var name = document.getElementById('lgAccName').value.trim();
            var type = document.getElementById('lgAccType').value;
            if (!name || !type) {
                toast('Account name and type are required.', 'error');
                return;
            }
            try {
                await window.ledgerCreate({
                    ledgerBookId: account ? account.ledgerBookId : null,
                    accountName: name,
                    accountCode: document.getElementById('lgAccCode').value.trim(),
                    accountType: type,
                    description: document.getElementById('lgAccDesc').value.trim(),
                });
                document.getElementById('lgAccountModal').classList.remove('open');
                applyAndRender();
                toast((isEdit ? 'Account updated.' : 'Account created.'), 'success');
            } catch (e) {
                toast('Save failed: ' + e.message, 'error');
            }
        };
    }

    // ── JOURNAL ENTRIES MODAL ─────────────────────────────────────────────────
    async function openJournalModal(bookId, name) {
        document.getElementById('lgJournalTitle').innerHTML = '<i class="fas fa-list-alt"></i> ' + name + ' — Journal Entries';
        document.getElementById('lgJournalBody').innerHTML =
            '<div class="fc-empty"><i class="fas fa-circle-notch fa-spin"></i> Loading entries…</div>';
        document.getElementById('lgJournalModal').classList.add('open');

        var fmt = window.ledgerFmt;
        var records = await window.ledgerFetchRecords(bookId, window.ledgerState.selectedYear, null);

        if (!records.length) {
            document.getElementById('lgJournalBody').innerHTML =
                '<div class="fc-empty"><i class="fas fa-inbox"></i> No journal entries found.</div>';
            return;
        }

        var rows = records.map(function (r) {
            var isDebit = r.entryType === 'DEBIT';
            return '<tr>' +
                '<td>' + fmt.dateTime(r.entryDate) + '</td>' +
                '<td><span class="lg-entry-' + (isDebit ? 'debit' : 'credit') + '">' + r.entryType + '</span></td>' +
                '<td class="fc-money' + (isDebit ? '' : ' fc-paid') + '">' + fmt.money(r.amount) + '</td>' +
                '<td>' + (r.description || '—') + '</td>' +
                '<td><code>' + (r.sourceReference || '—') + '</code></td>' +
                '<td>' + (r.sourceType || '—') + '</td>' +
                '</tr>';
        }).join('');

        document.getElementById('lgJournalBody').innerHTML =
            '<table class="fc-table"><thead><tr>' +
            '<th>Date</th><th>Type</th><th>Amount</th><th>Description</th><th>Reference</th><th>Source</th>' +
            '</tr></thead><tbody>' + rows + '</tbody></table>';
    }

    // ── FILTER & RENDER ──────────────────────────────────────────────────────
    function applyAndRender() {
        var accounts = window.ledgerFilter({
            type: document.getElementById('lgTypeSel')?.value,
            search: document.getElementById('lgSearch')?.value,
        });
        renderTable(accounts);
        renderTypeTotals(window.ledgerState.allAccounts);
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('lgFilterBtn').addEventListener('click', applyAndRender);
        document.getElementById('lgSearch').addEventListener('keydown', function (e) {
            if (e.key === 'Enter') applyAndRender();
        });
        document.getElementById('lgRefreshBtn').addEventListener('click', async function () {
            await window.ledgerFetchAll();
            applyAndRender();
        });
        document.getElementById('lgAddBtn').addEventListener('click', function () {
            openAccountModal(null);
        });

        document.getElementById('lgModalClose').addEventListener('click', function () {
            document.getElementById('lgAccountModal').classList.remove('open');
        });
        document.getElementById('lgModalCancelBtn').addEventListener('click', function () {
            document.getElementById('lgAccountModal').classList.remove('open');
        });
        document.getElementById('lgAccountModal').addEventListener('click', function (e) {
            if (e.target === this) this.classList.remove('open');
        });

        document.getElementById('lgJournalClose').addEventListener('click', function () {
            document.getElementById('lgJournalModal').classList.remove('open');
        });
        document.getElementById('lgJournalCloseBtn').addEventListener('click', function () {
            document.getElementById('lgJournalModal').classList.remove('open');
        });
        document.getElementById('lgJournalModal').addEventListener('click', function (e) {
            if (e.target === this) this.classList.remove('open');
        });
    }

    function toast(msg, type) {
        var el = document.getElementById('lgToast');
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
            if (window.ledgerState && window.ledgerState._loaded) {
                clearInterval(t);
                applyAndRender();
            }
        }, 30);
    }

    function boot() {
        if (window.ledgerState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financeLedgers.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();
})();