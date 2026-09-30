var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");

$(function () {
    var allBlocks = [];
    var allAssignments = [];   // every student-room assignment for this institution
    var currentBlockId = null;
    var currentRoomId = null;

    loadBlocks();

    // ==================== Data loading ====================

    async function loadBlocks() {
        showSplash();
        try {
            const [blocks, assignments] = await Promise.all([
                fetchPost("getInstitutionAccommodation", {val: instId}),
                fetchPost("getInstitutionRoomAssignments", {val: instId}),
            ]);
            allBlocks = blocks || [];
            allAssignments = assignments || [];
            renderBlocks();
        } catch (error) {
            console.error("Failed to load accommodation data:", error);
            showError("Could not load accommodation data. Please refresh and try again.");
        }
        hideSplash();
    }

    function occupantsForRoom(idBlockRoom) {
        return allAssignments.filter(a => a.blockRoom && a.blockRoom.idBlockRoom === idBlockRoom);
    }

    // ==================== Rendering ====================

    function renderBlocks() {
        const container = $("#blocksContainer");
        container.empty();
        $("#blocksEmptyState").toggle(allBlocks.length === 0);

        allBlocks.forEach(block => {
            const roomCount = (block.roomsList || []).length;
            const totalCapacity = (block.roomsList || []).reduce(
                (sum, r) => sum + (r.reservedBeds || 0) + (r.generalBeds || 0), 0);
            const totalOccupied = (block.roomsList || []).reduce(
                (sum, r) => sum + occupantsForRoom(r.idBlockRoom).length, 0);

            const genderBadge = block.gender
                ? `<span class="label label-info">${block.gender}</span>`
                : '<span class="label label-default">Mixed</span>';

            const card = $(`
                <div class="col-md-4">
                    <div class="panel panel-default block-card" data-block-id="${block.idBlock}">
                        <div class="panel-body">
                            <h4>${block.name} ${genderBadge}</h4>
                            ${block.slogan ? `<p class="text-muted small">${block.slogan}</p>` : ''}
                            <p>${roomCount} room(s) &middot; ${totalOccupied}/${totalCapacity} beds occupied</p>
                            <button class="btn btn-sm btn-default view-rooms-btn">
                                <i class="fa fa-bed"></i> Rooms
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
            tbody.append('<tr><td colspan="6" class="text-muted text-center">No rooms in this block yet. Add rooms under Setup &gt; Accommodation.</td></tr>');
            return;
        }

        block.roomsList.forEach(room => {
            const capacity = (room.reservedBeds || 0) + (room.generalBeds || 0);
            const occupied = occupantsForRoom(room.idBlockRoom).length;
            const row = $(`
                <tr data-room-id="${room.idBlockRoom}">
                    <td>${room.name}</td>
                    <td>${room.reservedBeds || 0}</td>
                    <td>${room.generalBeds || 0}</td>
                    <td>${occupied}</td>
                    <td>${capacity}</td>
                    <td>
                        <button class="btn btn-xs btn-primary view-occupants-btn">Students</button>
                    </td>
                </tr>
            `);
            tbody.append(row);
        });
    }

    function renderOccupants() {
        const list = $("#occupantsList");
        list.empty();
        const occupants = occupantsForRoom(currentRoomId);

        if (occupants.length === 0) {
            list.append('<li class="list-group-item text-muted">No students assigned to this room yet.</li>');
            return;
        }

        occupants.forEach(o => {
            list.append(`
                <li class="list-group-item" data-assignment-id="${o.idBlockRoomStudent}">
                    ${o.studentID}
                    <button class="btn btn-xs btn-danger pull-right unassign-btn">Remove</button>
                </li>
            `);
        });
    }

    // ==================== Rooms in Block ====================

    $(document).on("click", ".view-rooms-btn", function () {
        currentBlockId = parseInt($(this).closest(".block-card").data("block-id"), 10);
        const block = allBlocks.find(b => b.idBlock === currentBlockId);
        $("#manageBlockTitle").text((block ? block.name : "Block") + " - Rooms");
        renderRoomsTable();
        $("#manageBlockModal").modal("show");
    });

    // ==================== Room Occupants / Student Assignment ====================

    $(document).on("click", ".view-occupants-btn", function () {
        currentRoomId = parseInt($(this).closest("tr").data("room-id"), 10);
        const block = allBlocks.find(b => b.idBlock === currentBlockId);
        const room = block && block.roomsList ? block.roomsList.find(r => r.idBlockRoom === currentRoomId) : null;
        $("#roomOccupantsTitle").text("Students in " + (room ? room.name : "Room"));
        $("#assignStudentIdInput").val("");
        $("#assignError").hide();
        renderOccupants();
        $("#roomOccupantsModal").modal("show");
    });

    $("#assignStudentBtn").click(async () => {
        const studentID = $("#assignStudentIdInput").val().trim();
        if (!studentID) {
            swal({title: "Missing student ID", text: "Please enter a student ID.", type: "warning"});
            return;
        }

        const payload = {
            studentID: studentID,
            institutionID: instId,
            idBlockRoom: currentRoomId,
        };

        showSplash();
        try {
            await fetchPost("assignStudentToRoom", payload);
            await loadBlocks();
            renderRoomsTable();
            renderOccupants();
            $("#assignStudentIdInput").val("");
            $("#assignError").hide();
        } catch (error) {
            $("#assignError").text(parseBackendError(error)).show();
        }
        hideSplash();
    });

    $(document).on("click", ".unassign-btn", async function () {
        const idBlockRoomStudent = parseInt($(this).closest("li").data("assignment-id"), 10);
        showSplash();
        try {
            await fetchPost("unassignStudent", {val: idBlockRoomStudent});
            await loadBlocks();
            renderRoomsTable();
            renderOccupants();
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
