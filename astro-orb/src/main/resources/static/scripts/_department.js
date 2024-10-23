    id="";
    name=[];
    resultlist=[]
    type= $('[name="type"]').val();
//fetchLookup();
fetchInstitutionDepartment(instId.split(",")[0]);


    window.copyrights();

    $('.saveDepartment').click(async function() {

        var jso= postdata();
        return HttpPost("addDepartment",jso)
        .then(function(result){
           $('#departmentTable').DataTable().destroy();
           $('.dismissDepartment').click();
        swal({
               title: "Thank you!",
               text: "Your application is being submitted",
               type: "success"
          });
        })
    });


    function postdata(){
    let departments=[];
    let departmentDetailsList=[]
    resultlist=[];
    departments= document.getElementsByClassName('newDepartmenttxt');
    for(var i=0;i<departments.length; i++){
    var jsonObject={
                "id":id,
                "name":departments[i].value,
                "designationList":departmentDetailsList
            };
            resultlist.push(jsonObject);
    }
    var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
        v=v.replace(/\//g, '')
    var finalJsonObject={
                    "institution":v,
                    "departmentDetailsList":resultlist
                };
    return finalJsonObject;
    }

   /* function buildJson(){
    for(var i=0;i<name.length; i++){
        var jsonObject={
            "id":id,
            "name":name[i],
            "type":type
        };
        resultlist.push(jsonObject);
      }
        return resultlist
    }*/


  async function fetchInstitutionDepartment(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getInstitutionDepartment",instRequest)
     .then(function (result) {
       populateTable(result)
  })
  }

    async function fetchLookup(){
      var instRequest={"val":"ClassGroup"}
      return  HttpPost("getLookUpByType",instRequest)
       .then(function (result) {
         populateClassGroup(result)
    })
    }

  function populateClassGroup(data){
  data.forEach(function(d) {
        var details="<option value='" + d.id + "'>" + d.name +" </option>"
        $("#departmentTableBody").append(details);
    });
  }

  function populateTable(data){
  $("#departmentTableBody").empty();

    var bar = new Promise((resolve, reject) => {
        data.forEach((d,index, array) =>  {
        var details="<tr id="+ d.id +"> <td>" + d.name + " </td> <td> "+ d.designationList.length +"</td> </tr>"
        $("#departmentTableBody").append(details);
     if (index === array.length -1) resolve();
           });
       });
       bar.then(() => {
           console.log('All done!');
           dataTableInit();
       });
}

function dataTableInit(){
        // Initialize Example 1
            $('#departmentTable').dataTable({
                dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
                "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
                buttons: [
                    { extend: 'copy', className: 'btn-sm' },
                    { extend: 'csv', title: 'Department List', className: 'btn-sm' },
                    { extend: 'pdf', title: 'Department List', className: 'btn-sm' },
                    { extend: 'print', className: 'btn-sm' }
                ]
            });
  }

