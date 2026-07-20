/**
 * storesAdmin.js  —  UI renderer for Inventory Management (Admin).
 *
 * Companion to storesInventory.js (purchase-side storefront/POS).
 * Dynamically loads _storesAdmin.js — same two-file pattern as
 * financeLedger.js + _financeLedgers.js.
 *
 * Provides:
 *   - Summary cards (total items, units in stock, stock value, low/out of stock)
 *   - Inventory table (search + item-type filter, edit / restock / deactivate)
 *   - Add / Edit item modal
 *   - Restock modal
 *   - Stock movement audit trail modal (institution-wide or per item)
 */
(function () {
    'use strict';

    // ─── RESOLVE BASE PATH ──────────────────────────────────────────────────
    var _scriptBase = (function () {
        var el = document.currentScript ||
            (function () {
                var tags = document.getElementsByTagName('script');
                for (var i = tags.length - 1; i >= 0; i--) {
                    if (tags[i].src && tags[i].src.indexOf('storesAdmin') !== -1) {
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
        if (document.getElementById('storesAdminCSS')) return;
        var link = document.createElement('link');
        link.id = 'storesAdminCSS';
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
            '<div><h1><i class="fas fa-warehouse"></i> Inventory Management</h1>',
            '<p>Stock items, monitor levels, and review movement history</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="saMovementsBtn"><i class="fas fa-exchange-alt"></i> Stock Movements</button>',
            '<button class="fc-btn fc-btn-secondary" id="saRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '<button class="fc-btn fc-btn-primary" id="saAddItemBtn"><i class="fas fa-plus"></i> Add Item</button>',
            '</div>',
            '</header>',

            // ── SUMMARY CARDS ───────────────────────────────────────────────
            '<div class="summary-figures" id="saSummaryCards"></div>',

            // ── FILTER BAR ───────────────────────────────────────────────────
            '<div class="panel">',
            '<div class="panel-head">',
            '<span><i class="fas fa-boxes"></i> Inventory</span>',
            '<div class="si-admin-filters">',
            '<input type="text" id="saSearchInput" placeholder="Search by name, code, description…">',
            '<select id="saTypeFilter">',
            '<option value="">All Types</option>',
            '<option value="PROSPECTUS">Prospectus</option>',
            '<option value="UNIFORM">Uniform</option>',
            '<option value="STATIONERY">Stationery</option>',
            '<option value="OTHER">Other</option>',
            '</select>',
            '</div>',
            '</div>',
            '<div class="panel-body">',
            '<div id="saTableWrap">',
            '<div class="loading-state"><i class="fas fa-circle-notch fa-spin"></i> Loading inventory…</div>',
            '</div>',
            '</div>',
            '</div>',

            // ── ADD/EDIT ITEM MODAL ────────────────────────────────────────────
            '<div class="fc-modal-overlay" id="saItemModal">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head"><h5 id="saItemModalTitle"><i class="fas fa-box-open"></i> Add Item</h5>',
            '<button class="fc-modal-close" id="saItemModalClose">&times;</button></div>',
            '<div class="fc-modal-body">',
            '<input type="hidden" id="saItemId">',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Item Name <span class="req">*</span></label>',
            '<input type="text" id="saItemName" placeholder="e.g. Prospectus 2026"></div>',
            '<div class="fc-field"><label>Item Code <span class="req">*</span></label>',
            '<input type="text" id="saItemCode" placeholder="e.g. PROS-2026"></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Item Type <span class="req">*</span></label>',
            '<select id="saItemType">',
            '<option value="PROSPECTUS">Prospectus</option>',
            '<option value="UNIFORM">Uniform</option>',
            '<option value="STATIONERY">Stationery</option>',
            '<option value="OTHER">Other</option>',
            '</select></div>',
            '<div class="fc-field"><label>Channel <span class="req">*</span></label>',
            '<select id="saItemChannel">',
            '<option value="BOTH">Online &amp; In-Store</option>',
            '<option value="ONLINE">Online Only</option>',
            '<option value="INSTORE">In-Store Only</option>',
            '</select></div>',
            '</div>',
            '<div class="fc-field"><label>Description</label>',
            '<input type="text" id="saItemDescription" placeholder="Short description shown to buyers"></div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Unit Price (GH₵) <span class="req">*</span></label>',
            '<input type="number" id="saItemPrice" min="0" step="0.01" placeholder="0.00"></div>',
            '<div class="fc-field" id="saItemQtyField"><label>Opening Stock</label>',
            '<input type="number" id="saItemQty" min="0" step="1" placeholder="0"></div>',
            '<div class="fc-field"><label>Reorder Level</label>',
            '<input type="number" id="saItemReorder" min="0" step="1" placeholder="5"></div>',
            '</div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="saItemCancelBtn">Cancel</button>',
            '<button class="fc-btn fc-btn-primary" id="saItemSaveBtn"><i class="fas fa-save"></i> Save Item</button>',
            '</div>',
            '</div></div>',

            // ── RESTOCK MODAL ───────────────────────────────────────────────────
            '<div class="fc-modal-overlay" id="saRestockModal">',
            '<div class="fc-modal">',
            '<div class="fc-modal-head"><h5><i class="fas fa-dolly"></i> Restock Item</h5>',
            '<button class="fc-modal-close" id="saRestockClose">&times;</button></div>',
            '<div class="fc-modal-body">',
            '<input type="hidden" id="saRestockItemId">',
            '<div class="si-restock-item-info" id="saRestockItemInfo"></div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Quantity to Add <span class="req">*</span></label>',
            '<input type="number" id="saRestockQty" min="1" step="1" placeholder="e.g. 50"></div>',
            '<div class="fc-field"><label>Reference / PO Number</label>',
            '<input type="text" id="saRestockNotes" placeholder="e.g. PO-2026-014"></div>',
            '</div>',
            '</div>',
            '<div class="fc-modal-foot">',
            '<button class="fc-btn fc-btn-secondary" id="saRestockCancelBtn">Cancel</button>',
            '<button class="fc-btn fc-btn-success" id="saRestockSaveBtn"><i class="fas fa-check"></i> Confirm Restock</button>',
            '</div>',
            '</div></div>',

            // ── STOCK MOVEMENTS MODAL ───────────────────────────────────────────
            '<div class="fc-modal-overlay" id="saMovementsModal">',
            '<div class="fc-modal fc-modal-lg">',
            '<div class="fc-modal-head"><h5 id="saMovementsTitle"><i class="fas fa-exchange-alt"></i> Stock Movements</h5>',
            '<button class="fc-modal-close" id="saMovementsClose">&times;</button></div>',
            '<div class="fc-modal-body" id="saMovementsBody"></div>',
            '<div class="fc-modal-foot"><button class="fc-btn fc-btn-secondary" id="saMovementsCloseBtn">Close</button></div>',
            '</div></div>',

            '<div class="fc-toast" id="saToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="saYear"></span> | Inventory Management</footer>',

            '</div>', // end si-page
        ].join('');

        document.getElementById('saYear').textContent = new Date().getFullYear();
    }

    // ─── RENDER: SUMMARY CARDS ───────────────────────────────────────────────
    function renderSummary(items) {
        var s = window.storesAdminSummary(items);
        var fmt = window.storesAdminFmt;

        document.getElementById('saSummaryCards').innerHTML = [
            card('fa-boxes', s.totalItems, 'Items Tracked', ''),
            card('fa-cubes', s.totalUnits.toLocaleString(), 'Units in Stock', ''),
            card('fa-coins', fmt.money(s.totalValue), 'Stock Value', ''),
            card('fa-exclamation-triangle', s.lowStockCount, 'Low Stock', s.lowStockCount > 0 ? 'summary-warn' : ''),
            card('fa-times-circle', s.outOfStockCount, 'Out of Stock', s.outOfStockCount > 0 ? 'summary-danger' : ''),
        ].join('');
    }

    function card(icon, value, label, extraClass) {
        return '<div class="summary-item ' + (extraClass || '') + '">' +
            '<div class="summary-icon"><i class="fas ' + icon + '"></i></div>' +
            '<div class="summary-value">' + value + '</div>' +
            '<div class="summary-label">' + label + '</div>' +
            '</div>';
    }

    // ─── RENDER: TABLE ────────────────────────────────────────────────────────
    function renderTable() {
        var items = window.storesAdminFilter();
        var wrap = document.getElementById('saTableWrap');
        var fmt = window.storesAdminFmt;

        renderSummary(window.storesAdminData.allItems);

        if (!items.length) {
            wrap.innerHTML = '<div class="empty-state"><i class="fas fa-box-open"></i> No items match your filters.</div>';
            return;
        }

        var rows = items.map(function (item) {
            var meta = TYPE_META[item.itemType] || TYPE_META.OTHER;
            var stockBadge;
            if (item.quantityInStock <= 0) {
                stockBadge = '<span class="badge badge-danger">Out of stock</span>';
            } else if (item.lowStock) {
                stockBadge = '<span class="badge badge-warning">' + item.quantityInStock + ' (low)</span>';
            } else {
                stockBadge = '<span class="badge badge-success">' + item.quantityInStock + '</span>';
            }

            return '<tr>' +
                '<td><i class="fas ' + meta.icon + '"></i> ' + item.itemName + '</td>' +
                '<td><code>' + item.itemCode + '</code></td>' +
                '<td>' + meta.label + '</td>' +
                '<td>' + item.channel + '</td>' +
                '<td class="text-right">' + fmt.money(item.unitPrice) + '</td>' +
                '<td class="text-right">' + stockBadge + '</td>' +
                '<td class="text-right">' + item.reorderLevel + '</td>' +
                '<td>' +
                '<button class="fc-btn-icon sa-edit-btn" data-id="' + item.id + '" title="Edit"><i class="fas fa-edit"></i></button>' +
                '<button class="fc-btn-icon sa-restock-btn" data-id="' + item.id + '" title="Restock"><i class="fas fa-dolly"></i></button>' +
                '<button class="fc-btn-icon sa-history-btn" data-id="' + item.id + '" title="History"><i class="fas fa-history"></i></button>' +
                '<button class="fc-btn-icon sa-deactivate-btn" data-id="' + item.id + '" title="Deactivate"><i class="fas fa-trash-alt"></i></button>' +
                '</td>' +
                '</tr>';
        }).join('');

        wrap.innerHTML = '<table class="fc-table">' +
            '<thead><tr><th>Item</th><th>Code</th><th>Type</th><th>Channel</th>' +
            '<th class="text-right">Price</th><th class="text-right">Stock</th>' +
            '<th class="text-right">Reorder At</th><th>Actions</th></tr></thead>' +
            '<tbody>' + rows + '</tbody></table>';

        wireRowActions();
    }

    function wireRowActions() {
        document.querySelectorAll('.sa-edit-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                openItemModal(findItem(this.dataset.id));
            });
        });
        document.querySelectorAll('.sa-restock-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                openRestockModal(findItem(this.dataset.id));
            });
        });
        document.querySelectorAll('.sa-history-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                openMovementsModal(findItem(this.dataset.id));
            });
        });
        document.querySelectorAll('.sa-deactivate-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                confirmDeactivate(findItem(this.dataset.id));
            });
        });
    }

    function findItem(id) {
        return window.storesAdminData.allItems.find(function (i) {
            return i.id === id;
        });
    }

    // ─── ADD / EDIT ITEM MODAL ───────────────────────────────────────────────
    function openItemModal(item) {
        var isEdit = !!item;
        document.getElementById('saItemModalTitle').innerHTML =
            '<i class="fas fa-box-open"></i> ' + (isEdit ? 'Edit Item' : 'Add Item');

        document.getElementById('saItemId').value = isEdit ? item.id : '';
        document.getElementById('saItemName').value = isEdit ? item.itemName : '';
        document.getElementById('saItemCode').value = isEdit ? item.itemCode : '';
        document.getElementById('saItemType').value = isEdit ? item.itemType : 'PROSPECTUS';
        document.getElementById('saItemChannel').value = isEdit ? item.channel : 'BOTH';
        document.getElementById('saItemDescription').value = isEdit ? (item.description || '') : '';
        document.getElementById('saItemPrice').value = isEdit ? item.unitPrice : '';
        document.getElementById('saItemQty').value = isEdit ? item.quantityInStock : 0;
        document.getElementById('saItemReorder').value = isEdit ? item.reorderLevel : 5;

        // Item code + opening stock are fixed at creation time
        document.getElementById('saItemCode').disabled = isEdit;
        document.getElementById('saItemQtyField').style.display = isEdit ? 'none' : '';

        document.getElementById('saItemModal').classList.add('open');
    }

    async function saveItem() {
        var id = document.getElementById('saItemId').value;
        var name = document.getElementById('saItemName').value.trim();
        var code = document.getElementById('saItemCode').value.trim();
        var price = parseFloat(document.getElementById('saItemPrice').value);

        if (!name || !code || isNaN(price) || price < 0) {
            toast('Item name, code, and a valid price are required.', 'error');
            return;
        }

        var req = {
            itemName: name,
            itemCode: code,
            itemType: document.getElementById('saItemType').value,
            channel: document.getElementById('saItemChannel').value,
            description: document.getElementById('saItemDescription').value.trim(),
            unitPrice: price,
            reorderLevel: parseInt(document.getElementById('saItemReorder').value, 10) || 5,
        };

        try {
            if (id) {
                await window.storesAdminUpdateItem(id, req);
                toast('Item updated.', 'success');
            } else {
                req.quantityInStock = parseInt(document.getElementById('saItemQty').value, 10) || 0;
                await window.storesAdminCreateItem(req);
                toast('Item created.', 'success');
            }
            closeItemModal();
            await refreshAll();
        } catch (e) {
            toast('Save failed: ' + (e.message || e), 'error');
        }
    }

    function closeItemModal() {
        document.getElementById('saItemModal').classList.remove('open');
    }

    // ─── RESTOCK MODAL ───────────────────────────────────────────────────────
    function openRestockModal(item) {
        if (!item) return;
        document.getElementById('saRestockItemId').value = item.id;
        document.getElementById('saRestockItemInfo').innerHTML =
            '<strong>' + item.itemName + '</strong> <code>' + item.itemCode + '</code><br>' +
            'Current stock: <span class="badge badge-info">' + item.quantityInStock + '</span>';
        document.getElementById('saRestockQty').value = '';
        document.getElementById('saRestockNotes').value = '';
        document.getElementById('saRestockModal').classList.add('open');
    }

    async function saveRestock() {
        var itemId = document.getElementById('saRestockItemId').value;
        var qty = parseInt(document.getElementById('saRestockQty').value, 10);
        var notes = document.getElementById('saRestockNotes').value.trim();

        if (!qty || qty <= 0) {
            toast('Enter a valid quantity to add.', 'error');
            return;
        }

        var performedBy = (typeof staffId !== 'undefined' && staffId) ? staffId
            : (typeof userId !== 'undefined' ? userId : 'ADMIN');

        try {
            await window.storesAdminRestock({
                storeItemId: itemId,
                quantityToAdd: qty,
                performedBy: performedBy,
                notes: notes,
            });
            toast('Stock updated.', 'success');
            closeRestockModal();
            await refreshAll();
        } catch (e) {
            toast('Restock failed: ' + (e.message || e), 'error');
        }
    }

    function closeRestockModal() {
        document.getElementById('saRestockModal').classList.remove('open');
    }

    // ─── DEACTIVATE ──────────────────────────────────────────────────────────
    async function confirmDeactivate(item) {
        if (!item) return;
        if (!window.confirm('Remove "' + item.itemName + '" from the catalogue? This can be reversed by an admin in the database if needed.')) {
            return;
        }
        try {
            await window.storesAdminDeactivateItem(item.id);
            toast('Item removed from catalogue.', 'success');
            renderTable();
        } catch (e) {
            toast('Could not remove item: ' + (e.message || e), 'error');
        }
    }

    // ─── STOCK MOVEMENTS MODAL ───────────────────────────────────────────────
    async function openMovementsModal(item) {
        document.getElementById('saMovementsTitle').innerHTML =
            '<i class="fas fa-exchange-alt"></i> Stock Movements' + (item ? ' — ' + item.itemName : ' — All Items');
        document.getElementById('saMovementsBody').innerHTML =
            '<div class="loading-state"><i class="fas fa-circle-notch fa-spin"></i> Loading…</div>';
        document.getElementById('saMovementsModal').classList.add('open');

        var movements = item
            ? await window.storesAdminFetchMovementsByItem(item.id)
            : await window.storesAdminFetchMovements();

        renderMovements(movements);
    }

    function renderMovements(movements) {
        var fmt = window.storesAdminFmt;
        if (!movements.length) {
            document.getElementById('saMovementsBody').innerHTML =
                '<div class="empty-state"><i class="fas fa-inbox"></i> No stock movements recorded.</div>';
            return;
        }

        var TYPE_BADGE = {
            PURCHASE_SALE: 'badge-danger',
            RESTOCK: 'badge-success',
            ADJUSTMENT: 'badge-warning',
            RETURN: 'badge-info',
        };

        var rows = movements.map(function (m) {
            var sign = m.quantityChange > 0 ? '+' : '';
            return '<tr>' +
                '<td>' + fmt.dateTime(m.movementDate) + '</td>' +
                '<td>' + m.itemName + '</td>' +
                '<td><span class="badge ' + (TYPE_BADGE[m.movementType] || 'badge-default') + '">' + m.movementType + '</span></td>' +
                '<td class="text-right">' + sign + m.quantityChange + '</td>' +
                '<td class="text-right">' + m.balanceAfter + '</td>' +
                '<td>' + (m.referenceId || '—') + '</td>' +
                '<td>' + (m.performedBy || '—') + '</td>' +
                '<td>' + (m.notes || '—') + '</td>' +
                '</tr>';
        }).join('');

        document.getElementById('saMovementsBody').innerHTML =
            '<table class="fc-table">' +
            '<thead><tr><th>Date</th><th>Item</th><th>Type</th><th class="text-right">Change</th>' +
            '<th class="text-right">Balance After</th><th>Reference</th><th>By</th><th>Notes</th></tr></thead>' +
            '<tbody>' + rows + '</tbody></table>';
    }

    function closeMovementsModal() {
        document.getElementById('saMovementsModal').classList.remove('open');
    }

    // ─── TOAST ───────────────────────────────────────────────────────────────
    function toast(msg, type) {
        var el = document.getElementById('saToast');
        if (!el) return;
        el.textContent = msg;
        el.className = 'fc-toast show' + (type ? (' fc-toast-' + type) : '');
        clearTimeout(el._t);
        el._t = setTimeout(function () {
            el.classList.remove('show');
        }, 3500);
    }

    // ─── REFRESH ─────────────────────────────────────────────────────────────
    async function refreshAll() {
        await window.storesAdminFetchAll(window.storesAdminState.selectedType);
        await window.storesAdminFetchLowStock();
        applyLowStockFlags();
        renderTable();
    }

    /** Mark items as lowStock based on the dedicated low-stock list (keeps server authoritative) */
    function applyLowStockFlags() {
        var lowIds = new Set(window.storesAdminData.lowStockItems.map(function (i) {
            return i.id;
        }));
        window.storesAdminData.allItems.forEach(function (i) {
            i.lowStock = lowIds.has(i.id) && i.quantityInStock > 0;
        });
    }

    // ─── WIRE EVENTS ─────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('saAddItemBtn').addEventListener('click', function () {
            openItemModal(null);
        });

        document.getElementById('saSearchInput').addEventListener('input', function () {
            window.storesAdminState.searchQuery = this.value;
            renderTable();
        });

        document.getElementById('saTypeFilter').addEventListener('change', async function () {
            window.storesAdminState.selectedType = this.value;
            await refreshAll();
        });

        document.getElementById('saRefreshBtn').addEventListener('click', async function () {
            await refreshAll();
            toast('Refreshed.', 'success');
        });

        document.getElementById('saMovementsBtn').addEventListener('click', function () {
            openMovementsModal(null);
        });

        // Item modal
        document.getElementById('saItemModalClose').addEventListener('click', closeItemModal);
        document.getElementById('saItemCancelBtn').addEventListener('click', closeItemModal);
        document.getElementById('saItemSaveBtn').addEventListener('click', saveItem);
        document.getElementById('saItemModal').addEventListener('click', function (e) {
            if (e.target === e.currentTarget) closeItemModal();
        });

        // Restock modal
        document.getElementById('saRestockClose').addEventListener('click', closeRestockModal);
        document.getElementById('saRestockCancelBtn').addEventListener('click', closeRestockModal);
        document.getElementById('saRestockSaveBtn').addEventListener('click', saveRestock);
        document.getElementById('saRestockModal').addEventListener('click', function (e) {
            if (e.target === e.currentTarget) closeRestockModal();
        });

        // Movements modal
        document.getElementById('saMovementsClose').addEventListener('click', closeMovementsModal);
        document.getElementById('saMovementsCloseBtn').addEventListener('click', closeMovementsModal);
        document.getElementById('saMovementsModal').addEventListener('click', function (e) {
            if (e.target === e.currentTarget) closeMovementsModal();
        });
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────
    function init() {
        buildDOM();
        wireEvents();
        waitForRealData();
    }

    function waitForRealData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.storesAdminState._loaded || attempts > 200) {
                clearInterval(timer);
                applyLowStockFlags();
                renderTable();
                if (attempts > 200) console.warn('[storesAdmin] API data slow — rendering partial data.');
            }
        }, 30);
    }

    // ─── DYNAMIC SCRIPT LOADER ────────────────────────────────────────────────
    function loadLogicScript() {
        if (window.storesAdminData && window.storesAdminState) {
            init();
            return;
        }

        var s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = _scriptBase + '../_storesAdmin.js'; // lives in scripts/ not subscripts/

        s.onload = function () {
            if (window.storesAdminData && window.storesAdminState) {
                init();
            } else {
                console.error('[storesAdmin] _storesAdmin.js loaded but window.storesAdminData is not defined.');
            }
        };

        s.onerror = function () {
            console.error('[storesAdmin] Could not load: ' + _scriptBase + '../_storesAdmin.js');
        };

        document.body.appendChild(s);
    }

    // ─── BOOT ─────────────────────────────────────────────────────────────────
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initStoresAdmin = init;

})();
