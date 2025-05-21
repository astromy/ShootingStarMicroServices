window.copyrights();
id = "";
var keys, studentsJson, url;
var studentsSheet;
var v;
//fetchLookup(instId.split(",")[0])
var elm = document.querySelector(".studentsUploadBtn");
elm.addEventListener("click", function () {
  document.querySelector("#studentsInput").click();
});

document
  .querySelector("#studentsInput")
  .addEventListener("change", async function () {
    try {
      var doc = await uploadFileAsJSON(
        document.querySelector("#studentsInput"),
        document.querySelectorAll(".fileError")[0]
      );
      studentsJson = await processStudentFile(doc.fileContent);
      debugger;
      var scoreJson = await base64ToJson(doc.fileContent);
      //studentsJson= formatStudentData(studentsSheet)
      keys = Object.keys(scoreJson[0]);

      $("#studentsTableHead").empty();

      // Create table headers
      keys.forEach((header) => {
        $("#studentsTableHead").append(`<th>${header}</th>`);
      });

      let tbody = $("#studentsTableBody");
      tbody.empty(); // Clear existing rows

      // Create a hidden file input for capturing images from the camera
      let imageInput = $(
        '<input type="file" accept="image/*" capture="environment" style="display: none;">'
      );
      let pdfInput = $(
        '<input type="file" accept="application/pdf" style="display: none;">'
      );

      $("body").append(imageInput, pdfInput); // Append inputs to the body

      // Handle image input changes (Camera capture)
      imageInput.on("change", function (event) {
        let file = event.target.files[0];
        if (file) {
          let reader = new FileReader();
          reader.onload = function (e) {
            $(imageInput.currentCell).html(
              `<img src="${e.target.result}" alt="Captured Image" style="width: 50px; height: 50px;">`
            );
          };
          reader.readAsDataURL(file);
        }
      });

      // Handle PDF upload
      pdfInput.on("change", function (event) {
        let file = event.target.files[0];
        if (file) {
          $(pdfInput.currentCell).html(
            `<a href="#" onclick="window.open('${URL.createObjectURL(
              file
            )}')">View PDF</a>`
          );
        }
      });

      // Modify table generation to allow click-based image capture
      scoreJson.forEach((row) => {
        let tr = $("<tr></tr>");
        keys.forEach((key, index) => {
          let td = $("<td></td>").text(row[key] || "");

          // Column 10 (Image Capture)
          if (index === 10) {
            td.text("Tap to capture image")
              .css("cursor", "pointer")
              .on("click", function () {
                imageInput.currentCell = this;
                imageInput.click();
              });
          }
          // Column 11 (PDF Upload)
          else if (index === 11) {
            td.text("Tap to upload PDF")
              .css("cursor", "pointer")
              .on("click", function () {
                pdfInput.currentCell = this;
                pdfInput.click();
              });
          }

          tr.append(td);
        });
        tbody.append(tr);
      });
      intTable();
    } catch (error) {
      console.error("Error uploading file:", error);
    }
  });

function intTable() {
  // Initialize Example 1
  $("#studentsListTable").dataTable({
    dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
    lengthMenu: [
      [10, 25, 50, -1],
      [10, 25, 50, "All"],
    ],
    buttons: [
      { extend: "copy", className: "btn-sm" },
      { extend: "csv", title: "ExampleFile", className: "btn-sm" },
      { extend: "pdf", title: "ExampleFile", className: "btn-sm" },
      { extend: "print", className: "btn-sm" },
    ],
  });

  document
    .querySelector("#studentsListTable_wrapper")
    .setAttribute("style", "overflow: auto;");
}

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

$("#studentsSubmitBtn").click(async function () {
  url = "postBulkStudentList";
  var jso = studentsJson;
  return HttpPost(url, jso).then(function (result) {
    $("#studentsListTable").DataTable().destroy();
    //populateTable(result)
    swal({
      title: "Thank you!",
      text: "Settings Save Successfully",
      type: "success",
    });
  });
});

