$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                <button class="btn btn-info" type="button" id="modalopn" data-toggle="modal"
                data-target="#mygradingModal">New Grade Setting</button>
                </div>
            </div>
    `

    //function institutionBuild() {
    let grading = `
    
    
    <div class="content animate-panel" id="pagecontent">
    <div class="hpanel">

        <div class="panel-heading">
            <div class="panel-tools">
            </div>
            Grading
        </div>
        <div class="panel-body">
        <div class="row" style="margin-bottom:12px">
            <table id="gradingTable" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr>
                    <th hidden></th>
                    <th class="col-md-3">Class Percentage</th>
                    <th class="col-md-3">Exams Percentage</th>
                    <th class="col-md-3">Trailing Mark</th>
                    <th class="col-md-3">Allowed Trails</th>
                </tr>
                </thead>
                <tbody  id="gradingSettingsBody" style="margin-bottom:12px"></tbody>

            </table>
        </div>
        <div class="row" style="margin-top:12px">
            <table id="gradingTable2" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <th hidden></th>
                        <th class="col-sm-3">Upper Limit</th>
                        <th class="col-sm-3">Lower Limit</th>
                        <th class="col-sm-3">Grade</th>
                        <th class="col-sm-3">Comment</th>
                    </tr>
                </thead>
                <tbody id="gradingBracketsBody"> </tbody>
            </table>
            </div>
        </div>
    </div>
    

    
    <div class="modal fade hmodal-info" id="mygradingModal" tabindex="-1" role="dialog"aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="color-line"></div>
                    <div class="modal-header">
                        <h4 class="modal-title">Add Grade Setting</h4>
                        <small class="font-bold">Setup your grading rules using this feature.</small>
                    </div>
                    <div class="panel-body modalbody" style="border-bottom: 1px solid #a8bede;">
                        <div class="form-group">
                            <div class="col-sm-9">
                                <div class="row">
                                    <div class="col-md-12"><input type="text" placeholder="Enter Class %" class="form-control newadmissionstxt"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel-body modalbody">
                        <div class="form-group">
                            <div class="col-sm-9">
                                <div class="row">
                                    <div class="col-md-12"><input type="text" placeholder="Enter Exams %" class="form-control newadmissionstxt"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <!--<button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More Criteria</i></button>-->
                        <button type="button" class="btn btn-primary left test"><i class="fa fa-plus-square"><span style="margin-left:5px"/>Add More Grade Brackets</i></button>
                        <button type="button" class="btn btn-default dismissGrading"data-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary saveGrading">Save changes</button>
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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', grading);
    document.getElementsByClassName("test")[0].addEventListener("click", gradebrackets);
    //document.getElementsByClassName("test")[1].addEventListener("click", gradebrackets);
    document.getElementById("modalopn").addEventListener("click", modalopn);

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_grading.js");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);
function modalopn(){
    document.getElementsByClassName("modalbody")[0].innerHTML="";
    document.getElementsByClassName("modalbody")[1].innerHTML="";
    gradesettings();
    gradebrackets();
}

function gradesettings() {
    let div = `
    <div class="row gradeSettings">

        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12">
                    <input type="number" placeholder="Class Percentage" class="form-control newClassPercentage">
                </div>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12"><input type="number" placeholder="Exams Percentage" class="form-control newExamsPercentage"></div>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12">
                    <input type="number" placeholder="Trailing Mark" class="form-control newTrailingMark">
                </div>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="row">
                <div class="col-md-12">
                    <input type="number" placeholder="Allowed Trails" class="form-control newAllowedTrails">
                </div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[0].insertAdjacentHTML('beforeend', div);
}

function gradebrackets() {
    let div = `
    <div class="row gradeBrackets">

        <div class="col-sm-4">
            <div class="row">
                <div class="col-md-12"><input type="number" placeholder="Lower Limit" class="form-control lowerLimit"></div>
            </div>
        </div>

        <div class="col-sm-4">
            <div class="row">
            <div class="col-md-12"><input type="text" placeholder="Grade" class="form-control grade"></div>
            </div>
        </div>

        <div class="col-sm-4">
            <div class="row">
            <div class="col-md-12"><input type="text" placeholder="Comment" class="form-control comment"></div>
            </div>
        </div>
    </div>
    <div class="hr-line-dashed"></div>`
    document.getElementsByClassName("modalbody")[1].insertAdjacentHTML('beforeend', div);
}

