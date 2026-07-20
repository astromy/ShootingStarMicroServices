// ==================== CONSTANTS AND INITIALIZATION ====================
var INSTITUTION_ID = $("meta[name='institutionId']")
    .attr("content")
    .split("/")[1]
    .split(",")[0]
    .replace(/[\[\]'\/]+/g, "");

// ==================== DATA STORES ====================
var staffData = {
    currentStaff: null,
    allStaff: [],
    dependants: [],
    academics: [],
    professionals: [],
    documents: [],
};

// ==================== DOM ELEMENTS ====================
var domElements = {
    staffTable: $("#staffTable_1"),
    staffDesignation: $("#staffDesignation"),
    nationalitySelect: $("#nationality"),
    modalBody: $(".modalbody"),
    modalTitle: $(".modal-title"),
    splashScreen: $(".splash"),
};

// ==================== INITIALIZATION ====================
(async function init() {
    try {
        await Promise.all([
            fetchStaffList(INSTITUTION_ID),
            fetchDepartment(INSTITUTION_ID),
        ]);
        renderStaffTable();
        window.copyrights();
    } catch (error) {
        console.error("Initialization error:", error);
    }
})();

// ==================== CORE FUNCTIONS ====================
async function fetchStaffList(institutionId) {
    try {
        const result = await fetchPost("get-staff-by-institution", {
            val: institutionId,
        });
        staffData.allStaff = result;
    } catch (error) {
        console.error("Failed to fetch staff list:", error);
        throw error;
    }
}

async function fetchDepartment(institutionId) {
    try {
        const result = await fetchPost("getInstitutionDepartment", {
            val: institutionId,
        });

        if (!result) return;

        // Clear and repopulate selects
        domElements.staffDesignation.find("option:gt(0)").remove();
        domElements.nationalitySelect.find("option:gt(0)").remove();

        // Optimized option population
        const optionsFragment = document.createDocumentFragment();
        result.forEach((dept) => {
            dept.designationList.forEach((designation) => {
                const option = new Option(designation.name, designation.code);
                optionsFragment.appendChild(option);
            });
        });
        domElements.staffDesignation.append(optionsFragment);
    } catch (error) {
        console.error("Failed to fetch departments:", error);
        throw error;
    }
}

function renderStaffTable() {
    if (!staffData.allStaff.length) return;

    // Clear and reinitialize table
    domElements.staffTable.DataTable()?.destroy();

    // Optimized table rendering
    const tableBody = document.getElementById("staffTableBody_1");
    const rowsFragment = document.createDocumentFragment();

    staffData.allStaff.forEach((staff) => {
        const row = document.createElement("tr");
        row.dataset.staffCode = staff.staffCode;
        row.innerHTML = `
      <td>${staff.staffCode}</td>
      <td>${staff.firstNames} ${staff.lastName}</td>
      <td>${staff.gender}</td>
      <td>${staff.contact1}</td>
      <td>${staff.staffEmail}</td>
      <td>${staff.dateOfEmployment}</td>
      <td>${staff.designation}</td>
      <td>${staff.nationality}</td>
      <td>${renderStaffSubjects(staff.staffSubjectsResponseList)}</td>
    `;

        // Event delegation would be better here
        row.addEventListener("click", () => handleStaffSelection(staff));
        rowsFragment.appendChild(row);
    });

    tableBody.innerHTML = "";
    tableBody.appendChild(rowsFragment);
    initializeDataTable("staffTable_1");
}

// ==================== HELPER FUNCTIONS ====================
function renderStaffSubjects(subjects) {
    if (!subjects || !Array.isArray(subjects)) return "No subjects assigned";

    const validSubjects = subjects.filter((s) => s?.subject && s?.subjectClass);
    if (!validSubjects.length) return "No subjects assigned";

    return `
    <ul class="subject-list">
      ${validSubjects
        .map((s) => `<li>${s.subjectClass} → ${s.subject}</li>`)
        .join("")}
    </ul>
  `;
}

async function handleStaffSelection(staff) {
    staffData.currentStaff = staff;

    switch (itra) {
        case 1:
            // Basic info handling
            break;
        case 2:
            await renderDependants(staff);
            break;
        case 3:
            await renderAcademicRecords(staff);
            break;
        case 4:
            await renderProfessionalRecords(staff);
            break;
        default:
            console.warn("Unknown itra value:", itra);
    }
}

async function renderDependants(staff) {
    domElements.modalTitle.text("Add Dependants");
    domElements.modalBody.attr("id", staff.staffCode).empty();

    if (!staff.dependants?.length) return;

    const fragment = document.createDocumentFragment();
    staff.dependants.forEach((dependant) => {
        const dependantElement = createDependantElement(dependant);
        fragment.appendChild(dependantElement);
    });

    domElements.modalBody.append(fragment);
}

// ==================== EVENT HANDLERS ====================
$("#submitRequest").click(async () => {
    try {
        domElements.splashScreen.show();
        await fetchPost("create-staff", staffData.allStaff);
        showSuccessAlert("Staff Saved Successfully");
    } catch (error) {
        console.error("Submission error:", error);
        showErrorAlert("Failed to save staff");
    } finally {
        domElements.splashScreen.hide();
    }
});

$(".saveStaffDetails").click(async () => {
    try {
        domElements.splashScreen.show();

        if (document.querySelector(".dependants")) {
            await processDependants();
        } else if (document.querySelector(".academic")) {
            await processAcademicRecords();
        } else if (document.querySelector(".professional")) {
            await processProfessionalRecords();
        }

        $(".dismissModal").click();
    } catch (error) {
        console.error("Save error:", error);
    } finally {
        domElements.splashScreen.hide();
    }
});

// ==================== UTILITY FUNCTIONS ====================
function initializeDataTable(tableId) {
    $(`#${tableId}`).DataTable({
        dom:
            "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>" +
            "<'row'<'col-sm-12'tr>>" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        lengthMenu: [
            [10, 25, 50, -1],
            [10, 25, 50, "All"],
        ],
        buttons: [
            {extend: "copy", className: "btn btn-sm btn-primary"},
            {
                extend: "csv",
                title: "Department List",
                className: "btn btn-sm btn-success",
            },
            {
                extend: "pdf",
                title: "Department List",
                className: "btn btn-sm btn-danger",
            },
            {extend: "print", className: "btn btn-sm btn-secondary"},
        ],
        responsive: true,
    });
}

function showSuccessAlert(message) {
    swal({
        title: "Thank you!",
        text: message,
        type: "success",
    });
}

function showErrorAlert(message) {
    swal({
        title: "Error",
        text: message,
        type: "error",
    });
}

// ==================== IMAGE HANDLING ====================
var imageHandler = {
    images: [],

    handleImageChange(event) {
        this.images = [event.target.files[0]];
        this.displayImages(event.target.closest(".image-container"));
    },

    displayImages(container) {
        if (!this.images.length) return;

        const imagesHTML = this.images
            .map(
                (image, index) => `
      <div class="crest">
        <img src="${URL.createObjectURL(
                    image
                )}" alt="Staff image" class="crest">
        <span onclick="imageHandler.deleteImage(${index})">&times;</span>
      </div>
    `
            )
            .join("");

        container.querySelector(".image-preview").innerHTML = imagesHTML;
    },

    deleteImage(index) {
        this.images.splice(index, 1);
        this.displayImages();
    },
};

// Note: The createDependantElement, processDependants, etc. functions would be
// implemented similarly to the existing ones but with optimized code