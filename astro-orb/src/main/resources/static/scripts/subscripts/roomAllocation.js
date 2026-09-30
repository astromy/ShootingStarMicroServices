// Markup for Accommodation > Room Allocation.
// Redefined scope: this page ONLY assigns/unassigns students to rooms that
// already exist. Creating blocks, rooms, and assigning block masters moved
// to Setup > Accommodation - this page shows blocks and rooms read-only.

document.getElementById("wrapper").innerHTML =
    '<div class="fc-page" id="roomAllocationPage">' + [
        '<div class="row">',
        '  <div class="col-lg-12">',
        '    <div class="hpanel">',
        '      <div class="panel-heading hbuilt">',
        '        <h2 style="margin:0;">Room Allocation</h2>',
        '        <small class="text-muted">Assign students to rooms. To add blocks, rooms, or a block master, use Setup &gt; Accommodation.</small>',
        '      </div>',
        '      <div class="panel-body">',
        '        <div class="empty-state text-center text-muted" id="blocksEmptyState" style="display:none; padding:40px 0;">',
        '          <p>No blocks set up yet. Add blocks and rooms under Setup &gt; Accommodation first.</p>',
        '        </div>',
        '        <div class="row" id="blocksContainer"></div>',
        '      </div>',
        '    </div>',
        '  </div>',
        '</div>',

        // ==================== Rooms in Block Modal ====================
        '<div class="modal fade" id="manageBlockModal" tabindex="-1" role="dialog">',
        '  <div class="modal-dialog modal-lg" role="document">',
        '    <div class="modal-content">',
        '      <div class="modal-header">',
        '        <button type="button" class="close" data-dismiss="modal">&times;</button>',
        '        <h4 class="modal-title" id="manageBlockTitle">Block Rooms</h4>',
        '      </div>',
        '      <div class="modal-body">',
        '        <table class="table table-striped">',
        '          <thead><tr><th>Room</th><th>Reserved Beds</th><th>General Beds</th><th>Occupied</th><th>Capacity</th><th></th></tr></thead>',
        '          <tbody id="roomsTableBody"></tbody>',
        '        </table>',
        '      </div>',
        '      <div class="modal-footer">',
        '        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>',
        '      </div>',
        '    </div>',
        '  </div>',
        '</div>',

        // ==================== Room Occupants Modal ====================
        '<div class="modal fade" id="roomOccupantsModal" tabindex="-1" role="dialog">',
        '  <div class="modal-dialog" role="document">',
        '    <div class="modal-content">',
        '      <div class="modal-header">',
        '        <button type="button" class="close" data-dismiss="modal">&times;</button>',
        '        <h4 class="modal-title" id="roomOccupantsTitle">Students in Room</h4>',
        '      </div>',
        '      <div class="modal-body">',
        '        <ul class="list-group" id="occupantsList"></ul>',
        '        <div class="form-group" style="margin-top:15px;">',
        '          <label>Assign a student by ID</label>',
        '          <div class="input-group">',
        '            <input type="text" class="form-control" id="assignStudentIdInput" placeholder="Student ID">',
        '            <span class="input-group-btn">',
        '              <button class="btn btn-primary" id="assignStudentBtn">Assign</button>',
        '            </span>',
        '          </div>',
        '          <p class="text-danger small" id="assignError" style="display:none; margin-top:5px;"></p>',
        '        </div>',
        '      </div>',
        '      <div class="modal-footer">',
        '        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>',
        '      </div>',
        '    </div>',
        '  </div>',
        '</div>',

        '</div>'
    ].join('\n');

var script_roomAllocation = document.createElement("script");
script_roomAllocation.setAttribute("type", "text/javascript");
script_roomAllocation.setAttribute("src", "scripts/_roomAllocation.js");
script_roomAllocation.setAttribute("data-dynamic", "true");
document.getElementsByTagName("body")[0].appendChild(script_roomAllocation);
