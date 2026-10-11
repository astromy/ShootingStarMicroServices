/**
 * workingHours.js  —  Setup -> Working Hours.
 *
 * The working week and day, and the overtime rules and rates. Everything that
 * counts hours reads this: leave (which days are working days), man-hours
 * (hours expected) and overtime (what counts as overtime and what it pays).
 *
 * Overtime stays off until it is switched on here. Access is controlled by the
 * "Working Hours" permission. _payrollShared.js is loaded before this file by
 * common.js.
 */
(function () {
    'use strict';

    var P = window.payrollShared;
    var esc = P.escape;
    var toastTimer;

    // ISO numbering, as the server uses it: 1 = Monday ... 7 = Sunday
    var DAYS = [[1, 'Mon'], [2, 'Tue'], [3, 'Wed'], [4, 'Thu'], [5, 'Fri'], [6, 'Sat'], [7, 'Sun']];

    function buildDOM() {
        var dayBoxes = DAYS.map(function (d) {
            return '<label style="display:inline-flex;align-items:center;gap:6px;margin:0 14px 6px 0;font-weight:500">' +
                '<input type="checkbox" class="whDay" value="' + d[0] + '"> ' + d[1] + '</label>';
        }).join('');

        var rounding = [1, 5, 10, 15, 30, 60].map(function (m) {
            return '<option value="' + m + '">' + (m === 1 ? 'exact minutes' : m + ' minutes') + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-clock"></i> Working Hours</h1>',
            '<p>The working week and day, and the rules and rates for overtime.</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-primary" id="whSave"><i class="fas fa-check"></i> Save</button>',
            '</div>',
            '</header>',

            '<div class="fc-card" id="whBanner" style="margin-bottom:16px;display:none"><div class="fc-card-body" style="font-size:13px"></div></div>',

            // Working week and day
            '<div class="fc-card" style="margin-bottom:16px">',
            '<div class="fc-card-head"><i class="fas fa-calendar-week"></i> Working week and day</div>',
            '<div class="fc-card-body">',
            '<div class="fc-field"><label>Working days</label><div>' + dayBoxes + '</div></div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="whStart">Opening time</label><input type="time" id="whStart"></div>',
            '<div class="fc-field"><label for="whEnd">Closing time</label><input type="time" id="whEnd"></div>',
            '<div class="fc-field"><label for="whBreak">Unpaid break (minutes)</label><input type="number" id="whBreak" min="0" max="240" step="5"></div>',
            '</div>',
            '<p style="margin:4px 0 0;font-size:13px;color:#5b6573">Paid hours per day: <strong id="whPaidHours">—</strong>. ',
            'Public holidays come from <em>Setup &rarr; Holidays</em> and are never working days.</p>',
            '</div></div>',

            // Overtime
            '<div class="fc-card" style="margin-bottom:16px">',
            '<div class="fc-card-head"><i class="fas fa-business-time"></i> Overtime</div>',
            '<div class="fc-card-body">',
            '<label style="display:flex;align-items:center;gap:8px;font-weight:600;margin-bottom:6px">',
            '<input type="checkbox" id="whOvertimeOn"> Track overtime from clock-in and clock-out</label>',
            '<p style="margin:0 0 14px;font-size:13px;color:#5b6573">Overtime is worked out when staff clock out (Pulse or the Leave screen). ',
            'It is paid only after someone approves it under <em>Human Resource &rarr; Overtime</em>.</p>',

            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="whAfter">After hours (times normal pay)</label><input type="number" id="whAfter" min="1" max="5" step="0.05"></div>',
            '<div class="fc-field"><label for="whWeekend">Weekend / rest day</label><input type="number" id="whWeekend" min="1" max="5" step="0.05"></div>',
            '<div class="fc-field"><label for="whHoliday">Public holiday</label><input type="number" id="whHoliday" min="1" max="5" step="0.05"></div>',
            '</div>',
            '<p style="margin:0 0 14px;font-size:12px;color:#8a93a0">These are placeholders until you set them - use the rates in your staff agreements or collective agreement.</p>',

            '<div class="fc-fields-row">',
            '<div class="fc-field"><label for="whDivisor">Hours in a month (for the hourly rate)</label><input type="number" id="whDivisor" min="100" max="300" step="0.01"></div>',
            '<div class="fc-field"><label for="whMin">Ignore a day with less than (minutes)</label><input type="number" id="whMin" min="0" max="480" step="5"></div>',
            '<div class="fc-field"><label for="whRound">Round down to</label><select id="whRound">' + rounding + '</select></div>',
            '</div>',
            '<label style="display:flex;align-items:center;gap:8px;margin-top:4px"><input type="checkbox" id="whBefore"> Also count time worked before opening time on a working day</label>',
            '</div></div>',

            // Worked example
            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-calculator"></i> What this pays - check the rates</div>',
            '<div class="fc-card-body">',
            '<div class="fc-field" style="max-width:260px"><label for="whExample">Example monthly basic salary (GH&#8373;)</label><input type="number" id="whExample" value="3000" min="0" step="50"></div>',
            '<div id="whExampleOut" style="font-size:14px;line-height:1.8"></div>',
            '<p style="margin:10px 0 0;font-size:12px;color:#8a93a0">Hourly rate = basic salary &divide; hours in a month. Overtime is added to gross pay, so it is taxed like the rest of pay.</p>',
            '</div></div>',

            '<div class="fc-toast" id="whToast" role="status" aria-live="polite"></div>',
            '</div>'
        ].join('');
    }

    function $(id) { return document.getElementById(id); }

    function toast(message, type) {
        var el = $('whToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 5000);
    }

    function minutesOf(hhmm) {
        if (!hhmm || hhmm.indexOf(':') < 0) return NaN;
        var p = hhmm.split(':');
        return (+p[0]) * 60 + (+p[1]);
    }

    function paidHours() {
        var minutes = minutesOf($('whEnd').value) - minutesOf($('whStart').value) - (parseInt($('whBreak').value, 10) || 0);
        return isNaN(minutes) ? NaN : Math.max(0, minutes) / 60;
    }

    function refreshDerived() {
        var h = paidHours();
        $('whPaidHours').textContent = isNaN(h) ? '—' : (Math.round(h * 100) / 100) + ' hours';

        var basic = parseFloat($('whExample').value) || 0;
        var divisor = parseFloat($('whDivisor').value) || 0;
        var out = $('whExampleOut');
        if (!divisor) {
            out.textContent = 'Enter the hours in a month to see the hourly rate.';
            return;
        }
        var hourly = basic / divisor;
        var rows = [
            ['Normal hour', 1],
            ['After hours', parseFloat($('whAfter').value) || 0],
            ['Weekend / rest day', parseFloat($('whWeekend').value) || 0],
            ['Public holiday', parseFloat($('whHoliday').value) || 0]
        ];
        out.innerHTML = rows.map(function (r) {
            return '<div style="display:flex;justify-content:space-between;max-width:420px"><span>' + esc(r[0]) +
                (r[1] !== 1 ? ' <span style="color:#8a93a0">(&times;' + r[1] + ')</span>' : '') +
                '</span><strong>' + P.money(hourly * r[1]) + ' / hour</strong></div>';
        }).join('');
    }

    function fill(s) {
        document.querySelectorAll('.whDay').forEach(function (box) {
            box.checked = (s.workDays || []).indexOf(parseInt(box.value, 10)) !== -1;
        });
        $('whStart').value = s.startTime || '';
        $('whEnd').value = s.endTime || '';
        $('whBreak').value = s.breakMinutes;
        $('whOvertimeOn').checked = !!s.overtimeEnabled;
        $('whAfter').value = s.multiplierAfterHours;
        $('whWeekend').value = s.multiplierWeekend;
        $('whHoliday').value = s.multiplierHoliday;
        $('whDivisor').value = s.monthlyHoursDivisor;
        $('whMin').value = s.minOvertimeMinutes;
        $('whRound').value = String(s.roundToMinutes);
        $('whBefore').checked = !!s.countBeforeStart;

        var banner = $('whBanner');
        banner.style.display = 'block';
        banner.querySelector('.fc-card-body').innerHTML = s.configured
            ? (s.overtimeEnabled
                ? '<i class="fas fa-circle-check" style="color:#1e8e5a"></i> Overtime is <strong>on</strong>. It will appear for approval after staff clock out.'
                : '<i class="fas fa-circle-pause" style="color:#b7791f"></i> Overtime is <strong>off</strong>. Nothing is being detected or paid.')
            : '<i class="fas fa-circle-info" style="color:#2b6cb0"></i> Nothing has been saved yet. These are suggested starting values - check them, then <strong>Save</strong>. Overtime stays off until you switch it on.';
        refreshDerived();
    }

    function collect() {
        var days = [];
        document.querySelectorAll('.whDay').forEach(function (box) {
            if (box.checked) days.push(parseInt(box.value, 10));
        });
        return {
            workDays: days,
            startTime: $('whStart').value,
            endTime: $('whEnd').value,
            breakMinutes: parseInt($('whBreak').value, 10),
            overtimeEnabled: $('whOvertimeOn').checked,
            multiplierAfterHours: parseFloat($('whAfter').value),
            multiplierWeekend: parseFloat($('whWeekend').value),
            multiplierHoliday: parseFloat($('whHoliday').value),
            monthlyHoursDivisor: parseFloat($('whDivisor').value),
            minOvertimeMinutes: parseInt($('whMin').value, 10),
            roundToMinutes: parseInt($('whRound').value, 10),
            countBeforeStart: $('whBefore').checked
        };
    }

    function save() {
        var body = collect();
        if (!body.workDays.length) return toast('Choose at least one working day.', 'error');
        if (!body.startTime || !body.endTime) return toast('Enter the opening and closing times.', 'error');
        // A blank number box would be sent as NaN (and dropped), silently keeping the old value.
        var numeric = ['breakMinutes', 'multiplierAfterHours', 'multiplierWeekend', 'multiplierHoliday', 'monthlyHoursDivisor', 'minOvertimeMinutes'];
        for (var i = 0; i < numeric.length; i++) {
            if (isNaN(body[numeric[i]])) return toast('Fill in every number before saving.', 'error');
        }

        var btn = $('whSave');
        btn.disabled = true;
        fetchPost('saveWorkHoursSettings', body).then(function (saved) {
            fill(saved);
            toast('Saved.', 'success');
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    function bind() {
        $('whSave').addEventListener('click', save);
        ['whStart', 'whEnd', 'whBreak', 'whAfter', 'whWeekend', 'whHoliday', 'whDivisor', 'whExample'].forEach(function (id) {
            $(id).addEventListener('input', refreshDerived);
        });
    }

    buildDOM();
    bind();
    fetchPost('getWorkHoursSettings', {}).then(fill).catch(function (e) {
        toast(P.errorMessage(e), 'error');
    });
})();
