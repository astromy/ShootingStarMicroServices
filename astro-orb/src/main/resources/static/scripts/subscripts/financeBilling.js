
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
                        data-target="#newBillModal">New Billing</button>
                        </div>

                    <div id="hbreadcrumb" class="pull-right m-t-lg">
                        <ol class="hbreadcrumb breadcrumb">
                            <li>Finance</li>
                            <li class="active">
                                <span>Student Billing</span>
                            </li>
                        </ol>
                    </div>
                    <h2 class="font-light m-b-xs">
                        Student Billing
                    </h2>
                    <small>Used for Billing Students for a given academic period</small>


                    <div class="row">
                        <div class="col-lg-12 pull-left">
                            <div class="form-group col-lg-2 ">
                                <select class="form-control classGroupSelect">
                                    <option>Select Class Group</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-2 ">
                                <select class="form-control classSelect">
                                    <option>Select Class</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-2">
                                <select class="form-control termSelect">
                                    <option value="0">Select Term</option>
                                    <option value="First Term">First Term</option>
                                    <option value="Second Term">Second Term</option>
                                    <option value="Third Term">Third Term</option>
                                 </select>
                            </div>
                            <div class="form-group col-lg-2">
                                <select class="form-control academicYearSelect" >
                                    <option>Select Academic Year</option>
                                </select>
                            </div>
                            <div class="form-group col-sm-2" style="float: right;">
                                 <div id="billingFetch" class="">
                                      <button class="btn btn-success col-lg-12 form-control" type="button" id="submitBtn">Submit</button>
                                  </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>


               <div class="content animate-panel" id="pagecontent">
                 <div class="hpanel">
                    <div class="panel-heading">
                        <div class="panel-tools">
                            <a class="showhide"><i class="fa fa-chevron-up"></i></a>
                            <a class="closebox"><i class="fa fa-times"></i></a>
                        </div>
                        Subject Scores
                    </div>
                    <div class="panel-body">
                        <table id="billingTable" class="table table-striped table-bordered table-hover" width="100%">
                            <thead>
                                <tr>
                                    <th hidden>id</th>
                                    <th>Student Id</th>
                                    <th>Class</th>
                                    <th>Term</th>
                                    <th>Amount Due</th>
                                    <th>Amount Paid</th>
                                    <th>Amount Balance</th>
                                </tr>
                            </thead>
                            <tbody id="billingTableBody"></tbody>
                        </table>
                    </div>
                </div>
             </div>
        
        
        <div class="modal fade hmodal-info" id="newBillModal" tabindex="-1" role="dialog"aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="color-line"></div>
                        <div class="modal-header">
                            <h4 class="modal-title">Add Bill Item</h4>
                            <small class="font-bold"> Create new Bill Item</small>
                        </div>
                        <div class="panel-body modalbody" style="border-bottom: 1px solid #a8bede;">

                        </div>
                       
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default dismissBill" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary saveBilling">Save changes</button>
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
 /*   document.getElementsByClassName("test")[0].addEventListener("click", billingIndut);*/
    document.getElementById("modalopn").addEventListener("click", modalopn);

    constructExistingBillings();


    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_financeBilling.js");
    document.getElementsByTagName("body")[0].appendChild(script14);
});



//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    billingIndut();
}

function billingIndut() {
// Clone the elements with their child nodes (true means deep clone)
    const clone1 = clonable.cloneNode(true);
    const clone2 = clonable2.cloneNode(true);

    // Append the cloned elements to the modal body
    const modalBody = document.getElementsByClassName("modalbody")[0];
    modalBody.appendChild(clone1);
    modalBody.appendChild(tabs);
    modalBody.appendChild(clone2);


    document.querySelector("#billingClassOptions").addEventListener("change", function() {
      var selectedValue = this.value;

        const el=document.querySelector('.tabs');
        if (selectedValue !== "Select Class") {
        fetchStudentsByClass(selectedValue);
        el.style.pointerEvents = 'auto'; // Re-enable all clicks/interactions
        el.style.opacity = '1';          // Restore original appearance (optional)
        }else{
        el.style.pointerEvents = 'none'; // Disable all clicks/interactions
        el.style.opacity = '0.5';        // Make it appear disabled (optional)
        }
    });

    document.querySelector(".dismissBill").addEventListener("click", function() {

         const mainParent=document.querySelector('.parent');
            const tabControls= mainParent.querySelectorAll('li');
            tabControls.forEach(element => {
                    element.style.pointerEvents = 'auto'; // Re-enable all clicks/interactions
                    element.style.opacity = '1';
            });
            const checks=document.querySelectorAll('.i-checks').forEach(check => {
                       check.checked = false;
               });

    });

  document.querySelectorAll(".gBill").forEach(function(gBillElement) {
      gBillElement.addEventListener("change", function() {

                const mainParent=this.closest('.parent');
                const tabControls= mainParent.querySelectorAll('li:not(.active)');

                tabControls.forEach(element => {
                        element.style.pointerEvents = 'none'; // Disable all clicks/interactions
                        element.style.opacity = '0.5';
                });

                buildGeneralBill(this);
      });
  });

  document.querySelectorAll(".sBill").forEach(function(sBillElement) {
      sBillElement.addEventListener("change", function() {

                const mainParent=this.closest('.parent');
                const tabControls= mainParent.querySelectorAll('li:not(.active)');

                tabControls.forEach(element => {
                        element.style.pointerEvents = 'none'; // Disable all clicks/interactions
                        element.style.opacity = '0.5';
                });
      });
  });
}


