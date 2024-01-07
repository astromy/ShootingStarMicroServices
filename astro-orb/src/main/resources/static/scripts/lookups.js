var type,id;
let name=[];
let resultlist=[]
    type= $('[name="type"]').val();
var instId = $("meta[name='institutionId']").attr("content");
fetchInstitution(instId.split(",")[0]);
//fetchInstitution(instId);


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


  async function fetchInstitution(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":type}
    return  HttpPost("getLookUpByType",instRequest)
     .then(function (result) {
       populateTable(result)
    console.log(result);
  })
  }

  function populateTable(data){

  debugger
  data.forEach(function(d) {
        var details="<tr> <td hidden>" + d.id + " </td> <td> "+ d.name +"</td> </tr>"
        $("#classGroupTable").append(details);
    });


$(function () {

    // Initialize Example 1
    $('#example1').dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        "lengthMenu": [[5,10, 25, 50, -1], [5,10, 25, 50, "All"]],
        buttons: [
            { extend: 'copy', className: 'btn-sm' },
            { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'print', className: 'btn-sm' }
        ]
    });
});

  }

