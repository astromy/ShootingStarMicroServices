var selectPlan,
    institution,
    slogan,
    country,
    region,
    city,
    email,
    contact1,
    contact2,
    bececode,
    postalAddress,
    streams,
    population,
    website;
var subjectJsonData = null;
let selectedProgram = null;
let currentStudentData = null

const countries = ["Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Côte d'Ivoire", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo (Congo-Brazzaville)", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czechia (Czech Republic)", "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Holy See", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar (Burma)", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia", "Norway", "Oman", "Pakistan", "Palau", "Palestine State", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"];


$(".prev").click(function () {
    previous();
})

function previous() {
    var tabs = $(".tab-pane");
    var tbs = $(".tab-pane.active");
    tabs.removeClass("active");
    var prevLi = tbs.prev().addClass("active");

    var header = $(".wizardTabs");
    var header1 = $(".wizardTabs.btn-primary");
    header.removeClass("btn-primary");
    header.addClass("btn-default");
    header1.prev().removeClass("btn-default").addClass("btn-primary");
};

$(".next").click(function () {
    if (!validateForm()) {
        //previous();
        return;
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
    /*document.getElementById('client').innerHTML=document.getElementsByName('clientName')[0].value;
         confdata();*/
});


// Initialize country select
const countrySelect = document.getElementById('cob');
countries.forEach(country => {
    const option = document.createElement('option');
    option.value = country;
    option.textContent = country;
    countrySelect.appendChild(option);
});

// Set copyright year
document.getElementById('copyrightYear').textContent = new Date().getFullYear();

//TERMS AND CONDITIONS
$(".tnc").click(function () {
    document.getElementById("modb").innerHTML = contract;
    /*document.getElementById('contractClient').innerHTML=$('[name="clientName"]').val();*/
    document.getElementById("dtime").innerHTML = datetime();
});

$("#submitRequest").click(async function () {
    var approve = $(".approveCheck").is(":checked");
    // if (approve) {
    var c = validateForm();
    if (c === true) {
        // Got to step 1
        //  $('[href=#step1]').tab('show');
        //buildStudentPayload();
        var jso = await buildStudentPayload();
        // Serialize data to post method
        var datastring = $("#simpleForm").serialize();

        // Show notification
        //        Example code for post form

        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        $.ajax({
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            url: "postedStudentRegistration",
            type: "POST",
            data: JSON.stringify(jso),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            cache: false,
            contentType: false,
            processData: false,
            xhr: function () {
                var myXhr = $.ajaxSettings.xhr();
                if (myXhr.upload) {
                    myXhr.upload.addEventListener(
                        "progress",
                        function (e) {
                            if (e.lengthComputable) {
                                $("progress").attr({
                                    value: e.loaded,
                                    max: e.total,
                                });
                            }
                        },
                        false
                    );
                }
                return myXhr;
            },
            success: function (data) {
                swal({
                    title: "Thank you!",
                    text: "Your application is being submitted",
                    type: "success",
                });
            },
            error: function (errMsg) {
                swal({
                    title: "Sorry!",
                    text: "Operation Failed",
                    type: "error",
                });
            },
        });

        /*$.post("preRequestInstitution", jso, function(data) {

      })*/
    }
    /* } else {
       // Show notification
       swal({
         title: "Error!",
         text: "You have to first approve to the Terms and Conditions.",
         type: "error",
       });
     }*/
});

$("#copyrightYear").text(getYear());


function getBase64Image(img) {
    var canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0);
    var dataURL = canvas.toDataURL("image/png");
    return dataURL.split(",")[1].replace('"', "");
}


const input = document.querySelector(".imageInput");
const output = document.querySelector(".imageOutput");
var verfyOutput = document.querySelector("#studentPicture");
let imagesArray = [];

input.addEventListener("change", () => {
    const file = input.files;
    imagesArray = [];
    imagesArray.push(file[0]);
    displayImages();
});

function displayImages() {
    let images = "";
    imagesArray.forEach((image, index) => {
        images += `<div class="studPic">
                  <img src="${URL.createObjectURL(
            image
        )}" alt="image" id="studentPicture" class="studPic">
                  <span onclick="deleteImage(${index})">&times;</span>
                </div>`;
    });
    output.innerHTML = images;
    verfyOutput = images;
}

function deleteImage(index) {
    imagesArray.splice(index, 1);
    imagesArray = [];
    displayImages();
}

function datetime() {
    var objToday = new Date(),
        weekday = new Array(
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
        ),
        dayOfWeek = weekday[objToday.getDay()],
        domEnder = (function () {
            var a = objToday;
            if (/1/.test(parseInt((a + "").charAt(0)))) return "th";
            a = parseInt((a + "").charAt(1));
            return 1 == a ? "st" : 2 == a ? "nd" : 3 == a ? "rd" : "th";
        })(),
        dayOfMonth =
            today + (objToday.getDate() < 10)
                ? "0" + objToday.getDate() + domEnder
                : objToday.getDate() + domEnder,
        months = new Array(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        ),
        curMonth = months[objToday.getMonth()],
        curYear = objToday.getFullYear(),
        curHour =
            objToday.getHours() > 12
                ? objToday.getHours() - 12
                : objToday.getHours() < 10
                    ? "0" + objToday.getHours()
                    : objToday.getHours(),
        curMinute =
            objToday.getMinutes() < 10
                ? "0" + objToday.getMinutes()
                : objToday.getMinutes(),
        curSeconds =
            objToday.getSeconds() < 10
                ? "0" + objToday.getSeconds()
                : objToday.getSeconds(),
        curMeridiem = objToday.getHours() > 12 ? "PM" : "AM";
    var today =
        curHour +
        ":" +
        curMinute +
        "." +
        curSeconds +
        curMeridiem +
        " " +
        dayOfWeek +
        " " +
        dayOfMonth +
        " of " +
        curMonth +
        ", " +
        curYear;
    return objToday;
}

function getYear() {
    var objToday = new Date();
    curYear = objToday.getFullYear();
    return curYear;
}

async function fetchAllInstitutions() {
    try {
        const response = await fetchPost("/fetchAllInstitutions", {});
        const formattedSchools = formatSchoolsData(response);
        return formattedSchools;
    } catch (error) {
        console.error("Error fetching and formatting schools:", error);
        return [];
    }
}

async function fetchStudent(studentID, institutionId, institutionName) {
    try {
        const requestData = {
            key: ["studentId", "institutionCode", "applictionCode"],
            val: [studentID, institutionId, studentID],
        };
        const response = await fetchPost("/fetchStudent", requestData);

        // Treat null, empty object, or empty array all as "not found"
        const notFound =
            response === null ||
            (Array.isArray(response) && response.length === 0) ||
            (response && typeof response === "object" && !Array.isArray(response) && Object.keys(response).length === 0);

        if (notFound) {
            swal({
                title: "Not Found",
                text: "No record found for Student ID \"" + studentID + "\" at " + institutionName + ".\nPlease check the ID and try again.",
                type: "info",
            });
            return null;
        }

        currentStudentData = response;
        populateStudentForm(response);
        document.querySelector(".next").click();

    } catch (error) {
        console.error("Error fetching student data:", error.message);
        swal({title: "Error", text: "Could not reach the server. Please try again.", type: "error"});
        return null;
    }
}

//**************************************************************

document.addEventListener("DOMContentLoaded", async function () {
    const schoolsContainer = document.getElementById("schools-container");
    const searchInput = document.getElementById("search-input");
    const regionFilter = document.getElementById("region-filter");
    const programFilter = document.getElementById("program-filter");
    const studentIDInput = document.getElementById("studentID");
    const findStudentBtn = document.getElementById("findStudentBtn");
    const step1Hint = document.getElementById("step1-hint");

    // Track state for both prerequisites
    let selectedSchool = null;

    // ── helper: update button state and hint text ──────────────────────────
    function updateStep1State() {
        const hasID = studentIDInput.value.trim().length >= 3;
        const hasSchool = selectedSchool !== null;

        if (findStudentBtn) {
            findStudentBtn.disabled = !(hasID && hasSchool);
        }

        if (step1Hint) {
            if (!hasID && !hasSchool) {
                step1Hint.textContent = "Enter your Student ID then select a school.";
                step1Hint.className = "text-muted";
            } else if (!hasID) {
                step1Hint.textContent = "Now enter your Student ID to continue.";
                step1Hint.className = "text-warning";
            } else if (!hasSchool) {
                step1Hint.textContent = "Good — now click your school from the list below.";
                step1Hint.className = "text-warning";
            } else {
                step1Hint.textContent = "Ready! Click Find to look up your record.";
                step1Hint.className = "text-success";
            }
        }
    }

    // ── trigger fetch (shared by button click and Enter key) ───────────────
    async function triggerFetch() {
        if (!selectedSchool) {
            swal({title: "Select a School", text: "Please click on your school from the list first.", type: "warning"});
            return;
        }
        const id = studentIDInput.value.trim();
        if (id.length < 3) {
            studentIDInput.focus();
            studentIDInput.style.border = "1px solid red";
            swal({
                title: "Student ID required",
                text: "Please enter a valid Student ID (at least 3 characters).",
                type: "warning"
            });
            return;
        }

        // Show loading state on button
        if (findStudentBtn) {
            findStudentBtn.disabled = true;
            findStudentBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Searching…';
        }

        await fetchStudent(id, selectedSchool.bececode, selectedSchool.name);

        // Restore button
        if (findStudentBtn) {
            findStudentBtn.disabled = false;
            findStudentBtn.innerHTML = '<i class="fas fa-search"></i> Find';
        }
    }

    // ── wire up button and Enter key ───────────────────────────────────────
    if (findStudentBtn) {
        findStudentBtn.addEventListener("click", triggerFetch);
    }

    studentIDInput.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            triggerFetch();
        }
    });

    studentIDInput.addEventListener("input", function () {
        // Clear red border as user types
        this.style.border = "";
        updateStep1State();
    });

    const schools = await fetchAllInstitutions();

    // ── generate school cards ──────────────────────────────────────────────
    function generateSchoolCards(schoolsArray) {
        schoolsContainer.innerHTML = "";

        if (schoolsArray.length === 0) {
            schoolsContainer.innerHTML = `
                <div class="no-results">
                    <i class="fas fa-search fa-3x mb-3"></i>
                    <h4>No schools found</h4>
                    <p>Try adjusting your search or filters</p>
                </div>`;
            return;
        }

        schoolsArray.forEach((school) => {
            const programTags = school.programs
                .map((program) => `<span class="program-tag">${program}</span>`)
                .join("");

            const schoolCard = document.createElement("div");
            schoolCard.className = "school-card";
            schoolCard.dataset.id = school.id;
            schoolCard.innerHTML = `
                <div class="school-image" style="background-color: ${school.color};">
                    <img src="${school.logo}" alt="${school.name} Logo" class="school-logo">
                </div>
                <div class="school-content">
                    <h3 class="school-name">${school.name}</h3>
                    <div class="school-location">
                        <i class="fas fa-map-marker-alt"></i> ${school.location}
                    </div>
                    <span class="school-type">${school.type}</span>
                    <div class="school-programs">${programTags}</div>
                </div>`;

            schoolsContainer.appendChild(schoolCard);
        });

        // Card click → select only; no fetch here
        document.querySelectorAll(".school-card").forEach((card) => {
            card.addEventListener("click", function () {
                document.querySelectorAll(".school-card")
                    .forEach((c) => c.classList.remove("selected"));

                this.classList.add("selected");

                const schoolId = this.dataset.id;
                selectedSchool = schools.find((s) => s.id == schoolId);
                institution = selectedSchool.bececode;

                updateStep1State();

                // If ID is already filled and valid, go straight to fetch
                if (studentIDInput.value.trim().length >= 3) {
                    triggerFetch();
                }
            });
        });
    }

    // Initial render
    generateSchoolCards(schools);
    updateStep1State();

    // Search / filter
    searchInput.addEventListener("input", filterSchools);
    regionFilter.addEventListener("change", filterSchools);
    programFilter.addEventListener("change", filterSchools);

    function filterSchools() {
        const searchTerm = searchInput.value.toLowerCase();
        const regionValue = regionFilter.value;
        const programValue = programFilter.value;

        const filteredSchools = schools.filter((school) => {
            const matchesSearch =
                school.name.toLowerCase().includes(searchTerm) ||
                school.location.toLowerCase().includes(searchTerm);
            const matchesRegion =
                regionValue === "" || school.location.includes(regionValue);
            const matchesProgram =
                programValue === "" ||
                school.programs.some((program) => program === programValue);
            return matchesSearch && matchesRegion && matchesProgram;
        });

        generateSchoolCards(filteredSchools);
    }
});

