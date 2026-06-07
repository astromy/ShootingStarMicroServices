/**
 * _financePaymentChecker.js  —  Data & logic layer for Payment Checker.
 *
 * Provides quick lookup of any student's current bill status —
 * useful at the gate, examination hall, or any checkpoint.
 *
 * Exposes on window:
 *   pyCheckState
 *   pyCheckLoad()
 *   pyCheckLookup(studentId) → { bill, payments, status }
 *   pyCheckClassLookup(className) → Student_BillResponse[]
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    window.pyCheckState = {
        institutionCode: _inst,
        classGroups: [],
        classesRaw: [],
        lastResult: null,
        classResults: [],
        _loaded: false,
    };

    function showSplash() {
        if ($) $('.splash').css({display: 'block', background: '#ffffff3d'});
    }

    function hideSplash() {
        if ($) $('.splash').css('display', 'none');
    }

    // ── LOAD ─────────────────────────────────────────────────────────────────
    window.pyCheckLoad = async function () {
        try {
            showSplash();
            var [groups, inst] = await Promise.all([
                fetchPost('getLookUpByType', {val: 'ClassGroup'}),
                fetchPost('getInstitutionByCode', {val: _inst}),
            ]);
            if (groups) window.pyCheckState.classGroups = groups;
            if (inst && inst.classList)
                window.pyCheckState.classesRaw = inst.classList.map(function (c) {
                    return {name: c.name, classGroup: c.classGroup || ''};
                });
            window.pyCheckState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.pyCheckState._loaded = true;
        }
    };

    // ── SINGLE STUDENT LOOKUP ────────────────────────────────────────────────
    window.pyCheckLookup = async function (studentId) {
        if (!studentId) return null;
        showSplash();
        try {
            var [bill, payments] = await Promise.all([
                fetchPost('getStudentBillByIdAndInstitution', {
                    institutionCode: _inst,
                    studentId: studentId,
                    studentClass: ''
                }),
                fetchPost('get-billPayments-by-student', {institutionCode: _inst, name: studentId}),
            ]);

            var result = {
                studentId: studentId,
                bill: bill || null,
                payments: payments || [],
                status: 'NO_BILL',
            };

            if (bill) {
                var bal = bill.amountBalance || 0;
                result.status = bal <= 0 ? 'CLEARED' : bal < (bill.amountDue * 0.5) ? 'PARTIAL' : 'OWING';
            }

            window.pyCheckState.lastResult = result;
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            return null;
        }
    };

    // ── CLASS BULK LOOKUP ────────────────────────────────────────────────────
    window.pyCheckClassLookup = async function (className) {
        showSplash();
        try {
            var result = await fetchPost('getStudentBillsByInstitutionClass', {
                institutionCode: _inst, studentClass: className,
            });
            window.pyCheckState.classResults = result || [];
            hideSplash();
            return result || [];
        } catch (e) {
            hideSplash();
            return [];
        }
    };

    window.pyCheckClassesByGroup = function (groupName) {
        return (window.pyCheckState.classesRaw || []).filter(function (c) {
            return !groupName || (c.classGroup && c.classGroup.toLowerCase() === groupName.toLowerCase());
        });
    };

    window.pyCheckFmt = {
        money: function (v) {
            return 'GH₵ ' + (parseFloat(v) || 0).toLocaleString('en-GH', {minimumFractionDigits: 2});
        },
        date: function (d) {
            return d ? new Date(d).toLocaleDateString('en-GB', {day: '2-digit', month: 'short', year: 'numeric'}) : '—';
        },
    };

    if (window.copyrights) window.copyrights();
    pyCheckLoad();
})();