$(function () {

    let header = `

        <div class="normalheader transition animated fadeIn">
            <div class="hpanel">
                <div class="panel-body">
                    <a class="small-header-action" href="#">
                        <div class="clip-header">
                            <i class="fa fa-arrow-up"></i>
                        </div>
                    </a>

                    <div id="hbreadcrumb" class="pull-right m-t-lg m-l-md">
                        <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
                        data-target="#newBillModal">New Bill Item</button>
                        </div>

                    <div id="hbreadcrumb" class="pull-right m-t-lg">
                        <ol class="hbreadcrumb breadcrumb">
                            <li>Finance</li>
                            <li class="active">
                                <span>Bill Creation</span>
                            </li>
                        </ol>
                    </div>
                    <h2 class="font-light m-b-xs">
                        Bill Creation
                    </h2>
                    <small>Used for Creating New Bill Items and Displays Existing Bill Items</small>
                </div>
            </div>
        </div>
        <div class="content animate-panel"></div>
        
        
        <div class="modal fade hmodal-info" id="newBillModal" tabindex="-1" role="dialog"aria-hidden="true">
            <div class="modal-dialog modal-lg" style="margin: 100px auto">
                <div class="modal-content">
                    <div class="color-line"></div>
                        <div class="modal-header">
                            <h4 class="modal-title">Add Bill Item</h4>
                            <small class="font-bold"> Create new Bill Item</small>
                        </div>
                        <div class="panel-body modalbody" style="border-bottom: 1px solid #a8bede;">

                        </div>
                       
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More Bill</i></button>
                            <button type="button" class="btn btn-default dismissBill" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary saveBill">Save changes</button>
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
    document.getElementsByClassName("test")[0].addEventListener("click", newBillItem);
    document.getElementById("modalopn").addEventListener("click", modalopn);

    constructExistingBills();


    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_financeBills.js");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
   document.getElementsByClassName("modalbody")[0].innerHTML="";
   newBillItem();
}


function newBillItem() {
// Clone the elements with their child nodes (true means deep clone)
    const clone1a = clonable1b.cloneNode(true);
    const clone2 = clonable2.cloneNode(true);

    // Append the cloned elements to the modal body
    const modalBody = document.getElementsByClassName("modalbody")[0];
    modalBody.appendChild(clone1a);
    modalBody.appendChild(clone2);
}



    function constructExistingBills() {
        clonable1b=document.createElement("div");
        clonable1b.classList.add("row", "clonableBill");

        // Create div elements
        var ela1 = document.createElement("div");
        ela1.classList.add("col-sm-3");

        var ela2 = document.createElement("div");
        ela2.classList.add("row");

        var ela3 = document.createElement("div");
        ela3.classList.add("col-md-12");

        // Create select and options
        var ela4 = document.createElement("input");
        ela4.type = "text";
        ela4.placeholder = "Bill Name";
        ela4.classList.add("form-control", "newBillName");

        // Build the structure
        ela3.appendChild(ela4);
        ela2.appendChild(ela3);
        ela1.appendChild(ela2);
        clonable1b.appendChild(ela1);

//--------------------------------------------------
        // Create Amount elements
        var el1a = document.createElement("div");
        el1a.classList.add("col-sm-2");

        var el2a = document.createElement("div");
        el2a.classList.add("row");

        var el3a = document.createElement("div");
        el3a.classList.add("col-md-12");

        // Create input field
        var el4a = document.createElement("input");
        el4a.type = "number";
        el4a.placeholder = "Bill Amount";
        el4a.classList.add("form-control", "newBillAmount");

        // Append input to structure
        el3a.appendChild(el4a);
        el2a.appendChild(el3a);
        el1a.appendChild(el2a);
        clonable1b.appendChild(el1a);
        
    //-----------------------------------------------------

        // Create Description  elements
        var el1ai = document.createElement("div");
        el1ai.classList.add("col-sm-4");

        var el2ai = document.createElement("div");
        el2ai.classList.add("row");

        var el3ai = document.createElement("div");
        el3ai.classList.add("col-md-12");

        // Create input field
        var el4ai = document.createElement("input");
        el4ai.type = "text";
        el4ai.placeholder = "Bill Description";
        el4ai.classList.add("form-control", "newBillDescription");

        // Append input to structure
        el3ai.appendChild(el4ai);
        el2ai.appendChild(el3ai);
        el1ai.appendChild(el2ai);
        clonable1b.appendChild(el1ai);
//-----------------------------------------------------


        // Create third set of elements
        var el1c = document.createElement("div");
        el1c.classList.add("col-sm-3");

        var el2c = document.createElement("div");
        el2c.classList.add("row");

        var el3c = document.createElement("div");
        el3c.classList.add("col-md-12");

        // Create another select with options
        var el4c = document.createElement("select");
        el4c.classList.add("form-control", "m-b", "newBillCategory");

        var el4ci = document.createElement("option");
        el4ci.value = 0;
        el4ci.textContent = "Select Category";

        var el4cii = document.createElement("option");
        el4cii.value = 2;
        el4cii.textContent = "General";

        var el4ciii = document.createElement("option");
        el4ciii.value = 3;
        el4ciii.textContent = "Specific";

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


window.createNewBillRow=function createNewBillRow(d){
let newRow=`
    <div class="row billRow">
        <div class="col-lg-3"  data-toggle="modal" data-target="#newBillModal" style="cursor:pointer">
            <div class="hpanel horange contact-panel billContent">
                <div class="panel-body">
                    <h3> ${d.bill_Name} </h3>
                    <div class="text-muted font-bold m-b-xs">Bill Description</div>
                    <p class="billDescription">
                        ${d.bill_Description}
                    </p>
                </div>
                <div class="panel-footer contact-footer">
                    <div class="row">
                        <div class="col-md-4 border-right"> <div class="contact-stat"><span>Amount: </span> <strong>${d.bill_Amount}</strong></div> </div>
                        <div class="col-md-4 border-right"> <div class="contact-stat"><span>Category: </span> <strong>${d.bill_Cat}</strong></div> </div>
                        <div class="col-md-4"> <div class="contact-stat"><span>Views: </span> <strong></strong></div> </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `
    /*document.getElementsByClassName("content animate-panel")[0].insertAdjacentHTML('beforeend',newRow);*/
    let ell0=document.getElementsByClassName("content animate-panel")
        ell0[ell0.length - 1].insertAdjacentHTML('beforeend',newRow);
}

window.addBillItem= function addBillItem(d){
let existingBill=`
        <div class="col-lg-3"  data-toggle="modal" data-target="#newBillModal" style="cursor:pointer">
            <div class="hpanel horange contact-panel billContent">
                <div class="panel-body">
                    <h3> ${d.bill_Name} </h3>
                    <div class="text-muted font-bold m-b-xs">Bill Description</div>
                    <p class="billDescription">
                        ${d.bill_Description}
                    </p>
                </div>
                <div class="panel-footer contact-footer">
                    <div class="row">
                        <div class="col-md-4 border-right"> <div class="contact-stat"><span>Amount: </span> <strong>${d.bill_Amount}</strong></div> </div>
                        <div class="col-md-4 border-right"> <div class="contact-stat"><span>Category: </span> <strong>${d.bill_Cat}</strong></div> </div>
                        <div class="col-md-4"> <div class="contact-stat"><span>Views: </span> <strong></strong></div> </div>
                    </div>
                </div>
            </div>
        </div>
`
let ell=document.getElementsByClassName("billRow")
    ell[ell.length - 1].insertAdjacentHTML('beforeend',existingBill);
}
