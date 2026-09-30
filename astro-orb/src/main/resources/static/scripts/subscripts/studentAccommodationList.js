// Markup for Accommodation > Student List.
// Shows every room-assigned student, grouped by Block and Room, with a
// simple filter to narrow to one block.

document.getElementById("wrapper").innerHTML =
    '<div class="fc-page" id="studentListPage">' + [
        '<div class="row">',
        '  <div class="col-lg-12">',
        '    <div class="hpanel">',
        '      <div class="panel-heading hbuilt">',
        '        <h2 style="margin:0;">Student List</h2>',
        '        <small class="text-muted">Students by block and room</small>',
        '        <div class="pull-right" style="margin-top:-30px;">',
        '          <select class="form-control input-sm" id="blockFilterSelect" style="width:200px; display:inline-block;">',
        '            <option value="">All blocks</option>',
        '          </select>',
        '        </div>',
        '      </div>',
        '      <div class="panel-body">',
        '        <div class="empty-state text-center text-muted" id="studentListEmptyState" style="display:none; padding:40px 0;">',
        '          <p>No students currently assigned to rooms.</p>',
        '        </div>',
        '        <div id="studentListContainer"></div>',
        '      </div>',
        '    </div>',
        '  </div>',
        '</div>',
        '</div>'
    ].join('\n');

var script_studentList = document.createElement("script");
script_studentList.setAttribute("type", "text/javascript");
script_studentList.setAttribute("src", "scripts/_studentAccommodationList.js");
script_studentList.setAttribute("data-dynamic", "true");
document.getElementsByTagName("body")[0].appendChild(script_studentList);
