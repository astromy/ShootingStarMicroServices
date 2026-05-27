window.copyrights();

var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");

$(function () {
    // Global variables
    let allApplications = [];
    let filteredApplications = [];

    // Load applications when DOM is ready
    loadApplications();

    // Event listeners
    $('#refreshApplicationsBtn').click(() => loadApplications());
    $('#exportApplicationsBtn').click(() => exportToExcel());
    $('#yearFilter').change(() => filterAndDisplayApplications());
    $('#applicationTypeFilter').change(() => filterAndDisplayApplications());
    $('#statusFilter').change(() => filterAndDisplayApplications());
    $('#searchBtn').click(() => filterAndDisplayApplications());
    $('#searchInput').on('keypress', function (e) {
        if (e.which === 13) filterAndDisplayApplications();
    });

    // ============================================================
    // Data Loading Functions
    // ============================================================

    async function loadApplications() {
        showLoading();
        try {
            var instRequest = {val: v};
            const response = await fetchPost("/getAllApplicants", instRequest);
            if (response && response.length > 0) {
                allApplications = response;
                updateStatistics();
                populateFilters();
                filterAndDisplayApplications();
            } else {
                showEmptyState();
            }
        } catch (error) {
            console.error("Error loading applications:", error);
            showError("Failed to load applications. Please try again.");
        }
        hideLoading();
    }

    // ============================================================
    // Statistics Functions
    // ============================================================

    function updateStatistics() {
        const total = allApplications.length;
        const pending = allApplications.filter(app => app.applicationStatus === 'PENDING').length;
        const flagged = allApplications.filter(app => app.applicationStatus === 'FLAGGED' || isFlagged(app)).length;
        const approved = allApplications.filter(app => app.applicationStatus === 'APPROVED').length;

        $('#totalCount').text(total);
        $('#pendingCount').text(pending);
        $('#flaggedCount').text(flagged);
        $('#approvedCount').text(approved);
    }

    function isFlagged(application) {
        const missingDocuments = !application.applicantBirthCert;
        const incompleteInfo = !application.applicantFirstName || !application.applicantLastName;
        return missingDocuments || incompleteInfo;
    }

    // ============================================================
    // Filter Functions
    // ============================================================

    function populateFilters() {
        // Populate years
        const years = [...new Set(allApplications.map(app =>
            app.applicationDate ? new Date(app.applicationDate).getFullYear() : new Date().getFullYear()
        ))];
        years.sort().reverse();
        const yearSelect = $('#yearFilter');
        yearSelect.empty().append('<option value="all">All Years</option>');
        years.forEach(year => {
            yearSelect.append(`<option value="${year}">${year}</option>`);
        });

        // Populate application types
        const types = [...new Set(allApplications.map(app => app.applicationType).filter(t => t))];
        const typeSelect = $('#applicationTypeFilter');
        typeSelect.empty().append('<option value="all">All Application Types</option>');
        types.forEach(type => {
            typeSelect.append(`<option value="${type}">${type}</option>`);
        });
    }

    function filterAndDisplayApplications() {
        const year = $('#yearFilter').val();
        const type = $('#applicationTypeFilter').val();
        const status = $('#statusFilter').val();
        const searchTerm = $('#searchInput').val().toLowerCase();

        filteredApplications = allApplications.filter(app => {
            // Year filter
            if (year !== 'all') {
                const appYear = app.applicationDate ? new Date(app.applicationDate).getFullYear() : null;
                if (appYear != year) return false;
            }

            // Type filter
            if (type !== 'all' && app.applicationType !== type) return false;

            // Status filter
            if (status !== 'all') {
                if (status === 'FLAGGED') {
                    if (!isFlagged(app) && app.applicationStatus !== 'FLAGGED') return false;
                } else if (app.applicationStatus !== status) return false;
            }

            // Search filter
            if (searchTerm) {
                const fullName = `${app.applicantFirstName} ${app.applicantLastName}`.toLowerCase();
                const applicationCode = (app.applicationCode || '').toLowerCase();
                if (!fullName.includes(searchTerm) && !applicationCode.includes(searchTerm)) return false;
            }

            return true;
        });

        displayGroupedApplications();
    }

    // ============================================================
    // Display Functions
    // ============================================================

    function displayGroupedApplications() {
        const container = $('#applicationsContainer');
        container.empty();

        if (filteredApplications.length === 0) {
            container.html(`
                <div class="text-center p-5">
                    <i class="fas fa-inbox fa-4x text-muted mb-3"></i>
                    <h4>No Applications Found</h4>
                    <p>Try adjusting your filters or refresh the page.</p>
                </div>
            `);
            return;
        }

        // Group by year first
        const groupedByYear = {};
        filteredApplications.forEach(app => {
            const year = app.applicationDate ? new Date(app.applicationDate).getFullYear() : 'Unknown';
            if (!groupedByYear[year]) groupedByYear[year] = [];
            groupedByYear[year].push(app);
        });

        // Sort years descending
        const sortedYears = Object.keys(groupedByYear).sort((a, b) => b - a);

        // Render each year group
        sortedYears.forEach(year => {
            const yearGroup = groupedByYear[year];

            // Group by application type within year
            const groupedByType = {};
            yearGroup.forEach(app => {
                const type = app.applicationType || 'General';
                if (!groupedByType[type]) groupedByType[type] = [];
                groupedByType[type].push(app);
            });

            // Create year section
            const yearSection = $(`
                <div class="year-section mb-4">
                    <div class="year-header">
                        <h3><i class="fas fa-calendar-alt"></i> ${year}</h3>
                        <span class="badge badge-primary">${yearGroup.length} Applications</span>
                    </div>
                    <div class="type-groups"></div>
                </div>
            `);

            // Render each type group
            const typeGroupsContainer = yearSection.find('.type-groups');
            Object.keys(groupedByType).forEach(type => {
                const typeApplications = groupedByType[type];
                const flaggedApps = typeApplications.filter(app => isFlagged(app) || app.applicationStatus === 'FLAGGED');
                const normalApps = typeApplications.filter(app => !isFlagged(app) && app.applicationStatus !== 'FLAGGED');

                const typeCard = $(`
                    <div class="type-card mb-3">
                        <div class="type-header">
                            <h4><i class="fas fa-folder-open"></i> ${type}</h4>
                            <span class="badge badge-info">${typeApplications.length} Applications</span>
                            ${flaggedApps.length > 0 ? `<span class="badge badge-warning ml-2">${flaggedApps.length} Flagged</span>` : ''}
                        </div>
                        <div class="applications-grid"></div>
                    </div>
                `);

                const gridContainer = typeCard.find('.applications-grid');

                // Display flagged applications first
                if (flaggedApps.length > 0) {
                    const flaggedSection = $(`
                        <div class="flagged-section mb-3">
                            <div class="flagged-header">
                                <i class="fas fa-flag text-warning"></i> Flagged Applications
                            </div>
                            <div class="row flagged-grid"></div>
                        </div>
                    `);
                    const flaggedGrid = flaggedSection.find('.flagged-grid');
                    flaggedApps.forEach(app => {
                        flaggedGrid.append(createApplicationCard(app, true));
                    });
                    gridContainer.append(flaggedSection);
                }

                // Display normal applications
                const normalRow = $('<div class="row"></div>');
                normalApps.forEach(app => {
                    normalRow.append(createApplicationCard(app, false));
                });
                gridContainer.append(normalRow);

                typeGroupsContainer.append(typeCard);
            });

            container.append(yearSection);
        });
    }

    function createApplicationCard(application, isFlaggedApp) {
        const fullName = `${application.applicantFirstName || ''} ${application.applicantLastName || ''}`.trim();
        const applicationDate = application.applicationDate ? new Date(application.applicationDate).toLocaleDateString() : 'N/A';
        const status = application.applicationStatus || 'PENDING';

        let statusClass = '';
        let statusText = '';
        switch (status) {
            case 'APPROVED':
                statusClass = 'badge-success';
                statusText = 'Approved';
                break;
            case 'REJECTED':
                statusClass = 'badge-danger';
                statusText = 'Rejected';
                break;
            case 'FLAGGED':
                statusClass = 'badge-warning';
                statusText = 'Flagged';
                break;
            default:
                statusClass = 'badge-info';
                statusText = 'Pending';
        }

        return $(`
            <div class="col-lg-4 col-md-6 mb-3">
                <div class="application-card ${isFlaggedApp ? 'flagged-card' : ''}" data-application='${JSON.stringify(application)}'>
                    <div class="card-header">
                        <div class="student-avatar">
                            ${application.applicantPicture ?
            `<img src="data:image/png;base64,${application.applicantPicture}" alt="Student">` :
            `<div class="avatar-placeholder">${fullName.charAt(0) || 'S'}</div>`
        }
                        </div>
                        <div class="student-info">
                            <h5 class="student-name">${fullName || 'Unknown Student'}</h5>
                            <p class="application-code">${application.applicationCode || 'No Code'}</p>
                        </div>
                        <span class="badge ${statusClass} status-badge">${statusText}</span>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <i class="fas fa-calendar"></i>
                            <span>Applied: ${applicationDate}</span>
                        </div>
                        <div class="info-row">
                            <i class="fas fa-school"></i>
                            <span>${application.applicationInstitution || 'N/A'}</span>
                        </div>
                        <div class="info-row">
                            <i class="fas fa-tag"></i>
                            <span>${application.applicationType || 'General'}</span>
                        </div>
                        ${application.score ? `
                        <div class="info-row score-display">
                            <i class="fas fa-star text-warning"></i>
                            <span>Score: <strong>${application.score}</strong>/100</span>
                        </div>
                        ` : ''}
                        ${isFlaggedApp ? `
                        <div class="alert alert-warning mt-2 mb-0 p-2 small">
                            <i class="fas fa-exclamation-triangle"></i> Missing required documents or information
                        </div>
                        ` : ''}
                    </div>
                    <div class="card-footer">
                        <button class="btn btn-sm btn-outline-primary view-details-btn">
                            <i class="fas fa-eye"></i> View Details
                        </button>
                        <button class="btn btn-sm btn-outline-success add-score-btn">
                            <i class="fas fa-plus-circle"></i> Add Score
                        </button>
                    </div>
                </div>
            </div>
        `);
    }

    // ============================================================
    // Modal Functions
    // ============================================================

    function showApplicationDetails(application) {
        const fullName = `${application.applicantFirstName || ''} ${application.applicantLastName || ''}`.trim();
        const modalBody = $('#applicationModalBody');

        modalBody.html(`
            <div class="application-details">
                <!-- Student Information Section -->
                <div class="details-section">
                    <h5><i class="fas fa-user-graduate"></i> Student Information</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="detail-item"><label>Full Name:</label><p>${fullName}</p></div>
                            <div class="detail-item"><label>Other Name:</label><p>${application.applicantOtherName || 'N/A'}</p></div>
                            <div class="detail-item"><label>Date of Birth:</label><p>${application.applicantDateOfBirth || 'N/A'}</p></div>
                            <div class="detail-item"><label>Place of Birth:</label><p>${application.applicantPlaceOfBirth || 'N/A'}</p></div>
                            <div class="detail-item"><label>Gender:</label><p>${application.applicantGender || 'N/A'}</p></div>
                        </div>
                        <div class="col-md-6">
                            <div class="detail-item"><label>Country of Birth:</label><p>${application.applicantCountryOfBirth || 'N/A'}</p></div>
                            <div class="detail-item"><label>Nationality:</label><p>${application.applicantNationality || 'N/A'}</p></div>
                            <div class="detail-item"><label>Denomination:</label><p>${application.applicantDenomination || 'N/A'}</p></div>
                            <div class="detail-item">
                                <label>Birth Certificate:</label>
                                ${application.applicantBirthCert ?
            `<a href="#" onclick="viewBirthCert('${application.applicantBirthCert}')" class="btn btn-sm btn-link">View Certificate</a>` :
            '<span class="text-muted">Not uploaded</span>'
        }
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Parent Information Section -->
                <div class="details-section">
                    <h5><i class="fas fa-users"></i> Parent/Guardian Information</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="sub-section">
                                <h6><i class="fas fa-male"></i> Father's Details</h6>
                                <div class="detail-item"><label>Name:</label><p>${application.fatherFirstNames || ''} ${application.fatherLastName || ''}</p></div>
                                <div class="detail-item"><label>Email:</label><p>${application.fatherEmail || 'N/A'}</p></div>
                                <div class="detail-item"><label>Contact:</label><p>${application.fatherContact1 || ''} ${application.fatherContact2 ? `/ ${application.fatherContact2}` : ''}</p></div>
                                <div class="detail-item"><label>Occupation:</label><p>${application.fatherOccupation || 'N/A'}</p></div>
                                <div class="detail-item"><label>Place of Work:</label><p>${application.fatherPlaceOfWork || 'N/A'}</p></div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="sub-section">
                                <h6><i class="fas fa-female"></i> Mother's Details</h6>
                                <div class="detail-item"><label>Name:</label><p>${application.motherFirstNames || ''} ${application.motherLastName || ''}</p></div>
                                <div class="detail-item"><label>Email:</label><p>${application.motherEmail || 'N/A'}</p></div>
                                <div class="detail-item"><label>Contact:</label><p>${application.motherContact1 || ''} ${application.motherContact2 ? `/ ${application.motherContact2}` : ''}</p></div>
                                <div class="detail-item"><label>Occupation:</label><p>${application.motherOccupation || 'N/A'}</p></div>
                                <div class="detail-item"><label>Place of Work:</label><p>${application.motherPlaceOfWork || 'N/A'}</p></div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Application Information Section -->
                <div class="details-section">
                    <h5><i class="fas fa-file-alt"></i> Application Information</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="detail-item"><label>Application Code:</label><p>${application.applicationCode || 'N/A'}</p></div>
                            <div class="detail-item"><label>Application Type:</label><p>${application.applicationType || 'N/A'}</p></div>
                            <div class="detail-item"><label>Application Date:</label><p>${application.applicationDate || 'N/A'}</p></div>
                        </div>
                        <div class="col-md-6">
                            <div class="detail-item"><label>Institution:</label><p>${application.applicationInstitution || 'N/A'}</p></div>
                            <div class="detail-item"><label>Status:</label><p><span class="badge ${getStatusBadgeClass(application.applicationStatus)}">${application.applicationStatus || 'PENDING'}</span></p></div>
                            ${application.score ? `<div class="detail-item"><label>Score:</label><p><strong>${application.score}/100</strong></p></div>` : ''}
                        </div>
                    </div>
                </div>
                
                ${application.nameOfPreviousSchool ? `
                <div class="details-section">
                    <h5><i class="fas fa-school"></i> Previous School Information</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="detail-item"><label>Previous School:</label><p>${application.nameOfPreviousSchool}</p></div>
                            <div class="detail-item"><label>Class of Departure:</label><p>${application.classOfDeparture || 'N/A'}</p></div>
                        </div>
                        <div class="col-md-6">
                            <div class="detail-item"><label>Reason for Departure:</label><p>${application.reasonForDeparture || 'N/A'}</p></div>
                            <div class="detail-item"><label>School Address:</label><p>${application.addressOfPreviousSchool || 'N/A'}</p></div>
                        </div>
                    </div>
                </div>
                ` : ''}
            </div>
        `);

        $('#applicationModal').data('currentApplication', application);
        $('#applicationModal').modal('show');
    }

    function getStatusBadgeClass(status) {
        switch (status) {
            case 'APPROVED':
                return 'badge-success';
            case 'REJECTED':
                return 'badge-danger';
            case 'FLAGGED':
                return 'badge-warning';
            default:
                return 'badge-info';
        }
    }

    // ============================================================
    // Event Handlers
    // ============================================================

    $(document).on('click', '.application-card', function (e) {
        if (!$(e.target).closest('.view-details-btn').length && !$(e.target).closest('.add-score-btn').length) {
            const application = $(this).data('application');
            showApplicationDetails(application);
        }
    });

    $(document).on('click', '.view-details-btn', function (e) {
        e.stopPropagation();
        const application = $(this).closest('.application-card').data('application');
        showApplicationDetails(application);
    });

    $(document).on('click', '.add-score-btn', function (e) {
        e.stopPropagation();
        const application = $(this).closest('.application-card').data('application');
        const fullName = `${application.applicantFirstName || ''} ${application.applicantLastName || ''}`.trim();
        $('#scoreStudentName').val(fullName);
        $('#studentScore').val(application.score || '');
        $('#scoreRemarks').val('');
        $('#scoreModal').data('currentApplication', application);
        $('#scoreModal').modal('show');
    });

    $('#saveScoreBtn').click(async function () {
        const application = $('#scoreModal').data('currentApplication');
        const score = $('#studentScore').val();
        const remarks = $('#scoreRemarks').val();

        if (!score || score < 0 || score > 100) {
            alert('Please enter a valid score between 0 and 100');
            return;
        }

        showLoading();
        try {
            const payload = {
                applicationId: application.idapplication,
                score: parseFloat(score),
                remarks: remarks,
                institutionCode: application.applicationInstitution
            };

            const response = await fetchPost("/updateApplicationScore", payload);
            if (response) {
                alert('Score saved successfully!');
                $('#scoreModal').modal('hide');
                loadApplications();
            }
        } catch (error) {
            console.error("Error saving score:", error);
            alert('Failed to save score. Please try again.');
        }
        hideLoading();
    });

    $('#approveApplicationBtn').click(async function () {
        const application = $('#applicationModal').data('currentApplication');
        await updateApplicationStatus(application, 'APPROVED');
    });

    $('#rejectApplicationBtn').click(async function () {
        const application = $('#applicationModal').data('currentApplication');
        await updateApplicationStatus(application, 'REJECTED');
    });

    $('#flagApplicationBtn').click(async function () {
        const application = $('#applicationModal').data('currentApplication');
        await updateApplicationStatus(application, 'FLAGGED');
    });

    async function updateApplicationStatus(application, status) {
        showLoading();
        try {
            const payload = {
                applicationId: application.idapplication,
                status: status,
                institutionCode: application.applicationInstitution
            };

            const response = await fetchPost("/updateApplicationStatus", payload);
            if (response) {
                alert(`Application ${status.toLowerCase()} successfully!`);
                $('#applicationModal').modal('hide');
                loadApplications();
            }
        } catch (error) {
            console.error("Error updating status:", error);
            alert('Failed to update status. Please try again.');
        }
        hideLoading();
    }

    // ============================================================
    // Utility Functions
    // ============================================================

    function exportToExcel() {
        alert('Export functionality will be implemented');
    }

    function showLoading() {
        $('.splash').css({'display': 'block', 'background': '#ffffff3d'});
    }

    function hideLoading() {
        $('.splash').css('display', 'none');
    }

    function showEmptyState() {
        $('#applicationsContainer').html(`
            <div class="text-center p-5">
                <i class="fas fa-inbox fa-4x text-muted mb-3"></i>
                <h4>No Applications Found</h4>
                <p>No admission applications have been submitted yet.</p>
            </div>
        `);
    }

    function showError(message) {
        $('#applicationsContainer').html(`
            <div class="alert alert-danger text-center">
                <i class="fas fa-exclamation-triangle"></i> ${message}
            </div>
        `);
    }
});

// Helper function for birth certificate viewing
function viewBirthCert(base64Data) {
    const blob = base64ToBlob(base64Data, 'application/pdf');
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank');
}

function base64ToBlob(base64, mimeType) {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], {type: mimeType});
}

/*async function fetchPost(url, data) {
    const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;

    try {
        const headers = {
            "Content-Type": "application/json",
            "Accept": "application/json",
        };
        if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

        const response = await fetch(url, {
            method: "POST",
            headers: headers,
            body: JSON.stringify(data),
            credentials: "include",
        });

        if (response.status === 404) return null;
        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const contentLength = response.headers.get("content-length");
        const contentType = response.headers.get("content-type");
        if (contentLength === "0" || !contentType || !contentType.includes("application/json")) return null;

        return await response.json();
    } catch (error) {
        console.error("POST Error:", error.message || error);
        throw error;
    }
}*/
