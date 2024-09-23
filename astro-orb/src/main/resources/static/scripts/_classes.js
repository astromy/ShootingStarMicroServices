
fetchInstitutionClasses(instId.split(",")[0]);


    $('.saveClass').click(async function() {

                postdata();
                var jso= buildJson();
                return HttpPost("addLookUps",jso)
                .then(function(result){
                $('#classTable').DataTable().destroy();
                $('.dismissClass').click();
                populateTable(result)
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
var bar = new Promise((resolve, reject) => {
  data.forEach((d,index, array) =>  {
        var details="<tr> <td hidden>" + d.id + " </td> <td> "+ d.name +"</td><td>"+ d.classGroup +"</td> </tr>"
        document.getElementById("classesTableBody").insertAdjacentHTML('beforeend', details);
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
           { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
           { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
           { extend: 'print', className: 'btn-sm' }
       ]
   });

  }

