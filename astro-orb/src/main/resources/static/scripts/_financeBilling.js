
id="";
fetchBillings(instId.split(",")[0]);

    window.copyrights();

    $('.saveBilling').click(async function() {

                var jso= postdata();
                return HttpPost("addLookUps",jso)
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
   var classGroup=[];
    classGroup= document.getElementsByClassName('newClassGrouptxt');
    for(var i=0;i<classGroup.length; i++){
    name[i]=classGroup[i].value;
    }
    }

   


  async function fetchBillings(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"institutionCode":v,
                           "studentId":"",
                        "studentClass":"",
                                "term":""
                      }
    return  HttpPost("get-billing-by-institution",instRequest)
     .then(function (result) {
     fetchInstitutionBills(result,v);
  })
  }

    async function fetchInstitutionBills(result1,v){
      var instRequest={"val":v}
      return  HttpPost("get-bills-by-institution",instRequest)
       .then(function (result) {
       fetchInstitutionClasses(result1,result,v)
    })
    }

    async function fetchInstitutionClasses(billing,bills,v){
      var instRequest={"val":v}
      return  HttpPost("getInstitutionClasses",instRequest)
       .then(function (result) {
         populateClasses(result)
         createGeneralBills(bills)
         populateTable(billing)
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
      return  HttpPost("getStudentsByClass",instRequest)
       .then(function (result) {
       createStudentList(result);
    })
    }

  function populateClasses(data){

  data.forEach(function(d) {
      var classOptions = clonable.querySelector("#billingClassOptions");
      var option = document.createElement("option");
                      option.value = d.name;
                      option.textContent = d.name;
                      classOptions.appendChild(option);
          });

 /* data.forEach(function(d) {
        var details="<option value='" + d.id + "'>" + d.name +" </option>"
        $(".billingClassOptions").append(details);
    });*/
  }

  function populateTable(data){
var bar = new Promise((resolve, reject) => {
  data.forEach((d,index, array) =>  {
        var details="<tr> <td hidden>" + d.id + " </td> <td> "+ d.billname +"</td><td>"+ d.term +"</td> <td>"+ d.term +"</td><td>"+ d.term +"</td><td>"+ d.term +"</td><td>"+ d.billingDate +"</td></tr>"
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
        checkbox.classList.add('i-checks');
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
        checkbox.classList.add('i-checks');
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
                <label style="font-weight: unset">${data.studentId}</label>
            </div>
            <div class="col-sm-12">
                <label style="font-weight: unset">${data.lastName} ${data.firstName} ${data.otherName}</label>
            </div>
        </div>
    `;
}