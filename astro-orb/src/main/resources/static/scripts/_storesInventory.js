/**
 * _storesInventory.js  —  Data & logic layer for the Stores & Inventory module.
 *
 * Loaded at runtime by storesInventory.js (UI renderer), following the same
 * two-file pattern as _financeBilling.js / _financeFeeCollection.js / _financeLedgers.js.
 *
 * Covers both purchase channels:
 *   - ONLINE  : applicant/student storefront checkout (Paystack)
 *   - INSTORE : staff POS — cash / MoMo / bank sale
 *
 * Backend calls all go through the existing fetchPost('<route>', body) helper,
 * which is proxied by astro-orb's StoresController to the stores-inventory
 * microservice (POST /api/stores/**).
 *
 * Exposes on window:
 *   storesState / storesData
 *   storesLoad()                       — initial catalogue load
 *   storesFetchOnlineItems()           — GET items/online/{inst}
 *   storesFetchInstoreItems()          — GET items/instore/{inst}
 *   storesFetchLowStock()              — GET items/low-stock/{inst}
 *   storesCreateItem(req)              — admin: create item
 *   storesRestock(req)                 — admin: restock item
 *   storesPlaceOrder(req)              — place order (online or in-store)
 *   storesConfirmOrder(orderId, ref)   — confirm payment
 *   storesFulfilOrder(orderId, by)     — fulfil → stock + ledger
 *   storesCancelOrder(orderId, reason)
 *   storesFetchOrdersByInstitution(opts)
 *   storesFetchOrdersByBuyer(buyerId)
 *   storesPaystack(opts)               — Paystack checkout for online orders
 *   storesCart helpers (add/remove/total/clear)
 *   storesFmt                          — shared formatters
 */
