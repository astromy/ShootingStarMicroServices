$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
                data-target="#myclassGroupModal">New Class Group</button>
                </div>
            </div>
    `

    //function institutionBuild() {
    let classgroup = `
    
    
    <div class="content animate-panel" id="pagecontent">
    <div class="hpanel">

        <div class="panel-heading">
            <div class="panel-tools">
                <a class="showhide"><i class="fa fa-chevron-up"></i></a>
                <a class="closebox"><i class="fa fa-times"></i></a>
            </div>
            Class Groups
        </div>
        <div class="panel-body">
            <table id="example1" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Position</th>
                    <th>Office</th>
                    <th width="15%">Age</th>
                    <th width="15%">Start date</th>
                    <th width="15%">Salary</th>
                </tr>
                </thead>
            </table>

        </div>
    </div>
    

    
    <div class="modal fade hmodal-info" id="myclassGroupModal" tabindex="-1" role="dialog"
    aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="color-line"></div>
            <div class="modal-header">
                <h4 class="modal-title">Add Class Group</h4>
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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', classgroup);
    document.getElementsByClassName("test")[0].addEventListener("click", classgroupIndut);
    document.getElementById("modalopn").addEventListener("click", modalopn);

    /*var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/preorder.js");
    document.getElementsByTagName("body")[0].appendChild(script14);*/
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    classgroupIndut();
}

function classgroupIndut() {
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


$(function () {

    // Initialize Example 1
    $('#example1').dataTable({
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
