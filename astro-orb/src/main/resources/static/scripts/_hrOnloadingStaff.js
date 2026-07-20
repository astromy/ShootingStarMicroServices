var staffFname,
    staffLName,
    staffDoB,
    nationality,
    homeTown,
    city,
    contact1,
    bContact,
    IDType,
    iDNumber,
    snnitNum,
    maritalStatus,
    nameOfSpouse,
    DoE,
    gender,
    nextOfKing;
var dependantDetails = [];
var staffAcademicDetails = [];
var staffProfessionalDetails = [];
var staffDocumentDetails = [];
var exisitingstaff = [];
var staffList = [];
// Tracks the in-progress new staff record across the wizard's tabs (Dependants/Academic/
// Professional steps each render their own table row, but they all describe the SAME staff
// member). Used so we push exactly one entry per staff into staffList instead of one per tab.
var currentStaffDraftId = null;
var firstName,
    lastName,
    fullName,
    gender,
    dob,
    contact,
    bContact,
    staffEmail,
    doe,
    nationality,
    snnit,
    ht,
    residence,
    idType,
    idNumber,
    maritalStatus,
    nos,
    nok,
    code,
    designation,
    level;
var tempStaffHolder;
var countryList = [
    "Afghanistan",
    "Albania",
    "Algeria",
    "Andorra",
    "Angola",
    "Antigua and Barbuda",
    "Argentina",
    "Armenia",
    "Australia",
    "Austria",
    "Azerbaijan",
    "The Bahamas",
    "Bahrain",
    "Bangladesh",
    "Barbados",
    "Belarus",
    "Belgium",
    "Belize",
    "Benin",
    "Bhutan",
    "Bolivia",
    "Bosnia and Herzegovina",
    "Botswana",
    "Brazil",
    "Brunei ",
    "Bulgaria",
    "Burkina Faso",
    "Burma",
    "Burundi",
    "Cambodia",
    "Cameroon",
    "Canada",
    "Cape Verde",
    "Central African Republic",
    "Chad",
    "Chile",
    "China",
    "Colombia",
    "Comoros ",
    "Democratic Republic of Congo",
    "Congo-Brazzaville ",
    "Costa Rica",
    "Cote dIvoire",
    "Croatia",
    "Cuba",
    "Cyprus",
    "Czech Republic",
    "Denmark",
    "Djibouti",
    "Dominica",
    "Dominican Republic",
    "East Timor (see Timor-Leste)",
    "Ecuador",
    "Egypt",
    "El Salvador",
    "Equatorial Guinea",
    "Eritrea",
    "Estonia",
    "Ethiopia",
    "Fiji",
    "Finland",
    "France",
    "Gabon",
    "The Gambia",
    "Georgia",
    "Germany",
    "Ghana",
    "Greece",
    "Grenada",
    "Guatemala",
    "Guinea",
    "Guinea-Bissau",
    "Guyana",
    "Haiti",
    "Holy See",
    "Honduras",
    "Hong Kong",
    "Hungary",
    "Iceland",
    "India",
    "Indonesia",
    "Iran",
    "Iraq",
    "Ireland",
    "Israel",
    "Italy",
    "Jamaica",
    "Japan",
    "Jordan",
    "Kazakhstan",
    "Kenya",
    "Kiribati",
    "Kosovo",
    "Kuwait",
    "Kyrgyzstan",
    "Laos",
    "Latvia",
    "Lebanon",
    "Lesotho",
    "Liberia",
    "Libya",
    "Liechtenstein",
    "Lithuania",
    "Luxembourg",
    "Macau",
    "Macedonia",
    "Madagascar",
    "Malawi",
    "Malaysia",
    "Maldives",
    "Mali",
    "Malta",
    "Marshall Islands",
    "Mauritania",
    "Mauritius",
    "Mexico",
    "Micronesia",
    "Moldova",
    "Monaco",
    "Mongolia",
    "Montenegro",
    "Morocco",
    "Mozambique",
    "Namibia",
    "Nauru",
    "Nepal",
    "Netherlands",
    "Netherlands Antilles",
    "New Zealand",
    "Nicaragua",
    "Niger",
    "Nigeria",
    "North Korea",
    "Norway",
    "Oman",
    "Pakistan",
    "Palau",
    "Palestinian Territories",
    "Panama",
    "Papua New Guinea",
    "Paraguay",
    "Peru",
    "Philippines",
    "Poland",
    "Portugal ",
    "Qatar",
    "Romania",
    "Russia",
    "Rwanda",
    "Saint Kitts and Nevis",
    "Saint Lucia",
    "Saint Vincent and the Grenadines",
    "Samoa ",
    "San Marino",
    "Sao Tome and Principe",
    "Saudi Arabia",
    "Senegal",
    "Serbia",
    "Seychelles",
    "Sierra Leone",
    "Singapore",
    "Slovakia",
    "Slovenia",
    "Solomon Islands",
    "Somalia",
    "South Africa",
    "South Korea",
    "South Sudan",
    "Spain ",
    "Sri Lanka",
    "Sudan",
    "Suriname",
    "Swaziland ",
    "Sweden",
    "Switzerland",
    "Syria",
    "Taiwan",
    "Tajikistan",
    "Tanzania",
    "Thailand ",
    "Timor-Leste",
    "Togo",
    "Tonga",
    "Trinidad and Tobago",
    "Tunisia",
    "Turkey",
    "Turkmenistan",
    "Tuvalu",
    "Uganda",
    "Ukraine",
    "United Arab Emirates",
    "United Kingdom",
    "Uruguay",
    "Uzbekistan",
    "Vanuatu",
    "Venezuela",
    "Vietnam",
    "Yemen",
    "Zambia",
    "Zambia",
    "Zimbabwe",
];