/**
 * Makes a POST request using fetch with Bearer token.
 *
 * @param {string} url - The API endpoint.
 * @param {object} data - The JSON payload to send.
 * @returns {Promise<any>} - The parsed JSON response.
 */
async function fetchPost(url, data) {
    const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
    const csrfHeader = document.querySelector(
        "meta[name='_csrf_header']"
    )?.content;

    try {
        const headers = {
            "Content-Type": "application/json",
            Accept: "application/json",
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch(url, {
            method: "POST",
            headers: headers,
            body: JSON.stringify(data),
            credentials: "include",
        });

        // Handle 404 specifically - it's not an error, just "not found"
        if (response.status === 404) {
            return null;
        }

        // Still throw error for other non-2xx status codes (500, 400, etc.)
        if (!response.ok) {
            let errorText;
            try {
                errorText = await response.text();
            } catch (e) {
                errorText = "No error message available";
            }
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        // Handle empty responses
        const contentLength = response.headers.get("content-length");
        const contentType = response.headers.get("content-type");

        if (
            contentLength === "0" ||
            !contentType ||
            !contentType.includes("application/json")
        ) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error("POST Error:", error.message || error);
        // Re-throw the error with a proper message
        throw new Error(error.message || "Network request failed");
    }
}

function formatSchoolsData(originalData) {
    return originalData.map((school) => {
        // Extract unique program names from class groups
        const programs = [
            ...new Set(
                school.classList.map((cls) => {
                    const classGroup = cls.classGroup;
                    // Map classGroup numbers to program names
                    const programMap = {
                        1: "Creche",
                        2: "Nursery",
                        3: "K.G",
                        4: "Lower Primary",
                        5: "Upper Primary",
                        6: "J.H.S",
                    };
                    return programMap[classGroup] || `Program ${classGroup}`;
                })
            ),
        ];

        // Determine school type based on name and available data
        const schoolName = school.name.toLowerCase();
        let type = "Mixed"; // Default

        if (
            schoolName.includes("girls") ||
            schoolName.includes("girls'") ||
            schoolName.includes("female")
        ) {
            type = "Girls";
        } else if (
            schoolName.includes("boys") ||
            schoolName.includes("boys'") ||
            schoolName.includes("male")
        ) {
            type = "Boys";
        }

        // Create location string
        const location = `${school.city}, ${school.region}`;

        const bececode = `${school.bececode}`

        // Convert base64 crest to data URL if exists, otherwise use placeholder
        let logo =
            "https://via.placeholder.com/100x100/3498db/ffffff?text=" +
            encodeURIComponent(school.name.charAt(0));
        if (school.crest) {
            logo = `data:image/png;base64,${school.crest}`;
        }

        // Predefined color palette for consistent colors
        const colorPalette = [
            "#3498db",
            "#2ecc71",
            "#9b59b6",
            "#e74c3c",
            "#f39c12",
            "#1abc9c",
            "#d35400",
            "#27ae60",
            "#8e44ad",
            "#c0392b",
            "#16a085",
            "#2980b9",
            "#f1c40f",
            "#e67e22",
            "#2c3e50",
        ];

        const color = colorPalette[school.id % colorPalette.length];

        return {
            id: school.id,
            name: school.name,
            location: location,
            type: type,
            programs: programs,
            logo: logo,
            color: color,
            bececode: bececode,
        };
    });
}

function populateStudentForm(studentData) {
    if (!studentData) return;

    // Safe helper — trims only if value is a non-null string, otherwise returns ""
    const s = (v) => (v != null ? String(v).trim() : "");

    // Safe setter — only writes to a field if the element exists
    const set = (id, v) => {
        const el = document.getElementById(id);
        if (el) el.value = s(v);
    };

    // Safe select matcher — finds and selects the option whose text contains `val`
    const selectOpt = (id, val) => {
        if (!val) return;
        const el = document.getElementById(id);
        if (!el) return;
        const lower = String(val).toLowerCase();
        for (let i = 0; i < el.options.length; i++) {
            if (el.options[i].text.toLowerCase().includes(lower)) {
                el.selectedIndex = i;
                break;
            }
        }
    };

    // ── Basic student fields ─────────────────────────────────────────────────
    set("studFName", studentData.firstName);
    set("studSurName", studentData.lastName);
    set("studOtherName", studentData.otherName);

    selectOpt("studGender", studentData.gender);

    // dateOfBirth comes as "2019-01-29" (LocalDate) — safe via s()
    // dateOfAdmission is null from the admission service — leaves field blank
    set("studDOB", studentData.dateOfBirth);
    set("studDOA", studentData.dateOfAdmission);

    set("placeOfBirth", studentData.placeOfBirth);
    set("studResidence", studentData.residentialLocality);  // null-safe; blank if absent

    selectOpt("cob", studentData.countryOfBirth);

    set("denomination", studentData.denomination);

    // ── Parent information ───────────────────────────────────────────────────
    if (studentData.studentParents && studentData.studentParents.length > 0) {
        const parents = studentData.studentParents;

        const father = parents.find((p) => p.contact1 && p.contact1.trim()) || parents[0];
        if (father) {
            set("fatherFirstName", father.firstNames);
            set("fatherLastName", father.lastName);
            set("fatherEmail", father.email);
            set("fatherContact1", father.contact1);
            set("fatherContact2", father.contact2);   // null-safe — contact2 can be null
            set("fatherOccupation", father.occupation);
            set("fatherPlaceOfWork", father.placeOfWork);
            selectOpt("fatherType", father.parentType);
        }

        const mother = parents.find((p) => p !== father) || parents[1];
        if (mother && mother !== father) {
            set("motherFirstName", mother.firstNames);
            set("motherLastName", mother.lastName);
            set("motherEmail", mother.email);
            set("motherContact1", mother.contact1);
            set("motherContact2", mother.contact2);   // null-safe
            set("motherOccupation", mother.occupation);
            set("motherPlaceOfWork", mother.placeOfWork);
            selectOpt("motherType", mother.parentType);
        }
    }

    // ── Program / class auto-select (subject cards hidden but kept for fallback) ──
    if (studentData.studentClass) {
        const studentClass = studentData.studentClass.toLowerCase();
        let programCard = null;
        let programType = null;

        if (studentClass.includes("arts")) {
            programCard = document.getElementById("arts-card");
            programType = "arts";
        } else if (studentClass.includes("science")) {
            programCard = document.getElementById("science-card");
            programType = "science";
        } else if (studentClass.includes("business")) {
            programCard = document.getElementById("business-card");
            programType = "business";
        } else if (studentClass.includes("visual") || studentClass.includes("art")) {
            programCard = document.getElementById("visual-arts-card");
            programType = "visual-arts";
        } else if (studentClass.includes("home") || studentClass.includes("economics")) {
            programCard = document.getElementById("home-economics-card");
            programType = "home-economics";
        } else if (studentClass.includes("technical") || studentClass.includes("tech")) {
            programCard = document.getElementById("technical-card");
            programType = "technical";
        } else if (studentClass.includes("agriculture") || studentClass.includes("agri")) {
            programCard = document.getElementById("agriculture-card");
            programType = "agriculture";
        }

        if (programCard) {
            programCard.click();
            if (studentData.studentSubjectsResponse && studentData.studentSubjectsResponse.length > 0) {
                selectSubjectsBasedOnData(studentData.studentSubjectsResponse, programType);
            } else {
                selectFirstSubjectInProgram(programType);
            }
        }
    }

    if (studentData.picture) {
        displayStudentPicture(studentData.picture);
    }

    // Reveal the items-to-purchase panel now that we have student data
    showPurchaseItems(studentData);

    console.log("Student data populated successfully");
}

/**
 * Reveals the Items-to-Purchase panel (Step 4) and optionally injects
 * school-specific fee rows if the student data carries them.
 */
function showPurchaseItems(studentData) {
    const loading = document.getElementById('purchase-items-loading');
    const list = document.getElementById('purchase-items-list');

    if (loading) loading.style.display = 'none';
    if (list) list.style.display = 'block';

    // If the student response carries fee/preorder items, render them
    if (studentData && studentData.preOrderItems && studentData.preOrderItems.length > 0) {
        const feesBody = document.getElementById('fees-rows');
        if (feesBody) {
            feesBody.innerHTML = '';
            studentData.preOrderItems.forEach(function (item) {
                const tr = document.createElement('tr');
                tr.innerHTML = `<td>${item.itemName || item.name || '—'}</td>
                                <td>${item.period || item.frequency || '—'}</td>
                                <td>${item.amount || item.cost || '—'}</td>`;
                feesBody.appendChild(tr);
            });
        }
    }
}

function selectSubjectsBasedOnData(subjectsData, programType) {
    // Get all subject checkboxes for the program
    let subjectCheckboxes;
    const subjectSelector = `.${programType}-subject`;
    subjectCheckboxes = document.querySelectorAll(subjectSelector);

    // Clear any previously selected subjects in this program
    subjectCheckboxes.forEach(checkbox => {
        checkbox.checked = false;
    });

    // Select subjects that match the backend data
    subjectsData.forEach(subject => {
        // Clean the subject name more thoroughly
        const subjectName = subject.subjectName
            .replace(/\s+/g, ' ')  // Replace multiple whitespace with single space
            .replace(/\n/g, ' ')   // Remove newlines
            .trim()                // Trim leading/trailing spaces
            .toLowerCase();

        // Find the checkbox that matches this subject
        const matchingCheckbox = Array.from(subjectCheckboxes).find(checkbox => {
            // Find the parent div and then the label within it
            const parentDiv = checkbox.closest('.subject-item');
            if (parentDiv) {
                const label = parentDiv.querySelector('label');
                if (label) {
                    // Clean up the label text
                    const labelText = label.textContent
                        .replace(/\s+/g, ' ')
                        .replace(/\n/g, ' ')
                        .trim()
                        .toLowerCase();

                    console.log(`Comparing: "${labelText}" vs "${subjectName}"`);

                    // Use exact match instead of includes for better accuracy
                    return labelText === subjectName;
                }
            }
            return false;
        });

        if (matchingCheckbox) {
            console.log("Found matching checkbox for:", subjectName, matchingCheckbox);

            // Check if checkbox is disabled or readonly
            if (matchingCheckbox.disabled) {
                console.log("Checkbox is disabled:", matchingCheckbox);
            }
            if (matchingCheckbox.readOnly) {
                console.log("Checkbox is readOnly:", matchingCheckbox);
            }

            // Try different approaches to select the checkbox
            matchingCheckbox.checked = true;

            console.log("Selected subject:", subjectName, "Checkbox state:", matchingCheckbox.checked);
            if (matchingCheckbox.checked == false) {
                matchingCheckbox.checked = true;
                console.log("Selected subject:", subjectName, "Checkbox state:", matchingCheckbox.checked);
            }

        } else {
            console.log("Could not find checkbox for subject:", subjectName);
            console.log("Available labels:", Array.from(subjectCheckboxes).map(cb => {
                const parentDiv = cb.closest('.subject-item');
                const label = parentDiv ? parentDiv.querySelector('label') : null;
                return label ? `"${label.textContent.replace(/\s+/g, ' ').trim()}"` : 'no label';
            }));
        }
    });
}


// Function to display base64 image
function displayStudentPicture(base64Data) {
    try {
        // Create the image URL from base64 data
        const imageUrl = `data:image/png;base64,${base64Data}`;

        // Get the image element
        const imgElement = document.getElementById('studentPicture');
        const imageOutput = document.querySelector('.imageOutput');

        if (imgElement) {
            // Set the image source
            imgElement.src = imageUrl;

            // If you want to update the preview container too
            if (imageOutput) {
                imageOutput.innerHTML = `
          <div class="studPic">
            <img src="${imageUrl}" alt="Student Picture" id="studentPicture" class="studPic">
            <span onclick="deleteImage(0)">&times;</span>
          </div>
        `;
            }
        }
    } catch (error) {
        console.error('Error displaying student picture:', error);
    }
}

function selectFirstSubjectInProgram(programType) {
    let subjectCheckboxes;
    const subjectSelector = `.${programType}-subject`;
    subjectCheckboxes = document.querySelectorAll(subjectSelector);

    // Select the first available checkbox that isn't already checked
    if (subjectCheckboxes.length > 0) {
        const firstAvailableCheckbox = Array.from(subjectCheckboxes).find(
            (checkbox) => !checkbox.checked
        );
        if (firstAvailableCheckbox) {
            firstAvailableCheckbox.checked = true;
            firstAvailableCheckbox.dispatchEvent(new Event("change", {bubbles: true}));
            firstAvailableCheckbox.dispatchEvent(new Event("click", {bubbles: true}));
        }
    }
}


async function buildStudentPayload() {
    if (!currentStudentData) {
        throw new Error("No student data loaded. Please fetch student first.");
    }

    // Start with the complete existing data
    const payload = {...currentStudentData};

    // Only update fields that have values in the UI
    const studentId = document.getElementById('studentID').value.trim();
    if (studentId) payload.studentId = studentId;

    const firstName = document.getElementById('studFName').value.trim();
    if (firstName) payload.firstName = firstName;

    const otherName = document.getElementById('studOtherName').value.trim();
    if (otherName) payload.otherName = otherName;

    const lastName = document.getElementById('studSurName').value.trim();
    if (lastName) payload.lastName = lastName;

    const dateOfBirth = document.getElementById('studDOB').value.trim();
    if (dateOfBirth) payload.dateOfBirth = dateOfBirth;

    const dateOfAdmission = document.getElementById('studDOA').value.trim();
    if (dateOfAdmission) payload.dateOfAdmission = dateOfAdmission;

    const placeOfBirth = document.getElementById('placeOfBirth').value.trim();
    if (placeOfBirth) payload.placeOfBirth = placeOfBirth;

    const genderSelect = document.getElementById('studGender');
    if (genderSelect.value) payload.gender = genderSelect.value;

    const countryOfBirthSelect = document.getElementById('cob');
    if (countryOfBirthSelect.value) payload.countryOfBirth = countryOfBirthSelect.value;

    const residentialLocality = document.getElementById('studResidence').value.trim();
    if (residentialLocality) payload.residentialLocality = residentialLocality;

    const denomination = document.getElementById('denomination').value.trim();
    if (denomination) payload.denomination = denomination;

    // Handle picture - only update if changed
    const pictureInput = document.querySelector('.imageInput');
    const existingPicture = document.getElementById('studentPicture');


    if (pictureInput && pictureInput.files.length > 0) {
        const file = pictureInput.files[0];

        const optimizedDataURL = await optimizeImage(file, {
            maxSize: 400,
            quality: 0.7,
            outputFormat: 'jpeg'
        }).catch(error => {
            console.error("Image optimization failed", error);
            throw error;
        });

        payload.picture = optimizedDataURL.split(',')[1];
    }

    // If picture wasn't changed, keep the original value

    // Update institution code if selected
    if (institution) payload.institutionCode = institution;

    // Build parents data from UI (only if parents were modified)
    const uiParents = buildParentsData(payload.studentId);
    if (uiParents && uiParents.length > 0) {
        // Merge parents data - this is more complex, see next step
        payload.studentParents = mergeParentsData(currentStudentData.studentParents, uiParents);
    }

    // Build subjects data from UI
    const uiSubjects = buildSubjectsData();
    if (uiSubjects && uiSubjects.length > 0) {
        payload.studentSubjectsResponse = uiSubjects;
    }

    return payload;
}

function mergeParentsData(existingParents, uiParents) {
    const stripSlot = (p) => {
        const {_uiSlot, ...rest} = p;
        return rest;
    };

    if (!existingParents || existingParents.length === 0) {
        return uiParents.map(stripSlot);
    }
    if (!uiParents || uiParents.length === 0) {
        return existingParents;
    }

    const existingFather = existingParents.find(p => p.contact1 && p.contact1.trim()) || existingParents[0];
    const existingMother = existingParents.find(p => p !== existingFather) || existingParents[1];

    const merged = [];
    const touchedSlots = new Set();

    uiParents.forEach(uiParent => {
        const slot = uiParent._uiSlot;
        touchedSlots.add(slot);
        const base = slot === 'father' ? existingFather : slot === 'mother' ? existingMother : null;
        merged.push(stripSlot(base ? {...base, ...uiParent} : uiParent));
    });

    if (existingFather && !touchedSlots.has('father')) merged.push(existingFather);
    if (existingMother && existingMother !== existingFather && !touchedSlots.has('mother')) merged.push(existingMother);

    return merged;
}

function buildParentsData(studentId) {
    const parents = [];

    const fatherData = buildParentData('father', studentId);
    if (hasParentData(fatherData)) {
        fatherData._uiSlot = 'father';
        parents.push(fatherData);
    }

    const motherData = buildParentData('mother', studentId);
    if (hasParentData(motherData)) {
        motherData._uiSlot = 'mother';
        parents.push(motherData);
    }

    return parents;
}

function buildParentData(prefix, studentId) {
    const firstName = document.getElementById(`${prefix}FirstName`).value.trim();
    const lastName = document.getElementById(`${prefix}LastName`).value.trim();
    const email = document.getElementById(`${prefix}Email`).value.trim();
    const contact1 = document.getElementById(`${prefix}Contact1`).value.trim();
    const contact2 = document.getElementById(`${prefix}Contact2`).value.trim();
    const occupation = document.getElementById(`${prefix}Occupation`).value.trim();
    const placeOfWork = document.getElementById(`${prefix}PlaceOfWork`).value.trim();
    const parentType = document.getElementById(`${prefix}Type`).value.trim();

    // Only include fields that have values
    const parentData = {
        institutionCode: institution,
        studentId: studentId
    };

    if (firstName) parentData.firstNames = firstName;
    if (lastName) parentData.lastName = lastName;
    if (email) parentData.email = email;
    if (contact1) parentData.contact1 = contact1;
    if (contact2) parentData.contact2 = contact2;
    if (occupation) parentData.occupation = occupation;
    if (placeOfWork) parentData.placeOfWork = placeOfWork;
    if (parentType) parentData.parentType = parentType;

    return parentData;
}

function hasParentData(parentData) {
    // Check if any field (except institutionCode and studentId) has data
    const {institutionCode, studentId, ...rest} = parentData;
    return Object.values(rest).some(value => value && value.trim() !== '');
}

// Function to build subjects data
function buildSubjectsData() {
    const subjects = [];

    // Get all checked subject checkboxes
    const checkedSubjects = document.querySelectorAll('input[type="checkbox"]:checked');

    checkedSubjects.forEach(checkbox => {
        const subjectName = checkbox.nextElementSibling.textContent.trim();
        subjects.push({
            subjectName: subjectName
        });
    });

    return subjects;
}


//============================= VALIDATION ==============================

// Form validation for the student data capturing form
function validateForm() {
    // Track validation status
    let isValid = true;
    let errorMessages = [];

    // Get current active tab
    const activeTab = document.querySelector('.tab-pane.active');
    const tabIndex = Array.from(document.querySelectorAll('.tab-pane')).indexOf(activeTab);

    // Validate based on current step
    switch (tabIndex) {
        case 0: // School Selection
            if (!validateSchoolSelection()) {
                isValid = false;
                errorMessages.push("Please select a school and provide a Student ID");
            }
            break;

        case 1: // Student Bio Data
            if (!validateStudentBioData()) {
                isValid = false;
                errorMessages.push("Please complete all required student information");
            }
            break;

        case 2: // Parent Bio Data
            if (!validateParentData()) {
                isValid = false;
                errorMessages.push("Please complete all required parent information");
            }
            break;

        case 3: // Items to Purchase — no validation required, parent reviews the list
            break;

        case 4: // Approval
            // No validation needed for approval step
            break;
    }

    // Show error messages if any
    if (!isValid) {
        showValidationErrors(errorMessages);
    }

    return isValid;
}

// Validate School Selection (Step 1)
function validateSchoolSelection() {
    let isValid = true;

    // Check if a school is selected
    const selectedSchool = document.querySelector('.school-card.selected');
    if (!selectedSchool) {
        isValid = false;
        highlightError('Please select a school', 'schools-container');
    }

    // Validate Student ID
    const studentId = document.getElementById('studentID').value.trim();
    if (!studentId) {
        isValid = false;
        highlightFieldError('studentID', 'Student ID is required');
    } else if (studentId.length < 3) {
        isValid = false;
        highlightFieldError('studentID', 'Student ID must be at least 3 characters');
    }

    return isValid;
}

// Validate Student Bio Data (Step 2)
function validateStudentBioData() {
    let isValid = true;

    // Required fields
    const requiredFields = [
        'studFName', 'studSurName', 'studGender',
        'studDOB', 'studDOA', 'placeOfBirth',
        'cob', 'studResidence', 'denomination'
    ];

    requiredFields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (!field.value.trim()) {
            isValid = false;
            highlightFieldError(fieldId, `${field.labels[0].textContent} is required`);
        }
    });

    // Validate names
    const firstName = document.getElementById('studFName').value.trim();
    const surName = document.getElementById('studSurName').value.trim();

    if (firstName && firstName.length < 2) {
        isValid = false;
        highlightFieldError('studFName', 'First name must be at least 2 characters');
    }

    if (surName && surName.length < 2) {
        isValid = false;
        highlightFieldError('studSurName', 'Surname must be at least 2 characters');
    }

    // Validate dates
    const dob = document.getElementById('studDOB').value;
    const doa = document.getElementById('studDOA').value;

    if (dob) {
        const dobDate = new Date(dob);
        const today = new Date();
        const minAgeDate = new Date();
        minAgeDate.setFullYear(today.getFullYear() - 4); // At least 4 years old

        if (dobDate > minAgeDate) {
            isValid = false;
            highlightFieldError('studDOB', 'Student must be at least 4 years old');
        }
    }

    if (doa && dob) {
        const doaDate = new Date(doa);
        const dobDate = new Date(dob);

        if (doaDate < dobDate) {
            isValid = false;
            highlightFieldError('studDOA', 'Date of admission cannot be before date of birth');
        }
    }

    // Validate picture (if uploaded)
    const pictureInput = document.querySelector('.imageInput');
    if (pictureInput.files.length > 0) {
        const file = pictureInput.files[0];
        const validTypes = ['image/jpeg', 'image/png', 'image/jpg'];
        const maxSize = 2 * 1024 * 1024; // 2MB

        if (!validTypes.includes(file.type)) {
            isValid = false;
            highlightError('Please upload a valid image (JPEG, PNG, JPG)', 'studPic');
        }

        if (file.size > maxSize) {
            isValid = false;
            highlightError('Image size must be less than 2MB', 'studPic');
        }
    }

    return isValid;
}