function postdata() {
  resultlist = [];

  for (var i = 0; i < scoreJson.length; i++) {
    var jsonObject = {
      id: Number(id),
      score: Number(scoreJson[i].Score),
      totalScore: Number(scoreJson[i].TotalScore),
      subject: Number(
        document.getElementsByClassName(" subjectSelect")[0].value
      ),
      term: document.getElementsByClassName("termSelect")[0].value,
      studentClass: document.getElementsByClassName("classSelect")[0].value,
      academicYear:
        document.getElementsByClassName("academicYearSelect")[0].value,
      studentId: scoreJson[i].StudentId,
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
  });

function populateClasses(data) {
  data.forEach(function (d) {
    var classOptions = document.querySelector(".classSelect")[0];
    var details = $("<option>").val(d.name).text(d.name);
    $(".classSelect").append(details);
  });
}

async function fetchLookup(instId) {
  v = instId.replace(/[\[\]']+/g, "");
  v = v.replace(/\//g, "");
  var instRequest = { val: "ClassGroup" };
  return HttpPost("getLookUpByType", instRequest).then(function (result) {
    populateClassGroup(result);
    generateAcademicYears();
  });
}

function populateClassGroup(data) {
  data.forEach(function (d) {
    var details = $("<option>").val(d.id).text(d.name);
    $(".classGroupSelect").append(details);
  });
}

function populateSubjectsOptions(data) {
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

async function processStudentFile(base64String) {
  const binaryString = atob(base64String);
  const bytes = new Uint8Array(binaryString.length);

  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }

  const workbook = XLSX.read(bytes, { type: "array" });

  // Ensure the workbook contains at least two sheets
  if (workbook.SheetNames.length < 2) {
    throw new Error("The Excel file must contain at least two sheets.");
  }

  // Read the two sheets
  studentsSheet = XLSX.utils.sheet_to_json(
    workbook.Sheets[workbook.SheetNames[0]]
  );
  const parentsSheet = XLSX.utils.sheet_to_json(
    workbook.Sheets[workbook.SheetNames[1]]
  );

  return formatStudentImportRequest(studentsSheet, parentsSheet);
}

function formatStudentImportRequest(studentsSheet, parentsSheet) {
  let studentsMap = {};

  // Process Students Sheet
  studentsSheet.forEach((row) => {
    let studentId = row.studentId;

    if (!studentsMap[studentId]) {
      var dob = excelDateToJSDate(row.dateOfBirth);
      var doa = excelDateToJSDate(row.dateOfAdmission);
      studentsMap[studentId] = {
        id: id, // Set this if needed
        studentId: id,
        firstName: row.firstName,
        otherName: row.otherName || "",
        lastName: row.lastName,
        dateOfBirth: dob,
        dateOfAdmission: doa,
        placeOfBirth: row.placeOfBirth,
        gender: row.gender,
        countryOfBirth: row.countryOfBirth,
        residentialLocality: row.residentialLocality,
        picture: row.picture || "",
        birthCert: row.birthCert || "",
        denomination: row.denomination || "",
        institutionCode: row.institutionCode,
        studentClass: row.studentClass || "",
        status: row.status,
        parentsRequests: [], // Initialize parent list
      };
    }
  });

  // Process Parents Sheet
  parentsSheet.forEach((row) => {
    let studentId = row.studentId;

    if (studentsMap[studentId]) {
      let parent = {
        id: id, // Set this if needed
        firstNames: row.firstNames || "",
        lastName: row.lastName || "",
        email: row.email || "",
        contact1: row.contact1 || "",
        contact2: row.contact2 || "",
        occupation: row.occupation || "",
        placeOfWork: row.placeOfWork || "",
        parentType: row.parentType,
        studentId: row.studentId,
        institutionCode: row.institutionCode,
      };

      studentsMap[studentId].parentsRequests.push(parent);
    }
  });

  return Object.values(studentsMap);
}
