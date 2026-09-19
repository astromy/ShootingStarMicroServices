// This entire file is wrapped in an IIFE so none of its variables or
// functions leak onto `window`. This page swaps content into #wrapper and
// injects a fresh <script> tag without a real page reload, and the
// onboarding page (_hrOnloadingStaff.js) does the same - so both scripts
// can be alive in one browser session at once. Two collisions from that
// were found and fixed before this rewrite (itra, imageChange), but a
// third one slipped through: fetchStaffList and fetchDepartment were
// declared as plain top-level functions in BOTH files with incompatible
// bodies (onboarding's version writes to a global `exisitingstaff` and
// never touches staffData.allStaff). Whichever script's tag last defined
// the name on `window` won - so depending on load order, this page's own
// init could silently call ONBOARDING's fetchStaffList, staffData.allStaff
// would never get set, and the staff table would render empty or throw.
// Wrapping the whole file in a module scope closes off this entire class
// of bug at once, instead of continuing to rename one collision at a time
// as they're discovered.
(function () {
    "use strict";

    // ==================== CONSTANTS AND INITIALIZATION ====================
    var INSTITUTION_ID = $("meta[name='institutionId']")
        .attr("content")
        .split("/")[1]
        .split(",")[0]
        .replace(/[\[\]'\/]+/g, "");

    // Which step of the edit flow is showing - 1 = Basic Info, 2 = Dependants,
    // 3 = Academic Records, 4 = Professional Records. Now purely internal to
    // this module (see the IIFE comment above) so it can no longer be
    // clobbered by, or clobber, onboarding's own same-named global.
    var itra = 1;
    var LAST_STEP = 4;

    // Holds the staff record currently open in the modal, staged locally
    // across all four steps - mirrors onboarding's tempStaffHolder/staffList
    // pattern (nothing hits the network until submit), but scoped to the one
    // existing staff member being edited rather than a list of new hires,
    // since that's what this page actually does. Built once when a staff row
    // is clicked; every step reads from and writes back into this same
    // object, so moving Basic Info -> Dependants -> Academic -> Professional
    // -> Back doesn't lose whatever was already typed on an earlier step.
    var recordsDraft = null;

    var staffData = {
        currentStaff: null,
        allStaff: [],
    };

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

            domElements.staffDesignation.find("option:gt(0)").remove();
            domElements.nationalitySelect.find("option:gt(0)").remove();

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

        domElements.staffTable.DataTable()?.destroy();

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

    // ==================== IMAGE / FILE NORMALIZATION ====================
    // Stored picture/document content has shown up in at least two
    // incompatible shapes: raw base64 with no "data:" prefix (a browser
    // tries to GET that as a relative URL path, producing 400s), and
    // literal leftover local filesystem paths from before this data was
    // migrated to base64 storage at all (a browser refuses to load those
    // outright - "Not allowed to load local resource"). Every place that
    // renders a stored picture/document goes through these helpers instead
    // of interpolating the raw stored value directly, so both failure modes
    // degrade to a visible placeholder instead of a silent failed request.
    // A legacy filesystem path still needs a real backend migration
    // (re-upload, or a one-time script to re-save it as base64) - this only
    // stops the frontend from choking on it.
    function isLegacyFilesystemPath(value) {
        if (!value) return false;
        return (
            /^[a-zA-Z]:[\\/]/.test(value) ||
            value.startsWith("file://") ||
            value.includes("\\")
        );
    }

    function toImageSrc(content, fileType) {
        if (!content) return null;
        if (content.startsWith("data:")) return content;
        if (isLegacyFilesystemPath(content)) return null;

        var mime =
            fileType && fileType.includes("/")
                ? fileType
                : "image/" + (fileType || "png").replace(/^\./, "");
        return "data:" + mime + ";base64," + content;
    }

    function toDocumentHref(content, mimeType) {
        if (!content) return null;
        if (content.startsWith("data:")) return content;
        if (isLegacyFilesystemPath(content)) return null;
        return "data:" + (mimeType || "application/pdf") + ";base64," + content;
    }

    // ==================== DRAFT (STAGED EDIT) ====================
    // Builds the full staged shape for a staff member, up front, from
    // whatever the server currently has. From this point on, every step's
    // render function reads exclusively from this object - never from the
    // original `staff` argument again - so edits made on an earlier step
    // are still there if the person goes Back, or Next, or reopens a later
    // step, before anything is actually submitted.
    function buildDraftFromStaff(staff) {
        return {
            id: staff.id || "",
            staffCode: staff.staffCode,
            institutionCode: INSTITUTION_ID,
            firstNames: staff.firstNames || "",
            lastName: staff.lastName || "",
            dateOfBirth: staff.dateOfBirth || "",
            nationality: staff.nationality || "",
            homeTown: staff.homeTown || "",
            residentialTown: staff.residentialTown || "",
            contact1: staff.contact1 || "",
            backupContact: staff.backupContact || "",
            staffEmail: staff.staffEmail || "",
            nationalIDType: staff.nationalIDType || "",
            nationalID: staff.nationalID || "",
            snnitNumber: staff.snnitNumber || "",
            maritalStatus: staff.maritalStatus || "",
            nameOfSpouse: staff.nameOfSpouse || "",
            dateOfEmployment: staff.dateOfEmployment || "",
            gender: staff.gender || "",
            designation: staff.designation || "",
            level: staff.level || "",
            staffPicture: staff.staffPicture || "",
            nextOfKing: staff.nextOfKing || "",
            dependants: (staff.dependants || []).slice(),
            academicRecords: (staff.academicRecords || []).slice(),
            professionalRecords: (staff.professionalRecords || []).slice(),
        };
    }

    async function handleStaffSelection(staff) {
        var isNewSelection =
            !staffData.currentStaff || staffData.currentStaff.staffCode !== staff.staffCode;
        staffData.currentStaff = staff;

        // A fresh staff row click always starts a clean draft/step 1 - only
        // reusing an in-progress draft when re-clicking the SAME row (e.g.
        // after closing the modal mid-way through the steps without
        // submitting or explicitly starting over).
        if (isNewSelection || !recordsDraft) {
            itra = 1;
            recordsDraft = buildDraftFromStaff(staff);
        }

        await renderCurrentStep();
    }

    async function renderCurrentStep() {
        // Unconditionally clearing the modal body before every step render -
        // not just for Dependants/Academic/Professional, which already did
        // this - is what actually makes "start from scratch on a new record"
        // and "resume where you left off on the same record" both reliable.
        // Basic Info previously assumed its form markup was already sitting
        // in the DOM and never rebuilt it, so the first time any session
        // advanced past step 1 (which empties .modalbody to show dependant
        // rows instead), that markup was gone for good - later attempts to
        // show step 1 again, for this record or a different one, had
        // nothing to populate and nothing clearing whatever was left there
        // from before.
        domElements.modalBody.empty();
        switch (itra) {
            case 1:
                populateBasicInfoForm();
                break;
            case 2:
                await renderDependants();
                break;
            case 3:
                await renderAcademicRecords();
                break;
            case 4:
                await renderProfessionalRecords();
                break;
            default:
                console.warn("Unknown itra value:", itra);
        }
        ensureFooterControls();
    }

    // ==================== BASIC INFO STEP ====================
    // Previously this form only existed as static markup in the page's own
    // HTML, never as a template function like the other three steps - so it
    // could only ever be shown once, the very first time, before anything
    // else emptied .modalbody. Extracting it here means step 1 can now be
    // rebuilt from scratch exactly like Dependants/Academic/Professional
    // already were, which is what renderCurrentStep()'s blanket .empty()
    // now relies on.
    function createBasicInfoForm() {
        return `
            <form id="staffForm" role="form">
                <div class="row">
                    <div class="col-lg-3 text-center"></div>
                    <div class="col-lg-9">
                        <div class="row">
                            <div class="form-group col-lg-6">
                                <label for="code">Staff Code</label>
                                <input type="text" id="code" class="form-control"
                                       name="code" placeholder="Staff Code">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="staffFName">First Names</label>
                                <input type="text" required id="staffFName" class="form-control"
                                       name="staffFName" minlength="3" placeholder="First Names">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="staffLName">Last Name</label>
                                <input type="text" required id="staffLName" class="form-control"
                                       name="staffLName" minlength="3" placeholder="Last Name">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="dob">Date of Birth</label>
                                <input type="date" id="dob" class="form-control"
                                       name="dob" placeholder="Date of Birth" minlength="5">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="nationality">Nationality</label>
                                <select id="nationality" class="form-control"
                                       name="nationality" placeholder="Select Nationality">
                                       <option>Select Nationality</option>
                                       <option>Ghana</option>
                                       <option>Nigeria</option>
                                       <option>Cote DIvore'</option>
                                </select>
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="homeT">Home Town</label>
                                <input type="text" autocomplete="off" id="homeT"
                                       class="form-control" placeholder="Home Town"
                                       name="homeT" minlength="2">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="region">Residential Area</label>
                                <input type="text" autocomplete="off" required id="residence"
                                       class="form-control" placeholder="Residential Area" name="region"
                                       minlength="3">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="contact">Contact</label>
                                <input type="text" required id="contact" class="form-control"
                                       placeholder="Contact" name="contact" minlength="10">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="bContact">Backup Contact</label>
                                <input type="text" autocomplete="off" required id="bContact"
                                       class="form-control" name="bContact"
                                       placeholder="Backup Contact">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="staffEmail">Staff Email</label>
                                <input
                                  type="email"
                                  id="staffEmail"
                                  name="staffEmail"
                                  class="form-control"
                                  placeholder="Staff Email"
                                  pattern="[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+[.][a-zA-Z]{2,}"
                                  title="Please enter a valid company email address (e.g., name@company.com)"
                                  required
                                  autocomplete="email"
                                  inputmode="email"
                                  oninvalid="this.setCustomValidity('Please enter a valid company email address in the format: name@domain.com')"
                                  oninput="this.setCustomValidity('')"
                                >
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-3 text-center">
                        <div>
                            <label for="crest">Staff Picture</label>
                        </div>
                        <output class="imageOutput" name="crest" id="crest"
                                style="height: 220px; width:220px; border-radius: 10px;display: inline-block;"></output>
                        <input class="imageInput" type="file" onchange="staffRecordsImageChange(this)"
                               style="width: 200px;padding: 12px;display: inline;"
                               accept="image/jpeg, image/png, image/jpg">
                    </div>
                    <div class="col-lg-9">
                        <div class="row">
                            <div class="form-group col-lg-6">
                                <label for="idType">ID Type</label>
                                <select type="text" required id="idType" class="form-control"
                                       placeholder="Select ID Type" name="idType">
                                       <option>Select ID Type</option>
                                       <option>National ID Card</option>
                                       <option>Driver License</option>
                                       <option>Voter ID Card</option>
                                </select>
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="idNumber">National ID</label>
                                <input type="text" id="idNumber" class="form-control"
                                       placeholder="ID Number" name="idNumber">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="snnit">Snnit Number</label>
                                <input type="text" required id="snnit" class="form-control"
                                       placeholder="Snnit Number" name="snnit"
                                       minlength="5">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="maritalStatus">Marital Status</label>
                                <select type="text" required id="maritalStatus"
                                       class="form-control" name="maritalStatus"
                                       placeholder="Select Marital Status">
                                       <option>Select Marital Status</option>
                                       <option>Single</option>
                                       <option>Married</option>
                                       <option>Divorced</option>
                                </select>
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="nos">Name of Spouse</label>
                                <input type="text" id="nos" class="form-control"
                                       name="nos" placeholder="Name of Spouse">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="doe">Date of Employment</label>
                                <input type="date" required id="doe"
                                       class="form-control" name="doe"
                                       placeholder="Student Population">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="gender">Gender</label>
                                <select type="text" id="gender" class="form-control"
                                       name="gender" placeholder="Select Gender">
                                       <option>Select Gender</option>
                                       <option>Male</option>
                                       <option>Female</option>
                                </select>
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="staffDesignation">Staff Designation</label>
                                <select type="text" id="staffDesignation" class="form-control"
                                       name="staffDesignation" placeholder="Select Staff Designation">
                                       <option>Select Designation</option>
                                       <option>Teaching</option>
                                </select>
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="staffLevel">Staff Level</label>
                                <input type="text" id="level" class="form-control"
                                       name="nok" placeholder="Staff Level">
                            </div>
                            <div class="form-group col-lg-6">
                                <label for="nok">Next of King</label>
                                <input type="text" id="nok" class="form-control"
                                       name="nok" placeholder="Next of King">
                            </div>
                            <div>
                                <button id="instSubmit" class="btn btn-sm btn-primary m-t-n-xs"
                                        style="visibility: hidden;" type="submit">
                                    <strong>Submit</strong></button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <div class="text-right m-t-xs">
                <a class="btn btn-default next">Next</a>
            </div>
        `;
    }

    function populateBasicInfoForm() {
        domElements.modalBody.html(createBasicInfoForm()).attr("id", recordsDraft.staffCode);

        var d = recordsDraft;
        $("#code").val(d.staffCode);
        $("#staffFName").val(d.firstNames);
        $("#staffLName").val(d.lastName);
        $("#dob").val(d.dateOfBirth);
        $("#nationality").val(d.nationality);
        $("#homeT").val(d.homeTown);
        $("#residence").val(d.residentialTown);
        $("#contact").val(d.contact1);
        $("#bContact").val(d.backupContact);
        $("#staffEmail").val(d.staffEmail);
        $("#idType").val(d.nationalIDType);
        $("#idNumber").val(d.nationalID);
        $("#snnit").val(d.snnitNumber);
        $("#maritalStatus").val(d.maritalStatus);
        $("#nos").val(d.nameOfSpouse);
        $("#doe").val(d.dateOfEmployment);
        $("#gender").val(d.gender);
        $("#staffDesignation").val(d.designation);
        $("#level").val(d.level);
        $("#nok").val(d.nextOfKing);

        var crest = document.getElementById("crest");
        if (crest) {
            var pictureSrc = toImageSrc(d.staffPicture, null);
            if (pictureSrc) {
                crest.innerHTML = `<img src="${pictureSrc}" style="width:100%;height:100%;object-fit:cover;border-radius:10px;">`;
            } else if (d.staffPicture) {
                crest.innerHTML =
                    '<div style="font-size:12px;color:#999;padding:8px;text-align:center;">Legacy picture unavailable — please re-upload.</div>';
            } else {
                crest.innerHTML = "";
            }
        }

        domElements.modalTitle.text("Edit Staff Details");
        $("#staffModal").modal("show");
    }

    function stageBasicInfo() {
        if (!recordsDraft) return;
        recordsDraft.firstNames = $("#staffFName").val();
        recordsDraft.lastName = $("#staffLName").val();
        recordsDraft.dateOfBirth = $("#dob").val();
        recordsDraft.nationality = $("#nationality").val();
        recordsDraft.homeTown = $("#homeT").val();
        recordsDraft.residentialTown = $("#residence").val();
        recordsDraft.contact1 = $("#contact").val();
        recordsDraft.backupContact = $("#bContact").val();
        recordsDraft.staffEmail = $("#staffEmail").val();
        recordsDraft.nationalIDType = $("#idType").val();
        recordsDraft.nationalID = $("#idNumber").val();
        recordsDraft.snnitNumber = $("#snnit").val();
        recordsDraft.maritalStatus = $("#maritalStatus").val();
        recordsDraft.nameOfSpouse = $("#nos").val();
        recordsDraft.dateOfEmployment = $("#doe").val();
        recordsDraft.gender = $("#gender").val();
        recordsDraft.designation = $("#staffDesignation").val();
        recordsDraft.level = $("#level").val();
        recordsDraft.nextOfKing = $("#nok").val();
        // staffPicture is kept live on recordsDraft directly by
        // staffRecordsImageChange as soon as a new file is chosen, so
        // there's nothing to read back out of the DOM for it here.
    }

    /**
     * Previews a picture file and captures it as base64 - used for both the
     * staff's own picture input and every dependant row's picture input.
     *
     * Named staffRecordsImageChange rather than the generic imageChange
     * deliberately: _hrOnloadingStaff.js declares a global function
     * literally called imageChange with a different implementation, and
     * since both scripts can be loaded in the same session, a shared name
     * would have one silently overwrite the other. Now that this whole file
     * is IIFE-scoped that specific risk is closed regardless of the name
     * chosen, but the distinct name is kept for clarity and to match the
     * onchange="staffRecordsImageChange(this)" wired in the markup.
     */
    function staffRecordsImageChange(el) {
        var file = el.files && el.files[0];
        if (!file) {
            return;
        }
        var output = el.closest(".col-sm-6, .col-lg-3")?.querySelector(".imageOutput")
            || document.getElementById("crest");
        var reader = new FileReader();
        reader.onload = function (e) {
            if (output === document.getElementById("crest")) {
                if (recordsDraft) recordsDraft.staffPicture = e.target.result;
            } else if (output) {
                output.dataset.base64 = e.target.result;
            }
            if (output) {
                output.innerHTML = `<img src="${e.target.result}" style="width:100%;height:100%;object-fit:cover;border-radius:10px;">`;
            }
        };
        reader.readAsDataURL(file);
    }

    // Exposed on window because the markup calls this via an inline
    // onchange="" attribute, which can only resolve a global identifier.
    window.staffRecordsImageChange = staffRecordsImageChange;

    // The dependant/academic/professional templates wire their PDF inputs
    // to onchange="uploadPDF(this)" - a function that isn't defined in this
    // file or in _hrOnloadingStaff.js. Whatever previews a freshly-chosen
    // PDF depends on wherever that global actually lives, which this page
    // has no visibility into or control over. Rather than rely on it (and
    // risk the exact same kind of cross-page collision imageChange had),
    // this binds a records-owned preview via delegation instead, so the
    // preview works regardless of whether uploadPDF exists, is broken, or
    // gets overwritten by another page's script later in the session.
    //
    // In each template, .fileOutput / .pdfInput / .fileError are direct
    // siblings under the same .col-md-12, so el.parentElement reaches all
    // three without needing a wider .closest() search.
    function stagePdfPreview(el) {
        var container = el.parentElement;
        var fileOutput = container?.querySelector(".fileOutput");
        var errorEl = container?.querySelector(".fileError");
        var file = el.files && el.files[0];

        if (!file) {
            if (fileOutput) fileOutput.innerHTML = "";
            return;
        }

        var looksLikePdf = file.type === "application/pdf" || /\.pdf$/i.test(file.name);
        if (!looksLikePdf) {
            if (errorEl) errorEl.style.display = "block";
            el.value = "";
            if (fileOutput) fileOutput.innerHTML = "";
            return;
        }
        if (errorEl) errorEl.style.display = "none";

        var reader = new FileReader();
        reader.onload = function (e) {
            if (fileOutput) {
                fileOutput.dataset.base64 = e.target.result;
                fileOutput.innerHTML = `<a href="${e.target.result}" target="_blank" rel="noopener">${file.name}</a>`;
            }
        };
        reader.readAsDataURL(file);
    }

    // ==================== DEPENDANTS STEP ====================
    // dependantPicture/birthCertificate are stored as "fileName_fileType_base64"
    // (matching the format _hrOnloadingStaff.js writes) - splitting on the
    // first two underscores rather than a plain split("_") since standard
    // base64 content itself never contains "_", but a naive split could
    // still misbehave if a filename ever did.
    function parseFileTriplet(value) {
        if (!value) {
            return null;
        }
        var match = /^([^_]*)_([^_]*)_(.*)$/.exec(value);
        if (!match) {
            return null;
        }
        return {fileName: match[1], fileType: match[2], content: match[3]};
    }

    function createDependantElement(dependant) {
        var wrapper = document.createElement("div");
        wrapper.innerHTML = createDependant();
        var row = wrapper.firstElementChild;

        var removeBtn = document.createElement("span");
        removeBtn.innerHTML = "&times;";
        removeBtn.title = "Remove this dependant";
        removeBtn.style.cssText = "cursor:pointer;float:right;font-size:20px;color:#c0392b;";
        removeBtn.addEventListener("click", () => row.remove());
        row.insertBefore(removeBtn, row.firstChild);

        if (dependant) {
            row.dataset.dependantId = dependant.id || "";
            row.querySelector(".newDependantName").value = dependant.name || "";
            row.querySelector(".newDependantDOB").value = dependant.dateOfBirth || "";

            var genderSelect = row.querySelector(".newGenderSelect");
            Array.from(genderSelect.options).forEach((opt) => {
                if (opt.text === dependant.gender) genderSelect.value = opt.value;
            });

            var relSelect = row.querySelector(".newRelationshipType");
            Array.from(relSelect.options).forEach((opt) => {
                if (opt.text === dependant.relationType) relSelect.value = opt.value;
            });

            var picInfo = parseFileTriplet(dependant.dependantPicture);
            if (picInfo) {
                var picOutput = row.querySelector(".dependantPic");
                var picSrc = toImageSrc(picInfo.content, picInfo.fileType);
                if (picSrc) {
                    picOutput.dataset.base64 = picSrc;
                    picOutput.innerHTML = `<img src="${picSrc}" style="width:100%;height:100%;object-fit:cover;border-radius:10px;">`;
                } else {
                    picOutput.innerHTML =
                        '<div style="font-size:11px;color:#999;padding:6px;text-align:center;">Legacy picture unavailable — please re-upload.</div>';
                }
            }

            var certInfo = parseFileTriplet(dependant.birthCertificate);
            if (certInfo) {
                var fileOutput = row.querySelector(".fileOutput");
                var certHref = toDocumentHref(certInfo.content);
                if (certHref) {
                    fileOutput.innerHTML = `<a href="${certHref}" target="_blank" rel="noopener">${certInfo.fileName || "View certificate"}</a>`;
                } else {
                    fileOutput.innerHTML =
                        '<span style="font-size:11px;color:#999;">Legacy file unavailable — please re-upload.</span>';
                }
            }
        }

        return row;
    }

    async function renderDependants() {
        domElements.modalTitle.text("Add Dependants");
        domElements.modalBody.attr("id", recordsDraft.staffCode).empty();

        if (!recordsDraft.dependants.length) {
            domElements.modalBody.html('<p class="text-muted text-center m-t-md">No dependants recorded yet. Use "Add More Dependants" below to add one.</p>');
            return;
        }

        const fragment = document.createDocumentFragment();
        recordsDraft.dependants.forEach((dependant) => {
            fragment.appendChild(createDependantElement(dependant));
        });

        domElements.modalBody.append(fragment);
    }

    // Reads every ".dependants" row currently in the modal (DOM is the
    // source of truth for "how many" and "what values") and writes the
    // result onto recordsDraft.dependants - no network call here, that only
    // happens once, at the end, from submitDraft().
    async function stageDependants() {
        var rows = document.querySelectorAll(".dependants");
        var dependantDetails = [];

        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var picOutput = row.querySelector(".dependantPic");
            var picFile = row.querySelector(".dependantPicInput");
            var certFile = row.querySelector(".pdfInput");

            var picture = "";
            if (picFile && picFile.files[0]) {
                var imgs = await uploadIMGAsJSON(picFile, row.querySelector(".fileError"));
                picture = imgs ? imgs.fileName + "_" + imgs.fileType + "_" + imgs.fileContent : "";
            } else if (picOutput && picOutput.dataset.base64) {
                var existingMatch = /^data:([^;]+);base64,/.exec(picOutput.dataset.base64);
                var existingType = existingMatch ? existingMatch[1] : "image/png";
                picture = "existing_" + existingType + "_" + picOutput.dataset.base64;
            }

            var certificate = "";
            if (certFile && certFile.files[0]) {
                var doc = await uploadPDFAsJSON(certFile, row.querySelector(".fileError"));
                certificate = doc ? doc.fileName + "_" + doc.fileType + "_" + doc.fileContent : "";
            } else {
                var certLink = row.querySelector(".fileOutput a");
                if (certLink) certificate = "existing_application/pdf_" + certLink.getAttribute("href");
            }

            dependantDetails.push({
                id: row.dataset.dependantId || "",
                name: row.querySelector(".newDependantName").value,
                dateOfBirth: row.querySelector(".newDependantDOB").value,
                relationType: row.querySelector(".newRelationshipType").selectedOptions[0].text,
                gender: row.querySelector(".newGenderSelect").selectedOptions[0].text,
                birthCertificate: certificate,
                dependantPicture: picture,
                institutionCode: INSTITUTION_ID,
            });
        }

        recordsDraft.dependants = dependantDetails;
    }

    // ==================== ACADEMIC RECORDS STEP ====================
    function createAcademicElement(record) {
        var wrapper = document.createElement("div");
        wrapper.innerHTML = createAcademicData();
        var row = wrapper.firstElementChild;

        var removeBtn = document.createElement("span");
        removeBtn.innerHTML = "&times;";
        removeBtn.title = "Remove this record";
        removeBtn.style.cssText = "cursor:pointer;float:right;font-size:20px;color:#c0392b;";
        removeBtn.addEventListener("click", () => row.remove());
        row.insertBefore(removeBtn, row.firstChild);

        if (record) {
            row.dataset.academicId = record.id || "";
            row.querySelector(".nameOfInstitution").value = record.nameOfInstitution || "";
            row.querySelector(".dateOfAdmission").value = record.dateOfAdmission || "";
            row.querySelector(".dateOfGraduation").value = record.dateOfGraduation || "";
            row.querySelector(".programOffered").value = record.programOffered || "";

            var certSelect = row.querySelector(".certificateType");
            Array.from(certSelect.options).forEach((opt) => {
                if (opt.text === record.certificateType) certSelect.value = opt.value;
            });

            var docInfo = parseFileTriplet(record.supportingDocs);
            if (docInfo) {
                var fileOutput = row.querySelector(".fileOutput");
                var href = toDocumentHref(docInfo.content);
                if (href) {
                    fileOutput.innerHTML = `<a href="${href}" target="_blank" rel="noopener">${docInfo.fileName || "View document"}</a>`;
                } else {
                    fileOutput.innerHTML =
                        '<span style="font-size:11px;color:#999;">Legacy file unavailable — please re-upload.</span>';
                }
            }
        }

        return row;
    }

    async function renderAcademicRecords() {
        domElements.modalTitle.text("Add Academic Documents");
        domElements.modalBody.attr("id", recordsDraft.staffCode).empty();

        if (!recordsDraft.academicRecords.length) {
            domElements.modalBody.html('<p class="text-muted text-center m-t-md">No academic records recorded yet. Use "Add More Academic Records" below to add one.</p>');
            return;
        }

        const fragment = document.createDocumentFragment();
        recordsDraft.academicRecords.forEach((record) => {
            fragment.appendChild(createAcademicElement(record));
        });

        domElements.modalBody.append(fragment);
    }

    async function stageAcademicRecords() {
        var rows = document.querySelectorAll(".academic");
        var academicDetails = [];

        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var docFile = row.querySelector(".pdfInput");

            var supportingDocs = "";
            if (docFile && docFile.files[0]) {
                var doc = await uploadPDFAsJSON(docFile, row.querySelector(".fileError"));
                supportingDocs = doc ? doc.fileName + "_" + doc.fileType + "_" + doc.fileContent : "";
            } else {
                var docLink = row.querySelector(".fileOutput a");
                if (docLink) supportingDocs = "existing_application/pdf_" + docLink.getAttribute("href");
            }

            academicDetails.push({
                id: row.dataset.academicId || "",
                nameOfInstitution: row.querySelector(".nameOfInstitution").value,
                dateOfAdmission: row.querySelector(".dateOfAdmission").value,
                dateOfGraduation: row.querySelector(".dateOfGraduation").value,
                programOffered: row.querySelector(".programOffered").value,
                certificateType: row.querySelector(".certificateType").selectedOptions[0].text,
                supportingDocs: supportingDocs,
                institutionCode: INSTITUTION_ID,
            });
        }

        recordsDraft.academicRecords = academicDetails;
    }

    // ==================== PROFESSIONAL RECORDS STEP ====================
    function createProfessionalElement(record) {
        var wrapper = document.createElement("div");
        wrapper.innerHTML = createProfessionalData();
        var row = wrapper.firstElementChild;

        var removeBtn = document.createElement("span");
        removeBtn.innerHTML = "&times;";
        removeBtn.title = "Remove this record";
        removeBtn.style.cssText = "cursor:pointer;float:right;font-size:20px;color:#c0392b;";
        removeBtn.addEventListener("click", () => row.remove());
        row.insertBefore(removeBtn, row.firstChild);

        if (record) {
            row.dataset.professionalId = record.id || "";
            // .nameOfInstitution is the same class name createAcademicData()
            // uses - safe here since the lookup is scoped to this row only.
            row.querySelector(".nameOfInstitution").value = record.nameOfInstitution || "";
            row.querySelector(".dateOfEmployment").value = record.dateOfEmployment || "";
            row.querySelector(".dateOfDeparture").value = record.dateOfDeparture || "";
            row.querySelector(".designationAtInstitution").value = record.designationAtInstitution || "";
            row.querySelector(".employmentTypeAtInstitution").value = record.employmentTypeAtInstitution || "";

            var docInfo = parseFileTriplet(record.supportingDocs);
            if (docInfo) {
                var fileOutput = row.querySelector(".fileOutput");
                var href = toDocumentHref(docInfo.content);
                if (href) {
                    fileOutput.innerHTML = `<a href="${href}" target="_blank" rel="noopener">${docInfo.fileName || "View document"}</a>`;
                } else {
                    fileOutput.innerHTML =
                        '<span style="font-size:11px;color:#999;">Legacy file unavailable — please re-upload.</span>';
                }
            }
        }

        return row;
    }

    async function renderProfessionalRecords() {
        domElements.modalTitle.text("Add Professional Documents");
        domElements.modalBody.attr("id", recordsDraft.staffCode).empty();

        if (!recordsDraft.professionalRecords.length) {
            domElements.modalBody.html('<p class="text-muted text-center m-t-md">No professional records recorded yet. Use "Add More Professional Records" below to add one.</p>');
            return;
        }

        const fragment = document.createDocumentFragment();
        recordsDraft.professionalRecords.forEach((record) => {
            fragment.appendChild(createProfessionalElement(record));
        });

        domElements.modalBody.append(fragment);
    }

    async function stageProfessionalRecords() {
        var rows = document.querySelectorAll(".professional");
        var professionalDetails = [];

        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var docFile = row.querySelector(".pdfInput");

            var supportingDocs = "";
            if (docFile && docFile.files[0]) {
                var doc = await uploadPDFAsJSON(docFile, row.querySelector(".fileError"));
                supportingDocs = doc ? doc.fileName + "_" + doc.fileType + "_" + doc.fileContent : "";
            } else {
                var docLink = row.querySelector(".fileOutput a");
                if (docLink) supportingDocs = "existing_application/pdf_" + docLink.getAttribute("href");
            }

            professionalDetails.push({
                id: row.dataset.professionalId || "",
                nameOfInstitution: row.querySelector(".nameOfInstitution").value,
                dateOfEmployment: row.querySelector(".dateOfEmployment").value,
                dateOfDeparture: row.querySelector(".dateOfDeparture").value,
                designationAtInstitution: row.querySelector(".designationAtInstitution").value,
                employmentTypeAtInstitution: row.querySelector(".employmentTypeAtInstitution").value,
                supportingDocs: supportingDocs,
                institutionCode: INSTITUTION_ID,
            });
        }

        recordsDraft.professionalRecords = professionalDetails;
    }

    // ==================== STAGE / ADVANCE / SUBMIT ====================
    async function stageCurrentStep() {
        if (itra === 1) stageBasicInfo();
        else if (itra === 2) await stageDependants();
        else if (itra === 3) await stageAcademicRecords();
        else if (itra === 4) await stageProfessionalRecords();
    }

    // The one and only network call in this whole flow - mirrors onboarding
    // having exactly one #submitRequest that posts everything staged so far.
    async function submitDraft() {
        await fetchPost("create-staff", [recordsDraft]);
        await fetchStaffList(INSTITUTION_ID);
        renderStaffTable();
        showSuccessAlert("Staff record saved successfully");
        recordsDraft = null;
        staffData.currentStaff = null;
        itra = 1;
    }

    // "Save & Continue" on steps 1-3, "Submit" on step 4 - one button, whose
    // meaning changes with the step, same as onboarding's .saveStaffDetails
    // only ever staging while a separate #submitRequest does the real save;
    // here the two are merged into one control since a single-staff edit
    // flow naturally ends after Professional rather than looping back to a
    // staff table for more people.
    async function advanceOrSubmit() {
        try {
            domElements.splashScreen.show();
            await stageCurrentStep();

            if (itra < LAST_STEP) {
                itra += 1;
                await renderCurrentStep();
            } else {
                await submitDraft();
                $(".dismissModal").click();
            }
        } catch (error) {
            console.error("Save error:", error);
            showErrorAlert("Could not save this section. Please check the details and try again.");
        } finally {
            domElements.splashScreen.hide();
        }
    }

    // Lets someone submit from any step, not just after reaching
    // Professional - mirrors onboarding's #submitRequest being independently
    // clickable at any point, not gated behind having visited every tab.
    async function submitNow() {
        if (!recordsDraft) return;
        try {
            domElements.splashScreen.show();
            await stageCurrentStep();
            await submitDraft();
            $(".dismissModal").click();
        } catch (error) {
            console.error("Submit error:", error);
            showErrorAlert("Could not submit. Please check the details and try again.");
        } finally {
            domElements.splashScreen.hide();
        }
    }

    async function goBack() {
        if (itra <= 1) return;
        try {
            domElements.splashScreen.show();
            // Stage whatever's on the current step before leaving it, so
            // going Back and then Next again doesn't discard it.
            await stageCurrentStep();
            itra -= 1;
            await renderCurrentStep();
        } catch (error) {
            console.error("Back error:", error);
        } finally {
            domElements.splashScreen.hide();
        }
    }

    // Creates (once) and updates the Back / Submit Now / Save & Continue /
    // Add More controls in the modal footer to match the current step. Back
    // and Submit Now don't exist in the static markup, so they're injected
    // here rather than requiring an HTML change - the same approach already
    // used for each dependant/record row's remove ("×") button.
    function ensureFooterControls() {
        var footer = document.querySelector("#staffModal .modal-footer");
        if (!footer) return;

        var testBtn = footer.querySelector(".test");
        if (testBtn) {
            var addMoreLabels = {
                2: "Add More Dependants",
                3: "Add More Academic Records",
                4: "Add More Professional Records",
            };
            if (addMoreLabels[itra]) {
                testBtn.style.display = "";
                testBtn.innerHTML = '<i class="fa fa-plus-square"></i> ' + addMoreLabels[itra];
            } else {
                testBtn.style.display = "none";
            }
        }

        var backBtn = footer.querySelector(".recordsBack");
        if (!backBtn) {
            backBtn = document.createElement("button");
            backBtn.type = "button";
            backBtn.className = "btn btn-default recordsBack";
            backBtn.textContent = "Back";
            footer.insertBefore(backBtn, footer.firstChild);
        }
        backBtn.style.display = itra > 1 ? "" : "none";

        var saveBtn = footer.querySelector(".saveStaffDetails");
        var submitNowBtn = footer.querySelector(".recordsSubmitNow");
        if (!submitNowBtn && saveBtn) {
            submitNowBtn = document.createElement("button");
            submitNowBtn.type = "button";
            submitNowBtn.className = "btn btn-success recordsSubmitNow";
            submitNowBtn.textContent = "Submit Now";
            footer.insertBefore(submitNowBtn, saveBtn);
        }

        if (saveBtn) {
            saveBtn.textContent = itra < LAST_STEP ? "Save & Continue" : "Submit";
        }
    }

    // ==================== EVENT HANDLERS ====================
    // Namespaced ("click.records") and cleared with .off() before .on() so
    // that repeatedly navigating to this page - which re-injects and
    // re-executes this whole script each time, without ever removing the
    // previous copy's bindings - doesn't stack a second, third, Nth copy of
    // the same handler and fire an action multiple times per click.
    $(document).off("click.records", ".test").on("click.records", ".test", () => {
        domElements.modalBody.find("p.text-muted").remove();
        if (itra === 2) domElements.modalBody.append(createDependantElement(null));
        else if (itra === 3) domElements.modalBody.append(createAcademicElement(null));
        else if (itra === 4) domElements.modalBody.append(createProfessionalElement(null));
    });

    $(document).off("click.records", ".saveStaffDetails").on("click.records", ".saveStaffDetails", advanceOrSubmit);
    $(document).off("click.records", ".staffPane .next").on("click.records", ".staffPane .next", advanceOrSubmit);
    $(document).off("click.records", ".recordsBack").on("click.records", ".recordsBack", goBack);
    $(document).off("click.records", ".recordsSubmitNow").on("click.records", ".recordsSubmitNow", submitNow);
    $(document).off("change.records", ".pdfInput").on("change.records", ".pdfInput", function () {
        stagePdfPreview(this);
    });

    $(".wizardTabs").off("click.records").eq(0).on("click.records", () => {
        itra = 1;
        if (recordsDraft) renderCurrentStep();
    });

    // Bulk-save of the whole staff table in one go, independent of the
    // staged single-record flow above. Left as-is from before this rewrite;
    // #submitRequest doesn't appear in this page's own markup, so this is
    // most likely inert here rather than something actively relied on -
    // kept rather than removed since that wasn't asked for and it's
    // harmless if the element simply doesn't exist.
    $("#submitRequest").off("click.records").on("click.records", async () => {
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
})();