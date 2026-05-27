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
let extractedFormList = null
let schools = null

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
    document.getElementById("dtime").innerHTML = datetime();
});


$("#submitRequest").click(async function () {
    var approve = $(".approveCheck").is(":checked");
    var c = validateForm();
    if (c === true) {
        var jso = await buildStudentPayload();
        var datastring = $("#simpleForm").serialize();

        let amount = 0;
        if (window.selectedForm) {
            amount = window.selectedForm.cost * 100;
        }

        function getFilledEmail() {
            const fatherEmail = document.getElementById('fatherEmail').value.trim();
            const motherEmail = document.getElementById('motherEmail').value.trim();
            return fatherEmail || motherEmail || "adminEmail@astromyllc.com";
        }

        function getStudentName() {
            const firstName = document.getElementById('studFName').value.trim();
            const lastName = document.getElementById('studSurName').value.trim();
            return `${firstName} ${lastName}`.trim() || "Student";
        }

        const institutionBeceCode = institution || (window.selectedForm ? window.selectedForm.schoolCode : "unknown");

        const handler = PaystackPop.setup({
            key: 'pk_test_957a501ee4935ea125978771108f5aba4ad53acf',
            email: getFilledEmail(),
            amount: amount || 500 * 100,
            currency: 'GHS',
            ref: 'APPLICANT_' + getStudentName().replace(/\s+/g, '_') + '_' + new Date().getTime(),
            metadata: {
                custom_fields: [{
                    display_name: getStudentName(),
                    variable_name: getStudentName(),
                    value: institutionBeceCode
                }]
            },
            onClose: function () {
            },
            callback: function (response) {
                swal({
                    title: "Payment completed",
                    text: 'Reference: ' + response.reference,
                    type: "success",
                });

                var header = $("meta[name='_csrf_header']").attr("content");
                var token = $("meta[name='_csrf']").attr("content");

                $.ajax({
                    headers: {
                        Accept: "application/json",
                        "Content-Type": "application/json",
                    },
                    url: "postedStudentApplication",
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
            }
        });
        handler.openIframe();
    }
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
        extractedFormList = extractAllSchoolsAdmissions(response);
        return formattedSchools;
    } catch (error) {
        console.error("Error fetching and formatting schools:", error);
        return [];
    }
}

function buildCards(allAdmissionsData, schoolId, containerId) {
    document.querySelector(".next").click();
    createFormTypeCardsForSchool(allAdmissionsData, schoolId, containerId);
}

function fetchStudent() {
    document.querySelector(".next").click();
}

//**************************************************************
// FIRST CONTAINER - School Selection with Search/Filter (WORKING)
//**************************************************************

document.addEventListener("DOMContentLoaded", async function () {
    const schoolsContainer = document.getElementById("schools-container");
    const searchInput = document.getElementById("search-input");
    const regionFilter = document.getElementById("region-filter");

    // Log for debugging
    console.log("School Container:", schoolsContainer);
    console.log("Search Input:", searchInput);
    console.log("Region Filter:", regionFilter);

    if (!schoolsContainer) {
        console.error("schools-container not found!");
        return;
    }

    let selectedSchool = null;

    // Fetch schools
    schools = await fetchAllInstitutions();
    console.log("Schools loaded:", schools ? schools.length : 0);

    function generateSchoolCards(schoolsArray) {
        if (!schoolsContainer) return;
        schoolsContainer.innerHTML = "";

        if (!schoolsArray || schoolsArray.length === 0) {
            schoolsContainer.innerHTML = `
                <div class="no-results">
                    <i class="fas fa-search fa-3x mb-3"></i>
                    <h4>No schools found</h4>
                    <p>Try adjusting your search or filters</p>
                </div>
            `;
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
                    <div class="school-programs">
                        ${programTags}
                    </div>
                </div>
            `;

            schoolCard.addEventListener("click", async function () {
                document.querySelectorAll("#schools-container .school-card")
                    .forEach((c) => c.classList.remove("selected"));
                this.classList.add("selected");

                const schoolId = this.dataset.id;
                selectedSchool = schools.find((school) => school.id == schoolId);
                if (selectedSchool) {
                    institution = selectedSchool.bececode;
                    await buildCards(extractedFormList, schoolId, 'availableForm-container');
                }
            });

            schoolsContainer.appendChild(schoolCard);
        });
    }

    // Initial rendering
    generateSchoolCards(schools);

    // Filter function - ONLY for first container
    function filterSchools() {
        if (!searchInput || !regionFilter) return;

        const searchTerm = searchInput.value.toLowerCase();
        const regionValue = regionFilter.value;

        const filteredSchools = schools.filter((school) => {
            const matchesSearch = searchTerm === "" ||
                school.name.toLowerCase().includes(searchTerm) ||
                school.location.toLowerCase().includes(searchTerm);

            const matchesRegion = regionValue === "" ||
                school.location.includes(regionValue);

            return matchesSearch && matchesRegion;
        });

        console.log("Filtered schools:", filteredSchools.length);
        generateSchoolCards(filteredSchools);
    }

    // Attach event listeners for filters
    if (searchInput) {
        searchInput.addEventListener("input", filterSchools);
        console.log("Search input listener attached");
    }
    if (regionFilter) {
        regionFilter.addEventListener("change", filterSchools);
        console.log("Region filter listener attached");
    }
});


//**************************************************************
// SECOND CONTAINER - Available Forms (NO search/filters)
//**************************************************************

document.addEventListener("DOMContentLoaded", async function () {
    const schoolsContainer = document.getElementById("availableForm-container");

    if (!schoolsContainer) {
        console.error("availableForm-container not found!");
        return;
    }

    let availableSchools = [];

    try {
        availableSchools = await fetchAllInstitutions();
        if (!availableSchools || !Array.isArray(availableSchools)) {
            availableSchools = [];
        }
    } catch (error) {
        console.error("Error fetching schools for forms:", error);
        availableSchools = [];
    }

    function generateSchoolCards(schoolsArray) {
        if (!schoolsArray || schoolsArray.length === 0) {
            schoolsContainer.innerHTML = `
                <div class="no-results">
                    <i class="fas fa-info-circle fa-3x mb-3"></i>
                    <h4>No Forms Available</h4>
                    <p>Select a school to view available admission forms.</p>
                </div>
            `;
            return;
        }

        schoolsContainer.innerHTML = "";

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
                    <div class="school-programs">
                        ${programTags}
                    </div>
                </div>
            `;

            schoolsContainer.appendChild(schoolCard);
        });

        const cards = document.querySelectorAll("#availableForm-container .school-card");
        cards.forEach((card) => {
            card.addEventListener("click", async function () {
                cards.forEach((c) => c.classList.remove("selected"));
                this.classList.add("selected");

                const schoolId = this.dataset.id;
                const selectedSchool = schoolsArray.find((school) => school.id == schoolId);
                if (selectedSchool) {
                    institution = selectedSchool.bececode;
                    await fetchStudent(selectedSchool.bececode, selectedSchool.name);
                }
            });
        });
    }

    if (availableSchools && availableSchools.length > 0) {
        generateSchoolCards(availableSchools);
    } else {
        schoolsContainer.innerHTML = `
            <div class="loading-state">
                <i class="fas fa-spinner fa-spin fa-3x mb-3"></i>
                <h4>Loading Available Forms...</h4>
                <p>Please wait while we load the available admission forms.</p>
            </div>
        `;
    }

    // NO filter event listeners here - they only work on the first container
});


/**
 * Makes a POST request using fetch with Bearer token.
 */
async function fetchPost(url, data) {
    const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;

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

        if (response.status === 404) {
            return null;
        }

        if (!response.ok) {
            let errorText;
            try {
                errorText = await response.text();
            } catch (e) {
                errorText = "No error message available";
            }
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

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
        throw new Error(error.message || "Network request failed");
    }
}


function extractAllSchoolsAdmissions(schoolsData) {
    const allAdmissionsData = {};

    schoolsData.forEach(school => {
        const schoolData = {
            schoolId: school.id,
            schoolName: school.name,
            schoolCode: school.bececode,
            region: school.region,
            city: school.city,
            crest: school.crest,
            slogan: school.slogan,
            hasAdmissions: false,
            message: "No admission forms available at this time",
            formTypes: []
        };

        if (school.admissions) {
            const admissions = school.admissions;
            schoolData.hasAdmissions = true;
            schoolData.admissionCriteria = admissions.admissionCriteriaList || [];

            if (admissions.applicationCategoryList && admissions.applicationCategoryList.length > 0) {
                schoolData.formTypes = admissions.applicationCategoryList.map(category => ({
                    id: category.id,
                    formType: category.applicationFormType,
                    cost: category.applicationFormAmount,
                    quantityAvailable: category.applicationFormQNT,
                    paymentMedium: getPaymentMediumText(category.paymentMedium),
                    commencementDate: category.commencement,
                    closureDate: category.closure,
                    appointmentPerDay: category.appointmentPerDay,
                    appointmentCommencement: category.appointmentCommencement,
                    appointmentClosure: category.appointmentClosure,
                    status: getFormStatus(category.commencement, category.closure),
                    schoolId: school.id,
                    schoolName: school.name,
                    schoolCode: school.bececode
                }));
            }
        }

        allAdmissionsData[school.id] = schoolData;
        allAdmissionsData[school.name] = schoolData;
        allAdmissionsData[school.bececode] = schoolData;
    });

    return allAdmissionsData;
}

function getPaymentMediumText(code) {
    const paymentMethods = {
        "1": "Cash Only",
        "2": "Mobile Money",
        "3": "Bank Transfer",
        "4": "All Payment Methods"
    };
    return paymentMethods[code] || "Not Specified";
}

function getFormStatus(commencement, closure) {
    const now = new Date();
    const startDate = new Date(commencement);
    const endDate = new Date(closure);

    now.setHours(0, 0, 0, 0);
    startDate.setHours(0, 0, 0, 0);
    endDate.setHours(0, 0, 0, 0);

    if (now < startDate) return "Coming Soon";
    if (now > endDate) return "Closed";
    return "Open";
}

function createFormTypeCardsForSchool(extractedFormList, schoolIdentifier, containerId) {
    const container = document.getElementById(containerId);

    if (!container) {
        console.error(`Container with id ${containerId} not found`);
        return;
    }

    container.innerHTML = '';

    const schoolData = extractedFormList[schoolIdentifier];

    if (!schoolData) {
        container.innerHTML = `
            <div class="no-school-message">
                <div class="empty-state">
                    <i class="fas fa-school fa-3x"></i>
                    <h3>School Not Found</h3>
                    <p>No data available for the selected school.</p>
                </div>
            </div>
        `;
        return;
    }

    const schoolHeader = document.createElement('div');
    schoolHeader.className = 'school-header';
    schoolHeader.innerHTML = `
        <div class="school-info">
            ${schoolData.crest ? `
                <img src="data:image/png;base64,${schoolData.crest}"
                     alt="${schoolData.schoolName} Crest" class="school-crest">
            ` : `
                <div class="school-crest-placeholder">
                    <i class="fas fa-school"></i>
                </div>
            `}
            <div class="school-details">
                <h2>${schoolData.schoolName}</h2>
                <p class="school-location">${schoolData.city}, ${schoolData.region}</p>
                ${schoolData.slogan ? `<p class="school-slogan">"${schoolData.slogan}"</p>` : ''}
            </div>
        </div>
    `;
    container.appendChild(schoolHeader);

    const formsSection = document.createElement('div');
    formsSection.className = 'forms-section schools-grid';

    if (!schoolData.hasAdmissions || schoolData.formTypes.length === 0) {
        formsSection.innerHTML = `
            <div class="no-forms-message">
                <div class="empty-state">
                    <i class="fas fa-clipboard-list fa-3x"></i>
                    <h3>No Admission Forms Available</h3>
                    <p>${schoolData.message}</p>
                </div>
            </div>
        `;
    } else {
        const availableForms = schoolData.formTypes.filter(form => form.status !== "Closed");

        if (availableForms.length === 0) {
            formsSection.innerHTML = `
                <div class="no-forms-message">
                    <div class="empty-state">
                        <i class="fas fa-info-circle fa-3x"></i>
                        <h3>No Available Forms</h3>
                        <p>All admission forms are currently closed. Please check back later.</p>
                    </div>
                </div>
            `;
        } else {
            availableForms.forEach(form => {
                const isClosed = form.status === "Closed";
                const isComingSoon = form.status === "Coming Soon";

                const card = document.createElement('div');
                let statusClass = '';
                if (isClosed) statusClass = 'form-closed';
                if (isComingSoon) statusClass = 'form-coming-soon';

                card.className = `form-type-card ${form.status.toLowerCase().replace(' ', '-')} ${statusClass}`;

                if (isClosed) {
                    card.style.cursor = 'not-allowed';
                    card.style.opacity = '0.6';
                } else {
                    card.style.cursor = 'pointer';
                }

                let startDateHtml = '';
                if (isComingSoon && form.commencementDate) {
                    startDateHtml = `
                        <div class="form-start-date">
                            <i class="fas fa-calendar-alt"></i>
                            <span>Starts: ${formatDate(form.commencementDate)}</span>
                        </div>
                    `;
                }

                card.innerHTML = `
                    <div class="form-header">
                        <h3 class="form-title">${form.formType}</h3>
                        <span class="form-status ${form.status.toLowerCase().replace(' ', '-')}">
                            ${form.status}
                        </span>
                    </div>
                    <div class="form-details">
                        <div class="form-cost">
                            <i class="fas fa-tag"></i>
                            <span class="cost-amount">GH₵ ${form.cost}</span>
                        </div>
                        <div class="form-payment">
                            <i class="fas fa-credit-card"></i>
                            <span>${form.paymentMedium}</span>
                        </div>
                        ${startDateHtml}
                        ${isClosed ? `
                            <div class="form-closed-message">
                                <i class="fas fa-ban"></i>
                                <span>Applications Closed</span>
                            </div>
                        ` : ''}
                        ${isComingSoon ? `
                            <div class="form-coming-soon-message">
                                <i class="fas fa-clock"></i>
                                <span>Coming Soon</span>
                            </div>
                        ` : ''}
                    </div>
                `;

                card.dataset.formCost = form.cost;
                card.dataset.formType = form.formType;
                card.dataset.formStatus = form.status;
                card.dataset.startDate = form.commencementDate;

                if (!isClosed) {
                    card.addEventListener('click', function () {
                        document.querySelectorAll('.form-type-card').forEach(c => c.classList.remove('selected'));
                        this.classList.add('selected');

                        window.selectedForm = {
                            cost: form.cost,
                            type: form.formType,
                            applicationType: form.formType,
                            schoolCode: form.schoolCode,
                            status: form.status,
                            startDate: form.commencementDate
                        };

                        if (form.status === "Open") {
                            fetchStudent();
                        } else if (form.status === "Coming Soon") {
                            showNotification(`This form will be available starting ${formatDate(form.commencementDate)}. Please check back later.`, "info");
                        }
                    });
                } else {
                    card.title = "Applications are closed for this form";
                    card.addEventListener('click', function (e) {
                        e.stopPropagation();
                        showNotification("Applications are closed for this admission form.", "warning");
                    });
                }

                formsSection.appendChild(card);
            });
        }
    }

    container.appendChild(formsSection);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-GB', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
    });
}

