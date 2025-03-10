
id=null;
fetchInstitutionAdmissionsSettings(instId.split(",")[0]);


    window.copyrights();

    $('.saveAdmissions').click(async function() {
                var jso= postdata();
                return HttpPost("addAdmissions",jso)
                .then(function(result){
                    $('#admissionsTable').DataTable().destroy();
                    $('.dismissAdmission').click();
                    populateTable(result)
                    swal({
                           title: "Thank you!",
                           text: "Settings Save Successfully",
                           type: "success"
                      });
                    })
                });

     
    function postdata(){
        resultlist=[]
        resultlist2=[];
        let criteriaList=[];
        let categoryList=[];
        criteriaList= document.getElementsByClassName('clonableCriteria');
        categoryList= document.getElementsByClassName('clonableCategory');
        for(var i=0;i<criteriaList.length; i++){
            var jsonObject={
                            "id":Number(id),
                            "criteria":criteriaList[i].getElementsByClassName('newadmissionCriteria')[0].value,
                            "value":criteriaList[i].getElementsByClassName('newcriteriaValue')[0].value,
                            "operand":criteriaList[i].getElementsByClassName('newcriteriaOperand')[0].value
                        };
                    resultlist.push(jsonObject);
        }
        for(var i=0;i<categoryList.length; i++){
            let salesStart=(categoryList[i].getElementsByClassName('salesPeriodDatePicker')[0].value.split(" - ")[0]);
            var jsonObject2={
                            "id":Number(id),
                            "applicationFormAmount":categoryList[i].getElementsByClassName('newFormPricetxt')[0].value,
                            "applicationFormType":categoryList[i].getElementsByClassName('newFormTypetxt')[0].value,
                            "applicationFormQNT":categoryList[i].getElementsByClassName('newFormQNTtxt')[0].value,
                            "paymentMedium":categoryList[i].getElementsByClassName('newFormPaymentOptiontxt')[0].value,
                            "commencement":convertToISO(categoryList[i].getElementsByClassName('salesPeriodDatePicker')[0].value.split(" - ")[0]),
                            "closure":convertToISO(categoryList[i].getElementsByClassName('salesPeriodDatePicker')[0].value.split(" - ")[1]),
                            "appointmentPerDay":categoryList[i].getElementsByClassName('appointmentPerDay')[0].value,
                            "appointmentCommencement":convertToISO(categoryList[i].getElementsByClassName('interviewPeriodDatePicker')[0].value.split(" - ")[0]),
                            "appointmentClosure":convertToISO(categoryList[i].getElementsByClassName('interviewPeriodDatePicker')[0].value.split(" - ")[1])
                        };
                    resultlist2.push(jsonObject2);
        }
            var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
                v=v.replace(/\//g, '')
            var intermediateJsonObject={
                    "id":Number(id),
                    "admissionCriteriaList":resultlist,
                    "applicationCategoryList":resultlist2
                };
            var finalJsonObject={
                    "institution":v,
                    "admissionList":intermediateJsonObject
                };
            return finalJsonObject;
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


  async function fetchInstitutionAdmissionsSettings(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getInstitutionAdmissionSetup",instRequest)
     .then(function (result) {
     fetchLookup(result);
  })
  }

    async function fetchLookup(result1){
      var instRequest={"val":"AdmissionSetttings"}
      return  HttpPost("getLookUpByType",instRequest)
       .then(function (result) {
         populateTable(result1)
         populateClassGroup(result)
    })
    }

  function populateClassGroup(data){
  data.forEach(function(d) {
        var details="<option value='" + d.id + "'>" + d.name +" </option>"
        $(".newadmissionCriteria")[0].append(details);
    });
  }

  function populateTable(data){
  $("#admissionsTableBody").empty();

  if(data!=null){
    var bar = new Promise((resolve, reject) => {
  data.applicationCategoryList.forEach((d,index, array) =>  {
        var details="<tr id="+ d.id +"> <td>" + d.applicationFormType + " </td> <td> "+ d.applicationFormQNT +"</td> <td> "+ d.applicationFormAmount +"</td> <td> "+ d.paymentMedium +"</td> <td> "+ d.commencement.split("T")[0] + " \nTo\n " + d.closure.split("T")[0] +"</td> <td> "+ d.appointmentCommencement.replace("T"," ") + " \nTo\n  "+d.appointmentClosure.replace("T"," ") +"</td><td>"+ d.appointmentPerDay +"</td> </tr>"
        $("#admissionsTableBody").append(details);
        if (index === array.length -1) resolve();
        });
    });
    bar.then(() => {
        console.log('All done!');
        dataTableInit();
    });
    }
   }

  function dataTableInit(){
    $('#admissionsTable').dataTable({
         dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
         "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
         buttons: [
             { extend: 'copy', className: 'btn-sm' },
             { extend: 'csv', title: 'Admissions Details', className: 'btn-sm' },
             { extend: 'pdf', title: 'Admissions Details', className: 'btn-sm' },
             { extend: 'print', className: 'btn-sm' }
         ]
     });

  }

