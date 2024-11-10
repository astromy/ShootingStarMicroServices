$(function () {

let onboarding = `
    <div class="content animate-panel" id="onboarding">

        <div class="row">
            <div class="col-lg-12">
                <div class="hpanel">
                    <div class="panel-body">

                        <!--<form name="simpleForm" novalidate="novalidate" id="simpleForms" role="form">-->

                        <div class="text-center m-b-md" id="wizardControls">

                            <a class="btn btn-primary wizardTabs" data-toggle="tab">Step 1 - Bio Data</a>
                            <a class="btn btn-default wizardTabs" data-toggle="tab">Step 2 - Dependant Data</a>
                            <a class="btn btn-default wizardTabs" data-toggle="tab">Step 3 - Academic Data</a>
                            <a class="btn btn-default wizardTabs" data-toggle="tab">Step 4 - Professional Data</a>
                            <a class="btn btn-default wizardTabs" data-toggle="tab">Step 5 - Professional Data</a>
                            <a class="btn btn-default wizardTabs" data-toggle="tab">Step 5 - Approve & Submit</a>

                        </div>

                        <div class="tab-content">
                            <div class="p-m tab-pane active">
                                <!-- <form name="simpleForm" id="simpleForms" action="">-->
                                <form id="staffForm" role="form">
                                    <div class="row">
                                        <div class="col-lg-3 text-center">
                                        </div>
                                        <div class="col-lg-9">
                                            <div class="row">
                                                <div class="form-group col-lg-6">
                                                    <label for="staffFName">First Names</label>
                                                    <input type="text" requ ired id="staffFName" class="form-control"
                                                           name="staffFName" minlength="3" placeholder="First Names">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="staffLName">Last Name</label>
                                                    <input type="text" requ ired id="staffLName" class="form-control"
                                                           name="staffLName" minlength="3" placeholder="Last Name">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="dob">Date of Birth</label>
                                                    <input type="date" id="dob" class="form-control"
                                                           name="dob" placeholder="Date of Birth" minlength="5">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="nationality">Nationality</label>
                                                    <select id="nationality" class="form-control"
                                                           name="nationality" placeholder="Select Nationality">
                                                           <option>Select Nationality</option>
                                                           <option>Ghana</option>
                                                           <option>Nigeria</option>
                                                           <option>Cote DIvore'</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="homeT">Home Town</label>
                                                    <input type="text" autocomplete="off" id="homeT"
                                                           class="form-control" placeholder="Home Town"
                                                           name="homeT" minlength="2">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="region">Residential Area</label>
                                                    <input type="text" autocomplete="off" required id="region"
                                                           class="form-control" placeholder="Residential Area" name="region"
                                                           minlength="3">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="contact">Contact</label>
                                                    <input type="text" required id="contact" class="form-control"
                                                           placeholder="Contact" name="contact" minlength="10">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="bContact">Backup Contact</label>
                                                    <input type="text" autocomplete="off" required id="bContact"
                                                           class="form-control" name="bContact"
                                                           placeholder="Backup Contact">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-3 text-center">
                                            <div>
                                                <label for="crest">Staff Picture</label>
                                            </div>
                                            <output class="imageOutput" name="crest" id="crest"
                                                    style="height: 220px; width:220px; border-radius: 10px;display: inline-block;"></output>
                                            <input class="imageInput" type="file"
                                                   style="width: 200px;padding: 12px;display: inline;"
                                                   accept="image/jpeg, image/png, image/jpg">
                                        </div>
                                        <div class="col-lg-9">
                                            <div class="row">

                                                <div class="form-group col-lg-6">
                                                    <label for="idType">ID Type</label>
                                                    <select type="text" required id="idType" class="form-control"
                                                           placeholder="Select ID Type" name="idType">
                                                           <option>Select ID Type</option>
                                                           <option>National ID Card</option>
                                                           <option>Driver License</option>
                                                           <option>Voter ID Card</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="idNumber">National ID</label>
                                                    <input type="text" id="idNumber" class="form-control"
                                                           placeholder="ID Number" name="idNumber">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="snnit">Snnit Number</label>
                                                    <input type="text" required id="snnit" class="form-control"
                                                           placeholder="Snnit Number" name="snnit"
                                                           minlength="5">
                                                </div>
                                                <div class="form-group col-lg-6">
                                                    <label for="maritalStatus">Marital Status</label>
                                                    <select type="text" required id="maritalStatus"
                                                           class="form-control" name="maritalStatus"
                                                           placeholder="Select Marital Status">
                                                           <option>Select Marital Status</option>
                                                           <option>Single</option>
                                                           <option>Married</option>
                                                           <option>Divorced</option>
                                                    </select>
                                                </div>

                                                <div class="form-group col-lg-6">
                                                    <label for="nos">Name of Spouse</label>
                                                    <input type="text" id="nos" class="form-control"
                                                           name="nos" placeholder="Name of Spouse">
                                                </div>

                                                <div class="form-group col-lg-6">
                                                    <label for="doe">Date of Employment</label>
                                                    <input type="date" required id="doe"
                                                           class="form-control" name="doe"
                                                           placeholder="Student Population">
                                                </div>

                                                <div class="form-group col-lg-6">
                                                    <label for="gender">Gender</label>
                                                    <select type="text" id="gender" class="form-control"
                                                           name="gender" placeholder="Select Gender">
                                                           <option>Select Gender</option>
                                                           <option>Male</option>
                                                           <option>Female</option>
                                                    </select>
                                                </div>

                                                <div class="form-group col-lg-6">
                                                    <label for="nok">Next of King</label>
                                                    <input type="text" id="nok" class="form-control"
                                                           name="nok" placeholder="Next of King">
                                                </div>
                                                <div>
                                                    <button id="instSubmit" class="btn btn-sm btn-primary m-t-n-xs"
                                                            style="visibility: hidden;" type="submit">
                                                        <strong>Submit</strong></button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                                <!-- <input id="fsubmit" type="submit" style="display: none;"></input>
                                 </form>-->

                                <div class="text-right m-t-xs">
                                    <a class="btn btn-default next">Next</a>
                                </div>

                            </div>

                            <div class="p-m tab-pane">

                                <div class="row">
                                    <div class="col-lg-12">
                                        <div class="row">

                                            <table id="staffTable_1" class="table table-striped table-bordered table-hover" width="100%">
                                                <thead>
                                                    <tr>
                                                        <th hidden></th>
                                                        <th >Staff ID</th>
                                                        <th >Staff Name</th>
                                                        <th >Staff Gender</th>
                                                        <th >Staff Contact</th>
                                                        <th >Date of Employment</th>
                                                        <th >Designation</th>
                                                        <th >Nationality</th>
                                                        <th >Snnit</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="staffTableBody_1"></tbody>

                                            </table>
                                        </div>
                                    </div>
                                </div>
                                <div class="text-right m-t-xs">
                                    <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                    <a class="btn btn-default next" href="#" title="dropdown">Next</a>
                                </div>

                            </div>

                            <div class="p-m tab-pane">

                                <div class="row">
                                    <div class="col-lg-12">
                                        <div class="row">

                                            <table id="staffTable_2" class="table table-striped table-bordered table-hover" width="100%">
                                                <thead>
                                                    <tr>
                                                        <th hidden></th>
                                                        <th >Staff ID</th>
                                                        <th >Staff Name</th>
                                                        <th >Staff Gender</th>
                                                        <th >Staff Contact</th>
                                                        <th >Date of Employment</th>
                                                        <th >Designation</th>
                                                        <th >Nationality</th>
                                                        <th >Snnit</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="staffTableBody_2"></tbody>

                                            </table>
                                        </div>
                                    </div>
                                </div>

                                <div class="text-right m-t-xs">
                                    <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                    <a class="btn btn-default next" href="#" title="dropdown">Next</a>
                                </div>
                            </div>

                            <div class="p-m tab-pane">

                                <div class="row">
                                    <div class="col-lg-12 text-center">
                                        <div class="row">

                                            <table id="staffTable_3" class="table table-striped table-bordered table-hover" width="100%">
                                                <thead>
                                                    <tr>
                                                        <th hidden></th>
                                                        <th >Staff ID</th>
                                                        <th >Staff Name</th>
                                                        <th >Staff Gender</th>
                                                        <th >Staff Contact</th>
                                                        <th >Date of Employment</th>
                                                        <th >Designation</th>
                                                        <th >Nationality</th>
                                                        <th >Snnit</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="staffTableBody_3"></tbody>

                                            </table>
                                        </div>
                                    </div>
                                </div>

                                <div class="text-right m-t-xs">
                                    <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                    <a class="btn btn-default next" href="#" title="dropdown">Next</a>
                                </div>
                            </div>

                            <div class="p-m tab-pane">

                                <div class="row">
                                    <div class="col-lg-12 text-center">
                                        <div class="row">

                                            <table id="staffTable_4" class="table table-striped table-bordered table-hover" width="100%">
                                                <thead>
                                                    <tr>
                                                        <th hidden></th>
                                                        <th >Staff ID</th>
                                                        <th >Staff Name</th>
                                                        <th >Staff Gender</th>
                                                        <th >Staff Contact</th>
                                                        <th >Date of Employment</th>
                                                        <th >Designation</th>
                                                        <th >Nationality</th>
                                                        <th >Snnit</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="staffTableBody_4"></tbody>

                                            </table>
                                        </div>
                                    </div>
                                </div>

                                <div class="text-right m-t-xs">
                                    <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                    <a class="btn btn-default next" href="#" title="dropdown">Next</a>
                                </div>
                            </div>

                            <div class="tab-pane">
                                <div class="row text-center m-t-lg m-b-lg">
                                    <div class="col-lg-12">
                                        <div class="row">

                                            <table id="staffTable_5" class="table table-striped table-bordered table-hover" width="100%">
                                                <thead>
                                                    <tr>
                                                        <th hidden></th>
                                                        <th >Staff ID</th>
                                                        <th >Staff Name</th>
                                                        <th >Staff Gender</th>
                                                        <th >Staff Contact</th>
                                                        <th >Date of Employment</th>
                                                        <th >Designation</th>
                                                        <th >Nationality</th>
                                                        <th >Snnit</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="staffTableBody_5"></tbody>

                                            </table>
                                        </div>
                                    </div>
                                    <div class="checkbox col-lg-12">
                                        <input type="checkbox" class="i-checks approveCheck" placeholder="approve"
                                               title="approve" name="approve">
                                        Approve this form
                                    </div>
                                </div>
                                <div class="text-right m-t-xs">
                                    <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                    <button id="submitRequest" type="submit" class="ladda-button btn btn-success"
                                            data-style="expand-left"><span class="ladda-label">Submit</span><span
                                            class="ladda-spinner"></span>
                                        <div class="ladda-progress" style="width: 0px;"></div>
                                    </button>

                                    <!--<a class="btn btn-success submitWizard" href="#" title="dropdown">Submit</a>-->
                                </div>
                            </div>
                        </div>
                        <!--</form>-->

                    </div>
                </div>
            </div>

        </div>
    </div>

`



    document.getElementById("wrapper").innerHTML = onboarding;

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_hrOnloadingStaff.js");
    script14.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(script14);
});