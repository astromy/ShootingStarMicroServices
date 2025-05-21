window.copyrights();
id = null;
var keys, scoreJson, url;
var v, reportDataJSON;
fetchLookup(instId.split(",")[0]);

var keys, scoreJson, url;

var elm = document.querySelector(".reportPublishBtn");
elm.addEventListener("click", function () {});

async function displayReport(data) {
  try {
    const scoreJson = data;
    const tbody = $("#reportTableBody");
    const thead = $("#reportTableHead");

    tbody.empty();
    thead.empty();

    const studentList = scoreJson.studentReportResponseList || [];
    if (studentList.length === 0) {
      tbody.append("<tr><td colspan='100%'>No student reports available</td></tr>");
      return;
    }

    // 1. Extract all unique subjects from all students
    const allSubjects = [];
    const subjectSet = new Set();

    studentList.forEach(student => {
      student.studentAssessment?.forEach(assessment => {
        if (!subjectSet.has(assessment.subject)) {
          subjectSet.add(assessment.subject);
          allSubjects.push(assessment.subject);
        }
      });
    });

    // 2. Build header rows
    let headerRow1 = `
      <tr>
        <th rowspan="2">No</th>
        <th rowspan="2">Student ID</th>
        <th rowspan="2">Name</th>
    `;
    allSubjects.forEach(subject => {
      headerRow1 += `<th colspan="3">${subject}</th>`;
    });
    headerRow1 += '</tr>';

    let headerRow2 = '<tr>';
    allSubjects.forEach(() => {
      headerRow2 += `
        <th>Class Score</th>
        <th>Exams Score</th>
        <th>Total Score</th>
      `;
    });
    headerRow2 += '</tr>';

    thead.html(headerRow1 + headerRow2);

    // 3. Build student rows
    const rows = [];
    let counter = 1;

    studentList.forEach(student => {
      const fullName = [student.firstName, student.otherName, student.lastName].filter(Boolean).join(" ");
      let row = `
        <td>${counter++}</td>
        <td>${student.studentId || ""}</td>
        <td>${fullName}</td>
      `;

      allSubjects.forEach(subject => {
        const assessment = student.studentAssessment?.find(a => a.subject === subject);
        if (assessment) {
          row += `
            <td>${assessment.classScore ?? '-'}</td>
            <td>${assessment.examsScore ?? '-'}</td>
            <td>${assessment.totalScore ?? '-'}</td>
          `;
        } else {
          row += '<td>-</td><td>-</td><td>-</td>';
        }
      });

      rows.push(`<tr>${row}</tr>`);
    });

    tbody.append(rows.join(""));

  } catch (error) {
    console.error("Error building report:", error);
    tbody.html(`<tr><td colspan='100%'>Error: ${error.message}</td></tr>`);
  }
}


/*
document
  .querySelector("#reportTable_wrapper")
  .setAttribute("style", "overflow: auto;");
*/

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

url = "generateBroadsheet";

$("#reportGenerateBtn").click(async function () {
  const jso = postdata();
  return HttpPost(url, jso).then(function (result) {
    if ($.fn.DataTable.isDataTable("#reportTable")) {
      $("#reportTable").DataTable().destroy();
    }

    reportDataJSON = result;
    displayReport(result); // build headers + rows

    // Reinitialize DataTable AFTER DOM is ready
  setTimeout(() => {
    $('#reportTable').DataTable({
      dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
      scrollX: true,
      pageLength: 25,
      buttons: ['copy', 'csv', 'excel', 'pdf', 'print']
    });
  }, 100);

    swal({
      title: "Thank you!",
      text: "Operation Successfully",
      type: "success"
    });
  });
});


