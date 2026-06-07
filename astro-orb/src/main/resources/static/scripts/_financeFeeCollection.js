/**
 * _financeFeeCollection.js  —  Data & logic layer for Fee Collection.
 *
 * Exposes on window:
 *   feeCollState   — mutable UI state
 *   feeCollData    — loaded data cache
 *   feeCollLoad()  — initial load
 *   feeCollSubmit(request) → Bill_PaymentResponse
 *   feeCollFetchStudentBill(studentId) → Student_BillResponse
 *   feeCollFetchHistory(studentId) → Bill_PaymentResponse[]
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.feeCollState = {
        institutionCode: _inst,
        selectedStudent: null,   // { id, name, class }
        studentBill: null,   // Student_BillResponse
        paymentHistory: [],
        classes: [],
        academicYears: [],
        selectedYear: '',
        selectedTerm: '1st Term',
        paymentMethod: 'CASH',
        _loaded: false,
    };

    window.feeCollData = {
        institution: null,
        classGroups: [],
        students: {},          // className → student[]
        terms: [
            {value: '1st Term', label: '1st Term (Sep – Dec)'},
            {value: '2nd Term', label: '2nd Term (Jan – Apr)'},
            {value: '3rd Term', label: '3rd Term (May – Aug)'},
        ],
    };

    // ── HELPERS ──────────────────────────────────────────────────────────────
    function buildYears() {
        var y = new Date().getFullYear(), out = [];
        for (var i = 4; i >= 0; i--) out.push((y - i) + '/' + (y - i + 1));
        window.feeCollState.academicYears = out;
        window.feeCollState.selectedYear = out[out.length - 1];
    }

    function showSplash() {
        if ($) $('.splash').css({display: 'block', background: '#ffffff3d'}).find('h1,p').remove();
    }

    function hideSplash() {
        if ($) $('.splash').css('display', 'none');
    }

    // ── INITIAL LOAD ─────────────────────────────────────────────────────────
    window.feeCollLoad = async function () {
        try {
            showSplash();
            buildYears();
            var [inst, groups] = await Promise.all([
                fetchPost('getInstitutionByCode', {val: _inst}),
                fetchPost('getLookUpByType', {val: 'ClassGroup'}),
            ]);
            if (groups) window.feeCollData.classGroups = groups.map(function (g) {
                return {id: g.id, name: g.name};
            });
            if (inst && inst.classList) {
                window.feeCollData.classesRaw = inst.classList.map(function (c) {
                    return {name: c.name, classGroup: c.classGroup || ''};
                });
            }
            if (inst && inst.terms) window.feeCollData.terms = inst.terms.map(function (t) {
                return {value: t, label: t};
            });
            window.feeCollState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.feeCollState._loaded = true;
            console.error('[_financeFeeCollection] load error:', e);
        }
    };

    // ── CLASS FILTERING ──────────────────────────────────────────────────────
    window.feeCollClassesByGroup = function (groupName) {
        if (!window.feeCollData.classesRaw) return [];
        return window.feeCollData.classesRaw.filter(function (c) {
            return !groupName || (c.classGroup && c.classGroup.toLowerCase() === groupName.toLowerCase());
        });
    };

    // ── STUDENT FETCH ────────────────────────────────────────────────────────
    window.feeCollFetchStudents = async function (className) {
        if (window.feeCollData.students[className]) return window.feeCollData.students[className];
        showSplash();
        try {
            var result = await fetchPost('getSkimpStudentsByClass', {
                institutionCode: _inst,
                studentClass: className,
                studentId: '',
                gender: '',
                status: '',
                dateOfBirth: '',
                dateOfAdmission: '',
                denomination: '',
                nationality: '',
            });
            var mapped = (result || []).map(function (s) {
                return {
                    id: s.studentId || s.id,
                    name: [s.lastName, s.firstName, s.otherName].filter(Boolean).join(' ').trim(),
                    cls: className,
                };
            });
            window.feeCollData.students[className] = mapped;
            hideSplash();
            return mapped;
        } catch (e) {
            hideSplash();
            return [];
        }
    };

    // ── STUDENT BILL FETCH ───────────────────────────────────────────────────
    window.feeCollFetchStudentBill = async function (studentId) {
        try {
            var result = await fetchPost('getStudentBillByIdAndInstitution', {
                institutionCode: _inst, studentId: studentId, studentClass: '',
            });
            window.feeCollState.studentBill = result || null;
            return result;
        } catch (e) {
            return null;
        }
    };

    // ── PAYMENT HISTORY FETCH ────────────────────────────────────────────────
    window.feeCollFetchHistory = async function (studentId) {
        try {
            var result = await fetchPost('get-billPayments-by-student', {
                institutionCode: _inst, name: studentId,
            });
            window.feeCollState.paymentHistory = result || [];
            return result || [];
        } catch (e) {
            return [];
        }
    };

    // ── SUBMIT PAYMENT ───────────────────────────────────────────────────────
    window.feeCollSubmit = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('create-billPayment', {
                studentId: req.studentId,
                paymentAmount: req.amount,
                paidBy: req.paidBy,
                recieptNum: req.receiptNum,
                term: req.term,
                academicYear: req.academicYear,
                institutionCode: _inst,
                paymentMethod: req.paymentMethod || 'CASH',
                externalReference: req.externalReference || '',
            });
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── PAYSTACK ONLINE PAYMENT ──────────────────────────────────────────────
    window.feeCollPaystack = function (opts) {
        /* opts: { email, amountGhs, studentId, studentName, onSuccess, onClose } */
        if (typeof PaystackPop === 'undefined') {
            alert('Paystack SDK not loaded. Check internet connection.');
            return;
        }
        var handler = PaystackPop.setup({
            key: window.PAYSTACK_PUBLIC_KEY || 'pk_test_957a501ee4935ea125978771108f5aba4ad53acf',
            email: opts.email || 'admin@astromyllc.com',
            amount: Math.round(opts.amountGhs * 100),
            currency: 'GHS',
            ref: 'PAY_' + opts.studentId + '_' + Date.now(),
            metadata: {custom_fields: [{display_name: opts.studentName, variable_name: 'student', value: _inst}]},
            callback: function (res) {
                if (opts.onSuccess) opts.onSuccess(res.reference);
            },
            onClose: function () {
                if (opts.onClose) opts.onClose();
            },
        });
        handler.openIframe();
    };

    // ── FORMAT HELPERS (shared across UI) ───────────────────────────────────
    window.feeCollFmt = {
        money: function (v) {
            return 'GH₵ ' + (parseFloat(v) || 0).toLocaleString('en-GH', {minimumFractionDigits: 2});
        },
        date: function (d) {
            return d ? new Date(d).toLocaleDateString('en-GB', {day: '2-digit', month: 'short', year: 'numeric'}) : '—';
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
    feeCollLoad();

})();