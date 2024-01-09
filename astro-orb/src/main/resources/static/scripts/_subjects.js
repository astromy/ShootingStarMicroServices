var type,id;
var name=[];
var resultlist=[]
    type= $('[name="type"]').val();
var instId = $("meta[name='institutionId']").attr("content");
fetchLookup();
fetchInstitutionSubject(instId.split(",")[0]);


    $('.saveClassGroup').click(async function() {

                postdata();
                var jso= buildJson();
                return HttpPost("addLookUps",jso)
                .then(function(result){
                    swal({
                           title: "Thank you!",
                           text: "Your application is being submitted",
                           type: "success"
                      });
                    })
                });


    function postdata(){
   let classGroup=[];
    classGroup= document.getElementsByClassName('newClassGrouptxt');
    for(var i=0;i<classGroup.length; i++){
    name[i]=classGroup[i].value;
    }
    }

    function buildJson(){
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


  async function fetchInstitutionSubject(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getInstitutionSubjects",instRequest)
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
        $("#subjectClassGroups").append(details);
    });
  }

  function populateTable(data){
var bar = new Promise((resolve, reject) => {
  data.forEach((d,index, array) =>  {
        var details="<tr> <td hidden>" + d.id + " </td> <td> "+ d.name +"</td><td>"+ d.classGroup +"</td><td>"+ d.preference +"</td> </tr>"
        $("#subjectTableBody").append(details);
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
            $('#subjectTable').dataTable({
                dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
                "lengthMenu": [[5,10, 25, 50, -1], [5,10, 25, 50, "All"]],
                buttons: [
                    { extend: 'copy', className: 'btn-sm' },
                    { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
                    { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
                    { extend: 'print', className: 'btn-sm' }
                ]
            });
  }