(function () {
    'use strict';

    var _raw = (typeof instId !== 'undefined' ? instId : '').split(',')[0];
    var _inst = _raw.replace(/[\[\]']+/g, '').replace(/\//g, '');

    // ── STATE ────────────────────────────────────────────────────────────────
    window.storesState = {
        institutionCode: _inst,
        channel: 'ONLINE',          // ONLINE | INSTORE — current storefront mode
        cart: {},                    // storeItemId → { item, quantity }
        selectedBuyer: null,         // { id, name, contact } — set by staff for in-store sales
        paymentMethod: 'ONLINE',     // ONLINE | CASH | MOMO | BANK
        _loaded: false,
    };

    window.storesData = {
        onlineItems: [],
        instoreItems: [],
        lowStockItems: [],
        orders: [],
        myOrders: [],
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
    window.storesLoad = async function () {
        try {
            showSplash();
            var [online, instore, lowStock] = await Promise.all([
                fetchPost('stores/items/online/' + _inst, {}),
                fetchPost('stores/items/instore/' + _inst, {}),
                fetchPost('stores/items/low-stock/' + _inst, {}),
            ]);
            window.storesData.onlineItems = Array.isArray(online) ? online : [];
            window.storesData.instoreItems = Array.isArray(instore) ? instore : [];
            window.storesData.lowStockItems = Array.isArray(lowStock) ? lowStock : [];
            window.storesState._loaded = true;
            hideSplash();
        } catch (e) {
            hideSplash();
            window.storesState._loaded = true;
            console.error('[_storesInventory] load error:', e);
        }
    };

    // ── 2. ITEM FETCH (granular re-fetches) ─────────────────────────────────
    window.storesFetchOnlineItems = async function () {
        try {
            var result = await fetchPost('stores/items/online/' + _inst, {});
            window.storesData.onlineItems = Array.isArray(result) ? result : [];
            return window.storesData.onlineItems;
        } catch (e) {
            return [];
        }
    };

    window.storesFetchInstoreItems = async function () {
        try {
            var result = await fetchPost('stores/items/instore/' + _inst, {});
            window.storesData.instoreItems = Array.isArray(result) ? result : [];
            return window.storesData.instoreItems;
        } catch (e) {
            return [];
        }
    };

    window.storesFetchLowStock = async function () {
        try {
            var result = await fetchPost('stores/items/low-stock/' + _inst, {});
            window.storesData.lowStockItems = Array.isArray(result) ? result : [];
            return window.storesData.lowStockItems;
        } catch (e) {
            return [];
        }
    };

    /** All items for an institution — admin/inventory management view */
    window.storesFetchAllItems = async function (itemType) {
        try {
            var result = await fetchPost('stores/items/get-by-institution', {
                institutionCode: _inst,
                itemType: itemType || null,
            });
            return Array.isArray(result) ? result : [];
        } catch (e) {
            return [];
        }
    };

    // ── 3. ADMIN: CREATE / RESTOCK ───────────────────────────────────────────
    window.storesCreateItem = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('stores/items/create', Object.assign({
                institutionCode: _inst,
            }, req));
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    window.storesRestock = async function (req) {
        showSplash();
        try {
            var result = await fetchPost('stores/items/restock', Object.assign({
                institutionCode: _inst,
            }, req));
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── 4. CART HELPERS (local, no server call) ─────────────────────────────
    window.storesCartAdd = function (item, quantity) {
        var qty = Math.max(1, parseInt(quantity, 10) || 1);
        var existing = window.storesState.cart[item.id];
        var newQty = (existing ? existing.quantity : 0) + qty;

        if (newQty > item.quantityInStock) {
            newQty = item.quantityInStock;
        }
        if (newQty <= 0) return;

        window.storesState.cart[item.id] = {item: item, quantity: newQty};
    };

    window.storesCartSetQuantity = function (itemId, quantity) {
        var entry = window.storesState.cart[itemId];
        if (!entry) return;
        var qty = parseInt(quantity, 10) || 0;
        if (qty <= 0) {
            delete window.storesState.cart[itemId];
            return;
        }
        entry.quantity = Math.min(qty, entry.item.quantityInStock);
    };

    window.storesCartRemove = function (itemId) {
        delete window.storesState.cart[itemId];
    };

    window.storesCartClear = function () {
        window.storesState.cart = {};
    };

    window.storesCartItems = function () {
        return Object.values(window.storesState.cart);
    };

    window.storesCartTotal = function () {
        var total = 0;
        Object.values(window.storesState.cart).forEach(function (e) {
            total += (e.item.unitPrice || 0) * e.quantity;
        });
        return total;
    };

    window.storesCartCount = function () {
        var n = 0;
        Object.values(window.storesState.cart).forEach(function (e) {
            n += e.quantity;
        });
        return n;
    };

    // ── 5. ORDER PLACEMENT ───────────────────────────────────────────────────

    /**
     * Builds the OrderRequest payload from the current cart + buyer + channel.
     * buyer: { id, name, contact }
     */
    window.storesBuildOrderRequest = function (buyer, opts) {
        opts = opts || {};
        var lineItems = window.storesCartItems().map(function (e) {
            return {storeItemId: e.item.id, quantity: e.quantity};
        });

        return {
            institutionCode: _inst,
            buyerId: buyer.id,
            buyerName: buyer.name,
            buyerContact: buyer.contact || '',
            channel: window.storesState.channel,
            lineItems: lineItems,
            paymentMethod: opts.paymentMethod || window.storesState.paymentMethod,
            paymentReference: opts.paymentReference || '',
            paymentConfirmed: !!opts.paymentConfirmed,
            processedBy: opts.processedBy || '',
        };
    };

    /** POST /stores/orders/place — returns StoreOrderResponse (status PENDING|CONFIRMED) */
    window.storesPlaceOrder = async function (orderRequest) {
        showSplash();
        try {
            var result = await fetchPost('stores/orders/place', orderRequest);
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    /** POST /stores/orders/{id}/confirm?paymentReference=... */
    window.storesConfirmOrder = async function (orderId, paymentReference) {
        showSplash();
        try {
            var result = await fetchPost(
                'stores/orders/' + orderId + '/confirm?paymentReference=' + encodeURIComponent(paymentReference || ''),
                {}
            );
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    /**
     * POST /stores/orders/{id}/fulfil?processedBy=...
     * Deducts stock and posts the sale to the Finance ledger automatically.
     */
    window.storesFulfilOrder = async function (orderId, processedBy) {
        showSplash();
        try {
            var result = await fetchPost(
                'stores/orders/' + orderId + '/fulfil?processedBy=' + encodeURIComponent(processedBy || 'SYSTEM'),
                {}
            );
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    window.storesCancelOrder = async function (orderId, reason) {
        showSplash();
        try {
            var result = await fetchPost(
                'stores/orders/' + orderId + '/cancel' + (reason ? ('?reason=' + encodeURIComponent(reason)) : ''),
                {}
            );
            hideSplash();
            return result;
        } catch (e) {
            hideSplash();
            throw e;
        }
    };

    // ── 6. ORDER QUERIES ─────────────────────────────────────────────────────

    /** opts: { status, channel } */
    window.storesFetchOrdersByInstitution = async function (opts) {
        opts = opts || {};
        try {
            var result = await fetchPost('stores/orders/get-by-institution', {
                institutionCode: _inst,
                status: opts.status || null,
                channel: opts.channel || null,
            });
            window.storesData.orders = Array.isArray(result) ? result : [];
            return window.storesData.orders;
        } catch (e) {
            return [];
        }
    };

    window.storesFetchOrdersByBuyer = async function (buyerId) {
        try {
            var result = await fetchPost('stores/orders/buyer/' + _inst + '/' + buyerId, {});
            window.storesData.myOrders = Array.isArray(result) ? result : [];
            return window.storesData.myOrders;
        } catch (e) {
            return [];
        }
    };

    window.storesFetchOrderByRef = async function (orderRef) {
        try {
            return await fetchPost('stores/orders/ref/' + orderRef, {});
        } catch (e) {
            return null;
        }
    };

    // ── 7. PAYSTACK ONLINE CHECKOUT ──────────────────────────────────────────
    /**
     * opts: { email, amountGhs, buyerId, buyerName, onSuccess, onClose }
     * Same SDK usage as window.feeCollPaystack — keeps Paystack integration
     * consistent across modules.
     */
    window.storesPaystack = function (opts) {
        if (typeof PaystackPop === 'undefined') {
            alert('Paystack SDK not loaded. Check internet connection.');
            return;
        }
        var handler = PaystackPop.setup({
            key: window.PAYSTACK_PUBLIC_KEY || 'pk_test_957a501ee4935ea125978771108f5aba4ad53acf',
            email: opts.email || 'applicant@astromyllc.com',
            amount: Math.round((opts.amountGhs || 0) * 100),
            currency: 'GHS',
            ref: 'STORE_' + (opts.buyerId || 'GUEST') + '_' + Date.now(),
            metadata: {
                custom_fields: [
                    {display_name: opts.buyerName || '', variable_name: 'buyer', value: _inst},
                ],
            },
            callback: function (res) {
                if (opts.onSuccess) opts.onSuccess(res.reference);
            },
            onClose: function () {
                if (opts.onClose) opts.onClose();
            },
        });
        handler.openIframe();
    };

    // ── 8. FORMAT HELPERS (shared across UI) ────────────────────────────────
    window.storesFmt = {
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
        statusBadge: function (status) {
            var map = {
                PENDING: 'badge-warning',
                CONFIRMED: 'badge-info',
                FULFILLED: 'badge-success',
                CANCELLED: 'badge-danger',
            };
            return map[status] || 'badge-default';
        },
    };

    if (window.copyrights) window.copyrights();
    storesLoad();

})();