// Validate Parent Data (Step 3)
function validateParentData() {
    let isValid = true;
    let hasAtLeastOneParent = false;

    // Father validation
    const fatherFirstName = document.getElementById('fatherFirstName').value.trim();
    const fatherLastName = document.getElementById('fatherLastName').value.trim();

    if (fatherFirstName || fatherLastName) {
        hasAtLeastOneParent = true;

        const fatherRequired = [
            'fatherFirstName', 'fatherLastName', 'fatherEmail',
            'fatherContact1', 'fatherOccupation', 'fatherPlaceOfWork', 'fatherType'
        ];

        fatherRequired.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (!field.value.trim()) {
                isValid = false;
                highlightFieldError(fieldId, `${field.labels[0].textContent} is required for father`);
            }
        });

        // Validate father email
        const fatherEmail = document.getElementById('fatherEmail').value.trim();
        if (fatherEmail && !isValidEmail(fatherEmail)) {
            isValid = false;
            highlightFieldError('fatherEmail', 'Please enter a valid email address for father');
        }

        // Validate father contact
        const fatherContact = document.getElementById('fatherContact1').value.trim();
        if (fatherContact && !isValidPhone(fatherContact)) {
            isValid = false;
            highlightFieldError('fatherContact1', 'Please enter a valid phone number for father');
        }
    }

    // Mother validation
    const motherFirstName = document.getElementById('motherFirstName').value.trim();
    const motherLastName = document.getElementById('motherLastName').value.trim();

    if (motherFirstName || motherLastName) {
        hasAtLeastOneParent = true;

        const motherRequired = [
            'motherFirstName', 'motherLastName', 'motherEmail',
            'motherContact1', 'motherOccupation', 'motherPlaceOfWork', 'motherType'
        ];

        motherRequired.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (!field.value.trim()) {
                isValid = false;
                highlightFieldError(fieldId, `${field.labels[0].textContent} is required for mother`);
            }
        });

        // Validate mother email
        const motherEmail = document.getElementById('motherEmail').value.trim();
        if (motherEmail && !isValidEmail(motherEmail)) {
            isValid = false;
            highlightFieldError('motherEmail', 'Please enter a valid email address for mother');
        }

        // Validate mother contact
        const motherContact = document.getElementById('motherContact1').value.trim();
        if (motherContact && !isValidPhone(motherContact)) {
            isValid = false;
            highlightFieldError('motherContact1', 'Please enter a valid phone number for mother');
        }
    }

    // Check if at least one parent is provided
    if (!hasAtLeastOneParent) {
        isValid = false;
        highlightError('Please provide information for at least one parent', 'parentsDetailForm');
    }

    return isValid;
}

