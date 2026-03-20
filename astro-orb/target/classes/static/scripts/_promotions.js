id = null;
fetchLookup(instId.split(",")[0]);

window.copyrights();

$(".saveClass").click(async function () {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
  var jso = postdata();
  return fetchPost("addPromotionSetting", jso).then(function (result) {
    $("#promotionTable").DataTable().destroy();
    $(".dismissPromotion").click();
    populateTable(result);
    $('.splash').css('display', 'none')
    swal({
      title: "Thank you!",
      text: "Promotions Saved Successfully",
      type: "success",
    });
  });
});

function postdata() {
  let classGroup = [];
  resultlist = [];
  classGroup = document.getElementsByClassName("clonable");
  //classGroup= document.getElementsByClassName('classesOptions');
  for (var i = 0; i < classGroup.length; i++) {

  const selectedCurrent = classGroup[i].getElementsByClassName("promotionOptions")[0];
  const selectedCurrentText = selectedCurrent.options[selectedCurrent.selectedIndex].text;

  const selectedTarget = classGroup[i].getElementsByClassName("promotionOptions1")[0];
  const selectedTargetText = selectedTarget.options[selectedTarget.selectedIndex].text;

    var jsonObject = {
      promotionId: id,
      currentClass: selectedCurrentText,
      targetClass: selectedTargetText,
      academicYear:"",
    };
    resultlist.push(jsonObject);
  }
  var v = instId.split(",")[0].replace(/[\[\]']+/g, "");
  v = v.replace(/\//g, "");
  var finalJsonObject = {
    institution: v,
    promotionsRequestDetailsList: resultlist,
  };
  return finalJsonObject;
}

function fetchLookup(instId) {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
  var v = instId.replace(/[\[\]']+/g, "");
  v = v.replace(/\//g, "");
  var instRequest = { val: v };
  return fetchPost("getInstitutionPromotionSettings", instRequest).then((result) =>{
    fetchInstitutionPromotionSettings(result, v)
    $('.splash').css('display', 'none')
    });
}

function fetchInstitutionPromotionSettings(result1, v) {
  return fetchPost("getInstitutionClasses", v).then(function (result) {
    populateTable(result1);
    populateClassGroup(result);
  });
}

function populateClassGroup(data) {
  data.forEach(function (d) {
    var currentClass = document.getElementById("currentClass");
    var targetClass = document.getElementById("targetClass");
    var option = document.createElement("option");
    option.value = d.id;
    option.textContent = d.name;
    var option1 = document.createElement("option");
        option1.value = d.id;
        option1.textContent = d.name;
    currentClass.appendChild(option);
    targetClass.appendChild(option1);
  });
}

function populateTable(data) {
  $("#promotionTableBody").empty();

  var bar = new Promise((resolve, reject) => {
    data.forEach((d, index, array) => {
      var details =
        "<tr id=" +
        d.id +
        "> <td hidden>" +
        d.promotionId +
        " </td> <td> " +
        d.currentClass +
        "</td><td>" +
        d.targetClass +
        "</td> </tr>";
      $("#promotionTableBody").append(details);

      var existing = $("#" + d.promotionId);

      existing
        .attr({
          "data-toggle": "modal",
          "data-target": "#myPromotionsModal",
          style: "cursor: pointer",
        })
        .on("click", function () {
          var recordIndex = $(this).data("index");

          modalopn();
          $(".newclassestxt").val(d.name);
          $(".promotionOptions").val(d.classGroup);
          $(".modalbody").eq(1).empty();
        });

      if (index === array.length - 1) resolve();
    });
  });
  bar.then(() => {
    console.log("All done!");
    dataTableInit();
  });
}

function dataTableInit() {
  $("#promotionTable").dataTable({
    dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
    lengthMenu: [
      [10, 25, 50, -1],
      [10, 25, 50, "All"],
    ],
    buttons: [
      { extend: "copy", className: "btn-sm" },
      { extend: "csv", title: "Classes List", className: "btn-sm" },
      { extend: "pdf", title: "Classes List", className: "btn-sm" },
      { extend: "print", className: "btn-sm" },
    ],
  });
}
