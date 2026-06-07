/**
 * _financeLedgers.js  —  Data & logic layer for Ledger Books (Chart of Accounts).
 *
 * Manages the institution's chart of accounts — the named ledger accounts
 * that all financial transactions post to.
 *
 * Exposes on window:
 *   ledgerState
 *   ledgerLoad()
 *   ledgerCreate(req)             → LedgerBooksResponse
 *   ledgerFetchAll()              → LedgerBooksResponse[]
 *   ledgerFetchByType(type)       → LedgerBooksResponse[] (ASSET|LIABILITY|INCOME|EXPENSE|EQUITY)
 *   ledgerFetchRecords(bookId, opts) → LedgerRecordsResponse[]
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.ledgerState = {
        institutionCode: _inst,
        allAccounts: [],
        filtered: [],
        academicYears: [],
        selectedYear: '',
        selectedTerm: 'all',
        _loaded: false,
    };

    var ACCOUNT_TYPES = ['ASSET', 'LIABILITY', 'INCOME', 'EXPENSE', 'EQUITY'];

    function buildYears() {
        var y = new Date().getFullYear(), out = [];
        for (var i = 3; i >= 0; i--) out.push(String(y - i));
        window.ledgerState.academicYears = out;
        window.ledgerState.selectedYear = out[out.length - 1];
    }

    function showSplash() {
        if (typeof $ !== 'undefined') $('.splash').css({display: 'block', background: '#ffffff3d'});
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    // ── LOAD ─────────────────────────────────────────────────────────────────
    window.ledgerLoad = async function () {
        buildYears();
        try {
            showSplash();
            var accounts = await fetchPost('ledger/get-by-institution', {
                institutionCode: _inst,
                accountType: null,
                academicYear: null,
                term: null,
            });
            window.ledgerState.allAccounts = Array.isArray(accounts) ? accounts : [];
            window.ledgerState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.ledgerState._loaded = true;
            console.error('[_financeLedgers] load error:', e);
        }
    };

    // ── FILTER ───────────────────────────────────────────────────────────────
    window.ledgerFilter = function (opts) {
        var all = window.ledgerState.allAccounts;
        var q = (opts.search || '').toLowerCase().trim();
        return all.filter(function (a) {
            if (opts.type && opts.type !== 'all' && a.accountType !== opts.type) return false;
            if (q) {
                var hay = [a.accountName, a.accountCode, a.description].join(' ').toLowerCase();
                if (!hay.includes(q)) return false;
            }
            return true;
        });
    };

    // ── CREATE / UPSERT ──────────────────────────────────────────────────────
    window.ledgerCreate = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('ledger/create', Object.assign({institutionCode: _inst}, req));
            // Upsert into local list
            var idx = window.ledgerState.allAccounts.findIndex(function (a) {
                return a.ledgerBookId === result.ledgerBookId;
            });
            if (idx >= 0) window.ledgerState.allAccounts[idx] = result;
            else window.ledgerState.allAccounts.push(result);
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── FETCH ALL ────────────────────────────────────────────────────────────
    window.ledgerFetchAll = async function () {
        showSplash();
        try {
            var result = await fetchPost('ledger/get-by-institution', {
                institutionCode: _inst, accountType: null, academicYear: null, term: null,
            });
            window.ledgerState.allAccounts = Array.isArray(result) ? result : [];
            hideSplash();
            return window.ledgerState.allAccounts;
        } catch (e) {
            hideSplash();
            return [];
        }
    };

    // ── FETCH BY TYPE ─────────────────────────────────────────────────────────
    window.ledgerFetchByType = async function (type) {
        try {
            var result = await fetchPost('ledger/get-by-institution', {
                institutionCode: _inst, accountType: type, academicYear: null, term: null,
            });
            return Array.isArray(result) ? result : [];
        } catch (e) {
            return [];
        }
    };

    // ── FETCH RECORDS for a specific account ────────────────────────────────
    window.ledgerFetchRecords = async function (bookId, year, term) {
        try {
            var result = await fetchPost('ledger/get-records', {
                institutionCode: _inst,
                ledgerBookId: bookId,
                academicYear: year || null,
                term: term || null,
            });
            return Array.isArray(result) ? result : [];
        } catch (e) {
            return [];
        }
    };

    // ── SUMMARY ──────────────────────────────────────────────────────────────
    window.ledgerSummary = function (accounts) {
        var out = {ASSET: 0, LIABILITY: 0, INCOME: 0, EXPENSE: 0, EQUITY: 0, count: accounts.length};
        accounts.forEach(function (a) {
            if (out[a.accountType] !== undefined) out[a.accountType] += (a.currentBalance || 0);
        });
        return out;
    };

    // ── HELPERS ──────────────────────────────────────────────────────────────
    window.ledgerAccountTypes = ACCOUNT_TYPES;

    window.ledgerFmt = {
        money: function (v) {
            return 'GH₵ ' + (parseFloat(v) || 0).toLocaleString('en-GH', {minimumFractionDigits: 2});
        },
        date: function (d) {
            return d ? new Date(d).toLocaleDateString('en-GB', {
                day: '2-digit', month: 'short', year: 'numeric',
            }) : '—';
        },
        dateTime: function (d) {
            return d ? new Date(d).toLocaleString('en-GB', {
                day: '2-digit', month: 'short', year: 'numeric',
                hour: '2-digit', minute: '2-digit',
            }) : '—';
        },
        balance: function (v, accountType) {
            // Negative balance on ASSET/EXPENSE is unusual — show in red
            var n = parseFloat(v) || 0;
            var isDebitNormal = accountType === 'ASSET' || accountType === 'EXPENSE';
            if (isDebitNormal && n < 0) return {
                text: 'GH₵ ' + Math.abs(n).toLocaleString('en-GH', {minimumFractionDigits: 2}) + ' Cr',
                warn: true
            };
            if (!isDebitNormal && n < 0) return {
                text: 'GH₵ ' + Math.abs(n).toLocaleString('en-GH', {minimumFractionDigits: 2}) + ' Dr',
                warn: true
            };
            return {text: 'GH₵ ' + Math.abs(n).toLocaleString('en-GH', {minimumFractionDigits: 2}), warn: false};
        },
    };

    if (window.copyrights) window.copyrights();
    ledgerLoad();
})();