function addFormCardEventListeners() {
    document.querySelectorAll('.apply-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const formType = this.getAttribute('data-form-type');
            const schoolId = this.getAttribute('data-school-id');
            const cost = this.getAttribute('data-cost');
            handleFormApplication(formType, schoolId, cost);
        });
    });

    document.querySelectorAll('.details-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const formType = this.getAttribute('data-form-type');
            const schoolId = this.getAttribute('data-school-id');
            showFormDetails(formType, schoolId);
        });
    });
}

function handleFormApplication(formType, schoolId, cost) {
    console.log(`Applying for ${formType} at school ${schoolId}, Cost: GH₵${cost}`);
    swal({
        title: "Starting application",
        text: `Starting application for ${formType} - GH₵${cost}`,
        type: "info",
    });
}

function showFormDetails(formType, schoolId) {
    console.log(`Showing details for ${formType} at school ${schoolId}`);
    swal({
        title: "Form Details",
        text: `Details for ${formType}`,
        type: "info",
    });
}

function formatSchoolsData(originalData) {
    if (!originalData || !Array.isArray(originalData)) {
        return [];
    }

    return originalData.map((school) => {
        const programs = [
            ...new Set(
                school.classList.map((cls) => {
                    const classGroup = cls.classGroup;
                    const programMap = {
                        1: "General Science",
                        2: "General Arts",
                        3: "Business",
                        4: "Visual Arts",
                        5: "Home Economics",
                        6: "Agriculture",
                        7: "Technical",
                    };
                    return programMap[classGroup] || `Program ${classGroup}`;
                })
            ),
        ];

        const schoolName = school.name.toLowerCase();
        let type = "Public • Mixed";

        if (
            schoolName.includes("girls") ||
            schoolName.includes("girls'") ||
            schoolName.includes("female")
        ) {
            type = "Public • Girls";
        } else if (
            schoolName.includes("boys") ||
            schoolName.includes("boys'") ||
            schoolName.includes("male")
        ) {
            type = "Public • Boys";
        }

        const location = `${school.city}, ${school.region}`;
        const bececode = `${school.bececode}`

        let logo = "https://via.placeholder.com/100x100/3498db/ffffff?text=" +
            encodeURIComponent(school.name.charAt(0));
        if (school.crest) {
            logo = `data:image/png;base64,${school.crest}`;
        }

        const colorPalette = [
            "#3498db", "#2ecc71", "#9b59b6", "#e74c3c", "#f39c12",
            "#1abc9c", "#d35400", "#27ae60", "#8e44ad", "#c0392b",
            "#16a085", "#2980b9", "#f1c40f", "#e67e22", "#2c3e50"
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

    document.getElementById("studFName").value = studentData.firstName.trim();
    document.getElementById("studSurName").value = studentData.lastName.trim();
    document.getElementById("studOtherName").value = studentData.otherName.trim();

    if (studentData.gender) {
        const genderSelect = document.getElementById("studGender");
        const options = genderSelect.options;
        for (let i = 0; i < options.length; i++) {
            if (options[i].text.toLowerCase().includes(studentData.gender.toLowerCase())) {
                genderSelect.selectedIndex = i;
                break;
            }
        }
    }

    document.getElementById("studDOB").value = studentData.dateOfBirth.trim();
    document.getElementById("studDOA").value = studentData.dateOfAdmission.trim();
    document.getElementById("placeOfBirth").value = studentData.placeOfBirth.trim();
    document.getElementById("studResidence").value = studentData.residentialLocality.trim();

    if (studentData.countryOfBirth) {
        const countrySelect = document.getElementById("cob");
        const options = countrySelect.options;
        for (let i = 0; i < options.length; i++) {
            if (options[i].text.toLowerCase().includes(studentData.countryOfBirth.toLowerCase())) {
                countrySelect.selectedIndex = i;
                break;
            }
        }
    }

    document.getElementById("denomination").value = studentData.denomination.trim();

    if (studentData.studentParents && studentData.studentParents.length > 0) {
        const parents = studentData.studentParents;
        const father = parents.find((p) => p.contact1 && p.contact1.trim()) || parents[0];
        if (father) {
            document.getElementById("fatherFirstName").value = father.firstNames.trim();
            document.getElementById("fatherLastName").value = father.lastName.trim();
            document.getElementById("fatherEmail").value = father.email.trim();
            document.getElementById("fatherContact1").value = father.contact1.trim();
            document.getElementById("fatherContact2").value = father.contact2.trim();
            document.getElementById("fatherOccupation").value = father.occupation.trim();
            document.getElementById("fatherPlaceOfWork").value = father.placeOfWork.trim();

            if (father.parentType) {
                const fatherTypeSelect = document.getElementById("fatherType");
                const options = fatherTypeSelect.options;
                for (let i = 0; i < options.length; i++) {
                    if (options[i].text.toLowerCase().includes(father.parentType.toLowerCase())) {
                        fatherTypeSelect.selectedIndex = i;
                        break;
                    }
                }
            }
        }

        const mother = parents.find((p) => p !== father) || parents[1] || parents[0];
        if (mother && mother !== father) {
            document.getElementById("motherFirstName").value = mother.firstNames.trim();
            document.getElementById("motherLastName").value = mother.lastName.trim();
            document.getElementById("motherEmail").value = mother.email.trim();
            document.getElementById("motherContact1").value = mother.contact1.trim();
            document.getElementById("motherContact2").value = mother.contact2.trim();
            document.getElementById("motherOccupation").value = mother.occupation.trim();
            document.getElementById("motherPlaceOfWork").value = mother.placeOfWork.trim();

            if (mother.parentType) {
                const motherTypeSelect = document.getElementById("motherType");
                const options = motherTypeSelect.options;
                for (let i = 0; i < options.length; i++) {
                    if (options[i].text.toLowerCase().includes(mother.parentType.toLowerCase())) {
                        motherTypeSelect.selectedIndex = i;
                        break;
                    }
                }
            }
        }
    }

    if (studentData.picture) {
        displayStudentPicture(studentData.picture);
    }

    console.log("Student data populated successfully");
}

function selectSubjectsBasedOnData(subjectsData, programType) {
    let subjectCheckboxes;
    const subjectSelector = `.${programType}-subject`;
    subjectCheckboxes = document.querySelectorAll(subjectSelector);

    subjectCheckboxes.forEach(checkbox => {
        checkbox.checked = false;
    });

    subjectsData.forEach(subject => {
        const subjectName = subject.subjectName
            .replace(/\s+/g, ' ')
            .replace(/\n/g, ' ')
            .trim()
            .toLowerCase();

        const matchingCheckbox = Array.from(subjectCheckboxes).find(checkbox => {
            const parentDiv = checkbox.closest('.subject-item');
            if (parentDiv) {
                const label = parentDiv.querySelector('label');
                if (label) {
                    const labelText = label.textContent
                        .replace(/\s+/g, ' ')
                        .replace(/\n/g, ' ')
                        .trim()
                        .toLowerCase();
                    return labelText === subjectName;
                }
            }
            return false;
        });

        if (matchingCheckbox) {
            matchingCheckbox.checked = true;
        }
    });
}

function displayStudentPicture(base64Data) {
    try {
        const imageUrl = `data:image/png;base64,${base64Data}`;
        const imgElement = document.getElementById('studentPicture');
        const imageOutput = document.querySelector('.imageOutput');

        if (imgElement) {
            imgElement.src = imageUrl;
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
    const payload = {
        studentId: document.getElementById('studentID')?.value.trim() || currentStudentData?.studentId || "",
        firstName: document.getElementById('studFName').value.trim(),
        otherName: document.getElementById('studOtherName').value.trim(),
        lastName: document.getElementById('studSurName').value.trim(),
        placeOfBirth: document.getElementById('placeOfBirth').value.trim(),
        gender: document.getElementById('studGender').value,
        countryOfBirth: document.getElementById('cob').value,
        nationality: document.getElementById('cob').value,
        denomination: document.getElementById('denomination').value.trim(),
        institutionCode: institution || currentStudentData?.institutionCode || "",
        applicationType: window.selectedForm?.type || null,
        residentialLocality: document.getElementById('studResidence').value.trim(),
        status: "APPLIED",
        studentClass: determineStudentClass(),
        dateOfBirth: formatDateForJava(document.getElementById('studDOB').value),
        dateOfAdmission: formatDateForJava(document.getElementById('studDOA').value),
        picture: null,
        birthCert: pdfAttachmentData?.base64Data || null,
        applicantBirthCert: pdfAttachmentData?.fileName || null,
        applicantBirthCertFileType: pdfAttachmentData?.fileType || null,
        studentParents: [],
        studentSubjectsList: []
    };

    const pictureInput = document.querySelector('.imageInput');
    if (pictureInput && pictureInput.files.length > 0) {
        try {
            const file = pictureInput.files[0];
            const optimizedDataURL = await optimizeImage(file, {
                maxSize: 400,
                quality: 0.7,
                outputFormat: 'jpeg'
            });
            payload.picture = optimizedDataURL.split(',')[1];
        } catch (error) {
            console.error("Image optimization failed", error);
            const reader = new FileReader();
            const dataUrl = await new Promise((resolve) => {
                reader.onload = () => resolve(reader.result);
                reader.readAsDataURL(pictureInput.files[0]);
            });
            payload.picture = dataUrl.split(',')[1];
        }
    } else if (currentStudentData?.picture) {
        payload.picture = currentStudentData.picture;
    }

    payload.studentParents = buildParentsData(payload.studentId);
    payload.studentSubjectsList = buildSubjectsListData();

    Object.keys(payload).forEach(key => {
        if (payload[key] === undefined || payload[key] === null) {
            delete payload[key];
        }
    });

    console.log("Final payload matching Students2Request:", payload);
    return payload;
}

function formatDateForJava(dateString) {
    if (!dateString) return null;
    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return null;
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    } catch (error) {
        console.error("Error formatting date:", error);
        return null;
    }
}

function determineStudentClass() {
    if (window.selectedForm?.type) {
        return window.selectedForm.type;
    }

    const selectedSubjects = document.querySelectorAll('input[type="checkbox"]:checked');
    if (selectedSubjects.length > 0) {
        const firstSubject = selectedSubjects[0];
        if (firstSubject.className.includes('science-subject')) return "Science";
        if (firstSubject.className.includes('arts-subject')) return "Arts";
        if (firstSubject.className.includes('business-subject')) return "Business";
    }

    return "General";
}

function buildParentsData(studentId) {
    const parents = [];

    const fatherFirstName = document.getElementById('fatherFirstName').value.trim();
    const fatherLastName = document.getElementById('fatherLastName').value.trim();

    if (fatherFirstName || fatherLastName) {
        const fatherData = {
            firstNames: fatherFirstName,
            lastName: fatherLastName,
            email: document.getElementById('fatherEmail').value.trim(),
            contact1: document.getElementById('fatherContact1').value.trim(),
            contact2: document.getElementById('fatherContact2').value.trim(),
            occupation: document.getElementById('fatherOccupation').value.trim(),
            placeOfWork: document.getElementById('fatherPlaceOfWork').value.trim(),
            parentType: document.getElementById('fatherType').value,
            relationship: "FATHER",
            institutionCode: institution,
            studentId: studentId
        };

        Object.keys(fatherData).forEach(key => {
            if (!fatherData[key]) delete fatherData[key];
        });

        if (Object.keys(fatherData).length > 3) {
            parents.push(fatherData);
        }
    }

    const motherFirstName = document.getElementById('motherFirstName').value.trim();
    const motherLastName = document.getElementById('motherLastName').value.trim();

    if (motherFirstName || motherLastName) {
        const motherData = {
            firstNames: motherFirstName,
            lastName: motherLastName,
            email: document.getElementById('motherEmail').value.trim(),
            contact1: document.getElementById('motherContact1').value.trim(),
            contact2: document.getElementById('motherContact2').value.trim(),
            occupation: document.getElementById('motherOccupation').value.trim(),
            placeOfWork: document.getElementById('motherPlaceOfWork').value.trim(),
            parentType: document.getElementById('motherType').value,
            relationship: "MOTHER",
            institutionCode: institution,
            studentId: studentId
        };

        Object.keys(motherData).forEach(key => {
            if (!motherData[key]) delete motherData[key];
        });

        if (Object.keys(motherData).length > 3) {
            parents.push(motherData);
        }
    }

    return parents;
}

function buildSubjectsListData() {
    const subjectsList = [];
    const checkedSubjects = document.querySelectorAll('input[type="checkbox"]:checked');

    checkedSubjects.forEach(checkbox => {
        const subjectName = checkbox.nextElementSibling?.textContent.trim() ||
            checkbox.closest('.subject-item')?.querySelector('label')?.textContent.trim() ||
            `Subject_${checkbox.value || checkbox.name}`;

        let subjectClass = "CORE";
        if (checkbox.className.includes('elective')) subjectClass = "ELECTIVE";

        const subjectData = {
            subjectName: subjectName,
            subjectClass: subjectClass,
            institutionCode: institution,
            studentId: document.getElementById('studentID')?.value.trim() || ""
        };

        Object.keys(subjectData).forEach(key => {
            if (!subjectData[key]) delete subjectData[key];
        });

        subjectsList.push(subjectData);
    });

    return subjectsList;
}

function mergeParentsData(existingParents, uiParents) {
    if (!existingParents || existingParents.length === 0) {
        return uiParents;
    }

    if (!uiParents || uiParents.length === 0) {
        return existingParents;
    }

    const existingParentsMap = {};
    existingParents.forEach(parent => {
        if (parent.parentType) {
            existingParentsMap[parent.parentType.toLowerCase()] = parent;
        }
    });

    const mergedParents = uiParents.map(uiParent => {
        const parentType = uiParent.parentType?.toLowerCase() || '';
        if (existingParentsMap[parentType]) {
            return {...existingParentsMap[parentType], ...uiParent};
        }
        return uiParent;
    });

    Object.values(existingParentsMap).forEach(existingParent => {
        const parentType = existingParent.parentType?.toLowerCase() || '';
        const existsInUI = uiParents.some(uiParent =>
            (uiParent.parentType?.toLowerCase() || '') === parentType
        );
        if (!existsInUI) {
            mergedParents.push(existingParent);
        }
    });

    return mergedParents;
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
    const {institutionCode, studentId, ...rest} = parentData;
    return Object.values(rest).some(value => value && value.trim() !== '');
}

function buildSubjectsData() {
    const subjects = [];
    const checkedSubjects = document.querySelectorAll('input[type="checkbox"]:checked');

    checkedSubjects.forEach(checkbox => {
        const subjectName = checkbox.nextElementSibling.textContent.trim();
        subjects.push({subjectName: subjectName});
    });

    return subjects;
}

//============================= VALIDATION ==============================

function validateForm() {
    let isValid = true;
    let errorMessages = [];

    const activeTab = document.querySelector('.tab-pane.active');
    const tabIndex = Array.from(document.querySelectorAll('.tab-pane')).indexOf(activeTab);

    switch (tabIndex) {
        case 0:
            if (!validateSchoolSelection()) {
                isValid = false;
                errorMessages.push("Please select a school and provide a Student ID");
            }
            break;
        case 1:
            break;
        case 2:
            if (!validateStudentBioData()) {
                isValid = false;
                errorMessages.push("Please complete all required student information");
            }
            break;
        case 3:
            if (!validateParentData()) {
                isValid = false;
                errorMessages.push("Please complete all required parent information");
            }
            break;
        case 4:
            break;
    }

    if (!isValid) {
        showValidationErrors(errorMessages);
    }

    return isValid;
}

function validateSchoolSelection() {
    let isValid = true;
    const selectedSchool = document.querySelector('.school-card.selected');
    if (!selectedSchool) {
        isValid = false;
        highlightError('Please select a school', 'schools-container');
    }
    return isValid;
}

function validateStudentBioData() {
    let isValid = true;

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

    const dob = document.getElementById('studDOB').value;
    const doa = document.getElementById('studDOA').value;

    if (dob) {
        const dobDate = new Date(dob);
        const minAgeDate = new Date();
        minAgeDate.setFullYear(minAgeDate.getFullYear() - 4);
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

    const pictureInput = document.querySelector('.imageInput');
    if (pictureInput.files.length > 0) {
        const file = pictureInput.files[0];
        const validTypes = ['image/jpeg', 'image/png', 'image/jpg'];
        const maxSize = 2 * 1024 * 1024;
        if (!validTypes.includes(file.type)) {
            isValid = false;
            highlightError('Please upload a valid image (JPEG, PNG, JPG)', 'studPic');
        }
        if (file.size > maxSize) {
            isValid = false;
            highlightError('Image size must be less than 2MB', 'studPic');
        }
    }

    if (pdfAttachmentData) {
        const maxSize = 5 * 1024 * 1024;
        if (pdfAttachmentData.fileSize > maxSize) {
            isValid = false;
            highlightError('PDF file size must be less than 5MB', 'pdfAttachment');
        }
        if (pdfAttachmentData.fileType !== 'application/pdf') {
            isValid = false;
            highlightError('Only PDF files are allowed', 'pdfAttachment');
        }
    }

    return isValid;
}

function validateParentData() {
    let isValid = true;
    let hasAtLeastOneParent = false;

    const fatherFirstName = document.getElementById('fatherFirstName').value.trim();
    const fatherLastName = document.getElementById('fatherLastName').value.trim();

    if (fatherFirstName || fatherLastName) {
        hasAtLeastOneParent = true;
        const fatherRequired = ['fatherFirstName', 'fatherLastName', 'fatherEmail', 'fatherContact1', 'fatherOccupation', 'fatherPlaceOfWork', 'fatherType'];
        fatherRequired.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (!field.value.trim()) {
                isValid = false;
                highlightFieldError(fieldId, `${field.labels[0].textContent} is required for father`);
            }
        });

        const fatherEmail = document.getElementById('fatherEmail').value.trim();
        if (fatherEmail && !isValidEmail(fatherEmail)) {
            isValid = false;
            highlightFieldError('fatherEmail', 'Please enter a valid email address for father');
        }

        const fatherContact = document.getElementById('fatherContact1').value.trim();
        if (fatherContact && !isValidPhone(fatherContact)) {
            isValid = false;
            highlightFieldError('fatherContact1', 'Please enter a valid phone number for father');
        }
    }

    const motherFirstName = document.getElementById('motherFirstName').value.trim();
    const motherLastName = document.getElementById('motherLastName').value.trim();

    if (motherFirstName || motherLastName) {
        hasAtLeastOneParent = true;
        const motherRequired = ['motherFirstName', 'motherLastName', 'motherEmail', 'motherContact1', 'motherOccupation', 'motherPlaceOfWork', 'motherType'];
        motherRequired.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (!field.value.trim()) {
                isValid = false;
                highlightFieldError(fieldId, `${field.labels[0].textContent} is required for mother`);
            }
        });

        const motherEmail = document.getElementById('motherEmail').value.trim();
        if (motherEmail && !isValidEmail(motherEmail)) {
            isValid = false;
            highlightFieldError('motherEmail', 'Please enter a valid email address for mother');
        }

        const motherContact = document.getElementById('motherContact1').value.trim();
        if (motherContact && !isValidPhone(motherContact)) {
            isValid = false;
            highlightFieldError('motherContact1', 'Please enter a valid phone number for mother');
        }
    }

    if (!hasAtLeastOneParent) {
        isValid = false;
        highlightError('Please provide information for at least one parent', 'parentsDetailForm');
    }

    return isValid;
}

function validateSubjectSelection() {
    let isValid = true;
    let selectedProgram = null;
    let selectedSubjects = 0;

    const programCards = document.querySelectorAll('.program-card');
    programCards.forEach(card => {
        const subjectCheckboxes = card.querySelectorAll('input[type="checkbox"]:checked');
        if (subjectCheckboxes.length > 0) {
            selectedProgram = card.id.replace('-card', '');
            selectedSubjects = subjectCheckboxes.length;
        }
    });

    if (!selectedProgram) {
        isValid = false;
        highlightError('Please select at least one subject from a program', 'subject-selection-container');
        return isValid;
    }

    if (selectedSubjects !== 4) {
        isValid = false;
        highlightError(`Please select exactly 4 subjects from the ${formatProgramName(selectedProgram)} program`, selectedProgram + '-card');
    }

    return isValid;
}

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
        if (!field.nextElementSibling || !field.nextElementSibling.classList.contains('invalid-feedback')) {
            const errorDiv = document.createElement('div');
            errorDiv.style.setProperty('color', 'red', 'important');
            errorDiv.className = 'invalid-feedback';
            errorDiv.textContent = message;
            field.parentNode.appendChild(errorDiv);
        }
        field.scrollIntoView({behavior: 'smooth', block: 'center'});
    }
}

