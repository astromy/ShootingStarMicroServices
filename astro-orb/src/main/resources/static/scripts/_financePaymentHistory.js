/**
 * _financePaymentHistory.js  —  Data & logic layer for Payment History.
 *
 * Exposes on window:
 *   pyHistState
 *   pyHistData
 *   pyHistLoad()
 *   pyHistFetch(filters) → Bill_PaymentResponse[]
 *   pyHistFetchStudentBill(studentId) → Student_BillResponse
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.pyHistState = {
        institutionCode: _inst,
        allPayments: [],
        filtered: [],
        academicYears: [],
        selectedYear: '',
        selectedTerm: '',
        selectedMethod: '',
        searchQuery: '',
        _loaded: false,
    };

    window.pyHistData = {
        classGroups: [],
        terms: [
            {value: '1st Term', label: '1st Term'},
            {value: '2nd Term', label: '2nd Term'},
            {value: '3rd Term', label: '3rd Term'},
        ],
    };

    // ── HELPERS ──────────────────────────────────────────────────────────────
    function buildYears() {
        var y = new Date().getFullYear(), out = [];
        for (var i = 4; i >= 0; i--) out.push((y - i) + '/' + (y - i + 1));
        window.pyHistState.academicYears = out;
        window.pyHistState.selectedYear = out[out.length - 1];
    }

    function showSplash() {
        if ($) $('.splash').css({display: 'block', background: '#ffffff3d'});
    }

    function hideSplash() {
        if ($) $('.splash').css('display', 'none');
    }

    // ── INITIAL LOAD ─────────────────────────────────────────────────────────
    window.pyHistLoad = async function () {
        buildYears();
        try {
            showSplash();
            var [groups, payments] = await Promise.all([
                fetchPost('getLookUpByType', {val: 'ClassGroup'}),
                fetchPost('get-billPayments-by-institution', {institutionCode: _inst}),
            ]);
            if (groups) window.pyHistData.classGroups = groups;
            if (payments) window.pyHistState.allPayments = payments;
            window.pyHistState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.pyHistState._loaded = true;
            console.error('[_financePaymentHistory] load error:', e);
        }
    };

    // ── FILTER ───────────────────────────────────────────────────────────────
    window.pyHistFilter = function (opts) {
        var all = window.pyHistState.allPayments;
        var q = (opts.search || '').toLowerCase().trim();

        window.pyHistState.filtered = all.filter(function (p) {
            if (opts.year && opts.year !== 'all' && p.academicYear !== opts.year) return false;
            if (opts.term && opts.term !== 'all' && p.term !== opts.term) return false;
            if (opts.method && opts.method !== 'all' && p.paymentMethod !== opts.method) return false;
            if (q) {
                var haystack = [p.studentId, p.paidBy, p.recieptNum].join(' ').toLowerCase();
                if (!haystack.includes(q)) return false;
            }
            return true;
        });
        return window.pyHistState.filtered;
    };

    // ── REFETCH (after filter changes) ───────────────────────────────────────
    window.pyHistFetch = async function (filters) {
        showSplash();
        try {
            var result = await fetchPost('get-billPayments-by-institution', {institutionCode: _inst});
            window.pyHistState.allPayments = result || [];
            hideSplash();
        } catch (e) {
            hideSplash();
        }
        return window.pyHistFilter(filters || {});
    };

    // ── STUDENT BILL ─────────────────────────────────────────────────────────
    window.pyHistFetchStudentBill = async function (studentId) {
        try {
            return await fetchPost('getStudentBillByIdAndInstitution', {
                institutionCode: _inst, studentId: studentId,
            });
        } catch (e) {
            return null;
        }
    };

    // ── SUMMARY STATS ────────────────────────────────────────────────────────
    window.pyHistStats = function (payments) {
        var total = payments.reduce(function (s, p) {
            return s + (p.paymentAmount || 0);
        }, 0);
        var byMethod = {};
        payments.forEach(function (p) {
            var m = p.paymentMethod || 'CASH';
            byMethod[m] = (byMethod[m] || 0) + (p.paymentAmount || 0);
        });
        return {count: payments.length, total: total, byMethod: byMethod};
    };

    // ── EXPORT CSV ──────────────────────────────────────────────────────────
    window.pyHistExport = function (payments) {
        var rows = [['Receipt No.', 'Student ID', 'Amount', 'Method', 'Term', 'Year', 'Date', 'By']];
        payments.forEach(function (p) {
            rows.push([
                p.recieptNum || '', p.studentId || '',
                p.paymentAmount || 0, p.paymentMethod || 'CASH',
                p.term || '', p.academicYear || '',
                p.paymentDate ? new Date(p.paymentDate).toLocaleDateString('en-GB') : '',
                p.paidBy || '',
            ]);
        });
        var csv = rows.map(function (r) {
            return r.map(function (c) {
                return '"' + String(c).replace(/"/g, '""') + '"';
            }).join(',');
        }).join('\n');
        var a = document.createElement('a');
        a.href = URL.createObjectURL(new Blob([csv], {type: 'text/csv'}));
        a.download = 'payment_history_' + _inst + '_' + Date.now() + '.csv';
        a.click();
        URL.revokeObjectURL(a.href);
    };

    window.pyHistFmt = {
        money: function (v) {
            return 'GH₵ ' + (parseFloat(v) || 0).toLocaleString('en-GH', {minimumFractionDigits: 2});
        },
        dateTime: function (d) {
            return d ? new Date(d).toLocaleString('en-GB', {
                day: '2-digit',
                month: 'short',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            }) : '—';
        },
    };

    if (window.copyrights) window.copyrights();
    pyHistLoad();
})();