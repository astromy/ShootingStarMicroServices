type= $('[name="type"]').val();
name=[];
resultlist=[];
id="";
fetchInstitution(instId.split(",")[0]);
//fetchInstitution(instId);


    window.copyrights();

    $('.saveClassGroup').click(async function() {

                //postdata();
                var jso= postdata();
                return HttpPost("addLookUps",jso)
                .then(function(result){
                $('#example1').DataTable().destroy();
                $('.dismissClassGroup').click();
                populateTable(result)
                    swal({
                           title: "Thank you!",
                           text: "Data Saved Successfully",
                           type: "success"
                      });
                    })
                });

     
    function postdata(){
        resultlist=[];
        var module = $(".newClassGrouptxt");
        const classGroup= document.getElementsByClassName('newClassGrouptxt');
        for(let i=0;i<classGroup.length; i++){
            //name[i]=classGroup[i].value;
                var jsonObject={
                    "id":id,
                    "name":classGroup[i].value,
                    "type":type
                };
            resultlist.push(jsonObject);
        }
        return resultlist
    }

   /* function buildJson(){
    resultlist=[];
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
    $("#classGroupTable").empty();
  data.forEach(function(d) {
        var details="<tr id="+d.id+"> <td hidden>" + d.id + " </td> <td> "+ d.name +"</td> </tr>"
        $("#classGroupTable").append(details);

        var existing=$("#"+d.id);

        existing.attr({
                'data-toggle': 'modal',
                'data-target': '#myclassGroupModal',
                'style': 'cursor: pointer',
            })
            .on('click', function() {
            var recordIndex = $(this).data('index');

                    modalopn();
                    $(".newClassGrouptxt").val(d.name);
                    $('.modalbody').eq(1).empty();
            });
    });


$(function () {

    // Initialize Example 1
    $('#example1').dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            { extend: 'copy', className: 'btn-sm' },
            { extend: 'csv', title: 'Lookup List', className: 'btn-sm' },
            { extend: 'pdf', title: 'Lookup List', className: 'btn-sm' },
            { extend: 'print', className: 'btn-sm' }
        ]
    });
});

  }

