var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");

$(function () {
    var allBlocks = [];
    var currentAmenities = [];
    var currentConsumables = [];

    init();

    async function init() {
        showSplash();
        try {
            allBlocks = await fetchPost("getInstitutionAccommodation", {val: instId}) || [];
            const opts = allBlocks.map(b => `<option value="${b.idBlock}">${b.name}</option>`).join('');
            $("#resourceBlockSelect").find("option:not(:first)").remove().end().append(opts);
        } catch (error) {
            console.error("Failed to load blocks:", error);
            showError("Could not load blocks. Please refresh and try again.");
        }
        hideSplash();
    }

    $("#resourceBlockSelect").on("change", async function () {
        const idBlock = $(this).val();
        if (!idBlock) {
            $("#resourceTabs").hide();
            $(".resource-tab-pane").hide();
            return;
        }
        $("#resourceTabs").show();
        showTab("amenitiesTab");
        populateRoomSelect(parseInt(idBlock, 10));
        await Promise.all([loadAmenities(), loadConsumables()]);
    });

    function populateRoomSelect(idBlock) {
        const block = allBlocks.find(b => b.idBlock === idBlock);
        const rooms = block ? (block.roomsList || []) : [];
        const select = $("#amenityRoomSelect");
        select.find("option:not(:first)").remove();
        rooms.forEach(r => select.append(`<option value="${r.idBlockRoom}">${r.name}</option>`));
    }

    // ==================== Tabs ====================

    $(document).on("click", ".resourceTabBtn", function () {
        $(".resourceTabBtn").removeClass("btn-primary active-tab").addClass("btn-default");
        $(this).removeClass("btn-default").addClass("btn-primary active-tab");
        showTab($(this).data("tab"));
    });

    function showTab(tabId) {
        $(".resource-tab-pane").hide();
        $("#" + tabId).show();
    }

    // ==================== Amenities ====================

    async function loadAmenities() {
        const idBlock = parseInt($("#resourceBlockSelect").val(), 10);
        try {
            currentAmenities = await fetchPost("getBlockAmenities", {val: idBlock}) || [];
            renderAmenities();
        } catch (error) {
            console.error("Failed to load amenities:", error);
        }
    }

    function renderAmenities() {
        const tbody = $("#amenitiesTableBody");
        tbody.empty();

        if (currentAmenities.length === 0) {
            tbody.append('<tr><td colspan="6" class="text-muted text-center">No amenities recorded for this block yet.</td></tr>');
            return;
        }

        currentAmenities.forEach(a => {
            const badge = a.condition === 'GOOD' ? 'label-success'
                : a.condition === 'DAMAGED' ? 'label-danger' : 'label-warning';
            tbody.append(`
                <tr data-id="${a.idAmenity}">
                    <td>${a.name}</td>
                    <td>${a.roomName || '<span class="text-muted">Block-wide</span>'}</td>
                    <td><span class="label ${badge}">${a.condition}</span></td>
                    <td>${a.lastCheckedDate || ''}</td>
                    <td>${a.notes || ''}</td>
                    <td>
                        <button class="btn btn-xs btn-default edit-amenity-btn"><i class="fa fa-pencil"></i></button>
                        <button class="btn btn-xs btn-danger delete-amenity-btn"><i class="fa fa-trash"></i></button>
                    </td>
                </tr>
            `);
        });
    }

    $("#addAmenityBtn").click(() => {
        $("#amenityModalTitle").text("Add Amenity");
        $("#amenityIdInput").val("");
        $("#amenityNameInput").val("");
        $("#amenityRoomSelect").val("");
        $("#amenityConditionSelect").val("GOOD");
        $("#amenityCheckedDateInput").val("");
        $("#amenityNotesInput").val("");
        $("#amenityModal").modal("show");
    });

    $(document).on("click", ".edit-amenity-btn", function () {
        const idAmenity = parseInt($(this).closest("tr").data("id"), 10);
        const a = currentAmenities.find(x => x.idAmenity === idAmenity);
        if (!a) return;

        $("#amenityModalTitle").text("Edit Amenity");
        $("#amenityIdInput").val(a.idAmenity);
        $("#amenityNameInput").val(a.name);
        $("#amenityRoomSelect").val(a.idBlockRoom || "");
        $("#amenityConditionSelect").val(a.condition);
        $("#amenityCheckedDateInput").val(a.lastCheckedDate || "");
        $("#amenityNotesInput").val(a.notes || "");
        $("#amenityModal").modal("show");
    });

    $("#saveAmenityBtn").click(async () => {
        const name = $("#amenityNameInput").val().trim();
        if (!name) {
            swal({title: "Missing name", text: "Please enter an amenity name.", type: "warning"});
            return;
        }

        const idAmenity = $("#amenityIdInput").val();
        const idBlock = parseInt($("#resourceBlockSelect").val(), 10);
        const idBlockRoom = $("#amenityRoomSelect").val();

        const payload = {
            idAmenity: idAmenity ? parseInt(idAmenity, 10) : null,
            institutionCode: instId,
            idBlock,
            idBlockRoom: idBlockRoom ? parseInt(idBlockRoom, 10) : null,
            name,
            condition: $("#amenityConditionSelect").val(),
            lastCheckedDate: $("#amenityCheckedDateInput").val() || null,
            notes: $("#amenityNotesInput").val().trim(),
        };

        showSplash();
        try {
            await fetchPost(idAmenity ? "updateAmenity" : "addAmenity", payload);
            $("#amenityModal").modal("hide");
            await loadAmenities();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    $(document).on("click", ".delete-amenity-btn", async function () {
        if (!confirm("Delete this amenity record?")) return;
        const idAmenity = parseInt($(this).closest("tr").data("id"), 10);

        showSplash();
        try {
            await fetchPost("deleteAmenity", {val: idAmenity});
            await loadAmenities();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== Consumables ====================

    async function loadConsumables() {
        const idBlock = parseInt($("#resourceBlockSelect").val(), 10);
        try {
            currentConsumables = await fetchPost("getBlockConsumables", {val: idBlock}) || [];
            renderConsumables();
        } catch (error) {
            console.error("Failed to load consumables:", error);
        }
    }

    function renderConsumables() {
        const tbody = $("#consumablesTableBody");
        tbody.empty();

        if (currentConsumables.length === 0) {
            tbody.append('<tr><td colspan="5" class="text-muted text-center">No consumables recorded for this block yet.</td></tr>');
            return;
        }

        currentConsumables.forEach(c => {
            const badge = c.stockStatus === 'IN_STOCK' ? 'label-success'
                : c.stockStatus === 'LOW' ? 'label-warning' : 'label-danger';
            tbody.append(`
                <tr data-id="${c.idConsumable}">
                    <td>${c.name}</td>
                    <td>${c.quantity} ${c.unit || ''}</td>
                    <td><span class="label ${badge}">${c.stockStatus}</span></td>
                    <td>${c.lastRestockedDate || ''}</td>
                    <td>
                        <button class="btn btn-xs btn-default edit-consumable-btn"><i class="fa fa-pencil"></i></button>
                        <button class="btn btn-xs btn-danger delete-consumable-btn"><i class="fa fa-trash"></i></button>
                    </td>
                </tr>
            `);
        });
    }

    $("#addConsumableBtn").click(() => {
        $("#consumableModalTitle").text("Add Consumable");
        $("#consumableIdInput").val("");
        $("#consumableNameInput").val("");
        $("#consumableQuantityInput").val(0);
        $("#consumableUnitInput").val("");
        $("#consumableStatusSelect").val("IN_STOCK");
        $("#consumableRestockedDateInput").val("");
        $("#consumableModal").modal("show");
    });

    $(document).on("click", ".edit-consumable-btn", function () {
        const idConsumable = parseInt($(this).closest("tr").data("id"), 10);
        const c = currentConsumables.find(x => x.idConsumable === idConsumable);
        if (!c) return;

        $("#consumableModalTitle").text("Edit Consumable");
        $("#consumableIdInput").val(c.idConsumable);
        $("#consumableNameInput").val(c.name);
        $("#consumableQuantityInput").val(c.quantity);
        $("#consumableUnitInput").val(c.unit || "");
        $("#consumableStatusSelect").val(c.stockStatus);
        $("#consumableRestockedDateInput").val(c.lastRestockedDate || "");
        $("#consumableModal").modal("show");
    });

    $("#saveConsumableBtn").click(async () => {
        const name = $("#consumableNameInput").val().trim();
        if (!name) {
            swal({title: "Missing name", text: "Please enter a consumable name.", type: "warning"});
            return;
        }

        const idConsumable = $("#consumableIdInput").val();
        const idBlock = parseInt($("#resourceBlockSelect").val(), 10);

        const payload = {
            idConsumable: idConsumable ? parseInt(idConsumable, 10) : null,
            institutionCode: instId,
            idBlock,
            name,
            quantity: parseInt($("#consumableQuantityInput").val(), 10) || 0,
            unit: $("#consumableUnitInput").val().trim(),
            stockStatus: $("#consumableStatusSelect").val(),
            lastRestockedDate: $("#consumableRestockedDateInput").val() || null,
        };

        showSplash();
        try {
            await fetchPost(idConsumable ? "updateConsumable" : "addConsumable", payload);
            $("#consumableModal").modal("hide");
            await loadConsumables();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    $(document).on("click", ".delete-consumable-btn", async function () {
        if (!confirm("Delete this consumable record?")) return;
        const idConsumable = parseInt($(this).closest("tr").data("id"), 10);

        showSplash();
        try {
            await fetchPost("deleteConsumable", {val: idConsumable});
            await loadConsumables();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== Helpers ====================

    function parseBackendError(error) {
        const message = error && error.message ? error.message : String(error);
        const jsonStart = message.indexOf("{");
        if (jsonStart === -1) return message;
        try {
            return JSON.parse(message.slice(jsonStart)).error || message;
        } catch (e) {
            return message;
        }
    }

    function showSplash() {
        if ($) $(".splash").css({display: "block", background: "#ffffff3d"});
    }

    function hideSplash() {
        if ($) $(".splash").css("display", "none");
    }

    function showError(message) {
        swal({title: "Error", text: message, type: "error"});
    }
});