var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");
fetchStaffList(instId);
fetchDepartment(instId);

window.copyrights();

$(".prev").click(function () {
    var tabs = $(".tab-pane");
    var tbs = $(".tab-pane.active");
    tabs.removeClass("active");
    var prevLi = tbs.prev().addClass("active");

    var header = $(".wizardTabs");
    var header1 = $(".wizardTabs.btn-primary");
    header.removeClass("btn-primary");
    header.addClass("btn-default");
    header1.prev().removeClass("btn-default").addClass("btn-primary");
    tempStaffHolder = undefined;
    itra -= 1;
    addTableRow("staffTableBody_" + itra);
    $("#staffTable_" + itra)
        .DataTable()
        .destroy();
    dataTableInit("staffTable_" + itra);
});

var itra = 1;
$(".next").click(function () {
    if ($(".staffPane").hasClass("active")) {
        readNewStaffData();
    }

    var tabs = $(".tab-pane");
    var tbs = $(".tab-pane.active");
    tabs.removeClass("active");
    var nextLi = tbs.next().addClass("active");

    var header = $(".wizardTabs");
    var header1 = $(".wizardTabs.btn-primary");
    header.removeClass("btn-primary");
    header.addClass("btn-default");
    header1.next().removeClass("btn-default").addClass("btn-primary");
    tempStaffHolder = undefined;
    addTableRow("staffTableBody_" + itra);
    $("#staffTable_" + itra)
        .DataTable()
        .destroy();
    dataTableInit("staffTable_" + itra);
    itra += 1;
});

async function fetchStaffList(instId) {
    var instRequest = {val: instId};
    return fetchPost("get-staff-by-institution", instRequest).then(function (
        result
    ) {
        exisitingstaff = result;
    });
}

//Populates the Designation Select Field
async function fetchDepartment(instId) {
    var instRequest = {val: instId};
    return fetchPost("getInstitutionDepartment", instRequest).then(function (
        result
    ) {
        var el = $("#staffDesignation");
        el.find("option:gt(0)").remove();

        var el1 = $("#nationality");
        el1.find("option:gt(0)").remove();

        countryList.forEach((cl, index1, array1) => {
            el1.append(
                $("<option>", {
                    value: cl,
                    text: cl,
                })
            );
        });

        if (result != null) {
            var bar = new Promise((resolve, reject) => {
                result.forEach((d, index, array) => {
                    d.designationList.forEach((dl, index1, array1) => {
                        el.append(
                            $("<option>", {
                                value: dl.code,
                                text: dl.name,
                            })
                        );
                        if (index1 === array1.length - 1) resolve();
                    });
                });
            });
            bar.then(() => {
            });
        }
    });
}

// Submits ALL staged staff (and their dependants/academic/professional records) to the
// HR microservice in a single POST. This is the only place in the onboarding flow that
// talks to the network - saveStaffDetails (below) only prepares/stages data locally.
$("#submitRequest").click(async function () {
    if (!staffList.length) {
        swal({
            title: "Nothing to submit",
            text: "Add at least one staff member before submitting.",
            type: "warning",
        });
        return;
    }

    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    // tempId is purely a client-side bookkeeping field (see currentStaffDraftId) used to
    // keep one staffList entry per staff across wizard tabs - it has no matching property
    // on the backend's StaffRequest DTO, so it must be stripped before sending.
    var payload = staffList.map(function (staff) {
        var clean = Object.assign({}, staff);
        delete clean.tempId;
        return clean;
    });
    return fetchPost("create-staff", payload)
        .then(function (result) {
            $('.splash').css('display', 'none');
            staffList = [];
            swal({
                title: "Thank you!",
                text: "Staff Saved Successfully",
                type: "success",
            });
        })
        .catch(function (error) {
            $('.splash').css('display', 'none');
            swal({
                title: "Submission failed",
                text: "Staff could not be saved. Please check the details and try again.",
                type: "error",
            });
        });
});

