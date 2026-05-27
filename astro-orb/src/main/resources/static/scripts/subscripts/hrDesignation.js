$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
                data-target="#mydesignationModal">New designation Settings</button>
                </div>
            </div>
    `

    //function institutionBuild() {
    let designation = `

    <div class="content animate-panel" id="pagecontent">
        <div class="hpanel">

            <div class="panel-heading">
                <div class="panel-tools">
                </div>
                designation
            </div>
            <div class="panel-body">
                <table id="designationTable" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                    <tr>
                        <th class="col-sm-4">Name</th>
                        <th class="col-sm-3">Code</th>
                        <th class="col-sm-2">Total Slots</th>
                        <th class="col-sm-2">Available Slots</th>
                        <th class="col-sm-1">Open Advert</th>
                    </tr>
                    </thead>
                    <tbody id="designationTableBody"></tbody>
                </table>

            </div>
        </div>
    

    
        <div class="modal fade hmodal-info" id="mydesignationModal" tabindex="-1" role="dialog"aria-hidden="true">
            <div class="modal-dialog modal-lg" style="margin: 100px auto">
                <div class="modal-content">
                    <div class="color-line"></div>
                        <div class="modal-header">
                            <h4 class="modal-title">Add Designation</h4>
                            <small class="font-bold">Designation is the office held by an individual in an organization </small>
                        </div>
                        <div class="panel-body modalbody" style="border-bottom: 1px solid #a8bede;">
                            <div class="form-group">
                                <div class="col-sm-9">
                                    <div class="row">
                                        <div class="col-md-12"><input type="text" placeholder="Enter designation Name" class="form-control newDesignation"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="panel-body modalbody">
                            <div class="form-group">
                                <div class="col-sm-9">
                                    <div class="row">
                                        <div class="col-md-12"><input type="text" placeholder="Enter designation Name" class="form-control newdesignationtxt"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More Job Description</i></button>
                            <button type="button" class="btn btn-default dismissDesignation" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary saveDesignation">Save changes</button>
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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', designation);
    //document.getElementsByClassName("test")[0].addEventListener("click", designationSettingsCriteria);
    document.getElementsByClassName("test")[0].addEventListener("click", designationSettingsJobDescription);
    document.getElementById("modalopn").addEventListener("click", modalopn);

    designationCriteria();
    designationJobDescription();

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_hrDesignation.js");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
   document.getElementsByClassName("modalbody")[0].innerHTML="";
   document.getElementsByClassName("modalbody")[1].innerHTML="";
   designationSettingsCriteria();
   designationSettingsJobDescription();

}




function designationSettingsCriteria() {
// Clone the elements with their child nodes (true means deep clone)
    const clone1a = clonable1b.cloneNode(true);
    const clone2 = clonable2.cloneNode(true);

    // Append the cloned elements to the modal body
    const modalBody = document.getElementsByClassName("modalbody")[0];
    modalBody.appendChild(clone1a);
    modalBody.appendChild(clone2);
}

