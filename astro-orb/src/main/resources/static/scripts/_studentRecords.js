window.copyrights();

var v;                          // institution code
var studentRecordsData = [];    // holds the last fetched result set
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
        var option = $("<option>").val(d.id).text(d.name);
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

// ─── FETCH STUDENTS ───────────────────────────────────────────────────────────

document.getElementById("fetchStudentsBtn").addEventListener("click", async function () {
    showSplash();

    var studentClass = document.querySelector(".classSelect").value;
    var status = document.querySelector(".studentStatusSelect").value;
    var gender = document.querySelector(".genderSelect").value;

    // Build DynamicStringRequest — only include non-empty filters
    var keys = ["institutionCode"];
    var vals = [v];

    if (studentClass && !studentClass.toLowerCase().includes("select")) {
        keys.push("studentClass");
        vals.push(studentClass);
    }
    if (status && !status.toLowerCase().includes("select")) {
        keys.push("status");
        vals.push(status);
    }
    if (gender && !gender.toLowerCase().includes("select")) {
        keys.push("gender");
        vals.push(gender);
    }

    var jso = {key: keys, val: vals};

    try {
        const result = await fetchPost("getStudentsByDynamicData", jso);
        if (!result || result.length === 0) {
            swal({title: "No Results", text: "No students found for the selected parameters.", type: "info"});
            hideSplash();
            return;
        }
        studentRecordsData = result;
        destroyTable();
        $("#studentRecordsBody").empty();
        buildTable(result);
    } catch (error) {
        console.error("Error fetching students:", error);
        swal({title: "Error", text: "Failed to fetch student records.", type: "error"});
    }
    hideSplash();
});

// ─── BUILD TABLE ──────────────────────────────────────────────────────────────

function buildTable(data) {
    data.forEach(function (student) {
        var row = `
            <tr>
                <td>${student.studentId || ''}</td>
                <td>${student.lastName || ''}</td>
                <td>${student.firstName || ''}</td>
                <td>${student.otherName || ''}</td>
                <td>${student.gender || ''}</td>
                <td>${student.studentClass || ''}</td>
                <td>${student.dateOfBirth || ''}</td>
                <td>${student.dateOfAdmission || ''}</td>
                <td>${student.placeOfBirth || ''}</td>
                <td>${student.countryOfBirth || ''}</td>
                <td>${student.nationality || ''}</td>
                <td>${student.denomination || ''}</td>
                <td>
                    <span class="label ${statusLabel(student.status)}">${student.status || ''}</span>
                </td>
                <td>
                    <button class="btn btn-xs btn-warning view-student-btn"
                            data-student-id="${student.studentId}">
                        <i class="fa fa-pencil"></i> Edit
                    </button>
                </td>
            </tr>
        `;
        $("#studentRecordsBody").append(row);
    });

    initTable();
    bindViewButtons();
}

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

function initTable() {
    $("#studentRecordsTable").dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            {extend: "copy", className: "btn-sm"},
            {extend: "csv", title: "Student Records", className: "btn-sm"},
            {extend: "pdf", title: "Student Records", className: "btn-sm"},
            {extend: "print", className: "btn-sm"},
        ],
    });
    document.querySelector("#studentRecordsTable_wrapper")
        .setAttribute("style", "overflow: auto;");
}

function destroyTable() {
    if ($.fn.DataTable.isDataTable("#studentRecordsTable")) {
        $("#studentRecordsTable").DataTable().destroy();
    }
}

// ─── EXPORT BUTTON ────────────────────────────────────────────────────────────