// Stages the current modal's data (dependant/academic/professional record) onto the
// in-progress staff record in staffList. Does NOT call the HR microservice - that only
// happens once, in bulk, when #submitRequest is clicked.
$(".saveStaffDetails").click(async function () {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    if (document.querySelector(".dependants")) {
        const elm = document.querySelectorAll(".dependants");
        var cnt = elm.length;

        // FIX 2: Reset array before repopulating to prevent cross-staff data bleed
        dependantDetails = [];

        for (var i = 0; i < cnt; i++) {
            // FIX 3: Guard against null doc/imgs when no file is selected
            var doc = await uploadPDFAsJSON(
                document.querySelectorAll(".pdfInput")[i],
                document.querySelectorAll(".fileError")[i]
            );

            var imgs = await uploadIMGAsJSON(
                document.querySelectorAll(".dependantPicInput")[i],
                document.querySelectorAll(".fileError")[i]
            );

            var depJson = {
                id: "",
                name: document.querySelectorAll(".newDependantName")[i].value,
                dateOfBirth: document.querySelectorAll(".newDependantDOB")[i].value,
                relationType: document.querySelectorAll(".newRelationshipType")[i]
                    .options[
                    document.querySelectorAll(".newRelationshipType")[i].selectedIndex
                    ].text,
                gender:
                document.querySelectorAll(".newGenderSelect")[i].options[
                    document.querySelectorAll(".newGenderSelect")[i].selectedIndex
                    ].text,
                // FIX 3: Null-guard doc before accessing its properties
                birthCertificate: doc
                    ? doc.fileName + "_" + doc.fileType + "_" + doc.fileContent
                    : "",
                // FIX 3: Null-guard imgs (was already using imgs but still needs guard)
                dependantPicture: imgs
                    ? imgs.fileName + "_" + imgs.fileType + "_" + imgs.fileContent
                    : "",
                institutionCode: instId,
            };
            dependantDetails.push(depJson);
        }

        if (staffList.length > 0) {
            var modalId = document.getElementsByClassName("modalbody")[0].id;
            var matchedDraft = staffList.find(
                (obj) => obj["tempId"] === modalId || obj["staffCode"] === modalId
            );
            if (matchedDraft) {
                tempStaffHolder = matchedDraft;
                tempStaffHolder.dependants = dependantDetails;
            } else {
                tempStaffHolder.dependants = dependantDetails;
                staffList.push(tempStaffHolder);
            }
        } else {
            tempStaffHolder.dependants = dependantDetails;
            staffList.push(tempStaffHolder);
        }
    } else if (document.querySelector(".academic")) {
        const elm = document.querySelectorAll(".academic");
        var cnt = elm.length;

        // FIX 2: Reset array before repopulating to prevent cross-staff data bleed
        staffAcademicDetails = [];

        for (var i = 0; i < cnt; i++) {
            // FIX 3: Guard against null doc when no file is selected
            var doc = await uploadPDFAsJSON(
                document.querySelectorAll(".pdfInput")[i],
                document.querySelectorAll(".fileError")[i]
            );
            var academic = {
                id: "",
                nameOfInstitution:
                document.querySelectorAll(".nameOfInstitution")[i].value,
                dateOfAdmission: document.querySelectorAll(".dateOfAdmission")[i].value,
                programOffered: document.querySelectorAll(".programOffered")[i].value,
                dateOfGraduation:
                document.querySelectorAll(".dateOfGraduation")[i].value,
                certificateType:
                document.querySelectorAll(".certificateType")[i].options[
                    document.querySelectorAll(".certificateType")[i].selectedIndex
                    ].text,
                // FIX 1: Removed unknown field `staffAcademicRecords` (not on Java DTO)
                // FIX 3: Null-guard doc before accessing its properties
                supportingDocs: doc
                    ? doc.fileName + "_" + doc.fileType + "_" + doc.fileContent
                    : "",
                institutionCode: instId,
            };
            staffAcademicDetails.push(academic);
        }
        if (staffList.length > 0) {
            var modalId = document.getElementsByClassName("modalbody")[0].id;
            var matchedDraft = staffList.find(
                (obj) => obj["tempId"] === modalId || obj["staffCode"] === modalId
            );
            if (matchedDraft) {
                tempStaffHolder = matchedDraft;
                tempStaffHolder.academicRecords = staffAcademicDetails;
            } else {
                tempStaffHolder.academicRecords = staffAcademicDetails;
                staffList.push(tempStaffHolder);
            }
        } else {
            tempStaffHolder.academicRecords = staffAcademicDetails;
            staffList.push(tempStaffHolder);
        }
    } else if (document.querySelector(".professional")) {
        const elm = document.querySelectorAll(".professional");
        var cnt = elm.length;

        // FIX 2: Reset array before repopulating to prevent cross-staff data bleed
        staffProfessionalDetails = [];

        for (var i = 0; i < cnt; i++) {
            // FIX 3: Guard against null doc when no file is selected
            var doc = await uploadPDFAsJSON(
                document.querySelectorAll(".pdfInput")[i],
                document.querySelectorAll(".fileError")[i]
            );

            var professional = {
                id: "",
                nameOfInstitution:
                document.querySelectorAll(".nameOfInstitution")[i].value,
                dateOfEmployment:
                document.querySelectorAll(".dateOfEmployment")[i].value,
                dateOfDeparture: document.querySelectorAll(".dateOfDeparture")[i].value,
                designationAtInstitution: document.querySelectorAll(
                    ".designationAtInstitution"
                )[i].value,
                employmentTypeAtInstitution: document.querySelectorAll(
                    ".employmentTypeAtInstitution"
                )[i].value,
                // FIX 1: Removed unknown field `staffProfessionalRecords` (not on Java DTO)
                // FIX 3: Null-guard doc before accessing its properties
                supportingDocs: doc
                    ? doc.fileName + "_" + doc.fileType + "_" + doc.fileContent
                    : "",
                institutionCode: instId,
            };
            staffProfessionalDetails.push(professional);
        }

        if (staffList.length > 0) {
            var modalId = document.getElementsByClassName("modalbody")[0].id;
            var matchedDraft = staffList.find(
                (obj) => obj["tempId"] === modalId || obj["staffCode"] === modalId
            );
            if (matchedDraft) {
                tempStaffHolder = matchedDraft;
                tempStaffHolder.professionalRecords = staffProfessionalDetails;
            } else {
                tempStaffHolder.professionalRecords = staffProfessionalDetails;
                staffList.push(tempStaffHolder);
            }
        } else {
            tempStaffHolder.professionalRecords = staffProfessionalDetails;
            staffList.push(tempStaffHolder);
        }
    } else if (document.querySelector(".imageInputa")) {
        const elm = document.querySelectorAll(".dependants");
        var cnt = elm.length;

        for (var i = 0; i < cnt; i++) {
            var depJson = {
                id: "",
                name: document.querySelectorAll(".newDependantName")[i].value,
                dateOfBirth: document.querySelectorAll(".newDependantDOB")[i].value,
                relationType: document.querySelectorAll(".newRelationshipType")[i]
                    .options[
                    document.querySelectorAll(".newRelationshipType")[i].selectedIndex
                    ].text,
                gender:
                document.querySelectorAll(".newGenderSelect")[i].options[
                    document.querySelectorAll(".newGenderSelect")[i].selectedIndex
                    ].text,
                birthCertificate: uploadPDFAsJSON(
                    document.querySelectorAll(".pdfInput")[i],
                    document.querySelectorAll(".fileError")[i]
                ),
                dependantPicture: getBase64Image(
                    document.querySelectorAll(".dependantPic")[i].querySelector("img")
                ),
                institutionCode: "",
            };
            //dependantDetails.push(depJson);
        }
    } else if (document.querySelector(".imageInput")) {
    }

    passdetails();
    $('.splash').css('display', 'none')
    $(".dismissModal").click();
});

