var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");

$(function () {
    var allBlocks = [];
    var allDutyTypes = [];
    var currentRoster = [];

    init();

    async function init() {
        showSplash();
        try {
            const [blocks, dutyTypes] = await Promise.all([
                fetchPost("getInstitutionAccommodation", {val: instId}),
                fetchPost("getDutyTypes", {val: instId}),
            ]);
            allBlocks = blocks || [];
            allDutyTypes = dutyTypes || [];
            populateBlockSelects();
            populateDutyTypeSelect();
        } catch (error) {
            console.error("Failed to load duty roster setup data:", error);
            showError("Could not load blocks and duty types. Please refresh and try again.");
        }
        hideSplash();
    }

    function populateBlockSelects() {
        const opts = allBlocks.map(b => `<option value="${b.idBlock}">${b.name}</option>`).join('');
        $("#rosterBlockSelect").find("option:not(:first)").remove().end().append(opts);
        $("#rotationBlockSelect").find("option:not(:first)").remove().end().append(opts);
    }

    function populateDutyTypeSelect() {
        const opts = allDutyTypes.map(d => `<option value="${d.idDutyType}">${d.name}</option>`).join('');
        $("#rotationDutyTypeSelect").find("option:not(:first)").remove().end().append(opts);
    }

    // ==================== Roster view ====================

    $("#rosterBlockSelect").on("change", loadRoster);

    async function loadRoster() {
        const idBlock = $("#rosterBlockSelect").val();
        if (!idBlock) {
            $("#rosterTable").hide();
            $("#rosterEmptyState").hide();
            return;
        }

        showSplash();
        try {
            currentRoster = await fetchPost("getBlockDutyRoster", {val: parseInt(idBlock, 10)}) || [];
            renderRoster();
        } catch (error) {
            console.error("Failed to load roster:", error);
            showError("Could not load the duty roster for this block.");
        }
        hideSplash();
    }

    function renderRoster() {
        const tbody = $("#rosterTableBody");
        tbody.empty();

        if (currentRoster.length === 0) {
            $("#rosterTable").hide();
            $("#rosterEmptyState").show();
            return;
        }
        $("#rosterTable").show();
        $("#rosterEmptyState").hide();

        // Sort by week, then student name, so a block's roster reads as a
        // calendar rather than in whatever order the backend returned rows.
        const sorted = [...currentRoster].sort((a, b) =>
            (a.weekStartDate || '').localeCompare(b.weekStartDate || '') ||
            (a.studentName || '').localeCompare(b.studentName || ''));

        sorted.forEach(row => {
            const statusBadge = row.status === 'COMPLETED' ? 'label-success'
                : row.status === 'MISSED' ? 'label-danger' : 'label-default';

            tbody.append(`
                <tr data-id="${row.idDutyAssignment}">
                    <td>${row.weekStartDate || ''}</td>
                    <td>${row.dutyTypeName || ''}</td>
                    <td>${row.studentName || ''} (${row.studentId})</td>
                    <td><span class="label ${statusBadge}">${row.status}</span></td>
                    <td>
                        <button class="btn btn-xs btn-success mark-completed-btn" title="Mark completed"><i class="fa fa-check"></i></button>
                        <button class="btn btn-xs btn-warning mark-missed-btn" title="Mark missed"><i class="fa fa-times"></i></button>
                        <button class="btn btn-xs btn-danger delete-assignment-btn" title="Delete"><i class="fa fa-trash"></i></button>
                    </td>
                </tr>
            `);
        });
    }

    $(document).on("click", ".mark-completed-btn", function () {
        updateStatus(parseInt($(this).closest("tr").data("id"), 10), "COMPLETED");
    });
    $(document).on("click", ".mark-missed-btn", function () {
        updateStatus(parseInt($(this).closest("tr").data("id"), 10), "MISSED");
    });

    async function updateStatus(idDutyAssignment, status) {
        showSplash();
        try {
            await fetchPost("updateDutyStatus", {idDutyAssignment, status});
            await loadRoster();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    }

    $(document).on("click", ".delete-assignment-btn", async function () {
        if (!confirm("Delete this single week's duty assignment?")) return;
        const idDutyAssignment = parseInt($(this).closest("tr").data("id"), 10);

        showSplash();
        try {
            await fetchPost("deleteDutyAssignment", {val: idDutyAssignment});
            await loadRoster();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== Duty Types management ====================

    $("#manageDutyTypesBtn").click(() => {
        renderDutyTypesList();
        $("#newDutyTypeName").val("");
        $("#dutyTypesModal").modal("show");
    });

    function renderDutyTypesList() {
        const list = $("#dutyTypesList");
        list.empty();
        if (allDutyTypes.length === 0) {
            list.append('<li class="list-group-item text-muted">No duty types yet.</li>');
            return;
        }
        allDutyTypes.forEach(d => {
            list.append(`
                <li class="list-group-item" data-id="${d.idDutyType}">
                    ${d.name}
                    <button class="btn btn-xs btn-danger pull-right delete-duty-type-btn">Delete</button>
                </li>
            `);
        });
    }

    $("#addDutyTypeBtn").click(async () => {
        const name = $("#newDutyTypeName").val().trim();
        if (!name) {
            swal({title: "Missing name", text: "Please enter a duty type name.", type: "warning"});
            return;
        }

        showSplash();
        try {
            await fetchPost("addDutyType", {institutionCode: instId, name});
            allDutyTypes = await fetchPost("getDutyTypes", {val: instId}) || [];
            populateDutyTypeSelect();
            renderDutyTypesList();
            $("#newDutyTypeName").val("");
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    $(document).on("click", ".delete-duty-type-btn", async function () {
        if (!confirm("Delete this duty type? Existing roster entries using it are unaffected.")) return;
        const idDutyType = parseInt($(this).closest("li").data("id"), 10);

        showSplash();
        try {
            await fetchPost("deleteDutyType", {val: idDutyType});
            allDutyTypes = await fetchPost("getDutyTypes", {val: instId}) || [];
            populateDutyTypeSelect();
            renderDutyTypesList();
        } catch (error) {
            showError(parseBackendError(error));
        }
        hideSplash();
    });

    // ==================== Create Rotation ====================

    $("#createRotationBtn").click(() => {
        $("#rotationStudentName").val("");
        $("#rotationStudentId").val("");
        $("#rotationStartWeek").val("");
        $("#rotationWeeks").val(4);
        const preselected = $("#rosterBlockSelect").val();
        if (preselected) $("#rotationBlockSelect").val(preselected);
        $("#createRotationModal").modal("show");
    });

    $("#saveRotationBtn").click(async () => {
        const idBlock = parseInt($("#rotationBlockSelect").val(), 10);
        const idDutyType = parseInt($("#rotationDutyTypeSelect").val(), 10);
        const studentName = $("#rotationStudentName").val().trim();
        const studentId = $("#rotationStudentId").val().trim();
        const startWeek = $("#rotationStartWeek").val();
        const numberOfWeeks = parseInt($("#rotationWeeks").val(), 10) || 1;

        if (!idBlock || !idDutyType || !studentId || !startWeek) {
            swal({title: "Missing details", text: "Block, duty type, student ID, and start week are required.", type: "warning"});
            return;
        }

        const payload = {
            institutionCode: instId,
            idBlock, idDutyType, studentId, studentName,
            startWeek, numberOfWeeks,
        };

        showSplash();
        try {
            await fetchPost("createDutyRotation", payload);
            $("#createRotationModal").modal("hide");
            if ($("#rosterBlockSelect").val() == idBlock) {
                await loadRoster();
            }
            swal({title: "Assigned", text: `Rotation created for ${numberOfWeeks} week(s).`, type: "success"});
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
