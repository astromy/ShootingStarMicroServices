/**
 * libraryCirculation.js  —  UI renderer for Library Circulation (Admin).
 *
 * Rebuilt to use the SAME design system as Fee Collection (financeShared.css
 * — .fc-page, .fc-body/.fc-col two-column layout, .fc-card, .fc-field,
 * .fc-table, .fc-btn, .fc-toast) rather than invented one-off classes.
 *
 * Dynamically loads _libraryCirculation.js — same two-file pattern as
 * libraryCatalogue.js + _libraryCatalogue.js.
 */
(function () {
    'use strict';

    // ─── BUILD DOM ────────────────────────────────────────────────────────────
    function buildDOM() {
        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [

            '<header class="fc-header">',
            '<div><h1><i class="fas fa-exchange-alt"></i> Library Circulation</h1>',
            '<p>Check books in and out, and track overdue loans</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="lcirRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '</div>',
            '</header>',

            '<div id="lcirOverdueBanner"></div>',

            '<div class="fc-body">',

            '<div class="fc-col fc-col-left">',
            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-hand-holding"></i> Check Out</div>',
            '<div class="fc-card-body">',
            '<div class="fc-field"><label>Book Code <span class="req">*</span></label><input type="text" id="lcirCheckoutBookCode" placeholder="Scan or type book code"></div>',
            '<div class="fc-field"><label>Student Index <span class="req">*</span></label><input type="text" id="lcirCheckoutStudentIndex" placeholder="e.g. 26-00147-0001"></div>',
            '<div class="fc-field"><label>Student Name (optional)</label><input type="text" id="lcirCheckoutStudentName"></div>',
            '<div class="fc-field"><label>Loan Period (days)</label><input type="number" id="lcirCheckoutDays" value="14" min="1"></div>',
            '<div class="fc-actions"><button class="fc-btn fc-btn-primary" id="lcirCheckoutBtn"><i class="fas fa-check"></i> Check Out</button></div>',
            '</div>',
            '</div>',
            '</div>',

            '<div class="fc-col fc-col-left">',
            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-undo"></i> Return</div>',
            '<div class="fc-card-body">',
            '<div class="fc-field"><label>Book Code <span class="req">*</span></label><input type="text" id="lcirReturnBookCode" placeholder="Scan or type book code"></div>',
            '<div class="fc-field"><label>Student Index <span class="req">*</span></label><input type="text" id="lcirReturnStudentIndex" placeholder="e.g. 26-00147-0001"></div>',
            '<div class="fc-actions"><button class="fc-btn fc-btn-primary" id="lcirReturnBtn"><i class="fas fa-check"></i> Return</button></div>',
            '</div>',
            '</div>',
            '</div>',

            '</div>', // .fc-body

            '<div class="fc-card" style="margin:0 24px 24px;">',
            '<div class="fc-card-head"><i class="fas fa-list"></i> Active Loans</div>',
            '<div class="ph-table-wrap" style="margin:0; border:none; box-shadow:none;">',
            '<table class="fc-table" id="lcirActiveTable">',
            '<thead><tr>',
            '<th>Book</th><th>Student</th><th>Checked Out</th><th>Due</th><th>Status</th><th>Actions</th>',
            '</tr></thead><tbody></tbody>',
            '</table>',
            '</div>',
            '</div>',

            '<div class="fc-toast" id="lcirToast"></div>',

        ].join('') + '</div>';
    }

    function escapeHtml(s) {
        return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
            return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c];
        });
    }

    function formatDate(iso) {
        if (!iso) return '—';
        var d = new Date(iso);
        return isNaN(d.getTime()) ? iso : d.toLocaleDateString();
    }

    // ─── TOAST ──────────────────────────────────────────────────────────────
    var toastTimer = null;

    function showToast(message, type) {
        var el = document.getElementById('lcirToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () {
            el.classList.remove('show');
        }, 3200);
    }

    // ─── OVERDUE BANNER (reuses the trial-balance-style banner pattern) ─────
    function renderOverdueBanner() {
        var overdue = window.libraryLoanData.activeLoans.filter(function (l) {
            return l.overdue;
        });
        var el = document.getElementById('lcirOverdueBanner');
        if (!overdue.length) {
            el.innerHTML = '';
            return;
        }
        el.innerHTML = '<div class="tb-imbalance-banner" style="margin:16px 24px 0;">' +
            '<i class="fas fa-exclamation-triangle"></i> ' +
            overdue.length + ' loan' + (overdue.length === 1 ? '' : 's') + ' overdue: ' +
            overdue.map(function (l) {
                return escapeHtml(l.bookTitle) + ' (' + escapeHtml(l.studentIndex) + ')';
            }).join(', ') +
            '</div>';
    }

    // ─── ACTIVE LOANS TABLE ─────────────────────────────────────────────────
    function renderActiveTable() {
        var loans = window.libraryLoanData.activeLoans;
        var tbody = document.querySelector('#lcirActiveTable tbody');

        if (!loans.length) {
            tbody.innerHTML = '<tr><td colspan="6" class="fc-empty">No active loans right now.</td></tr>';
            return;
        }

        tbody.innerHTML = loans.map(function (l) {
            var statusBadge = l.overdue
                ? '<span class="badge badge-danger">Overdue</span>'
                : '<span class="badge badge-success">On Time</span>';
            return '<tr>' +
                '<td>' + escapeHtml(l.bookTitle) + ' <small>(' + escapeHtml(l.bookCode) + ')</small></td>' +
                '<td>' + escapeHtml(l.studentName || l.studentIndex) + '</td>' +
                '<td>' + formatDate(l.checkoutDate) + '</td>' +
                '<td>' + formatDate(l.dueDate) + '</td>' +
                '<td>' + statusBadge + '</td>' +
                '<td><button class="fc-btn fc-btn-secondary fc-btn-sm lcir-quick-return-btn" data-book="' +
                escapeHtml(l.bookCode) + '" data-student="' + escapeHtml(l.studentIndex) +
                '"><i class="fas fa-undo"></i> Return</button></td>' +
                '</tr>';
        }).join('');

        tbody.querySelectorAll('.lcir-quick-return-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                document.getElementById('lcirReturnBookCode').value = this.dataset.book;
                document.getElementById('lcirReturnStudentIndex').value = this.dataset.student;
                doReturn();
            });
        });
    }

    // ─── ACTIONS ────────────────────────────────────────────────────────────
    async function doCheckout() {
        var payload = {
            institutionCode: window.libraryLoanState.institutionCode,
            bookCode: document.getElementById('lcirCheckoutBookCode').value.trim(),
            studentIndex: document.getElementById('lcirCheckoutStudentIndex').value.trim(),
            studentName: document.getElementById('lcirCheckoutStudentName').value.trim(),
            processedBy: window.libraryLoanState.staffId || 'web-admin',
            loanPeriodDays: parseInt(document.getElementById('lcirCheckoutDays').value, 10) || 14,
        };
        if (!payload.bookCode || !payload.studentIndex) {
            showToast('Book code and student index are both required.', 'warning');
            return;
        }
        try {
            await window.libraryCheckout(payload);
            showToast('Checked out successfully.', 'success');
            document.getElementById('lcirCheckoutBookCode').value = '';
            document.getElementById('lcirCheckoutStudentIndex').value = '';
            document.getElementById('lcirCheckoutStudentName').value = '';
            await refreshLoans();
        } catch (e) {
            showToast('Checkout failed: ' + (e.message || e), 'error');
        }
    }

    async function doReturn() {
        var payload = {
            institutionCode: window.libraryLoanState.institutionCode,
            bookCode: document.getElementById('lcirReturnBookCode').value.trim(),
            studentIndex: document.getElementById('lcirReturnStudentIndex').value.trim(),
            processedBy: window.libraryLoanState.staffId || 'web-admin',
        };
        if (!payload.bookCode || !payload.studentIndex) {
            showToast('Book code and student index are both required.', 'warning');
            return;
        }
        try {
            await window.libraryReturn(payload);
            showToast('Returned successfully.', 'success');
            document.getElementById('lcirReturnBookCode').value = '';
            document.getElementById('lcirReturnStudentIndex').value = '';
            await refreshLoans();
        } catch (e) {
            showToast('Return failed: ' + (e.message || e), 'error');
        }
    }

    async function refreshLoans() {
        await window.libraryFetchActiveLoans();
        renderOverdueBanner();
        renderActiveTable();
    }

    // ─── WIRE EVENTS ────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('lcirCheckoutBtn').addEventListener('click', doCheckout);
        document.getElementById('lcirReturnBtn').addEventListener('click', doReturn);
        document.getElementById('lcirRefreshBtn').addEventListener('click', refreshLoans);
    }

    // ─── INIT ───────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        renderOverdueBanner();
        renderActiveTable();
        waitForRealData();
    }

    function waitForRealData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.libraryLoanState._loaded || attempts > 200) {
                clearInterval(timer);
                renderOverdueBanner();
                renderActiveTable();
                if (attempts > 200) console.warn('[libraryCirculation] API data slow — rendering partial data.');
            }
        }, 30);
    }

    function loadLogicScript() {
        var el = document.currentScript;
        var base = el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';

        if (window.libraryLoanData && window.libraryLoanState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = base + '../_libraryCirculation.js';
        s.onload = function () {
            if (window.libraryLoanData && window.libraryLoanState) {
                init();
            } else {
                console.error('[libraryCirculation] _libraryCirculation.js loaded but window.libraryLoanData is not defined.');
            }
        };
        s.onerror = function () {
            console.error('[libraryCirculation] Could not load: ' + base + '../_libraryCirculation.js');
        };
        document.body.appendChild(s);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initLibraryCirculation = init;

})();
