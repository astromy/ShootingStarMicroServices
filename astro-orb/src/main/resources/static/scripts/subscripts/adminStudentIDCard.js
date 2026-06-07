/**
 * IDCardGeneration.js  —  UI renderer for Student ID Card Generation.
 *
 * Loaded by the Orb host page. Injects CSS, loads _adminStudentIDCardGenerator.js,
 * then renders the card preview and print interface once data is ready.
 *
 * Features:
 *   - Class group → class → student cascade
 *   - Individual student preview with live card render
 *   - Multi-select checklist with Select All
 *   - Card style customisation (background, text colour, school name, tagline)
 *   - Bulk print via idCardPrint()
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('IDCardGeneration') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    // ── CSS ──────────────────────────────────────────────────────────────────
    (function () {
        if (!document.getElementById('admIDCSS')) {
            var l = document.createElement('link');
            l.id = 'admIDCSS';
            l.rel = 'stylesheet';
            l.href = _base + '../../styles/style.css';
            document.head.appendChild(l);
        }
        if (!document.getElementById('fcDesignCSS')) {
            var d = document.createElement('link');
            d.id = 'fcDesignCSS';
            d.rel = 'stylesheet';
            d.href = _base + '../../styles/fc-design-system.css';
            document.head.appendChild(d);
        }
        if (!document.getElementById('fcFontAwesome')) {
            var f = document.createElement('link');
            f.id = 'fcFontAwesome';
            f.rel = 'stylesheet';
            f.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css';
            document.head.appendChild(f);
        }
    })();

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        document.getElementById('wrapper').innerHTML = [
            '<div class="fc-page adm-page" id="admIDPage">',

            // HEADER
            '<header class="fc-header">',
            '<div>',
            '<h1><i class="fas fa-id-card"></i> ID Card Generation</h1>',
            '<p>Preview and print student identity cards individually or in bulk</p>',
            '</div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-primary" id="admIDPrintAll" disabled>',
            '<i class="fas fa-print"></i> Print Selected (<span id="admIDSelCount">0</span>)',
            '</button>',
            '</div>',
            '</header>',

            // BODY — two-column
            '<div class="fc-body">',

            // LEFT: student selector
            '<div class="fc-col fc-col-left">',
            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-search"></i> Find Students</div>',
            '<div class="fc-card-body">',

            '<div class="fc-field">',
            '<label>Class Group</label>',
            '<select id="admIDGroupSel" disabled><option value="">Loading…</option></select>',
            '</div>',

            '<div class="fc-field">',
            '<label>Class</label>',
            '<select id="admIDClassSel" disabled><option value="">Select group first</option></select>',
            '</div>',

            '<button class="fc-btn fc-btn-primary" id="admIDLoadBtn" disabled>',
            '<i class="fas fa-users"></i> Load Students',
            '</button>',
            '</div></div>',

            // Student checklist card
            '<div class="fc-card" id="admIDCheckCard" style="display:none">',
            '<div class="fc-card-head">',
            '<span id="admIDCheckTitle"><i class="fas fa-list-ul"></i> Students</span>',
            '<label style="display:flex;align-items:center;gap:6px;font-size:12px;font-weight:600;cursor:pointer;">',
            '<input type="checkbox" id="admIDSelectAll"> Select All',
            '</label>',
            '</div>',
            '<div class="adm-id-checklist" id="admIDChecklist"></div>',
            '</div>',

            '</div>', // end left col

            // RIGHT: preview + settings
            '<div class="fc-col fc-col-right">',

            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-eye"></i> ID Card Preview</div>',
            '<div class="fc-card-body" id="admIDPreviewBody">',
            '<div class="adm-id-placeholder">',
            '<i class="fas fa-id-card" style="font-size:40px;color:var(--fc-muted);opacity:0.4"></i>',
            '<p style="color:var(--fc-muted);margin-top:10px;font-size:13px">Select a student to preview ID card</p>',
            '</div>',
            '</div>',
            '</div>',

            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-palette"></i> Card Settings</div>',
            '<div class="fc-card-body">',
            '<div class="fc-fields-row">',
            '<div class="fc-field">',
            '<label>Background Color</label>',
            '<input type="color" id="admIDCardBg" value="#1A3A5C">',
            '</div>',
            '<div class="fc-field">',
            '<label>Text Color</label>',
            '<input type="color" id="admIDCardFg" value="#ffffff">',
            '</div>',
            '<div class="fc-field">',
            '<label>School Name Override</label>',
            '<input type="text" id="admIDSchoolName" placeholder="Leave blank to use institution name">',
            '</div>',
            '</div>',
            '<div class="fc-field">',
            '<label>Tagline / Motto</label>',
            '<input type="text" id="admIDTagline" placeholder="e.g. Excellence in Education">',
            '</div>',
            '<button class="fc-btn fc-btn-secondary" id="admIDApplySettings">',
            '<i class="fas fa-check"></i> Apply Settings',
            '</button>',
            '</div></div>',

            '</div>', // end right col
            '</div>', // end body

            '<div class="fc-toast" id="admIDToast"></div>',

            '<footer class="footer">',
            '<i class="far fa-copyright"></i> Astromy LLC 2013–<span id="admIDYear"></span> | ID Card Generation',
            '</footer>',

            '</div>',
        ].join('');

        document.getElementById('admIDYear').textContent = new Date().getFullYear();
    }

    // ── POPULATE GROUP DROPDOWN ──────────────────────────────────────────────
    function populateGroups() {
        var sel = document.getElementById('admIDGroupSel');
        if (!sel) return;
        sel.innerHTML = '<option value="">Select group…</option>' +
            window.idCardData.classGroups.map(function (g) {
                return '<option value="' + g.name + '">' + g.name + '</option>';
            }).join('');
        sel.disabled = false;
    }

    // ── CURRENT STYLE ────────────────────────────────────────────────────────
    function readStyle() {
        return {
            bg: document.getElementById('admIDCardBg').value || '#1A3A5C',
            fg: document.getElementById('admIDCardFg').value || '#ffffff',
            school: document.getElementById('admIDSchoolName').value.trim(),
            tagline: document.getElementById('admIDTagline').value.trim(),
        };
    }

    // ── RENDER PREVIEW ───────────────────────────────────────────────────────
    function renderPreview(student) {
        var body = document.getElementById('admIDPreviewBody');
        if (!body) return;
        if (!student) {
            body.innerHTML = [
                '<div class="adm-id-placeholder">',
                '<i class="fas fa-id-card" style="font-size:40px;color:var(--fc-muted);opacity:0.4"></i>',
                '<p style="color:var(--fc-muted);margin-top:10px;font-size:13px">Select a student to preview ID card</p>',
                '</div>',
            ].join('');
            return;
        }
        body.innerHTML = window.idCardBuildCard(student, readStyle());
    }

    // ── RENDER CHECKLIST ─────────────────────────────────────────────────────
    function renderChecklist(students) {
        var list = document.getElementById('admIDChecklist');
        var card = document.getElementById('admIDCheckCard');
        var title = document.getElementById('admIDCheckTitle');
        if (!list) return;

        card.style.display = '';
        title.innerHTML = '<i class="fas fa-list-ul"></i> Students (' + students.length + ')';

        if (!students.length) {
            list.innerHTML = '<div class="fc-empty" style="padding:12px">No students found in this class</div>';
            return;
        }

        list.innerHTML = students.map(function (s) {
            var name = window.idCardFmt.studentName(s);
            return '<label class="adm-id-check-row" data-id="' + s.studentId + '">' +
                '<input type="checkbox" class="adm-id-chk" value="' + s.studentId + '">' +
                '<span class="adm-id-chk-name">' + name + '</span>' +
                '<span class="adm-id-chk-id">' + (s.studentId || '') + '</span>' +
                '</label>';
        }).join('');

        // Attach individual checkbox listeners
        list.querySelectorAll('.adm-id-chk').forEach(function (chk) {
            chk.addEventListener('change', function () {
                updateSelection();
                // Preview the last-checked student
                if (this.checked) {
                    var sid = this.value;
                    var student = (window.idCardData.students[_currentCacheKey] || [])
                        .find(function (s) {
                            return s.studentId === sid;
                        });
                    if (student) renderPreview(student);
                }
            });
        });

        // Row click passes through to checkbox
        list.querySelectorAll('.adm-id-check-row').forEach(function (row) {
            row.addEventListener('click', function (e) {
                if (e.target.tagName === 'INPUT') return; // already handled
                var chk = row.querySelector('.adm-id-chk');
                if (chk) chk.click();
            });
        });
    }

    // ── SELECTION SYNC ───────────────────────────────────────────────────────
    var _currentCacheKey = '';

    function updateSelection() {
        var checked = Array.from(
            document.querySelectorAll('.adm-id-chk:checked')
        ).map(function (c) {
            return c.value;
        });

        window.idCardState.selectedStudents = checked;
        document.getElementById('admIDSelCount').textContent = checked.length;
        document.getElementById('admIDPrintAll').disabled = !checked.length;

        // Sync Select All checkbox
        var all = document.querySelectorAll('.adm-id-chk');
        var selAll = document.getElementById('admIDSelectAll');
        if (selAll && all.length) selAll.checked = (checked.length === all.length);
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {

        // Group → populate classes
        document.getElementById('admIDGroupSel').addEventListener('change', function () {
            var group = this.value;
            var classSel = document.getElementById('admIDClassSel');
            var classes = window.idCardClassesByGroup(group);
            classSel.innerHTML = '<option value="">Select class…</option>' +
                classes.map(function (c) {
                    return '<option>' + c.name + '</option>';
                }).join('');
            classSel.disabled = !classes.length;
            document.getElementById('admIDLoadBtn').disabled = true;
            document.getElementById('admIDCheckCard').style.display = 'none';
        });

        // Class → enable Load button
        document.getElementById('admIDClassSel').addEventListener('change', function () {
            document.getElementById('admIDLoadBtn').disabled = !this.value;
        });

        // Load Students button
        document.getElementById('admIDLoadBtn').addEventListener('click', async function () {
            var groupSel = document.getElementById('admIDGroupSel');
            var classSel = document.getElementById('admIDClassSel');
            var group = groupSel.value;
            var cls = classSel.value;
            if (!group || !cls) return;

            _currentCacheKey = group + '::' + cls;
            var students = await window.idCardFetchStudents(group, cls);
            renderChecklist(students);
            renderPreview(null);
            updateSelection();
        });

        // Select All toggle
        document.getElementById('admIDSelectAll').addEventListener('change', function () {
            var checked = this.checked;
            document.querySelectorAll('.adm-id-chk').forEach(function (chk) {
                chk.checked = checked;
            });
            updateSelection();
            // Preview first student when selecting all
            if (checked) {
                var students = window.idCardData.students[_currentCacheKey] || [];
                if (students.length) renderPreview(students[0]);
            }
        });

        // Apply Settings → re-render preview with current selection
        document.getElementById('admIDApplySettings').addEventListener('click', function () {
            var style = readStyle();
            window.idCardState.currentStyle = style;

            // Re-render preview for currently shown student
            var students = window.idCardData.students[_currentCacheKey] || [];
            var selected = window.idCardState.selectedStudents;
            var previewStudent = selected.length
                ? students.find(function (s) {
                    return s.studentId === selected[selected.length - 1];
                })
                : null;
            renderPreview(previewStudent || null);
            toast('Card settings applied.', 'success');
        });

        // Print Selected
        document.getElementById('admIDPrintAll').addEventListener('click', function () {
            var students = (window.idCardData.students[_currentCacheKey] || []).filter(function (s) {
                return window.idCardState.selectedStudents.includes(s.studentId);
            });
            if (!students.length) {
                toast('No students selected.', 'error');
                return;
            }
            window.idCardPrint(students, window.idCardState.currentStyle);
        });
    }

    // ── TOAST ────────────────────────────────────────────────────────────────
    function toast(msg, type) {
        var el = document.getElementById('admIDToast');
        if (!el) return;
        el.textContent = msg;
        el.className = 'fc-toast show ' + (type || 'info');
        clearTimeout(el._t);
        el._t = setTimeout(function () {
            el.classList.remove('show');
        }, 3500);
    }

    // ── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        waitForData();
    }

    function waitForData() {
        var t = setInterval(function () {
            if (window.idCardState && window.idCardState._loaded) {
                clearInterval(t);
                populateGroups();
            }
        }, 30);
    }

    function boot() {
        if (window.idCardState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_adminStudentIDCardGenerator.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();

})();