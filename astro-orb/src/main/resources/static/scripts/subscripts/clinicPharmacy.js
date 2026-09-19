/**
 * clinicPharmacy.js  —  UI renderer for the Clinic Pharmacy / Stock module.
 *
 * Loads first, dynamically loads _clinicPharmacy.js at runtime — same
 * two-file pattern as clinic.js / storesInventory.js.
 *
 * Catalogue (with low-stock flags) + a restock/dispense form + recent
 * movement history. This was completely unimplemented before (empty
 * MedicalProductService/InventoryService, no controller, no UI at all).
 */
(function () {
    'use strict';

    var _scriptBase = (function () {
        var el = document.currentScript ||
            (function () {
                var tags = document.getElementsByTagName('script');
                for (var i = tags.length - 1; i >= 0; i--) {
                    if (tags[i].src && tags[i].src.indexOf('clinicPharmacy') !== -1) {
                        return tags[i];
                    }
                }
                return null;
            })();
        if (!el || !el.src) return '';
        return el.src.substring(0, el.src.lastIndexOf('/') + 1);
    })();

    // style.css is already loaded globally by the base template.

    if (!document.querySelector('link[href*="font-awesome"], link[href*="fontawesome"]')) {
        var fa = document.createElement('link');
        fa.rel = 'stylesheet';
        fa.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
        document.head.appendChild(fa);
    }

    (function injectScopedStyle() {
        if (document.getElementById('pharmacyScopedCSS')) return;
        var style = document.createElement('style');
        style.id = 'pharmacyScopedCSS';
        style.textContent = [
            '.ph-grid{display:grid;grid-template-columns:1.6fr 1fr;gap:18px;align-items:start;}',
            '.ph-badge{padding:2px 8px;border-radius:10px;font-size:11px;font-weight:600;}',
            '.ph-badge.low{background:#fdeaea;color:#8a2020;}',
            '.ph-badge.ok{background:#eaf7ea;color:#256029;}',
            '@media (max-width: 900px){.ph-grid{grid-template-columns:1fr;}}',
        ].join('\n');
        document.head.appendChild(style);
    })();

    var CATEGORY_OPTIONS = ['MEDICATION', 'CONSUMABLE', 'EQUIPMENT', 'OTHER'];

    function buildDOM() {
        document.getElementById('wrapper').innerHTML = '<div class="fc-page">' + [
            '<header class="fc-header">',
            '<div><h1><i class="fas fa-flask"></i> Pharmacy &amp; Stock</h1>',
            '<p>Medical product catalogue and stock movements</p></div>',
            '<div class="ph-header-actions">',
            '<button class="fc-btn fc-btn-secondary" id="phRefreshBtn"><i class="fas fa-sync-alt"></i> Refresh</button>',
            '<button class="fc-btn fc-btn-primary" id="phAddProductBtn"><i class="fas fa-plus"></i> New Product</button>',
            '</div>',
            '</header>',

            '<div class="ph-grid">',

            '<div>',
            '<div class="panel">',
            '<div class="panel-head"><span><i class="fas fa-boxes"></i> Catalogue</span></div>',
            '<div class="panel-body" id="phCatalogueBody">',
            '<div class="loading-state"><i class="fas fa-circle-notch fa-spin"></i> Loading…</div>',
            '</div>',
            '</div>',

            '<div class="panel">',
            '<div class="panel-head"><span><i class="fas fa-history"></i> Recent Movements</span></div>',
            '<div class="panel-body" id="phMovementsBody"></div>',
            '</div>',
            '</div>',

            '<div class="panel" id="phFormPanel" style="display:none;">',
            '<div class="panel-head"><span id="phFormTitle">New Product</span>',
            '<button class="fc-btn-icon" id="phFormCloseBtn"><i class="fas fa-times"></i></button></div>',
            '<div class="panel-body" id="phFormBody"></div>',
            '</div>',

            '</div>', // end ph-grid

            '<div class="fc-toast" id="phToast"></div>',
            '<footer class="footer"><i class="far fa-copyright"></i> Astromy LLC 2013–<span id="phYear"></span> | Pharmacy</footer>',
            '</div>',
        ].join('');

        document.getElementById('phYear').textContent = new Date().getFullYear();
    }

    // ─── CATALOGUE ────────────────────────────────────────────────────────
    function renderCatalogue() {
        var products = window.pharmacyData.products;
        var body = document.getElementById('phCatalogueBody');

        if (!products.length) {
            body.innerHTML = '<div class="empty-state"><i class="fas fa-box-open"></i> No products yet — add one to get started.</div>';
            return;
        }

        var rows = products.map(function (p) {
            return '<tr>' +
                '<td>' + p.name + '<div style="color:#999;font-size:11px;">' + (p.productCode || '') + '</div></td>' +
                '<td>' + (p.category || '') + '</td>' +
                '<td>' + p.availableStock + ' / ' + p.totalStock + ' ' + (p.unit || '') + '</td>' +
                '<td>' + (p.lowStock
                    ? '<span class="ph-badge low">Low stock</span>'
                    : '<span class="ph-badge ok">OK</span>') + '</td>' +
                '<td>' +
                '<button class="fc-btn fc-btn-secondary ph-restock-btn" data-id="' + p.id + '" data-name="' + p.name + '">Restock</button> ' +
                '<button class="fc-btn fc-btn-secondary ph-dispense-btn" data-id="' + p.id + '" data-name="' + p.name + '">Dispense</button> ' +
                '<button class="fc-btn-icon ph-deactivate-btn" data-id="' + p.id + '" title="Deactivate"><i class="fas fa-trash"></i></button>' +
                '</td>' +
                '</tr>';
        }).join('');

        body.innerHTML = '<table class="fc-table"><thead><tr>' +
            '<th>Product</th><th>Category</th><th>Stock (avail/total)</th><th>Status</th><th></th>' +
            '</tr></thead><tbody>' + rows + '</tbody></table>';

        body.querySelectorAll('.ph-restock-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                openMovementForm(btn.dataset.id, btn.dataset.name, 'RESTOCK');
            });
        });
        body.querySelectorAll('.ph-dispense-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                openMovementForm(btn.dataset.id, btn.dataset.name, 'DISPENSE');
            });
        });
        body.querySelectorAll('.ph-deactivate-btn').forEach(function (btn) {
            btn.addEventListener('click', async function () {
                if (!confirm('Deactivate this product? It will no longer appear in the catalogue.')) return;
                btn.disabled = true;
                try {
                    await window.pharmacyDeactivateProduct(btn.dataset.id);
                    renderCatalogue();
                    toast('Product deactivated.', 'success');
                } catch (e) {
                    console.error('[clinicPharmacy] Failed to deactivate product:', e);
                    toast('Could not deactivate this product.', 'error');
                    btn.disabled = false;
                }
            });
        });
    }

    // ─── MOVEMENTS ────────────────────────────────────────────────────────
    function renderMovements() {
        var movements = window.pharmacyData.movements;
        var body = document.getElementById('phMovementsBody');
        var fmt = window.pharmacyFmt;

        if (!movements.length) {
            body.innerHTML = '<div class="empty-state"><i class="fas fa-inbox"></i> No stock movements yet.</div>';
            return;
        }

        var rows = movements.slice(0, 25).map(function (m) {
            return '<tr>' +
                '<td>' + m.productName + '</td>' +
                '<td>' + m.movementType + '</td>' +
                '<td>' + m.quantity + '</td>' +
                '<td>' + (m.patientId || '—') + '</td>' +
                '<td>' + fmt.dateTime(m.dateTime) + '</td>' +
                '</tr>';
        }).join('');

        body.innerHTML = '<table class="fc-table"><thead><tr>' +
            '<th>Product</th><th>Type</th><th>Qty</th><th>Patient</th><th>When</th>' +
            '</tr></thead><tbody>' + rows + '</tbody></table>';
    }

    // ─── ADD PRODUCT FORM ───────────────────────────────────────────────────
    function openAddProductForm() {
        document.getElementById('phFormTitle').textContent = 'New Product';
        document.getElementById('phFormBody').innerHTML = [
            '<div class="fc-field"><label>Name <span class="req">*</span></label><input type="text" id="phName"></div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Product code</label><input type="text" id="phCode"></div>',
            '<div class="fc-field"><label>Category</label><select id="phCategory">' +
            CATEGORY_OPTIONS.map(function (c) { return '<option value="' + c + '">' + c + '</option>'; }).join('') +
            '</select></div>',
            '</div>',
            '<div class="fc-fields-row">',
            '<div class="fc-field"><label>Unit</label><input type="text" id="phUnit" placeholder="e.g. tablet, bottle, box"></div>',
            '<div class="fc-field"><label>Initial stock</label><input type="number" id="phInitialStock" value="0" min="0"></div>',
            '</div>',
            '<div class="fc-field"><label>Reorder level (low-stock threshold)</label><input type="number" id="phReorderLevel" min="0"></div>',
            '<button class="process-btn" id="phSaveProductBtn"><i class="fas fa-save"></i> Save Product</button>',
        ].join('');

        document.getElementById('phSaveProductBtn').addEventListener('click', async function () {
            var name = fieldVal('phName');
            if (!name) {
                toast('Product name is required.', 'error');
                return;
            }
            var req = {
                institutionCode: window.pharmacyState.institutionCode,
                name: name,
                productCode: fieldVal('phCode'),
                category: document.getElementById('phCategory').value,
                unit: fieldVal('phUnit'),
                totalStock: Number(fieldVal('phInitialStock') || 0),
                reorderLevel: fieldVal('phReorderLevel') ? Number(fieldVal('phReorderLevel')) : null,
            };

            var btn = document.getElementById('phSaveProductBtn');
            btn.disabled = true;
            try {
                await window.pharmacyCreateProduct(req);
                closeForm();
                renderCatalogue();
                toast('Product added.', 'success');
            } catch (e) {
                console.error('[clinicPharmacy] Failed to create product:', e);
                toast('Could not save this product.', 'error');
                btn.disabled = false;
            }
        });

        document.getElementById('phFormPanel').style.display = '';
    }

    // ─── RESTOCK / DISPENSE FORM ────────────────────────────────────────────
    function openMovementForm(productId, productName, movementType) {
        var isDispense = movementType === 'DISPENSE';
        document.getElementById('phFormTitle').textContent = (isDispense ? 'Dispense: ' : 'Restock: ') + productName;

        document.getElementById('phFormBody').innerHTML = [
            '<div class="fc-field"><label>Quantity <span class="req">*</span></label><input type="number" id="phMoveQty" min="1"></div>',
            isDispense ? [
                '<div class="fc-fields-row">',
                '<div class="fc-field"><label>Patient ID</label><input type="text" id="phMovePatientId"></div>',
                '<div class="fc-field"><label>Patient type</label><select id="phMovePatientType"><option value="STUDENT">STUDENT</option><option value="STAFF">STAFF</option></select></div>',
                '</div>',
                '<div class="fc-field"><label>Linked visit ID (optional)</label><input type="text" id="phMoveVisitId"></div>',
            ].join('') : '',
            '<div class="fc-field"><label>Recorded by</label><input type="text" id="phMoveRecordedBy" value="' + (window.pharmacyState.recordedBy || '') + '"></div>',
            '<button class="process-btn" id="phSaveMoveBtn"><i class="fas fa-save"></i> ' + (isDispense ? 'Dispense' : 'Restock') + '</button>',
        ].join('');

        document.getElementById('phSaveMoveBtn').addEventListener('click', async function () {
            var qty = Number(fieldVal('phMoveQty') || 0);
            if (qty <= 0) {
                toast('Quantity must be greater than zero.', 'error');
                return;
            }
            var req = {
                institutionCode: window.pharmacyState.institutionCode,
                medicalProductId: Number(productId),
                quantity: qty,
                movementType: movementType,
                recordedBy: fieldVal('phMoveRecordedBy'),
            };
            if (isDispense) {
                req.patientId = fieldVal('phMovePatientId');
                req.patientType = document.getElementById('phMovePatientType').value;
                var visitId = fieldVal('phMoveVisitId');
                req.visitId = visitId ? Number(visitId) : null;
            }

            var btn = document.getElementById('phSaveMoveBtn');
            btn.disabled = true;
            try {
                await window.pharmacyRecordMovement(req);
                closeForm();
                renderCatalogue();
                renderMovements();
                toast((isDispense ? 'Dispensed.' : 'Restocked.'), 'success');
            } catch (e) {
                console.error('[clinicPharmacy] Failed to record movement:', e);
                toast(e && e.message ? e.message : 'Could not record this movement.', 'error');
                btn.disabled = false;
            }
        });

        document.getElementById('phFormPanel').style.display = '';
    }

    function closeForm() {
        document.getElementById('phFormPanel').style.display = 'none';
        document.getElementById('phFormBody').innerHTML = '';
    }

    function fieldVal(id) {
        var el = document.getElementById(id);
        return el ? el.value.trim() : '';
    }

    function toast(msg, type) {
        var el = document.getElementById('phToast');
        if (!el) return;
        el.textContent = msg;
        el.className = 'fc-toast show' + (type ? (' fc-toast-' + type) : '');
        setTimeout(function () {
            el.classList.remove('show');
        }, 3500);
    }

    // ─── WIRE + INIT ────────────────────────────────────────────────────────
    function wireEvents() {
        document.getElementById('phAddProductBtn').addEventListener('click', openAddProductForm);
        document.getElementById('phFormCloseBtn').addEventListener('click', closeForm);
        document.getElementById('phRefreshBtn').addEventListener('click', async function () {
            await window.pharmacyLoad();
            renderCatalogue();
            renderMovements();
            toast('Refreshed.', 'success');
        });
    }

    function init() {
        buildDOM();
        wireEvents();
        renderCatalogue();
        renderMovements();
        waitForRealData();
    }

    function waitForRealData() {
        var attempts = 0;
        var timer = setInterval(function () {
            attempts++;
            if (window.pharmacyState._loaded || attempts > 200) {
                clearInterval(timer);
                renderCatalogue();
                renderMovements();
                if (attempts > 200) console.warn('[clinicPharmacy] API data slow — rendering partial data.');
            }
        }, 30);
    }

    function loadLogicScript() {
        if (window.pharmacyData && window.pharmacyState) {
            init();
            return;
        }

        var s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = _scriptBase + '../_clinicPharmacy.js';

        s.onload = function () {
            if (window.pharmacyData && window.pharmacyState) {
                init();
                window.pharmacyLoad();
            } else {
                console.error('[clinicPharmacy] _clinicPharmacy.js loaded but window.pharmacyData is not defined.');
            }
        };

        s.onerror = function () {
            console.error('[clinicPharmacy] Could not load: ' + _scriptBase + '../_clinicPharmacy.js');
        };

        document.body.appendChild(s);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', loadLogicScript);
    } else {
        loadLogicScript();
    }

    window.initClinicPharmacy = init;

})();
