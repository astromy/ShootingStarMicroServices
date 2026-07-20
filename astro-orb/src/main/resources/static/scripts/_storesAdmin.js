/**
 * _storesAdmin.js  —  Data & logic layer for Inventory Management (Admin).
 *
 * Companion to _storesInventory.js (the purchase-side storefront/POS).
 * This file covers the ADMIN side: create items, restock, monitor stock
 * levels, and review the stock movement audit trail.
 *
 * Follows the same two-file pattern as _financeLedgers.js + financeLedger.js.
 *
 * Exposes on window:
 *   storesAdminState / storesAdminData
 *   storesAdminLoad()                  — initial load (all items + low stock)
 *   storesAdminFetchAll(itemType)      — full inventory list, optional type filter
 *   storesAdminCreateItem(req)         — create item
 *   storesAdminUpdateItem(id, req)     — edit item
 *   storesAdminDeactivateItem(id)      — soft-delete
 *   storesAdminRestock(req)            — add stock
 *   storesAdminFetchMovements()        — full audit trail for institution
 *   storesAdminFetchMovementsByItem(id)— audit trail for one item
 *   storesAdminFmt                     — shared formatters (reuses storesFmt if present)
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.storesAdminState = {
        institutionCode: _inst,
        selectedType: '',     // '' | PROSPECTUS | UNIFORM | STATIONERY | OTHER
        searchQuery: '',
        _loaded: false,
    };

    window.storesAdminData = {
        allItems: [],
        lowStockItems: [],
        movements: [],
    };

    // ── HELPERS ──────────────────────────────────────────────────────────────
    function showSplash() {
        if (typeof $ !== 'undefined') {
            $('.splash').css({display: 'block', background: '#ffffff3d'}).find('h1, p').remove();
        }
    }

    function hideSplash() {
        if (typeof $ !== 'undefined') $('.splash').css('display', 'none');
    }

    // ── 1. INITIAL LOAD ──────────────────────────────────────────────────────
    window.storesAdminLoad = async function () {
        try {
            showSplash();
            var [items, lowStock] = await Promise.all([
                fetchPost('stores/items/get-by-institution', {institutionCode: _inst, itemType: null}),
                fetchPost('stores/items/low-stock/' + _inst, {}),
            ]);
            window.storesAdminData.allItems = Array.isArray(items) ? items : [];
            window.storesAdminData.lowStockItems = Array.isArray(lowStock) ? lowStock : [];
            window.storesAdminState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.storesAdminState._loaded = true;
            console.error('[_storesAdmin] load error:', e);
        }
    };

    // ── 2. FETCH ALL ITEMS (optional type filter) ───────────────────────────
    window.storesAdminFetchAll = async function (itemType) {
        try {
            var result = await fetchPost('stores/items/get-by-institution', {
                institutionCode: _inst,
                itemType: itemType || null,
            });
            window.storesAdminData.allItems = Array.isArray(result) ? result : [];
            return window.storesAdminData.allItems;
        } catch (e) {
            return [];
        }
    };

    window.storesAdminFetchLowStock = async function () {
        try {
            var result = await fetchPost('stores/items/low-stock/' + _inst, {});
            window.storesAdminData.lowStockItems = Array.isArray(result) ? result : [];
            return window.storesAdminData.lowStockItems;
        } catch (e) {
            return [];
        }
    };

    // ── 3. CREATE / EDIT / DEACTIVATE ────────────────────────────────────────

    /**
     * req: { itemName, itemCode, itemType, description, unitPrice,
     *        quantityInStock, reorderLevel, channel }
     */
    window.storesAdminCreateItem = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('stores/items/create', Object.assign({
                institutionCode: _inst,
            }, req));
            window.storesAdminData.allItems.push(result);
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    window.storesAdminUpdateItem = async function (id, req) {
        showSplash();
        try {
            // PUT not supported by fetchPost's POST-only signature in this codebase,
            // so the proxy maps stores/items/update/{id} → PUT /api/stores/items/{id}
            var result = await fetchPost('stores/items/update/' + id, Object.assign({
                institutionCode: _inst,
            }, req));
            var idx = window.storesAdminData.allItems.findIndex(function (i) {
                return i.id === id;
            });
            if (idx >= 0) window.storesAdminData.allItems[idx] = result;
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    window.storesAdminDeactivateItem = async function (id) {
        showSplash();
        try {
            await fetchPost('stores/items/deactivate/' + id, {});
            window.storesAdminData.allItems = window.storesAdminData.allItems.filter(function (i) {
                return i.id !== id;
            });
            hideSplash();
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── 4. RESTOCK ────────────────────────────────────────────────────────────

    /** req: { storeItemId, quantityToAdd, performedBy, notes } */
    window.storesAdminRestock = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('stores/items/restock', Object.assign({
                institutionCode: _inst,
            }, req));
            var idx = window.storesAdminData.allItems.findIndex(function (i) {
                return i.id === result.id;
            });
            if (idx >= 0) window.storesAdminData.allItems[idx] = result;
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── 5. STOCK MOVEMENT AUDIT TRAIL ────────────────────────────────────────

    window.storesAdminFetchMovements = async function () {
        try {
            var result = await fetchPost('stores/movements/' + _inst, {});
            window.storesAdminData.movements = Array.isArray(result) ? result : [];
            return window.storesAdminData.movements;
        } catch (e) {
            return [];
        }
    };

    window.storesAdminFetchMovementsByItem = async function (storeItemId) {
        try {
            var result = await fetchPost('stores/movements/item/' + storeItemId, {});
            return Array.isArray(result) ? result : [];
        } catch (e) {
            return [];
        }
    };

    // ── 6. FILTER (local, no server call) ────────────────────────────────────
    window.storesAdminFilter = function () {
        var items = window.storesAdminData.allItems;
        var type = window.storesAdminState.selectedType;
        var q = (window.storesAdminState.searchQuery || '').toLowerCase().trim();

        return items.filter(function (i) {
            if (type && i.itemType !== type) return false;
            if (q) {
                var hay = [i.itemName, i.itemCode, i.description].join(' ').toLowerCase();
                if (!hay.includes(q)) return false;
            }
            return true;
        });
    };

    // ── 7. SUMMARY ────────────────────────────────────────────────────────────
    window.storesAdminSummary = function (items) {
        var out = {totalItems: items.length, totalUnits: 0, totalValue: 0, lowStockCount: 0, outOfStockCount: 0};
        items.forEach(function (i) {
            out.totalUnits += (i.quantityInStock || 0);
            out.totalValue += (i.quantityInStock || 0) * (i.unitPrice || 0);
            if (i.quantityInStock <= 0) out.outOfStockCount++;
            else if (i.lowStock) out.lowStockCount++;
        });
        return out;
    };

    // ── 8. FORMAT HELPERS — reuse storesFmt if loaded, else define our own ───
    window.storesAdminFmt = window.storesFmt || {
        money: function (v) {
            return 'GH₵ ' + (parseFloat(v) || 0).toLocaleString('en-GH', {minimumFractionDigits: 2});
        },
        date: function (d) {
            return d ? new Date(d).toLocaleDateString('en-GB', {day: '2-digit', month: 'short', year: 'numeric'}) : '—';
        },
        dateTime: function (d) {
            return d ? new Date(d).toLocaleString('en-GB', {
                day: '2-digit', month: 'short', year: 'numeric',
                hour: '2-digit', minute: '2-digit',
            }) : '—';
        },
    };

    if (window.copyrights) window.copyrights();
    storesAdminLoad();

})();
