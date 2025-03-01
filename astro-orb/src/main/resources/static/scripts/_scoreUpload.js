 window.copyrights();
 id="";
var keys,scoreJson,url;
var v;
fetchLookup(instId.split(",")[0])
 var elm=document.querySelector(".scoreUploadBtn");
 elm.addEventListener('click', function() {
                                    document.querySelector('#scoreInput').click();
                                   });


document.querySelector('#scoreInput').addEventListener('change', async function () {
    try {
        var doc = await uploadFileAsJSON(document.querySelector('#scoreInput'), document.querySelectorAll(".fileError")[0]);
        scoreJson=await base64ToJson(doc.fileContent);
        keys = Object.keys(scoreJson[0]);


         $("#scoreTableHead").empty();

            // Loop through the list and create <th> elements
            keys.forEach(header => {
                $("#scoreTableHead").append(`<th>${header}</th>`);
            });
            let tbody = $("#scoreTableBody");
            tbody.empty(); // Clear existing rows

                    // Loop through the JSON data and create table rows
                     scoreJson.forEach(row => {
                                let tr = $("<tr></tr>");
                                keys.forEach(key => {
                                    tr.append(`<td>${row[key] || ""}</td>`); // Insert corresponding data
                                });
                                tbody.append(tr);
                            });
    } catch (error) {
        console.error("Error uploading file:", error);
    }
});
document.querySelector('#subjectScoreTable_wrapper').setAttribute("style", "overflow: auto;");

var selectedValue = document.querySelector("#scoreTypeControl").parentElement.querySelector("label").innerHTML;

document.querySelector("#scoreTypeControl").addEventListener("change", function() {
      var selectedValue = this.parentElement.querySelector("label").innerHTML;

        if (selectedValue == "Class Score") {
        this.parentElement.querySelector("label").innerHTML="Exams Score"
        url="uploadExamsScores"
        }else{
        this.parentElement.querySelector("label").innerHTML="Class Score"
        url="uploadAssesmentScores"
        }
    });

     if(selectedValue=="Class Score"){
     url="uploadAssesmentScores"
     }else{
     url="uploadExamsScores"
     }


    $('#scoreSubmitExport').click(async function() {
                var jso= postdata();
                return HttpPost(url,jso)
                .then(function(result){
                    $('#subjectScoreTable').DataTable().destroy();
                    //populateTable(result)
                    swal({
                           title: "Thank you!",
                           text: "Settings Save Successfully",
                           type: "success"
                      });
                    })
                });


     
    function postdata(){
        resultlist=[]

        for(var i=0;i<scoreJson.length; i++){
            var jsonObject={
                            "id":Number(id),
                            "score":Number(scoreJson[i].Score),
                            "totalScore":Number(scoreJson[i].TotalScore),
                            "subject":Number(document.getElementsByClassName(' subjectSelect')[0].value),
                            "term":document.getElementsByClassName('termSelect')[0].value,
                            "studentClass":document.getElementsByClassName('classSelect')[0].value,
                            "academicYear":document.getElementsByClassName('academicYearSelect')[0].value,
                            "studentId":scoreJson[i].StudentId,
                            "dateTime":new Date().toString(),
                            "institutionCode":v
                        };
                    resultlist.push(jsonObject);
        }
            return resultlist;
    }


    function convertToISO(dateStr) {
        // Split the input string into date and time
        const [datePart, timePart] = dateStr.split(' ');

        // Split the date into components (month/day/year)
        const [month, day, year] = datePart.split('/');

        // Combine the components into an ISO format date string
        const isoDate = `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;

        // Return the final ISO string with time (timePart already in HH:mm format)
        return `${isoDate} ${timePart}:00`;
    }



    async function fetchInstitutionClasses(v){
      var instRequest={"val":v}
      return  HttpPost("getInstitutionClasses",instRequest)
       .then(function (result) {
         populateClasses(result)
    })
    }

document.querySelector('.classGroupSelect').addEventListener('change', async function () {

      var vg=document.getElementsByClassName('classGroupSelect')[0].value;

      var instRequest={"id":0,"name":v,"classGroup":document.getElementsByClassName('classGroupSelect')[0].value,"preference":0}
      var instRequest2={"institution":v,"classGroup":document.getElementsByClassName('classGroupSelect')[0].value}
       try {
              // Await the result of the HTTP request
              const result = await HttpPost("getInstitutionSubjectsAndClassGroup", instRequest);
              const result2 = await HttpPost("getInstitutionClassesByClassGroup", instRequest2);
              populateSubjectsOptions(result);
              populateClasses(result2);
          } catch (error) {
              console.error("Error in fetchInstitutionSubject:", error);
          }
    });


  function populateClasses(data){

  data.forEach(function(d) {
      var classOptions = document.querySelector(".classSelect")[0];
         var details = $("<option>").val(d.name).text(d.name);
         $(".classSelect").append(details);
        });
  }

    async function fetchLookup(instId){
     v= instId.replace(/[\[\]']+/g,'')
          v=v.replace(/\//g, '')
      var instRequest={"val":"ClassGroup"}
      return  HttpPost("getLookUpByType",instRequest)
       .then(function (result) {
         populateClassGroup(result);
         generateAcademicYears();
    })
    }

  function populateClassGroup(data){
  data.forEach(function(d) {
         var details = $("<option>").val(d.id).text(d.name);
         $(".classGroupSelect").append(details);
    });
  }

  function populateSubjectsOptions(data){
  data.forEach(function(d) {
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


/*  function dataTableInit(){
    $('#subjectScoreTable').dataTable({
         dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
         "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
         buttons: [
             { extend: 'copy', className: 'btn-sm' },
             { extend: 'csv', title: 'Admissions Details', className: 'btn-sm' },
             { extend: 'pdf', title: 'Admissions Details', className: 'btn-sm' },
             { extend: 'print', className: 'btn-sm' }
         ]
     });

  }*/