function passdetails() {
}

// Remove aria-hidden when modal opens so screen readers + focus work
$('#staffModal').on('show.bs.modal', function () {
    $(this).removeAttr('aria-hidden');
}).on('hidden.bs.modal', function () {
    $(this).attr('aria-hidden', 'true');
});

function readNewStaffData() {
    // A fresh draft id means "this is a new staff member" - all rows created later in the
    // wizard (dependants/academic/professional tabs) for this staff will share this id.
    currentStaffDraftId = "draft-" + Date.now() + "-" + Math.floor(Math.random() * 100000);

    // Retrieve values from the specified input and select elements
    code = document.getElementById("code").value;
    firstName = document.getElementById("staffFName").value;
    lastName = document.getElementById("staffLName").value;
    fullName = `${firstName} ${lastName}`;
    gender =
        document.getElementById("gender").options[
            document.getElementById("gender").selectedIndex
            ].text;
    dob = document.getElementById("dob").value;

    contact = document.getElementById("contact").value;
    bContact = document.getElementById("bContact").value;
    staffEmail = document.getElementById("staffEmail").value;
    doe = document.getElementById("doe").value;
    nationality =
        document.getElementById("nationality").options[
            document.getElementById("nationality").selectedIndex
            ].text;
    snnit = document.getElementById("snnit").value;
    ht = document.getElementById("homeT").value;
    residence = document.getElementById("residence").value;
    idType =
        document.getElementById("idType").options[
            document.getElementById("idType").selectedIndex
            ].text;
    idNumber = document.getElementById("idNumber").value;
    maritalStatus =
        document.getElementById("maritalStatus").options[
            document.getElementById("maritalStatus").selectedIndex
            ].text;
    designation =
        document.getElementById("staffDesignation").options[
            document.getElementById("staffDesignation").selectedIndex
            ].text;
    nos = document.getElementById("nos").value;
    nok = document.getElementById("nok").value;
    level = document.getElementById("level").value;
}

