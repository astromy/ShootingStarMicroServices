
id="";
fetchDepartment(instId.split(",")[0]);


    window.copyrights();

    $('.saveDesignation').click(async function() {
                var jso= postdata();
                return HttpPost("addDesignation",jso)
                .then(function(result){
                    $('#designationTable').DataTable().destroy();
                    $('.dismissDesignation').click();
                    populateDesignationTable(result)
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
        let joDescriptionList=[];
        joDescriptionList= document.getElementsByClassName('clonableJobDescription');

        for(var i=0;i<joDescriptionList.length; i++){
            var jsonObject={
                            "idJobDescription":Number(id),
                            "jobDescription":joDescriptionList[i].getElementsByClassName('newJobDescriptionTxt')[0].value
                        };
                    resultlist.push(jsonObject);
        }
        //for(var i=0;i<categoryList.length; i++){
            var jsonObject2={
                            "idDesignation":Number(id),
                            "name":document.getElementsByClassName('newDesignationName')[0].value,
                            "code":document.getElementsByClassName('newDesignationCode')[0].value,
                            "jobDescriptionList":resultlist
                             };
                        debugger;
                    resultlist2.push(jsonObject2);
        //}
            var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
                v=v.replace(/\//g, '')

            var finalJsonObject={
                    "institutionBECECode":v,
                    "departmentId":document.getElementsByClassName('newDesignationDepartment')[0].value,
                    "designationRequestDetails":resultlist2
                };
                debugger;
            return finalJsonObject;
    }



/*  async function fetchInstitutionDesignations(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getDesignation",instRequest)
     .then(function (result) {
     fetchDepartment(result);
  })
  }*/

    async function fetchDepartment(instId){
      var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
          v=v.replace(/\//g, '')
          var instRequest={"val":v}
      return  HttpPost("getInstitutionDepartment",instRequest)
       .then(function (result) {
         populateDepartment(result)
         populateTable(result)
    })
    }

  function populateDepartment(data){
  var n=document.getElementsByClassName("newDesignationDepartment");
    data.forEach(function(d) {
        var classOptions = document.getElementsByClassName("newDesignationDepartment")[0];
        var option = document.createElement("option");
                        option.value = d.idDepartment;
                        option.textContent = d.name;
                        classOptions.appendChild(option);
            });
  }

  function populateTable(data){
  $("#designationTableBody").empty();

  if(data!=null){
    var bar = new Promise((resolve, reject) => {
        data.forEach((d,index, array) =>  {

            d.designationList.forEach((dl,index1, array1) =>  {
                var details="<tr id="+ dl.code +"> <td>" + dl.name + " </td> <td> "+ dl.code +"</td>  </tr>"
                $("#designationTableBody").append(details);
                if (index1 === array1.length -1) resolve();
            });
        });
    });
    bar.then(() => {
        console.log('All done!');
        dataTableInit();
    });
    }
   }




  function populateDesignationTable(data){
  $("#designationTableBody").empty();

  if(data!=null){
    var bar = new Promise((resolve, reject) => {
        data.forEach((d,index, array) =>  {
            var details="<tr id="+ d.code +"> <td>" + d.name + " </td> <td> "+ d.code +"</td>  </tr>"
            $("#designationTableBody").append(details);
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
    $('#designationTable').dataTable({
         dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
         "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
         buttons: [
             { extend: 'copy', className: 'btn-sm' },
             { extend: 'csv', title: 'Designation Details', className: 'btn-sm' },
             { extend: 'pdf', title: 'Designation Details', className: 'btn-sm' },
             { extend: 'print', className: 'btn-sm' }
         ]
     });

  }

