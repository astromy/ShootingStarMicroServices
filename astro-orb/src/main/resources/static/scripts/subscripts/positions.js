// Markup for the Accommodation > Positions & Prefects page.
// Scoped to prefects only - block master assignment moved to
// Setup > Accommodation, since it's a setup-time decision made alongside
// creating the block and its rooms.

document.getElementById("wrapper").innerHTML =
    '<div class="fc-page" id="positionsPage">' + [
        '<div class="row">',
        '  <div class="col-lg-12">',
        '    <div class="hpanel">',
        '      <div class="panel-heading hbuilt">',
        '        <h2 style="margin:0;">Positions & Prefects</h2>',
        '        <small class="text-muted">Assign and manage student prefects for each block</small>',
        '      </div>',
        '      <div class="panel-body">',
        '        <div class="empty-state" id="positionsEmptyState" style="display:none;">',
        '          <p class="text-muted text-center">No blocks yet. Create a block under Setup &gt; Accommodation first.</p>',
        '        </div>',
        '        <div id="blocksPositionsContainer"></div>',
        '      </div>',
        '    </div>',
        '  </div>',
        '</div>',

        // ==================== Assign Block Prefect Modal ====================
        '<div class="modal fade" id="assignPrefectModal" tabindex="-1" role="dialog">',
        '  <div class="modal-dialog" role="document">',
        '    <div class="modal-content">',
        '      <div class="modal-header">',
        '        <button type="button" class="close" data-dismiss="modal">&times;</button>',
        '        <h4 class="modal-title" id="assignPrefectTitle">Assign Block Prefect</h4>',
        '      </div>',
        '      <div class="modal-body">',
        '        <div class="form-group">',
        '          <label>Student Name</label>',
        '          <input type="text" class="form-control" id="prefectStudentName" placeholder="e.g. Ama Serwaa">',
        '        </div>',
        '        <div class="form-group">',
        '          <label>Student ID</label>',
        '          <input type="text" class="form-control" id="prefectStudentId" placeholder="Student ID">',
        '        </div>',
        '        <div class="form-group">',
        '          <label>Appointment Date</label>',
        '          <input type="date" class="form-control" id="prefectAppointmentDate">',
        '        </div>',
        '      </div>',
        '      <div class="modal-footer">',
        '        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>',
        '        <button type="button" class="btn btn-primary" id="savePrefectBtn">Assign</button>',
        '      </div>',
        '    </div>',
        '  </div>',
        '</div>',

        '</div>'
    ].join('\n');

var script_positions = document.createElement("script");
script_positions.setAttribute("type", "text/javascript");
script_positions.setAttribute("src", "scripts/_positions.js");
script_positions.setAttribute("data-dynamic", "true");
document.getElementsByTagName("body")[0].appendChild(script_positions);
