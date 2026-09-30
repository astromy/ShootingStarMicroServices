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
 *
 * Needs _payrollShared.js (loaded first by common.js).
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

    // Pay periods from two years back to next year (newest first), via
    // _payrollShared.js - same 'September 2026' format the runs are stored in.
    function buildYears() {
        window.payslipState.academicYears = window.payrollShared.academicYears().slice().reverse();
    }

    function buildPeriods() {
        var P = window.payrollShared, out = [];
        P.years().slice().reverse().forEach(function (y) {
            for (var m = 11; m >= 0; m--) out.push({value: P.period(m, y), label: P.period(m, y)});
        });
        window.payslipState.payPeriods = out;
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

    // _payrollShared.js is normally loaded first by common.js; load it here if not.
    var payslipScript = document.currentScript;

    function ensureShared() {
        if (window.payrollShared) return Promise.resolve();
        var base = payslipScript && payslipScript.src
            ? payslipScript.src.substring(0, payslipScript.src.lastIndexOf('/') + 1)
            : 'scripts/';
        return new Promise(function (resolve) {
            var s = document.createElement('script');
            s.src = base + '_payrollShared.js';
            s.setAttribute('data-dynamic', 'true');
            s.onload = resolve;
            s.onerror = function () {
                console.error('[_financePayslip] Could not load _payrollShared.js');
                resolve();
            };
            document.body.appendChild(s);
        });
    }

    /**
     * The page draws its dropdowns as soon as this file loads. If the period
     * lists were filled after that (shared helper loaded late), put them in
     * now, defaulting to the current month and academic year.
     */
    function fillPeriodSelects() {
        var s = window.payslipState, P = window.payrollShared;
        var period = document.getElementById('psPeriod');
        var year = document.getElementById('psYear');
        if (period && !period.options.length) {
            period.innerHTML = s.payPeriods.map(function (p) {
                return '<option value="' + p.value + '">' + p.label + '</option>';
            }).join('');
        }
        if (year && !year.options.length) {
            year.innerHTML = s.academicYears.map(function (y) {
                return '<option>' + y + '</option>';
            }).join('');
        }
        if (P && !s._periodDefaulted) {
            var now = P.current();
            if (period) period.value = P.period(now.month, now.year);
            if (year) year.value = now.academicYear;
            if (period && year) s._periodDefaulted = true;
        }
    }

    window.payslipLoad = async function () {
        await ensureShared();
        buildYears();
        buildPeriods();
        fillPeriodSelects();
        showSplash();

        try {
            var inst = await fetchPost('getInstitutionByCode', {val: _inst});
            if (inst) {
                window.payslipState.institutionName = inst.name || _inst;
                window.payslipState.institution = inst;
            }
        } catch (e) {
            console.warn('[_financePayslip] institution load failed:', e.message);
        }

        try {
            // Staff come from HR, through astro-orb's PayrollController.
            var staffResult = await fetchPost('payroll/staff', {});
            if (Array.isArray(staffResult)) {
                window.payslipState.staff = staffResult.map(function (s) {
                    return {
                        id: s.staffId,
                        name: s.staffName || s.staffId,
                        designation: s.designation || '',
                        // Used on the printed payslip.
                        level: s.level || '',
                        snnitNumber: s.snnitNumber || '',
                        nationalID: s.nationalID || '',
                        nationalIDType: s.nationalIDType || '',
                        dateOfEmployment: s.dateOfEmployment || null,
                    };
                });
            }
        } catch (e) {
            console.warn('[_financePayslip] staff load failed (non-fatal):', e.message);
        }

        window.payslipState._loaded = true;
        fillPeriodSelects();
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

    // ── PRINT ────────────────────────────────────────────────────────────────
    // Reproduces the school's Excel "STAFF PAYSLIP" layout: phones, crest and
    // title at the top; the employee block; the salary item table with
    // earnings, deductions and totals columns; the summary line; and the
    // payment details box (from the staff member's salary profile at the time
    // of the run). Details the system doesn't hold (division, overtime hours)
    // print blank rather than "#N/A".

    function esc(v) {
        return String(v == null ? '' : v).replace(/[&<>"']/g, function (c) {
            return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c];
        });
    }

    /** Stored images: data URLs, raw base64, or "file.png_image/png_<base64>". */
    function imageSrc(value) {
        if (!value) return '';
        value = String(value).trim();
        if (/^(data:|https?:\/\/)/.test(value)) return value;
        var prefixed = value.match(/_(image\/[a-z0-9.+-]+)_/i);
        if (prefixed) return 'data:' + prefixed[1] + ';base64,' + value.slice(prefixed.index + prefixed[0].length);
        return 'data:' + (value.indexOf('iVBOR') === 0 ? 'image/png' : 'image/jpeg') + ';base64,' + value;
    }

    /** "GHS" on the left of the cell, the amount on the right - as in the template. */
    function ghs(v) {
        var n = parseFloat(v) || 0;
        return '<span class="cur">GHS</span><span class="amt">' +
            n.toLocaleString('en-GH', {minimumFractionDigits: 2, maximumFractionDigits: 2}) + '</span>';
    }

    /** Percentage items are stored as a rate; print their actual cedi amount. */
    function itemAmount(item, basic) {
        if (item.isPercentage && !(parseFloat(item.amount) > 0)) {
            return (parseFloat(basic) || 0) * (parseFloat(item.percentageRate) || 0) / 100;
        }
        return parseFloat(item.amount) || 0;
    }

    function staffFor(slip) {
        return (window.payslipState.staff || []).find(function (s) {
            return s.id === slip.staffId;
        }) || {};
    }

    /** In Ghana the Ghana Card PIN is the Tax Identification Number. */
    function taxId(staff) {
        if (!staff.nationalID) return '';
        var type = String(staff.nationalIDType || '').toLowerCase();
        return !type || type.indexOf('ghana') >= 0 ? staff.nationalID : '';
    }

    /**
     * The payslip in the school's template layout, as a complete HTML
     * document. Used for both the on-page preview (in an iframe) and printing,
     * so what's previewed is exactly what prints.
     */
    window.payslipDocument = function (slip) {
        var fmt = window.payslipFmt;
        var inst = window.payslipState.institution || {};
        var instName = window.payslipState.institutionName || inst.name || _inst;
        var staff = staffFor(slip);
        var basic = parseFloat(slip.basicSalary) || 0;

        var earnings = [{name: 'Basic', amount: basic}].concat(
            (slip.salaryItems || []).filter(function (i) {
                return i.itemType === 'ALLOWANCE';
            })
                .map(function (i) {
                    return {name: i.itemName, amount: itemAmount(i, basic)};
                })
        );
        var deductions = (slip.salaryItems || []).filter(function (i) {
            return i.itemType === 'DEDUCTION';
        })
            .map(function (i) {
                return {name: i.itemName, amount: itemAmount(i, basic)};
            });
        // The school's own SSNIT contribution: shown like the Excel sheet, but
        // not part of the staff member's deductions.
        var employerSsnit = parseFloat(slip.employerSsnit) > 0 ? parseFloat(slip.employerSsnit) : null;

        var gross = earnings.reduce(function (t, e) {
            return t + e.amount;
        }, 0);
        var totalDeductions = slip.totalDeductions != null
            ? parseFloat(slip.totalDeductions) || 0
            : deductions.reduce(function (t, d) {
                return t + d.amount;
            }, 0);
        var net = slip.netSalary != null ? parseFloat(slip.netSalary) || 0 : gross - totalDeductions;

        // Item rows: earnings, then deductions, then the deductions total -
        // the gross total sits on the last earnings row, as in the template.
        var rows = earnings.map(function (e, i) {
            return {name: e.name, earn: e.amount, total: i === earnings.length - 1 ? gross : null};
        }).concat(deductions.map(function (d) {
            return {name: d.name, deduct: d.amount};
        }));
        if (employerSsnit != null) rows.push({name: 'Employer SSNIT (paid by the school)', deduct: employerSsnit});
        if (deductions.length) rows.push({total: totalDeductions});
        while (rows.length < 12) rows.push({});   // keep the table the template's height

        var itemRows = rows.map(function (r) {
            return '<tr class="item">' +
                '<td class="c-item">' + esc(r.name || '') + '</td>' +
                '<td class="c-hours"></td>' +
                '<td class="c-earn">' + (r.earn != null ? ghs(r.earn) : '') + '</td>' +
                '<td class="c-deduct">' + (r.deduct != null ? ghs(r.deduct) : '') + '</td>' +
                '<td class="c-total">' + (r.total != null ? ghs(r.total) : '') + '</td>' +
                '</tr>';
        }).join('');

        var phones = [inst.contact1, inst.contact2].filter(Boolean);
        var crest = imageSrc(inst.crest);

        function field(label, value) {
            return '<td class="lbl">' + label + '</td><td class="val">' + esc(value || '') + '</td>';
        }

        var css = [
            '@page{size:A4 portrait;margin:14mm}',
            '*{box-sizing:border-box}',
            'body{margin:0;color:#111;font-family:Cambria,"Times New Roman",Georgia,serif;font-size:11.5px}',
            '.slip{max-width:190mm;margin:0 auto}',
            '.top{display:grid;grid-template-columns:1fr auto 1fr;align-items:center;gap:12px;margin-bottom:6px}',
            '.tel{font-size:13px;line-height:1.3}',
            '.tel span{display:block;padding-left:34px}',
            '.tel span:first-child{padding-left:0}',
            '.crest{height:64px;max-width:120px;object-fit:contain;display:block}',
            '.doc{text-align:right;font-weight:700;font-size:14px}',
            'h1{margin:6px 0 14px;text-align:center;font-family:Rockwell,"Rockwell Extra Bold",Georgia,serif;font-size:22px;font-weight:800;text-transform:uppercase;letter-spacing:.3px}',
            '.box{border:2px solid #000;margin-bottom:14px}',
            'table{width:100%;border-collapse:collapse}',
            '.emp td{padding:7px 10px;text-transform:uppercase}',
            '.emp .lbl{width:28%}',
            '.emp .val{width:22%;font-weight:700}',
            '.box-title{text-align:center;font-family:Rockwell,Georgia,serif;font-weight:800;font-size:15px;padding:4px 0;border-bottom:2px solid #000;text-transform:uppercase}',
            '.items th{font-weight:400;padding:4px 6px;text-transform:uppercase}',
            '.items thead tr:last-child th{border-bottom:2px solid #000}',
            '.items .c-item{width:30%;padding:7px 10px}',
            '.items .c-hours{width:8%}',
            '.items .c-earn,.items .c-deduct,.items .c-total{width:20.6%;padding:7px 8px}',
            '.items .c-hours,.items .c-earn{border-left:2px solid #000}',
            '.items .c-hours{border-left:2px solid #000}',
            '.items .c-earn{border-left:1px solid #bbb}',
            '.items .c-deduct,.items .c-total{border-left:2px solid #000}',
            '.items tr.item td{height:30px}',
            '.cur{float:left}.amt{float:right}',
            '.sum{border-top:2px solid #000}',
            '.sum{table-layout:fixed}',
            '.sum td{padding:6px 8px;text-transform:uppercase;white-space:nowrap}',
            '.sum .strong{font-weight:700}',
            '.sum .money{border-bottom:3px double #000;white-space:nowrap}',
            '.pay td{padding:5px 10px;text-transform:uppercase}',
            '.pay .line{border-bottom:1px solid #999;width:70%}',
            '.printed{margin-top:22px;font-family:Arial,sans-serif;font-size:11px;color:#333}',
            '@media print{.slip{max-width:none}}',
            '@media screen{body{padding:18px;background:#fff}}',
        ].join('');

        var body = [
            '<div class="slip">',

            '<div class="top">',
            '<div class="tel">' + (phones.length
                ? phones.map(function (p, i) {
                    return '<span>' + (i === 0 ? 'TEL: ' : '') + esc(p) + '</span>';
                }).join('')
                : '') + '</div>',
            '<div>' + (crest ? '<img class="crest" src="' + crest + '" alt="">' : '') + '</div>',
            '<div class="doc">STAFF PAYSLIP</div>',
            '</div>',

            '<h1>' + esc(instName) + '</h1>',

            // Employee details
            '<div class="box"><table class="emp">',
            '<tr>' + field('Employee Code', slip.staffId) + field('Payment Date', slip.paymentDate ? fmt.date(slip.paymentDate) : '') + '</tr>',
            '<tr>' + field('Employee Name', slip.staffName || staff.name) + field('Payment Period', slip.payPeriod) + '</tr>',
            '<tr>' + field('Pay Grade', staff.level) + field('Division', '') + '</tr>',
            '<tr>' + field('SNNIT Number', staff.snnitNumber) + field('Department', slip.designation || staff.designation) + '</tr>',
            '<tr>' + field('Tax Identification', taxId(staff)) + '<td></td><td></td></tr>',
            '<tr>' + field('Employment Date', staff.dateOfEmployment ? fmt.date(staff.dateOfEmployment) : '') + '<td></td><td></td></tr>',
            '</table></div>',

            // Salary items
            '<div class="box">',
            '<div class="box-title">Salary Item Details</div>',
            '<table class="items">',
            '<thead>',
            '<tr><th rowspan="2" class="c-item" style="border-bottom:2px solid #000">Items</th>',
            '<th colspan="2" class="c-hours">Earnings</th>',
            '<th class="c-deduct">Deductions</th>',
            '<th rowspan="2" class="c-total" style="border-bottom:2px solid #000;vertical-align:top">Totals</th></tr>',
            '<tr><th class="c-hours">Hours</th><th class="c-earn">Amount</th><th class="c-deduct">Amount</th></tr>',
            '</thead>',
            '<tbody>' + itemRows + '</tbody>',
            '</table>',
            '<table class="sum">',
            '<colgroup><col style="width:14%"><col style="width:18%"><col style="width:21%">',
            '<col style="width:16%"><col style="width:11%"><col style="width:20%"></colgroup>',
            '<tr><td colspan="2">Taxable Income</td><td class="strong">' +
            (slip.taxableIncome != null ? ghs(slip.taxableIncome) : '') + '</td><td colspan="3"></td></tr>',
            '<tr>',
            '<td class="strong">Gross Pay</td><td class="money">' + ghs(gross) + '</td>',
            '<td class="strong" style="text-align:center">Total Deductions</td><td class="money">' + ghs(totalDeductions) + '</td>',
            '<td class="strong">Net Pay</td><td class="money">' + ghs(net) + '</td>',
            '</tr>',
            '</table>',
            '</div>',

            // Payment details
            '<div class="box">',
            '<div class="box-title">Payment Details</div>',
            '<table class="pay">',
            (slip.paymentMethod === 'MOMO'
                ? '<tr><td style="width:18%">Mobile Money</td><td class="line">' + (esc(slip.momoNumber) || '&nbsp;') + '</td></tr>'
                : slip.paymentMethod === 'CASH'
                    ? '<tr><td style="width:18%">Payment</td><td class="line">Cash</td></tr>'
                    : '<tr><td style="width:18%">Bank</td><td class="line">' + (esc(slip.bankName) || '&nbsp;') + '</td></tr>' +
                    '<tr><td>Branch</td><td class="line">' + (esc(slip.bankBranch) || '&nbsp;') + '</td></tr>' +
                    '<tr><td>A/C Number</td><td class="line">' + (esc(slip.accountNumber) || '&nbsp;') + '</td></tr>'),
            (slip.externalReference ? '<tr><td>Reference</td><td class="line">' + esc(slip.externalReference) + '</td></tr>' : ''),
            '</table>',
            '</div>',

            '<div class="printed">' + esc(new Date().toLocaleString('en-GB', {
                day: '2-digit', month: '2-digit', year: 'numeric', hour: 'numeric', minute: '2-digit',
            })) + '</div>',

            '</div>',
        ].join('');

        return '<!DOCTYPE html><html><head><meta charset="utf-8"><title>Payslip - ' +
            esc(slip.staffName || slip.staffId) + ' - ' + esc(slip.payPeriod || '') +
            '</title><style>' + css + '</style></head><body>' + body + '</body></html>';
    };

    window.payslipPrint = function (slip) {
        if (!slip) return;
        var win = window.open('', '_blank', 'width=820,height=1000');
        if (!win) {
            alert('Allow pop-ups for this site to print payslips.');
            return;
        }
        win.document.write(window.payslipDocument(slip));
        win.document.close();
        // Print once the crest has loaded (or after a short wait, whichever comes first).
        var printed = false;

        function doPrint() {
            if (printed) return;
            printed = true;
            win.focus();
            win.print();
        }

        win.onload = doPrint;
        setTimeout(doPrint, 1200);
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

    // Start loading straight away. The period lists are filled synchronously
    // when the shared helper is already present, so the page's dropdowns have
    // them the moment it draws; staff and school details follow.
    if (window.payrollShared) {
        buildYears();
        buildPeriods();
    }
    window.payslipLoad();

    if (window.copyrights) window.copyrights();
})();