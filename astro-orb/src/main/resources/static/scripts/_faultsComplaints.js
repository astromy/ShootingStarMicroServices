var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");

$(function () {
    var allBlocks = [];
    var allTickets = [];
    var currentFilter = "";
    var currentTicketId = null;

    init();

    async function init() {
        showSplash();
        try {
            const [blocks, tickets] = await Promise.all([
                fetchPost("getInstitutionAccommodation", {val: instId}),
                fetchPost("getInstitutionTickets", {val: instId}),
            ]);
            allBlocks = blocks || [];
            allTickets = tickets || [];

            const opts = allBlocks.map(b => `<option value="${b.idBlock}">${b.name}</option>`).join('');
            $("#ticketBlockSelect").find("option:not(:first)").remove().end().append(opts);

            renderTickets();
        } catch (error) {
            console.error("Failed to load tickets:", error);
            showError("Could not load tickets. Please refresh and try again.");
        }
        hideSplash();
    }

    $("#ticketBlockSelect").on("change", function () {
        const idBlock = parseInt($(this).val(), 10);
        const block = allBlocks.find(b => b.idBlock === idBlock);
        const rooms = block ? (block.roomsList || []) : [];
        const roomSelect = $("#ticketRoomSelect");
        roomSelect.find("option:not(:first)").remove();
        rooms.forEach(r => roomSelect.append(`<option value="${r.idBlockRoom}">${r.name}</option>`));
    });

    // ==================== Filtering / Rendering ====================

    $(document).on("click", ".status-filter-btn", function () {
        $(".status-filter-btn").removeClass("btn-primary active-tab").addClass("btn-default");
        $(this).removeClass("btn-default").addClass("btn-primary active-tab");
        currentFilter = $(this).data("status");
        renderTickets();
    });

    function renderTickets() {
        const container = $("#ticketsContainer");
        container.empty();

        const filtered = currentFilter
            ? allTickets.filter(t => t.status === currentFilter)
            : allTickets;

        $("#ticketsEmptyState").toggle(filtered.length === 0);

        // Newest first
        const sorted = [...filtered].sort((a, b) => (b.dateRaised || '').localeCompare(a.dateRaised || ''));

        sorted.forEach(t => {
            const statusBadge = t.status === 'OPEN' ? 'label-danger'
                : t.status === 'IN_PROGRESS' ? 'label-warning' : 'label-success';
            const priorityBadge = t.priority === 'HIGH' ? 'label-danger'
                : t.priority === 'MEDIUM' ? 'label-warning' : 'label-default';

            container.append(`
                <div class="hpanel" data-id="${t.idTicket}" style="margin-bottom:12px;">
                    <div class="panel-body">
                        <h5 style="margin:0 0 5px 0;">
                            ${t.title}
                            <span class="label ${statusBadge}">${t.status}</span>
                            <span class="label ${priorityBadge}">${t.priority}</span>
                        </h5>
                        <p class="text-muted small" style="margin-bottom:5px;">
                            ${t.blockName || ''}${t.roomName ? ' &middot; ' + t.roomName : ''}
                            &middot; Raised ${t.dateRaised || ''}
                            ${t.raisedByStaffName ? ' by ' + t.raisedByStaffName : ''}
                        </p>
                        ${t.description ? `<p style="margin-bottom:5px;">${t.description}</p>` : ''}
                        ${t.resolutionNotes ? `<p class="text-success small"><strong>Resolution:</strong> ${t.resolutionNotes}</p>` : ''}
                        <button class="btn btn-xs btn-default update-ticket-btn">Update Status</button>
                    </div>
                </div>
            `);
        });
    }

    // ==================== Raise Ticket ====================

    $("#raiseTicketBtn").click(() => {
        $("#ticketBlockSelect").val("");
        $("#ticketRoomSelect").find("option:not(:first)").remove();
        $("#ticketTitleInput").val("");
        $("#ticketDescriptionInput").val("");
        $("#ticketPrioritySelect").val("MEDIUM");
        $("#ticketRaisedByInput").val("");
        $("#ticketModal").modal("show");
    });

    $("#saveTicketBtn").click(async () => {
        const idBlock = parseInt($("#ticketBlockSelect").val(), 10);
        const title = $("#ticketTitleInput").val().trim();

        if (!idBlock || !title) {
            swal({title: "Missing details", text: "Block and title are required.", type: "warning"});
            return;
        }

        const idBlockRoom = $("#ticketRoomSelect").val();
        const payload = {
            institutionCode: instId,
            idBlock,
            idBlockRoom: idBlockRoom ? parseInt(idBlockRoom, 10) : null,
            title,
            description: $("#ticketDescriptionInput").val().trim(),
            priority: $("#ticketPrioritySelect").val(),
            raisedByStaffName: $("#ticketRaisedByInput").val().trim(),
        };

        showSplash();
        try {
            await fetchPost("raiseTicket", payload);
            $("#ticketModal").modal("hide");
            allTickets = await fetchPost("getInstitutionTickets", {val: instId}) || [];
            renderTickets();
            swal({title: "Ticket Raised", text: "The issue has been logged.", type: "success"});
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== Update / Resolve ====================

    $(document).on("click", ".update-ticket-btn", function () {
        currentTicketId = parseInt($(this).closest("[data-id]").data("id"), 10);
        const ticket = allTickets.find(t => t.idTicket === currentTicketId);
        if (!ticket) return;

        $("#resolveStatusSelect").val(ticket.status);
        $("#resolveNotesInput").val(ticket.resolutionNotes || "");
        $("#resolveTicketModal").modal("show");
    });

    $("#saveResolveBtn").click(async () => {
        const payload = {
            idTicket: currentTicketId,
            status: $("#resolveStatusSelect").val(),
            resolutionNotes: $("#resolveNotesInput").val().trim(),
        };

        showSplash();
        try {
            await fetchPost("updateTicketStatus", payload);
            $("#resolveTicketModal").modal("hide");
            allTickets = await fetchPost("getInstitutionTickets", {val: instId}) || [];
            renderTickets();
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
