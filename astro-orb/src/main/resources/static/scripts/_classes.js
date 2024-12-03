id="";
fetchInstitutionClasses(instId.split(",")[0]);


    window.copyrights();

    $('.saveClass').click(async function() {

                var jso= postdata();
                return HttpPost("addClasses",jso)
                .then(function(result){
                $('#classTable').DataTable().destroy();
                $('.dismissClass').click();
                populateTable(result)
                    swal({
                           title: "Thank you!",
                           text: "Classes Saved Successfully",
                           type: "success"
                      });
                    })
                });


    function postdata(){
    let classGroup=[];
        resultlist=[];
        classGroup= document.getElementsByClassName('clonable');
    //classGroup= document.getElementsByClassName('classesOptions');
    for(var i=0;i<classGroup.length; i++){
    var jsonObject={
                "id":id,
                "name":classGroup[i].getElementsByClassName('newclassestxt')[0].value,
                "classGroup":classGroup[i].getElementsByClassName('classesOptions')[0].value
            };
            resultlist.push(jsonObject);
    }
    var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
        v=v.replace(/\//g, '')
    var finalJsonObject={
                    "institution":v,
                    "classDetailList":resultlist
                };
    return finalJsonObject;
    }


  async function fetchInstitutionClasses(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getInstitutionClasses",instRequest)
     .then(result => fetchLookup(result))
  }

    async function fetchLookup(result1){
      var instRequest={"val":"ClassGroup"}
      return  HttpPost("getLookUpByType",instRequest)
       .then(function (result) {
         populateTable(result1)
         populateClassGroup(result)
    })
    }

  function populateClassGroup(data){
    data.forEach(function(d) {
    var classOptions = document.getElementById("classOptions");
    var option = document.createElement("option");
                    option.value = d.id;
                    option.textContent = d.name;
                    classOptions.appendChild(option);
        });
  }

  function populateTable(data){
    $("#classesTableBody").empty();

    var bar = new Promise((resolve, reject) => {
        data.forEach((d,index, array) =>  {
        var details="<tr> <td hidden>" + d.id + " </td> <td> "+ d.name +"</td><td>"+ d.classGroup +"</td> </tr>"
        $("#classesTableBody").append(details);
        if (index === array.length -1) resolve();
        });
    });
    bar.then(() => {
        console.log('All done!');
        dataTableInit();
    });
   }

  function dataTableInit(){
    $('#classTable').dataTable({
       dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
       "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
       buttons: [
           { extend: 'copy', className: 'btn-sm' },
           { extend: 'csv', title: 'Classes List', className: 'btn-sm' },
           { extend: 'pdf', title: 'Classes List', className: 'btn-sm' },
           { extend: 'print', className: 'btn-sm' }
       ]
   });

  }

