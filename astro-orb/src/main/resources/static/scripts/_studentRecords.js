window.copyrights();

var v;                          // institution code
var allClasses = [];            // holds classes fetched for dropdowns

// ─── INIT ────────────────────────────────────────────────────────────────────

fetchLookup(instId.split(",")[0]);

// ─── FETCH CLASS GROUPS & POPULATE DROPDOWNS ─────────────────────────────────

async function fetchLookup(instId) {
    showSplash();
    v = instId.replace(/[\[\]']+/g, "").replace(/\//g, "");
    var instRequest = {val: "ClassGroup"};
    return fetchPost("getLookUpByType", instRequest).then(function (result) {
        populateClassGroup(result);
        hideSplash();
    });
}

function populateClassGroup(data) {
    data.forEach(function (d) {
        // Use d.name as value so the payload receives the group name, not the id
        var option = $("<option>").val(d.name).text(d.name);
        $(".classGroupSelect").append(option);
    });
}

// On class group change — load classes for that group
document.querySelector(".classGroupSelect").addEventListener("change", async function () {
    showSplash();
    $(".classSelect").find("option:not(:first)").remove();

    var instRequest2 = {
        institution: v,
        classGroup: this.value,
    };
    try {
        const result = await fetchPost("getInstitutionClassesByClassGroup", instRequest2);
        populateClasses(result);
        // Also populate the edit modal class dropdown
        populateEditClassDropdown(result);
    } catch (error) {
        console.error("Error fetching classes:", error);
    }
    hideSplash();
});

function populateClasses(data) {
    allClasses = data;
    data.forEach(function (d) {
        var option = $("<option>").val(d.name).text(d.name);
        $(".classSelect").append(option);
    });
}

function populateEditClassDropdown(data) {
    $("#editStudentClass").find("option:not(:first)").remove();
    data.forEach(function (d) {
        var option = $("<option>").val(d.name).text(d.name);
        $("#editStudentClass").append(option);
    });
}

// ─── FETCH STUDENTS (server-side paging) ──────────────────────────────────────
// The table asks the server for one page at a time (getStudentsPage); searching, sorting and paging are
// done there. Nothing but the rows on screen is ever downloaded, so it opens at once for any school size.

var studentsTable = null;
var studentFilters = {};

document.getElementById("fetchStudentsBtn").addEventListener("click", function () {
    var studentClass = document.querySelector(".classSelect").value;
    var status = document.querySelector(".studentStatusSelect").value;
    var gender = document.querySelector(".genderSelect").value;

    studentFilters = {};
    if (studentClass && !studentClass.toLowerCase().includes("select")) studentFilters.studentClass = studentClass;
    if (status && !status.toLowerCase().includes("select")) studentFilters.status = status;
    if (gender && !gender.toLowerCase().includes("select")) studentFilters.gender = gender;

    if (studentsTable) studentsTable.destroy();
    studentsTable = ServerTable.create({
        table: "#studentRecordsTable",
        endpoint: "getStudentsPage",
        title: "Student Records",
        order: [[1, "asc"]],
        params: function () { return studentFilters; },
        columns: [
            {data: "studentId", sort: "studentId"},
            {data: "lastName", sort: "lastName"},
            {data: "firstName", sort: "firstName"},
            {data: "otherName", sort: "otherName"},
            {data: "gender", sort: "gender"},
            {data: "studentClass", sort: "studentClass"},
            {data: "dateOfBirth", sort: "dateOfBirth"},
            {data: "dateOfAdmission", sort: "dateOfAdmission"},
            {data: "placeOfBirth", sort: "placeOfBirth"},
            {data: "countryOfBirth", sort: "countryOfBirth"},
            // Nationality mirrors Country of Birth - the Students model has no separate nationality field.
            {data: "nationality", sort: "countryOfBirth"},
            {data: "denomination", sort: "denomination"},
            {
                data: "status", sort: "status",
                render: function (value) {
                    return '<span class="label ' + statusLabel(value) + '">' + ServerTable.esc(value) + '</span>';
                }
            },
            {
                data: null, exportable: false,
                render: function (value, row) {
                    return '<button class="btn btn-xs btn-warning view-student-btn" data-student-id="' +
                        ServerTable.esc(row.studentId) + '"><i class="fa fa-pencil"></i> Edit</button>';
                }
            }
        ],
        onResult: function (result, firstLoad) {
            if (firstLoad && !result.total) {
                swal({title: "No Results", text: "No students found for the selected parameters.", type: "info"});
            }
        }
    });
});

function statusLabel(status) {
    switch ((status || '').toLowerCase()) {
        case 'active':
            return 'label-success';
        case 'suspended':
            return 'label-warning';
        case 'dismissed':
            return 'label-danger';
        case 'completed':
            return 'label-primary';
        default:
            return 'label-default';
    }
}

// ─── EXPORT BUTTON ────────────────────────────────────────────────────────────

document.getElementById("exportStudentsBtn").addEventListener("click", function () {
    if (studentsTable) {
        studentsTable.exportAll("csv");
    } else {
        swal({title: "No Data", text: "Please fetch student records first.", type: "info"});
    }
});

// ─── LOAD CLASSES INTO EDIT MODAL ────────────────────────────────────────────

// Fetches all classes for this institution and populates the edit modal dropdown.
// Pre-selects currentClass if provided.
async function loadModalClasses(currentClass) {
    try {
        var instRequest = {val: v};
        const result = await fetchPost("getInstitutionClasses", instRequest);
        var select = $("#editStudentClass");
        select.find("option:not(:first)").remove();
        result.forEach(function (d) {
            var option = $("<option>").val(d.name).text(d.name);
            select.append(option);
        });
        if (currentClass) {
            select.val(currentClass);
        }
    } catch (error) {
        console.error("Error loading classes for modal:", error);
    }
}

// ─── VIEW / EDIT MODAL ───────────────────────────────────────────────────────

// Rows are redrawn on every page, so one delegated handler serves them all
$(document).off("click.studentEdit").on("click.studentEdit", "#studentRecordsTable .view-student-btn", function () {
    openStudentModal($(this).attr("data-student-id"));
});

async function openStudentModal(studentId) {
    // The table only holds slim rows; the full record (picture, birth certificate, parents) is fetched
    // for the one student being edited.
    showSplash();
    var student = null;
    try {
        student = await fetchPost("getStudentDetail", {studentId: studentId});
    } catch (error) {
        console.error("Error fetching student:", error);
    }
    hideSplash();
    if (!student || !student.studentId) {
        swal({title: "Error", text: "Could not load this student's record.", type: "error"});
        return;
    }

    // Set modal title to student's name so it's clear whose record is open
    $("#studentModalTitle").text(
        "Edit: " + (student.firstName || '') + " " + (student.otherName || '') + " " + (student.lastName || '')
    );
    $("#studentModalSubtitle").text("Student ID: " + (student.studentId || ''));

    // Always load all institution classes for the edit dropdown,
    // independent of what is selected in the filter header
    await loadModalClasses(student.studentClass);

    // Reset tabs
    showTab("bioTab");

    // Room tab data loads in the background - it's only shown if the user
    // clicks over to that tab, but starting the fetch now (rather than on
    // tab-click) means it's usually already there by the time they do.
    loadRoomTabSection(student);

    // Populate bio data fields
    $("#editStudentId").val(student.studentId || '');
    $("#editFirstName").val(student.firstName || '');
    $("#editOtherName").val(student.otherName || '');
    $("#editLastName").val(student.lastName || '');
    $("#editDOB").val(student.dateOfBirth || '');
    $("#editDOA").val(student.dateOfAdmission || '');
    $("#editGender").val(student.gender || '');
    $("#editPlaceOfBirth").val(student.placeOfBirth || '');
    $("#editCountryOfBirth").val(student.countryOfBirth || '');
    // Nationality is a read-only mirror of Country of Birth - there's no
    // separate nationality field on the backend. Populated from the same
    // value, kept in sync below if Country of Birth is edited, and never
    // sent back on save (see the payload further down).
    $("#editNationality").val(student.countryOfBirth || '');
    // Keep Nationality mirroring Country of Birth live while this form is
    // open. .off() first so re-opening the modal for another student
    // doesn't stack a duplicate handler on top of the last one.
    $("#editCountryOfBirth").off("input.nationalityMirror").on("input.nationalityMirror", function () {
        $("#editNationality").val($(this).val());
    });
    $("#editDenomination").val(student.denomination || '');
    $("#editResidentialLocality").val(student.residentialLocality || '');
    $("#editStudentClass").val(student.studentClass || '');
    $("#editStatus").val(student.status || '');

    // Picture: shown from the cacheable photo endpoint instead of being carried in the record.
    // A student with no picture answers 404 and the placeholder shows.
    var picOutput = document.querySelector(".studentPicOutput");
    picOutput.innerHTML = `<img src="student-photo/${encodeURIComponent(student.studentId)}?size=thumb"
                                style="width:180px;height:180px;border-radius:10px;object-fit:cover;"
                                onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">
                           <div class="placeholder-avatar" style="display:none;height:180px;width:180px;
                                background:#ccc;align-items:center;justify-content:center;border-radius:10px;">
                                <i class="fa fa-user fa-4x" style="color:#fff;"></i>
                           </div>`;

    // Birth certificate: the record only says whether one is on file (its file name). The PDF itself is
    // not downloaded; choosing a file below replaces it, leaving the box empty keeps the stored one.
    $(".birthCertOutput").empty();
    document.querySelector(".birthCertInput").value = "";
    if (student.birthCert && typeof student.birthCert === 'string') {
        $(".birthCertOutput").append(
            $("<div>").css({border: "1px dashed #ccc", padding: "10px"})
                .append($("<i>").addClass("fa fa-file-pdf-o"))
                .append(document.createTextNode(" Birth certificate on file: " + student.birthCert + ". Choose a file to replace it.")));
    }

    // Parents tab
    populateParentsTable(student.studentParents || []);

    // Store reference for save
    $("#studentModal").data("student", student);

    $("#studentModal").modal("show");
}

function populateParentsTable(parents) {
    var tbody = $("#parentsTableBody").empty();
    if (!parents || parents.length === 0) {
        tbody.append(`<tr><td colspan="6" class="text-center text-muted">No parent records found</td></tr>`);
        return;
    }
    parents.forEach(function (p) {
        var row = `
            <tr>
                <td>${(p.firstNames || '')} ${(p.lastName || '')}</td>
                <td>${p.parentType || ''}</td>
                <td>${p.contact1 || ''}</td>
                <td>${p.contact2 || ''}</td>
                <td>${p.email || ''}</td>
                <td>${p.occupation || ''}</td>
            </tr>
        `;
        tbody.append(row);
    });
}

// ─── TAB SWITCHING ───────────────────────────────────────────────────────────

document.querySelectorAll(".studentTabBtn").forEach(function (btn) {
    btn.addEventListener("click", function () {
        showTab(this.dataset.tab);
        document.querySelectorAll(".studentTabBtn").forEach(function (b) {
            b.classList.remove("btn-primary");
            b.classList.add("btn-default");
        });
        this.classList.remove("btn-default");
        this.classList.add("btn-primary");
    });
});

function showTab(tabId) {
    document.querySelectorAll(".student-tab-pane").forEach(function (pane) {
        pane.style.display = "none";
    });
    var target = document.getElementById(tabId);
    if (target) target.style.display = "block";
}

// ─── ROOM TAB ────────────────────────────────────────────────────────────────
// Lets a student's room be reassigned from their record directly, using the
// same accommodation endpoints as Room Allocation and the admission modal -
// this is the third place that widget appears, per the "student record edit
// function" requirement.

async function loadRoomTabSection(student) {
    var statusEl = $("#roomTabStatus");
    statusEl.text("Loading rooms…");
    $("#editUnassignRoomBtn").hide().data("assignment-id", null);

    try {
        var results = await Promise.all([
            fetchPost("getInstitutionAccommodation", {val: v}),
            fetchPost("getInstitutionRoomAssignments", {val: v}),
        ]);
        var blocks = results[0] || [];
        var assignments = results[1] || [];

        var blockSelect = $("#editBlockSelect");
        blockSelect.find("option:not(:first)").remove();
        blocks.forEach(function (b) {
            blockSelect.append('<option value="' + b.idBlock + '">' + b.name + '</option>');
        });
        blockSelect.data("blocks", blocks);

        var existing = assignments.find(function (a) {
            return a.studentID === student.studentId;
        });

        if (existing && existing.blockRoom) {
            statusEl.html(
                '<span class="text-success"><i class="fa fa-check-circle"></i> Currently in room <strong>' +
                existing.blockRoom.name + '</strong></span>'
            );
            $("#editUnassignRoomBtn").show().data("assignment-id", existing.idBlockRoomStudent);
        } else {
            statusEl.html('<span class="text-muted">Not currently assigned to a room.</span>');
        }
    } catch (error) {
        console.error("Failed to load rooms for student record:", error);
        statusEl.html('<span class="text-danger">Could not load room list.</span>');
    }
}

$(document).on("change", "#editBlockSelect", function () {
    var idBlock = parseInt($(this).val(), 10);
    var roomSelect = $("#editRoomSelect");
    var blocks = $(this).data("blocks") || [];
    var block = blocks.find(function (b) {
        return b.idBlock === idBlock;
    });

    if (!block) {
        roomSelect.html('<option value="">Select block first</option>').prop("disabled", true);
        return;
    }

    var rooms = block.roomsList || [];
    if (rooms.length === 0) {
        roomSelect.html('<option value="">No rooms in this block</option>').prop("disabled", true);
        return;
    }

    var opts = '<option value="">Select room…</option>';
    rooms.forEach(function (r) {
        opts += '<option value="' + r.idBlockRoom + '">' + r.name + '</option>';
    });
    roomSelect.html(opts).prop("disabled", false);
});

document.getElementById("editAssignRoomBtn").addEventListener("click", async function () {
    var student = $("#studentModal").data("student") || {};
    var idBlockRoom = parseInt($("#editRoomSelect").val(), 10);

    if (!idBlockRoom) {
        swal({title: "Select a room", text: "Please choose a block and room first.", type: "warning"});
        return;
    }

    var payload = {
        studentID: student.studentId,
        institutionID: v,
        idBlockRoom: idBlockRoom,
    };

    showSplash();
    try {
        await fetchPost("assignStudentToRoom", payload);
        await loadRoomTabSection(student);
        swal({title: "Assigned", text: "Room assigned successfully.", type: "success"});
    } catch (error) {
        swal({title: "Could not assign room", text: parseRoomTabError(error), type: "error"});
    }
    hideSplash();
});

document.getElementById("editUnassignRoomBtn").addEventListener("click", async function () {
    var idBlockRoomStudent = $(this).data("assignment-id");
    if (!idBlockRoomStudent) return;
    if (!confirm("Remove this student from their current room?")) return;

    var student = $("#studentModal").data("student") || {};

    showSplash();
    try {
        await fetchPost("unassignStudent", {val: idBlockRoomStudent});
        await loadRoomTabSection(student);
    } catch (error) {
        swal({title: "Could not remove room assignment", text: parseRoomTabError(error), type: "error"});
    }
    hideSplash();
});

function parseRoomTabError(error) {
    var message = error && error.message ? error.message : String(error);
    var jsonStart = message.indexOf("{");
    if (jsonStart === -1) return message;
    try {
        return JSON.parse(message.slice(jsonStart)).error || message;
    } catch (e) {
        return message;
    }
}

// ─── SAVE STUDENT ────────────────────────────────────────────────────────────

document.getElementById("saveStudentBtn").addEventListener("click", async function () {
    showSplash();

    var original = $("#studentModal").data("student") || {};

    // Deduplicate parents by email — server sometimes returns duplicates
    var rawParents = original.studentParents || [];
    var seenEmails = new Set();
    var uniqueParents = rawParents.filter(function (p) {
        var key = (p.email || '') + '|' + (p.firstNames || '') + '|' + (p.lastName || '');
        if (seenEmails.has(key)) return false;
        seenEmails.add(key);
        return true;
    });

    // StudentsImportRequest uses plain strings for dates — no conversion needed
    var updated = {
        id: original.id || '',
        studentId: $("#editStudentId").val(),
        firstName: $("#editFirstName").val(),
        otherName: $("#editOtherName").val(),
        lastName: $("#editLastName").val(),
        dateOfBirth: $("#editDOB").val(),
        dateOfAdmission: $("#editDOA").val(),
        gender: $("#editGender").val(),
        placeOfBirth: $("#editPlaceOfBirth").val(),
        countryOfBirth: $("#editCountryOfBirth").val(),
        // Nationality intentionally not sent - it's a read-only mirror of
        // countryOfBirth in this form, not a real, separately-updatable
        // field. countryOfBirth above is the one value that actually
        // persists.
        denomination: $("#editDenomination").val(),
        residentialLocality: $("#editResidentialLocality").val(),
        studentClass: $("#editStudentClass").val(),
        status: $("#editStatus").val(),
        institutionCode: v,
        parentsRequests: uniqueParents,
    };

    // A new picture or certificate is sent only when one was chosen. Leaving them out keeps the stored
    // ones: the server only changes a field when the request carries a value for it.
    var picInput = document.querySelector(".studentPicInput");
    if (picInput.files && picInput.files.length > 0) {
        var imgJson = await uploadIMGAsJSON(picInput, document.querySelector(".fileError"));
        if (imgJson) {
            updated.picture = imgJson.fileName + "_" + imgJson.fileType + "_" + imgJson.fileContent;
        }
    }

    var certInput = document.querySelector(".birthCertInput");
    if (certInput.files && certInput.files.length > 0) {
        var pdfJson = await uploadPDFAsJSON(certInput, document.querySelector(".fileError"));
        if (pdfJson) {
            updated.birthCert = pdfJson.fileName + "_" + pdfJson.fileType + "_" + pdfJson.fileContent;
        }
    }

    try {
        await fetchPost("updateStudentRecord", updated);
        $("#studentModal").modal("hide");

        // Reload the current page so the row shows the saved values
        if (studentsTable) studentsTable.reload(true);

        swal({title: "Success!", text: "Student record updated successfully.", type: "success"});
    } catch (error) {
        console.error("Error saving student:", error);
        swal({title: "Error", text: "Failed to save student record.", type: "error"});
    }
    hideSplash();
});

// ─── HELPERS ─────────────────────────────────────────────────────────────────

function showSplash() {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
}

function hideSplash() {
    $('.splash').css('display', 'none');
}

// Extracts the raw base64 content from your server format:
// "filename_mimetype_<base64>" or a data URL
function extractBase64(str) {
    if (!str) return '';
    // Standard data URL: "data:image/jpeg;base64,<base64>"
    if (str.includes('base64,')) return str.split('base64,').pop();
    // Server format: "profile.jpg_image/jpeg_<base64>"
    // Split on the mime type pattern to get everything after it
    var mimeMatch = str.match(/_(image\/[^_]+)_(.+)$/s);
    if (mimeMatch) return mimeMatch[2];
    // Fallback: last underscore-separated segment
    var parts = str.split('_');
    return parts[parts.length - 1];
}

function base64ToBlob(base64, mimeType) {
    var byteChars = atob(base64);
    var byteNumbers = new Array(byteChars.length);
    for (var i = 0; i < byteChars.length; i++) {
        byteNumbers[i] = byteChars.charCodeAt(i);
    }
    return new Blob([new Uint8Array(byteNumbers)], {type: mimeType});
}

function placeholderAvatar() {
    return '<div class="placeholder-avatar" style="height:180px;width:180px;background:#ccc;display:flex;align-items:center;justify-content:center;border-radius:10px;"><i class="fa fa-user fa-4x" style="color:#fff;"></i></div>';
}

// Safe PDF preview that does not rely on the hardcoded global #error element
function uploadPDFSafe(fileInput, errorEl) {
    if (!fileInput || !fileInput.files || !fileInput.files[0]) return;
    var file = fileInput.files[0];
    var pdfPreview = fileInput.parentElement
        ? fileInput.parentElement.querySelector(".fileOutput")
        : null;
    if (pdfPreview) pdfPreview.innerHTML = "";

    if (file.type === "application/pdf") {
        if (errorEl) errorEl.style.display = "none";
        if (pdfPreview) {
            var fileURL = URL.createObjectURL(file);
            var iframe = document.createElement("iframe");
            iframe.src = fileURL;
            iframe.width = "100%";
            iframe.style.border = "1px solid #ccc";
            pdfPreview.appendChild(iframe);
        }
    } else {
        if (errorEl) {
            errorEl.style.display = "block";
            errorEl.textContent = "Only PDF files are allowed!";
        }
    }
}