function highlightError(message, containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-danger';
        alertDiv.textContent = message;
        container.prepend(alertDiv);
        alertDiv.scrollIntoView({behavior: 'smooth', block: 'center'});
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }
}

function showValidationErrors(messages) {
    const errorAlert = document.createElement('div');
    errorAlert.className = 'alert alert-danger';
    errorAlert.innerHTML = '<strong>Please fix the following errors:</strong><ul>' +
        messages.map(msg => `<li>${msg}</li>`).join('') + '</ul>';
    const formContainer = document.querySelector('.panel-body');
    formContainer.prepend(errorAlert);
    errorAlert.scrollIntoView({behavior: 'smooth', block: 'center'});
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

function clearValidationErrors() {
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());
    document.querySelectorAll('.alert-danger').forEach(el => el.remove());
}

document.addEventListener('DOMContentLoaded', function () {
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
});

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

            const canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0, width, height);

            canvas.toBlob(
                (blob) => {
                    const newReader = new FileReader();
                    newReader.onload = () => resolve(newReader.result);
                    newReader.readAsDataURL(blob);
                },
                options.outputFormat === 'png' ? 'image/png' : 'image/jpeg',
                options.quality
            );
        };
        img.onerror = reject;
    });
}

$(function () {
    $('.ladda-button').ladda('bind', {timeout: 2000});
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
        l.ladda('start');
        setTimeout(function () {
            l.ladda('stop');
        }, 12000);
    });
});

