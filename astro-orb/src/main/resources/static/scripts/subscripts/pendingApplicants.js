$(function () {
    // Header HTML for the applications management UI
    let header = `
        <div class="panel-body">
            <div class="row">
                <div class="col-lg-12">
                    <div class="pull-right">
                        <button class="btn btn-primary" type="button" id="refreshApplicationsBtn">
                            <i class="fas fa-sync-alt"></i> Refresh
                        </button>
                        <button class="btn btn-success" type="button" id="exportApplicationsBtn">
                            <i class="fas fa-file-excel"></i> Export to Excel
                        </button>
                    </div>
                </div>
            </div>
            <div class="row mt-3">
                <div class="col-lg-12">
                    <div class="form-group col-lg-3">
                        <select class="form-control" id="yearFilter">
                            <option value="all">All Years</option>
                        </select>
                    </div>
                    <div class="form-group col-lg-3">
                        <select class="form-control" id="applicationTypeFilter">
                            <option value="all">All Application Types</option>
                        </select>
                    </div>
                    <div class="form-group col-lg-3">
                        <select class="form-control" id="statusFilter">
                            <option value="all">All Status</option>
                            <option value="PENDING">Pending Review</option>
                            <option value="APPROVED">Approved</option>
                            <option value="REJECTED">Rejected</option>
                            <option value="FLAGGED">Flagged</option>
                        </select>
                    </div>
                    <div class="form-group col-lg-3">
                        <div class="input-group">
                            <input type="text" class="form-control" id="searchInput" placeholder="Search by name or application code...">
                            <div class="input-group-append">
                                <button class="btn btn-outline-secondary" type="button" id="searchBtn">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Main content HTML
    let mainContent = `
        <div class="content animate-panel" id="pagecontent">
            <div class="hpanel">
                <div class="panel-heading">
                    <div class="panel-tools">
                        <a class="showhide"><i class="fa fa-chevron-up"></i></a>
                        <a class="closebox"><i class="fa fa-times"></i></a>
                    </div>
                    <i class="fas fa-users"></i> Admission Applications Management
                </div>
                <div class="panel-body">
                    <!-- Statistics Cards -->
                    <div class="row stat-cards mb-4" id="statCards">
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-card total-applications">
                                <div class="stat-icon"><i class="fas fa-file-alt"></i></div>
                                <div class="stat-info">
                                    <h3 id="totalCount">0</h3>
                                    <p>Total Applications</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-card pending-applications">
                                <div class="stat-icon"><i class="fas fa-clock"></i></div>
                                <div class="stat-info">
                                    <h3 id="pendingCount">0</h3>
                                    <p>Pending Review</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-card flagged-applications">
                                <div class="stat-icon"><i class="fas fa-flag"></i></div>
                                <div class="stat-info">
                                    <h3 id="flaggedCount">0</h3>
                                    <p>Flagged Applications</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-card approved-applications">
                                <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
                                <div class="stat-info">
                                    <h3 id="approvedCount">0</h3>
                                    <p>Approved</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Grouped Applications View -->
                    <div id="applicationsContainer"></div>
                </div>
            </div>
        </div>
        
        <footer class="footer">
            <span class="pull-right">ORB</span>
            <span class="fa fa-copyright"></span>
            Astromy LLC 2013-<span id="copyrightYear"></span>
        </footer>
    `;

    // Modal for viewing application details
    let modalHtml = `
        <div class="modal fade" id="applicationModal" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Application Details</h5>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body" id="applicationModalBody"></div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-success" id="approveApplicationBtn">Approve</button>
                        <button type="button" class="btn btn-danger" id="rejectApplicationBtn">Reject</button>
                        <button type="button" class="btn btn-warning" id="flagApplicationBtn">Flag</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Score input modal
    let scoreModalHtml = `
        <div class="modal fade" id="scoreModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Enter Student Score</h5>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Student Name</label>
                            <input type="text" class="form-control" id="scoreStudentName" readonly>
                        </div>
                        <div class="form-group">
                            <label>Score (0-100)</label>
                            <input type="number" class="form-control" id="studentScore" min="0" max="100" step="0.01">
                        </div>
                        <div class="form-group">
                            <label>Remarks (Optional)</label>
                            <textarea class="form-control" id="scoreRemarks" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" id="saveScoreBtn">Save Score</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Render the UI
    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', mainContent);
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    document.body.insertAdjacentHTML('beforeend', scoreModalHtml);

    // Set copyright year
    document.getElementById('copyrightYear').textContent = new Date().getFullYear();
    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_pendingApplications.js");
    document.getElementsByTagName("body")[0].appendChild(script14);
});