/**
 * storesInventory.js  —  UI renderer for the Stores & Inventory module.
 *
 * This file loads first (registered by the host page). It dynamically loads
 * _storesInventory.js at runtime — same two-file pattern as financeBilling.js /
 * financeFeeCollection.js / financeLedger.js.
 *
 * Provides:
 *   - Catalogue grid (ONLINE storefront / INSTORE POS, switchable)
 *   - Cart with quantity controls + running total
 *   - Checkout:
 *       ONLINE  → place order (PENDING) → Paystack → confirm → fulfil
 *       INSTORE → buyer lookup, payment method, place (auto-CONFIRMED for CASH) → fulfil
 *   - Order history (admin: all orders; applicant/student: "my orders")
 *   - Low-stock alert strip (admin)
 *   - Receipt modal (print / download) after fulfilment
 */
(function () {
    'use strict';

    // ─── RESOLVE BASE PATH ──────────────────────────────────────────────────
    var _scriptBase = (function () {
        var el = document.currentScript ||
            (function () {
                var tags = document.getElementsByTagName('script');
                for (var i = tags.length - 1; i >= 0; i--) {
                    if (tags[i].src && tags[i].src.indexOf('storesInventory') !== -1) {
                        return tags[i];
                    }
                }
                return null;
            })();
        if (!el || !el.src) return '';
        return el.src.substring(0, el.src.lastIndexOf('/') + 1);
    })();

    // ─── INJECT CSS / FONT AWESOME ──────────────────────────────────────────
    (function injectCSS() {
        if (document.getElementById('storesInventoryCSS')) return;
        var link = document.createElement('link');
        link.id = 'storesInventoryCSS';
        link.rel = 'stylesheet';
        link.href = _scriptBase + '../../styles/style.css';
        document.head.appendChild(link);
    })();

    if (!document.querySelector('link[href*="font-awesome"], link[href*="fontawesome"]')) {
        var fa = document.createElement('link');
        fa.rel = 'stylesheet';
        fa.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
        document.head.appendChild(fa);
    }

    // ─── ITEM TYPE META ──────────────────────────────────────────────────────
    var TYPE_META = {
        PROSPECTUS: {icon: 'fa-file-alt', label: 'Prospectus'},
        UNIFORM: {icon: 'fa-tshirt', label: 'Uniform'},
        STATIONERY: {icon: 'fa-pencil-alt', label: 'Stationery'},
        OTHER: {icon: 'fa-box', label: 'Other'},
    };

    // ─── BUILD DOM ────────────────────────────────────────────────────────────
    function buildDOM() {
        document.getElementById('wrapper').innerHTML = '<div class="si-page">' + [

            // ── HEADER ──────────────────────────────────────────────────────
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-store"></i> Stores &amp; Inventory</h1>',
            '<p>Purchase prospectus, uniforms and stationery — online or in-store</p></div>',
            '<div class="ph-header-actions">',
            '<div class="si-channel-toggle">',
            '<button class="si-channel-btn active" id="channelOnline" data-channel="ONLINE">',
            '<i class="fas fa-globe"></i> Online</button>',
            '<button class="si-channel-btn" id="channelInstore" data-channel="INSTORE">',
            '<i class="fas fa-cash-register"></i> In-Store</button>',
            '</div>',
            '<button class="fc-btn fc-btn-secondary" id="siRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '<button class="fc-btn fc-btn-secondary" id="siHistoryBtn"><i class="fas fa-history"></i> Order History</button>',
            '</div>',
            '</header>',

            // ── LOW STOCK STRIP (admin) ───────────────────────────────────────
            '<div class="si-lowstock-strip" id="siLowStockStrip" style="display:none;"></div>',

            // ── MAIN GRID ────────────────────────────────────────────────────
            '<div class="si-grid">',

            // Catalogue panel
            '<div class="panel si-catalogue-panel">',
            '<div class="panel-head">',
            '<span><i class="fas fa-th-large"></i> Catalogue</span>',
            '<select id="siTypeFilter">',
            '<option value="">All Items</option>',
            '<option value="PROSPECTUS">Prospectus</option>',
            '<option value="UNIFORM">Uniform</option>',
            '<option value="STATIONERY">Stationery</option>',
            '<option value="OTHER">Other</option>',
            '</select>',
            '</div>',
            '<div class="panel-body">',
            '<div class="si-catalogue-grid" id="siCatalogueGrid">',
            '<div class="loading-state"><i class="fas fa-circle-notch fa-spin"></i> Loading items…</div>',
            '</div>',
            '</div>',
            '</div>',

            // Cart / Checkout panel
            '<div class="panel si-cart-panel">',
            '<div class="panel-head">',
            '<span><i class="fas fa-shopping-cart"></i> Cart</span>',
            '<span class="bill-tally" id="siCartCount">0 items</span>',
            '</div>',
            '<div class="panel-body">',

            // Buyer info (shown for in-store; auto-filled for online from session)
            '<div class="si-buyer-box" id="siBuyerBox">',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Buyer ID <span class="req">*</span></label>',
            '<input type="text" id="siBuyerId" placeholder="Applicant / Student ID"></div>',
            '<div class="fc-field"><label>Buyer Name <span class="req">*</span></label>',
            '<input type="text" id="siBuyerName" placeholder="Full name"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Contact (email/phone)</label>',
            '<input type="text" id="siBuyerContact" placeholder="For receipt"></div>',
            '<div class="fc-field" id="siPaymentMethodField">',
            '<label>Payment Method</label>',
            '<select id="siPaymentMethod">',
            '<option value="ONLINE">Online (Paystack)</option>',
            '<option value="CASH">Cash</option>',
            '<option value="MOMO">Mobile Money</option>',
            '<option value="BANK">Bank Transfer</option>',
            '</select>',
            '</div>',
            '</div>',
            '</div>',

            // Cart items
            '<div class="si-cart-items" id="siCartItems">',
            '<div class="empty-state"><i class="fas fa-shopping-basket"></i> Your cart is empty</div>',
            '</div>',

            // Total + checkout
            '<div class="si-cart-summary" id="siCartSummary"></div>',
            '<button class="process-btn" id="siCheckoutBtn" disabled>',
            '<i class="fas fa-check-circle"></i> <span id="siCheckoutLabel">Checkout</span>',
            '</button>',

            '</div>',
            '</div>',

            '</div>', // end si-grid

            // ── ORDER HISTORY MODAL ────────────────────────────────────────────
            '<div class="fc-modal-overlay" id="siHistoryModal">',
            '<div class="fc-modal fc-modal-lg">',
            '<div class="fc-modal-head"><h5><i class="fas fa-history"></i> Order History</h5>',
            '<button class="fc-modal-close" id="siHistoryClose">&times;</button></div>',
            '<div class="fc-modal-body" id="siHistoryBody"></div>',
            '<div class="fc-modal-foot"><button class="fc-btn fc-btn-secondary" id="siHistoryCloseBtn">Close</button></div>',
            '</div></div>',

            // ── RECEIPT MODAL ───────────────────────────────────────────────────
            '<div class="fc-modal-overlay" id="siReceiptModal">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head"><h5><i class="fas fa-receipt"></i> Receipt</h5>',
            '<button class="fc-modal-close" id="siReceiptClose">&times;</button></div>',
            '<div class="fc-modal-body" id="siReceiptBody"></div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="siReceiptCloseBtn">Close</button>',
            '<button class="fc-btn fc-btn-success" id="siReceiptPrintBtn"><i class="fas fa-print"></i> Print</button>',
            '</div>',
            '</div></div>',

            '<div class="fc-toast" id="siToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="siYear"></span> | Stores &amp; Inventory</footer>',

            '</div>', // end si-page
        ].join('');

        document.getElementById('siYear').textContent = new Date().getFullYear();

        // Pre-fill buyer fields from logged-in applicant/student context if present
        var loggedInId = (typeof userId !== 'undefined' && userId) ? userId : '';
        var loggedInName = (typeof userName !== 'undefined' && userName) ? userName : '';
        if (loggedInId) document.getElementById('siBuyerId').value = loggedInId;
        if (loggedInName) document.getElementById('siBuyerName').value = loggedInName;
    }

    // ─── RENDER: CATALOGUE ───────────────────────────────────────────────────
    function renderCatalogue() {
        var grid = document.getElementById('siCatalogueGrid');
        if (!grid) return;

        var channel = window.storesState.channel;
        var items = channel === 'ONLINE'
            ? window.storesData.onlineItems
            : window.storesData.instoreItems;

        var typeFilter = document.getElementById('siTypeFilter').value;
        if (typeFilter) {
            items = items.filter(function (i) {
                return i.itemType === typeFilter;
            });
        }

        if (!items.length) {
            grid.innerHTML = '<div class="empty-state"><i class="fas fa-box-open"></i> No items available' +
                (typeFilter ? ' for this category' : '') + '.</div>';
            return;
        }

        var fmt = window.storesFmt;
        grid.innerHTML = items.map(function (item) {
            var meta = TYPE_META[item.itemType] || TYPE_META.OTHER;
            var outOfStock = item.quantityInStock <= 0;
            var lowStock = item.lowStock && !outOfStock;
            var inCart = window.storesState.cart[item.id];

            return '<div class="si-item-card' + (outOfStock ? ' is-out' : '') + '">' +
                '<div class="si-item-icon"><i class="fas ' + meta.icon + '"></i></div>' +
                '<div class="si-item-body">' +
                '<h4>' + item.itemName + '</h4>' +
                '<p class="si-item-desc">' + (item.description || meta.label) + '</p>' +
                '<div class="si-item-meta">' +
                '<span class="si-item-price">' + fmt.money(item.unitPrice) + '</span>' +
                (outOfStock
                    ? '<span class="badge badge-danger">Out of stock</span>'
                    : lowStock
                        ? '<span class="badge badge-warning">Low stock: ' + item.quantityInStock + '</span>'
                        : '<span class="badge badge-success">' + item.quantityInStock + ' in stock</span>') +
                '</div>' +
                '</div>' +
                '<div class="si-item-actions">' +
                (outOfStock
                    ? '<button class="fc-btn fc-btn-secondary" disabled>Unavailable</button>'
                    : inCart
                        ? '<div class="si-qty-control">' +
                          '<button class="si-qty-btn" data-action="dec" data-id="' + item.id + '">−</button>' +
                          '<span class="si-qty-val">' + inCart.quantity + '</span>' +
                          '<button class="si-qty-btn" data-action="inc" data-id="' + item.id + '">+</button>' +
                          '</div>'
                        : '<button class="fc-btn fc-btn-primary si-add-btn" data-id="' + item.id + '">' +
                          '<i class="fas fa-cart-plus"></i> Add</button>') +
                '</div>' +
                '</div>';
        }).join('');

        // Wire add-to-cart
        grid.querySelectorAll('.si-add-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var item = findItem(this.dataset.id);
                if (!item) return;
                window.storesCartAdd(item, 1);
                renderCatalogue();
                renderCart();
            });
        });

        // Wire quantity +/-
        grid.querySelectorAll('.si-qty-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var id = this.dataset.id;
                var entry = window.storesState.cart[id];
                if (!entry) return;
                var delta = this.dataset.action === 'inc' ? 1 : -1;
                window.storesCartSetQuantity(id, entry.quantity + delta);
                renderCatalogue();
                renderCart();
            });
        });
    }

    function findItem(id) {
        var all = window.storesData.onlineItems.concat(window.storesData.instoreItems);
        return all.find(function (i) {
            return i.id === id;
        });
    }

    // ─── RENDER: CART ────────────────────────────────────────────────────────
    function renderCart() {
        var container = document.getElementById('siCartItems');
        var entries = window.storesCartItems();
        var fmt = window.storesFmt;

        document.getElementById('siCartCount').textContent =
            window.storesCartCount() + (window.storesCartCount() === 1 ? ' item' : ' items');

        if (!entries.length) {
            container.innerHTML = '<div class="empty-state"><i class="fas fa-shopping-basket"></i> Your cart is empty</div>';
        } else {
            container.innerHTML = entries.map(function (e) {
                return '<div class="si-cart-row">' +
                    '<div class="si-cart-row-name">' + e.item.itemName +
                    '<small> x' + e.quantity + '</small></div>' +
                    '<div class="si-cart-row-amount">' + fmt.money(e.item.unitPrice * e.quantity) + '</div>' +
                    '<button class="fc-btn-icon si-cart-remove" data-id="' + e.item.id + '" title="Remove">' +
                    '<i class="fas fa-times"></i></button>' +
                    '</div>';
            }).join('');

            container.querySelectorAll('.si-cart-remove').forEach(function (btn) {
                btn.addEventListener('click', function () {
                    window.storesCartRemove(this.dataset.id);
                    renderCatalogue();
                    renderCart();
                });
            });
        }

        renderCartSummary();
        updateCheckoutButton();
    }

    function renderCartSummary() {
        var el = document.getElementById('siCartSummary');
        var total = window.storesCartTotal();
        el.innerHTML = '<div class="summary-figures">' +
            '<div class="summary-item">' +
            '<div class="summary-label">Items</div>' +
            '<div class="summary-value">' + window.storesCartCount() + '</div>' +
            '</div>' +
            '<div class="summary-item">' +
            '<div class="summary-label">Total</div>' +
            '<div class="summary-value">' + window.storesFmt.money(total) + '</div>' +
            '</div>' +
            '</div>';
    }

    function updateCheckoutButton() {
        var btn = document.getElementById('siCheckoutBtn');
        var label = document.getElementById('siCheckoutLabel');
        var hasItems = window.storesCartCount() > 0;
        var paymentMethod = document.getElementById('siPaymentMethod').value;
        var channel = window.storesState.channel;

        btn.disabled = !hasItems;

        if (channel === 'ONLINE' && paymentMethod === 'ONLINE') {
            label.textContent = 'Pay with Paystack — ' + window.storesFmt.money(window.storesCartTotal());
        } else {
            label.textContent = 'Complete Sale — ' + window.storesFmt.money(window.storesCartTotal());
        }
    }

    // ─── RENDER: LOW STOCK STRIP ─────────────────────────────────────────────
    function renderLowStock() {
        var strip = document.getElementById('siLowStockStrip');
        var items = window.storesData.lowStockItems;
        if (!items.length) {
            strip.style.display = 'none';
            return;
        }
        strip.style.display = 'block';
        strip.innerHTML = '<i class="fas fa-exclamation-triangle"></i> <strong>Low stock alert:</strong> ' +
            items.map(function (i) {
                return i.itemName + ' (' + i.quantityInStock + ' left)';
            }).join(', ');
    }

    // ─── CHANNEL SWITCH ──────────────────────────────────────────────────────
    function switchChannel(channel) {
        window.storesState.channel = channel;
        document.getElementById('channelOnline').classList.toggle('active', channel === 'ONLINE');
        document.getElementById('channelInstore').classList.toggle('active', channel === 'INSTORE');

        // Payment method defaults + buyer box behaviour
        var pmSel = document.getElementById('siPaymentMethod');
        var onlineOpt = pmSel.querySelector('option[value="ONLINE"]');

        if (channel === 'ONLINE') {
            if (onlineOpt) onlineOpt.style.display = '';
            pmSel.value = 'ONLINE';
            window.storesState.paymentMethod = 'ONLINE';
        } else {
            if (onlineOpt) onlineOpt.style.display = 'none';
            pmSel.value = 'CASH';
            window.storesState.paymentMethod = 'CASH';
        }

        renderCatalogue();
        renderCart();
    }

    // ─── CHECKOUT FLOW ───────────────────────────────────────────────────────
    async function handleCheckout() {
        var buyerId = document.getElementById('siBuyerId').value.trim();
        var buyerName = document.getElementById('siBuyerName').value.trim();
        var buyerContact = document.getElementById('siBuyerContact').value.trim();
        var paymentMethod = document.getElementById('siPaymentMethod').value;

        if (!buyerId || !buyerName) {
            toast('Buyer ID and Name are required.', 'error');
            return;
        }
        if (!window.storesCartCount()) {
            toast('Cart is empty.', 'error');
            return;
        }

        window.storesState.paymentMethod = paymentMethod;
        var buyer = {id: buyerId, name: buyerName, contact: buyerContact};

        try {
            if (window.storesState.channel === 'ONLINE' && paymentMethod === 'ONLINE') {
                await checkoutOnlinePaystack(buyer);
            } else {
                await checkoutDirect(buyer, paymentMethod);
            }
        } catch (e) {
            toast('Checkout failed: ' + (e.message || e), 'error');
        }
    }

    /** Online checkout: place PENDING order → Paystack → confirm → fulfil */
    async function checkoutOnlinePaystack(buyer) {
        var orderReq = window.storesBuildOrderRequest(buyer, {paymentMethod: 'ONLINE'});
        var order = await window.storesPlaceOrder(orderReq);

        window.storesPaystack({
            email: buyer.contact || 'applicant@astromyllc.com',
            amountGhs: order.totalAmount,
            buyerId: buyer.id,
            buyerName: buyer.name,
            onSuccess: async function (reference) {
                try {
                    await window.storesConfirmOrder(order.id, reference);
                    var fulfilled = await window.storesFulfilOrder(order.id, buyer.id);
                    onOrderFulfilled(fulfilled);
                } catch (e) {
                    toast('Payment received but order processing failed. Contact support with ref ' + reference, 'error');
                }
            },
            onClose: function () {
                toast('Payment cancelled. Your order is saved as pending — ref ' + order.orderRef, 'warning');
            },
        });
    }

    /** In-store / non-Paystack checkout: place (auto-confirmed for CASH) → fulfil */
    async function checkoutDirect(buyer, paymentMethod) {
        var processedBy = (typeof staffId !== 'undefined' && staffId) ? staffId : buyer.id;

        var orderReq = window.storesBuildOrderRequest(buyer, {
            paymentMethod: paymentMethod,
            processedBy: processedBy,
            paymentConfirmed: paymentMethod === 'CASH',
            paymentReference: paymentMethod === 'CASH' ? '' : ('REF-' + Date.now()),
        });

        var order = await window.storesPlaceOrder(orderReq);

        // CASH in-store orders come back CONFIRMED already; others need explicit confirm
        if (order.status === 'PENDING') {
            order = await window.storesConfirmOrder(order.id, orderReq.paymentReference);
        }

        var fulfilled = await window.storesFulfilOrder(order.id, processedBy);
        onOrderFulfilled(fulfilled);
    }

    function onOrderFulfilled(order) {
        window.storesCartClear();
        renderCart();
        showReceipt(order);

        // Refresh catalogue + low stock since quantities just changed
        Promise.all([
            window.storesFetchOnlineItems(),
            window.storesFetchInstoreItems(),
            window.storesFetchLowStock(),
        ]).then(function () {
            renderCatalogue();
            renderLowStock();
        });

        toast('Order ' + order.orderRef + ' completed.', 'success');
    }

    // ─── RECEIPT MODAL ───────────────────────────────────────────────────────
    function showReceipt(order) {
        var fmt = window.storesFmt;
        var rows = (order.lineItems || []).map(function (li) {
            return '<tr><td>' + li.itemName + '</td>' +
                '<td class="text-right">' + li.quantity + '</td>' +
                '<td class="text-right">' + fmt.money(li.unitPrice) + '</td>' +
                '<td class="text-right">' + fmt.money(li.lineTotal) + '</td></tr>';
        }).join('');

        document.getElementById('siReceiptBody').innerHTML =
            '<div class="invoice-meta">' +
            '<strong>Order Ref:</strong> ' + order.orderRef + '<br>' +
            '<strong>Buyer:</strong> ' + order.buyerName + ' (' + order.buyerId + ')<br>' +
            '<strong>Channel:</strong> ' + order.channel + ' &nbsp;|&nbsp; ' +
            '<strong>Payment:</strong> ' + order.paymentMethod + '<br>' +
            '<strong>Date:</strong> ' + fmt.dateTime(order.fulfilledDate || order.orderDate) + '<br>' +
            '<strong>Status:</strong> <span class="badge ' + fmt.statusBadge(order.status) + '">' + order.status + '</span>' +
            (order.ledgerPosted
                ? ' <span class="badge badge-success">Ledger posted</span>'
                : ' <span class="badge badge-warning">Ledger pending</span>') +
            '</div>' +
            '<table class="inv-table">' +
            '<thead><tr><th>Item</th><th class="text-right">Qty</th><th class="text-right">Unit Price</th><th class="text-right">Total</th></tr></thead>' +
            '<tbody>' + rows + '</tbody>' +
            '<tfoot><tr><td colspan="3"><strong>Grand Total</strong></td>' +
            '<td class="text-right"><strong>' + fmt.money(order.totalAmount) + '</strong></td></tr></tfoot>' +
            '</table>';

        document.getElementById('siReceiptModal').classList.add('open');
    }

    // ─── ORDER HISTORY MODAL ─────────────────────────────────────────────────
    async function showHistory() {
        document.getElementById('siHistoryBody').innerHTML =
            '<div class="loading-state"><i class="fas fa-circle-notch fa-spin"></i> Loading orders…</div>';
        document.getElementById('siHistoryModal').classList.add('open');

        var buyerId = document.getElementById('siBuyerId').value.trim();
        var isStaffView = (typeof staffId !== 'undefined' && staffId);

        var orders = isStaffView
            ? await window.storesFetchOrdersByInstitution({})
            : await window.storesFetchOrdersByBuyer(buyerId);

        renderHistory(orders);
    }

    function renderHistory(orders) {
        var fmt = window.storesFmt;
        if (!orders.length) {
            document.getElementById('siHistoryBody').innerHTML =
                '<div class="empty-state"><i class="fas fa-inbox"></i> No orders found.</div>';
            return;
        }

        var rows = orders.map(function (o) {
            return '<tr>' +
                '<td><code>' + o.orderRef + '</code></td>' +
                '<td>' + o.buyerName + '</td>' +
                '<td>' + o.channel + '</td>' +
                '<td class="text-right">' + fmt.money(o.totalAmount) + '</td>' +
                '<td><span class="badge ' + fmt.statusBadge(o.status) + '">' + o.status + '</span></td>' +
                '<td>' + (o.ledgerPosted ? '<i class="fas fa-check-circle" style="color:#28a745"></i>' : '<i class="fas fa-clock" style="color:#f0ad4e"></i>') + '</td>' +
                '<td>' + fmt.date(o.orderDate) + '</td>' +
                '</tr>';
        }).join('');

        document.getElementById('siHistoryBody').innerHTML =
            '<table class="fc-table">' +
            '<thead><tr><th>Ref</th><th>Buyer</th><th>Channel</th><th>Total</th><th>Status</th><th>Ledger</th><th>Date</th></tr></thead>' +
            '<tbody>' + rows + '</tbody></table>';
    }

    // ─── TOAST ───────────────────────────────────────────────────────────────
    function toast(msg, type) {
        var el = document.getElementById('siToast');
        if (!el) return;
        el.textContent = msg;
        el.className = 'fc-toast show' + (type ? (' fc-toast-' + type) : '');
        setTimeout(function () {
            el.classList.remove('show');
        }, 3500);
    }

    // ─── PRINT ───────────────────────────────────────────────────────────────
    function printReceipt() {
        var content = document.getElementById('siReceiptBody');
        if (!content) return;
        var win = window.open('', '_blank');
        win.document.write(
            '<!DOCTYPE html><html><head><title>Receipt</title>' +
            '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">' +
            '<style>' +
            'body{font-family:sans-serif;padding:28px;font-size:14px;}' +
            'table{width:100%;border-collapse:collapse;margin-top:14px;}' +
            'th{background:#2f54d4;color:#fff;padding:8px;text-align:left;font-size:13px;}' +
            'td{padding:8px;border-bottom:1px solid #eee;}' +
            '.text-right{text-align:right;}' +
            '.badge{display:inline-block;padding:2px 8px;border-radius:4px;font-size:11px;color:#fff;}' +
            '</style></head><body>' + content.innerHTML + '</body></html>'
        );
        win.document.close();
        win.print();
    }

    // ─── WIRE EVENTS ─────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('channelOnline').addEventListener('click', function () {
            switchChannel('ONLINE');
        });
        document.getElementById('channelInstore').addEventListener('click', function () {
            switchChannel('INSTORE');
        });

        document.getElementById('siTypeFilter').addEventListener('change', renderCatalogue);

        document.getElementById('siPaymentMethod').addEventListener('change', function () {
            window.storesState.paymentMethod = this.value;
            updateCheckoutButton();
        });

        document.getElementById('siCheckoutBtn').addEventListener('click', handleCheckout);

        document.getElementById('siRefreshBtn').addEventListener('click', async function () {
            await window.storesLoad();
            renderCatalogue();
            renderCart();
            renderLowStock();
            toast('Refreshed.', 'success');
        });

        document.getElementById('siHistoryBtn').addEventListener('click', showHistory);
        document.getElementById('siHistoryClose').addEventListener('click', closeHistory);
        document.getElementById('siHistoryCloseBtn').addEventListener('click', closeHistory);
        document.getElementById('siHistoryModal').addEventListener('click', function (e) {
            if (e.target === e.currentTarget) closeHistory();
        });

        document.getElementById('siReceiptClose').addEventListener('click', closeReceipt);
        document.getElementById('siReceiptCloseBtn').addEventListener('click', closeReceipt);
        document.getElementById('siReceiptPrintBtn').addEventListener('click', printReceipt);
        document.getElementById('siReceiptModal').addEventListener('click', function (e) {
            if (e.target === e.currentTarget) closeReceipt();
        });
    }

    function closeHistory() {
        document.getElementById('siHistoryModal').classList.remove('open');
    }

    function closeReceipt() {
        document.getElementById('siReceiptModal').classList.remove('open');
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();

        // Default channel: staff context → INSTORE; otherwise ONLINE
        var isStaffView = (typeof staffId !== 'undefined' && staffId);
        switchChannel(isStaffView ? 'INSTORE' : 'ONLINE');

        renderCatalogue();
        renderCart();
        waitForRealData();
    }

    function waitForRealData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.storesState._loaded || attempts > 200) {
                clearInterval(timer);
                renderCatalogue();
                renderLowStock();
                if (attempts > 200) console.warn('[storesInventory] API data slow — rendering partial data.');
            }
        }, 30);
    }

    // ─── DYNAMIC SCRIPT LOADER ────────────────────────────────────────────────
    function loadLogicScript() {
        if (window.storesData && window.storesState) {
            init();
            return;
        }

        var s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = _scriptBase + '../_storesInventory.js'; // lives in scripts/ not subscripts/

        s.onload = function () {
            if (window.storesData && window.storesState) {
                init();
            } else {
                console.error('[storesInventory] _storesInventory.js loaded but window.storesData is not defined.');
            }
        };

        s.onerror = function () {
            console.error('[storesInventory] Could not load: ' + _scriptBase + '../_storesInventory.js');
        };

        document.body.appendChild(s);
    }

    // ─── BOOT ─────────────────────────────────────────────────────────────────
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initStoresInventory = init;

})();
