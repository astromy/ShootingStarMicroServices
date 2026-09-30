/**
 * _payrollShared.js  —  Helpers shared by Salary Setup, Salary Approvals and
 * Payslip. Loaded before each of those pages by common.js.
 *
 * Exposes window.payrollShared:
 *   months                  ['January', ...]
 *   years()                 selectable calendar years (two back, one ahead)
 *   academicYears()         e.g. ['2023/2024', ..., '2027/2028']
 *   academicYearFor(m, y)   school year a month falls in (Sept starts a new year)
 *   period(m, y)            'September 2026' - the stored pay-period format
 *   current()               {month, year, academicYear} for today
 *   money(v)                'GH₵ 1,352.00'
 *   date(d)                 '28 Sept 2026'
 *   errorMessage(e)         the server's message from a failed fetchPost
 *   escape(s)               HTML-escape text for templates
 *   statusBadge(status)     PENDING / APPROVED / PAID badge
 *   runDetailHtml(run)      breakdown of one run, for detail pop-ups
 */
(function () {
    'use strict';

    var MONTHS = ['January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];

    // Month (1-12) in which a new school year starts.
    var ACADEMIC_YEAR_START_MONTH = 9;

    var STATUS = {
        PENDING: {cls: 'sal-pending', label: 'Pending approval'},
        APPROVED: {cls: 'sal-approved', label: 'Approved'},
        PAID: {cls: 'sal-paid', label: 'Paid'},
    };

    function escape(s) {
        return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
            return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c];
        });
    }

    function thisYear() {
        return new Date().getFullYear();
    }

    window.payrollShared = {
        months: MONTHS,

        years: function () {
            var y = thisYear();
            return [y - 2, y - 1, y, y + 1];
        },

        academicYears: function () {
            var y = thisYear(), out = [];
            for (var start = y - 3; start <= y + 1; start++) out.push(start + '/' + (start + 1));
            return out;
        },

        academicYearFor: function (monthIndex, year) {
            var start = monthIndex + 1 >= ACADEMIC_YEAR_START_MONTH ? year : year - 1;
            return start + '/' + (start + 1);
        },

        period: function (monthIndex, year) {
            return MONTHS[monthIndex] + ' ' + year;
        },

        current: function () {
            var d = new Date();
            return {
                month: d.getMonth(),
                year: d.getFullYear(),
                academicYear: this.academicYearFor(d.getMonth(), d.getFullYear()),
            };
        },

        money: function (v) {
            return 'GH\u20B5\u00A0' + (parseFloat(v) || 0).toLocaleString('en-GH', {
                minimumFractionDigits: 2, maximumFractionDigits: 2,
            });
        },

        date: function (d) {
            if (!d) return '—';
            if (Array.isArray(d)) d = new Date(d[0], (d[1] || 1) - 1, d[2] || 1);
            var parsed = new Date(d);
            return isNaN(parsed) ? '—' : parsed.toLocaleDateString('en-GB', {day: '2-digit', month: 'short', year: 'numeric'});
        },

        /** fetchPost throws "HTTP 400: {\"message\":\"...\"}"; pull out the message. */
        errorMessage: function (error) {
            var text = String((error && error.message) || error || '');
            var start = text.indexOf('{');
            if (start >= 0) {
                try {
                    var body = JSON.parse(text.slice(start));
                    if (body && (body.message || body.error)) return body.message || body.error;
                } catch (ignored) {
                    // not JSON
                }
            }
            return 'Something went wrong. Please try again.';
        },

        escape: escape,

        statusBadge: function (status) {
            var s = STATUS[status] || {cls: 'sal-pending', label: status || '—'};
            return '<span class="sal-status ' + s.cls + '">' + escape(s.label) + '</span>';
        },

        /** Full breakdown of one run, for the detail pop-ups. */
        runDetailHtml: function (r) {
            var P = window.payrollShared, money = P.money;
            var gross = r.grossPay != null ? r.grossPay : (r.basicSalary || 0) + (r.totalAllowances || 0);
            var line = function (label, value, cls) {
                return '<div class="sal-detail-row' + (cls ? ' ' + cls : '') + '"><span>' + escape(label) + '</span><strong>' + value + '</strong></div>';
            };
            var items = (r.salaryItems || []).map(function (i) {
                return '<div class="sal-detail-item ' + (i.itemType === 'DEDUCTION' ? 'sal-deduct' : 'sal-allow') + '">' +
                    '<span>' + escape(i.itemName) + '</span><strong>' + (i.itemType === 'DEDUCTION' ? '−' : '+') + money(i.amount) + '</strong></div>';
            }).join('');
            var paidTo = r.paymentMethod === 'BANK' ? [r.bankName, r.bankBranch, r.accountNumber].filter(Boolean).join(' · ')
                : r.paymentMethod === 'MOMO' ? 'Mobile money ' + (r.momoNumber || '') : 'Cash';
            return [
                line('Status', P.statusBadge(r.status)),
                line('Basic salary', money(r.basicSalary)),
                items,
                line('Gross pay', money(gross)),
                r.taxableIncome != null ? line('Taxable income', money(r.taxableIncome)) : '',
                line('Total deductions', money(r.totalDeductions)),
                line('Net pay', money(r.netSalary), 'sal-detail-net'),
                r.employerSsnit != null ? line('Employer SSNIT (paid by the school)', money(r.employerSsnit)) : '',
                line('Paid to', escape(paidTo || '—')),
                line('Created by', escape(r.createdBy || '—')),
                line('Approved by', escape(r.approvedBy ? r.approvedBy + ' · ' + P.date(r.approvedAt) : '—')),
                line('Paid by', escape(r.status === 'PAID' ? (r.processedBy || '—') + ' · ' + P.date(r.paymentDate) : '—')),
                r.externalReference ? line('Payment reference', escape(r.externalReference)) : '',
            ].join('');
        },
    };
})();