// Validate Subject Selection (Step 4)
function validateSubjectSelection() {
    let isValid = true;
    let selectedProgram = null;
    let selectedSubjects = 0;

    // Check which program has selected subjects
    const programCards = document.querySelectorAll('.program-card');
    programCards.forEach(card => {
        const subjectCheckboxes = card.querySelectorAll('input[type="checkbox"]:checked');
        if (subjectCheckboxes.length > 0) {
            selectedProgram = card.id.replace('-card', '');
            selectedSubjects = subjectCheckboxes.length;
        }
    });

    // Validate program selection
    if (!selectedProgram) {
        isValid = false;
        highlightError('Please select at least one subject from a program', 'subject-selection-container');
        return isValid;
    }

    // Validate subject count (must be exactly 4 for most programs)
    if (selectedSubjects !== 4) {
        isValid = false;
        highlightError(`Please select exactly 4 subjects from the ${formatProgramName(selectedProgram)} program`, selectedProgram + '-card');
    }

    return isValid;
}

// Helper functions
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function isValidPhone(phone) {
    const phoneRegex = /^[+]?[\d\s\-()]{10,}$/;
    return phoneRegex.test(phone);
}

function highlightFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.add('is-invalid');

        // Add error message if not already exists
        if (!field.nextElementSibling || !field.nextElementSibling.classList.contains('invalid-feedback')) {
            const errorDiv = document.createElement('div');
            errorDiv.style.setProperty('color', 'red', 'important');
            errorDiv.className = 'invalid-feedback';
            errorDiv.textContent = message;
            field.parentNode.appendChild(errorDiv);
        }

        // Scroll to field
        field.scrollIntoView({behavior: 'smooth', block: 'center'});
    }
}