function postdata() {
  var jsonObject = {
    classGroup: document.getElementsByClassName("classGroupSelect")[0].value,
    term: document.getElementsByClassName("termSelect")[0].value,
    targetClass: document.getElementsByClassName("classSelect")[0].value,
    academicYear:
      document.getElementsByClassName("academicYearSelect")[0].value,
    institutionCode: v,
    gradingSetting:Number(document.getElementsByClassName("gradingSettingSelect")[0].value)
  };
  return jsonObject;
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
  .querySelector("#reportPublishBtn")
  .addEventListener("click", function () {
    url = "postStudentReports";

    var jso = postdata();
    return HttpPost(url, jso).then(function (result) {
      $("#reportTable").DataTable().destroy();
      reportDataJSON = result;
      generatePDF(reportDataJSON);
      swal({
        title: "Thank you!",
        text: "Operation Successful",
        type: "success",
      });
    });
  });

document
  .querySelector("#reportExportBtn")
  .addEventListener("click", function () {
    if (reportDataJSON) {
      generatePDF(reportDataJSON);
    } else {
    }
  });

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
      populateClasses(result2);
    } catch (error) {
      console.error("Error in fetchInstitutionSubject:", error);
    }
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
  v = instId.replace(/[\[\]']+/g, "");
  v = v.replace(/\//g, "");
  var instRequest = { val: "ClassGroup" };
  return HttpPost("getLookUpByType", instRequest).then(function (result) {
    populateClassGroup(result);
    generateAcademicYears();
    fetchInstitutionGrading();
  });
}


async function fetchInstitutionGrading() {
  var instRequest = { val: v };
  return HttpPost("getInstitutionGradingSetting", instRequest).then(function (
    result
  ) {
  $(".gradingSettingSelect option:not(:eq(0))").remove();
  result.forEach(function (d) {
    var details = $("<option>").val(d.id).text(d.classPercentage + " / "+ d.examsPercentage);
    $(".gradingSettingSelect").append(details);
  });
  });
}

function populateClassGroup(data) {
  $(".classGroupSelect option:not(:eq(0))").remove();
  data.forEach(function (d) {
    var details = $("<option>").val(d.id).text(d.name);
    $(".classGroupSelect").append(details);
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

function generatePDF(assessments) {
  const { jsPDF } = window.jspdf;
  const terminalReport = new jsPDF();
  if (typeof terminalReport.autoTable !== "function") {
    terminalReport.autoTable = window.jspdf?.autoTable; // Try to attach it manually
  }
  const offset = 5;
  const WIDTH = terminalReport.internal.pageSize.width;
  const HEIGHT = terminalReport.internal.pageSize.height;
  terminalReport.setFontSize(10);
  terminalReport.setLineWidth(1.2);
  const logoWidth = 30;
  const logoHeight = 30;
  var xcord, ycord;

  // Institution Details
  const institution = assessments.institutionDetail;
  const studentReports = assessments.studentReportResponseList;

  console.log("jsPDF:", window.jspdf); // Should not be undefined
  console.log("autoTable:", window.jspdf?.autoTable); // Should be a function
  console.log("autoTable on jsPDF instance:", typeof new jsPDF().autoTable);

  let firstPage = true;

  studentReports.forEach((student) => {
    if (!firstPage) terminalReport.addPage();
    firstPage = false;

    // Automatically split text into lines
    var wrappedText;

    xcord = 10;
    ycord = 53;

    // ** Add Crest (Logo) if available **
    if (institution.crest) {
      let img = "data:image/jpeg;base64," + institution.crest;
      terminalReport.addImage(
        img,
        "jpeg",
        offset + 10,
        offset + 15,
        logoWidth,
        logoHeight
      );
    }

    // ** Add Head Signature if available **
    if (institution.headSignature) {
      let img = "data:image/jpeg;base64," + institution.headSignature;
      terminalReport.addImage(
        img,
        "jpeg",
        xcord + 133,
        ycord + 162 + 20,
        logoWidth,
        logoHeight / 2
      );
    }

    // ** Add Institution Name & Slogan **
    terminalReport.setFontSize(16);
    terminalReport.setFont("helvetica", "bold");
    terminalReport.setTextColor("#058709");
    terminalReport.text(
      institution.name.toUpperCase(),
      offset + logoWidth + 22.5,
      offset + 20
    );
    terminalReport.setFontSize(9);
    terminalReport.setFont("helvetica", "normal");
    terminalReport.setTextColor("#058709");
    terminalReport.text(
      "..." + institution.slogan,
      offset + logoWidth + 22.5,
      offset + 24
    );

    // ** Institution Contact Details **
    terminalReport.setFontSize(9);
    terminalReport.text(
      institution.postalAddress,
      offset + xcord + 65 * 2,
      offset + 32
    );
    terminalReport.setFontSize(9);
    terminalReport.text(
      "Email: " + institution.email,
      offset + xcord + 65 * 2,
      offset + 36
    );
    terminalReport.setFontSize(9);
    terminalReport.text(
      "Website: " + institution.website,
      offset + xcord + 65 * 2,
      offset + 40
    );
    terminalReport.setFontSize(9);
    terminalReport.text(
      institution.city + " " + institution.region + " " + institution.country,
      offset + xcord + 65 * 2,
      offset + 44
    );

    // ** Add Student Details **
    terminalReport.setFontSize(9);
    terminalReport.setTextColor("#000");
    terminalReport.text(`Student ID: ${student.studentId}`, xcord, ycord + 4);
    terminalReport.text("Student Name: ", xcord, ycord + 4 * 2);

    var name = `${student.firstName} ${student.otherName || ""} ${
      student.lastName
    }`;
    wrappedText = terminalReport.splitTextToSize(name, 53);
    terminalReport.text(wrappedText, 32, ycord + 4 * 2);
    terminalReport.text(`Gender: ${student.gender}`, xcord, ycord + 4 * 3);
    terminalReport.text(
      `Class: ${student.studentAssessment[0]?.studentClass || "N/A"}`,
      xcord,
      ycord + 4 * 4
    );
    terminalReport.text(
      `Term: ${student.studentAssessment[0]?.term || "N/A"}`,
      xcord,
      ycord + 4 * 5
    );
    terminalReport.text(
      `Academic Year: ${student.studentAssessment[0]?.academicYear || "N/A"}`,
      xcord,
      ycord + 4 * 6
    );

    // ** Add Class Details **
    terminalReport.setFontSize(9);
    terminalReport.setTextColor("#000");
    terminalReport.text(
      "Program of Study:" + institution.classGroup,
      xcord + 65,
      ycord + 4 * 1
    );
    terminalReport.text(
      `Student Class: ${student.studentAssessment[0]?.studentClass || "N/A"}`,
      xcord + 65,
      ycord + 4 * 2
    );
    terminalReport.text(
      `Class Size: ${studentReports.length}`,
      xcord + 65,
      ycord + 4 * 3
    );
    terminalReport.text(
      "Class Average Mark:" + institution.classAverage,
      xcord + 65,
      ycord + 4 * 4
    );
    terminalReport.text("", xcord + 65, ycord + 4 * 5); //Empty field waiting for information
    terminalReport.text("", xcord + 65, ycord + 4 * 6); //Empty field waiting for information

    // ** Add Performance Details **
    terminalReport.setFontSize(9);
    terminalReport.setTextColor("#000");
    terminalReport.text(
      `Student Average Grade: ${student.averageGrade || "N/A"}`,
      xcord + 65 * 2,
      ycord + 4 * 1
    );
    terminalReport.text(
      `Student General Remarks: ${student.averageRemark || "N/A"}`,
      xcord + 65 * 2,
      ycord + 4 * 2
    );
    terminalReport.text(
      `Student Average Mark: ${student.averageScore || "N/A"}`,
      xcord + 65 * 2,
      ycord + 4 * 3
    );
    terminalReport.text(
      `Student Average Position: ${student.averageScore || "N/A"}`,
      xcord + 65 * 2,
      ycord + 4 * 4
    );
    terminalReport.text("", xcord + 65 * 2, ycord + 4 * 5); //Empty field waiting for information
    terminalReport.text("", xcord + 65 * 2, ycord + 4 * 6); //Empty field waiting for information

    // ** Line Separator **
    terminalReport.setLineWidth(0.5);
    terminalReport.line(10, ycord + 4 * 7, 200, ycord + 4 * 7); // Horizontal line
    terminalReport.line(10, ycord + 4 * 7 + 1, 200, ycord + 4 * 7 + 1); // Horizontal line

    // ** Table Headers **
    const headers = [
      "Subject",
      "Class Score",
      "Exams Score",
      "Total Score",
      "Grade",
      "Position",
      "Remarks",
    ];

    // ** Table Data **
    const tableData = student.studentAssessment.map((r) => [
      r.subject,
      isNaN(r.classScore) ? "N/A" : Number(r.classScore).toFixed(2),
      isNaN(r.examsScore) ? "N/A" : Number(r.examsScore).toFixed(2),
      isNaN(r.totalScore) ? "N/A" : Number(r.totalScore).toFixed(2),
      r.grade || "N/A",
      r.position || "N/A",
      r.gradeRemarks || "N/A",
    ]);

    // ** Add Table Below Student Details **
    terminalReport.autoTable({
      head: [headers],
      body: tableData,
      startY: 90, // Position below header
      styles: { fontSize: 10 },
      headStyles: { fillColor: ["#7eb2f5"] },
    });

    // ** Line Separator **
    terminalReport.setTextColor("#000");
    terminalReport.setLineWidth(0.5);
    terminalReport.line(10, ycord + 4 * (7 + 30), 200, ycord + 4 * (7 + 30)); // Horizontal line

    terminalReport.text("Attendance ", xcord, ycord + 155);
    terminalReport.setLineDash([0.5, 0.5]);
    terminalReport.line(xcord + 20, ycord + 155, 70, ycord + 155); // Horizontal line

    terminalReport.text("Out of ", xcord + 61, ycord + 155);
    terminalReport.setLineDash([0.5, 0.5]);
    terminalReport.line(xcord + 78, ycord + 155, 141, ycord + 155); // Horizontal line

    terminalReport.text("Promoted to ", xcord + 133, ycord + 155);
    terminalReport.setLineDash([0.5, 0.5]);
    terminalReport.line(xcord + 155, ycord + 155, 200, ycord + 155); // Horizontal line

    terminalReport.text("Form Teachers Remarks ", xcord, ycord + 162);
    terminalReport.setLineDash([0.5, 0.5]);
    terminalReport.line(xcord + 36, ycord + 162, 200, ycord + 162); // Horizontal line

    terminalReport.text("House Heads' Remarks ", xcord, ycord + (162 + 7));
    terminalReport.setLineDash([0.5, 0.5]);
    terminalReport.line(xcord + 40, ycord + 162 + 7, 200, ycord + 162 + 7); // Horizontal line

    terminalReport.text("Heads' Remarks ", xcord, ycord + (162 + 14));
    terminalReport.setLineDash([0.5, 0.5]);
    terminalReport.line(xcord + 40, ycord + 162 + 14, 200, ycord + 162 + 14); // Horizontal line

    terminalReport.text(
      "Heads' Signature ",
      xcord + 133,
      ycord + 162 + 23 + logoHeight / 2
    );

    const currentYear = new Date().getFullYear();
    const footerText = `© Astromy LLC 2013-${currentYear}`;

    // Add footer
    terminalReport.setFontSize(10);
    terminalReport.text(
      footerText,
      10,
      terminalReport.internal.pageSize.height - 10
    );
  });

  // ** Save or Download PDF **
  terminalReport.save(
    studentReports[0].studentAssessment[0]?.studentClass +
      " Broadsheet Report.pdf"
  );
}