// FIX: Helper that clones the .test button, wires a new click listener, and puts the
// fresh clone back in the same DOM position. This keeps the button findable by
// getElementsByClassName("test")[0] on every subsequent tab switch, so the click
// handler never crashes before setFileInputFromByteArray runs (which is what broke
// PDF preview when liveBtn2/3/4 each called .replaceWith and left nothing in the DOM).
function refreshAddButton(createFn) {
    var liveBtn = document.getElementsByClassName("test")[0];
    if (!liveBtn) return;
    var newBtn = liveBtn.cloneNode(true);
    liveBtn.replaceWith(newBtn);
    newBtn.addEventListener("click", function () {
        document.getElementsByClassName("modalbody")[0]
            .insertAdjacentHTML("beforeend", createFn());
    });
}

function addTableRow(tableId) {
    // Find the table by ID
    const table = document.getElementById(tableId);

    const testButton = document.getElementsByClassName("test")[0];
    // Create a new row
    const row = table.insertRow();
    row.setAttribute("data-toggle", "modal");
    row.setAttribute("data-target", "#staffModal");
    row.style.cursor = "pointer";
    row.addEventListener("click", function () {
        // Reuse the existing draft for this staff member if one was already pushed
        // (e.g. the user already clicked the row on a previous tab). Only create a
        // new staffList entry the first time this staff's row is interacted with.
        var existingDraft = staffList.find(
            (obj) => obj.tempId && obj.tempId === currentStaffDraftId
        );
        if (existingDraft) {
            tempStaffHolder = existingDraft;
        } else {
            tempStaffHolder = formatNewStaff();
            if (tempStaffHolder.firstNames && tempStaffHolder.firstNames.length > 3) {
                staffList.push(tempStaffHolder);
            }
        }
        document.getElementsByClassName("modalbody")[0].id = tempStaffHolder.tempId || "";
        switch (itra) {
            case 1:
                break;
            case 2:
                document.getElementsByClassName("modalbody")[0].innerHTML = "";
                document.getElementsByClassName("modal-title")[0].innerHTML =
                    "Add Dependants";
                document
                    .getElementsByClassName("modalbody")[0]
                    .insertAdjacentHTML("beforeend", createDependant());
                document.getElementsByClassName("modalbody")[0].id = tempStaffHolder.tempId || "";
                refreshAddButton(createDependant);
                break;
            case 3:
                document.getElementsByClassName("modalbody")[0].innerHTML = "";
                document.getElementsByClassName("modal-title")[0].innerHTML =
                    "Add Academic Documents";
                document
                    .getElementsByClassName("modalbody")[0]
                    .insertAdjacentHTML("beforeend", createAcademicData());
                document.getElementsByClassName("modalbody")[0].id = tempStaffHolder.tempId || "";
                refreshAddButton(createAcademicData);
                break;
            case 4:
                document.getElementsByClassName("modalbody")[0].innerHTML = "";
                document.getElementsByClassName("modal-title")[0].innerHTML =
                    "Add Professional Documents";
                document
                    .getElementsByClassName("modalbody")[0]
                    .insertAdjacentHTML("beforeend", createProfessionalData());
                document.getElementsByClassName("modalbody")[0].id = tempStaffHolder.tempId || "";
                refreshAddButton(createProfessionalData);
                break;
            case 5:
                document.getElementsByClassName("modalbody")[0].innerHTML = "";
                document.getElementsByClassName("modal-title")[0].innerHTML =
                    "Add Staff Designation";
                document
                    .getElementsByClassName("modalbody")[0]
                    .insertAdjacentHTML("beforeend", createDesignationForm(designation));
                document.getElementsByClassName("modalbody")[0].id = tempStaffHolder.tempId || "";
                refreshAddButton(function () {
                    return createDesignationForm("");
                });
                break;
            default:
                break;
        }
    });

    for (let i = 0; i < 8; i++) {
        const cell = row.insertCell();
        var v = instId;
        switch (i) {
            case 0:
                cell.textContent = code;
                break;
            case 1:
                cell.textContent = fullName;
                break;
            case 2:
                cell.textContent = gender;
                break;
            case 3:
                cell.textContent = contact;
                break;
            case 4:
                cell.textContent = doe;
                break;
            case 5:
                cell.textContent = designation;
                break;
            case 6:
                cell.textContent = nationality;
                break;
            case 7:
                cell.textContent = snnit;
                break;
            default:
                cell.textContent = `Cell ${i + 1}`;
        }
    }
    existingStaff(table);
}

