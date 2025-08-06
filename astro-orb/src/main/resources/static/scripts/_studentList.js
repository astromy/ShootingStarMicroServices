window.copyrights();
id = "";
var keys, studentsJson, url;
var studentsSheet;
var v;
fetchLookup(instId.split(",")[0]);

var elm = document.querySelector(".studentsUploadBtn");
elm.addEventListener("click", function () {
  document.querySelector("#studentsInput").click();
});

document
  .querySelector("#studentsInput")
  .addEventListener("change", async function () {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    try {
      var doc = await uploadFileAsJSON(
        document.querySelector("#studentsInput"),
        document.querySelectorAll(".fileError")[0]
      );
      window.studentsJson = await processStudentFile(doc.fileContent);
      var scoreJson = await base64ToJson(doc.fileContent);
      keys = Object.keys(scoreJson[0]);

      $("#studentsTableHead").empty();

      // Create table headers
      keys.forEach((header) => {
        $("#studentsTableHead").append(`<th>${header}</th>`);
      });

      let tbody = $("#studentsTableBody");
      tbody.empty();

      let imageInput = $(
        '<input type="file" accept="image/*" capture="environment" style="display: none;">'
      );
      let pdfInput = $(
        '<input type="file" accept="application/pdf" style="display: none;">'
      );

      $("body").append(imageInput, pdfInput);

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

      pdfInput.on("change", function (event) {
        let file = event.target.files[0];
        if (file) {
          $(pdfInput.currentCell).html(
            `<a href="#" onclick="window.open('${URL.createObjectURL(
              file
            )}')">View PDF</a>`
          );
        }
        $('.splash').css('display', 'none')
      });

      scoreJson.forEach((row) => {
        let studentId = row.studentId || row['Student ID'];
        let tr = $("<tr></tr>").addClass('student-row').data('student-id', studentId);
        
        keys.forEach((key, index) => {
          let td = $("<td></td>").text(row[key] || "");

          if (index === 10) {
            td.text("Tap to capture image")
              .css("cursor", "pointer")
              .on("click", function () {
                imageInput.currentCell = this;
                imageInput.click();
              });
          }
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
  $("#studentsListTable").dataTable({
    dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
    lengthMenu: [
      [10, 25, 50, -1],
      [10, 25, 50, "All"],
    ],
    buttons: [
      { extend: "copy", className: "btn-sm" },
      { extend: "csv", title: "Students List", className: "btn-sm" },
      { extend: "pdf", title: "Students List", className: "btn-sm" },
      { extend: "print", className: "btn-sm" },
    ],
  });

  document
    .querySelector("#studentsListTable_wrapper")
    .setAttribute("style", "overflow: auto;");
}


$("#studentsSubmitBtn").click(async function () {
  $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
  url = "getStudentsByDynamicData";
  var jso = postdata();
  return fetchPost(url, jso).then(function (result) {
    $("#studentsListTable").DataTable().destroy();
    $("#studentsTableBody").empty();
    $('.splash').css('display', 'none')
    buildTable(result);
    window.studentsJson=result;
    swal({
      title: "Thank you!",
      text: "Operation Completed Successfully",
      type: "success",
    });
  });
});


function buildTable(result){

if (result && result.length > 0) {
            result.forEach(student => {
                const row = `
                    <tr class="student-row" data-student-id="${student.studentId}">
                        <td>${student.studentId || ''}</td>
                        <td>${student.lastName || ''}</td>
                        <td>${student.firstName || ''}</td>
                        <td>${student.otherName || ''}</td>
                        <td>${student.gender || ''}</td>
                        <td>${student.studentClass || ''}</td>
                        <td>${student.dateOfBirth || ''}</td>
                        <td>${student.dateOfAdmission || ''}</td>
                        <td>${student.placeOfBirth || ''}</td>
                        <td>${student.countryOfBirth || ''}</td>
                        <td>${student.nationality || ''}</td>
                        <td>${student.denomination || ''}</td>
                        <td>${student.status || ''}</td>
                        <td>
                            <button class="btn btn-xs btn-info view-parents"
                                    data-student-id="${student.studentId}">
                                View Parents
                            </button>
                        </td>
                    </tr>
                `;
                $("#studentsTableBody").append(row);
            });

            // Initialize DataTable
            intTable();

            // Add click handler for view parents buttons
            $('.view-parents').click(function() {
                const studentId = $(this).data('student-id');
                showParentInfo(studentId);
            });
        } else {
            swal({
                title: "No Students Found",
                text: "No student records were found for this institution",
                type: "info"
            });
        }

}


function postdata() {
var studentClass,studentStatus,institutionCode;

 studentClass= document.getElementsByClassName("classSelect")[0].value;
 if (studentClass && studentClass.toLowerCase().includes("select")) {
     studentClass = null;  // Set to null if it's a default/unselected value
 }
 studentStatus= document.getElementsByClassName("studentStatus")[0].value;
 if (studentStatus && studentStatus.toLowerCase().includes("select")) {
     studentStatus = null;  // Set to null if it's a default/unselected value
 }
 institutionCode= v;

 var key = ["studentClass","status","institutionCode"];
 var val = [studentClass,studentStatus,institutionCode];


    var jsonObject = {
    key:key,
    val:val
    }
  return jsonObject;
}

function convertToISO(dateStr) {
  const [datePart, timePart] = dateStr.split(" ");
  const [month, day, year] = datePart.split("/");
  const isoDate = `${year}-${month.padStart(2, "0")}-${day.padStart(2, "0")}`;
  return `${isoDate} ${timePart}:00`;
}

async function fetchInstitutionClasses(v) {
  var instRequest = { val: v };
  return fetchPost("getInstitutionClasses", instRequest).then(function (result) {
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
      const result = await fetchPost(
        "getInstitutionSubjectsAndClassGroup",
        instRequest
      );
      const result2 = await fetchPost(
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
  return fetchPost("getLookUpByType", instRequest).then(function (result) {
    populateClassGroup(result);
    $('.splash').css('display', 'none')
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
  select.innerHTML = "";

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

  if (workbook.SheetNames.length < 2) {
    throw new Error("The Excel file must contain at least two sheets.");
  }

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

  studentsSheet.forEach((row) => {
    let studentId = row.studentId;

    if (!studentsMap[studentId]) {
      var dob = excelDateToJSDate(row.dateOfBirth);
      var doa = excelDateToJSDate(row.dateOfAdmission);
      studentsMap[studentId] = {
        id: id,
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
        parentsRequests: [],
      };
    }
  });

  parentsSheet.forEach((row) => {
    let studentId = row.studentId;

    if (studentsMap[studentId]) {
      let parent = {
        id: id,
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

// Parent Modal Functionality
$(document).on('click', '.student-row', function() {
    const studentId = $(this).data('student-id');
    showParentInfo(studentId);
});

function showParentInfo(studentId) {
;
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    
    const student = window.studentsJson.find(s => s.studentId === studentId);
    
    if (student && student.studentParents && student.studentParents.length > 0) {
        const parentTable = $('#parentInfoTable');
        parentTable.empty();
        
        student.studentParents.forEach(parent => {
            const parentRow = $(`
                <tr>
                    <td>${parent.firstNames || ''} ${parent.lastName || ''}</td>
                    <td>${parent.contact1 || ''} ${parent.contact2 ? '<br/>' + parent.contact2 : ''}</td>
                    <td>${parent.email || ''}</td>
                    <td>${parent.parentType || ''}</td>
                </tr>
            `);
            parentTable.append(parentRow);
        });
        
        $('#parentModalLabel').text(`Parents of ${student.firstName} ${student.lastName}`);
        $('#parentModal').modal('show');
    } else {
        swal({
            title: "No Parents Found",
            text: "No parent information available for this student",
            type: "info"
        });
    }
    
    $('.splash').css('display', 'none');
}