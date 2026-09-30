var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");

$(function () {
    var allBlocks = [];
    var currentBlockId = null;

    loadBlocks();

    // ==================== Data loading ====================

    async function loadBlocks() {
        showSplash();
        try {
            allBlocks = await fetchPost("getInstitutionAccommodation", {val: instId}) || [];
            renderBlocks();
        } catch (error) {
            console.error("Failed to load blocks:", error);
            showError("Could not load block data. Please refresh and try again.");
        }
        hideSplash();
    }

    function activeMaster(block) {
        return (block.blockMasters || []).find(m => !m.exitDate);
    }

    // ==================== Rendering ====================

    function renderBlocks() {
        const container = $("#blocksContainer");
        container.empty();
        $("#blocksEmptyState").toggle(allBlocks.length === 0);

        allBlocks.forEach(block => {
            const roomCount = (block.roomsList || []).length;
            const master = activeMaster(block);
            const genderBadge = block.gender
                ? `<span class="label label-info">${block.gender}</span>`
                : '<span class="label label-default">Mixed</span>';

            const card = $(`
                <div class="col-md-4">
                    <div class="panel panel-default block-card" data-block-id="${block.idBlock}">
                        <div class="panel-body">
                            <h4>${block.name} ${genderBadge}</h4>
                            ${block.slogan ? `<p class="text-muted small">${block.slogan}</p>` : ''}
                            <p>${roomCount} room(s)</p>
                            <p class="small">
                                <strong>Master:</strong>
                                ${master ? master.staffName : '<span class="text-muted">Not assigned</span>'}
                            </p>
                            <button class="btn btn-sm btn-default manage-block-btn">
                                <i class="fa fa-cog"></i> Manage
                            </button>
                        </div>
                    </div>
                </div>
            `);
            container.append(card);
        });
    }

    function renderRoomsTable() {
        const block = allBlocks.find(b => b.idBlock === currentBlockId);
        const tbody = $("#roomsTableBody");
        tbody.empty();

        if (!block || !block.roomsList || block.roomsList.length === 0) {
            tbody.append('<tr><td colspan="6" class="text-muted text-center">No rooms in this block yet.</td></tr>');
            return;
        }

        block.roomsList.forEach(room => {
            const capacity = (room.reservedBeds || 0) + (room.generalBeds || 0);
            const row = $(`
                <tr data-room-id="${room.idBlockRoom}">
                    <td>${room.name}</td>
                    <td>${room.reservedBeds || 0}</td>
                    <td>${room.generalBeds || 0}</td>
                    <td>-</td>
                    <td>${capacity}</td>
                    <td>
                        <button class="btn btn-xs btn-danger delete-room-btn"><i class="fa fa-trash"></i></button>
                    </td>
                </tr>
            `);
            tbody.append(row);
        });
    }

    function renderMasterSection() {
        const block = allBlocks.find(b => b.idBlock === currentBlockId);
        const master = block ? activeMaster(block) : null;
        const section = $("#masterSection");

        if (master) {
            section.html(`
                <div class="list-group-item">
                    <strong>${master.staffName}</strong> (${master.staffId})
                    <br><span class="text-muted small">Since ${master.appointmentDate || 'N/A'}</span>
                    <button class="btn btn-xs btn-danger pull-right end-master-btn" data-id="${master.idBlockMaster}">End</button>
                </div>
            `);
        } else {
            section.html(`
                <div class="list-group-item text-muted">
                    No block master assigned.
                    <button class="btn btn-xs btn-primary pull-right" id="assignMasterBtn">Assign</button>
                </div>
            `);
        }
    }

    // ==================== Add / Edit Block ====================

    $("#addBlockBtn").click(() => {
        $("#blockModalTitle").text("Add Block");
        $("#blockIdInput").val("");
        $("#blockNameInput").val("");
        $("#blockSloganInput").val("");
        $("#blockGenderInput").val("");
        $("#blockModal").modal("show");
    });

    $("#saveBlockBtn").click(async () => {
        const name = $("#blockNameInput").val().trim();
        if (!name) {
            swal({title: "Missing name", text: "Please enter a block name.", type: "warning"});
            return;
        }

        const payload = {
            institutionCode: instId,
            name: name,
            slogan: $("#blockSloganInput").val().trim(),
            gender: $("#blockGenderInput").val(),
        };

        showSplash();
        try {
            await fetchPost("addInstitutionAccommodation", payload);
            $("#blockModal").modal("hide");
            await loadBlocks();
            swal({title: "Saved", text: "Block created successfully.", type: "success"});
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== Manage Block (rooms + master) ====================

    $(document).on("click", ".manage-block-btn", function () {
        currentBlockId = parseInt($(this).closest(".block-card").data("block-id"), 10);
        const block = allBlocks.find(b => b.idBlock === currentBlockId);
        $("#manageBlockTitle").text("Manage " + (block ? block.name : "Block"));
        $("#newRoomName").val("");
        $("#newRoomReserved").val(0);
        $("#newRoomGeneral").val(4);
        renderRoomsTable();
        renderMasterSection();
        $("#manageBlockModal").modal("show");
    });

    $("#addRoomBtn").click(async () => {
        const name = $("#newRoomName").val().trim();
        if (!name) {
            swal({title: "Missing name", text: "Please enter a room name.", type: "warning"});
            return;
        }

        const payload = {
            idBlock: currentBlockId,
            room: {
                name: name,
                reservedBeds: parseInt($("#newRoomReserved").val(), 10) || 0,
                generalBeds: parseInt($("#newRoomGeneral").val(), 10) || 0,
            },
        };

        showSplash();
        try {
            await fetchPost("addRoomToBlock", payload);
            await loadBlocks();
            renderRoomsTable();
            $("#newRoomName").val("");
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    $(document).on("click", ".delete-room-btn", async function () {
        const idBlockRoom = parseInt($(this).closest("tr").data("room-id"), 10);
        if (!confirm("Delete this room? Any students assigned to it should be reassigned first.")) return;
        showSplash();
        try {
            await fetchPost("deleteRoom", {val: idBlockRoom});
            await loadBlocks();
            renderRoomsTable();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    $("#deleteBlockBtn").click(async () => {
        if (!confirm("Delete this block? This also removes all its rooms, masters, and prefects.")) return;

        showSplash();
        try {
            await fetchPost("deleteBlock", {val: currentBlockId});
            $("#manageBlockModal").modal("hide");
            await loadBlocks();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== Block Master ====================

    $(document).on("click", "#assignMasterBtn", function () {
        $("#masterStaffName").val("");
        $("#masterStaffId").val("");
        $("#masterAppointmentDate").val("");
        $("#assignMasterModal").modal("show");
    });

    $("#saveMasterBtn").click(async () => {
        const staffName = $("#masterStaffName").val().trim();
        const staffId = $("#masterStaffId").val().trim();
        if (!staffName || !staffId) {
            swal({title: "Missing details", text: "Please enter staff name and staff ID.", type: "warning"});
            return;
        }

        const payload = {
            idBlock: currentBlockId,
            blockMaster: {
                staffName: staffName,
                staffId: staffId,
                appointmentDate: $("#masterAppointmentDate").val() || null,
            },
        };

        showSplash();
        try {
            await fetchPost("assignBlockMaster", payload);
            $("#assignMasterModal").modal("hide");
            await loadBlocks();
            renderMasterSection();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    $(document).on("click", ".end-master-btn", async function () {
        if (!confirm("End this block master's assignment?")) return;
        const idBlockMaster = parseInt($(this).data("id"), 10);

        showSplash();
        try {
            await fetchPost("endBlockMasterAssignment", {val: idBlockMaster});
            await loadBlocks();
            renderMasterSection();
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
            const parsed = JSON.parse(message.slice(jsonStart));
            return parsed.error || message;
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
