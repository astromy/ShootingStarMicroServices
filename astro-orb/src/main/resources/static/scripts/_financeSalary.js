/**
 * _financeSalary.js  —  Data & logic layer for Salary / Payroll Management.
 *
 * Exposes on window:
 *   salaryState
 *   salaryData
 *   salaryLoad()
 *   salaryFetchRuns(filters) → SalaryResponse[]
 *   salaryCreate(request)    → SalaryResponse
 *   salaryBatch(requests)    → SalaryResponse[]
 *   salaryApprove(id, by)    → SalaryResponse
 *   salaryMarkPaid(id, by, ref) → SalaryResponse
 *   salaryGetSettings()      → SalarySettingsResponse
 *   salarySaveSettings(req)  → SalarySettingsResponse
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.salaryState = {
        institutionCode: _inst,
        allRuns: [],
        filtered: [],
        settings: null,
        staff: [],
        academicYears: [],
        payPeriods: [],
        selectedYear: '',
        selectedPeriod: '',
        _loaded: false,
    };

    window.salaryData = {};

    function buildYears() {
        var y = new Date().getFullYear(), out = [];
        for (var i = 3; i >= 0; i--) out.push((y - i) + '/' + (y - i + 1));
        window.salaryState.academicYears = out;
        window.salaryState.selectedYear = out[out.length - 1];
    }

    function buildPeriods() {
        // Month-based pay periods
        var months = ['January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'];
        window.salaryState.payPeriods = months.map(function (m) {
            return {value: m + ' ' + new Date().getFullYear(), label: m};
        });
    }

    function showSplash() {
        if ($) $('.splash').css({display: 'block', background: '#ffffff3d'});
    }

    function hideSplash() {
        if ($) $('.splash').css('display', 'none');
    }

    // ── LOAD ─────────────────────────────────────────────────────────────────
    window.salaryLoad = async function () {
        buildYears();
        buildPeriods();
        try {
            showSplash();
            var [settings, runs, staff] = await Promise.all([
                fetchPost('salary-settings/get', {institutionCode: _inst}),
                fetchPost('salary/get-by-institution', {institutionCode: _inst, academicYear: null, status: null}),
                fetchPost('getAllStaffByInstitution', {institutionCode: _inst}).catch(function () {
                    return [];
                }),
            ]);
            if (settings) window.salaryState.settings = settings;
            if (runs) window.salaryState.allRuns = Array.isArray(runs) ? runs : [];
            if (staff) window.salaryState.staff = Array.isArray(staff) ? staff : [];
            window.salaryState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.salaryState._loaded = true;
            console.error('[_financeSalary] load error:', e);
        }
    };

    // ── FILTER ───────────────────────────────────────────────────────────────
    window.salaryFilter = function (opts) {
        var all = window.salaryState.allRuns;
        var q = (opts.search || '').toLowerCase().trim();
        window.salaryState.filtered = all.filter(function (s) {
            if (opts.year && opts.year !== 'all' && s.academicYear !== opts.year) return false;
            if (opts.period && opts.period !== 'all' && s.payPeriod !== opts.period) return false;
            if (opts.status && opts.status !== 'all' && s.status !== opts.status) return false;
            if (q) {
                var hay = [s.staffId, s.staffName, s.designation].join(' ').toLowerCase();
                if (!hay.includes(q)) return false;
            }
            return true;
        });
        return window.salaryState.filtered;
    };

    // ── CREATE SINGLE RUN ────────────────────────────────────────────────────
    window.salaryCreate = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('salary/create', Object.assign({institutionCode: _inst}, req));
            window.salaryState.allRuns.push(result);
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── BATCH CREATE (run payroll for all staff) ──────────────────────────────
    window.salaryBatch = async function (requests) {
        showSplash();
        try {
            var result = await fetchPost('salary/create-batch', requests);
            if (Array.isArray(result)) window.salaryState.allRuns.push.apply(window.salaryState.allRuns, result);
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── APPROVE ──────────────────────────────────────────────────────────────
    window.salaryApprove = async function (salaryId, approvedBy) {
        showSplash();
        try {
            var result = await fetchPost('salary/approve/' + salaryId + '?approvedBy=' + encodeURIComponent(approvedBy), {});
            _syncRun(result);
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── MARK PAID ────────────────────────────────────────────────────────────
    window.salaryMarkPaid = async function (salaryId, processedBy, ref) {
        showSplash();
        try {
            var url = 'salary/mark-paid/' + salaryId +
                '?processedBy=' + encodeURIComponent(processedBy) +
                (ref ? '&externalReference=' + encodeURIComponent(ref) : '');
            var result = await fetchPost(url, {});
            _syncRun(result);
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── SETTINGS ─────────────────────────────────────────────────────────────
    window.salaryGetSettings = async function () {
        try {
            var result = await fetchPost('salary-settings/get', {institutionCode: _inst});
            window.salaryState.settings = result;
            return result;
        } catch (e) {
            return null;
        }
    };

    window.salarySaveSettings = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('salary-settings/save', Object.assign({institutionCode: _inst}, req));
            window.salaryState.settings = result;
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── SUMMARY STATS ────────────────────────────────────────────────────────
    window.salaryStats = function (runs) {
        return {
            count: runs.length,
            totalNet: runs.reduce(function (s, r) {
                return s + (r.netSalary || 0);
            }, 0),
            totalGross: runs.reduce(function (s, r) {
                return s + (r.basicSalary || 0) + (r.totalAllowances || 0);
            }, 0),
            totalDed: runs.reduce(function (s, r) {
                return s + (r.totalDeductions || 0);
            }, 0),
            pending: runs.filter(function (r) {
                return r.status === 'PENDING';
            }).length,
            approved: runs.filter(function (r) {
                return r.status === 'APPROVED';
            }).length,
            paid: runs.filter(function (r) {
                return r.status === 'PAID';
            }).length,
        };
    };

    // ── HELPERS ──────────────────────────────────────────────────────────────
    function _syncRun(updated) {
        var idx = window.salaryState.allRuns.findIndex(function (r) {
            return r.salaryId === updated.salaryId;
        });
        if (idx >= 0) window.salaryState.allRuns[idx] = updated;
    }

    window.salaryFmt = {
        money: function (v) {
            return 'GH₵ ' + (parseFloat(v) || 0).toLocaleString('en-GH', {minimumFractionDigits: 2});
        },
        date: function (d) {
            return d ? new Date(d).toLocaleDateString('en-GB', {day: '2-digit', month: 'short', year: 'numeric'}) : '—';
        },
    };

    if (window.copyrights) window.copyrights();
    salaryLoad();
})();