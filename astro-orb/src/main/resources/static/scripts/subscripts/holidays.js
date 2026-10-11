/**
 * holidays.js  —  Setup -> Holidays.
 *
 * The school's public-holiday calendar. Leave, man-hours and overtime all read
 * it, so it has to be right: "Staff are off" is the date that counts, and it
 * can differ from the date the law names (a holiday on a weekend moves to the
 * next working day; the President can move a midweek holiday to the Friday
 * before or the Monday after).
 *
 * The dates are fetched once for the year from a holiday service (Mansa API, else
 * HolidaysMap - by the server, at the start of the year or when someone presses
 * Load holidays) and kept, so
 * they are used all year without asking again. They are only a starting point:
 * anything here can be edited, added or removed, and a person's change is
 * never overwritten by a later load.
 *
 * Access is controlled by the "Holidays" permission. _payrollShared.js is
 * loaded before this file by common.js.
 */
(function () {
    'use strict';

    var P = window.payrollShared;
    var esc = P.escape;
    var toastTimer;
    var state = {year: new Date().getFullYear(), rows: [], editingId: null, deletingId: null};

    var TYPES = {
        STATUTORY: 'Statutory',
        DECLARED: 'Declared by government',
        SCHOOL: 'School closure'
    };

    function buildDOM() {
        var thisYear = new Date().getFullYear();
        var yearOptions = [thisYear - 1, thisYear, thisYear + 1, thisYear + 2].map(function (y) {
            return '<option value="' + y + '"' + (y === state.year ? ' selected' : '') + '>' + y + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-calendar-check"></i> Public Holidays</h1>',
            '<p>The days staff are off. Leave, man-hours and overtime all use this calendar.</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-primary" id="hdSeedBtn"><i class="fas fa-flag"></i> Load holidays</button>',
            '<button class="fc-btn fc-btn-secondary" id="hdRefreshBtn" disabled title="Ask the holiday service for the year again"><i class="fas fa-cloud-arrow-down"></i> Refresh dates</button>',
            '<button class="fc-btn fc-btn-success" id="hdAddBtn"><i class="fas fa-plus"></i> Add holiday</button>',
            '</div>',
            '</header>',

            '<div class="fc-card" style="margin-bottom:16px"><div class="fc-card-body" style="font-size:13px;line-height:1.55">',
            '<div id="hdSource" style="margin-bottom:8px">Checking where this year\'s dates come from…</div>',
            '<strong>Check each year against the Ministry of the Interior\'s notices.</strong> ',
            'Dates from a holiday service can be wrong or late, and Ghana moves holidays by Executive Instrument ',
            '(a midweek holiday can go to the Friday before or the Monday after). Change the <em>Staff are off</em> date, add Eid when it is announced, ',
            'or remove anything that doesn\'t apply. <strong>Your changes are never overwritten</strong> when holidays are loaded again.',
            '</div></div>',

            '<div class="ph-controls">',
            '<select id="hdYear" aria-label="Year">' + yearOptions + '</select>',
            '</div>',

            '<div class="ph-table-wrap"><table class="fc-table" id="hdTable">',
            '<thead><tr><th>Holiday</th><th>The law says</th><th>Staff are off</th><th>Type</th><th>Source</th><th>Note</th><th></th></tr></thead>',
            '<tbody></tbody></table></div>',

            // Add / edit
            '<div class="fc-modal-overlay" id="hdModal"><div class="fc-modal">',
            '<div class="fc-modal-head"><h5 id="hdModalTitle"><i class="fas fa-calendar-plus"></i> Add holiday</h5>',
            '<button class="fc-modal-close" data-close="hdModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body">',
            '<div class="fc-field"><label for="hdName">Holiday</label><input type="text" id="hdName" placeholder="e.g. Eid ul-Fitr"></div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="hdStatutory">The law says (optional)</label><input type="date" id="hdStatutory"></div>',
            '<div class="fc-field"><label for="hdObserved">Staff are off</label><input type="date" id="hdObserved"></div>',
            '</div>',
            '<div class="fc-field"><label for="hdType">Type</label><select id="hdType">',
            '<option value="STATUTORY">Statutory</option>',
            '<option value="DECLARED">Declared by government</option>',
            '<option value="SCHOOL">School closure</option>',
            '</select></div>',
            '<div class="fc-field"><label for="hdNote">Note (optional)</label><input type="text" id="hdNote" placeholder="e.g. Moved to Friday by Executive Instrument"></div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="hdModal">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="hdSave"><i class="fas fa-check"></i> Save</button>',
            '</div></div></div>',

            // Refresh from HolidaysMap
            '<div class="fc-modal-overlay" id="hdRefreshModal"><div class="fc-modal">',
            '<div class="fc-modal-head"><h5><i class="fas fa-cloud-arrow-down"></i> Refresh the dates</h5>',
            '<button class="fc-modal-close" data-close="hdRefreshModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body"><p style="margin:0 0 10px">Ask the holiday service for this year\'s dates again?</p>',
            '<ul style="margin:0 0 0 18px;font-size:13px;line-height:1.6">',
            '<li>It uses part of the service\'s request allowance - normally one request.</li>',
            '<li>It can be done once a day.</li>',
            '<li>Holidays you added, edited or moved are <strong>never changed</strong>; only missing ones are added.</li>',
            '</ul></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="hdRefreshModal">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="hdRefreshConfirm"><i class="fas fa-cloud-arrow-down"></i> Refresh</button>',
            '</div></div></div>',

            // Delete
            '<div class="fc-modal-overlay" id="hdDeleteModal"><div class="fc-modal">',
            '<div class="fc-modal-head"><h5><i class="fas fa-trash"></i> Remove holiday</h5>',
            '<button class="fc-modal-close" data-close="hdDeleteModal" aria-label="Close">&times;</button></div>',
            '<div class="fc-modal-body"><p id="hdDeleteText" style="margin:0"></p></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" data-close="hdDeleteModal">Keep it</button>',
            '<button class="fc-btn fc-btn-primary" id="hdDeleteConfirm"><i class="fas fa-trash"></i> Remove</button>',
            '</div></div></div>',

            '<div class="fc-toast" id="hdToast" role="status" aria-live="polite"></div>',
            '</div>'
        ].join('');
    }

    function toast(message, type) {
        var el = document.getElementById('hdToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 5000);
    }

    function open(id) { document.getElementById(id).classList.add('open'); }

    function close(id) { document.getElementById(id).classList.remove('open'); }

    // yyyy-MM-dd -> "Fri 09 Jan 2026" without time-zone surprises
    function fmt(ymd) {
        if (!ymd) return '—';
        var p = String(ymd).split('-');
        return new Date(+p[0], +p[1] - 1, +p[2]).toLocaleDateString('en-GB',
            {weekday: 'short', day: '2-digit', month: 'short', year: 'numeric'});
    }

    function render() {
        var tbody = document.querySelector('#hdTable tbody');
        if (!state.rows.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="fc-empty">No holidays for ' + state.year +
                ' yet. Use <strong>Load holidays</strong> to start from the standard list.</td></tr>';
            return;
        }
        tbody.innerHTML = state.rows.map(function (h) {
            var moved = h.statutoryDate && h.statutoryDate !== h.observedDate;
            return '<tr>' +
                '<td><strong>' + esc(h.name) + '</strong></td>' +
                '<td>' + (moved ? esc(fmt(h.statutoryDate)) : '<span style="color:#9aa3ad">same day</span>') + '</td>' +
                '<td><strong>' + esc(fmt(h.observedDate)) + '</strong>' +
                (moved ? ' <span style="font-size:11px;background:#fff4d6;color:#8a6100;border-radius:10px;padding:2px 8px">moved</span>' : '') + '</td>' +
                '<td>' + esc(TYPES[h.type] || h.type) + '</td>' +
                '<td>' + sourceBadge(h.source) + '</td>' +
                '<td>' + esc(h.note || '') + '</td>' +
                '<td style="white-space:nowrap">' +
                '<button class="fc-btn fc-btn-secondary fc-btn-sm" data-edit="' + esc(h.id) + '" aria-label="Edit"><i class="fas fa-pen"></i></button> ' +
                '<button class="fc-btn fc-btn-secondary fc-btn-sm" data-del="' + esc(h.id) + '" aria-label="Remove"><i class="fas fa-trash"></i></button>' +
                '</td></tr>';
        }).join('');
    }

    function sourceBadge(source) {
        var m = {
            MANSA: ['Mansa API', '#e8f0fe', '#1a56c4'],
            HOLIDAYSMAP: ['HolidaysMap', '#e8f0fe', '#1a56c4'],
            API: ['Holiday service', '#e8f0fe', '#1a56c4'],
            RULES: ['Built-in rules', '#fff4d6', '#8a6100'],
            ADMIN: ['You', '#e6f4ea', '#1e6b3a']
        }[source];
        if (!m) return '<span style="color:#9aa3ad">—</span>';
        return '<span style="font-size:11px;background:' + m[1] + ';color:' + m[2] + ';border-radius:10px;padding:2px 9px">' + m[0] + '</span>';
    }

    // Where this year's dates came from: fetched once and kept, or why not.
    function renderSource(st) {
        var el = document.getElementById('hdSource');
        var when = st.fetchedAt ? P.date(st.fetchedAt) : '';
        var from = st.provider ? esc(st.provider) : 'a holiday service';
        var html;
        if (st.inProgress) {
            html = '<i class="fas fa-spinner fa-spin"></i> Fetching ' + state.year + ' one day at a time. This takes a few minutes - reload then.';
        } else if (!st.apiConfigured) {
            html = '<i class="fas fa-circle-info" style="color:#2b6cb0"></i> No holiday service is connected (no API key is set on the server). <strong>Load holidays</strong> uses the built-in rules.';
        } else if (st.cached) {
            html = '<i class="fas fa-circle-check" style="color:#1e8e5a"></i> ' + state.year + ' dates were fetched from ' + from + ' on <strong>' + esc(when) +
                '</strong> (' + st.count + ' holidays) and are kept for the year.' +
                (st.lastAttemptFailed ? ' <span style="color:#b42318">The latest refresh failed (' + esc(st.message || '') + ') - the earlier dates were kept.</span>' : '');
        } else if (st.lastAttemptFailed) {
            html = '<i class="fas fa-triangle-exclamation" style="color:#b7791f"></i> Couldn\'t fetch the dates: ' + esc(st.message || '') +
                ' The built-in rules are used instead.';
        } else {
            html = '<i class="fas fa-circle-info" style="color:#2b6cb0"></i> ' + state.year +
                ' hasn\'t been fetched yet. <strong>Load holidays</strong> fetches it once and keeps it.';
        }
        el.innerHTML = html;
        var refresh = document.getElementById('hdRefreshBtn');
        refresh.disabled = !st.apiConfigured || !!st.inProgress;
        refresh.title = !st.apiConfigured ? 'No holiday service is connected on the server' : 'Ask the holiday service for ' + state.year + ' again';
    }

    function loadStatus() {
        return fetchPost('getHolidaySyncStatus', {year: state.year}).then(renderSource).catch(function () {
            document.getElementById('hdSource').textContent = '';
        });
    }

    function find(id) {
        return state.rows.filter(function (h) { return h.id === id; })[0];
    }

    function load() {
        loadStatus();
        return fetchPost('getHolidays', {year: state.year}).then(function (rows) {
            state.rows = rows || [];
            render();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        });
    }

    function openForm(holiday) {
        state.editingId = holiday ? holiday.id : null;
        document.getElementById('hdModalTitle').innerHTML = holiday
            ? '<i class="fas fa-pen"></i> Edit holiday'
            : '<i class="fas fa-calendar-plus"></i> Add holiday';
        document.getElementById('hdName').value = holiday ? holiday.name : '';
        document.getElementById('hdStatutory').value = holiday && holiday.statutoryDate ? holiday.statutoryDate : '';
        document.getElementById('hdObserved').value = holiday ? holiday.observedDate : '';
        document.getElementById('hdType').value = holiday ? holiday.type : 'DECLARED';
        document.getElementById('hdNote').value = holiday && holiday.note ? holiday.note : '';
        open('hdModal');
    }

    function save() {
        var name = document.getElementById('hdName').value.trim();
        var observed = document.getElementById('hdObserved').value;
        if (!name) return toast('Give the holiday a name.', 'error');
        if (!observed) return toast('Choose the date staff are off.', 'error');

        var statutory = document.getElementById('hdStatutory').value;
        var btn = document.getElementById('hdSave');
        btn.disabled = true;
        fetchPost('saveHoliday', {
            id: state.editingId,
            name: name,
            statutoryDate: statutory || null,
            observedDate: observed,
            type: document.getElementById('hdType').value,
            note: document.getElementById('hdNote').value.trim() || null
        }).then(function (saved) {
            close('hdModal');
            // Show the year the holiday landed in, so it doesn't seem to vanish.
            var landedIn = saved && saved.year ? saved.year : state.year;
            if (landedIn !== state.year) {
                state.year = landedIn;
                var select = document.getElementById('hdYear');
                if (![].some.call(select.options, function (o) { return +o.value === landedIn; })) {
                    select.insertAdjacentHTML('beforeend', '<option value="' + landedIn + '">' + landedIn + '</option>');
                }
                select.value = landedIn;
            }
            toast('Holiday saved.', 'success');
            return load();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    function seed() {
        var btn = document.getElementById('hdSeedBtn');
        btn.disabled = true;
        fetchPost('seedGhanaHolidays', {year: state.year}).then(function (result) {
            state.rows = result.holidays || [];
            render();
            loadStatus();
            toast(summary(result), result.added ? 'success' : '');
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    // One sentence on what a load did and where the dates came from.
    function summary(result) {
        var from = result.provider ? ' from ' + result.provider : ' from the built-in rules';
        var text = result.added
            ? 'Added ' + result.added + ' holiday' + (result.added === 1 ? '' : 's') + from + '. Now check for moved days and add Eid.'
            : 'Nothing to add - the holidays are already there.';
        return result.sourceNote ? text + ' ' + result.sourceNote : text;
    }

    function refreshFromApi() {
        var btn = document.getElementById('hdRefreshConfirm');
        btn.disabled = true;
        fetchPost('refreshHolidaysFromApi', {year: state.year}).then(function (result) {
            close('hdRefreshModal');
            state.rows = result.holidays || [];
            render();
            loadStatus();
            toast(summary(result), result.added ? 'success' : '');
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    function confirmDelete() {
        var btn = document.getElementById('hdDeleteConfirm');
        btn.disabled = true;
        fetchPost('deleteHoliday', {id: state.deletingId}).then(function () {
            close('hdDeleteModal');
            toast('Holiday removed.', 'success');
            return load();
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    function bind() {
        document.getElementById('hdYear').addEventListener('change', function () {
            state.year = parseInt(this.value, 10);
            load();
        });
        document.getElementById('hdAddBtn').addEventListener('click', function () { openForm(null); });
        document.getElementById('hdSeedBtn').addEventListener('click', seed);
        document.getElementById('hdRefreshBtn').addEventListener('click', function () { open('hdRefreshModal'); });
        document.getElementById('hdRefreshConfirm').addEventListener('click', refreshFromApi);
        document.getElementById('hdSave').addEventListener('click', save);
        document.getElementById('hdDeleteConfirm').addEventListener('click', confirmDelete);

        // Typing the law's date fills the observed date until it is set by hand.
        document.getElementById('hdStatutory').addEventListener('change', function () {
            var observed = document.getElementById('hdObserved');
            if (!observed.value) observed.value = this.value;
        });

        document.getElementById('hdTable').addEventListener('click', function (e) {
            var edit = e.target.closest('[data-edit]');
            var del = e.target.closest('[data-del]');
            if (edit) {
                var h = find(edit.getAttribute('data-edit'));
                if (h) openForm(h);
            } else if (del) {
                var d = find(del.getAttribute('data-del'));
                if (!d) return;
                state.deletingId = d.id;
                document.getElementById('hdDeleteText').textContent =
                    'Remove "' + d.name + '" (' + fmt(d.observedDate) + ')? Staff will be expected to work that day, ' +
                    'and it will count as a normal day in leave and man-hours. Overtime that has already been approved is not changed.';
                open('hdDeleteModal');
            }
        });

        document.querySelectorAll('[data-close]').forEach(function (b) {
            b.addEventListener('click', function () { close(b.getAttribute('data-close')); });
        });
        document.querySelectorAll('.fc-modal-overlay').forEach(function (o) {
            o.addEventListener('click', function (e) { if (e.target === o) o.classList.remove('open'); });
        });
    }

    buildDOM();
    bind();
    load();
})();
