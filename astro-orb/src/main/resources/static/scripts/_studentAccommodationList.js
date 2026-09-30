var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");

$(function () {
    var allBlocks = [];
    var allAssignments = [];

    loadData();

    async function loadData() {
        showSplash();
        try {
            const [blocks, assignments] = await Promise.all([
                fetchPost("getInstitutionAccommodation", {val: instId}),
                fetchPost("getInstitutionRoomAssignments", {val: instId}),
            ]);
            allBlocks = blocks || [];
            allAssignments = assignments || [];
            populateBlockFilter();
            render();
        } catch (error) {
            console.error("Failed to load student list:", error);
            showError("Could not load the student list. Please refresh and try again.");
        }
        hideSplash();
    }

    function populateBlockFilter() {
        const select = $("#blockFilterSelect");
        select.find("option:not(:first)").remove();
        allBlocks.forEach(b => {
            select.append(`<option value="${b.idBlock}">${b.name}</option>`);
        });
    }

    $("#blockFilterSelect").on("change", render);

    // A room can hold assignments whose blockRoom the backend didn't
    // resolve (a stale/deleted room reference) - those are skipped here
    // rather than shown under a phantom room.
    function render() {
        const container = $("#studentListContainer");
        container.empty();

        const filterBlockId = $("#blockFilterSelect").val();
        const blocksToShow = filterBlockId
            ? allBlocks.filter(b => String(b.idBlock) === filterBlockId)
            : allBlocks;

        let anyStudents = false;

        blocksToShow.forEach(block => {
            const roomIds = new Set((block.roomsList || []).map(r => r.idBlockRoom));
            const blockAssignments = allAssignments.filter(a => a.blockRoom && roomIds.has(a.blockRoom.idBlockRoom));
            if (blockAssignments.length === 0) return;
            anyStudents = true;

            const byRoom = {};
            blockAssignments.forEach(a => {
                const roomName = a.blockRoom.name;
                if (!byRoom[roomName]) byRoom[roomName] = [];
                byRoom[roomName].push(a);
            });

            let roomsHtml = '';
            Object.keys(byRoom).sort().forEach(roomName => {
                const students = byRoom[roomName];
                roomsHtml += `
                    <h5 style="margin-top:15px;">${roomName} <span class="text-muted small">(${students.length})</span></h5>
                    <table class="table table-condensed table-striped">
                        <tbody>
                            ${students.map(s => `<tr><td>${s.studentID}</td></tr>`).join('')}
                        </tbody>
                    </table>
                `;
            });

            container.append(`
                <div class="hpanel" style="margin-bottom:20px;">
                    <div class="panel-heading"><h4 style="margin:0;">${block.name}</h4></div>
                    <div class="panel-body">${roomsHtml}</div>
                </div>
            `);
        });

        $("#studentListEmptyState").toggle(!anyStudents);
    }

    // ==================== Helpers ====================

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
