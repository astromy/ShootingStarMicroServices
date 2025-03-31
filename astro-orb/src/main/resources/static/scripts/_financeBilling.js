
id=null;
var studentList=[];
var billList=[];
var v;
fetchLookup(instId.split(",")[0]);

    window.copyrights();



    async function fetchLookup(instId){
     v= instId.replace(/[\[\]']+/g,'')
          v=v.replace(/\//g, '')
      var instRequest={"val":"ClassGroup"}
      return  HttpPost("getLookUpByType",instRequest)
       .then(function (result) {
        fetchInstitutionBills(v);
         populateClassGroup(result);
    })
    }

  function populateClassGroup(data){
 $(".classGroupSelect option:not(:eq(0))").remove();
  data.forEach(function(d) {
         var details = $("<option>").val(d.id).text(d.name);
         $(".classGroupSelect").append(details);
    });
  }

    $('.saveBilling').click(async function() {

                var jso= postdata();
                return HttpPost("bill-students-by-institution",jso)
                .then(function(result){
                $('.dismissBilling').click();
                    swal({
                           title: "Thank you!",
                           text: "Your application is being submitted",
                           type: "success"
                      });
                    })
                });

     
    function postdata(){

        var json={
                "term":document.querySelector(".termOptions").value,
                "studentClass":document.querySelector(".billingClassOptions").value,
                "studentId":studentList,
                "billname":billList,
                "academicYear":document.querySelector(".academicYearSelect").value,
                "institutionCode":v
        }
        return json;
    }

   


  async function fetchBillings(){
    var instRequest={"institutionCode":v,
                           "studentId":"",
                        "studentClass":document.querySelector(".classSelect").value,
                                "term":document.querySelector(".termSelect").value
                      }
    return  HttpPost("get-billing-by-institutionClass",instRequest)
     .then(function (result) {
        populateTable(result);
  })
  }

    async function fetchInstitutionBills(v){
      var instRequest={"val":v}
      return  HttpPost("get-bills-by-institution",instRequest)
       .then(function (result) {
       fetchInstitutionClasses(result,v)
    })
    }


    document.querySelector('#submitBtn').addEventListener('click', async function () {
    fetchBillings();
    });



document.querySelector('.classGroupSelect').addEventListener('change', async function () {

      var vg=document.getElementsByClassName('classGroupSelect')[0].value;

      var instRequest2={"institution":v,"classGroup":document.getElementsByClassName('classGroupSelect')[0].value}
       try {
              // Await the result of the HTTP request
              const result2 = await HttpPost("getInstitutionClassesByClassGroup", instRequest2);
              populateSelectClasses(result2);
          } catch (error) {
              console.error("Error in fetchInstitutionSubject:", error);
          }
    });


  function populateSelectClasses(data){
    $(".classSelect option:not(:eq(0))").remove();
  data.forEach(function(d) {
      var classOptions = document.querySelector(".classSelect")[0];
         var details = $("<option>").val(d.name).text(d.name);
         $(".classSelect").append(details);
        });
  }



    async function fetchInstitutionClasses(bills,v){
      var instRequest={"val":v}
      return  HttpPost("getInstitutionClasses",instRequest)
       .then(function (result) {
         populateClasses(result)
         createGeneralBills(bills)
    })
    }

    async function fetchStudentsByClass(studClass){
    var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')

      var instRequest={"institutionCode":v,
                       "dateOfAdmission":"",
                          "studentClass":studClass,
                          "denomination":"",
                           "dateOfBirth":"",
                           "nationality":"",
                             "studentId":"",
                                "gender":"",
                                "status":""
                        }
      return  HttpPost("getSkimpStudentsByClass",instRequest)
       .then(function (result) {
       studentList=result.map(student => student.studentId);
       createStudentList(result);
    })
    }



  function generateAcademicYears() {
        const select = clonable.querySelector(".academicYearSelect");

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

  function populateClasses(data){

  data.forEach(function(d) {
      var classOptions = clonable.querySelector("#billingClassOptions");
      var option = document.createElement("option");
                      option.value = d.name;
                      option.textContent = d.name;
                      classOptions.appendChild(option);
          });
         generateAcademicYears();
  }

  function populateTable(data){

var bar = new Promise((resolve, reject) => {
   $('#billingTable').DataTable().destroy();
$("#billingTableBody").empty();
  data.forEach((d,index, array) =>  {
        var details="<tr> <td hidden>" + d.studentBillId + " </td> <td> "+ d.studentId +"</td><td>"+ d.studentClass +"</td> <td>"+ d.term +"</td><td>"+ d.amountDue +"</td><td>"+ d.amountPaid +"</td><td>"+ d.amountBalance +"</td></tr>"
        $("#billingTableBody").append(details);
        if (index === array.length -1) resolve();
        });
    });
    bar.then(() => {
        console.log('All done!');
        dataTableInit();
    });
   }

  function dataTableInit(){
    $('#billingTable').dataTable();

  }

function createGeneralBills(data){


    // Get the table body element where rows will be added
    const billItemsTbody = tabs.querySelector('#billItems');
    const billItemsTbody2 = tabs.querySelectorAll('.specificBills');

    // Function to create and append rows with checkboxes
    data.forEach(item => {
    if(item.bill_Cat=="General"){
        // Create a new row (tr) element
        const row = document.createElement('tr');

        // Create the checkbox cell (td)
        const checkboxCell = document.createElement('td');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.classList.add('i-checks','gBill');
        checkbox.checked = item.checked; // Set the checkbox state
        checkboxCell.appendChild(checkbox);

        // Create the description cell (td)
        const descriptionCell = document.createElement('td');
        descriptionCell.textContent = item.bill_Name;

        // Append both cells to the row
        row.appendChild(checkboxCell);
        row.appendChild(descriptionCell);

        // Append the row to the table body
        billItemsTbody.appendChild(row);
        }else{
        billItemsTbody2.forEach(tb => {

        // Create a new row (tr) element
        const row = document.createElement('tr');

        // Create the checkbox cell (td)
        const checkboxCell = document.createElement('td');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.classList.add('i-checks','sBill');
        checkbox.checked = item.checked; // Set the checkbox state
        checkboxCell.appendChild(checkbox);

        // Create the description cell (td)
        const descriptionCell = document.createElement('td');
        descriptionCell.textContent = item.bill_Name;

        // Append both cells to the row
        row.appendChild(checkboxCell);
        row.appendChild(descriptionCell);

        // Append the row to the table body
        tb.appendChild(row);
         });

        }
    });
}

function createStudentList(data){


    // Get the table body element where rows will be added
    const billItemsTbody2 = tabs.querySelectorAll('.studentsList');


    // Function to create and append rows with checkboxes
    data.forEach((item,index) =>
        billItemsTbody2.forEach(tb => {

        // Create a new row (tr) element
        const row = document.createElement('div');
        row.classList.add('row');
        if(index % 2 ==0){
        row.style.backgroundColor = '#f9f9f9';
        row.style.border = '1px solid #ddd';
        }

        // Create the checkbox cell (td)
        const checkboxCell = document.createElement('div');
        checkboxCell.classList.add('col-sm-2');
        checkboxCell.style.paddingTop='13px;';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.classList.add('i-checks');
        checkbox.checked = item.checked; // Set the checkbox state
        checkboxCell.appendChild(checkbox);

        // Create the description cell (td)
        const descriptionCell = document.createElement('div');
        descriptionCell.classList.add('col-sm-10');
        descriptionCell.classList.add('border');
        descriptionCell.classList.add('border-info');
        descriptionCell.innerHTML = studentObject(item);

        // Append both cells to the row
        row.appendChild(checkboxCell);
        row.appendChild(descriptionCell);

        // Append the row to the table body
        tb.appendChild(row);
         }));
    };

function studentObject(data){
    // Return HTML string instead of divs
        /*checkboxCell.classList.add('border');
        checkboxCell.classList.add('border-info');*/
    return `
        <div class="row border border-info">
            <div class="col-sm-12">
                <label style="font-weight: unset studentId">${data.studentId}</label>
            </div>
            <div class="col-sm-12">
                <label style="font-weight: unset">${data.lastName} ${data.firstName} ${data.otherName}</label>
            </div>
        </div>
    `;
}

function buildGeneralBill(el){
billList.push(el.closest('tr').querySelectorAll('td')[1].innerHTML);
}
