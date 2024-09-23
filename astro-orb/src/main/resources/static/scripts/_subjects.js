
name=[];
resultlist=[];
    type= $('[name="type"]').val();
    id="";
/*fetchLookup();*/
fetchInstitutionSubject(instId.split(",")[0]);


    $('.saveSubjects').click(async function() {


                var jso=  postdata(); //buildJson();
                return HttpPost("addSubjects",jso)
                .then(function(result){
                    swal({
                           title: "Thank you!",
                           text: "Your application is being submitted",
                           type: "success"
                      });
                    })
                });


    function postdata(){
   let subjects=[];
    subjects= document.getElementsByClassName('clonable');
    for(var i=0;i<subjects.length; i++){
    var c =subjects[i].getElementsByClassName('classOptions')[0];
    var jsonObject={
                "id":id,
                "name":subjects[i].getElementsByClassName('newSubjectTxt')[0].value,
                "classGroup":subjects[i].getElementsByClassName('classOption')[0].value,
                "preference":subjects[i].getElementsByClassName('subjectPref')[0].value
            };
        resultlist.push(jsonObject);
    }
    var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
        v=v.replace(/\//g, '')
    var finalJsonObject={
                    "institution":v,
                    "subjectDetails":resultlist
                };
    return finalJsonObject;
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
     try {
            // Await the result of the HTTP request
            const result = await HttpPost("getInstitutionSubjects", instRequest);

            // Pass the result to fetchLookup and await it
            return await fetchLookup(result);
        } catch (error) {
            console.error("Error in fetchInstitutionSubject:", error);
        }
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
    var classOptions = document.getElementById("classGroupOptions");
    var option = document.createElement("option");
                    option.value = d.id;
                    option.textContent = d.name;
                    classOptions.appendChild(option);
        });
  }

  function populateTable(data){
var bar = new Promise((resolve, reject) => {
  data.forEach((d,index, array) =>  {
        var details="<tr> <td hidden>" + d.id + " </td> <td> "+ d.name +"</td><td>"+ d.classGroupName +"</td><td>"+ d.preference +"</td> </tr>"
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
                "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
                buttons: [
                    { extend: 'copy', className: 'btn-sm' },
                    { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
                    { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
                    { extend: 'print', className: 'btn-sm' }
                ]
            });
  }

