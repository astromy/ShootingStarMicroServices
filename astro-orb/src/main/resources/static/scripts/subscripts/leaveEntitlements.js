/**
 * leaveEntitlements.js  —  Setup -> Leave Entitlements.
 *
 * How many days of each kind of leave a staff member gets in a year, and whether the
 * leave counts every day or only working days. Balances, and the check when someone
 * applies, both use these figures. Access is controlled by the "Leave Entitlements"
 * permission. _payrollShared.js is loaded before this file by common.js.
 */
(function () {
    'use strict';

    var P = window.payrollShared;
    var esc = P.escape;
    var toastTimer;

    function $(id) { return document.getElementById(id); }

    function buildDOM() {
        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-calendar-minus"></i> Leave Entitlements</h1>',
            '<p>How much leave each staff member gets in a year.</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-primary" id="leSave"><i class="fas fa-check"></i> Save</button>',
            '</div>',
            '</header>',

            '<div class="fc-card" id="leBanner" style="margin-bottom:16px;display:none"><div class="fc-card-body" style="font-size:13px"></div></div>',

            '<div class="ph-table-wrap"><table class="fc-table" id="leTable">',
            '<thead><tr><th>Leave</th><th style="width:150px">Days a year</th><th>What counts as a leave day</th></tr></thead>',
            '<tbody></tbody></table></div>',

            '<div class="fc-card" style="margin-top:16px"><div class="fc-card-body" style="font-size:13px;line-height:1.6">',
            '<ul style="margin:0 0 0 18px">',
            '<li><strong>0 days</strong> means your school doesn\'t offer that leave: staff can\'t apply for it.</li>',
            '<li>Public holidays are set under <em>Setup &rarr; Holidays</em> and the working week under <em>Setup &rarr; Working Hours</em>.</li>',
            '<li>New figures apply to balances and new requests straight away. Leave already approved keeps the days it was approved for, ',
            'and lowering a figure below what someone has already used just leaves them with no balance.</li>',
            '</ul></div></div>',

            '<div class="fc-toast" id="leToast" role="status" aria-live="polite"></div>',
            '</div>'
        ].join('');
    }

    function toast(message, type) {
        var el = $('leToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 5000);
    }

    function hint(calendarDays) {
        return calendarDays
            ? 'Every day in the period counts, weekends and public holidays included (one continuous period).'
            : 'Only working days count - weekends and public holidays don\'t use up leave.';
    }

    function render(rows) {
        var anyConfigured = rows.some(function (r) { return r.configured; });
        var banner = $('leBanner');
        banner.style.display = 'block';
        banner.querySelector('.fc-card-body').innerHTML = anyConfigured
            ? '<i class="fas fa-circle-check" style="color:#1e8e5a"></i> These are your school\'s figures.'
            : '<i class="fas fa-circle-info" style="color:#2b6cb0"></i> Nothing has been saved yet. These are suggested starting values - change them to match your staff agreements, then <strong>Save</strong>.';

        document.querySelector('#leTable tbody').innerHTML = rows.map(function (r) {
            return '<tr data-type="' + esc(r.leaveType) + '">' +
                '<td><strong>' + esc(r.label) + '</strong>' +
                (r.configured ? '' : ' <span style="font-size:11px;background:#fff4d6;color:#8a6100;border-radius:10px;padding:2px 8px">suggested</span>') + '</td>' +
                '<td><input type="number" class="leDays" min="0" max="366" step="1" value="' + r.days + '" style="width:100px" aria-label="Days a year for ' + esc(r.label) + '"></td>' +
                '<td><label style="display:flex;align-items:center;gap:8px;margin:0;font-weight:500">' +
                '<input type="checkbox" class="leCal"' + (r.calendarDays ? ' checked' : '') + '> Count weekends and public holidays</label>' +
                '<div class="leHint" style="font-size:12px;color:#8a93a0;margin-top:3px">' + esc(hint(r.calendarDays)) + '</div></td>' +
                '</tr>';
        }).join('');
    }

    function collect() {
        return [].map.call(document.querySelectorAll('#leTable tbody tr'), function (tr) {
            return {
                leaveType: tr.getAttribute('data-type'),
                days: tr.querySelector('.leDays').value === '' ? null : parseInt(tr.querySelector('.leDays').value, 10),
                calendarDays: tr.querySelector('.leCal').checked
            };
        });
    }

    function save() {
        var items = collect();
        for (var i = 0; i < items.length; i++) {
            if (items[i].days === null || isNaN(items[i].days) || items[i].days < 0 || items[i].days > 366) {
                return toast('Enter a number of days from 0 to 366 for every leave type.', 'error');
            }
        }
        var btn = $('leSave');
        btn.disabled = true;
        fetchPost('saveLeaveEntitlements', {items: items}).then(function (rows) {
            render(rows);
            toast('Saved.', 'success');
        }).catch(function (e) {
            toast(P.errorMessage(e), 'error');
        }).then(function () {
            btn.disabled = false;
        });
    }

    buildDOM();
    $('leSave').addEventListener('click', save);
    $('leTable').addEventListener('change', function (e) {
        if (e.target.classList.contains('leCal')) {
            e.target.closest('tr').querySelector('.leHint').textContent = hint(e.target.checked);
        }
    });
    fetchPost('getLeaveEntitlements', {}).then(render).catch(function (e) {
        toast(P.errorMessage(e), 'error');
        document.querySelector('#leTable tbody').innerHTML =
            '<tr><td colspan="3" class="fc-empty"><i class="fas fa-triangle-exclamation"></i> The entitlements couldn\'t be loaded. ' + esc(P.errorMessage(e)) + '</td></tr>';
        $('leSave').disabled = true;   // saving blank figures over the real ones would be worse than not saving
    });
})();
