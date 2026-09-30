/**
 * _financeSalaryApprovals.js  —  Data layer for Finance -> Salary Approvals.
 *
 * Loaded at runtime by subscripts/financeSalaryApprovals.js. astro-orb's
 * PayrollController adds the school and records the signed-in user as the
 * approver / payer - nothing here sends either.
 *
 * Exposes on window:
 *   approvalState                  {runs, _loaded, loadError}
 *   approvalLoad()
 *   approvalApprove(salaryIds)     → {updated, skipped}
 *   approvalMarkPaid(salaryIds, reference)
 */
(function () {
    'use strict';

    window.approvalState = {runs: [], _loaded: false, loadError: null};

    window.approvalLoad = function () {
        var s = window.approvalState;
        s._loaded = false;
        s.loadError = null;
        return fetchPost('salary/get-by-institution', {}).then(function (runs) {
            s.runs = Array.isArray(runs) ? runs : [];
        }).catch(function (e) {
            console.error('[_financeSalaryApprovals] load failed', e);
            s.loadError = window.payrollShared.errorMessage(e);
        }).then(function () {
            s._loaded = true;
        });
    };

    window.approvalApprove = function (salaryIds) {
        return fetchPost('payroll/approve', {salaryIds: salaryIds});
    };

    window.approvalMarkPaid = function (salaryIds, reference) {
        return fetchPost('payroll/mark-paid', {salaryIds: salaryIds, externalReference: reference || null});
    };

    if (window.copyrights) window.copyrights();
})();
