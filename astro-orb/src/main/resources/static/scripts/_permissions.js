var exisitingstaff = [];
var staffPermissionList = [];
var staffCode;

var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
instId = v.replace(/\//g, "");
fetchStaffList(instId);

id = null;
//fetchInstitutionClasses(instId.split(",")[0]);

window.copyrights();

$(".savePermissions").click(async function () {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    return fetchPost("addStaffPermissions", staffPermissionList).then(function (
        result
    ) {
        staffPermissionList = [];
        $(".dismissPermissions").click();
        $('.splash').css('display', 'none')
        swal({
            title: "Thank you!",
            text: "Your application is being submitted",
            type: "success",
        });
    });
});

async function fetchInstitutionClasses(instId) {
    var v = instId.replace(/[\[\]']+/g, "");
    v = v.replace(/\//g, "");
    var instRequest = {val: v};
    return fetchPost("getInstitutionClasses", instRequest).then(function (result) {
        fetchLookup(result);
    });
}

async function fetchLookup(result1) {
    var instRequest = {val: "ClassGroup"};
    return fetchPost("getLookUpByType", instRequest).then(function (result) {
        populateTable(result1);
        populateClassGroup(result);
    });
}

function populateClassGroup(data) {
    data.forEach(function (d) {
        var details = "<option value='" + d.id + "'>" + d.name + " </option>";
        $("#classGroupOptions").append(details);
    });
}

function populateTable(data) {
    var bar = new Promise((resolve, reject) => {
        data.forEach((d, index, array) => {
            var details =
                "<tr> <td hidden>" +
                d.id +
                " </td> <td> " +
                d.name +
                "</td><td>" +
                d.classGroup +
                "</td> </tr>";
            $("#classesTableBody").append(details);
            if (index === array.length - 1) resolve();
        });
    });
    bar.then(() => {
        dataTableInit();
    });
}

// Add an onchange event listener
document.querySelector("#StaffList").addEventListener("change", (event) => {
    staffCode = event.target.value;
});

async function fetchStaffList(instId) {
    var instRequest = {val: instId};
    return fetchPost("get-staff-by-institution", instRequest).then(function (
        result
    ) {
        exisitingstaff = result;

        var el = $("#StaffList");
        el.find("option:gt(0)").remove();

        if (result != null) {
            var bar = new Promise((resolve, reject) => {
                result.forEach((sl, index1, array1) => {
                    el.append(
                        $("<option>", {
                            value: sl.staffCode,
                            text:
                                sl.staffCode +
                                " " +
                                "[" +
                                sl.firstNames +
                                " " +
                                sl.lastName +
                                "]" +
                                "  ",
                        })
                    );
                    if (index1 === array1.length - 1) resolve();
                });
            });
            bar.then(() => {
            });

            populateUsersTable(result);
        }
    });
}

// Renders every staff member into the Permissions table. Clicking a row (or
// its "Manage" button) opens the Set Permissions modal pre-selected for that
// user, with whatever permissions they already have checked.
function populateUsersTable(data) {
    var tbody = $("#permissionsTableBody");
    tbody.empty();

    data.forEach(function (staff) {
        var name = (staff.firstNames || "") + " " + (staff.lastName || "");
        var row = $("<tr>", {"data-staff-code": staff.staffCode, style: "cursor:pointer"});
        row.append($("<td>").text(staff.staffCode || ""));
        row.append($("<td>").text(name.trim()));
        row.append($("<td>").text(staff.designation || ""));
        row.append($("<td>").text(staff.staffEmail || ""));
        row.append(
            $("<td>").append(
                $("<button>", {
                    type: "button",
                    class: "btn btn-info btn-sm manage-permissions-btn",
                    text: "Manage Permissions",
                })
            )
        );
        tbody.append(row);
    });

    dataTableInit();
}

// Whole row is clickable, and so is the button inside it - both resolve to
// the same staffCode via the row's data attribute, so either works.
$(document).on("click", "#permissionsTable tbody tr", function (event) {
    var staffCode = $(this).data("staff-code");
    if (staffCode) {
        openPermissionsFor(String(staffCode));
    }
});

function openPermissionsFor(staffCode) {
    // Open the modal first - this rebuilds the tabs/checkboxes fresh via
    // modalopn()/staffpermissionsIndut(), which the StaffList "change"
    // handler below relies on already existing (it toggles .tabs, which
    // only exists once the modal body has been rendered).
    document.getElementById("modalopn").click();

    var staffList = document.getElementById("StaffList");
    staffList.value = staffCode;
    staffList.dispatchEvent(new Event("change"));

    fetchExistingPermissions(staffCode);
}

// Pre-checks whichever permission checkboxes match what this staff member
// already has, without emitting "change" events - so the pre-check itself
// doesn't get treated as a pending edit by permissionBuilder(). Only actual
// clicks by the admin after this point get queued into staffPermissionList.
async function fetchExistingPermissions(staffCode) {
    var request = {val: staffCode};
    return fetchPost("get-permissions-by-staff", request).then(function (result) {
        if (!result) {
            return;
        }
        result.forEach(function (perm) {
            var checkbox = document.querySelector(
                'input[type="checkbox"][value="' + perm.permissionCode + '"]'
            );
            if (checkbox) {
                checkbox.checked = true;
            }
        });
    });
}

function dataTableInit() {
    // Initialize Example 1
    $("#permissionsTable").dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        lengthMenu: [
            [10, 25, 50, -1],
            [10, 25, 50, "All"],
        ],
        buttons: [
            {extend: "copy", className: "btn-sm"},
            {extend: "csv", title: "ExampleFile", className: "btn-sm"},
            {extend: "pdf", title: "ExampleFile", className: "btn-sm"},
            {extend: "print", className: "btn-sm"},
        ],
    });
}

function permissionBuilder() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    // Add an event listener to each checkbox
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", (event) => {
            const label = document.querySelector(`label[for="${checkbox.id}"]`);
            if (event.target.checked) {
                if (
                    staffPermissionList.some((obj) => obj["staffCode"] === staffCode) &&
                    staffPermissionList.some(
                        (obj) => obj["permission"] === label.innerHTML
                    )
                ) {
                    staffPermissionList.find(
                        (obj) => obj["permission"] === label.innerHTML
                    ).state = "add";
                } else {
                    var staffPermission = {
                        id: "",
                        staffCode: staffCode,
                        permissionCode: event.target.value,
                        permission: label.innerHTML,
                        institutionCode: instId,
                        state: "add",
                    };
                    staffPermissionList.push(staffPermission);
                }
            } else {
                if (
                    staffPermissionList.some((obj) => obj["staffCode"] === staffCode) &&
                    staffPermissionList.some(
                        (obj) => obj["permission"] === label.innerHTML
                    )
                ) {
                    staffPermissionList.find(
                        (obj) => obj["permission"] === label.innerHTML
                    ).state = "delete";
                } else {
                    var staffPermission = {
                        id: "",
                        staffCode: staffCode,
                        permissionCode: event.target.value,
                        permission: label.innerHTML,
                        institutionCode: instId,
                        state: "delete",
                    };
                    staffPermissionList.push(staffPermission);
                }
            }
        });
    });
}

document.querySelector("#StaffList").addEventListener("change", function () {
    var selectedValue = this.value;

    const el = document.querySelector(".tabs");
    if (selectedValue !== "Select Staff") {
        el.style.pointerEvents = "auto"; // Re-enable all clicks/interactions
        el.style.opacity = "1"; // Restore original appearance (optional)
    } else {
        el.style.pointerEvents = "none"; // Disable all clicks/interactions
        el.style.opacity = "0.5"; // Make it appear disabled (optional)
    }
});