function highlightError(message, containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        // Create error alert
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-danger';
        alertDiv.textContent = message;

        // Prepend to container
        container.prepend(alertDiv);

        // Scroll to error
        alertDiv.scrollIntoView({behavior: 'smooth', block: 'center'});

        // Remove after 5 seconds
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }
}

function showValidationErrors(messages) {
    // Create error alert at the top of the form
    const errorAlert = document.createElement('div');
    errorAlert.className = 'alert alert-danger';
    errorAlert.innerHTML = '<strong>Please fix the following errors:</strong><ul>' +
        messages.map(msg => `<li>${msg}</li>`).join('') + '</ul>';

    // Add to the top of the form
    const formContainer = document.querySelector('.panel-body');
    formContainer.prepend(errorAlert);

    // Scroll to errors
    errorAlert.scrollIntoView({behavior: 'smooth', block: 'center'});

    // Remove after 5 seconds
    setTimeout(() => {
        errorAlert.remove();
    }, 5000);
}

function formatProgramName(programId) {
    const names = {
        'arts': 'General Arts',
        'science': 'General Science',
        'business': 'Business',
        'visual-arts': 'Visual Arts',
        'home-economics': 'Home Economics',
        'technical': 'Technical',
        'agriculture': 'Agriculture'
    };
    return names[programId] || programId;
}

