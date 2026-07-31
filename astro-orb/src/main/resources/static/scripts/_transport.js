let transportRoutes = [];
let transportStaff = [];

fetchInstitutionBuses(instId.split(",")[0]);
fetchInstitutionRoutes(instId.split(",")[0]);
fetchTransportStaff(instId.split(",")[0]);

window.copyrights();

$(".saveBus").click(async function () {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();

    var jso = busPostData();

    var request;
    if (window.editingBusId) {
        // Editing an existing bus — updateBus takes the single BusDetails
        // object directly (idBus present tells the backend which one).
        request = fetchPost("updateBus", jso);
    } else {
        // Adding a new one — addBuses takes the bulk-add shape (matches
        // the same institution + detailsList pattern as addDepartment).
        var v = instId.split(",")[0].replace(/[\[\]']+/g, "").replace(/\//g, "");
        request = fetchPost("addBuses", {institution: v, busDetailsList: [jso]});
    }

    return request.then(function () {
        $("#busTable").DataTable().destroy();
        $(".dismissBus").click();
        $('.splash').css('display', 'none');
        swal({
            title: "Saved!",
            text: "The bus has been saved.",
            type: "success",
        });
        fetchInstitutionBuses(instId.split(",")[0]);
    });
});

$(".saveRoute").click(async function () {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();

    var name = $("#routeName").val();
    var description = $("#routeDescription").val();
    var v = instId.split(",")[0].replace(/[\[\]']+/g, "").replace(/\//g, "");
    var finalJsonObject = {
        institution: v,
        routeDetailsList: [{name: name, description: description}],
    };

    return fetchPost("addRoutes", finalJsonObject).then(function () {
        $("#routeTable").DataTable().destroy();
        $(".dismissRoute").click();
        $('.splash').css('display', 'none');
        swal({
            title: "Saved!",
            text: "The route has been saved.",
            type: "success",
        });
        fetchInstitutionRoutes(instId.split(",")[0]);
    });
});

function busPostData() {
    return {
        idBus: window.editingBusId || null,
        name: $("#busName").val(),
        registrationPlate: $("#busPlate").val(),
        vehicleType: $("#busVehicleType").val(),
        vehicleBrand: $("#busVehicleBrand").val(),
        sittingCapacity: $("#busCapacity").val() ? parseInt($("#busCapacity").val()) : null,
        insuranceExpiryDate: $("#busInsuranceExpiry").val() || null,
        roadworthyExpiryDate: $("#busRoadworthyExpiry").val() || null,
        driverStaffCode: $("#busDriver").val() || null,
        routeId: $("#busRoute").val() ? parseInt($("#busRoute").val()) : null,
    };
}

async function fetchInstitutionBuses(instIdVal) {
    var v = instIdVal.replace(/[\[\]']+/g, "").replace(/\//g, "");
    return fetchPost("getInstitutionBuses", {val: v}).then(function (result) {
        populateBusTable(result);
    });
}

async function fetchInstitutionRoutes(instIdVal) {
    var v = instIdVal.replace(/[\[\]']+/g, "").replace(/\//g, "");
    return fetchPost("getInstitutionRoutes", {val: v}).then(function (result) {
        transportRoutes = result;
        populateRouteTable(result);
        populateRouteDropdown(result);
    });
}

async function fetchTransportStaff(instIdVal) {
    var v = instIdVal.replace(/[\[\]']+/g, "").replace(/\//g, "");
    return fetchPost("get-staff-by-institution", {val: v}).then(function (result) {
        transportStaff = result;
        populateDriverDropdown(result);
    });
}

function populateRouteDropdown(data) {
    var select = $("#busRoute");
    select.find('option[value!=""]').remove();
    data.forEach(function (r) {
        select.append("<option value='" + r.idRoute + "'>" + r.name + "</option>");
    });
}

function populateDriverDropdown(data) {
    var select = $("#busDriver");
    select.find('option[value!=""]').remove();
    data.forEach(function (s) {
        select.append("<option value='" + s.staffCode + "'>" + s.firstNames + " " + s.lastName + "</option>");
    });
}

function driverName(staffCode) {
    if (!staffCode) return "—";
    var staff = transportStaff.find(function (s) {
        return s.staffCode === staffCode;
    });
    return staff ? (staff.firstNames + " " + staff.lastName) : staffCode;
}

function statusBadge(status) {
    if (!status) return "—";
    var cls = status === "EXPIRED" ? "label-danger" : "label-success";
    return "<span class='label " + cls + "'>" + status + "</span>";
}

function populateBusTable(data) {
    $("#busTableBody").empty();

    var bar = new Promise((resolve, reject) => {
        if (data.length === 0) resolve();
        data.forEach((d, index, array) => {
            var details =
                "<tr id=" + d.idBus + ">" +
                "<td>" + d.name + "</td>" +
                "<td>" + (d.vehicleBrand || "") + " " + (d.vehicleType || "") + "</td>" +
                "<td>" + (d.registrationPlate || "—") + "</td>" +
                "<td>" + (d.sittingCapacity || "—") + "</td>" +
                "<td>" + (d.routeName || "—") + "</td>" +
                "<td>" + driverName(d.driverStaffCode) + "</td>" +
                "<td>" + statusBadge(d.insuranceStatus) + "</td>" +
                "<td>" + statusBadge(d.roadworthyStatus) + "</td>" +
                "<td><button type='button' class='btn btn-xs btn-default editBusBtn' data-id='" + d.idBus + "'>Edit</button></td>" +
                "</tr>";
            $("#busTableBody").append(details);
            if (index === array.length - 1) resolve();
        });
    });
    bar.then(() => {
        dataTableInit("#busTable");
        wireEditButtons(data);
    });
}

function wireEditButtons(data) {
    $(".editBusBtn").off("click").on("click", function () {
        var id = parseInt($(this).data("id"));
        var bus = data.find(function (b) {
            return b.idBus === id;
        });
        if (!bus) return;

        window.editingBusId = bus.idBus;
        document.getElementById("busModalTitle").innerText = "Edit Bus";
        $("#busName").val(bus.name);
        $("#busPlate").val(bus.registrationPlate);
        $("#busVehicleType").val(bus.vehicleType);
        $("#busVehicleBrand").val(bus.vehicleBrand);
        $("#busCapacity").val(bus.sittingCapacity);
        $("#busRoute").val(bus.routeId || "");
        $("#busDriver").val(bus.driverStaffCode || "");
        $("#busInsuranceExpiry").val(bus.insuranceExpiryDate || "");
        $("#busRoadworthyExpiry").val(bus.roadworthyExpiryDate || "");
        $("#busModal").modal("show");
    });
}

function populateRouteTable(data) {
    $("#routeTableBody").empty();

    var bar = new Promise((resolve, reject) => {
        if (data.length === 0) resolve();
        data.forEach((d, index, array) => {
            var details =
                "<tr id=" + d.idRoute + "><td>" + d.name + "</td><td>" + (d.description || "") + "</td></tr>";
            $("#routeTableBody").append(details);
            if (index === array.length - 1) resolve();
        });
    });
    bar.then(() => {
        dataTableInit("#routeTable");
    });
}

function dataTableInit(selector) {
    $(selector).dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        lengthMenu: [
            [10, 25, 50, -1],
            [10, 25, 50, "All"],
        ],
        buttons: [
            {extend: "copy", className: "btn-sm"},
            {extend: "csv", className: "btn-sm"},
            {extend: "pdf", className: "btn-sm"},
            {extend: "print", className: "btn-sm"},
        ],
    });
}