$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
                data-target="#myadmissionsModal">New Admissions Settings</button>
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
                        <th width="15%">Payment Medium</th>
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
            <div class="modal-dialog modal-lg" style="margin: 100px auto">
                <div class="modal-content">
                    <div class="color-line"></div>
                        <div class="modal-header">
                            <h4 class="modal-title">Add Admissions</h4>
                            <small class="font-bold">Admissions settings are a set of settings made to auto control the Criteria for an online admission and \b the quantity, price, type and period for admissions among others </small>
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
                            <button type="button" class="btn btn-default dismissAdmission" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary saveAdmissions">Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
    </div>

      <!-- Footer-->
        <footer class="footer">
            <span class="pull-right">
                ORB
            </span>
            <span class="fa fa-copyright"></span>
            Astromy LLC 2013-<span id="copyrightYear"></span>
        </footer>
    `




    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', admissions);
    document.getElementsByClassName("test")[0].addEventListener("click", admissionsSettingsCriteria);
    document.getElementsByClassName("test")[1].addEventListener("click", admissionsSettingsCategory);
    document.getElementById("modalopn").addEventListener("click", modalopn);

    admissionsCriteria();
    admissionsCategory();

    /*document.getElementsByClassName("modalbody")[0].append(clonable1b);
    document.getElementsByClassName("modalbody")[0].append(clonable2);

    document.getElementsByClassName("modalbody")[1].append(clonable);
    document.getElementsByClassName("modalbody")[1].append(clonable2);*/

    daterangepicker(".datetimepicker",".datePicker1_div");
    daterangepicker(".datetimepicker2",".datePicker1_div2")

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_admissions.js");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
   document.getElementsByClassName("modalbody")[0].innerHTML="";
   document.getElementsByClassName("modalbody")[1].innerHTML="";
   admissionsSettingsCriteria();
   admissionsSettingsCategory();

}




function admissionsSettingsCriteria() {
// Clone the elements with their child nodes (true means deep clone)
    const clone1a = clonable1b.cloneNode(true);
    const clone2 = clonable2.cloneNode(true);

    // Append the cloned elements to the modal body
    const modalBody = document.getElementsByClassName("modalbody")[0];
    modalBody.appendChild(clone1a);
    modalBody.appendChild(clone2);
    const $clone1a = $(clone1a);
    daterangepicker($clone1a.find('.datetimepicker','.datePicker1_div'));
}

function admissionsSettingsCategory() {
// Clone the elements with their child nodes (true means deep clone)
    const clone1b = clonable.cloneNode(true);
    const clone2 = clonable2.cloneNode(true);

    const modalBody2 = document.getElementsByClassName("modalbody")[1];
    modalBody2.appendChild(clone1b);
    modalBody2.appendChild(clone2);
    const $clone1b = $(clone1b);
    daterangepicker($clone1b.find('.datetimepicker','.datePicker1_div'));
    daterangepicker($clone1b.find('.datetimepicker2','.datePicker1_div2'));
}


    function admissionsCriteria() {
        clonable1b=document.createElement("div");
        clonable1b.classList.add("row", "clonableCriteria");

        // Create div elements
        var ela1 = document.createElement("div");
        ela1.classList.add("col-sm-4");

        var ela2 = document.createElement("div");
        ela2.classList.add("row");

        var ela3 = document.createElement("div");
        ela3.classList.add("col-md-12");

        // Create select and options
        var ela4 = document.createElement("select");
        ela4.classList.add("form-control", "m-b", "newadmissionCriteria");

        var ela4i = document.createElement("option");
        ela4i.value = 0;
        ela4i.textContent = "Select Criteria";

        var ela4ii = document.createElement("option");
        ela4ii.value = 2;
        ela4ii.textContent = "Scored Marks";

        var ela4iii = document.createElement("option");
        ela4iii.value = 3;
        ela4iii.textContent = "Denomination";

        var ela4iv = document.createElement("option");
        ela4iv.value = 4;
        ela4iv.textContent = "Age";

        // Append options to select
        ela4.appendChild(ela4i);
        ela4.appendChild(ela4ii);
        ela4.appendChild(ela4iii);
        ela4.appendChild(ela4iv);

        // Build the structure
        ela3.appendChild(ela4);
        ela2.appendChild(ela3);
        ela1.appendChild(ela2);
        clonable1b.appendChild(ela1);

        // Create second set of elements
        var el1a = document.createElement("div");
        el1a.classList.add("col-sm-4");

        var el2a = document.createElement("div");
        el2a.classList.add("row");

        var el3a = document.createElement("div");
        el3a.classList.add("col-md-12");

        // Create input field
        var el4a = document.createElement("input");
        el4a.type = "text";
        el4a.placeholder = "Enter Value";
        el4a.classList.add("form-control", "newcriteriaValue");

        // Append input to structure
        el3a.appendChild(el4a);
        el2a.appendChild(el3a);
        el1a.appendChild(el2a);
        clonable1b.appendChild(el1a);

        // Create third set of elements
        var el1c = document.createElement("div");
        el1c.classList.add("col-sm-3");

        var el2c = document.createElement("div");
        el2c.classList.add("row");

        var el3c = document.createElement("div");
        el3c.classList.add("col-md-12");

        // Create another select with options
        var el4c = document.createElement("select");
        el4c.classList.add("form-control", "m-b", "newcriteriaOperand");

        var el4ci = document.createElement("option");
        el4ci.value = 0;
        el4ci.textContent = "Select Combination";

        var el4cii = document.createElement("option");
        el4cii.value = 2;
        el4cii.textContent = "AND";

        var el4ciii = document.createElement("option");
        el4ciii.value = 3;
        el4ciii.textContent = "OR";

        // Append options to select
        el4c.appendChild(el4ci);
        el4c.appendChild(el4cii);
        el4c.appendChild(el4ciii);

        // Append to the structure
        el3c.appendChild(el4c);
        el2c.appendChild(el3c);
        el1c.appendChild(el2c);
        clonable1b.appendChild(el1c);

        // Create hr-line-dashed div
        clonable2 = document.createElement("div");
        clonable2.classList.add("hr-line-dashed");

        // Append to modal body
        document.getElementsByClassName("modalbody")[0].appendChild(clonable1b);
        document.getElementsByClassName("modalbody")[0].appendChild(clonable2);

    }

    function admissionsCategory() {
       /* let <div> = `*/

        clonable= document.createElement("div");
        clonable.classList.add("row", "clonableCategory");

        // Create first set of elements
        var el1 = document.createElement("div");
        el1.classList.add("col-sm-3");

        var el2 = document.createElement("div");
        el2.classList.add("row");

        var el3 = document.createElement("div");
        el3.classList.add("col-md-12");

        var el4 = document.createElement("input");
        el4.type = "number";
        el4.placeholder = "Price per Form";
        el4.classList.add("form-control", "newFormPricetxt");

        el3.appendChild(el4);
        el2.appendChild(el3);
        el1.appendChild(el2);
        clonable.appendChild(el1);

        // Create second set of elements
        var el1a = document.createElement("div");
        el1a.classList.add("col-sm-3");

        var el2a = document.createElement("div");
        el2a.classList.add("row");

        var el3a = document.createElement("div");
        el3a.classList.add("col-md-12");

        var el4a = document.createElement("input");
        el4a.type = "text";
        el4a.placeholder = "Form Type";
        el4a.classList.add("form-control", "newFormTypetxt");

        el3a.appendChild(el4a);
        el2a.appendChild(el3a);
        el1a.appendChild(el2a);
        clonable.appendChild(el1a);

        // Create third set of elements
        var el1b = document.createElement("div");
        el1b.classList.add("col-sm-3");

        var el2b = document.createElement("div");
        el2b.classList.add("row");

        var el3b = document.createElement("div");
        el3b.classList.add("col-md-12");

        var el4b = document.createElement("input");
        el4b.type = "text";
        el4b.placeholder = "Issued Form Quantity";
        el4b.classList.add("form-control", "newFormQNTtxt");

        el3b.appendChild(el4b);
        el2b.appendChild(el3b);
        el1b.appendChild(el2b);
        clonable.appendChild(el1b);

        // Create fourth set of elements with select options
        var el1c = document.createElement("div");
        el1c.classList.add("col-sm-3");

        var el2c = document.createElement("div");
        el2c.classList.add("row");

        var el3c = document.createElement("div");
        el3c.classList.add("col-md-12");

        var el4c = document.createElement("select");
        el4c.classList.add("form-control", "m-b", "newFormPaymentOptiontxt");

        var el4ci = document.createElement("option");
        el4ci.value = 0;
        el4ci.textContent = "Select Payment form";

        var el4cii = document.createElement("option");
        el4cii.value = 2;
        el4cii.textContent = "Online";

        var el4ciii = document.createElement("option");
        el4ciii.value = 3;
        el4ciii.textContent = "On-premis";

        el4c.appendChild(el4ci);
        el4c.appendChild(el4cii);
        el4c.appendChild(el4ciii);

        el3c.appendChild(el4c);
        el2c.appendChild(el3c);
        el1c.appendChild(el2c);
        clonable.appendChild(el1c);

        // Create fifth set of elements with datepicker
        var el1d = document.createElement("div");
        el1d.classList.add("col-sm-3");

        var el2d = document.createElement("div");
        el2d.classList.add("row");

        var el3d = document.createElement("div");
        el3d.classList.add("col-md-12", "datePicker1_div");

        var el4d = document.createElement("input");
        el4d.type = "text";
        el4d.placeholder = "Form Sales Period";
        el4d.classList.add("form-control", "datetimepicker", "salesPeriodDatePicker");

        el3d.appendChild(el4d);
        el2d.appendChild(el3d);
        el1d.appendChild(el2d);
        clonable.appendChild(el1d);

        // Create sixth set of elements
        var el1e = document.createElement("div");
        el1e.classList.add("col-sm-3");

        var el2e = document.createElement("div");
        el2e.classList.add("row");

        var el3e = document.createElement("div");
        el3e.classList.add("col-md-12");

        var el4e = document.createElement("input");
        el4e.type = "number";
        el4e.placeholder = "Appointments per Day";
        el4e.classList.add("form-control", "appointmentPerDay");

        el3e.appendChild(el4e);
        el2e.appendChild(el3e);
        el1e.appendChild(el2e);
        clonable.appendChild(el1e);

        // Create seventh set of elements with interview period
        var el1d2 = document.createElement("div");
        el1d2.classList.add("col-sm-3");

        var el2d2 = document.createElement("div");
        el2d2.classList.add("row");

        var el3d2 = document.createElement("div");
        el3d2.classList.add("col-md-12", "datePicker1_div2");

        var el4d2 = document.createElement("input");
        el4d2.type = "text";
        el4d2.placeholder = "Interview Period";
        el4d2.classList.add("form-control", "datetimepicker2", "interviewPeriodDatePicker");

        el3d2.appendChild(el4d2);
        el2d2.appendChild(el3d2);
        el1d2.appendChild(el2d2);
        clonable.appendChild(el1d2);

        // Create a div for hr-line-dashed
        clonable2 = document.createElement("div");
        clonable2.classList.add("hr-line-dashed");

        // Append everything to the modal body
        document.getElementsByClassName("modalbody")[1].appendChild(clonable);
        document.getElementsByClassName("modalbody")[1].appendChild(clonable2);

    }

function daterangepicker(selector,divEl) {

    // Get today's date
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    const last7Days = new Date(today);
    last7Days.setDate(today.getDate() - 7);

    const last30Days = new Date(today);
    last30Days.setDate(today.getDate() - 30);

    const thisMonthStart = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastMonthStart = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const lastMonthEnd = new Date(today.getFullYear(), today.getMonth(), 0);


        $(selector).daterangepicker({
            "showDropdowns": true,
            "showWeekNumbers": true,
            "timePicker": true,
            "ranges": {
                "Today": [today, today],
                "Yesterday": [yesterday, yesterday],
                "Last 7 Days": [last7Days, today],
                "Last 30 Days": [last30Days, today],
                "This Month": [thisMonthStart, today],
                "Last Month": [lastMonthStart, lastMonthEnd]
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
            "parentEl": divEl,
            "startDate": today,
            "endDate": today,
            "opens": "center",
            "drops": "up"
        }, function(start, end, label) {
          console.log("New date range selected: " + start.format('YYYY-MM-DD') + " to " + end.format('YYYY-MM-DD') + " (predefined range: " + label + ")");
        });
}
