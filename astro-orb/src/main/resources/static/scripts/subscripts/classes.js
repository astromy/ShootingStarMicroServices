
$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                    <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal" data-target="#myclassesModal">New Class</button>
                </div>
            </div>
    `

    let classes = `
        
            <div class="content animate-panel" id="pagecontent">
                <div class="hpanel">
                    <div class="panel-heading">
                        <div class="panel-tools">
                        </div>
                        Class Groups
                    </div>
                    <div class="panel-body">
                        <table id="classTable" class="table table-striped table-bordered table-hover" width="100%">
                            <thead>
                                <tr>
                                    <th hidden>id</th>
                                    <th>Class Name</th>
                                    <th>Class Group</th>
                                </tr>
                            </thead>
                            <tbody id="classesTableBody"></tbody>
                        </table>
                    </div>
                </div>
        

        
                <div class="modal fade hmodal-info" id="myclassesModal" tabindex="-1" role="dialog" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="color-line"></div>
                            <div class="modal-header">
                                <h4 class="modal-title">Add Class</h4>
                                <small class="font-bold">The Clases are the individual levels a student progresses through eg. Creche, Nursery 1, KG 2 etc.</small>
                            </div>
                            <div class="panel-body modalbody">
                                <div class="form-group"><label class="col-sm-3 control-label">Group Name</label>
                                    <div class="col-sm-9">
                                        <div class="row">
                                            <div class="col-md-12"><input type="text" placeholder="Enter Class Name" class="form-control newclassestxt"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More</i></button>
                                <button type="button" class="btn btn-default dismissClass" data-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary saveClass">Save changes</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `

    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', classes);
    document.getElementsByClassName("test")[0].addEventListener("click", classesIndut);
    document.getElementById("modalopn").addEventListener("click", modalopn);
    createModalElements();
    document.getElementsByClassName("modalbody")[0].appendChild(clonable);
    document.getElementsByClassName("modalbody")[0].appendChild(clonable2);

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_classes.js");
    script14.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    classesIndut();
}

function classesIndut() {
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
clonable.setAttribute("class", "row");

var el1=document.createElement("div");
el1.setAttribute("class", "col-sm-6");

var el2=document.createElement("div");
el2.setAttribute("class", "row");

var el3=document.createElement("div");
el3.setAttribute("class", "col-md-12");

var el4=document.createElement("select");
el4.setAttribute("class", "form-control m-b");
el4.setAttribute("name", "classes");
el4.setAttribute("id", "classOptions");

var el5=document.createElement("option");
el5.innerHTML=("Select Class Group");

el4.appendChild(el5);   // Append default option to select
    el3.appendChild(el4);   // Append select to col-md-12
    el2.appendChild(el3);   // Append col-md-12 to inner row
    el1.appendChild(el2);   // Append inner row to col-sm-6
    clonable.appendChild(el1); // Append col-sm-6 to outer row (clonable)



var ela=document.createElement("div");
ela.setAttribute("class", "col-sm-6");

var elb=document.createElement("div");
elb.setAttribute("class", "row");

var elc=document.createElement("div");
elc.setAttribute("class", "col-md-12");

var eld=document.createElement("input");
eld.setAttribute("class", "form-control newclassestxt");
eld.setAttribute("type", "text");
eld.setAttribute("placeholder", "Enter Class Name");


    elc.appendChild(eld);   // Append input to col-md-12
    elb.appendChild(elc);   // Append col-md-12 to inner row
    ela.appendChild(elb);   // Append inner row to col-sm-6
    clonable.appendChild(ela); // Append col-sm-6 to outer row (clonable)

clonable2=document.createElement("div");
clonable2.setAttribute("class", "hr-line-dashed");
}


/*$(function () {

    // Initialize Example 1
    $('#classTable').dataTable({
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