function constructExistingBillings(){

    clonable=document.createElement("div");
    clonable.setAttribute("class", "row clonable");

    var el1=document.createElement("div");
    el1.setAttribute("class", "col-sm-3");

    var el2=document.createElement("div");
    el2.setAttribute("class", "row");

    var el3=document.createElement("div");
    el3.setAttribute("class", "col-md-12");

    var el4=document.createElement("select");
    el4.setAttribute("class", "form-control m-b billingClassOptions");
    el4.setAttribute("name", "billingClasses");
    el4.setAttribute("id", "billingClassOptions");

    var el5=document.createElement("option");
    el5.innerHTML=("Select Class");
    el4.appendChild(el5);   // Append default option to select

/*
    el4.addEventListener("change", function() {
      var selectedValue = this.value;

        if (selectedValue !== "Select Class") {
        const el=document.querySelector('.tabs');
        el.style.pointerEvents = 'auto'; // Re-enable all clicks/interactions
        el.style.opacity = '1';          // Restore original appearance (optional)
        }
    });*/

        el3.appendChild(el4);   // Append select to col-md-12
        el2.appendChild(el3);   // Append col-md-12 to inner row
        el1.appendChild(el2);   // Append inner row to col-sm-6
        clonable.appendChild(el1); // Append col-sm-6 to outer row (clonable)

    var ell1=document.createElement("div");
    ell1.setAttribute("class", "col-sm-3");

    var ell2=document.createElement("div");
    ell2.setAttribute("class", "row");

    var ell3=document.createElement("div");
    ell3.setAttribute("class", "col-md-12");

    var ell4=document.createElement("select");
    ell4.setAttribute("class", "form-control m-b termOptions");
    ell4.setAttribute("name", "terms");
    ell4.setAttribute("id", "termOptions");

    var ell5=document.createElement("option");
    ell5.innerHTML=("Select Term");
    var ell5i=document.createElement("option");
    ell5i.innerHTML=("First Term");
    var ell5ii=document.createElement("option");
    ell5ii.innerHTML=("Second Term");
    var ell5iii=document.createElement("option");
    ell5iii.innerHTML=("Third Term");

    ell4.appendChild(ell5);   // Append default option to select
    ell4.appendChild(ell5i);   // Append default option to select
    ell4.appendChild(ell5ii);   // Append default option to select
    ell4.appendChild(ell5iii);   // Append default option to select
        ell3.appendChild(ell4);   // Append select to col-md-12
        ell2.appendChild(ell3);   // Append col-md-12 to inner row
        ell1.appendChild(ell2);   // Append inner row to col-sm-6
        clonable.appendChild(ell1); // Append col-sm-6 to outer row (clonable)

createGeneralTab();

clonable2=document.createElement("div");
clonable2.setAttribute("class", "hr-line-dashed");

}