function existingStaff(table) {
    var cnt = 0;
    exisitingstaff.forEach(function (d) {
        cnt += 1;
        const row = table.insertRow();
        row.setAttribute("data-toggle", "modal");
        row.setAttribute("data-target", "#staffModal");
        row.style.cursor = "pointer";
        row.addEventListener("click", function () {
            if (
                tempStaffHolder == undefined ||
                tempStaffHolder.staffCode != d.staffCode
            ) {
                var t = staffList.find((obj) => obj["staffCode"] === d.staffCode);
                if (t == undefined) {
                    tempStaffHolder = formatExisting(d);
                    staffList.push(tempStaffHolder);
                } else {
                    tempStaffHolder = t;
                }
            }

            switch (itra) {
                case 1:
                    break;

                // ── DEPENDANTS ──────────────────────────────────────────────
                case 2:
                    document.getElementsByClassName("modalbody")[0].innerHTML = "";
                    document.getElementsByClassName("modal-title")[0].innerHTML = "Add Dependants";
                    document.getElementsByClassName("modalbody")[0].id = d.staffCode;

                    // FIX: refreshAddButton keeps the .test button in the DOM so
                    // getElementsByClassName("test")[0] never returns undefined on
                    // subsequent tab switches, which was crashing the handler before
                    // setFileInputFromByteArray could run and breaking PDF preview.
                    refreshAddButton(createDependant);

                    if (d.dependants.length === 0) {
                        document.getElementsByClassName("modalbody")[0]
                            .insertAdjacentHTML("beforeend", createDependant());
                    } else {
                        d.dependants.forEach(function (dep) {
                            document.getElementsByClassName("modalbody")[0]
                                .insertAdjacentHTML("beforeend", createDependant());

                            const name = document.querySelectorAll(".newDependantName");
                            const dob = document.querySelectorAll(".newDependantDOB");
                            const gens = document.querySelectorAll(".newGenderSelect");
                            const relationTypes = document.querySelectorAll(".newRelationshipType");
                            const dependantPic = document.querySelectorAll(".dependantPicInput");
                            const birthCertificate = document.querySelectorAll(".pdfInput");

                            name[name.length - 1].value = dep.name;
                            dob[name.length - 1].value = dep.dateOfBirth;

                            const gen = gens[name.length - 1];
                            for (let i = 0; i < gen.options.length; i++) {
                                if (gen.options[i].text === dep.gender) {
                                    gen.selectedIndex = i;
                                    break;
                                }
                            }

                            const relationType = relationTypes[name.length - 1];
                            for (let i = 0; i < relationType.options.length; i++) {
                                if (relationType.options[i].text === dep.relationType) {
                                    relationType.selectedIndex = i;
                                    break;
                                }
                            }

                            setFileInputFromByteArray(dep.birthCertificate, birthCertificate[name.length - 1], "pdf");
                            setFileInputFromByteArray(dep.dependantPicture, dependantPic[name.length - 1], "image/png", dep.name);
                        });
                    }
                    break;

                // ── ACADEMIC ─────────────────────────────────────────────────
                case 3:
                    document.getElementsByClassName("modalbody")[0].innerHTML = "";
                    document.getElementsByClassName("modal-title")[0].innerHTML = "Add Academic Documents";
                    document.getElementsByClassName("modalbody")[0].id = d.staffCode;

                    refreshAddButton(createAcademicData);

                    if (d.academicRecords.length === 0) {
                        document.getElementsByClassName("modalbody")[0]
                            .insertAdjacentHTML("beforeend", createAcademicData());
                    } else {
                        d.academicRecords.forEach(function (aca) {
                            document.getElementsByClassName("modalbody")[0]
                                .insertAdjacentHTML("beforeend", createAcademicData());

                            const name = document.querySelectorAll(".nameOfInstitution");
                            const doa = document.querySelectorAll(".dateOfAdmission");
                            const dog = document.querySelectorAll(".dateOfGraduation");
                            const po = document.querySelectorAll(".programOffered");
                            const cte = document.querySelectorAll(".certificateType");
                            const sd = document.querySelectorAll(".pdfInput");

                            name[name.length - 1].value = aca.nameOfInstitution;
                            doa[name.length - 1].value = aca.dateOfAdmission;
                            dog[name.length - 1].value = aca.dateOfGraduation;
                            po[name.length - 1].value = aca.programOffered;

                            const ct = cte[name.length - 1];
                            for (let i = 0; i < ct.options.length; i++) {
                                if (ct.options[i].text === aca.certificateType) {
                                    ct.selectedIndex = i;
                                    break;
                                }
                            }

                            setFileInputFromByteArray(aca.supportingDocs, sd[name.length - 1], "pdf");
                        });
                    }
                    break;

                // ── PROFESSIONAL ──────────────────────────────────────────────
                case 4:
                    document.getElementsByClassName("modalbody")[0].innerHTML = "";
                    document.getElementsByClassName("modal-title")[0].innerHTML = "Add Professional Documents";
                    document.getElementsByClassName("modalbody")[0].id = d.staffCode;

                    refreshAddButton(createProfessionalData);

                    if (d.professionalRecords.length === 0) {
                        document.getElementsByClassName("modalbody")[0]
                            .insertAdjacentHTML("beforeend", createProfessionalData());
                    } else {
                        d.professionalRecords.forEach(function (prof) {
                            document.getElementsByClassName("modalbody")[0]
                                .insertAdjacentHTML("beforeend", createProfessionalData());

                            const name = document.querySelectorAll(".nameOfInstitution");
                            const doe = document.querySelectorAll(".dateOfEmployment");
                            const dod = document.querySelectorAll(".dateOfDeparture");
                            const des = document.querySelectorAll(".designationAtInstitution");
                            const et = document.querySelectorAll(".employmentTypeAtInstitution");
                            const sd = document.querySelectorAll(".pdfInput");

                            name[name.length - 1].value = prof.nameOfInstitution;
                            doe[name.length - 1].value = prof.dateOfEmployment;
                            dod[name.length - 1].value = prof.dateOfDeparture;
                            des[name.length - 1].value = prof.designationAtInstitution;
                            et[name.length - 1].value = prof.employmentTypeAtInstitution;

                            setFileInputFromByteArray(prof.supportingDocs, sd[name.length - 1], "pdf");
                        });
                    }
                    break;

                // ── DESIGNATION (Step 5, new) ──────────────────────────────
                case 5:
                    document.getElementsByClassName("modalbody")[0].innerHTML = "";
                    document.getElementsByClassName("modal-title")[0].innerHTML = "Add Staff Designation";
                    document.getElementsByClassName("modalbody")[0].id = d.staffCode;

                    refreshAddButton(function () {
                        return createDesignationForm("");
                    });

                    document.getElementsByClassName("modalbody")[0]
                        .insertAdjacentHTML("beforeend", createDesignationForm(d.designation || ""));
                    break;

                default:
                    break;
            }
        });

        for (let i = 0; i < 8; i++) {
            const cell = row.insertCell();
            var v = instId;
            switch (i) {
                case 0:
                    cell.textContent = d.staffCode;
                    break;
                case 1:
                    cell.textContent = d.firstNames + " " + d.lastName;
                    break;
                case 2:
                    cell.textContent = d.gender;
                    break;
                case 3:
                    cell.textContent = d.contact1;
                    break;
                case 4:
                    cell.textContent = d.dateOfEmployment;
                    break;
                case 5:
                    cell.textContent = d.designation;
                    break;
                case 6:
                    cell.textContent = d.nationality;
                    break;
                case 7:
                    cell.textContent = d.dependants.length;
                    break;
                default:
                    cell.textContent = `Cell ${i + 1}`;
            }
        }
    });
}