// Clear validation errors
function clearValidationErrors() {
    // Remove field errors
    document.querySelectorAll('.is-invalid').forEach(el => {
        el.classList.remove('is-invalid');
    });

    // Remove error messages
    document.querySelectorAll('.invalid-feedback').forEach(el => {
        el.remove();
    });

    // Remove alert errors
    document.querySelectorAll('.alert-danger').forEach(el => {
        el.remove();
    });
}

// Add event listeners for real-time validation
document.addEventListener('DOMContentLoaded', function () {
    // Add input event listeners to clear validation on change
    const allInputs = document.querySelectorAll('input, select, textarea');
    allInputs.forEach(input => {
        input.addEventListener('input', function () {
            if (this.classList.contains('is-invalid')) {
                this.classList.remove('is-invalid');
                const errorMsg = this.nextElementSibling;
                if (errorMsg && errorMsg.classList.contains('invalid-feedback')) {
                    errorMsg.remove();
                }
            }
        });
    });

    // Validate on form submission
    const submitButton = document.getElementById('submitRequest');
    if (submitButton) {
        submitButton.addEventListener('click', function (e) {
            // Validate all steps before submission
            let allValid = true;
            const tabPanes = document.querySelectorAll('.tab-pane');

            for (let i = 1; i < tabPanes.length - 1; i++) { // Skip approval tab
                // Activate each tab and validate
                document.querySelectorAll('.wizardTabs')[i].click();
                if (!validateForm()) {
                    allValid = false;
                }
            }

            // If any step is invalid, prevent submission
            if (!allValid) {
                e.preventDefault();
                // Go back to first invalid step
                for (let i = 0; i < tabPanes.length; i++) {
                    document.querySelectorAll('.wizardTabs')[i].click();
                    if (!validateForm()) {
                        break;
                    }
                }
            }
        });
    }

});