let pdfAttachmentData = null;

document.addEventListener("DOMContentLoaded", function () {
    initializePdfUpload();
});

function initializePdfUpload() {
    const pdfInput = document.getElementById('pdfAttachment');
    const pdfUploadBtn = document.getElementById('pdfUploadBtn');
    const removePdfBtn = document.getElementById('removePdfBtn');

    if (pdfUploadBtn && pdfInput) {
        pdfUploadBtn.addEventListener('click', function () {
            pdfInput.click();
        });
    }

    if (pdfInput) {
        pdfInput.addEventListener('change', handlePdfUpload);
    }

    if (removePdfBtn) {
        removePdfBtn.addEventListener('click', removePdf);
    }
}

function handlePdfUpload(event) {
    const file = event.target.files[0];
    const pdfFileName = document.getElementById('pdfFileName');
    const pdfPreview = document.getElementById('pdfPreview');
    const pdfPreviewName = document.getElementById('pdfPreviewName');
    const pdfPreviewSize = document.getElementById('pdfPreviewSize');

    if (file) {
        if (file.type !== 'application/pdf') {
            alert('Please select a PDF file only.');
            resetPdfUpload();
            return;
        }

        const maxSize = 5 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('PDF file size must be less than 5MB.');
            resetPdfUpload();
            return;
        }

        pdfFileName.textContent = file.name;
        pdfPreviewName.textContent = file.name;
        pdfPreviewSize.textContent = formatFileSize(file.size);
        pdfPreview.style.display = 'block';

        const reader = new FileReader();
        reader.onload = function (e) {
            pdfAttachmentData = {
                fileName: file.name,
                fileSize: file.size,
                fileType: file.type,
                base64Data: e.target.result.split(',')[1],
                uploadDate: new Date().toISOString()
            };
            console.log('PDF attached:', pdfAttachmentData.fileName);
        };
        reader.onerror = function () {
            alert('Error reading PDF file. Please try again.');
            resetPdfUpload();
        };
        reader.readAsDataURL(file);
    }
}