function formatNewStaff() {
    var staffObject = {
        id: "",
        tempId: currentStaffDraftId,
        staffCode: code,
        firstNames: firstName,
        lastName: lastName,
        dateOfBirth: dob,
        nationality: nationality,
        homeTown: ht,
        residentialTown: residence,
        contact1: contact,
        backupContact: bContact,
        staffEmail: staffEmail,
        nationalIDType: idType,
        nationalID: idNumber,
        snnitNumber: snnit,
        maritalStatus: maritalStatus,
        nameOfSpouse: nos,
        dateOfEmployment: doe,
        gender: gender,
        level: level,
        designation: designation,
        staffPicture: "",
        nextOfKing: nok,
        institutionCode: instId,
        dependants: [],
        academicRecords: [],
        professionalRecords: [],
        staffDesignations: [],
        staffSubjects: [],
        staffDocuments: staffDocumentDetails,
    };
    return staffObject;
}

function formatExisting(d) {
    var staffObject = {
        id: "",
        staffCode: d.staffCode,
        firstNames: d.firstNames,
        lastName: d.lastName,
        dateOfBirth: d.dateOfBirth,
        nationality: d.nationality,
        homeTown: d.homeTown,
        residentialTown: d.residentialTown,
        contact1: d.contact1,
        backupContact: d.backupContact,
        staffEmail: d.staffEmail,
        nationalIDType: d.nationalIDType,
        nationalID: d.nationalID,
        snnitNumber: d.snnitNumber,
        maritalStatus: d.maritalStatus,
        nameOfSpouse: d.nameOfSpouse,
        dateOfEmployment: d.dateOfEmployment,
        gender: d.gender,
        level: d.level,
        designation: d.designation,
        staffPicture: "",
        nextOfKing: d.nextOfKing,
        institutionCode: instId,
        dependants: [],
        academicRecords: [],
        professionalRecords: [],
        staffDesignations: [],
        staffSubjects: [],
        staffDocuments: staffDocumentDetails,
    };
    return staffObject;
}

