/**
 * _adminStudentIDCardGenerator.js  —  Data & logic layer for Student ID Card generation.
 *
 * Exposes on window:
 *   idCardState          — mutable UI state
 *   idCardData           — loaded data cache
 *   idCardLoad()         — initial load
 *   idCardFetchStudents(classGroup, className) → Student[]
 *   idCardBuildCard(student, style) → HTML string
 *   idCardBuildCards(students, style) → HTML string
 *   idCardPrint(students, style)
 *   idCardFmt
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.idCardState = {
        institutionCode: _inst,
        institutionName: '',
        selectedStudents: [],   // studentId[]
        currentStyle: {
            bg: '#1A3A5C',
            fg: '#ffffff',
            school: '',
            tagline: '',
        },
        _loaded: false,
    };

    window.idCardData = {
        institution: null,
        classGroups: [],        // { id, name }[]
        classes: {},            // groupName → { name }[]
        students: {},           // className → Student[]
    };

    // ── HELPERS ──────────────────────────────────────────────────────────────
    function showSplash() {
        if ($) $('.splash').css({display: 'block', background: '#ffffff3d'}).find('h1,p').remove();
    }

    function hideSplash() {
        if ($) $('.splash').css('display', 'none');
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    // ── INITIAL LOAD ─────────────────────────────────────────────────────────
    window.idCardLoad = async function () {
        try {
            showSplash();
            var [inst, groups] = await Promise.all([
                fetchPost('getInstitutionByCode', {val: _inst}),
                fetchPost('getLookUpByType', {val: 'ClassGroup'}),
            ]);
            if (inst) {
                window.idCardData.institution = inst;
                window.idCardState.institutionName = inst.name || _inst;
                if (inst.classList) {
                    // Build classes lookup keyed by classGroup name
                    inst.classList.forEach(function (c) {
                        var grp = c.classGroup || '';
                        if (!window.idCardData.classes[grp]) window.idCardData.classes[grp] = [];
                        window.idCardData.classes[grp].push({name: c.name});
                    });
                }
            }
            if (groups) {
                window.idCardData.classGroups = groups.map(function (g) {
                    return {id: g.id, name: g.name};
                });
            }
            window.idCardState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.idCardState._loaded = true;
            console.error('[_adminStudentIDCardGenerator] load error:', e);
        }
    };

    // ── CLASS FILTERING ──────────────────────────────────────────────────────
    window.idCardClassesByGroup = function (groupName) {
        return window.idCardData.classes[groupName] || [];
    };

    // ── STUDENT FETCH ────────────────────────────────────────────────────────
    window.idCardFetchStudents = async function (classGroup, className) {
        var cacheKey = (classGroup || '') + '::' + (className || '');
        if (window.idCardData.students[cacheKey]) return window.idCardData.students[cacheKey];

        showSplash();
        try {
            var result = await fetchPost('getStudentsByClass', {
                classGroup: classGroup || '',
                className: className || '',
                institutionCode: _inst,
            });
            var students = Array.isArray(result) ? result : [];
            window.idCardData.students[cacheKey] = students;
            hideSplash();
            return students;
        } catch (e) {
            hideSplash();
            console.error('[_adminStudentIDCardGenerator] fetch students error:', e);
            return [];
        }
    };

    // ── FORMAT HELPERS ───────────────────────────────────────────────────────
    window.idCardFmt = {
        studentName: function (student) {
            return [
                student.studentLastName || '',
                student.studentFirstName || '',
                student.studentOtherName || '',
            ].filter(Boolean).join(' ').trim() || student.studentId || '—';
        },
        initials: function (student) {
            var first = (student.studentFirstName || '?')[0];
            var last = (student.studentLastName || '?')[0];
            return (last + first).toUpperCase();
        },
        academicYear: function () {
            var y = new Date().getFullYear();
            return y + '/' + (y + 1);
        },
    };

    // ── CARD BUILDING ────────────────────────────────────────────────────────
    window.idCardBuildCard = function (student, style) {
        if (!student) return '';
        var fmt = window.idCardFmt;
        var schoolName = style.school || window.idCardState.institutionName || 'School Name';

        return [
            '<div class="adm-id-card" style="--card-bg:' + escapeHtml(style.bg) + ';--card-fg:' + escapeHtml(style.fg) + '">',
            '<div class="adm-id-card-header">',
            '<div class="adm-id-school">' + escapeHtml(schoolName) + '</div>',
            style.tagline ? '<div class="adm-id-tagline">' + escapeHtml(style.tagline) + '</div>' : '',
            '</div>',
            '<div class="adm-id-card-body">',
            '<div class="adm-id-avatar">' + escapeHtml(fmt.initials(student)) + '</div>',
            '<div class="adm-id-info">',
            '<div class="adm-id-fullname">' + escapeHtml(fmt.studentName(student)) + '</div>',
            '<div class="adm-id-field"><span>Student ID</span><strong>' + escapeHtml(student.studentId || '—') + '</strong></div>',
            '<div class="adm-id-field"><span>Class</span><strong>' + escapeHtml(student.studentClass || '—') + '</strong></div>',
            '<div class="adm-id-field"><span>Gender</span><strong>' + escapeHtml(student.studentGender || '—') + '</strong></div>',
            '</div>',
            '</div>',
            '<div class="adm-id-card-footer">',
            '<div class="adm-id-barcode">||||||||||||| ' + escapeHtml(student.studentId || '') + ' |||||||||||||</div>',
            '<div class="adm-id-validity">Academic Year — ' + fmt.academicYear() + '</div>',
            '</div>',
            '</div>',
        ].join('');
    };

    window.idCardBuildCards = function (students, style) {
        if (!students || !students.length) return '';
        return students.map(function (s) {
            return window.idCardBuildCard(s, style);
        }).join('');
    };

    // ── PRINTING ─────────────────────────────────────────────────────────────
    window.idCardPrint = function (students, style) {
        if (!students || !students.length) return;

        var cards = window.idCardBuildCards(students, style);
        var printStyles = [
            'body{margin:0;padding:20px;background:#f4f6f9;font-family:"Segoe UI",sans-serif;}',
            '.adm-id-card{background:var(--card-bg,#1A3A5C);color:var(--card-fg,#fff);',
            'width:320px;border-radius:12px;overflow:hidden;margin:10px;display:inline-block;',
            'box-shadow:0 4px 16px rgba(0,0,0,0.25);vertical-align:top;}',
            '.adm-id-card-header{padding:14px 16px 10px;border-bottom:2px solid rgba(255,255,255,0.2);}',
            '.adm-id-school{font-size:14px;font-weight:800;letter-spacing:1px;text-transform:uppercase;}',
            '.adm-id-tagline{font-size:10px;opacity:0.7;margin-top:2px;}',
            '.adm-id-card-body{display:flex;gap:12px;padding:14px 16px;}',
            '.adm-id-avatar{width:56px;height:56px;border-radius:50%;background:rgba(255,255,255,0.2);',
            'display:flex;align-items:center;justify-content:center;font-size:22px;font-weight:800;flex-shrink:0;}',
            '.adm-id-fullname{font-size:13px;font-weight:700;margin-bottom:6px;}',
            '.adm-id-field{display:flex;gap:8px;font-size:11px;margin-bottom:3px;}',
            '.adm-id-field span{opacity:0.65;min-width:60px;}',
            '.adm-id-field strong{font-weight:700;}',
            '.adm-id-card-footer{padding:8px 16px;background:rgba(0,0,0,0.2);font-size:10px;',
            'display:flex;justify-content:space-between;}',
            '.adm-id-barcode{font-family:monospace;letter-spacing:2px;font-size:9px;opacity:0.6;}',
            '@media print{body{padding:0;background:white;}',
            '.adm-id-card{margin:8px;width:280px;break-inside:avoid;}}',
        ].join('');

        var win = window.open('', '_blank', 'width=800,height=600');
        win.document.write('<!DOCTYPE html><html><head>' +
            '<meta charset="utf-8"><title>Student ID Cards</title>' +
            '<style>' + printStyles + '</style>' +
            '</head><body>' + cards + '</body></html>');
        win.document.close();
        setTimeout(function () {
            win.print();
        }, 400);
    };

    // ── BOOT ─────────────────────────────────────────────────────────────────
    if (window.copyrights) window.copyrights();
    idCardLoad();

})();