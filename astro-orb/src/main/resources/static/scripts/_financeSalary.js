/**
 * _financeSalary.js  —  Data layer for Finance -> Salary Setup.
 *
 * Loaded at runtime by subscripts/financeSalary.js. All calls go through
 * fetchPost() to astro-orb's PayrollController, which adds the school, the
 * HR staff list and the signed-in user - nothing here sends an institution
 * code or a user name.
 *
 * Exposes on window:
 *   salaryState                     {staff, profiles, runs, settings, _loaded, loadError}
 *   salaryLoad()                    staff (HR) + profiles + runs + settings
 *   salaryFetchRuns(filters)        runs, filtered by academicYear / payPeriod / status
 *   salarySaveProfile(profile)
 *   salaryRunPayroll(academicYear, payPeriod)   → {updated: [...], skipped: [...]}
 *   salaryDeletePending(salaryIds)
 *   salaryGetSettings() / salarySaveSettings(settings)
 */
(function () {
    'use strict';

    window.salaryState = {
        staff: [],
        profiles: [],
        runs: [],
        settings: null,
        _loaded: false,
        loadError: null,
    };

    window.salaryLoad = function () {
        var s = window.salaryState;
        s._loaded = false;
        s.loadError = null;
        return Promise.all([
            fetchPost('payroll/staff', {}),
            fetchPost('payroll/profiles/get', {}),
            fetchPost('salary/get-by-institution', {}),
            fetchPost('salary-settings/get', {}),
        ]).then(function (results) {
            s.staff = Array.isArray(results[0]) ? results[0] : [];
            s.profiles = Array.isArray(results[1]) ? results[1] : [];
            s.runs = Array.isArray(results[2]) ? results[2] : [];
            s.settings = results[3] || null;
        }).catch(function (e) {
            console.error('[_financeSalary] load failed', e);
            s.loadError = window.payrollShared.errorMessage(e);
        }).then(function () {
            s._loaded = true;
        });
    };

    window.salaryFetchRuns = function (filters) {
        return fetchPost('salary/get-by-institution', filters || {}).then(function (runs) {
            window.salaryState.runs = Array.isArray(runs) ? runs : [];
            return window.salaryState.runs;
        });
    };

    window.salarySaveProfile = function (profile) {
        return fetchPost('payroll/profiles/save', profile).then(function (saved) {
            var list = window.salaryState.profiles;
            var i = list.findIndex(function (p) { return p.staffId === saved.staffId; });
            if (i >= 0) list[i] = saved; else list.push(saved);
            return saved;
        });
    };

    window.salaryRunPayroll = function (academicYear, payPeriod) {
        return fetchPost('payroll/run', {academicYear: academicYear, payPeriod: payPeriod});
    };

    window.salaryDeletePending = function (salaryIds) {
        return fetchPost('payroll/delete-pending', {salaryIds: salaryIds});
    };

    window.salaryGetSettings = function () {
        return fetchPost('salary-settings/get', {}).then(function (settings) {
            window.salaryState.settings = settings || null;
            return window.salaryState.settings;
        });
    };

    window.salarySaveSettings = function (settings) {
        return fetchPost('salary-settings/save', settings).then(function (saved) {
            window.salaryState.settings = saved;
            return saved;
        });
    };

    if (window.copyrights) window.copyrights();
})();
