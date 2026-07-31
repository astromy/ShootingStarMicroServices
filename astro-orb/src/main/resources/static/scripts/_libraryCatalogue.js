/**
 * _libraryCatalogue.js  —  Data & logic layer for the Library Catalogue module.
 *
 * Loaded at runtime by libraryCatalogue.js (UI renderer), following the same
 * two-file pattern as _storesAdmin.js / _financeBilling.js.
 *
 * Backend calls all go through the fetchPost('<route>', body) helper in
 * common.js, which is proxied by astro-orb's LibraryController to the
 * library microservice (POST/GET /api/library/**).
 *
 * Exposes on window:
 *   libraryState / libraryData
 *   libraryFetchBooks()             — get-by-institution
 *   libraryCreateBook(req)
 *   libraryUpdateBook(id, req)
 *   libraryDeactivateBook(id)
 *   librarySearchBooks(keyword)
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    window.libraryState = {
        institutionCode: _inst,
        _loaded: false,
    };

    window.libraryData = {
        books: [],
    };

    function showSplash() {
        if (typeof $ !== 'undefined') {
            $('.splash').css({display: 'block', background: '#ffffff3d'}).find('h1, p').remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    // ── LOAD ─────────────────────────────────────────────────────────────────
    window.libraryFetchBooks = async function () {
        try {
            showSplash();
            var result = await fetchPost('library/books/get-by-institution', {institutionCode: _inst});
            window.libraryData.books = Array.isArray(result) ? result : [];
            window.libraryState._loaded = true;
            hideSplash();
            return window.libraryData.books;
        } catch (e) {
            hideSplash();
            window.libraryState._loaded = true;
            console.error('[_libraryCatalogue] load error:', e);
            return [];
        }
    };

    window.libraryCreateBook = async function (req) {
        return fetchPost('library/books/create', req);
    };

    window.libraryUpdateBook = async function (id, req) {
        return fetchPost('library/books/update/' + id, req);
    };

    window.libraryDeactivateBook = async function (id) {
        return fetchPost('library/books/deactivate/' + id, {});
    };

    window.librarySearchBooks = async function (keyword) {
        try {
            var result = await fetchPost('library/books/search', {institutionCode: _inst, keyword: keyword});
            return Array.isArray(result) ? result : [];
        } catch (e) {
            console.error('[_libraryCatalogue] search error:', e);
            return [];
        }
    };

    // Initial load as soon as this data layer is available.
    window.libraryFetchBooks();

})();
