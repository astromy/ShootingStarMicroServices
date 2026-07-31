/**
 * libraryCatalogue.js  —  UI renderer for Library Catalogue Management (Admin).
 *
 * Rebuilt to use the SAME design system as Fee Collection / Salary / Ledger
 * (financeShared.css — .fc-page, .fc-header, .fc-card, .fc-table, .fc-btn,
 * .fc-modal, .fc-toast, .ph-stat, .ph-table-wrap, .fc-field), rather than
 * invented one-off classes, so this looks native to the rest of the app.
 *
 * Dynamically loads _libraryCatalogue.js — same two-file pattern as
 * storesAdmin.js + _storesAdmin.js.
 */
(function () {
    'use strict';

    var CATEGORY_META = {
        FICTION: {icon: 'fa-book', label: 'Fiction'},
        NON_FICTION: {icon: 'fa-book-open', label: 'Non-Fiction'},
        TEXTBOOK: {icon: 'fa-graduation-cap', label: 'Textbook'},
        REFERENCE: {icon: 'fa-bookmark', label: 'Reference'},
        PERIODICAL: {icon: 'fa-newspaper', label: 'Periodical'},
        OTHER: {icon: 'fa-book', label: 'Other'},
    };

    // ─── BUILD DOM ────────────────────────────────────────────────────────────
    function buildDOM() {
        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [

            '<header class="fc-header">',
            '<div><h1><i class="fas fa-book"></i> Library Catalogue</h1>',
            '<p>Manage titles, copy counts, and availability</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="lcRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '<button class="fc-btn fc-btn-primary" id="lcAddBookBtn"><i class="fas fa-plus"></i> Add Book</button>',
            '</div>',
            '</header>',

            '<div class="ph-stats" id="lcStats"></div>',

            '<div class="ph-controls">',
            '<div class="ph-search-wrap">',
            '<i class="fas fa-search"></i>',
            '<input type="text" id="lcSearchInput" placeholder="Search by title, author, or code…">',
            '</div>',
            '<select id="lcCategoryFilter">',
            '<option value="">All Categories</option>',
            '<option value="FICTION">Fiction</option>',
            '<option value="NON_FICTION">Non-Fiction</option>',
            '<option value="TEXTBOOK">Textbook</option>',
            '<option value="REFERENCE">Reference</option>',
            '<option value="PERIODICAL">Periodical</option>',
            '<option value="OTHER">Other</option>',
            '</select>',
            '</div>',

            '<div class="ph-table-wrap">',
            '<table class="fc-table" id="lcBooksTable">',
            '<thead><tr>',
            '<th>Code</th><th>Title</th><th>Author</th><th>Category</th>',
            '<th>Total</th><th>Available</th><th>Status</th><th>Actions</th>',
            '</tr></thead><tbody></tbody>',
            '</table>',
            '</div>',

            // ── ADD/EDIT MODAL ──────────────────────────────────────────────
            '<div class="fc-modal-overlay" id="lcBookModalOverlay">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head">',
            '<h5 id="lcBookModalTitle"><i class="fas fa-book"></i> Add Book</h5>',
            '<button class="fc-modal-close" id="lcBookModalClose">&times;</button>',
            '</div>',
            '<div class="fc-modal-body">',
            '<input type="hidden" id="lcBookId">',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Book Code <span class="req">*</span></label><input type="text" id="lcBookCode" placeholder="e.g. LIB-00231"></div>',
            '<div class="fc-field"><label>ISBN (optional)</label><input type="text" id="lcBookIsbn"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Title <span class="req">*</span></label><input type="text" id="lcBookTitle"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Author</label><input type="text" id="lcBookAuthor"></div>',
            '<div class="fc-field"><label>Publisher (optional)</label><input type="text" id="lcBookPublisher"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Category</label>',
            '<select id="lcBookCategory">',
            '<option value="FICTION">Fiction</option>',
            '<option value="NON_FICTION">Non-Fiction</option>',
            '<option value="TEXTBOOK">Textbook</option>',
            '<option value="REFERENCE">Reference</option>',
            '<option value="PERIODICAL">Periodical</option>',
            '<option value="OTHER">Other</option>',
            '</select></div>',
            '<div class="fc-field"><label>Total Copies</label><input type="number" id="lcBookTotalCopies" min="1" value="1"></div>',
            '</div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="lcBookModalCancel">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="lcBookModalSave"><i class="fas fa-check"></i> Save</button>',
            '</div>',
            '</div>',
            '</div>',

            '<div class="fc-toast" id="lcToast"></div>',

        ].join('') + '</div>';
    }

    // ─── STATS STRIP (.ph-stat, matches Fee Collection / Payment History) ────
    function renderStats() {
        var books = window.libraryData.books;
        var totalTitles = books.length;
        var totalCopies = books.reduce(function (s, b) { return s + (b.totalCopies || 0); }, 0);
        var onLoan = books.reduce(function (s, b) { return s + ((b.totalCopies || 0) - (b.availableCopies || 0)); }, 0);
        var fullyOut = books.filter(function (b) { return (b.availableCopies || 0) === 0; }).length;

        document.getElementById('lcStats').innerHTML = [
            statCard('fa-book', 'Titles', totalTitles, 'ph-stat-blue'),
            statCard('fa-copy', 'Total Copies', totalCopies, 'ph-stat-navy'),
            statCard('fa-hand-holding', 'Currently On Loan', onLoan, 'ph-stat-amber'),
            statCard('fa-exclamation-triangle', 'Fully Checked Out', fullyOut, 'ph-stat-green'),
        ].join('');
    }

    function statCard(icon, label, value, colorClass) {
        return '<div class="ph-stat ' + colorClass + '">' +
            '<i class="fas ' + icon + ' ph-stat-icon"></i>' +
            '<div><h3>' + value + '</h3><p>' + label + '</p></div>' +
            '</div>';
    }

    // ─── TABLE ──────────────────────────────────────────────────────────────
    function renderTable() {
        var search = (document.getElementById('lcSearchInput').value || '').toLowerCase();
        var category = document.getElementById('lcCategoryFilter').value;

        var rows = window.libraryData.books.filter(function (b) {
            var matchesSearch = !search ||
                (b.title || '').toLowerCase().includes(search) ||
                (b.author || '').toLowerCase().includes(search) ||
                (b.bookCode || '').toLowerCase().includes(search);
            var matchesCategory = !category || b.category === category;
            return matchesSearch && matchesCategory;
        });

        var tbody = document.querySelector('#lcBooksTable tbody');

        if (!rows.length) {
            tbody.innerHTML = '<tr><td colspan="8" class="fc-empty">No books match your filters.</td></tr>';
            return;
        }

        tbody.innerHTML = rows.map(function (b) {
            var meta = CATEGORY_META[b.category] || CATEGORY_META.OTHER;
            var statusBadge = b.availableCopies > 0
                ? '<span class="badge badge-success">Available</span>'
                : '<span class="badge badge-danger">Fully Checked Out</span>';
            return '<tr>' +
                '<td>' + escapeHtml(b.bookCode) + '</td>' +
                '<td>' + escapeHtml(b.title) + '</td>' +
                '<td>' + escapeHtml(b.author || '—') + '</td>' +
                '<td><i class="fas ' + meta.icon + '"></i> ' + meta.label + '</td>' +
                '<td>' + b.totalCopies + '</td>' +
                '<td>' + b.availableCopies + '</td>' +
                '<td>' + statusBadge + '</td>' +
                '<td>' +
                '<button class="fc-btn-icon lc-edit-btn" data-id="' + b.id + '" title="Edit"><i class="fas fa-edit"></i></button>' +
                '<button class="fc-btn-icon lc-deactivate-btn" data-id="' + b.id + '" title="Remove"><i class="fas fa-trash"></i></button>' +
                '</td>' +
                '</tr>';
        }).join('');

        tbody.querySelectorAll('.lc-edit-btn').forEach(function (btn) {
            btn.addEventListener('click', function () { openEditModal(this.dataset.id); });
        });
        tbody.querySelectorAll('.lc-deactivate-btn').forEach(function (btn) {
            btn.addEventListener('click', function () { confirmDeactivate(this.dataset.id); });
        });
    }

    function escapeHtml(s) {
        return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
            return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c];
        });
    }

    // ─── TOAST (matches .fc-toast used across Finance screens) ─────────────
    var toastTimer = null;
    function showToast(message, type) {
        var el = document.getElementById('lcToast');
        el.textContent = message;
        el.className = 'fc-toast show' + (type ? ' ' + type : '');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(function () { el.classList.remove('show'); }, 3200);
    }

    // ─── ADD/EDIT MODAL ─────────────────────────────────────────────────────
    function openAddModal() {
        document.getElementById('lcBookModalTitle').innerHTML = '<i class="fas fa-book"></i> Add Book';
        document.getElementById('lcBookId').value = '';
        ['lcBookCode', 'lcBookIsbn', 'lcBookTitle', 'lcBookAuthor', 'lcBookPublisher'].forEach(function (id) {
            document.getElementById(id).value = '';
        });
        document.getElementById('lcBookCategory').value = 'FICTION';
        document.getElementById('lcBookTotalCopies').value = 1;
        document.getElementById('lcBookModalOverlay').classList.add('open');
    }

    function openEditModal(id) {
        var book = window.libraryData.books.find(function (b) { return b.id === id; });
        if (!book) return;
        document.getElementById('lcBookModalTitle').innerHTML = '<i class="fas fa-book"></i> Edit Book';
        document.getElementById('lcBookId').value = book.id;
        document.getElementById('lcBookCode').value = book.bookCode || '';
        document.getElementById('lcBookIsbn').value = book.isbn || '';
        document.getElementById('lcBookTitle').value = book.title || '';
        document.getElementById('lcBookAuthor').value = book.author || '';
        document.getElementById('lcBookPublisher').value = book.publisher || '';
        document.getElementById('lcBookCategory').value = book.category || 'FICTION';
        document.getElementById('lcBookTotalCopies').value = book.totalCopies || 1;
        document.getElementById('lcBookModalOverlay').classList.add('open');
    }

    function closeModal() {
        document.getElementById('lcBookModalOverlay').classList.remove('open');
    }

    async function saveBook() {
        var id = document.getElementById('lcBookId').value;
        var payload = {
            institutionCode: window.libraryState.institutionCode,
            bookCode: document.getElementById('lcBookCode').value.trim(),
            isbn: document.getElementById('lcBookIsbn').value.trim(),
            title: document.getElementById('lcBookTitle').value.trim(),
            author: document.getElementById('lcBookAuthor').value.trim(),
            publisher: document.getElementById('lcBookPublisher').value.trim(),
            category: document.getElementById('lcBookCategory').value,
            totalCopies: parseInt(document.getElementById('lcBookTotalCopies').value, 10) || 1,
        };

        if (!payload.bookCode || !payload.title) {
            showToast('Book code and title are required.', 'warning');
            return;
        }

        try {
            if (id) {
                await window.libraryUpdateBook(id, payload);
                showToast('Book updated.', 'success');
            } else {
                await window.libraryCreateBook(payload);
                showToast('Book added to catalogue.', 'success');
            }
            closeModal();
            await window.libraryFetchBooks();
            renderStats();
            renderTable();
        } catch (e) {
            showToast('Could not save book: ' + (e.message || e), 'error');
        }
    }

    function confirmDeactivate(id) {
        var book = window.libraryData.books.find(function (b) { return b.id === id; });
        if (!book) return;
        if (!confirm('Remove "' + book.title + '" from the catalogue?')) return;

        window.libraryDeactivateBook(id).then(function () {
            showToast('Book removed from catalogue.', 'success');
            return window.libraryFetchBooks();
        }).then(function () {
            renderStats();
            renderTable();
        }).catch(function (e) {
            showToast('Could not remove book: ' + (e.message || e), 'error');
        });
    }

    // ─── WIRE EVENTS ────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('lcRefreshBtn').addEventListener('click', async function () {
            await window.libraryFetchBooks();
            renderStats();
            renderTable();
        });
        document.getElementById('lcAddBookBtn').addEventListener('click', openAddModal);
        document.getElementById('lcBookModalClose').addEventListener('click', closeModal);
        document.getElementById('lcBookModalCancel').addEventListener('click', closeModal);
        document.getElementById('lcBookModalSave').addEventListener('click', saveBook);
        document.getElementById('lcBookModalOverlay').addEventListener('click', function (e) {
            if (e.target === this) closeModal();
        });
        document.getElementById('lcSearchInput').addEventListener('input', renderTable);
        document.getElementById('lcCategoryFilter').addEventListener('change', renderTable);
    }

    // ─── INIT ───────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        renderStats();
        renderTable();
        waitForRealData();
    }

    function waitForRealData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.libraryState._loaded || attempts > 200) {
                clearInterval(timer);
                renderStats();
                renderTable();
                if (attempts > 200) console.warn('[libraryCatalogue] API data slow — rendering partial data.');
            }
        }, 30);
    }

    // ─── DYNAMIC SCRIPT LOADER ────────────────────────────────────────────────
    function loadLogicScript() {
        var el = document.currentScript;
        var base = el && el.src ? el.src.substring(0, el.src.lastIndexOf('/') + 1) : '';

        if (window.libraryData && window.libraryState) {
            init();
            return;
        }

        var s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = base + '../_libraryCatalogue.js';

        s.onload = function () {
            if (window.libraryData && window.libraryState) {
                init();
            } else {
                console.error('[libraryCatalogue] _libraryCatalogue.js loaded but window.libraryData is not defined.');
            }
        };

        s.onerror = function () {
            console.error('[libraryCatalogue] Could not load: ' + base + '../_libraryCatalogue.js');
        };

        document.body.appendChild(s);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initLibraryCatalogue = init;

})();
