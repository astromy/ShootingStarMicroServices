$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
                data-target="#departmentModal">New Department</button>
                </div>
            </div>
    `

    //function institutionBuild() {
    let department = `
    
    
    <div class="content animate-panel" id="pagecontent">
    <div class="hpanel">

        <div class="panel-heading">
            <div class="panel-tools">
            </div>
            Departments
        </div>
        <div class="panel-body">
            <table id="departmentTable" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Number of Designations</th>
                </tr>
                </thead>
                <tbody id="departmentTableBody"></tbody>
            </table>

        </div>
    </div>
    

    
    <div class="modal fade hmodal-info" id="departmentModal" tabindex="-1" role="dialog"
    aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="color-line"></div>
            <div class="modal-header">
                <h4 class="modal-title">Add Department</h4>
                <small class="font-bold">A class group is a collection of classes eg. Creche, Nursery, KG, Lower Primary, Upper Primary etc.</small>
            </div>
            <div class="panel-body modalbody">
            <div class="form-group"><label class="col-sm-3 control-label">Group Name</label>

            <div class="col-sm-9">
                <div class="row">
                    <div class="col-md-12"><input type="text" placeholder="Enter Class Group Name" class="form-control newClassGrouptxt"></div>
                </div>
            </div>
        </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More</i></button>
                <button type="button" class="btn btn-default"
                    data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary">Save changes</button>
            </div>
        </div>
    </div>
</div>
</div>
    `




    document.getElementById("wrapper").innerHTML = header;
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', department);
    document.getElementsByClassName("test")[0].addEventListener("click", departmentInput);
    document.getElementById("modalopn").addEventListener("click", modalopn);

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_department.js");
    script14.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    departmentInput();
}

function departmentInput() {
    let div = `
    <div class="row"><label class="col-sm-3 control-label">Group Name</label>
        <div class="col-sm-9">
            <div class="row">
                <div class="col-md-12"><input type="text" placeholder="Enter Class Group Name" class="form-control newClassGrouptxt"></div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[0].insertAdjacentHTML('beforeend', div);
}

