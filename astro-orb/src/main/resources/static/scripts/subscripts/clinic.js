/**
 * clinic.js  —  UI renderer for the Clinic module.
 *
 * This file loads first (registered by the host page). It dynamically loads
 * _clinic.js at runtime — same two-file pattern as storesInventory.js /
 * financeBilling.js / financeFeeCollection.js / financeLedger.js.
 *
 * Three real, distinct tabs — NOT the same screen shown three times.
 * Which one opens by default is set by common.js's clinicBuild(event)
 * via window.clinicStartTab, based on which "Infairmary" sidebar item
 * (vitals_recording / diagnosis_recording / medical_history) was clicked.
 * All three tabs stay reachable from within the page too.
 *
 *   ── CHECK-IN ──────────────────────────────────────────────────────────
 *   Scan/type a student ID → look up name → record a new visit (reason +
 *   optional vitals/diagnosis/prescription/notes captured at check-in) →
 *   server-side notifies the parent. This is the front-desk / "a kid just
 *   walked in" screen.
 *
 *   ── DIAGNOSIS & PRESCRIPTIONS ─────────────────────────────────────────
 *   The list of everyone currently IN_PROGRESS. Click a row to attach a
 *   diagnosis/prescription/vitals to that visit (for clinical notes
 *   captured separately from check-in — a nurse assesses first, a
 *   different staff member sees them later), or discharge them.
 *
 *   ── MEDICAL HISTORY ───────────────────────────────────────────────────
 *   Look up a student and see their full clinic timeline — past visits,
 *   diagnoses, prescriptions, vitals — read-only. For "has this kid been
 *   in here a lot lately" questions, not for recording anything new.
 */