/**
 * Optimizes an image file using canvas
 * @param {File} file - The original image file
 * @param {Object} options - Compression options
 * @returns {Promise<String>} - A promise that resolves with a Base64 data URI
 */
function optimizeImage(file, options) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        const reader = new FileReader();

        reader.onload = function (e) {
            img.src = e.target.result;
        };

        reader.onerror = reject;
        reader.readAsDataURL(file);

        img.onload = function () {
            // Calculate new dimensions while maintaining aspect ratio
            const maxDimension = options.maxSize;
            let width = img.width;
            let height = img.height;

            if (width > height) {
                if (width > maxDimension) {
                    height = Math.round((height * maxDimension) / width);
                    width = maxDimension;
                }
            } else {
                if (height > maxDimension) {
                    width = Math.round((width * maxDimension) / height);
                    height = maxDimension;
                }
            }

            // Create a canvas and draw the resized image on it
            const canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;

            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0, width, height);

            // Convert the canvas to a compressed Blob, then to a Data URL
            canvas.toBlob(
                (blob) => {
                    const newReader = new FileReader();
                    newReader.onload = () => resolve(newReader.result); // This is the Base64 string
                    newReader.readAsDataURL(blob);
                },
                options.outputFormat === 'png' ? 'image/png' : 'image/jpeg', // Mime type
                options.quality // Quality for JPEG
            );
        };

        img.onerror = reject;
    });
}


$(function () {

// Bind normal buttons
    $('.ladda-button').ladda('bind', {timeout: 2000});

// Bind progress buttons and simulate loading progress
    Ladda.bind('.progress-demo .ladda-button', {
        callback: function (instance) {
            var progress = 0;
            var interval = setInterval(function () {
                progress = Math.min(progress + Math.random() * 0.1, 1);
                instance.setProgress(progress);

                if (progress === 1) {
                    instance.stop();
                    clearInterval(interval);
                }
            }, 200);
        }
    });


    var l = $('.ladda-button-demo').ladda();

    l.click(function () {
        // Start loading
        l.ladda('start');

        // Timeout example
        // Do something in backend and then stop ladda
        setTimeout(function () {
            l.ladda('stop');
        }, 12000)


    });

});