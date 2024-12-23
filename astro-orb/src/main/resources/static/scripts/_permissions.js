
var exisitingstaff=[];
var staffPermissionList=[];
var staffCode;

var instId = $("meta[name='institutionId']").attr("content");
var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
    instId=v.replace(/\//g, '')
fetchStaffList(instId);

id="";
//fetchInstitutionClasses(instId.split(",")[0]);


    window.copyrights();

    $('.savePermissions').click(async function() {

        return HttpPost("addStaffPermissions",staffPermissionList)
        .then(function(result){
            staffPermissionList=[];
             $('.dismissPermissions').click();
            swal({
               title: "Thank you!",
               text: "Your application is being submitted",
               type: "success"
            });
        })
     });


  async function fetchInstitutionClasses(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getInstitutionClasses",instRequest)
     .then(function (result) {
     fetchLookup(result);
  })
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
        var details="<option value='" + d.id + "'>" + d.name +" </option>"
        $("#classGroupOptions").append(details);
    });
  }

  function populateTable(data){
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



// Add an onchange event listener
document.querySelector('#StaffList').addEventListener('change', (event) => {
    staffCode = event.target.value;
});


    async function fetchStaffList(instId){
      var instRequest={"val":instId}
      return  HttpPost("get-staff-by-institution",instRequest)
       .then(function (result) {
      exisitingstaff=result;

                  var el= $("#StaffList")
                          el.find('option:gt(0)').remove();

                     if(result!=null){
                       var bar = new Promise((resolve, reject) => {
                               result.forEach((sl,index1, array1) =>  {
                                   el.append($('<option>', {
                                           value: sl.staffCode,
                                           text: sl.staffCode + " " + "["+sl.firstNames+" "+sl.lastName+"]"+"  "
                                       }));
                                   if (index1 === array1.length -1) resolve();
                               });
                       });
                       bar.then(() => {
                           console.log('All done!');
                       });
                       }

      })
    }



function dataTableInit(){

    // Initialize Example 1
    $('#permissionsTable').dataTable({
        dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            { extend: 'copy', className: 'btn-sm' },
            { extend: 'csv', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'pdf', title: 'ExampleFile', className: 'btn-sm' },
            { extend: 'print', className: 'btn-sm' }
        ]
    });
};


function permissionBuilder(){
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    // Add an event listener to each checkbox
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', (event) => {
        const label = document.querySelector(`label[for="${checkbox.id}"]`);
            if (event.target.checked) {

               if(staffPermissionList.some(obj => obj["staffCode"] === staffCode) && staffPermissionList.some(obj => obj["permission"]===label.innerHTML)){
                    staffPermissionList.find(obj => obj["permission"] === label.innerHTML).state="add";
               }else{
               var staffPermission={
               "id":"",
               "staffCode":staffCode,
               "permissionCode":event.target.value,
               "permission":label.innerHTML,
               "institutionCode":instId,
               "state":"add",
               }
                    staffPermissionList.push(staffPermission);
                }
            } else {

               if(staffPermissionList.some(obj => obj["staffCode"] === staffCode) && staffPermissionList.some(obj => obj["permission"]===label.innerHTML)){
                    staffPermissionList.find(obj => obj["permission"] === label.innerHTML).state="delete";
               }else{
               var staffPermission={
               "id":"",
               "staffCode":staffCode,
               "permissionCode":event.target.value,
               "permission":label.innerHTML,
               "institutionCode":instId,
               "state":"delete",
               }
                   staffPermissionList.push(staffPermission);
                }
            }
        });
    });
}


    document.querySelector("#StaffList").addEventListener("change", function() {
      var selectedValue = this.value;

        const el=document.querySelector('.tabs');
        if (selectedValue !== "Select Staff") {
        el.style.pointerEvents = 'auto'; // Re-enable all clicks/interactions
        el.style.opacity = '1';          // Restore original appearance (optional)
        }else{
        el.style.pointerEvents = 'none'; // Disable all clicks/interactions
        el.style.opacity = '0.5';        // Make it appear disabled (optional)
        }
    });