(function () {
    'use strict';

    // ─── RESOLVE BASE PATH ──────────────────────────────────────────────────
    var _scriptBase = (function () {
        var el = document.currentScript ||
            (function () {
                var tags = document.getElementsByTagName('script');
                for (var i = tags.length - 1; i >= 0; i--) {
                    if (tags[i].src && tags[i].src.indexOf('clinic') !== -1) {
                        return tags[i];
                    }
                }
                return null;
            })();
        if (!el || !el.src) return '';
        return el.src.substring(0, el.src.lastIndexOf('/') + 1);
    })();

    // ─── CSS ─────────────────────────────────────────────────────────────────
    // style.css is already loaded globally by the base template.

    if (!document.querySelector('link[href*="font-awesome"], link[href*="fontawesome"]')) {
        var fa = document.createElement('link');
        fa.rel = 'stylesheet';
        fa.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
        document.head.appendChild(fa);
    }

    (function injectScopedStyle() {
        if (document.getElementById('clinicScopedCSS')) return;
        var style = document.createElement('style');
        style.id = 'clinicScopedCSS';
        style.textContent = [
            '.cl-tabs{display:flex;gap:4px;margin-bottom:18px;border-bottom:2px solid #eee;}',
            '.cl-tab{padding:10px 16px;cursor:pointer;font-size:14px;font-weight:600;color:#888;border-bottom:2px solid transparent;margin-bottom:-2px;}',
            '.cl-tab.active{color:#2f54d4;border-bottom-color:#2f54d4;}',
            '.cl-scan{display:flex;gap:10px;margin-bottom:18px;}',
            '.cl-scan input{flex:1;font-size:16px;padding:10px 12px;border:1px solid #dcdcdc;border-radius:6px;}',
            '.cl-banner{padding:12px 16px;border-radius:6px;margin-bottom:18px;font-size:14px;}',
            '.cl-banner.ok{background:#eaf7ea;color:#256029;border:1px solid #bfe3bf;}',
            '.cl-banner.warn{background:#fff8e6;color:#8a6116;border:1px solid #f0dca0;}',
            '.cl-patient{display:flex;justify-content:space-between;align-items:center;margin-bottom:14px;}',
            '.cl-badge{padding:2px 8px;border-radius:10px;font-size:11px;font-weight:600;}',
            '.cl-badge.notified{background:#eaf7ea;color:#256029;}',
            '.cl-badge.pending{background:#fdeaea;color:#8a2020;}',
            '.cl-badge.done{background:#eef1fb;color:#2f54d4;}',
            '.cl-visit-row{border:1px solid #eee;border-radius:6px;padding:12px 14px;margin-bottom:10px;}',
            '.cl-visit-row-head{display:flex;justify-content:space-between;align-items:center;cursor:pointer;}',
            '.cl-visit-row-detail{margin-top:12px;padding-top:12px;border-top:1px solid #eee;display:none;}',
            '.cl-visit-row-detail.open{display:block;}',
            '.cl-history-section{margin-bottom:18px;}',
            '.cl-history-section h4{font-size:13px;text-transform:uppercase;letter-spacing:.04em;color:#888;margin-bottom:8px;}',
            '.cl-history-entry{padding:8px 0;border-bottom:1px solid #f0f0f0;font-size:13px;}',
            '.cl-history-entry:last-child{border-bottom:none;}',
            '.cl-history-entry .when{color:#999;font-size:11px;}',
        ].join('\n');
        document.head.appendChild(style);
    })();

    var TABS = [
        {id: 'checkin', label: 'Check-In', icon: 'fa-id-card'},
        {id: 'diagnosis', label: 'Diagnosis & Prescriptions', icon: 'fa-user-md'},
        {id: 'history', label: 'Medical History', icon: 'fa-history'},
    ];

    var state = {
        activeTab: 'checkin',
        historyPatientId: '',
        historyResult: null,
        openVisitId: null, // which in-progress row is expanded, in the Diagnosis tab
    };

    // ─── SHELL (built once; tab body re-rendered on switch) ──────────────────
    function buildShell() {
        var tabsHtml = TABS.map(function (t) {
            return '<div class="cl-tab' + (t.id === state.activeTab ? ' active' : '') + '" data-tab="' + t.id + '">' +
                '<i class="fas ' + t.icon + '"></i> ' + t.label + '</div>';
        }).join('');

        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-briefcase-medical"></i> Clinic</h1>',
            '<p>Check patients in, record clinical notes, and review history</p></div>',
            '</header>',

            '<div class="cl-tabs" id="clTabs">', tabsHtml, '</div>',
            '<div id="clTabBody"></div>',

            '<div class="fc-toast" id="clToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="clYear"></span> | Clinic</footer>',
        ].join('') + '</div>';

        document.getElementById('clYear').textContent = new Date().getFullYear();

        document.querySelectorAll('.cl-tab').forEach(function (tab) {
            tab.addEventListener('click', function () {
                switchTab(tab.dataset.tab);
            });
        });
    }

    function switchTab(tabId) {
        state.activeTab = tabId;
        document.querySelectorAll('.cl-tab').forEach(function (tab) {
            tab.classList.toggle('active', tab.dataset.tab === tabId);
        });
        renderTabBody();
    }

    function renderTabBody() {
        if (state.activeTab === 'checkin') renderCheckinTab();
        else if (state.activeTab === 'diagnosis') renderDiagnosisTab();
        else renderHistoryTab();
    }

    // ─────────────────────────────────────────────────────────────────────
    // TAB 1: CHECK-IN
    // ─────────────────────────────────────────────────────────────────────
    function renderCheckinTab() {
        var s = window.clinicState.lookedUpStudent;
        var patientHtml = '';

        if (s) {
            var fullName = [s.firstName, s.otherName, s.lastName].filter(Boolean).join(' ');
            patientHtml = [
                '<div class="panel">',
                '<div class="panel-head"><span><i class="fas fa-user"></i> ' + fullName + '</span>',
                '<span style="color:#888;font-size:13px;">' + s.studentId + (s.studentClass ? ' · ' + s.studentClass : '') + '</span></div>',
                '<div class="panel-body">',

                '<div class="fc-fields-row">',
                '<div class="fc-field"><label>Reason for visit <span class="req">*</span></label>',
                '<input type="text" id="clReason" placeholder="e.g. Headache, fever, injury"></div>',
                '<div class="fc-field"><label>Recorded by</label>',
                '<input type="text" id="clRecordedBy" value="' + (window.clinicState.recordedBy || '') + '"></div>',
                '</div>',

                '<div class="fc-fields-row">',
                '<div class="fc-field"><label>Vitals type (optional)</label>',
                '<input type="text" id="clVitalType" placeholder="e.g. Temperature"></div>',
                '<div class="fc-field"><label>Vitals value (optional)</label>',
                '<input type="text" id="clVitalValue" placeholder="e.g. 38.2\u00B0C"></div>',
                '</div>',

                '<div class="fc-field"><label>Diagnosis (optional)</label><textarea id="clDiagnosis" rows="2"></textarea></div>',
                '<div class="fc-field"><label>Prescription (optional)</label><textarea id="clPrescription" rows="2"></textarea></div>',
                '<div class="fc-field"><label>Notes (optional)</label><textarea id="clNotes" rows="2"></textarea></div>',

                '<button class="process-btn" id="clSubmitBtn">',
                '<i class="fas fa-notes-medical"></i> <span id="clSubmitLabel">Record Visit &amp; Notify Parent</span>',
                '</button>',
                '</div></div>',
            ].join('');
        }

        document.getElementById('clTabBody').innerHTML = [
            '<div id="clBanner"></div>',
            '<div class="cl-scan">',
            '<input id="clIdInput" placeholder="Scan ID card or type student ID, then press Enter" autofocus>',
            '<button class="fc-btn fc-btn-primary" id="clLookupBtn">Look Up</button>',
            '</div>',
            patientHtml,
        ].join('');

        var idInput = document.getElementById('clIdInput');
        idInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                handleLookup();
            }
        });
        document.getElementById('clLookupBtn').addEventListener('click', handleLookup);
        idInput.focus();

        var submitBtn = document.getElementById('clSubmitBtn');
        if (submitBtn) submitBtn.addEventListener('click', handleRecordVisit);
    }

    async function handleLookup() {
        var input = document.getElementById('clIdInput');
        var val = input.value.trim();
        if (!val) return;

        var btn = document.getElementById('clLookupBtn');
        btn.disabled = true;
        document.getElementById('clBanner').innerHTML = '';

        try {
            var result = await window.clinicCheckStudentByID(val);
            if (!result) {
                toast('No student found for that ID.', 'error');
            } else {
                renderCheckinTab();
            }
        } catch (e) {
            console.error('[clinic] Student lookup failed:', e);
            toast('Could not look up that student ID.', 'error');
        } finally {
            btn.disabled = false;
        }
    }

    async function handleRecordVisit() {
        var s = window.clinicState.lookedUpStudent;
        if (!s) return;

        var reason = fieldVal('clReason');
        if (!reason) {
            toast('Reason for visit is required.', 'error');
            return;
        }

        var payload = {
            institutionCode: window.clinicState.institutionCode,
            patientId: s.studentId,
            patientType: 'STUDENT',
            patientName: [s.firstName, s.otherName, s.lastName].filter(Boolean).join(' '),
            reason: reason,
            notes: fieldVal('clNotes'),
            recordedBy: fieldVal('clRecordedBy'),
            vitalsRecordType: fieldVal('clVitalType'),
            vitalsValue: fieldVal('clVitalValue'),
            diagnosisText: fieldVal('clDiagnosis'),
            prescriptionText: fieldVal('clPrescription'),
        };

        var submitBtn = document.getElementById('clSubmitBtn');
        var submitLabel = document.getElementById('clSubmitLabel');
        submitBtn.disabled = true;
        submitLabel.textContent = 'Recording…';

        try {
            var visit = await window.clinicRecordVisit(payload);
            document.getElementById('clIdInput') && (document.getElementById('clIdInput').value = '');
            renderCheckinTab();
            showBanner(!!visit.parentNotified);
            toast('Visit recorded.', 'success');
        } catch (e) {
            console.error('[clinic] Failed to record visit:', e);
            toast('Could not record this visit. Please try again.', 'error');
            submitBtn.disabled = false;
            submitLabel.textContent = 'Record Visit & Notify Parent';
        }
    }

    function showBanner(notified) {
        var el = document.getElementById('clBanner');
        if (!el) return;
        el.innerHTML = '<div class="cl-banner ' + (notified ? 'ok' : 'warn') + '">' +
            (notified
                ? '<i class="fas fa-check-circle"></i> Visit recorded — the parent has been notified.'
                : '<i class="fas fa-exclamation-triangle"></i> Visit recorded, but the parent notification could not be sent yet. It will be retried automatically.') +
            '</div>';
    }

    // ─────────────────────────────────────────────────────────────────────
    // TAB 2: DIAGNOSIS & PRESCRIPTIONS
    // ─────────────────────────────────────────────────────────────────────
    function renderDiagnosisTab() {
        var visits = window.clinicData.inProgressVisits;
        var fmt = window.clinicFmt;

        var body;
        if (!visits.length) {
            body = '<div class="empty-state"><i class="fas fa-check-circle"></i> No students currently checked in.</div>';
        } else {
            body = visits.map(function (v) {
                var open = state.openVisitId === v.id;
                var hasDiagnosis = !!v.diagnosisId;
                var hasPrescription = !!v.prescriptionId;

                return [
                    '<div class="cl-visit-row">',
                    '<div class="cl-visit-row-head" data-visit-id="' + v.id + '">',
                    '<div><strong>' + (v.patientName || v.patientId) + '</strong>',
                    '<div style="color:#888;font-size:12px;">' + (v.reason || '') + ' · checked in ' + fmt.time(v.checkInTime) + '</div></div>',
                    '<div>',
                    '<span class="cl-badge ' + (hasDiagnosis ? 'done' : 'pending') + '">' + (hasDiagnosis ? 'Diagnosed' : 'No diagnosis yet') + '</span> ',
                    '<span class="cl-badge ' + (hasPrescription ? 'done' : 'pending') + '">' + (hasPrescription ? 'Prescribed' : 'No prescription yet') + '</span> ',
                    '<i class="fas fa-chevron-' + (open ? 'up' : 'down') + '"></i>',
                    '</div>',
                    '</div>',

                    '<div class="cl-visit-row-detail' + (open ? ' open' : '') + '" id="clDetail-' + v.id + '">',
                    '<div class="fc-fields-row">',
                    '<div class="fc-field"><label>Vitals type</label><input type="text" id="clDxVitalType-' + v.id + '" placeholder="e.g. Temperature"></div>',
                    '<div class="fc-field"><label>Vitals value</label><input type="text" id="clDxVitalValue-' + v.id + '" placeholder="e.g. 38.2\u00B0C"></div>',
                    '</div>',
                    '<div class="fc-field"><label>Diagnosis</label><textarea id="clDxDiagnosis-' + v.id + '" rows="2" ' + (hasDiagnosis ? 'placeholder="Already recorded for this visit"' : '') + '></textarea></div>',
                    '<div class="fc-field"><label>Prescription</label><textarea id="clDxPrescription-' + v.id + '" rows="2" ' + (hasPrescription ? 'placeholder="Already recorded for this visit"' : '') + '></textarea></div>',
                    '<button class="fc-btn fc-btn-primary cl-save-notes-btn" data-visit-id="' + v.id + '"><i class="fas fa-save"></i> Save Notes</button> ',
                    '<button class="fc-btn fc-btn-secondary cl-discharge-btn" data-visit-id="' + v.id + '"><i class="fas fa-sign-out-alt"></i> Discharge</button>',
                    '</div>',
                    '</div>',
                ].join('');
            }).join('');
        }

        document.getElementById('clTabBody').innerHTML =
            '<div class="panel"><div class="panel-head"><span><i class="fas fa-clinic-medical"></i> Currently at the Clinic</span>' +
            '<button class="fc-btn fc-btn-secondary" id="clDxRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button></div>' +
            '<div class="panel-body">' + body + '</div></div>';

        document.getElementById('clDxRefreshBtn').addEventListener('click', async function () {
            await window.clinicFetchInProgressVisits();
            renderDiagnosisTab();
            toast('Refreshed.', 'success');
        });

        document.querySelectorAll('.cl-visit-row-head').forEach(function (head) {
            head.addEventListener('click', function () {
                var id = Number(head.dataset.visitId);
                state.openVisitId = state.openVisitId === id ? null : id;
                renderDiagnosisTab();
            });
        });

        document.querySelectorAll('.cl-save-notes-btn').forEach(function (btn) {
            btn.addEventListener('click', function (e) {
                e.stopPropagation();
                handleSaveClinicalNotes(btn.dataset.visitId, btn);
            });
        });

        document.querySelectorAll('.cl-discharge-btn').forEach(function (btn) {
            btn.addEventListener('click', function (e) {
                e.stopPropagation();
                handleDischarge(btn.dataset.visitId, btn);
            });
        });
    }

    async function handleSaveClinicalNotes(visitId, btn) {
        var payload = {
            recordedBy: window.clinicState.recordedBy,
            vitalsRecordType: fieldVal('clDxVitalType-' + visitId),
            vitalsValue: fieldVal('clDxVitalValue-' + visitId),
            diagnosisText: fieldVal('clDxDiagnosis-' + visitId),
            prescriptionText: fieldVal('clDxPrescription-' + visitId),
        };

        if (!payload.diagnosisText && !payload.prescriptionText && !payload.vitalsRecordType) {
            toast('Nothing to save.', 'error');
            return;
        }

        btn.disabled = true;
        try {
            await window.clinicUpdateVisitClinicalNotes(visitId, payload);
            renderDiagnosisTab();
            toast('Clinical notes saved.', 'success');
        } catch (e) {
            console.error('[clinic] Failed to save clinical notes:', e);
            toast('Could not save these notes.', 'error');
            btn.disabled = false;
        }
    }

    async function handleDischarge(visitId, btn) {
        var notes = prompt('Discharge notes (optional):') || '';
        btn.disabled = true;
        try {
            await window.clinicDischargeVisit(visitId, {recordedBy: window.clinicState.recordedBy, notes: notes});
            renderTabBody();
            toast('Patient discharged.', 'success');
        } catch (e) {
            console.error('[clinic] Failed to discharge visit:', e);
            toast('Could not discharge this visit.', 'error');
            btn.disabled = false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // TAB 3: MEDICAL HISTORY
    // ─────────────────────────────────────────────────────────────────────
    function renderHistoryTab() {
        document.getElementById('clTabBody').innerHTML = [
            '<div class="cl-scan">',
            '<input id="clHistoryIdInput" placeholder="Enter student ID, then press Enter" value="' + (state.historyPatientId || '') + '">',
            '<button class="fc-btn fc-btn-primary" id="clHistoryLookupBtn">Search</button>',
            '</div>',
            '<div id="clHistoryResult"></div>',
        ].join('');

        var input = document.getElementById('clHistoryIdInput');
        input.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                handleHistorySearch();
            }
        });
        document.getElementById('clHistoryLookupBtn').addEventListener('click', handleHistorySearch);

        if (state.historyResult) renderHistoryResult();
    }

    async function handleHistorySearch() {
        var val = fieldVal('clHistoryIdInput');
        if (!val) return;

        state.historyPatientId = val;
        var btn = document.getElementById('clHistoryLookupBtn');
        btn.disabled = true;
        document.getElementById('clHistoryResult').innerHTML =
            '<div class="loading-state"><i class="fas fa-circle-notch fa-spin"></i> Loading history…</div>';

        try {
            state.historyResult = await window.clinicFetchPatientHistory(val);
            renderHistoryResult();
        } catch (e) {
            console.error('[clinic] Failed to load patient history:', e);
            toast('Could not load history for that student.', 'error');
            document.getElementById('clHistoryResult').innerHTML = '';
        } finally {
            btn.disabled = false;
        }
    }

    function renderHistoryResult() {
        var container = document.getElementById('clHistoryResult');
        if (!container) return;
        var h = state.historyResult;
        var fmt = window.clinicFmt;

        if (!h.visits.length && !h.diagnoses.length && !h.prescriptions.length && !h.vitals.length) {
            container.innerHTML = '<div class="empty-state"><i class="fas fa-inbox"></i> No clinic history found for this student.</div>';
            return;
        }

        container.innerHTML = [
            historySection('Visits', h.visits, function (v) {
                return '<strong>' + (v.reason || 'Visit') + '</strong> — ' + v.status +
                    (v.parentNotified ? ' <span class="cl-badge notified">Parent notified</span>' : '') +
                    '<div class="when">' + fmt.dateTime(v.checkInTime) + (v.recordedBy ? ' · ' + v.recordedBy : '') + '</div>';
            }),
            historySection('Diagnoses', h.diagnoses, function (d) {
                return d.diagnosis + '<div class="when">' + fmt.dateTime(d.dateTime) + '</div>';
            }),
            historySection('Prescriptions', h.prescriptions, function (p) {
                return p.prescription + '<div class="when">' + fmt.dateTime(p.dateTime) + '</div>';
            }),
            historySection('Vitals', h.vitals, function (v) {
                return v.recordType + ': ' + v.value + '<div class="when">' + fmt.dateTime(v.dateTime) + '</div>';
            }),
        ].join('');
    }

    function historySection(title, items, renderItem) {
        if (!items.length) return '';
        return '<div class="panel cl-history-section"><div class="panel-head"><span>' + title + ' (' + items.length + ')</span></div>' +
            '<div class="panel-body">' +
            items.map(function (item) {
                return '<div class="cl-history-entry">' + renderItem(item) + '</div>';
            }).join('') +
            '</div></div>';
    }

    // ─── SHARED HELPERS ─────────────────────────────────────────────────────
    function fieldVal(id) {
        var el = document.getElementById(id);
        return el ? el.value.trim() : '';
    }

    function toast(msg, type) {
        var el = document.getElementById('clToast');
        if (!el) return;
        el.textContent = msg;
        el.className = 'fc-toast show' + (type ? (' fc-toast-' + type) : '');
        setTimeout(function () {
            el.classList.remove('show');
        }, 3500);
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        state.activeTab = window.clinicStartTab || 'checkin';
        state.openVisitId = null;
        buildShell();
        renderTabBody();
        waitForRealData();
    }

    function waitForRealData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.clinicState._loaded || attempts > 200) {
                clearInterval(timer);
                if (state.activeTab === 'diagnosis' || state.activeTab === 'checkin') renderTabBody();
                if (attempts > 200) console.warn('[clinic] API data slow — rendering partial data.');
            }
        }, 30);
    }

    // ─── DYNAMIC SCRIPT LOADER ────────────────────────────────────────────────
    function loadLogicScript() {
        if (window.clinicData && window.clinicState) {
            init();
            return;
        }

        var s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = _scriptBase + '../_clinic.js'; // lives in scripts/ not subscripts/

        s.onload = function () {
            if (window.clinicData && window.clinicState) {
                init();
                window.clinicLoad();
            } else {
                console.error('[clinic] _clinic.js loaded but window.clinicData is not defined.');
            }
        };

        s.onerror = function () {
            console.error('[clinic] Could not load: ' + _scriptBase + '../_clinic.js');
        };

        document.body.appendChild(s);
    }

    // ─── BOOT ─────────────────────────────────────────────────────────────────
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initClinic = init;

})();
