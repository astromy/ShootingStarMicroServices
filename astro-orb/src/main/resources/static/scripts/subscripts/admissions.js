$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
                data-target="#myadmissionsModal">New Admissions</button>
                </div>
            </div>
    `

    //function institutionBuild() {
    let admissions = `
    
    
    <div class="content animate-panel" id="pagecontent">
        <div class="hpanel">

            <div class="panel-heading">
                <div class="panel-tools">
                </div>
                Admissions
            </div>
            <div class="panel-body">
                <table id="admissionsTable" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                    <tr>
                        <th>Form Type</th>
                        <th>Quantity</th>
                        <th>Unit Price</th>
                        <th width="15%">Payment Form</th>
                        <th width="15%">Sales Period</th>
                        <th width="15%">Interview Period</th>
                        <th width="15%">Daily Appointments</th>
                    </tr>
                    </thead>
                    <tbody id="admissionsTableBody"></tbody>
                </table>

            </div>
        </div>
    

    
        <div class="modal fade hmodal-info" id="myadmissionsModal" tabindex="-1" role="dialog"aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="color-line"></div>
                        <div class="modal-header">
                            <h4 class="modal-title">Add Admissions</h4>
                            <small class="font-bold">A Admissions is a collection of classes eg. Creche, Nursery, KG, Lower Primary, Upper Primary etc.</small>
                        </div>
                        <div class="panel-body modalbody" style="border-bottom: 1px solid #a8bede;">
                            <div class="form-group">
                                <div class="col-sm-9">
                                    <div class="row">
                                        <div class="col-md-12"><input type="text" placeholder="Enter Admissions Name" class="form-control newadmissionstxt"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="panel-body modalbody">
                            <div class="form-group">
                                <div class="col-sm-9">
                                    <div class="row">
                                        <div class="col-md-12"><input type="text" placeholder="Enter Admissions Name" class="form-control newadmissionstxt"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More Criteria</i></button>
                            <button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More Category</i></button>
                            <button type="button" class="btn btn-default"data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary">Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
    </div>
    `




    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', admissions);
    document.getElementsByClassName("test")[0].addEventListener("click", admissionsCriteria);
    document.getElementsByClassName("test")[1].addEventListener("click", admissionsCategory);
    document.getElementById("modalopn").addEventListener("click", modalopn);

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_admissions.js");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    document.getElementsByClassName("modalbody")[1].innerHTML="";
    admissionsCriteria();
    admissionsCategory();
}

function admissionsCriteria() {
    let div = `
    <div class="row">

        <div class="col-sm-4">
            <div class="row">
                <div class="col-md-12">
                    <select class="form-control m-b newadmissionCriteria" name="classGroup">
                        <option>Select Criteria</option>
                        <option>Scored Marks</option>
                        <option></option>
                    </select>
                </div>
            </div>
        </div>
        <div class="col-sm-4">
            <div class="row">
                <div class="col-md-12"><input type="text" placeholder="Enter Value" class="form-control newcriteriaValue"></div>
            </div>
        </div>
        <div class="col-sm-4">
            <div class="row">
                <div class="col-md-12">
                    <select class="form-control m-b newcriteriaOperand" name="class">
                        <option>Select Operand</option>
                        <option>AND</option>
                        <option>OR</option>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[0].insertAdjacentHTML('beforeend', div);
}

function admissionsCategory() {
    let div = `
    <div class="row">

        <div class="col-sm-3">
            <div class="row">
            <div class="col-md-12"><input type="number" placeholder="Price per Form" class="form-control newclassestxt"></div>
            </div>
        </div>

        <div class="col-sm-3">
            <div class="row">
            <div class="col-md-12"><input type="text" placeholder="Form Type" class="form-control newclassestxt"></div>
            </div>
        </div>

        <div class="col-sm-3">
            <div class="row">
            <div class="col-md-12"><input type="number" placeholder="Issued Form Quantity" class="form-control newclassestxt"></div>
            </div>
        </div>

        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12">
                    <select class="form-control m-b" name="classGroup">
                        <option>Select Payment form</option>
                        <option>Online</option>
                        <option>On-premis</option>
                    </select>
                </div>
            </div>
        </div>

        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12 datpicker1div">
                    <input class="form-control datetimepicker" type="text" placeholder="Form Sales Period"  />
                </div>
            </div>
        </div>

        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12">
                <input type="number" placeholder="Appointments per Day" class="form-control newclassestxt">
                </div>
            </div>
        </div>

        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12 datpicker1div">
                    <input class="form-control datetimepicker" type="text" placeholder="Interview Period" />
                </div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[1].insertAdjacentHTML('beforeend', div);
}


$('.datetimepicker').daterangepicker({
    "showDropdowns": true,
    "showWeekNumbers": true,
    "timePicker": true,
    "ranges": {
        "Today": [
            "2023-11-25T04:27:41.846Z",
            "2023-11-25T04:27:41.846Z"
        ],
        "Yesterday": [
            "2023-11-24T04:27:41.846Z",
            "2023-11-24T04:27:41.846Z"
        ],
        "Last 7 Days": [
            "2023-11-19T04:27:41.846Z",
            "2023-11-25T04:27:41.846Z"
        ],
        "Last 30 Days": [
            "2023-10-27T04:27:41.846Z",
            "2023-11-25T04:27:41.846Z"
        ],
        "This Month": [
            "2023-11-01T00:00:00.000Z",
            "2023-11-30T23:59:59.999Z"
        ],
        "Last Month": [
            "2023-10-01T00:00:00.000Z",
            "2023-10-31T23:59:59.999Z"
        ]
    },
    "locale": {
        "direction": "ltr",
        "format": "MM/DD/YYYY HH:mm",
        "separator": " - ",
        "applyLabel": "Apply",
        "cancelLabel": "Cancel",
        "fromLabel": "From",
        "toLabel": "To",
        "customRangeLabel": "Custom",
        "daysOfWeek": [
            "Su",
            "Mo",
            "Tu",
            "We",
            "Th",
            "Fr",
            "Sa"
        ],
        "monthNames": [
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
        ],
        "firstDay": 1
    },
    "alwaysShowCalendars": true,
    "parentEl": "\"$('.datpicker1div')\"",
    "startDate": "11/19/2023",
    "endDate": "11/25/2023",
    "opens": "center",
    "drops": "up"
}, function(start, end, label) {
  console.log("New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')");
});

$(function () {

    // Initialize Example 1
    $('#admissionsTable').dataTable({
        "ajax": 'api/datatables.json',
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            { extend: 'copy', className: 'btn-sm' },
            { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'print', className: 'btn-sm' }
        ]
    });
});
