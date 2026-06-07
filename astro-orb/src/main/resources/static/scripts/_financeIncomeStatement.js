/**
 * _financeIncomeStatement.js  —  Data & logic layer for Income Statement.
 *
 * Derives a P&L from the ledger accounts:
 *   Revenue (INCOME accounts)  − Expenses (EXPENSE accounts)  =  Net Income
 *
 * Exposes on window:
 *   incomeStmtState
 *   incomeStmtLoad()
 *   incomeStmtFetch(year, term) → { income[], expenses[], netIncome }
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    window.incomeStmtState = {
        institutionCode: _inst,
        academicYears: [],
        selectedYear: '',
        selectedTerm: 'all',
        lastResult: null,
        _loaded: false,
    };

    var TERMS = ['all', '1st Term', '2nd Term', '3rd Term'];

    function buildYears() {
        var y = new Date().getFullYear(), out = [];
        for (var i = 3; i >= 0; i--) out.push(String(y - i));
        window.incomeStmtState.academicYears = out;
        window.incomeStmtState.selectedYear = out[out.length - 1];
    }

    function showSplash() {
        if (typeof $ !== 'undefined') $('.splash').css({display: 'block', background: '#ffffff3d'});
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    // ── LOAD ─────────────────────────────────────────────────────────────────
    window.incomeStmtLoad = async function () {
        buildYears();
        window.incomeStmtState._loaded = true;
    };

    // ── FETCH ─────────────────────────────────────────────────────────────────
    window.incomeStmtFetch = async function (year, term) {
        showSplash();
        try {
            var accounts = await fetchPost('ledger/income-statement', {
                institutionCode: _inst,
                academicYear: year || window.incomeStmtState.selectedYear,
                term: (term && term !== 'all') ? term : null,
            });

            var arr = Array.isArray(accounts) ? accounts : [];
            var income = arr.filter(function (a) {
                return a.accountType === 'INCOME';
            });
            var expenses = arr.filter(function (a) {
                return a.accountType === 'EXPENSE';
            });

            var totalRevenue = income.reduce(function (s, a) {
                return s + (a.currentBalance || 0);
            }, 0);
            var totalExpenses = expenses.reduce(function (s, a) {
                return s + (a.currentBalance || 0);
            }, 0);

            window.incomeStmtState.lastResult = {
                income: income,
                expenses: expenses,
                totalRevenue: totalRevenue,
                totalExpenses: totalExpenses,
                netIncome: totalRevenue - totalExpenses,
                year: year,
                term: term,
            };
            hideSplash();
            return window.incomeStmtState.lastResult;
        } catch (e) {
            hideSplash();
            console.error('[_financeIncomeStatement] fetch error:', e);
            return null;
        }
    };

    window.incomeStmtTerms = TERMS;

    window.incomeStmtFmt = {
        money: function (v) {
            return 'GH₵ ' + (parseFloat(v) || 0).toLocaleString('en-GH', {minimumFractionDigits: 2});
        },
    };

    if (window.copyrights) window.copyrights();
    incomeStmtLoad();
})();