$(function () {

    let header = `
            <div class="panel-body">
                <div id="hbreadcrumb" class="pull-right">
                <button class="btn btn-default" type="button" id="modalopnRoute" data-toggle="modal"
                data-target="#routeModal">New Route</button>
                <button class="btn btn-info" type="button" id="modalopnBus" data-toggle="modal"
                data-target="#busModal">New Bus</button>
                </div>
            </div>
    `

    let transport = `


    <div class="content animate-panel" id="pagecontent">
    <div class="hpanel">

        <div class="panel-heading">
            <div class="panel-tools">
            </div>
            Buses
        </div>
        <div class="panel-body">
            <table id="busTable" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Vehicle</th>
                    <th>Plate</th>
                    <th>Capacity</th>
                    <th>Route</th>
                    <th>Driver</th>
                    <th>Insurance</th>
                    <th>Roadworthy</th>
                    <th>Edit</th>
                </tr>
                </thead>
                <tbody id="busTableBody"></tbody>
            </table>
        </div>
    </div>

    <div class="hpanel">
        <div class="panel-heading">
            <div class="panel-tools">
            </div>
            Routes
        </div>
        <div class="panel-body">
            <table id="routeTable" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
                </thead>
                <tbody id="routeTableBody"></tbody>
            </table>
        </div>
    </div>

    <!-- Bus modal -->
    <div class="modal fade hmodal-info" id="busModal" tabindex="-1" role="dialog" >
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="color-line"></div>
                <div class="modal-header">
                    <h4 class="modal-title" id="busModalTitle">Add Bus</h4>
                    <small class="font-bold">Vehicle details, compliance dates, and its assigned route and driver.</small>
                </div>
                <div class="panel-body modalbody">
                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label class="control-label">Bus Name</label>
                            <input type="text" placeholder="e.g. Bus 3" class="form-control" id="busName"/>
                        </div>
                        <div class="col-md-6 form-group">
                            <label class="control-label">Registration Plate</label>
                            <input type="text" placeholder="e.g. GT 1234-24" class="form-control" id="busPlate"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label class="control-label">Vehicle Type</label>
                            <input type="text" placeholder="e.g. Coaster" class="form-control" id="busVehicleType"/>
                        </div>
                        <div class="col-md-6 form-group">
                            <label class="control-label">Vehicle Brand</label>
                            <input type="text" placeholder="e.g. Toyota" class="form-control" id="busVehicleBrand"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label class="control-label">Sitting Capacity</label>
                            <input type="number" min="1" placeholder="e.g. 30" class="form-control" id="busCapacity"/>
                        </div>
                        <div class="col-md-6 form-group">
                            <label class="control-label">Route</label>
                            <select class="form-control" id="busRoute">
                                <option value="">-- No route assigned --</option>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label class="control-label">Insurance Expiry Date</label>
                            <input type="date" class="form-control" id="busInsuranceExpiry"/>
                        </div>
                        <div class="col-md-6 form-group">
                            <label class="control-label">Roadworthy Expiry Date</label>
                            <input type="date" class="form-control" id="busRoadworthyExpiry"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label class="control-label">Driver</label>
                            <select class="form-control" id="busDriver">
                                <option value="">-- No driver assigned --</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default dismissBus" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary saveBus">Save Bus</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Route modal -->
    <div class="modal fade hmodal-info" id="routeModal" tabindex="-1" role="dialog" >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="color-line"></div>
                <div class="modal-header">
                    <h4 class="modal-title">Add Route</h4>
                    <small class="font-bold">A route buses run and students are assigned to, e.g. "Accra Route".</small>
                </div>
                <div class="panel-body modalbody">
                    <div class="form-group">
                        <label class="control-label">Route Name</label>
                        <input type="text" placeholder="e.g. Accra Route" class="form-control" id="routeName"/>
                    </div>
                    <div class="form-group">
                        <label class="control-label">Description</label>
                        <input type="text" placeholder="Optional" class="form-control" id="routeDescription"/>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default dismissRoute" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary saveRoute">Save Route</button>
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
    document.getElementById("wrapper").insertAdjacentHTML('beforeend', transport);
    document.getElementById("modalopnBus").addEventListener("click", openAddBusModal);
    document.getElementById("modalopnRoute").addEventListener("click", openAddRouteModal);

    var script15 = document.createElement("script");
    script15.setAttribute("type", "text/javascript");
    script15.setAttribute("src", "scripts/_transport.js");
    script15.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(script15);
});

// Resets the modal to "add new" state — editingBusId is read by _transport.js's
// saveBus handler to decide between addBuses and updateBus.
function openAddBusModal() {
    window.editingBusId = null;
    document.getElementById("busModalTitle").innerText = "Add Bus";
    document.getElementById("busName").value = "";
    document.getElementById("busPlate").value = "";
    document.getElementById("busVehicleType").value = "";
    document.getElementById("busVehicleBrand").value = "";
    document.getElementById("busCapacity").value = "";
    document.getElementById("busRoute").value = "";
    document.getElementById("busDriver").value = "";
    document.getElementById("busInsuranceExpiry").value = "";
    document.getElementById("busRoadworthyExpiry").value = "";
}

function openAddRouteModal() {
    document.getElementById("routeName").value = "";
    document.getElementById("routeDescription").value = "";
}