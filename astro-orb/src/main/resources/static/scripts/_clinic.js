/**
 * _clinic.js  —  Data & logic layer for the Clinic module.
 *
 * Loaded at runtime by scripts/subscripts/clinic.js (UI renderer), following
 * the same two-file pattern as _storesInventory.js / _financeBilling.js /
 * _financeFeeCollection.js / _financeLedgers.js.
 *
 * Backend calls all go through the existing fetchPost('<route>', body)
 * helper, which is proxied by astro-orb's ClinicController to the clinic
 * microservice (POST /api/clinic/**), and to administration-pta for the
 * student-ID lookup (POST /api/administration-pta/checkStudentByID).
 *
 * Exposes on window:
 *   clinicState / clinicData
 *   clinicLoad()                            — initial "currently at the clinic" load
 *   clinicCheckStudentByID(studentId)       — resolve a scanned/typed ID to a student
 *   clinicRecordVisit(payload)              — check a patient in (+ notifies parent server-side)
 *   clinicUpdateVisitClinicalNotes(id, req) — add diagnosis/prescription/vitals to an in-progress visit
 *   clinicDischargeVisit(visitId, req)      — discharge
 *   clinicFetchInProgressVisits()           — refresh the in-progress list (Check-In / Diagnosis tabs)
 *   clinicFetchPatientHistory(patientId)    — full history for the Medical History tab
 *   clinicFmt                               — shared formatters
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    var _recordedBy = (typeof staffId !== 'undefined' && staffId)
        ? staffId
        : ((typeof userName !== 'undefined' && userName) ? userName : '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.clinicState = {
        institutionCode: _inst,
        recordedBy: _recordedBy,
        lookedUpStudent: null,   // set after a successful ID lookup (Check-In tab)
        _loaded: false,
    };

    window.clinicData = {
        inProgressVisits: [],
    };

    // ── HELPERS ──────────────────────────────────────────────────────────────
    function showSplash() {
        if (typeof $ !== 'undefined') {
            $('.splash').css({display: 'block', background: '#ffffff3d'}).find('h1, p').remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    // ── 1. INITIAL LOAD ──────────────────────────────────────────────────────
    window.clinicLoad = async function () {
        try {
            showSplash();
            await window.clinicFetchInProgressVisits();
            window.clinicState._loaded = true;
        } catch (e) {
            console.error('[_clinic] Initial load failed:', e);
            window.clinicState._loaded = true; // don't leave the UI waiting forever
        } finally {
            hideSplash();
        }
    };

    // ── 2. STUDENT LOOKUP (ID scan / type) ────────────────────────────────────
    window.clinicCheckStudentByID = async function (studentId) {
        var result = await fetchPost('clinic/checkStudentByID', {val: studentId});
        window.clinicState.lookedUpStudent = result || null;
        return result;
    };

    // ── 3. VISITS ────────────────────────────────────────────────────────────
    window.clinicRecordVisit = async function (payload) {
        var visit = await fetchPost('clinic/recordVisit', payload);
        window.clinicState.lookedUpStudent = null;
        await window.clinicFetchInProgressVisits();
        return visit;
    };

    window.clinicUpdateVisitClinicalNotes = async function (visitId, payload) {
        var visit = await fetchPost('clinic/updateVisitClinicalNotes/' + visitId, payload);
        await window.clinicFetchInProgressVisits();
        return visit;
    };

    window.clinicDischargeVisit = async function (visitId, dischargeReq) {
        var visit = await fetchPost('clinic/dischargeVisit/' + visitId, dischargeReq);
        await window.clinicFetchInProgressVisits();
        return visit;
    };

    window.clinicFetchInProgressVisits = async function () {
        var visits = await fetchPost('clinic/getVisitsByInstitution', {
            institutionCode: _inst,
            status: 'IN_PROGRESS',
        });
        window.clinicData.inProgressVisits = Array.isArray(visits) ? visits : [];
        return window.clinicData.inProgressVisits;
    };

    // ── 4. MEDICAL HISTORY (patient timeline) ──────────────────────────────────
    // Pulls all four record types for one patient in parallel — backs the
    // Medical History tab, and is the first real UI on top of the
    // getVisitsByPatient / getDiagnosisByPatient / getPrescriptionByPatient /
    // getAllVitalRecordsByPatient endpoints, which existed server-side but
    // had nothing calling them until now.
    window.clinicFetchPatientHistory = async function (patientId) {
        var results = await Promise.all([
            fetchPost('clinic/getVisitsByPatient', {institutionCode: _inst, patientId: patientId}),
            fetchPost('clinic/getDiagnosisByPatient', {institutionCode: _inst, patientId: patientId}),
            fetchPost('clinic/getPrescriptionByPatient', {institutionCode: _inst, patientId: patientId}),
            fetchPost('clinic/getAllVitalRecordsByPatient', {institutionCode: _inst, patientId: patientId}),
        ]);

        return {
            visits: Array.isArray(results[0]) ? results[0] : [],
            diagnoses: Array.isArray(results[1]) ? results[1] : [],
            prescriptions: Array.isArray(results[2]) ? results[2] : [],
            vitals: Array.isArray(results[3]) ? results[3] : [],
        };
    };

    // ── FORMATTERS ───────────────────────────────────────────────────────────
    window.clinicFmt = {
        time: function (dateStr) {
            if (!dateStr) return '';
            var d = new Date(dateStr);
            if (isNaN(d.getTime())) return dateStr;
            return d.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
        },
        dateTime: function (dateStr) {
            if (!dateStr) return '';
            var d = new Date(dateStr);
            if (isNaN(d.getTime())) return dateStr;
            return d.toLocaleDateString([], {month: 'short', day: 'numeric', year: 'numeric'}) +
                ' ' + d.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
        },
    };
})();
