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

    function activePrefects(block) {
        return (block.blockPrefects || []).filter(p => !p.exitDate);
    }

    function pastPrefects(block) {
        return (block.blockPrefects || []).filter(p => p.exitDate);
    }

    // ==================== Rendering ====================

    function renderBlocks() {
        const container = $("#blocksPositionsContainer");
        container.empty();
        $("#positionsEmptyState").toggle(allBlocks.length === 0);

        allBlocks.forEach(block => {
            const prefects = activePrefects(block);
            const pastP = pastPrefects(block);

            let prefectsHtml = '';
            if (prefects.length === 0) {
                prefectsHtml = '<div class="list-group-item text-muted">No active prefects.</div>';
            } else {
                prefects.forEach(p => {
                    prefectsHtml += `<div class="list-group-item">
                        <strong>${p.studentName}</strong> (${p.studentId})
                        <br><span class="text-muted small">Since ${p.appointmentDate || 'N/A'}</span>
                        <button class="btn btn-xs btn-danger pull-right end-prefect-btn" data-id="${p.idBlockPrefect}">End</button>
                    </div>`;
                });
            }

            const historyId = `history-${block.idBlock}`;

            const card = $(`
                <div class="hpanel" data-block-id="${block.idBlock}" style="margin-bottom:20px;">
                    <div class="panel-heading">
                        <h4 style="margin:0;">${block.name}</h4>
                    </div>
                    <div class="panel-body">
                        <h5>
                            Prefects
                            <button class="btn btn-xs btn-primary pull-right add-prefect-btn">Add Prefect</button>
                        </h5>
                        <div class="list-group">${prefectsHtml}</div>

                        ${pastP.length > 0 ? `
                        <p style="margin-top:10px;">
                            <a href="#" class="toggle-history" data-target="${historyId}">Show history (${pastP.length})</a>
                        </p>
                        <div class="list-group" id="${historyId}" style="display:none;"></div>
                        ` : ''}
                    </div>
                </div>
            `);

            if (pastP.length > 0) {
                const historyList = card.find(`#${historyId}`);
                pastP.forEach(p => {
                    historyList.append(`<div class="list-group-item text-muted">
                        ${p.studentName} (${p.studentId}) — ${p.appointmentDate || 'N/A'} to ${p.exitDate}
                    </div>`);
                });
            }

            container.append(card);
        });
    }

    // ==================== Block Prefect ====================

    $(document).on("click", ".add-prefect-btn", function () {
        currentBlockId = parseInt($(this).closest("[data-block-id]").data("block-id"), 10);
        $("#prefectStudentName").val("");
        $("#prefectStudentId").val("");
        $("#prefectAppointmentDate").val("");
        $("#assignPrefectModal").modal("show");
    });

    $("#savePrefectBtn").click(async () => {
        const studentName = $("#prefectStudentName").val().trim();
        const studentId = $("#prefectStudentId").val().trim();
        if (!studentName || !studentId) {
            swal({title: "Missing details", text: "Please enter student name and student ID.", type: "warning"});
            return;
        }

        const payload = {
            idBlock: currentBlockId,
            blockPrefect: {
                studentName: studentName,
                studentId: studentId,
                appointmentDate: $("#prefectAppointmentDate").val() || null,
            },
        };

        showSplash();
        try {
            await fetchPost("assignBlockPrefect", payload);
            $("#assignPrefectModal").modal("hide");
            await loadBlocks();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    $(document).on("click", ".end-prefect-btn", async function () {
        if (!confirm("End this prefect's appointment?")) return;
        const idBlockPrefect = parseInt($(this).data("id"), 10);

        showSplash();
        try {
            await fetchPost("endBlockPrefectAssignment", {val: idBlockPrefect});
            await loadBlocks();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== History toggle ====================

    $(document).on("click", ".toggle-history", function (e) {
        e.preventDefault();
        const target = $(this).data("target");
        $(`#${target}`).slideToggle();
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
