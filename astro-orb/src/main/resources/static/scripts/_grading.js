id=null;
var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
fetchInstitutionGrading(instId.split(",")[0]);


    window.copyrights();

    $('.saveGrading').click(async function() {

                var jso= postdata();
                return HttpPost("addGradingSetting",jso)
                .then(function(result){
                $('#gradingTable').DataTable().destroy();
                $('#gradingTable2').DataTable().destroy();
                $('.dismissSubject').click();
                populateTable(result)
                    swal({
                           title: "Thank you!",
                           text: "Your application is being submitted",
                           type: "success"
                      });
                    })
                });

     
    function postdata(){

   let gradeBrackets=[];
    resultlist=[];
    gradeBrackets= document.getElementsByClassName('gradeBrackets');
    for(var i=0;i<gradeBrackets.length; i++){
    var jsonObject={
                "id":id,
                "lowerLimit":Number(gradeBrackets[i].getElementsByClassName('lowerLimit')[0].value),
                "grade":gradeBrackets[i].getElementsByClassName('grade')[0].value,
                "comment":gradeBrackets[i].getElementsByClassName('comment')[0].value
            };
        resultlist.push(jsonObject);
    }
    let settings=document.getElementsByClassName('gradeSettings')[0]
    var interJsonObject={
                         "id":id,
                         "classPercentage":Number(settings.getElementsByClassName('newClassPercentage')[0].value),
                         "examsPercentage":Number(settings.getElementsByClassName('newExamsPercentage')[0].value),
                         "trailingMark":Number(settings.getElementsByClassName('newTrailingMark')[0].value),
                         "allowedTrails":Number(settings.getElementsByClassName('newAllowedTrails')[0].value),
                         "gradingList":resultlist
                     };
    var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
        v=v.replace(/\//g, '')
    var finalJsonObject={
                    "institution":v,
                    "gradingSettingDetails":interJsonObject
                };
    return finalJsonObject;
    }





  async function fetchInstitutionGrading(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getInstitutionGradingSetting",instRequest)
     .then(function (result) {
       result && populateTable(result)
  })
  }

  function populateTable(data){
    var bar = new Promise((resolve, reject) => {
         $("#gradingSettingsBody").empty();
         $("#gradingBracketsBody").empty();

        let topTable="<tr> <td hidden>" + data.id + " </td> <td> "+ data.classPercentage +"</td><td>"+ data.examsPercentage +"</td><td>"+ data.trailingMark +"</td><td>"+ data.allowedTrails +"</td> </tr>"
        $("#gradingSettingsBody").append(topTable);
        data.gradingList.forEach((d,index, array) =>  {
            var details="<tr> <td hidden>" + d.id + " </td> <td> "+ "" +"</td><td>"+ d.lowerLimit +"</td><td>"+ d.grade +"</td><td>"+ d.comment +"</td> </tr>"
            $("#gradingBracketsBody").append(details);
            if (index === array.length -1) resolve();
        });
    });
    bar.then(() => {
        console.log('All done!');
        dataTableInit("#gradingTable");
        dataTableInit("#gradingTable2");
    });
   }

  function dataTableInit(table){
        // Initialize Example 1
        $(table).dataTable({
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