function removePdf() {
    resetPdfUpload();
}

function resetPdfUpload() {
    const pdfInput = document.getElementById('pdfAttachment');
    const pdfFileName = document.getElementById('pdfFileName');
    const pdfPreview = document.getElementById('pdfPreview');

    if (pdfInput) pdfInput.value = '';
    if (pdfFileName) pdfFileName.textContent = 'No file chosen';
    if (pdfPreview) pdfPreview.style.display = 'none';
    pdfAttachmentData = null;
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function showNotification(message, type = "info") {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas ${getNotificationIcon(type)}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close">&times;</button>
    `;

    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: bold;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        animation: slideIn 0.3s ease-out;
        display: flex;
        align-items: center;
        justify-content: space-between;
        min-width: 300px;
    `;

    const colors = {
        info: '#17a2b8',
        warning: '#ffc107',
        error: '#dc3545',
        success: '#28a745'
    };
    notification.style.backgroundColor = colors[type] || colors.info;
    notification.style.color = type === 'warning' ? '#333' : 'white';

    document.body.appendChild(notification);

    const closeBtn = notification.querySelector('.notification-close');
    closeBtn.style.cssText = `
        background: none;
        border: none;
        color: inherit;
        font-size: 20px;
        cursor: pointer;
        margin-left: 15px;
        padding: 0 5px;
    `;
    closeBtn.onclick = () => notification.remove();

    setTimeout(() => {
        if (notification && notification.remove) {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => notification.remove(), 300);
        }
    }, 5000);
}

function getNotificationIcon(type) {
    const icons = {
        info: 'fa-info-circle',
        warning: 'fa-exclamation-triangle',
        error: 'fa-times-circle',
        success: 'fa-check-circle'
    };
    return icons[type] || 'fa-info-circle';
}

const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);

function safelyGenerateCards(schoolsArray, container, emptyMessage = "No items found") {
    if (!container) {
        console.error("Container element not found");
        return false;
    }

    if (!schoolsArray || !Array.isArray(schoolsArray)) {
        console.error("Invalid schoolsArray provided:", schoolsArray);
        container.innerHTML = `
            <div class="error-state">
                <i class="fas fa-exclamation-triangle fa-3x mb-3"></i>
                <h4>Error Loading Data</h4>
                <p>Unable to load data. Please refresh the page or try again later.</p>
            </div>
        `;
        return false;
    }

    if (schoolsArray.length === 0) {
        container.innerHTML = `
            <div class="no-results">
                <i class="fas fa-search fa-3x mb-3"></i>
                <h4>${emptyMessage}</h4>
                <p>Try adjusting your search or filters</p>
            </div>
        `;
        return false;
    }

    return true;
}