function createGeneralTab(){

  // Create the parent row div
  var rowDiv = document.createElement('div');
  rowDiv.classList.add('row', 'tabs');
  rowDiv.style.pointerEvents = 'none'; // Disable all clicks/interactions
  rowDiv.style.opacity = '0.5';        // Make it appear disabled (optional)

  // Create the col-lg-12 div
  const colDiv = document.createElement('div');
  colDiv.classList.add('col-lg-12');

  // Create the hpanel div
  const hpanelDiv = document.createElement('div');
  hpanelDiv.classList.add('hpanel','parent');

  // Create the nav tabs list
  const ulTabs = document.createElement('ul');
  ulTabs.classList.add('nav', 'nav-tabs');

  // Tab items
  const tabItems = [
    { id: 'tab-1', text: 'General Billing', active: true },
    { id: 'tab-2', text: 'Inclusive Billing', active: false },
    { id: 'tab-3', text: 'Exclusive Billing', active: false }
  ];

  // Create li elements for each tab
  tabItems.forEach(tab => {
    const li = document.createElement('li');
    if (tab.active) li.classList.add('active');

    const a = document.createElement('a');
    a.setAttribute('data-toggle', 'tab');
    a.setAttribute('href', `#${tab.id}`);
    a.textContent = tab.text;

    li.appendChild(a);
    ulTabs.appendChild(li);
  });

  // Create the tab-content div
  const tabContentDiv = document.createElement('div');
  tabContentDiv.classList.add('tab-content');

  // Create the tab-pane divs
  tabItems.forEach(tab => {
    const tabPaneDiv = document.createElement('div');
    tabPaneDiv.id = tab.id;
    tabPaneDiv.classList.add('tab-pane');
    if (tab.active) tabPaneDiv.classList.add('active');

    // Create panel-body div for each tab
    const panelBodyDiv = document.createElement('div');
    panelBodyDiv.classList.add('panel-body');

    // Add content to the "General Billing" tab (tab-1)


      const billColDiv = document.createElement('div');
      billColDiv.classList.add('col-lg-9');

      const billHpanelDiv = document.createElement('div');
      billHpanelDiv.classList.add('hpanel');

      const panelHeadingDiv = document.createElement('div');
      panelHeadingDiv.classList.add('panel-heading');
      panelHeadingDiv.textContent = 'Bill Items';

      const panelBodyListDiv = document.createElement('div');
      panelBodyListDiv.classList.add('panel-body', 'list');

      const tableResponsiveDiv = document.createElement('div');
      tableResponsiveDiv.classList.add('table-responsive', 'project-list');
      tableResponsiveDiv.style.maxHeight = '300px';  // Set max-height
      tableResponsiveDiv.style.overflowY = 'auto';   // Enable vertical scrolling

      const table = document.createElement('table');
      table.classList.add('table', 'table-striped');

      const thead = document.createElement('thead');
      const tr = document.createElement('tr');
      const th = document.createElement('th');
      th.setAttribute('colspan', '2');
      th.textContent = 'Bill';
      tr.appendChild(th);
      thead.appendChild(tr);

      const tbody = document.createElement('tbody');
      tbody.id = 'billItems'; // This will hold dynamic content

      table.appendChild(thead);
      table.appendChild(tbody);

      tableResponsiveDiv.appendChild(table);
      panelBodyListDiv.appendChild(tableResponsiveDiv);
      billHpanelDiv.appendChild(panelHeadingDiv);
      billHpanelDiv.appendChild(panelBodyListDiv);
      billColDiv.appendChild(billHpanelDiv);
    if (tab.id === 'tab-1') {
      panelBodyDiv.appendChild(billColDiv);
    tabPaneDiv.appendChild(panelBodyDiv);
    }else{
    const dvT=createSpecificTab(billColDiv,panelBodyDiv)
    tabPaneDiv.appendChild(dvT);
    }

    tabPaneDiv.appendChild(panelBodyDiv);
    tabContentDiv.appendChild(tabPaneDiv);
  });

  // Append all elements together
  hpanelDiv.appendChild(ulTabs);
  hpanelDiv.appendChild(tabContentDiv);
  colDiv.appendChild(hpanelDiv);
  rowDiv.appendChild(colDiv);

tabs=rowDiv

}


function createSpecificTab(innerDiv,me){
specificTab=innerDiv.cloneNode(true); ;
specificTab.classList.remove("col-lg-9");
specificTab.classList.add("col-lg-6");
specificTab.querySelector('#billItems').classList.add("specificBills");
specificTab.querySelector('#billItems').removeAttribute("id");

me.appendChild(specificTab);

const cl2=innerDiv.cloneNode(true); ;
cl2.classList.remove("col-lg-9");
cl2.classList.add("col-lg-6");
cl2.classList.add("students");
cl2.querySelector('.panel-heading').textContent = 'Student List';
 const t2=cl2.querySelector('.table-responsive');


    const labelDiv = document.createElement('div');
    const label = document.createElement('label');
        label.style.padding = '8px';
        label.style.marginBottom = '0px';

    label.textContent = 'Students';
    labelDiv.appendChild(label);
    cl2.querySelector('.panel-body').insertBefore(labelDiv,t2);

    const t1 = cl2.querySelector('table');
    t1.remove();

    // Create the outer div with class 'row'
    const rowDiv = document.createElement('div');
    rowDiv.classList.add("studentsList");
    cl2.querySelector('.table-responsive').appendChild(rowDiv);
    cl2.querySelector('.table-responsive').style.maxHeight= "265px";
   /* if (t1) {
        t1.classList.add("studentsList");
        t1.removeAttribute("id");
    }*/

    specificStudentsTab = cl2;
    me.appendChild(specificStudentsTab);

    return me;
}

//Creates a students object element
function createSpecificTabStudentList(){

}