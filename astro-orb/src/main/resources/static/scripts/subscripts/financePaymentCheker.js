/**
 * financePaymentChecker.js  —  UI renderer for Payment Checker.
 * Two modes: single student quick-lookup and class-wide owing report.
 */
(function () {
    'use strict';
    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financePaymentChecker') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();
    /*
        (function () {
            if (document.getElementById('pyCheckCSS')) return;
            var l = document.createElement('link');
            l.id = 'pyCheckCSS';
            l.rel = 'stylesheet';
            l.href = _base + '../../styles/styles.css';
            document.head.appendChild(l);
        })();*/

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        document.getElementById('wrapper').innerHTML = [
            '<div class="pc-page">',

            '<header class="fc-header">',
            '<div><h1><i class="fas fa-search-dollar"></i> Payment Checker</h1>',
            '<p>Verify a student\'s fee payment status instantly</p></div>',
            '</header>',

            '<div class="pc-body">',

            // LEFT — quick lookup
            '<div class="pc-panel">',
            '<div class="fc-card-head"><i class="fas fa-id-card"></i> Quick Lookup</div>',
            '<div class="pc-lookup-form">',
            '<div class="pc-search-wrap">',
            '<input type="text" id="pcStudentId" placeholder="Enter Student ID…" autofocus>',
            '<button class="fc-btn fc-btn-primary" id="pcLookupBtn"><i class="fas fa-search"></i> Check</button>',
            '</div>',
            '<div id="pcResult" class="pc-result-area">',
            '<div class="pc-idle"><i class="fas fa-user-circle"></i><p>Enter a student ID and press Check</p></div>',
            '</div>',
            '</div>',
            '</div>',

            // RIGHT — class bulk view
            '<div class="pc-panel">',
            '<div class="fc-card-head"><i class="fas fa-users"></i> Class Owing Report</div>',
            '<div class="pc-class-filters">',
            '<select id="pcGroupSel" disabled><option value="">Loading…</option></select>',
            '<select id="pcClassSel" disabled><option value="">Select group first</option></select>',
            '<button class="fc-btn fc-btn-primary" id="pcClassBtn" disabled><i class="fas fa-table"></i> Load</button>',
            '</div>',
            '<div id="pcClassResult">',
            '<div class="pc-idle"><i class="fas fa-chalkboard"></i><p>Select a class to view payment status</p></div>',
            '</div>',
            '</div>',

            '</div>', // pc-body

            '<div class="fc-toast" id="pcToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="pcYear"></span> | Payment Checker</footer>',
            '</div>',
        ].join('');

        document.getElementById('pcYear').textContent = new Date().getFullYear();
    }

    // ── RENDER SINGLE RESULT ─────────────────────────────────────────────────
    function renderResult(result) {
        var el = document.getElementById('pcResult');
        if (!el) return;

        if (!result) {
            el.innerHTML = '<div class="pc-no-record"><i class="fas fa-exclamation-circle"></i><p>No record found for this student ID.</p></div>';
            return;
        }

        var fmt = window.pyCheckFmt;
        var bill = result.bill;
        var statusMeta = {
            CLEARED: {cls: 'pc-cleared', icon: 'fa-check-circle', label: 'CLEARED', msg: 'All fees have been paid.'},
            PARTIAL: {
                cls: 'pc-partial',
                icon: 'fa-adjust',
                label: 'PARTIAL',
                msg: 'Partial payment — balance outstanding.'
            },
            OWING: {
                cls: 'pc-owing',
                icon: 'fa-times-circle',
                label: 'OWING',
                msg: 'Outstanding balance. Payment required.'
            },
            NO_BILL: {cls: 'pc-no-bill', icon: 'fa-minus-circle', label: 'NO BILL', msg: 'No billing record found.'},
        };
        var meta = statusMeta[result.status] || statusMeta['NO_BILL'];

        var billHtml = bill ? [
            '<div class="pc-bill-row"><span>Total Billed</span><strong>' + fmt.money(bill.amountDue) + '</strong></div>',
            '<div class="pc-bill-row"><span>Total Paid</span><strong class="fc-paid">' + fmt.money(bill.amountPaid) + '</strong></div>',
            '<div class="pc-bill-row pc-bill-row-balance"><span>Balance</span>',
            '<strong class="' + (bill.amountBalance > 0 ? 'fc-owing' : 'fc-clear') + '">' + fmt.money(bill.amountBalance) + '</strong></div>',
            '<div class="pc-bill-row"><span>Last Term</span><strong>' + (bill.term || '—') + ' · ' + (bill.academicYear || '—') + '</strong></div>',
        ].join('') : '';

        var lastPayment = result.payments && result.payments.length
            ? result.payments.sort(function (a, b) {
                return new Date(b.paymentDate) - new Date(a.paymentDate);
            })[0]
            : null;

        var lastPayHtml = lastPayment
            ? '<div class="pc-last-pay"><i class="fas fa-clock"></i> Last payment: <strong>' +
            fmt.money(lastPayment.paymentAmount) + '</strong> on ' + fmt.date(lastPayment.paymentDate) +
            ' via ' + (lastPayment.paymentMethod || 'CASH') + '</div>'
            : '';

        el.innerHTML = [
            '<div class="pc-status-banner ' + meta.cls + '">',
            '<i class="fas ' + meta.icon + '"></i>',
            '<div><div class="pc-status-label">' + meta.label + '</div>',
            '<div class="pc-status-msg">' + meta.msg + '</div></div>',
            '</div>',
            '<div class="pc-student-id"><i class="fas fa-id-badge"></i> ' + result.studentId + '</div>',
            billHtml,
            lastPayHtml,
            result.payments.length
                ? '<div class="pc-pay-count"><i class="fas fa-receipt"></i> ' + result.payments.length + ' payment record' + (result.payments.length > 1 ? 's' : '') + '</div>'
                : '',
        ].join('');
    }

    // ── RENDER CLASS TABLE ────────────────────────────────────────────────────
    function renderClassResult(bills) {
        var el = document.getElementById('pcClassResult');
        var fmt = window.pyCheckFmt;
        if (!el) return;

        if (!bills || !bills.length) {
            el.innerHTML = '<div class="pc-idle"><i class="fas fa-inbox"></i><p>No billing records for this class.</p></div>';
            return;
        }

        var owing = bills.filter(function (b) {
            return (b.amountBalance || 0) > 0;
        });
        var cleared = bills.filter(function (b) {
            return (b.amountBalance || 0) <= 0;
        });

        var rows = bills.map(function (b) {
            var bal = b.amountBalance || 0;
            var cls = bal <= 0 ? 'pc-row-clear' : (bal < (b.amountDue * 0.5) ? 'pc-row-partial' : 'pc-row-owing');
            return '<tr class="' + cls + '">' +
                '<td>' + b.studentId + '</td>' +
                '<td class="fc-money">' + fmt.money(b.amountDue) + '</td>' +
                '<td class="fc-money fc-paid">' + fmt.money(b.amountPaid) + '</td>' +
                '<td class="fc-money ' + (bal > 0 ? 'fc-owing' : 'fc-clear') + '">' + fmt.money(bal) + '</td>' +
                '<td>' + (b.term || '—') + '</td>' +
                '</tr>';
        }).join('');

        el.innerHTML = [
            '<div class="pc-class-summary">',
            '<span class="pc-tag pc-tag-total">' + bills.length + ' students</span>',
            '<span class="pc-tag pc-tag-clear">' + cleared.length + ' cleared</span>',
            '<span class="pc-tag pc-tag-owing">' + owing.length + ' owing</span>',
            '</div>',
            '<div class="pc-table-wrap">',
            '<table class="fc-table">',
            '<thead><tr><th>Student ID</th><th>Billed</th><th>Paid</th><th>Balance</th><th>Term</th></tr></thead>',
            '<tbody>' + rows + '</tbody>',
            '</table>',
            '</div>',
        ].join('');
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {
        // Quick lookup
        async function doLookup() {
            var id = document.getElementById('pcStudentId').value.trim();
            if (!id) {
                toast('Enter a student ID.', 'warning');
                return;
            }
            var result = await window.pyCheckLookup(id);
            renderResult(result);
        }

        document.getElementById('pcLookupBtn').addEventListener('click', doLookup);
        document.getElementById('pcStudentId').addEventListener('keydown', function (e) {
            if (e.key === 'Enter') doLookup();
        });

        // Group → classes
        document.getElementById('pcGroupSel').addEventListener('change', function () {
            var classes = window.pyCheckClassesByGroup(this.value);
            var sel = document.getElementById('pcClassSel');
            sel.innerHTML = '<option value="">Select class…</option>' +
                classes.map(function (c) {
                    return '<option>' + c.name + '</option>';
                }).join('');
            sel.disabled = !classes.length;
            document.getElementById('pcClassBtn').disabled = !classes.length;
        });

        // Class bulk check
        document.getElementById('pcClassBtn').addEventListener('click', async function () {
            var cls = document.getElementById('pcClassSel').value;
            if (!cls) {
                toast('Select a class.', 'warning');
                return;
            }
            var bills = await window.pyCheckClassLookup(cls);
            renderClassResult(bills);
        });
    }

    // ── POPULATE GROUPS ──────────────────────────────────────────────────────
    function populateGroups() {
        var sel = document.getElementById('pcGroupSel');
        if (!sel) return;
        var groups = window.pyCheckState.classGroups;
        sel.innerHTML = '<option value="">Select group…</option>' +
            groups.map(function (g) {
                return '<option value="' + g.name + '">' + g.name + '</option>';
            }).join('');
        sel.disabled = false;
    }

    function toast(msg, type) {
        var el = document.getElementById('pcToast');
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
            if (window.pyCheckState && window.pyCheckState._loaded) {
                clearInterval(t);
                populateGroups();
            }
        }, 30);
    }

    function boot() {
        if (window.pyCheckState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financePaymentChecker.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();
})();