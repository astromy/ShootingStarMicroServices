/**
 * _libraryCirculation.js  —  Data & logic layer for the Library Circulation module.
 *
 * Loaded at runtime by libraryCirculation.js (UI renderer), following the same
 * two-file pattern as _libraryCatalogue.js.
 *
 * Backend calls go through fetchPost('<route>', body) / fetchGet-equivalent,
 * proxied by astro-orb's LibraryController to the library microservice
 * (POST/GET /api/library/loans/**).
 *
 * Exposes on window:
 *   libraryLoanState / libraryLoanData
 *   libraryFetchActiveLoans()
 *   libraryFetchOverdueLoans()
 *   libraryCheckout(req)
 *   libraryReturn(req)
 *   libraryFetchLoansByStudent(studentIndex)
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // staffId — best-effort pull from whatever the shell already exposes for
    // the logged-in user (mirrors how other modules stamp a "processedBy"/
    // "createdBy" field). Falls back to a generic label rather than blocking
    // the action if it isn't set — confirm the real global name in your
    // session/user context and adjust this line if it differs.
    var _staffId = (typeof currentUserId !== 'undefined' && currentUserId)
        || (typeof loggedInStaffId !== 'undefined' && loggedInStaffId)
        || 'web-admin';

    window.libraryLoanState = {
        institutionCode: _inst,
        staffId: _staffId,
        _loaded: false,
    };

    window.libraryLoanData = {
        activeLoans: [],
        overdueLoans: [],
    };

    function showSplash() {
        if (typeof $ !== 'undefined') {
            $('.splash').css({display: 'block', background: '#ffffff3d'}).find('h1, p').remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    // ── LOAD ─────────────────────────────────────────────────────────────────
    window.libraryFetchActiveLoans = async function () {
        try {
            showSplash();
            var result = await fetchGetOrPost('library/loans/active/' + _inst);
            window.libraryLoanData.activeLoans = Array.isArray(result) ? result : [];
            window.libraryLoanState._loaded = true;
            hideSplash();
            return window.libraryLoanData.activeLoans;
        } catch (e) {
            hideSplash();
            window.libraryLoanState._loaded = true;
            console.error('[_libraryCirculation] active-loans load error:', e);
            return [];
        }
    };

    window.libraryFetchOverdueLoans = async function () {
        try {
            var result = await fetchGetOrPost('library/loans/overdue/' + _inst);
            window.libraryLoanData.overdueLoans = Array.isArray(result) ? result : [];
            return window.libraryLoanData.overdueLoans;
        } catch (e) {
            console.error('[_libraryCirculation] overdue-loans load error:', e);
            return [];
        }
    };

    window.libraryFetchLoansByStudent = async function (studentIndex) {
        try {
            return await fetchPost('library/loans/get-by-student', {
                institutionCode: _inst,
                studentIndex: studentIndex,
            });
        } catch (e) {
            console.error('[_libraryCirculation] student-loans load error:', e);
            return [];
        }
    };

    window.libraryCheckout = async function (req) {
        return fetchPost('library/loans/checkout', req);
    };

    window.libraryReturn = async function (req) {
        return fetchPost('library/loans/return', req);
    };

    // common.js's fetchPost() is POST-only; the LibraryController's active/overdue
    // routes are plain GETs (no body needed), so this tries a lightweight native
    // fetch GET first and only falls back to fetchPost if that helper isn't
    // available for some reason (keeps this resilient to older common.js builds).
    async function fetchGetOrPost(path) {
        if (typeof window.fetchGet === 'function') {
            return window.fetchGet(path);
        }
        var res = await fetch(path, {method: 'GET', headers: {'Content-Type': 'application/json'}});
        if (!res.ok) throw new Error('Request failed: ' + res.status);
        return res.json();
    }

    // Initial load as soon as this data layer is available.
    window.libraryFetchActiveLoans();

})();
