
id="";
fetchInstitutionAdmissionsSettings(instId.split(",")[0]);


    $('.saveAdmissions').click(async function() {

                postdata();
                var jso= buildJson();
                return HttpPost("addLookUps",jso)
                .then(function(result){
                    $('#admissionsTable').DataTable().destroy();
                    $('.dismissAdmission').click();
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

    function buildJson(){
    var resultlist=[]
    for(var i=0;i<name.length; i++){
        var jsonObject={
            "id":id,
            "name":name[i],
            "type":type
        };
        resultlist.push(jsonObject);
      }
        return resultlist
    }


  async function fetchInstitutionAdmissionsSettings(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    debugger;
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
    var bar = new Promise((resolve, reject) => {
  data.applicationCategoryList.forEach((d,index, array) =>  {
        var details="<tr id="+ d.id +"> <td>" + d.applicationFormType + " </td> <td> "+ d.applicationFormQNT +"</td> <td> "+ d.applicationFormAmount +"</td> <td> "+ d.paymentMedium +"</td> <td> "+ d.commencement.split("T")[0] + " \nTo " + d.closure.split("T")[0] +"</td> <td> "+ d.appointmentCommencement.replace("T"," ") + " \nTo  "+d.appointmentClosure.replace("T"," ") +"</td><td>"+ d.appointmentPerDay +"</td> </tr>"
        $("#admissionsTableBody").append(details);
        if (index === array.length -1) resolve();
        });
    });
    bar.then(() => {
        console.log('All done!');
        dataTableInit();
    });
   }

  function dataTableInit(){
    $('#admissionsTable').dataTable({
         dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
         "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
         buttons: [
             { extend: 'copy', className: 'btn-sm' },
             { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
             { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
             { extend: 'print', className: 'btn-sm' }
         ]
     });

  }

