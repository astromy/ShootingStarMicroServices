window.copyrights();
id = null;
var keys, scoreJson, url;
var v;
fetchLookup(instId.split(",")[0]);
var elm = document.querySelector(".scoreUploadBtn");
elm.addEventListener("click", function () {
  document.querySelector("#scoreInput").click();
});

document
  .querySelector("#classListExport")
  .addEventListener("click", async function () {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    var jso = {
      institutionCode: v,
      studentClass: document.querySelector(".classSelect").value,
    };
    return HttpPost("getAssessmentList", jso).then(function (result) {
    if(result.length>0){
      $("#subjectScoreTable").DataTable().destroy();
      result && generateExcel(result);
      }
    $('.splash').css('display', 'none')
    swal({
      title: "Thank you!",
      text: "Operation Completed Successfully",
      type: "success",
    });
    });
  });

document
  .querySelector("#scoreInput")
  .addEventListener("change", async function () {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    try {
      var doc = await uploadFileAsJSON(
        document.querySelector("#scoreInput"),
        document.querySelectorAll(".fileError")[0]
      );
      scoreJson = await base64ToJson(doc.fileContent);
      keys = Object.keys(scoreJson[0]);

      $("#scoreTableHead").empty();

      // Loop through the list and create <th> elements
      keys.forEach((header) => {
        $("#scoreTableHead").append(`<th>${header}</th>`);
      });
      let tbody = $("#scoreTableBody");
      tbody.empty(); // Clear existing rows

      // Loop through the JSON data and create table rows
      scoreJson.forEach((row) => {
        let tr = $("<tr></tr>");
        keys.forEach((key) => {
          tr.append(`<td>${row[key] || ""}</td>`); // Insert corresponding data
        });
        tbody.append(tr);
      });
    } catch (error) {
      console.error("Error uploading file:", error);
    }
    $('.splash').css('display', 'none')
  });

document
  .querySelector("#subjectScoreTable_wrapper")
  .setAttribute("style", "overflow: auto;");

var selectedValue = document
  .querySelector("#scoreTypeControl")
  .parentElement.querySelector("label").innerHTML;

document
  .querySelector("#scoreTypeControl")
  .addEventListener("change", function () {
    var selectedValue = this.parentElement.querySelector("label").innerHTML;

    if (selectedValue == "Class Score") {
      this.parentElement.querySelector("label").innerHTML = "Exams Score";
      url = "uploadExamsScores";
    } else {
      this.parentElement.querySelector("label").innerHTML = "Class Score";
      url = "uploadAssesmentScores";
    }
  });

if (selectedValue == "Class Score") {
  url = "uploadAssesmentScores";
} else {
  url = "uploadExamsScores";
}

