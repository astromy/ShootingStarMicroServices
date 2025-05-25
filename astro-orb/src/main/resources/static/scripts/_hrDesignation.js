id = null;
existingData = [];
fetchDepartment(instId.split(",")[0]);

window.copyrights();

$(".saveDesignation").click(async function () {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
  var jso = postdata();
  return HttpPost("addDesignation", jso).then(function (result) {
    $("#designationTable").DataTable().destroy();
    $(".dismissDesignation").click();
    populateDesignationTable(result);
    $('.splash').css('display', 'none')
    swal({
      title: "Thank you!",
      text: "Operation Completed Successfully",
      type: "success",
    });
  });
});

function postdata() {
  resultlist = [];
  resultlist2 = [];
  let joDescriptionList = [];
  joDescriptionList = document.getElementsByClassName("clonableJobDescription");

  for (var i = 0; i < joDescriptionList.length; i++) {
    var jsonObject = {
      idJobDescription: Number(id),
      jobDescription: joDescriptionList[i].getElementsByClassName(
        "newJobDescriptionTxt"
      )[0].value,
    };
    resultlist.push(jsonObject);
  }
  //for(var i=0;i<categoryList.length; i++){
  var jsonObject2 = {
    idDesignation: Number(id),
    name: document.getElementsByClassName("newDesignationName")[0].value,
    code: document.getElementsByClassName("newDesignationCode")[0].value,
    totalSlots: document.getElementsByClassName("newDesignationTotalSlots")[0]
      .value,
    availableSlots: document.getElementsByClassName(
      "newDesignationAvailableSlots"
    )[0].value,
    jobDescriptionList: resultlist,
  };
  resultlist2.push(jsonObject2);
  //}
  var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
  v = v.replace(/\//g, "");

  var finalJsonObject = {
    institutionBECECode: v,
    departmentId: document.getElementsByClassName("newDesignationDepartment")[0]
      .value,
    designationRequestDetails: resultlist2,
  };
  return finalJsonObject;
}

/*  async function fetchInstitutionDesignations(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getDesignation",instRequest)
     .then(function (result) {
     fetchDepartment(result);
  })
  }*/

async function fetchDepartment(instId) {
  var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
  v = v.replace(/\//g, "");
  var instRequest = { val: v };
  return HttpPost("getInstitutionDepartment", instRequest).then(function (
    result
  ) {
    populateDepartment(result);
    populateTable(result);
  });
}

function populateDepartment(data) {
  var n = document.getElementsByClassName("newDesignationDepartment");
  data.forEach(function (d) {
    var classOptions = document.getElementsByClassName(
      "newDesignationDepartment"
    )[0];
    var option = document.createElement("option");
    option.value = d.idDepartment;
    option.textContent = d.name;
    classOptions.appendChild(option);
  });
}

function populateTable(data) {
  $("#designationTableBody").empty();
  existingData = data;
  if (data != null) {
    var bar = new Promise((resolve, reject) => {
      data.forEach((d, index, array) => {
        d.designationList.forEach((dl, index1, array1) => {
          var details = `<tr id="${dl.code}" data-index="${index1}">
                                        <td>${dl.name}</td>
                                        <td>${dl.code}</td>
                                        <td>${dl.totalSlots}</td>
                                        <td>${dl.availableSlots}</td>
                                        <td><input type='checkbox'></td>
                                    </tr>`;
          $("#designationTableBody").append(details);

          var existing = $("#" + dl.code);

          existing
            .attr({
              "data-toggle": "modal",
              "data-target": "#mydesignationModal",
              style: "cursor: pointer",
            })
            .on("click", function () {
              var recordIndex = $(this).data("index");

              modalopn();
              $(".newDesignationName").val(dl.name);
              $(".newDesignationCode").val(dl.code);
              $(".newDesignationTotalSlots").val(dl.totalSlots);
              $(".newDesignationAvailableSlots").val(dl.availableSlots);
              $(".modalbody").eq(1).empty();

              let parentObject = existingData.find((parent) =>
                parent.designationList.some(
                  (innerItem) => innerItem.code === dl.code
                )
              );

              document.getElementsByClassName(
                "newDesignationDepartment"
              )[0].value = parentObject.idDepartment;

              let result = existingData
                .flatMap((item) => item.designationList)
                .find((innerItem) => innerItem.code === dl.code);

              result.jobDescriptionList.forEach((ejd, index, array) => {
                // Create the main clonable div with nested elements
                var clonable = $(`
                                                    <div class="row clonableJobDescription">
                                                        <div class="col-sm-12">
                                                            <div class="row">
                                                                <div class="col-md-12">
                                                                    <input type="text" placeholder="Enter Job Description" class="form-control newJobDescriptionTxt">
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                `);

                // Assign a value to the input field
                clonable.find(".newJobDescriptionTxt").val(ejd.jobDescription);

                // Create the hr-line-dashed div
                var clonable2 = $('<div class="hr-line-dashed"></div>');

                // Append both to a parent container (if needed)
                $(".modalbody").eq(1).append(clonable, clonable2);
              });
              // Add your click handler code here
            });
          if (index1 === array1.length - 1) resolve();
        });
      });
    });
    bar.then(() => {
      console.log("All done!");
      dataTableInit();
    });
  }
}

function populateDesignationTable(data) {
  $("#designationTableBody").empty();

  if (data != null) {
    var bar = new Promise((resolve, reject) => {
      data.forEach((d, index, array) => {
        var details = `<tr id="${d.code}" data-index="${index1}">
                                                    <td>${d.name}</td>
                                                    <td>${d.code}</td>
                                                    <td>${d.totalSlots}</td>
                                                    <td>${d.availableSlots}</td>
                                                    <td><input type='checkbox'></td>
                                                </tr>`;
        $("#designationTableBody").append(details);
        if (index === array.length - 1) resolve();
      });
    });
    bar.then(() => {
      console.log("All done!");
      dataTableInit();
    });
  }
}

function dataTableInit() {
  $("#designationTable").dataTable({
    dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
    lengthMenu: [
      [10, 25, 50, -1],
      [10, 25, 50, "All"],
    ],
    buttons: [
      { extend: "copy", className: "btn-sm" },
      { extend: "csv", title: "Designation Details", className: "btn-sm" },
      { extend: "pdf", title: "Designation Details", className: "btn-sm" },
      { extend: "print", className: "btn-sm" },
    ],
  });
}