//============================Images UTIL====================================

var input = document.querySelector(".imageInput");
var output = document.querySelector(".imageOutput");
var imagesArray = [];

function imageChange(el) {
    var file = el.files[0];
    if (!file) return;

    imagesArray = [];
    imagesArray.push(file);

    var src = URL.createObjectURL(file);

    // Try to find dependantPic inside parent (modal dependant inputs)
    var container = el.parentElement.querySelector('.dependantPic');

    // Fall back to the main staff picture output
    if (!container) {
        container = document.querySelector('.imageOutput');
    }

    if (!container) {
        console.error("No image container found");
        return;
    }

    container.innerHTML = `
    <div class="crest">
      <img src="${src}" alt="image" class="crest" style="width:100%;height:100%;object-fit:cover;border-radius:10px;">
    </div>`;
}

function displayImages(el) {
    let images = "";
    imagesArray.forEach((image, index) => {
        let src;

        if (image instanceof File) {
            src = URL.createObjectURL(image);
        } else if (typeof image === "string" && image.startsWith("data:image")) {
            src = image;
        } else {
            console.error("Invalid image type:", image);
            return;
        }

        images += `<div class="crest">
                         <img src="${src}" alt="image" id="crestImage" class="crest">
                         <span onclick="deleteImage(${index})">&times;</span>
                       </div>`;
    });

    const container = el.getElementsByClassName("dependantPic")[0];
    if (!container) {
        console.error("Container for images not found!");
        return;
    }

    container.innerHTML = images;
}

function displayFetchImages(srcs) {
    var images = "";
    images +=
        `<div class="crest">
                    <img src=` +
        srcs +
        ` alt="image" id="crestImage" class="crest">
                  </div>`;
    output.innerHTML = images;
}

function deleteImage(index) {
    imagesArray.splice(index, 1);
    imagesArray = [];
    displayImages();
}

function getBase64Image(img) {
    var canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0);
    var dataURL = canvas.toDataURL("image/png");
    return dataURL.split(",")[1].replace('"', "");
}

/* VALIDATION SECTION*/
//============================================================

document.getElementById("staffEmail").addEventListener("blur", function () {
    const email = this.value;
    if (
        email &&
        !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)
    ) {
        this.setCustomValidity(
            "Please include an @ and a valid domain (e.g., name@company.com)"
        );
        this.reportValidity();
    }
});

function dataTableInit(tabeId) {
    $("#" + tabeId).DataTable({
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
            {extend: "csv", title: "Department List", className: "btn btn-sm btn-success"},
            {extend: "pdf", title: "Department List", className: "btn btn-sm btn-danger"},
            {extend: "print", className: "btn btn-sm btn-secondary"},
        ],
        responsive: true,
    });
}