
$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                    <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal" data-target="#mysubjectModal">New Subject</button>
                </div>
            </div>
    `

    let subject = `

        <div class="content animate-panel" id="pagecontent">
            <div class="hpanel">
                <div class="panel-heading">
                    <div class="panel-tools">
                    </div>
                    Subjects
                </div>
                <div class="panel-body">
                    <table id="subjectTable" class="table table-striped table-bordered table-hover" width="100%">
                        <thead>
                            <tr>
                                <th hidden></th>
                                <th>Subject Name</th>
                                <th>Class Group</th>
                                <th>Subject Preference</th>
                            </tr>
                        </thead>
                        <tbody id="subjectTableBody"></tbody>
                    </table>
                </div>
            </div>
    

    
            <div class="modal fade hmodal-info" id="mysubjectModal" tabindex="-1" role="dialog" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="color-line"></div>
                        <div class="modal-header">
                            <h4 class="modal-title">Add Subject</h4>
                            <small class="font-bold">Select a Class Group, and its corresponding Class then enter the name of the subject you wish to create.</small>
                        </div>
                        <div class="panel-body modalbody">
                            <div class="form-group"><label class="col-sm-3 control-label">Group Name</label>
                                <div class="col-sm-9">
                                    <div class="row">
                                        <div class="col-md-12"><input type="text" placeholder="Enter Subject Name" class="form-control newsubjecttxt"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More</i></button>
                            <button type="button" class="btn btn-default dismissSubject" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary saveSubjects">Save changes</button>
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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', subject);
    document.getElementsByClassName("test")[0].addEventListener("click", subjectIndut);
    document.getElementById("modalopn").addEventListener("click", modalopn);
    createModalElements();
    document.getElementsByClassName("modalbody")[0].appendChild(clonable);
    document.getElementsByClassName("modalbody")[0].appendChild(clonable2);

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_subjects.js");
    script14.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    subjectIndut();
}

function subjectIndut() {
// Clone the elements with their child nodes (true means deep clone)
    const clone1 = clonable.cloneNode(true);
    const clone2 = clonable2.cloneNode(true);

    // Append the cloned elements to the modal body
    const modalBody = document.getElementsByClassName("modalbody")[0];
    modalBody.appendChild(clone1);
    modalBody.appendChild(clone2);
}


function createModalElements(){
clonable=document.createElement("div");
clonable.setAttribute("class", "row clonable");

var el1=document.createElement("div");
el1.setAttribute("class", "col-sm-4");

var el2=document.createElement("div");
el2.setAttribute("class", "row");

var el3=document.createElement("div");
el3.setAttribute("class", "col-md-12");

var el4=document.createElement("select");
el4.setAttribute("class", "form-control m-b classOption");
el4.setAttribute("name", "classesgroups");
el4.setAttribute("id", "classGroupOptions");

var el5=document.createElement("option");
el5.innerHTML=("Select Class Group");

el4.appendChild(el5);   // Append default option to select
    el3.appendChild(el4);   // Append select to col-md-12
    el2.appendChild(el3);   // Append col-md-12 to inner row
    el1.appendChild(el2);   // Append inner row to col-sm-4
    clonable.appendChild(el1); // Append col-sm-4 to outer row (clonable)



var ela=document.createElement("div");
ela.setAttribute("class", "col-sm-4");

var elb=document.createElement("div");
elb.setAttribute("class", "row");

var elc=document.createElement("div");
elc.setAttribute("class", "col-md-12");

var eld=document.createElement("input");
eld.setAttribute("class", "form-control newSubjectTxt");
eld.setAttribute("type", "text");
eld.setAttribute("placeholder", "Enter Subject Name");


    elc.appendChild(eld);   // Append input to col-md-12
    elb.appendChild(elc);   // Append col-md-12 to inner row
    ela.appendChild(elb);   // Append inner row to col-sm-4
    clonable.appendChild(ela); // Append col-sm-4 to outer row (clonable)




var eli=document.createElement("div");
eli.setAttribute("class", "col-sm-4");

var elii=document.createElement("div");
elii.setAttribute("class", "row");

var eliii=document.createElement("div");
eliii.setAttribute("class", "col-md-12");

var eliv=document.createElement("input");
eliv.setAttribute("class", "form-control subjectPref");
eliv.setAttribute("type", "number");
eliv.setAttribute("placeholder", "Enter Subject Preference");


    eliii.appendChild(eliv);   // Append input to col-md-12
    elii.appendChild(eliii);   // Append col-md-12 to inner row
    eli.appendChild(elii);   // Append inner row to col-sm-4
    clonable.appendChild(eli); // Append col-sm-4 to outer row (clonable)

clonable2=document.createElement("div");
clonable2.setAttribute("class", "hr-line-dashed");
}



/*$(function () {

    // Initialize Example 1
    $('#subjectTable').dataTable({
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
});*/

