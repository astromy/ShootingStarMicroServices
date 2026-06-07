/**
 * _financePayslip.js  —  Data & logic layer for Payslip generation.
 *
 * Pattern mirrors _financeBilling.js exactly:
 *   - IIFE, state on window, fetchPost for every API call
 *   - window.copyrights() called at bottom
 *   - No DOM manipulation
 *
 * Exposes:
 *   window.payslipState
 *   window.payslipLoad()
 *   window.payslipFetch(staffId, payPeriod, academicYear) → SalaryResponse|null
 *   window.payslipFetchAll(staffId) → SalaryResponse[]
 *   window.payslipPrint(slip)
 *   window.payslipFmt
 */
(function () {
    'use strict';

    var instId = window.instId || '';
    var _raw = instId.split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '').trim();

    window.payslipState = {
        institutionCode: _inst,
        staff: [],
        academicYears: [],
        payPeriods: [],
        currentSlip: null,
        allSlips: [],
        institutionName: '',
        _loaded: false,
    };

    function buildYears() {
        var y = new Date().getFullYear(), out = [];
        for (var i = 3; i >= 0; i--) out.push((y - i) + '/' + (y - i + 1));
        window.payslipState.academicYears = out;
    }

    function buildPeriods() {
        var months = ['January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'];
        var y = new Date().getFullYear();
        window.payslipState.payPeriods = months.map(function (m) {
            return {value: m + ' ' + y, label: m + ' ' + y};
        });
    }

    function showSplash() {
        try {
            $('.splash').css({display: 'block', background: '#ffffff3d'});
        } catch (e) {
        }
    }

    function hideSplash() {
        try {
            $('.splash').css('display', 'none');
        } catch (e) {
        }
    }

    window.payslipLoad = async function () {
        buildYears();
        buildPeriods();
        showSplash();

        try {
            var inst = await fetchPost('getInstitutionByCode', {val: _inst});
            if (inst) window.payslipState.institutionName = inst.name || _inst;
        } catch (e) {
            console.warn('[_financePayslip] institution load failed:', e.message);
        }

        try {
            var staffResult = await fetchPost('getAllStaffByInstitution', {institutionCode: _inst});
            if (Array.isArray(staffResult)) {
                window.payslipState.staff = staffResult.map(function (s) {
                    var names = [s.lastName, s.firstName, s.otherName].filter(Boolean);
                    var desig = (s.designationList && s.designationList.length)
                        ? (s.designationList[0].name || '') : '';
                    return {
                        id: s.staffId || s.id || '',
                        name: names.join(', ').trim() || s.staffId || '',
                        designation: desig
                    };
                });
            }
        } catch (e) {
            console.warn('[_financePayslip] staff load failed (non-fatal):', e.message);
        }

        window.payslipState._loaded = true;
        hideSplash();
    };

    window.payslipFetch = async function (staffId, payPeriod, academicYear) {
        if (!staffId || !payPeriod) return null;
        showSplash();
        try {
            var result = await fetchPost('salary/payslip', {
                institutionCode: _inst,
                staffId: staffId,
                payPeriod: payPeriod,
                academicYear: academicYear || '',
            });
            window.payslipState.currentSlip = result || null;
            hideSplash();
            return result || null;
        } catch (e) {
            hideSplash();
            console.error('[_financePayslip] fetch error:', e.message);
            return null;
        }
    };

    window.payslipFetchAll = async function (staffId) {
        if (!staffId) return [];
        showSplash();
        try {
            var result = await fetchPost('salary/get-by-institution', {
                institutionCode: _inst,
                staffId: staffId,
                status: null,
            });
            var slips = Array.isArray(result) ? result : [];
            // Sort newest first
            slips.sort(function (a, b) {
                return new Date(b.createdAt || 0) - new Date(a.createdAt || 0);
            });
            window.payslipState.allSlips = slips;
            hideSplash();
            return slips;
        } catch (e) {
            hideSplash();
            return [];
        }
    };

    window.payslipPrint = function (slip) {
        if (!slip) return;
        var fmt = window.payslipFmt;
        var inst = window.payslipState.institutionName || _inst;

        var allowances = (slip.salaryItems || []).filter(function (i) {
            return i.itemType === 'ALLOWANCE';
        });
        var deductions = (slip.salaryItems || []).filter(function (i) {
            return i.itemType === 'DEDUCTION';
        });

        function itemRow(i, sign, color) {
            var val = i.isPercentage ? (i.percentageRate || 0) + '% of basic' : fmt.money(i.amount);
            return '<tr><td style="padding:5px 0">' + i.itemName + '</td>' +
                '<td style="text-align:right;color:' + color + ';padding:5px 0">' + sign + val + '</td></tr>';
        }

        var css = [
            'body{font-family:Arial,sans-serif;padding:32px;max-width:560px;margin:0 auto;color:#1A1A2E;font-size:13px}',
            '.hdr{background:#0F2340;color:#fff;padding:18px 22px;border-radius:8px;display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}',
            '.hdr h2,.hdr p{margin:0} .hdr p{opacity:.7;font-size:11px}',
            '.badge{background:rgba(255,255,255,.15);border-radius:6px;padding:8px 14px;text-align:center}',
            '.badge .lbl{font-size:10px;opacity:.7;letter-spacing:1px;text-transform:uppercase;display:block}',
            '.badge .val{font-size:13px;font-weight:700;margin-top:2px;display:block}',
            '.sec{margin-bottom:16px}',
            '.sec-title{font-size:10px;font-weight:700;text-transform:uppercase;letter-spacing:1.5px;color:#2A5282;border-bottom:1px solid #E2E8F0;padding-bottom:5px;margin-bottom:10px}',
            '.row{display:flex;justify-content:space-between;padding:4px 0;border-bottom:1px solid #F0F4F8;font-size:13px}',
            'table{width:100%;border-collapse:collapse}',
            '.net{background:#0F2340;color:#fff;padding:14px 18px;border-radius:8px;display:flex;justify-content:space-between;align-items:center;margin-top:16px}',
            '.net .lbl{font-size:12px;opacity:.7} .net .val{font-size:22px;font-weight:700}',
            '.footer{text-align:center;font-size:11px;color:#aaa;margin-top:20px;padding-top:12px;border-top:1px solid #eee}',
            '@media print{body{padding:0}}',
        ].join('');

        var body = [
            '<div class="hdr"><div><h2>' + inst + '</h2><p>Official Payslip</p></div>',
            '<div class="badge"><span class="lbl">Status</span><span class="val">' + (slip.status || 'PAID') + '</span></div></div>',

            '<div class="sec"><div class="sec-title">Employee Details</div>',
            '<div class="row"><span>Staff ID</span><strong>' + slip.staffId + '</strong></div>',
            '<div class="row"><span>Name</span><strong>' + (slip.staffName || '—') + '</strong></div>',
            '<div class="row"><span>Designation</span><strong>' + (slip.designation || '—') + '</strong></div>',
            '</div>',

            '<div class="sec"><div class="sec-title">Pay Period</div>',
            '<div class="row"><span>Period</span><strong>' + (slip.payPeriod || '—') + '</strong></div>',
            '<div class="row"><span>Academic Year</span><strong>' + (slip.academicYear || '—') + '</strong></div>',
            (slip.paymentDate ? '<div class="row"><span>Payment Date</span><strong>' + fmt.date(slip.paymentDate) + '</strong></div>' : ''),
            '</div>',

            '<div class="sec"><div class="sec-title">Earnings</div><table>',
            '<tr><td style="padding:5px 0">Basic Salary</td><td style="text-align:right;padding:5px 0"><strong>' + fmt.money(slip.basicSalary) + '</strong></td></tr>',
            allowances.map(function (i) {
                return itemRow(i, '+', '#276749');
            }).join(''),
            '</table></div>',

            (deductions.length
                ? '<div class="sec"><div class="sec-title">Deductions</div><table>' +
                deductions.map(function (i) {
                    return itemRow(i, '-', '#9B2335');
                }).join('') +
                '</table></div>'
                : ''),

            '<div class="sec"><div class="sec-title">Summary</div>',
            '<div class="row"><span>Gross Pay</span><strong>' + fmt.money((slip.basicSalary || 0) + (slip.totalAllowances || 0)) + '</strong></div>',
            '<div class="row"><span>Total Deductions</span><strong style="color:#9B2335">&#8722;' + fmt.money(slip.totalDeductions) + '</strong></div>',
            '</div>',

            '<div class="net"><div><div class="lbl">NET PAY</div></div><div class="val">' + fmt.money(slip.netSalary) + '</div></div>',

            (slip.processedBy ? '<p style="margin-top:12px;font-size:11px;color:#888">Processed by: ' + slip.processedBy + '</p>' : ''),

            '<div class="footer">This is a computer-generated payslip &mdash; Shooting Star &middot; Astromy LLC</div>',
        ].join('');

        var win = window.open('', '_blank', 'width=680,height=860');
        win.document.write('<!DOCTYPE html><html><head><meta charset="utf-8"><title>Payslip</title><style>' + css + '</style></head><body>' + body + '</body></html>');
        win.document.close();
        setTimeout(function () {
            win.print();
        }, 400);
    };

    window.payslipFmt = {
        money: function (v) {
            return 'GH\u20B5\u00A0' + (parseFloat(v) || 0).toLocaleString('en-GH', {
                minimumFractionDigits: 2, maximumFractionDigits: 2,
            });
        },
        date: function (d) {
            if (!d) return '—';
            return new Date(d).toLocaleDateString('en-GB', {
                day: '2-digit', month: 'short', year: 'numeric',
            });
        },
    };

    if (window.copyrights) window.copyrights();
})();