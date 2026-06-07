/**
 * _financeTrialBalance.js  —  Data & logic layer for Trial Balance.
 *
 * The trial balance lists every ledger account with its DEBIT or CREDIT balance.
 * Total debits must equal total credits — this is the core double-entry check.
 *
 * Exposes on window:
 *   trialBalState
 *   trialBalLoad()
 *   trialBalFetch(year, term) → { accounts[], totalDebits, totalCredits, isBalanced }
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    window.trialBalState = {
        institutionCode: _inst,
        academicYears: [],
        selectedYear: '',
        lastResult: null,
        _loaded: false,
    };

    function buildYears() {
        var y = new Date().getFullYear(), out = [];
        for (var i = 3; i >= 0; i--) out.push(String(y - i));
        window.trialBalState.academicYears = out;
        window.trialBalState.selectedYear = out[out.length - 1];
    }

    function showSplash() {
        if (typeof $ !== 'undefined') $('.splash').css({display: 'block', background: '#ffffff3d'});
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    window.trialBalLoad = async function () {
        buildYears();
        window.trialBalState._loaded = true;
    };

    // ── FETCH ─────────────────────────────────────────────────────────────────
    window.trialBalFetch = async function (year, term) {
        showSplash();
        try {
            var accounts = await fetchPost('ledger/trial-balance', {
                institutionCode: _inst,
                academicYear: year || window.trialBalState.selectedYear,
                term: (term && term !== 'all') ? term : null,
            });

            var arr = Array.isArray(accounts) ? accounts : [];

            // Assign each account its normal-balance side and compute debit/credit columns
            var totalDebits = 0;
            var totalCredits = 0;

            var rows = arr.map(function (a) {
                var isDebitNormal = (a.accountType === 'ASSET' || a.accountType === 'EXPENSE');
                var bal = parseFloat(a.currentBalance) || 0;
                var debit = 0, credit = 0;
                if (isDebitNormal) {
                    if (bal >= 0) debit = bal; else credit = Math.abs(bal);
                } else {
                    if (bal >= 0) credit = bal; else debit = Math.abs(bal);
                }
                totalDebits += debit;
                totalCredits += credit;
                return Object.assign({}, a, {debit: debit, credit: credit});
            });

            var result = {
                accounts: rows,
                totalDebits: totalDebits,
                totalCredits: totalCredits,
                isBalanced: Math.abs(totalDebits - totalCredits) < 0.01,
                year: year,
                term: term,
            };

            window.trialBalState.lastResult = result;
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            console.error('[_financeTrialBalance] fetch error:', e);
            return null;
        }
    };

    window.trialBalFmt = {
        money: function (v) {
            var n = parseFloat(v) || 0;
            return n > 0 ? 'GH₵ ' + n.toLocaleString('en-GH', {minimumFractionDigits: 2}) : '—';
        },
    };

    if (window.copyrights) window.copyrights();
    trialBalFetch();
})();