document.getElementById("exportStudentsBtn").addEventListener("click", function () {
    if ($.fn.DataTable.isDataTable("#studentRecordsTable")) {
        $("#studentRecordsTable").DataTable().button(".buttons-csv").trigger();
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

function bindViewButtons() {
    $(".view-student-btn").off("click").on("click", function () {
        var studentId = $(this).data("student-id");
        openStudentModal(studentId);
    });
}

async function openStudentModal(studentId) {
    var student = studentRecordsData.find(function (s) {
        return s.studentId === studentId;
    });
    if (!student) return;

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
    $("#editNationality").val(student.nationality || '');
    $("#editDenomination").val(student.denomination || '');
    $("#editResidentialLocality").val(student.residentialLocality || '');
    $("#editStudentClass").val(student.studentClass || '');
    $("#editStatus").val(student.status || '');

    // Student picture — guard against null/undefined/plain filename
    var picOutput = document.querySelector(".studentPicOutput");
    // Server format: "profile.jpg_image/jpeg_<base64>" or "profile.jpg_image/png_<base64>"
    // Picture may arrive as:
    // 1. "profile.jpg_image/jpeg_<base64>"  — prefixed server format
    // 2. "/9j/4AAQ..."                       — raw JPEG Base64 (no prefix)
    // 3. "data:image/jpeg;base64,..."        — data URL
    // 4. null / short string / plain filename — no picture
    var hasPicture = student.picture &&
        typeof student.picture === 'string' &&
        student.picture.length > 100;   // anything real will be much longer than 100 chars

    if (hasPicture) {
        var mimeType, b64Data;

        if (student.picture.startsWith('data:image')) {
            // Already a data URL
            mimeType = student.picture.split(';')[0].split(':')[1];
            b64Data = student.picture.split('base64,').pop();
        } else if (student.picture.includes('_image/')) {
            // Server prefixed format: "filename_image/jpeg_<base64>"
            var mimeMatch = student.picture.match(/_(image\/[^_]+)_/);
            mimeType = mimeMatch ? mimeMatch[1] : 'image/jpeg';
            b64Data = extractBase64(student.picture);
        } else {
            // Raw Base64 — JPEG starts with /9j/, PNG starts with iVBOR
            mimeType = student.picture.trimStart().startsWith('iVBOR') ? 'image/png' : 'image/jpeg';
            b64Data = student.picture.trim();
        }

        picOutput.innerHTML = `<img src="data:${mimeType};base64,${b64Data}"
                                    style="width:180px;height:180px;border-radius:10px;object-fit:cover;"
                                    onerror="handleImgError(this)">
                               <div class="placeholder-avatar" style="display:none;height:180px;width:180px;
                                    background:#ccc;align-items:center;justify-content:center;border-radius:10px;">
                                    <i class="fa fa-user fa-4x" style="color:#fff;"></i>
                               </div>`;
    } else {
        picOutput.innerHTML = placeholderAvatar();
    }

    // Birth certificate PDF — guard against plain filename or null
    $(".birthCertOutput").empty();
    var hasBirthCert = student.birthCert &&
        typeof student.birthCert === 'string' &&
        student.birthCert.length > 50 &&
        (student.birthCert.includes('_application/pdf_') ||
            student.birthCert.startsWith('data:application/pdf'));

    if (hasBirthCert) {
        try {
            var pdfBase64 = student.birthCert.includes('_application/pdf_')
                ? student.birthCert.split('_application/pdf_')[1]
                : student.birthCert.split('base64,').pop();

            var pdfBlob = base64ToBlob(pdfBase64, 'application/pdf');
            var pdfURL = URL.createObjectURL(pdfBlob);
            var certOutput = document.querySelector(".birthCertOutput");
            certOutput.innerHTML = "";
            var iframe = document.createElement('iframe');
            iframe.src = pdfURL;
            iframe.width = '100%';
            iframe.height = '200px';
            iframe.style.border = '1px dashed #ccc';
            certOutput.appendChild(iframe);

            // Populate file input so Save picks it up unchanged
            var fileName = student.birthCert.split('_application/pdf_')[0] || 'birthCert.pdf';
            var file = new File([pdfBlob], fileName, {type: 'application/pdf'});
            var dt = new DataTransfer();
            dt.items.add(file);
            document.querySelector(".birthCertInput").files = dt.files;
        } catch (e) {
            console.warn("Could not load birth certificate:", e);
        }
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
        nationality: $("#editNationality").val(),
        denomination: $("#editDenomination").val(),
        residentialLocality: $("#editResidentialLocality").val(),
        studentClass: $("#editStudentClass").val(),
        status: $("#editStatus").val(),
        institutionCode: v,
        parentsRequests: uniqueParents,
    };

    // Pick up updated picture if changed
    var picInput = document.querySelector(".studentPicInput");
    if (picInput.files && picInput.files.length > 0) {
        var imgJson = await uploadIMGAsJSON(picInput, document.querySelector(".fileError"));
        if (imgJson) {
            updated.picture = imgJson.fileName + "_" + imgJson.fileType + "_" + imgJson.fileContent;
        }
    } else {
        updated.picture = original.picture || '';
    }

    // Pick up updated birth cert if changed.
    // Guard against the server returning just a plain filename (e.g. "26-00147-2.pdf")
    // instead of a full Base64 string — in that case treat it as no cert on file.
    var certInput = document.querySelector(".birthCertInput");
    var originalBirthCert = original.birthCert || '';
    var birthCertIsValidBase64 = originalBirthCert.includes('_application/pdf_') ||
        originalBirthCert.startsWith('data:application/pdf');

    if (certInput.files && certInput.files.length > 0) {
        var pdfJson = await uploadPDFAsJSON(certInput, document.querySelector(".fileError"));
        if (pdfJson) {
            updated.birthCert = pdfJson.fileName + "_" + pdfJson.fileType + "_" + pdfJson.fileContent;
        }
    } else if (birthCertIsValidBase64) {
        // Only pass back the original if it's a real Base64 string
        updated.birthCert = originalBirthCert;
    } else {
        // Plain filename or empty — send null so backend doesn't choke
        updated.birthCert = null;
    }

    try {
        await fetchPost("updateStudentRecord", updated);
        $("#studentModal").modal("hide");

        // Refresh the local data store with updated values
        var idx = studentRecordsData.findIndex(function (s) {
            return s.studentId === updated.studentId;
        });
        if (idx !== -1) studentRecordsData[idx] = Object.assign({}, original, updated);

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