$("#scoreSubmitExport").click(async function () {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
  var jso = postdata();
  return HttpPost(url, jso).then(function (result) {
    $("#subjectScoreTable").DataTable().destroy();
    //populateTable(result)
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

  for (var i = 0; i < scoreJson.length; i++) {
    var jsonObject = {
      id: Number(id),
      score: Number(scoreJson[i].score),
      totalScore: Number(scoreJson[i].totalScore),
      subject: Number(
        document.getElementsByClassName(" subjectSelect")[0].value
      ),
      term: document.getElementsByClassName("termSelect")[0].value,
      studentClass: document.getElementsByClassName("classSelect")[0].value,
      academicYear:
        document.getElementsByClassName("academicYearSelect")[0].value,
      studentId: scoreJson[i].studentID,
      dateTime: new Date().toString(),
      institutionCode: v,
    };
    resultlist.push(jsonObject);
  }
  return resultlist;
}

function convertToISO(dateStr) {
  // Split the input string into date and time
  const [datePart, timePart] = dateStr.split(" ");

  // Split the date into components (month/day/year)
  const [month, day, year] = datePart.split("/");

  // Combine the components into an ISO format date string
  const isoDate = `${year}-${month.padStart(2, "0")}-${day.padStart(2, "0")}`;

  // Return the final ISO string with time (timePart already in HH:mm format)
  return `${isoDate} ${timePart}:00`;
}

async function fetchInstitutionClasses(v) {
  var instRequest = { val: v };
  return HttpPost("getInstitutionClasses", instRequest).then(function (result) {
    populateClasses(result);
  });
}

document
  .querySelector(".classGroupSelect")
  .addEventListener("change", async function () {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    var vg = document.getElementsByClassName("classGroupSelect")[0].value;

    var instRequest = {
      id: 0,
      name: v,
      classGroup: document.getElementsByClassName("classGroupSelect")[0].value,
      preference: 0,
    };
    var instRequest2 = {
      institution: v,
      classGroup: document.getElementsByClassName("classGroupSelect")[0].value,
    };
    try {
      // Await the result of the HTTP request
      const result = await HttpPost(
        "getInstitutionSubjectsAndClassGroup",
        instRequest
      );
      const result2 = await HttpPost(
        "getInstitutionClassesByClassGroup",
        instRequest2
      );
      populateSubjectsOptions(result);
      populateClasses(result2);
    } catch (error) {
      console.error("Error in fetchInstitutionSubject:", error);
    }
    $('.splash').css('display', 'none')
  });

function populateClasses(data) {
  $(".classSelect option:not(:eq(0))").remove();

  data.forEach(function (d) {
    var classOptions = document.querySelector(".classSelect")[0];
    var details = $("<option>").val(d.name).text(d.name);
    $(".classSelect").append(details);
  });
}

async function fetchLookup(instId) {
$('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
  v = instId.replace(/[\[\]']+/g, "");
  v = v.replace(/\//g, "");
  var instRequest = { val: "ClassGroup" };
  return HttpPost("getLookUpByType", instRequest).then(function (result) {
    populateClassGroup(result);
    generateAcademicYears();
    $('.splash').css('display', 'none')
  });
}

function populateClassGroup(data) {
  $(".classGroupSelect option:not(:eq(0))").remove();
  data.forEach(function (d) {
    var details = $("<option>").val(d.id).text(d.name);
    $(".classGroupSelect").append(details);
  });
}

function populateSubjectsOptions(data) {
  $(".subjectSelect option:not(:eq(0))").remove();
  data.forEach(function (d) {
    var details = $("<option>").val(d.id).text(d.name);
    $(".subjectSelect").append(details);
  });
}

function generateAcademicYears() {
  const select = document.querySelector(".academicYearSelect");
  select.innerHTML = ""; // Clear existing options

  const currentYear = new Date().getFullYear();

  for (let i = 4; i >= 0; i--) {
    const startYear = currentYear - i;
    const endYear = startYear + 1;
    const option = document.createElement("option");
    option.value = `${startYear}/${endYear}`;
    option.textContent = `${startYear}/${endYear}`;
    select.appendChild(option);
  }
}

function generateExcel(jsonData) {
  const selectElement = document.querySelector(".subjectSelect");
  var subject = (selectedText =
    selectElement.selectedIndex >= 0
      ? selectElement.options[selectElement.selectedIndex].text
      : "");
  // Define the desired order of fields
  const desiredOrder = [
    "studentID",
    "name",
    "studentClass",
    "subject",
    "score",
    "totalScore",
  ];

  // Rearrange the data to match the desired order
  const orderedData = jsonData.map((item) => {
    const orderedItem = {};
    desiredOrder.forEach((field) => {
      if (field === "subject") {
        orderedItem[field] = subject;
      } else {
        orderedItem[field] = item[field];
      }
    });
    return orderedItem;
  });

  // Create a worksheet from the ordered data
  const ws = XLSX.utils.json_to_sheet(orderedData);

  // Create a workbook
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, jsonData[0].studentClass);

  // Write the workbook to a binary string
  const wbout = XLSX.write(wb, { bookType: "xlsx", type: "binary" });

  // Create a Blob object with the binary string data
  const blob = new Blob([s2ab(wbout)], { type: "application/octet-stream" });

  // Create a link element and trigger a download
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = jsonData[0].studentClass + "_" + subject + ".xlsx";
  link.click();
}

// Convert string to array buffer
function s2ab(s) {
  const buf = new ArrayBuffer(s.length);
  const view = new Uint8Array(buf);
  for (let i = 0; i < s.length; i++) {
    view[i] = s.charCodeAt(i) & 0xff;
  }
  return buf;
}



$(document).ready(function () {
  // Disable all except classGroupSelect
  $(".classSelect, .subjectSelect, .termSelect, .academicYearSelect").prop(
    "disabled",
    true
  );

  // On change of classGroupSelect
  $(".classGroupSelect").on("change", function () {
    let val = $(this).val();
    $(".classSelect").prop("disabled", !val);
  });

  $(".classSelect").on("change", function () {
    let val = $(this).val();
    $(".subjectSelect").prop("disabled", !val);
  });

  $(".subjectSelect").on("change", function () {
    let val = $(this).val();
    $(".termSelect").prop("disabled", !val);
  });

  $(".termSelect").on("change", function () {
    let val = $(this).val();
    $(".academicYearSelect").prop("disabled", !val);
  });
});