function designationSettingsJobDescription() {
// Clone the elements with their child nodes (true means deep clone)
    const clone1b = clonable.cloneNode(true);
    const clone2 = clonable2.cloneNode(true);

    const modalBody2 = document.getElementsByClassName("modalbody")[1];
    modalBody2.appendChild(clone1b);
    modalBody2.appendChild(clone2);
}


    function designationCriteria() {
        clonable1b=document.createElement("div");
        clonable1b.classList.add("row", "clonableCriteria");

        // Create div elements
        var ela1 = document.createElement("div");
        ela1.classList.add("col-sm-3");

        var ela2 = document.createElement("div");
        ela2.classList.add("row");

        var ela3 = document.createElement("div");
        ela3.classList.add("col-md-12");

        // Create select and options
        var ela4 = document.createElement("input");
        ela4.classList.add("form-control", "newDesignationName");
        ela4.type = "text";
        ela4.placeholder = "Enter Designation Name";

        // Build the structure
        ela3.appendChild(ela4);
        ela2.appendChild(ela3);
        ela1.appendChild(ela2);
        clonable1b.appendChild(ela1);

        // Create second set of elements
        var el1a = document.createElement("div");
        el1a.classList.add("col-sm-3");

        var el2a = document.createElement("div");
        el2a.classList.add("row");

        var el3a = document.createElement("div");
        el3a.classList.add("col-md-12");

        // Create input field
        var el4a = document.createElement("input");
        el4a.type = "text";
        el4a.placeholder = "Designation Code";
        el4a.classList.add("form-control", "newDesignationCode");

        // Append input to structure
        el3a.appendChild(el4a);
        el2a.appendChild(el3a);
        el1a.appendChild(el2a);
        clonable1b.appendChild(el1a);



        // Create third set of elements
        var el1c = document.createElement("div");
        el1c.classList.add("col-sm-2");

        var el2c = document.createElement("div");
        el2c.classList.add("row");

        var el3c = document.createElement("div");
        el3c.classList.add("col-md-12");

        // Create another select with options
        var el4c = document.createElement("select");
        el4c.classList.add("form-control", "m-b", "newDesignationDepartment");

        var el4ci = document.createElement("option");
        el4ci.value = 0;
        el4ci.textContent = "Department";

        // Append options to select
        el4c.appendChild(el4ci);

        // Append to the structure
        el3c.appendChild(el4c);
        el2c.appendChild(el3c);
        el1c.appendChild(el2c);
        clonable1b.appendChild(el1c);


        // Create second set of elements
        var tsd1 = document.createElement("div");
        tsd1.classList.add("col-sm-2");

        var tsd2 = document.createElement("div");
        tsd2.classList.add("row");

        var tsd3 = document.createElement("div");
        tsd3.classList.add("col-md-12");

        // Create input field
        var tsi1 = document.createElement("input");
        tsi1.type = "text";
        tsi1.placeholder = "Total Slots";
        tsi1.classList.add("form-control", "newDesignationTotalSlots");

        // Append input to structure
        tsd3.appendChild(tsi1);
        tsd2.appendChild(tsd3);
        tsd1.appendChild(tsd2);
        clonable1b.appendChild(tsd1);


        // Create second set of elements
        var asd1 = document.createElement("div");
        asd1.classList.add("col-sm-2");

        var asd2 = document.createElement("div");
        asd2.classList.add("row");

        var asd3 = document.createElement("div");
        asd3.classList.add("col-md-12");

        // Create input field
        var asi1 = document.createElement("input");
        asi1.type = "text";
        asi1.placeholder = "Available Slots";
        asi1.classList.add("form-control", "newDesignationAvailableSlots");

        // Append input to structure
        asd3.appendChild(asi1);
        asd2.appendChild(asd3);
        asd1.appendChild(asd2);
        clonable1b.appendChild(asd1);



        // Create hr-line-dashed div
        clonable2 = document.createElement("div");
        clonable2.classList.add("hr-line-dashed");

        // Append to modal body
        document.getElementsByClassName("modalbody")[0].appendChild(clonable1b);
        document.getElementsByClassName("modalbody")[0].appendChild(clonable2);

    }

    function designationJobDescription() {
       /* let <div> = `*/

        clonable= document.createElement("div");
        clonable.classList.add("row", "clonableJobDescription");

        // Create first set of elements
        var el1 = document.createElement("div");
        el1.classList.add("col-sm-12");

        var el2 = document.createElement("div");
        el2.classList.add("row");

        var el3 = document.createElement("div");
        el3.classList.add("col-md-12");

        var el4 = document.createElement("input");
        el4.type = "text";
        el4.placeholder = "Enter Job Description";
        el4.classList.add("form-control", "newJobDescriptionTxt");

        el3.appendChild(el4);
        el2.appendChild(el3);
        el1.appendChild(el2);
        clonable.appendChild(el1);

        // Create a div for hr-line-dashed
        clonable2 = document.createElement("div");
        clonable2.classList.add("hr-line-dashed");

        // Append everything to the modal body
        document.getElementsByClassName("modalbody")[1].appendChild(clonable);
        document.getElementsByClassName("modalbody")[1].appendChild(clonable2);

    }

