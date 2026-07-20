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
    fetchInstitution();   // parallel — feeds the class assignment dropdowns in the modal

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
    // Institution / Class Data   (mirrors _financeBilling.js)
    // ============================================================

    window.pendingClassesList = [];   // [{ name, classGroup }] — read by pendingApplications.js

    async function fetchInstitution() {
        try {
            const inst = await fetchPost("/getInstitutionByCode", {val: instId});
            if (!inst) return;

            const raw = inst.classList || inst.classes || inst.courses || [];

            window.pendingClassesList = raw
                .map(c => ({
                    name: c.name || c.className || c.class || "",
                    classGroup: c.classGroup || c.group || c.category || "",
                }))
                .filter(c => c.name);

        } catch (err) {
            console.warn("[_pendingApplications] Could not fetch institution:", err);
        }
    }

    /** Unique sorted class-group names */
    window.getApplicationClassGroups = function () {
        return [...new Set(window.pendingClassesList.map(c => c.classGroup).filter(Boolean))].sort();
    };

    /** Classes belonging to a given group */
    window.getApplicationClassesByGroup = function (groupName) {
        if (!groupName) return window.pendingClassesList;
        return window.pendingClassesList.filter(
            c => c.classGroup.toLowerCase() === groupName.toLowerCase()
        );
    };

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
        const status = application.applicationStatus || 'APPLIED';

        let statusClass = 'badge-info';
        let statusText = 'Applied';
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
        }

        function extractFilename(path) {
            if (!path) return null;
            return path.split('\\').pop().split('/').pop();
        }

        var filename = extractFilename(application.applicantPicture);

        var avatarHtml = filename
            ? '<img src="/getApplicantPicture/' + filename + '" alt="Student" class="student-img" onerror="handleImgError(this)"><div class="avatar-placeholder" style="display:none">' + (fullName.charAt(0) || 'S') + '</div>'
            : '<div class="avatar-placeholder">' + (fullName.charAt(0) || 'S') + '</div>';

        return $(`
        <div class="col-lg-4 col-md-6 mb-3">
            <div class="application-card ${isFlaggedApp ? 'flagged-card' : ''}" data-application='${JSON.stringify(application)}'>
                <div class="card-header">
                    <div class="student-avatar">
                        ${avatarHtml}
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
                        <span>${application.applicationInstitutionName || application.applicationInstitution || 'N/A'}</span>
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

        const parents = application.studentParents || [];
        const father = parents.find(p =>
            p.parentType?.toLowerCase().includes('father') ||
            p.parentType?.toLowerCase().includes('biological') ||
            p.parentType?.toLowerCase().includes('guardian')
        ) || parents[0] || null;

        const mother = parents.find(p =>
            p.parentType?.toLowerCase().includes('mother')
        ) || (parents.length > 1 ? parents[1] : null);

        const isApproved = (application.applicationStatus || '').toUpperCase() === 'APPROVED';

        // ── Class assignment dropdown state ───────────────────────────────────
        const groups = window.getApplicationClassGroups();
        const groupOpts = groups.length
            ? groups.map(g => `<option value="${g}">${g}</option>`).join('')
            : '<option value="">No groups available</option>';

        const currentClass = application.assignedClass || '';
        let preselectedGroup = '';
        if (currentClass) {
            const match = window.pendingClassesList.find(c => c.name === currentClass);
            if (match) preselectedGroup = match.classGroup;
        }

        const classOpts = preselectedGroup
            ? window.getApplicationClassesByGroup(preselectedGroup)
                .map(c => `<option value="${c.name}" ${c.name === currentClass ? 'selected' : ''}>${c.name}</option>`)
                .join('')
            : '<option value="">Select group first</option>';

        const modalBody = $('#applicationModalBody');

        modalBody.html(`
        <div class="application-details">

            <!-- Student Information -->
            <div class="details-section">
                <h5><i class="fas fa-user-graduate"></i> Student Information</h5>
                <div class="row">
                    <div class="col-md-6">
                        <div class="detail-item"><label>Full Name:</label><p>${fullName || 'N/A'}</p></div>
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
                            ${application.applicantBirthCert
            ? `<a href="#" onclick="viewBirthCert('${application.applicantBirthCert}')" class="btn btn-sm btn-link">View Certificate</a>`
            : '<span class="text-muted">Not uploaded</span>'
        }
                        </div>
                    </div>
                </div>
            </div>

            <!-- Parent Information -->
            <div class="details-section">
                <h5><i class="fas fa-users"></i> Parent / Guardian Information</h5>
                <div class="row">
                    <div class="col-md-6">
                        <div class="sub-section">
                            <h6><i class="fas fa-male"></i> ${father ? father.parentType || 'Parent' : 'Parent'} Details</h6>
                            ${father ? `
                                <div class="detail-item"><label>Name:</label><p>${father.firstNames || ''} ${father.lastName || ''}</p></div>
                                <div class="detail-item"><label>Email:</label><p>${father.email || 'N/A'}</p></div>
                                <div class="detail-item"><label>Contact:</label><p>${father.contact1 || 'N/A'}${father.contact2 ? ` / ${father.contact2}` : ''}</p></div>
                                <div class="detail-item"><label>Occupation:</label><p>${father.occupation || 'N/A'}</p></div>
                                <div class="detail-item"><label>Place of Work:</label><p>${father.placeOfWork || 'N/A'}</p></div>
                            ` : '<p class="text-muted">No parent information provided</p>'}
                        </div>
                    </div>
                    <div class="col-md-6">
                        ${mother ? `
                        <div class="sub-section">
                            <h6><i class="fas fa-female"></i> ${mother.parentType || 'Parent'} Details</h6>
                            <div class="detail-item"><label>Name:</label><p>${mother.firstNames || ''} ${mother.lastName || ''}</p></div>
                            <div class="detail-item"><label>Email:</label><p>${mother.email || 'N/A'}</p></div>
                            <div class="detail-item"><label>Contact:</label><p>${mother.contact1 || 'N/A'}${mother.contact2 ? ` / ${mother.contact2}` : ''}</p></div>
                            <div class="detail-item"><label>Occupation:</label><p>${mother.occupation || 'N/A'}</p></div>
                            <div class="detail-item"><label>Place of Work:</label><p>${mother.placeOfWork || 'N/A'}</p></div>
                        </div>
                        ` : '<p class="text-muted small mt-3">Only one parent/guardian provided</p>'}
                    </div>
                </div>
            </div>

            <!-- Application Information -->
            <div class="details-section">
                <h5><i class="fas fa-file-alt"></i> Application Information</h5>
                <div class="row">
                    <div class="col-md-6">
                        <div class="detail-item"><label>Application Code:</label><p><strong>${application.applicationCode || 'N/A'}</strong></p></div>
                        <div class="detail-item"><label>Application Type:</label><p>${application.applicationType || 'N/A'}</p></div>
                        <div class="detail-item"><label>Application Date:</label><p>${application.applicationDate || 'N/A'}</p></div>
                        <div class="detail-item"><label>Appointment Date:</label>
                            <p>${application.appointmentDate
            ? new Date(application.appointmentDate).toLocaleString('en-GB', {
                weekday: 'long', year: 'numeric', month: 'long',
                day: '2-digit', hour: '2-digit', minute: '2-digit'
            })
            : 'N/A'}
                            </p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="detail-item"><label>Institution:</label><p>${application.applicationInstitutionName || application.applicationInstitution || 'N/A'}</p></div>
                        <div class="detail-item"><label>Status:</label>
                            <p><span class="badge ${getStatusBadgeClass(application.applicationStatus)}">${application.applicationStatus || 'APPLIED'}</span></p>
                        </div>
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

            <!-- Class Assignment -->
            <div class="details-section">
                <h5><i class="fas fa-chalkboard-teacher"></i> Class Assignment</h5>
                ${!isApproved ? `
                <div class="alert alert-warning py-2 px-3 small">
                    <i class="fas fa-lock"></i> Approve this application first to assign a class.
                </div>` : ''}
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label>Class Group</label>
                            <select class="form-control" id="modalClassGroupSelect" ${!isApproved ? 'disabled' : ''}>
                                <option value="">Select group…</option>
                                ${groupOpts}
                            </select>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label>Class</label>
                            <select class="form-control" id="modalClassSelect" disabled>
                                ${classOpts}
                            </select>
                        </div>
                    </div>
                </div>
                ${currentClass ? `
                <p class="text-success small mb-0">
                    <i class="fas fa-check-circle"></i> Currently assigned to <strong>${currentClass}</strong>
                </p>` : ''}
            </div>

        </div>
    `);

        // Pre-select group if already assigned
        if (preselectedGroup) {
            $('#modalClassGroupSelect').val(preselectedGroup);
            $('#modalClassSelect').prop('disabled', !isApproved);
        }

        // Wire cascading dropdown
        $('#modalClassGroupSelect').on('change', function () {
            const selectedGroup = $(this).val();
            const classSelect = $('#modalClassSelect');
            if (!selectedGroup) {
                classSelect.html('<option value="">Select group first</option>').prop('disabled', true);
                return;
            }
            const classes = window.getApplicationClassesByGroup(selectedGroup);
            if (classes.length) {
                classSelect.html(
                    '<option value="">Select class…</option>' +
                    classes.map(c => `<option value="${c.name}">${c.name}</option>`).join('')
                ).prop('disabled', false);
            } else {
                classSelect.html('<option value="">No classes in this group</option>').prop('disabled', true);
            }
        });

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
        const assignedClass = status === 'APPROVED'
            ? ($('#modalClassSelect').val() || null)
            : null;

        showLoading();
        try {
            const payload = [
                {
                    idapplication: application.idapplication,
                    status: status,
                    institutionCode: application.applicationInstitution,
                    applicationCode: application.applicationCode,
                    ...(assignedClass ? {assignedClass} : {}),
                }
            ];

            const response = await fetchPost("/updateApplicationStatus", payload);
            if (response) {
                swal({
                    title: "Success!",
                    text: `Application ${status.toLowerCase()} successfully!`,
                    type: "success"
                });
                $('#applicationModal').modal('hide');
                loadApplications();
            }
        } catch (error) {
            console.error("Error updating status:", error);
            swal({title: "Error", text: "Failed to update status.", type: "error"});
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
function viewBirthCert(filePath) {
    var filename = filePath.split('\\').pop().split('/').pop();
    window.open('/getApplicantBirthCert/' + filename, '_blank');
}

window.handleImgError = function (img) {
    img.style.display = 'none';
    var placeholder = img.nextElementSibling;
    if (placeholder) placeholder.style.display = 'flex';
};