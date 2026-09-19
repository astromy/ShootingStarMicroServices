/**
 * _clinicPharmacy.js  —  Data & logic layer for the Clinic Pharmacy / Stock module.
 *
 * Loaded at runtime by scripts/subscripts/clinicPharmacy.js, same two-file
 * pattern as _clinic.js / _storesInventory.js. Talks to the clinic
 * microservice's MedicalProduct/Inventory endpoints via ClinicController's
 * /api/clinic/pharmacy/** proxy routes.
 *
 * Exposes on window:
 *   pharmacyState / pharmacyData
 *   pharmacyLoad()                    — initial product catalogue + movement history load
 *   pharmacyCreateProduct(req)
 *   pharmacyFetchProducts()
 *   pharmacyDeactivateProduct(id)
 *   pharmacyRecordMovement(req)       — RESTOCK or DISPENSE, per req.movementType
 *   pharmacyFetchMovements()
 *   pharmacyFmt
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    var _recordedBy = (typeof staffId !== 'undefined' && staffId)
        ? staffId
        : ((typeof userName !== 'undefined' && userName) ? userName : '');

    window.pharmacyState = {
        institutionCode: _inst,
        recordedBy: _recordedBy,
        _loaded: false,
    };

    window.pharmacyData = {
        products: [],
        movements: [],
    };

    function showSplash() {
        if (typeof $ !== 'undefined') {
            $('.splash').css({display: 'block', background: '#ffffff3d'}).find('h1, p').remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    window.pharmacyLoad = async function () {
        try {
            showSplash();
            await Promise.all([
                window.pharmacyFetchProducts(),
                window.pharmacyFetchMovements(),
            ]);
            window.pharmacyState._loaded = true;
        } catch (e) {
            console.error('[_clinicPharmacy] Initial load failed:', e);
            window.pharmacyState._loaded = true;
        } finally {
            hideSplash();
        }
    };

    window.pharmacyCreateProduct = async function (req) {
        var product = await fetchPost('clinic/pharmacy/createProduct', req);
        await window.pharmacyFetchProducts();
        return product;
    };

    window.pharmacyFetchProducts = async function () {
        var products = await fetchPost('clinic/pharmacy/getProductsByInstitution', {institutionCode: _inst});
        window.pharmacyData.products = Array.isArray(products) ? products : [];
        return window.pharmacyData.products;
    };

    window.pharmacyDeactivateProduct = async function (productId) {
        await fetchPost('clinic/pharmacy/deactivateProduct/' + productId, {});
        await window.pharmacyFetchProducts();
    };

    window.pharmacyRecordMovement = async function (req) {
        var movement = await fetchPost('clinic/pharmacy/recordMovement', req);
        await Promise.all([window.pharmacyFetchProducts(), window.pharmacyFetchMovements()]);
        return movement;
    };

    window.pharmacyFetchMovements = async function () {
        var movements = await fetchPost('clinic/pharmacy/getMovementsByInstitution', {institutionCode: _inst});
        window.pharmacyData.movements = Array.isArray(movements) ? movements : [];
        return window.pharmacyData.movements;
    };

    window.pharmacyFmt = {
        dateTime: function (dateStr) {
            if (!dateStr) return '';
            var d = new Date(dateStr);
            if (isNaN(d.getTime())) return dateStr;
            return d.toLocaleDateString([], {month: 'short', day: 'numeric', year: 'numeric'}) +
                ' ' + d.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
        },
    };
})();
