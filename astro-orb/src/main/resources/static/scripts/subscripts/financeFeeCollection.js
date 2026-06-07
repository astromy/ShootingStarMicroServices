/**
 * financeFeeCollection.js  —  UI renderer for Fee Collection.
 *
 * Loaded by the Orb host page. Injects CSS, loads _financeFeeCollection.js,
 * then renders the payment entry interface once data is ready.
 *
 * Features:
 *   - Class group → class → student cascade
 *   - Live student bill summary (amountDue / amountPaid / balance / oldBalance)
 *   - Cash and online (Paystack) payment modes
 *   - Payment history table per student
 *   - Receipt generation on successful payment
 */
(function () {
    'use strict';

    var _base = (function () {
        var el = document.currentScript || (function () {
            var tags = document.getElementsByTagName('script');
            for (var i = tags.length - 1; i >= 0; i--)
                if (tags[i].src && tags[i].src.indexOf('financeFeeCollection') !== -1) return tags[i];
        })();
        return el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';
    })();

    // ── CSS ──────────────────────────────────────────────────────────────────
    (function () {
        if (document.getElementById('feeCollCSS')) return;
        var l = document.createElement('link');
        l.id = 'feeCollCSS';
        l.rel = 'stylesheet';
        l.href = _base + '../../styles/style.css';
        document.head.appendChild(l);
    })();

    // ── DOM ──────────────────────────────────────────────────────────────────
    function buildDOM() {
        var s = window.feeCollState;

        var yearOpts = s.academicYears.map(function (y) {
            return '<option ' + (y === s.selectedYear ? 'selected' : '') + '>' + y + '</option>';
        }).join('');

        var termOpts = window.feeCollData.terms.map(function (t) {
            return '<option value="' + t.value + '" ' + (t.value === s.selectedTerm ? 'selected' : '') + '>' + t.label + '</option>';
        }).join('');

        document.getElementById('wrapper').innerHTML = [
            '<div class="fc-page">',

            // HEADER
            '<header class="fc-header">',
            '<div>',
            '<h1><i class="fas fa-hand-holding-usd"></i> Fee Collection</h1>',
            '<p>Record student fee payments — cash, mobile money or online</p>',
            '</div>',
            '</header>',

            // BODY — two-column
            '<div class="fc-body">',

            // LEFT: student selector
            '<div class="fc-col fc-col-left">',
            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-search"></i> Find Student</div>',
            '<div class="fc-card-body">',

            '<div class="fc-field">',
            '<label>Class Group</label>',
            '<select id="fcGroupSel" disabled><option value="">Loading…</option></select>',
            '</div>',

            '<div class="fc-field">',
            '<label>Class</label>',
            '<select id="fcClassSel" disabled><option value="">Select group first</option></select>',
            '</div>',

            '<div class="fc-field">',
            '<label>Student</label>',
            '<select id="fcStudentSel" disabled><option value="">Select class first</option></select>',
            '</div>',

            '<button class="fc-btn fc-btn-primary" id="fcLoadBillBtn" disabled>',
            '<i class="fas fa-sync-alt"></i> Load Bill',
            '</button>',
            '</div></div>',

            // Bill summary card
            '<div class="fc-card fc-bill-card" id="fcBillCard" style="display:none">',
            '<div class="fc-card-head"><i class="fas fa-file-invoice-dollar"></i> Student Account</div>',
            '<div class="fc-card-body">',
            '<div class="fc-student-name" id="fcStudentName"></div>',
            '<div class="fc-bill-grid">',
            '<div class="fc-bill-item"><div class="fc-bill-label">Total Billed</div><div class="fc-bill-val" id="fcAmtDue">—</div></div>',
            '<div class="fc-bill-item"><div class="fc-bill-label">Total Paid</div><div class="fc-bill-val fc-paid" id="fcAmtPaid">—</div></div>',
            '<div class="fc-bill-item"><div class="fc-bill-label">Balance</div><div class="fc-bill-val fc-balance" id="fcBalance">—</div></div>',
            '<div class="fc-bill-item"><div class="fc-bill-label">Previous Balance</div><div class="fc-bill-val fc-old" id="fcOldBal">—</div></div>',
            '</div>',
            '</div></div>',
            '</div>',  // end left col

            // RIGHT: payment form + history
            '<div class="fc-col fc-col-right">',

            '<div class="fc-card">',
            '<div class="fc-card-head"><i class="fas fa-money-bill-wave"></i> Record Payment</div>',
            '<div class="fc-card-body">',

            '<div class="fc-fields-row">',
            '<div class="fc-field">',
            '<label>Amount (GH₵) <span class="req">*</span></label>',
            '<input type="number" id="fcAmount" min="0.01" step="0.01" placeholder="0.00">',
            '</div>',
            '<div class="fc-field">',
            '<label>Payment Method</label>',
            '<select id="fcMethod">',
            '<option value="CASH">Cash</option>',
            '<option value="MOMO">Mobile Money</option>',
            '<option value="BANK">Bank Transfer</option>',
            '<option value="ONLINE">Online (Paystack)</option>',
            '</select>',
            '</div>',
            '</div>',

            '<div class="fc-fields-row">',
            '<div class="fc-field">',
            '<label>Term <span class="req">*</span></label>',
            '<select id="fcTerm">' + termOpts + '</select>',
            '</div>',
            '<div class="fc-field">',
            '<label>Academic Year <span class="req">*</span></label>',
            '<select id="fcYear">' + yearOpts + '</select>',
            '</div>',
            '</div>',

            '<div class="fc-fields-row">',
            '<div class="fc-field">',
            '<label>Received By <span class="req">*</span></label>',
            '<input type="text" id="fcPaidBy" placeholder="Cashier name">',
            '</div>',
            '<div class="fc-field">',
            '<label>Receipt / Reference No.</label>',
            '<input type="text" id="fcReceipt" placeholder="Auto-generated if blank">',
            '</div>',
            '</div>',

            '<div class="fc-online-wrap" id="fcOnlineWrap" style="display:none">',
            '<div class="fc-field">',
            '<label>Parent / Guardian Email (for Paystack)</label>',
            '<input type="email" id="fcEmail" placeholder="parent@email.com">',
            '</div>',
            '</div>',

            '<div class="fc-actions">',
            '<button class="fc-btn fc-btn-success" id="fcPayBtn" disabled>',
            '<i class="fas fa-check-circle"></i> Record Payment',
            '</button>',
            '<button class="fc-btn fc-btn-secondary" id="fcClearBtn">',
            '<i class="fas fa-times"></i> Clear',
            '</button>',
            '</div>',
            '</div></div>',

            // Payment history
            '<div class="fc-card fc-history-card" id="fcHistoryCard" style="display:none">',
            '<div class="fc-card-head">',
            '<span><i class="fas fa-history"></i> Payment History</span>',
            '<button class="fc-btn-icon" id="fcPrintHistoryBtn" title="Print"><i class="fas fa-print"></i></button>',
            '</div>',
            '<div class="fc-card-body">',
            '<table class="fc-table">',
            '<thead><tr>',
            '<th>Date</th><th>Amount</th><th>Method</th>',
            '<th>Term</th><th>Year</th><th>Receipt</th><th>By</th>',
            '</tr></thead>',
            '<tbody id="fcHistoryBody"><tr><td colspan="7" class="fc-empty">Select a student to view history</td></tr></tbody>',
            '</table>',
            '</div></div>',

            '</div>', // end right col
            '</div>', // end body

            // Receipt modal
            '<div class="fc-modal-overlay" id="fcReceiptModal">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head">',
            '<h5><i class="fas fa-receipt"></i> Payment Receipt</h5>',
            '<button class="fc-modal-close" id="fcReceiptClose">&times;</button>',
            '</div>',
            '<div class="fc-modal-body" id="fcReceiptBody"></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="fcReceiptCloseBtn">Close</button>',
            '<button class="fc-btn fc-btn-primary" id="fcReceiptPrintBtn">',
            '<i class="fas fa-print"></i> Print',
            '</button>',
            '</div>',
            '</div>',
            '</div>',

            '<div class="fc-toast" id="fcToast"></div>',

            '<footer class="footer">',
            '<i class="far fa-copyright"></i> Astromy LLC 2013–<span id="fcYear"></span> | Fee Collection',
            '</footer>',

            '</div>',
        ].join('');

        document.getElementById('fcYear').textContent = new Date().getFullYear();
    }

    // ── POPULATE GROUP DROPDOWN ──────────────────────────────────────────────
    function populateGroups() {
        var sel = document.getElementById('fcGroupSel');
        if (!sel) return;
        sel.innerHTML = '<option value="">Select group…</option>' +
            window.feeCollData.classGroups.map(function (g) {
                return '<option value="' + g.name + '">' + g.name + '</option>';
            }).join('');
        sel.disabled = false;
    }

    // ── RENDER BILL SUMMARY ──────────────────────────────────────────────────
    function renderBill(bill, studentName) {
        if (!bill) {
            document.getElementById('fcBillCard').style.display = 'none';
            return;
        }
        var fmt = window.feeCollFmt;
        document.getElementById('fcStudentName').textContent = studentName || bill.studentId;
        document.getElementById('fcAmtDue').textContent = fmt.money(bill.amountDue || 0);
        document.getElementById('fcAmtPaid').textContent = fmt.money(bill.amountPaid || 0);

        var bal = bill.amountBalance || 0;
        var balEl = document.getElementById('fcBalance');
        balEl.textContent = fmt.money(bal);
        balEl.className = 'fc-bill-val ' + (bal > 0 ? 'fc-balance fc-owing' : 'fc-balance fc-clear');

        document.getElementById('fcOldBal').textContent = fmt.money(bill.oldBalance || 0);
        document.getElementById('fcBillCard').style.display = '';

        // Pre-fill amount with outstanding balance
        var amtInput = document.getElementById('fcAmount');
        if (amtInput && bal > 0) amtInput.value = bal.toFixed(2);
    }

    // ── RENDER HISTORY ───────────────────────────────────────────────────────
    function renderHistory(history) {
        var tbody = document.getElementById('fcHistoryBody');
        var card = document.getElementById('fcHistoryCard');
        if (!tbody) return;

        card.style.display = '';

        if (!history || !history.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="fc-empty">No payment records found</td></tr>';
            return;
        }
        var fmt = window.feeCollFmt;
        tbody.innerHTML = history.map(function (p) {
            return '<tr>' +
                '<td>' + fmt.dateTime(p.paymentDate) + '</td>' +
                '<td class="fc-money">' + fmt.money(p.paymentAmount) + '</td>' +
                '<td>' + (p.paymentMethod || 'CASH') + '</td>' +
                '<td>' + (p.term || '—') + '</td>' +
                '<td>' + (p.academicYear || '—') + '</td>' +
                '<td>' + (p.recieptNum || '—') + '</td>' +
                '<td>' + (p.paidBy || '—') + '</td>' +
                '</tr>';
        }).join('');
    }

    // ── RECEIPT MODAL ────────────────────────────────────────────────────────
    function showReceipt(payment, studentName) {
        var fmt = window.feeCollFmt;
        var bill = window.feeCollState.studentBill;

        document.getElementById('fcReceiptBody').innerHTML = [
            '<div class="fc-receipt">',
            '<div class="fc-receipt-header">',
            '<div class="fc-receipt-logo"><i class="fas fa-star"></i> Shooting Star</div>',
            '<div class="fc-receipt-title">Official Receipt</div>',
            '</div>',
            '<div class="fc-receipt-row"><span>Receipt No.</span><strong>' + (payment.recieptNum || payment.billPaymentId || '—') + '</strong></div>',
            '<div class="fc-receipt-row"><span>Date</span><strong>' + fmt.dateTime(payment.paymentDate) + '</strong></div>',
            '<div class="fc-receipt-divider"></div>',
            '<div class="fc-receipt-row"><span>Student</span><strong>' + studentName + '</strong></div>',
            '<div class="fc-receipt-row"><span>Student ID</span><strong>' + payment.studentId + '</strong></div>',
            '<div class="fc-receipt-row"><span>Term</span><strong>' + payment.term + ' — ' + payment.academicYear + '</strong></div>',
            '<div class="fc-receipt-divider"></div>',
            '<div class="fc-receipt-row fc-receipt-amount"><span>Amount Paid</span><strong>' + fmt.money(payment.paymentAmount) + '</strong></div>',
            '<div class="fc-receipt-row"><span>Method</span><strong>' + (payment.paymentMethod || 'CASH') + '</strong></div>',
            '<div class="fc-receipt-row"><span>Received By</span><strong>' + (payment.paidBy || '—') + '</strong></div>',
            bill ? '<div class="fc-receipt-divider"></div>' +
                '<div class="fc-receipt-row"><span>New Balance</span><strong>' + fmt.money(bill.amountBalance) + '</strong></div>' : '',
            '<div class="fc-receipt-footer">Thank you. Please keep this receipt for your records.</div>',
            '</div>',
        ].join('');

        document.getElementById('fcReceiptModal').classList.add('open');
    }

    function closeReceiptModal() {
        document.getElementById('fcReceiptModal').classList.remove('open');
    }

    // ── WIRE EVENTS ──────────────────────────────────────────────────────────
    function wireEvents() {

        // Group → populate classes
        document.getElementById('fcGroupSel').addEventListener('change', function () {
            var group = this.value;
            var classSel = document.getElementById('fcClassSel');
            var classes = window.feeCollClassesByGroup(group);
            classSel.innerHTML = '<option value="">Select class…</option>' +
                classes.map(function (c) {
                    return '<option>' + c.name + '</option>';
                }).join('');
            classSel.disabled = !classes.length;
            document.getElementById('fcStudentSel').innerHTML = '<option value="">Select class first</option>';
            document.getElementById('fcStudentSel').disabled = true;
            document.getElementById('fcLoadBillBtn').disabled = true;
        });

        // Class → fetch & populate students
        document.getElementById('fcClassSel').addEventListener('change', async function () {
            var cls = this.value;
            if (!cls) return;
            var studSel = document.getElementById('fcStudentSel');
            studSel.innerHTML = '<option value="">Loading…</option>';
            studSel.disabled = true;
            var students = await window.feeCollFetchStudents(cls);
            studSel.innerHTML = '<option value="">Select student…</option>' +
                students.map(function (s) {
                    return '<option value="' + s.id + '" data-name="' + s.name + '">' + s.name + ' (' + s.id + ')</option>';
                }).join('');
            studSel.disabled = false;
        });

        // Student selected → enable load button
        document.getElementById('fcStudentSel').addEventListener('change', function () {
            document.getElementById('fcLoadBillBtn').disabled = !this.value;
        });

        // Load bill button
        document.getElementById('fcLoadBillBtn').addEventListener('click', async function () {
            var studSel = document.getElementById('fcStudentSel');
            var studentId = studSel.value;
            var studentName = studSel.options[studSel.selectedIndex]?.dataset.name || studentId;

            if (!studentId) return;
            window.feeCollState.selectedStudent = {id: studentId, name: studentName};

            var [bill, history] = await Promise.all([
                window.feeCollFetchStudentBill(studentId),
                window.feeCollFetchHistory(studentId),
            ]);

            renderBill(bill, studentName);
            renderHistory(history);
            document.getElementById('fcPayBtn').disabled = false;
        });

        // Method → show/hide Paystack email field
        document.getElementById('fcMethod').addEventListener('change', function () {
            window.feeCollState.paymentMethod = this.value;
            document.getElementById('fcOnlineWrap').style.display =
                this.value === 'ONLINE' ? '' : 'none';
        });

        // Record Payment
        document.getElementById('fcPayBtn').addEventListener('click', async function () {
            var student = window.feeCollState.selectedStudent;
            if (!student) {
                toast('No student selected.');
                return;
            }

            var amount = parseFloat(document.getElementById('fcAmount').value);
            var paidBy = document.getElementById('fcPaidBy').value.trim();
            var receipt = document.getElementById('fcReceipt').value.trim();
            var term = document.getElementById('fcTerm').value;
            var year = document.getElementById('fcYear') ? document.getElementById('fcYear').textContent : document.getElementById('fcYear')?.textContent;
            var yearVal = document.getElementById('fcYearSel') ? document.getElementById('fcYearSel').value : window.feeCollState.selectedYear;
            var method = document.getElementById('fcMethod').value;

            // Use the correct year select id
            var yearSel = document.querySelector('#fcYear, select[id="fcYear"]');
            yearVal = window.feeCollState.selectedYear;
            var yrEl = document.getElementById('fcYear');
            // fcYear is the footer span — use a different approach
            var yrInputs = document.querySelectorAll('.fc-page select');
            yrInputs.forEach(function (s) {
                if (s.id === 'fcYearSel') yearVal = s.value;
            });

            if (!amount || amount <= 0) {
                toast('Enter a valid payment amount.', 'error');
                return;
            }
            if (!paidBy) {
                toast('Enter the name of the cashier.', 'error');
                return;
            }

            var req = {
                studentId: student.id,
                amount: amount,
                paidBy: paidBy,
                receiptNum: receipt || ('REC-' + Date.now()),
                term: term,
                academicYear: yearVal,
                paymentMethod: method,
            };

            if (method === 'ONLINE') {
                var email = document.getElementById('fcEmail').value.trim();
                window.feeCollPaystack({
                    email: email || 'admin@astromyllc.com',
                    amountGhs: amount,
                    studentId: student.id,
                    studentName: student.name,
                    onSuccess: async function (ref) {
                        req.externalReference = ref;
                        await submitAndRefresh(req);
                    },
                    onClose: function () {
                        toast('Payment window closed.', 'warning');
                    },
                });
            } else {
                await submitAndRefresh(req);
            }
        });

        async function submitAndRefresh(req) {
            try {
                var result = await window.feeCollSubmit(req);
                var bill = await window.feeCollFetchStudentBill(req.studentId);
                var hist = await window.feeCollFetchHistory(req.studentId);
                renderBill(bill, window.feeCollState.selectedStudent.name);
                renderHistory(hist);
                showReceipt(result, window.feeCollState.selectedStudent.name);
                toast('Payment of ' + window.feeCollFmt.money(req.amount) + ' recorded.', 'success');
                document.getElementById('fcAmount').value = '';
                document.getElementById('fcReceipt').value = '';
            } catch (e) {
                toast('Payment failed: ' + e.message, 'error');
            }
        }

        // Clear
        document.getElementById('fcClearBtn').addEventListener('click', function () {
            document.getElementById('fcAmount').value = '';
            document.getElementById('fcPaidBy').value = '';
            document.getElementById('fcReceipt').value = '';
            document.getElementById('fcEmail').value = '';
        });

        // Receipt modal
        document.getElementById('fcReceiptClose').addEventListener('click', closeReceiptModal);
        document.getElementById('fcReceiptCloseBtn').addEventListener('click', closeReceiptModal);
        document.getElementById('fcReceiptPrintBtn').addEventListener('click', function () {
            var win = window.open('', '_blank');
            win.document.write('<html><head><title>Receipt</title><style>body{font-family:sans-serif;padding:24px;max-width:400px;margin:0 auto;}.fc-receipt-row{display:flex;justify-content:space-between;padding:6px 0;border-bottom:1px solid #eee;font-size:14px;}.fc-receipt-amount strong{color:#1A3A5C;font-size:18px;}.fc-receipt-header{text-align:center;margin-bottom:16px;}.fc-receipt-footer{text-align:center;font-size:12px;color:#888;margin-top:16px;}</style></head><body>' +
                document.getElementById('fcReceiptBody').innerHTML + '</body></html>');
            win.document.close();
            win.print();
        });
        document.getElementById('fcReceiptModal').addEventListener('click', function (e) {
            if (e.target === this) closeReceiptModal();
        });

        // Print history
        document.getElementById('fcPrintHistoryBtn').addEventListener('click', function () {
            var win = window.open('', '_blank');
            var table = document.querySelector('.fc-history-card .fc-table');
            if (!table) return;
            win.document.write('<html><head><title>Payment History</title><style>table{width:100%;border-collapse:collapse;}th,td{padding:8px;border:1px solid #ddd;font-size:13px;}th{background:#1A3A5C;color:#fff;}</style></head><body><h3>Payment History — ' + (window.feeCollState.selectedStudent?.name || '') + '</h3>' + table.outerHTML + '</body></html>');
            win.document.close();
            win.print();
        });
    }

    // ── TOAST ────────────────────────────────────────────────────────────────
    function toast(msg, type) {
        var el = document.getElementById('fcToast');
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
            if (window.feeCollState && window.feeCollState._loaded) {
                clearInterval(t);
                populateGroups();
            }
        }, 30);
    }

    function boot() {
        if (window.feeCollState) {
            init();
            return;
        }
        var s = document.createElement('script');
        s.src = _base + '../_financeFeeCollection.js';
        s.onload = function () {
            init();
        };
        document.body.appendChild(s);
    }

    document.readyState === 'loading'
        ? document.addEventListener('DOMContentLoaded', boot)
        : boot();

})();