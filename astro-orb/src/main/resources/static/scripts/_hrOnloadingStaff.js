var staffFname,staffLName,staffDoB,nationality,homeTown,city,contact1,bContact,IDType,iDNumber,snnitNum,maritalStatus,nameOfSpouse,DoE,gender,nextOfKing;
var dependantDetails=[];
var staffAcademicDetails=[];
var staffProfessionalDetails=[];
var staffDocumentDetails=[];
var exisitingstaff=[];
var staffList=[];
var firstName ,lastName,fullName,gender,dob,contact,bContact,doe,nationality,snnit,ht,residence,idType,idNumber,maritalStatus, nos, nok,code,designation,level;
var tempStaffHolder;
var countryList=["Afghanistan","Albania","Algeria","Andorra","Angola","Antigua and Barbuda","Argentina","Armenia","Australia","Austria","Azerbaijan","The Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bhutan","Bolivia","Bosnia and Herzegovina","Botswana","Brazil","Brunei ","Bulgaria","Burkina Faso","Burma","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Central African Republic","Chad","Chile","China","Colombia","Comoros ","Democratic Republic of Congo","Congo-Brazzaville ","Costa Rica","Cote dIvoire","Croatia","Cuba","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","East Timor (see Timor-Leste)","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Fiji","Finland","France","Gabon","The Gambia","Georgia","Germany","Ghana","Greece","Grenada","Guatemala","Guinea","Guinea-Bissau","Guyana","Haiti","Holy See","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Israel","Italy","Jamaica","Japan","Jordan","Kazakhstan","Kenya","Kiribati","Kosovo","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Morocco","Mozambique","Namibia","Nauru","Nepal","Netherlands","Netherlands Antilles","New Zealand","Nicaragua","Niger","Nigeria","North Korea","Norway","Oman","Pakistan","Palau","Palestinian Territories","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal ","Qatar","Romania","Russia","Rwanda","Saint Kitts and Nevis","Saint Lucia","Saint Vincent and the Grenadines","Samoa ","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Korea","South Sudan","Spain ","Sri Lanka","Sudan","Suriname","Swaziland ","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand ","Timor-Leste","Togo","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","Uruguay","Uzbekistan","Vanuatu","Venezuela","Vietnam","Yemen","Zambia","Zambia","Zimbabwe"
]

var instId = $("meta[name='institutionId']").attr("content");
var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
    instId=v.replace(/\//g, '')
fetchStaffList(instId);
fetchDepartment(instId)

  window.copyrights();

    $('.prev').click(function () {
        var tabs= $('.tab-pane');
        var tbs= $('.tab-pane.active');
        tabs.removeClass('active');
        var prevLi = tbs.prev().addClass("active");

        var header=$('.wizardTabs');
        var header1=$('.wizardTabs.btn-primary');
        header.removeClass('btn-primary');
        header.addClass('btn-default');
        header1.prev().removeClass('btn-default').addClass('btn-primary');
        tempStaffHolder=undefined;
        itra-=1;
        addTableRow('staffTableBody_'+ itra)
    });

    var itra=1;
    $('.next').click(function () {

       var tabs= $('.tab-pane');
       var tbs= $('.tab-pane.active');
       tabs.removeClass('active');
       var nextLi = tbs.next().addClass("active");

       var header=$('.wizardTabs')
       var header1=$('.wizardTabs.btn-primary');
       header.removeClass('btn-primary');
       header.addClass('btn-default');
       header1.next().removeClass('btn-default').addClass('btn-primary');
       tempStaffHolder=undefined;
        addTableRow('staffTableBody_'+ itra)
        itra+=1;
    });



    async function fetchStaffList(instId){
      var instRequest={"val":instId}
      return  HttpPost("get-staff-by-institution",instRequest)
       .then(function (result) {
      exisitingstaff=result;
      })
    }

//Populates the Designation Select Field
    async function fetchDepartment(instId){
              var instRequest={"val":instId}
          return  HttpPost("getInstitutionDepartment",instRequest)
           .then(function (result) {

            var el= $("#staffDesignation")
                    el.find('option:gt(0)').remove();

            var el1= $("#nationality")
                    el1.find('option:gt(0)').remove();


                         countryList.forEach((cl,index1, array1) =>  {
                             el1.append($('<option>', {
                                     value: cl,
                                     text: cl
                                 }));
                         });

               if(result!=null){
                 var bar = new Promise((resolve, reject) => {
                     result.forEach((d,index, array) =>  {
                         d.designationList.forEach((dl,index1, array1) =>  {
                             el.append($('<option>', {
                                     value: dl.code,
                                     text: dl.name
                                 }));
                             if (index1 === array1.length -1) resolve();
                         });
                     });
                 });
                 bar.then(() => {
                     console.log('All done!');
                 });
                 }

        })
    }


    $('#submitRequest').click(async function() {
        console.log(JSON.stringify(staffList));
            return HttpPost("create-staff",staffList)
                .then(function(result){
                    swal({
                           title: "Thank you!",
                           text: "Staff Saved Successfully",
                           type: "success"
                      });
                })

    });

    $('.saveStaffDetails').click(async function() {
        if(document.querySelector(".dependants")){
        const elm =document.querySelectorAll(".dependants");
        var cnt=elm.length;

        for (var i=0; i<cnt; i++){
       var doc=await uploadPDFAsJSON(document.querySelectorAll(".pdfInput")[i],document.querySelectorAll(".fileError")[i]);
       var dpic=document.querySelectorAll(".dependantPicInput")[i];
       var picFile=document.querySelectorAll(".fileError")[i]
       var imgs=await uploadIMGAsJSON(document.querySelectorAll(".dependantPicInput")[i],document.querySelectorAll(".fileError")[i]);

        var depJson={
            "id":"",
            "name":document.querySelectorAll(".newDependantName")[i].value,
            "dateOfBirth":document.querySelectorAll(".newDependantDOB")[i].value,
            "relationType":document.querySelectorAll(".newRelationshipType")[i].options[document.querySelectorAll(".newRelationshipType")[i].selectedIndex].text,
            "gender": document.querySelectorAll(".newGenderSelect")[i].options[document.querySelectorAll(".newGenderSelect")[i].selectedIndex].text,
            "birthCertificate":doc.fileName+"_"+doc.fileType+"_"+doc.fileContent,
            "dependantPicture":imgs.fileName+"_"+imgs.fileType+"_"+imgs.fileContent,
            "institutionCode":instId,
            }
            dependantDetails.push(depJson);
        }

        if(staffList.length>0){
            tempStaffHolder = staffList.find(obj => obj["staffCode"] === document.getElementsByClassName("modalbody")[0].id);
            tempStaffHolder.dependants=dependantDetails;
           }else{
            tempStaffHolder.dependants=dependantDetails;
            staffList.push(tempStaffHolder);
            }

    }else if(document.querySelector(".academic")){
        const elm =document.querySelectorAll(".academic");
        var cnt=elm.length;
        for (var i=0; i<cnt; i++){
        var doc=await uploadPDFAsJSON(document.querySelectorAll(".pdfInput")[i],document.querySelectorAll(".fileError")[i]);
        var academic={
            "id":"",
            "nameOfInstitution":document.querySelectorAll(".nameOfInstitution")[i].value,
            "dateOfAdmission":document.querySelectorAll(".dateOfAdmission")[i].value,
            "programOffered":document.querySelectorAll(".programOffered")[i].value,
            "dateOfGraduation":document.querySelectorAll(".dateOfGraduation")[i].value,
            "certificateType":document.querySelectorAll(".certificateType")[i].options[document.querySelectorAll(".certificateType")[i].selectedIndex].text,
            "staffAcademicRecords":doc.fileName+"_"+doc.fileType+"_"+doc.fileContent,
            "supportingDocs":doc.fileName+"_"+doc.fileType+"_"+doc.fileContent,
            "institutionCode":instId,
            }
            staffAcademicDetails.push(academic);
        }
         if(staffList.length>0){
            tempStaffHolder = staffList.find(obj => obj["staffCode"] === document.getElementsByClassName("modalbody")[0].id);
            tempStaffHolder.academicRecords=staffAcademicDetails;
          }else{
            tempStaffHolder.academicRecords=staffAcademicDetails;
            staffList.push(tempStaffHolder);
          }

    }else if(document.querySelector(".professional")){
        const elm =document.querySelectorAll(".professional");
        var cnt=elm.length;

        for (var i=0; i<cnt; i++){
        var doc=await uploadPDFAsJSON(document.querySelectorAll(".pdfInput")[i],document.querySelectorAll(".fileError")[i]);

        var professional={
            "id":"",
            "nameOfInstitution":document.querySelectorAll(".nameOfInstitution")[i].value,
            "dateOfEmployment":document.querySelectorAll(".dateOfEmployment")[i].value,
            "dateOfDeparture":document.querySelectorAll(".dateOfDeparture")[i].value,
            "designationAtInstitution":document.querySelectorAll(".designationAtInstitution")[i].value,
            "employmentTypeAtInstitution":document.querySelectorAll(".employmentTypeAtInstitution")[i].value,
            "staffProfessionalRecords":doc.fileName+"_"+doc.fileType+"_"+doc.fileContent,
            "supportingDocs":doc.fileName+"_"+doc.fileType+"_"+doc.fileContent,
            "institutionCode":instId,
            }
            staffProfessionalDetails.push(professional);
        }

        if(staffList.length>0){
            tempStaffHolder = staffList.find(obj => obj["staffCode"] === document.getElementsByClassName("modalbody")[0].id);
            tempStaffHolder.professionalRecords=staffProfessionalDetails;
           }else{
            tempStaffHolder.professionalRecords=staffProfessionalDetails;
            staffList.push(tempStaffHolder);
          }

    }else if(document.querySelector(".imageInputa")){
        const elm =document.querySelectorAll(".dependants");
        var cnt=elm.length;

        for (var i=0; i<cnt; i++){
        var depJson={
            "id":"",
            "name":document.querySelectorAll(".newDependantName")[i].value,
            "dateOfBirth":document.querySelectorAll(".newDependantDOB")[i].value,
            "relationType":document.querySelectorAll(".newRelationshipType")[i].options[document.querySelectorAll(".newRelationshipType")[i].selectedIndex].text,
            "gender": document.querySelectorAll(".newGenderSelect")[i].options[document.querySelectorAll(".newGenderSelect")[i].selectedIndex].text,
            "birthCertificate":uploadPDFAsJSON(document.querySelectorAll(".pdfInput")[i],document.querySelectorAll(".fileError")[i]),
            "dependantPicture":getBase64Image(document.querySelectorAll(".dependantPic")[i].querySelector("img")),
            "institutionCode":"",
            }
            //dependantDetails.push(depJson);
        }
    }else if(document.querySelector(".imageInput")){
    }

        passdetails();
        $('.dismissModal').click();

    })

function passdetails(){

}


function addTableRow(tableId) {
    // Find the table by ID
    const table = document.getElementById(tableId);

    // Retrieve values from the specified input and select elements
    code=document.getElementById('code').value;
     firstName = document.getElementById('staffFName').value;
     lastName = document.getElementById('staffLName').value;
     fullName = `${firstName} ${lastName}`;
     gender = document.getElementById('gender').options[document.getElementById('gender').selectedIndex].text;
     dob = document.getElementById('dob').value;

     contact = document.getElementById('contact').value;
     bContact = document.getElementById('bContact').value;
     doe = document.getElementById('doe').value;
     nationality = document.getElementById('nationality').options[document.getElementById('nationality').selectedIndex].text;
     snnit = document.getElementById('snnit').value;
     ht = document.getElementById('homeT').value;
     residence = document.getElementById('residence').value;
     idType = document.getElementById('idType').options[document.getElementById('idType').selectedIndex].text;
     idNumber = document.getElementById('idNumber').value;
     maritalStatus = document.getElementById('maritalStatus').options[document.getElementById('maritalStatus').selectedIndex].text;
     designation = document.getElementById('staffDesignation').options[document.getElementById('staffDesignation').selectedIndex].text;
     nos = document.getElementById('nos').value;
     nok = document.getElementById('nok').value;
     level = document.getElementById('level').value;


     const testButton = document.getElementsByClassName('test')[0];
    // Create a new row
    const row = table.insertRow();
           row.setAttribute('data-toggle','modal');
           row.setAttribute('data-target','#staffModal');
           row.style.cursor = 'pointer';
           row.addEventListener('click', function() {

           tempStaffHolder=  formatNewStaff();
          /* if(tempStaffHolder.firstNames.length>3){
           staffList.push(tempStaffHolder);
           }*/
               switch (itra) {
                   case 1:
                       // Code for itra = 1
                       break;
                   case 2:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Dependants";
                      document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createDependant());
                      document.getElementsByClassName("modalbody")[0].id = "";

                     const dependantButton = testButton.cloneNode(true); // Clone to remove listeners
                     testButton.replaceWith(dependantButton); // Replace old button with a fresh one

                      dependantButton.addEventListener('click', function() {
                       document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createDependant());
                      });
                       break;
                   case 3:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Academic Documents";
                      document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createAcademicData());
                      document.getElementsByClassName("modalbody")[0].id = "";

                     const academicButton = testButton.cloneNode(true); // Clone to remove listeners
                     testButton.replaceWith(academicButton); // Replace old button with a fresh one

                      academicButton.addEventListener('click', function() {
                       document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createAcademicData());
                      });
                       break;
                   case 4:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Professional Documents";
                      document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createProfessionalData());
                      document.getElementsByClassName("modalbody")[0].id = "";

                     const profButton = testButton.cloneNode(true); // Clone to remove listeners
                     testButton.replaceWith(profButton); // Replace old button with a fresh one

                      profButton.addEventListener('click', function() {
                       document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createProfessionalData());
                      });

                       break;
                   default:
                       // Optional: Code for other values of itra
                       break;
               }

           });

    // Loop to create 8 cells and populate them with the appropriate values
    for (let i = 0; i < 8; i++) {
        const cell = row.insertCell();

     var v= instId;

        // Populate each cell according to the specified requirements
        switch (i) {
            case 0:
                cell.textContent = code;
                break;
            case 1:
                cell.textContent = fullName;
                break;
            case 2:
                cell.textContent = gender;
                break;
            case 3:
                cell.textContent = contact;
                break;
            case 4:
                cell.textContent = doe;
                break;
            case 5:
                cell.textContent = designation;
                break;
            case 6:
                cell.textContent = nationality;
                break;
            case 7:
                cell.textContent = snnit;
                break;
            default:
                cell.textContent = `Cell ${i + 1}`; // Default content for unspecified cells
        }
    }
    existingStaff(table);
}

function existingStaff(table){

        var cnt=0;
        const testButton2 = document.getElementsByClassName('test')[0];
        exisitingstaff.forEach(function(d){
        cnt+=1;
           const row = table.insertRow();
           row.setAttribute('data-toggle','modal');
           row.setAttribute('data-target','#staffModal');
           row.style.cursor = 'pointer';
           row.addEventListener('click', function() {

            /**
            Checks is tempStaffHolder is undefined(which means its the time) or if the staffCode differs from what is already there.
            If so format the new existing staff and pass it to the tempStaffHolder
            */
            if(tempStaffHolder==undefined || tempStaffHolder.staffCode!=d.staffCode){
            var t = staffList.find(obj => obj["staffCode"] === d.staffCode);
                if(t==undefined){
                tempStaffHolder= formatExisting(d);
                staffList.push(tempStaffHolder);
                }else{
                tempStaffHolder=t;
                }
            }

               switch (itra) {
                   case 1:
                       // Code for itra = 1
                       break;
                   case 2:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Dependants";
                      document.getElementsByClassName("modalbody")[0].id = d.staffCode;

                       d.dependants.forEach(function(dep){
                      document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createDependant());

                        const dependantButton = testButton2.cloneNode(true); // Clone to remove listeners
                        testButton2.replaceWith(dependantButton); // Replace old button with a fresh one

                        dependantButton.addEventListener('click', function() {
                        document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createDependant());
                       });


                      // Get all elements by class name using querySelectorAll
                      const name = document.querySelectorAll('.newDependantName');
                      const dob = document.querySelectorAll('.newDependantDOB');
                      const gens = document.querySelectorAll('.newGenderSelect');
                      const relationTypes = document.querySelectorAll('.newRelationshipType');
                      const dependantPic = document.querySelectorAll('.dependantPicInput');
                      const birthCertificate = document.querySelectorAll('.pdfInput');

                      // Get the last element in the NodeList
                      name[name.length - 1].value =dep.name;
                      dob[name.length - 1].value =dep.dateOfBirth;


                      const gen=gens[name.length - 1];

                      for (let i = 0; i < gen.options.length; i++) {
                        const option = gen.options[i];
                        if (option.text === dep.gender) {
                          gen.selectedIndex = i;
                          break; // Exit the loop once the option is found
                        }
                      }

                    const relationType = relationTypes[name.length - 1];

                      for (let i = 0; i < relationType.options.length; i++) {
                        const option = relationType.options[i];
                        if (option.text === dep.relationType) {
                          relationType.selectedIndex = i;
                          break; // Exit the loop once the option is found
                        }
                      }


                      dependantPic[name.length - 1];
                       setFileInputFromByteArray(dep.birthCertificate,birthCertificate[name.length - 1],"pdf");
                       setFileInputFromByteArray(dep.dependantPicture,dependantPic[name.length - 1],'image/png', dep.name);

                    })

                       break;
                   case 3:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Academic Documents";
                      document.getElementsByClassName("modalbody")[0].id = d.staffCode;

                       d.academicRecords.forEach(function(aca){
                      document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createAcademicData());

                    const academicButton = testButton2.cloneNode(true); // Clone to remove listeners
                    testButton2.replaceWith(academicButton); // Replace old button with a fresh one

                    academicButton.addEventListener('click', function() {
                    document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createAcademicData());
                    });



                      // Get all elements by class name using querySelectorAll
                      const name = document.querySelectorAll('.nameOfInstitution');
                      const doa = document.querySelectorAll('.dateOfAdmission');
                      const dog = document.querySelectorAll('.dateOfGraduation');
                      const po = document.querySelectorAll('.programOffered');
                      const cte = document.querySelectorAll('.certificateType');
                      const sd = document.querySelectorAll('.pdfInput');

                      // Get the last element in the NodeList
                      name[name.length - 1].value =aca.nameOfInstitution;
                      doa[name.length - 1].value =aca.dateOfAdmission;
                      dog[name.length - 1].value =aca.dateOfGraduation;
                      po[name.length - 1].value =aca.programOffered;


                      const ct=cte[name.length - 1];

                      for (let i = 0; i < ct.options.length; i++) {
                        const option = ct.options[i];
                        if (option.text === aca.certificateType) {
                          ct.selectedIndex = i;
                          break; // Exit the loop once the option is found
                        }
                      }

                      sd[name.length - 1];
                       setFileInputFromByteArray(aca.supportingDocs,sd[name.length - 1],"pdf");

                    })

                       break;
                   case 4:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Professional Documents";
                      document.getElementsByClassName("modalbody")[0].id = d.staffCode;

                      d.professionalRecords.forEach(function(prof){
                      document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createProfessionalData());

                      const profButton = testButton2.cloneNode(true); // Clone to remove listeners
                      testButton2.replaceWith(profButton); // Replace old button with a fresh one

                       profButton.addEventListener('click', function() {
                        document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createProfessionalData());
                       });



                      // Get all elements by class name using querySelectorAll
                      const name = document.querySelectorAll('.nameOfInstitution');
                      const doe = document.querySelectorAll('.dateOfEmployment');
                      const dod = document.querySelectorAll('.dateOfDeparture');
                      const des = document.querySelectorAll('.designationAtInstitution');
                      const et = document.querySelectorAll('.employmentTypeAtInstitution');
                      const sd = document.querySelectorAll('.pdfInput');

                      // Get the last element in the NodeList
                      name[name.length - 1].value =prof.nameOfInstitution;
                      doe[name.length - 1].value =prof.dateOfEmployment;
                      dod[name.length - 1].value =prof.dateOfDeparture;
                      des[name.length - 1].value =prof.designationAtInstitution;
                      et[name.length - 1].value =prof.employmentTypeAtInstitution;


                      sd[name.length - 1];
                       setFileInputFromByteArray(prof.supportingDocs,sd[name.length - 1],"pdf");

                    })

                       break;
                   default:
                       // Optional: Code for other values of itra
                       break;
               }

           });

            for (let i = 0; i < 8; i++) {
                    const cell = row.insertCell();

                 var v= instId;
                    // Populate each cell according to the specified requirements
                   switch (i) {
                       case 0:
                           cell.textContent = d.staffCode;
                           break;
                       case 1:
                           cell.textContent = d.firstNames+" "+d.lastName;
                           break;
                       case 2:
                           cell.textContent = d.gender;
                           break;
                       case 3:
                           cell.textContent = d.contact1;
                           break;
                       case 4:
                           cell.textContent = d.dateOfEmployment;
                           break;
                       case 5:
                           cell.textContent = d.designation;
                           break;
                       case 6:
                           cell.textContent = d.nationality;
                           break;
                       case 7:
                           cell.textContent = d.dependants.length;
                           break;
                       default:
                           cell.textContent = `Cell ${i + 1}`; // Default content for unspecified cells
                   }
            }
         })
    }

// Call the function by passing the ID of the table
//addTableRow('myTable'); // Replace 'myTable' with your actual table ID
function formatNewStaff(){
    var staffObject={
       "id":"",
       "staffCode":code,
       "firstNames":firstName,
       "lastName":lastName,
       "dateOfBirth":dob,
       "nationality":nationality,
       "homeTown":ht,
       "residentialTown":residence,
       "contact1":contact,
       "backupContact":bContact,
       "nationalIDType":idType,
       "nationalID":idNumber,
       "snnitNumber":snnit,
       "maritalStatus":maritalStatus,
       "nameOfSpouse":nos,
       "dateOfEmployment":doe,
       "gender":gender,
       "level":level,
       "designation":designation,
       "staffPicture":"",
       "nextOfKing":nok,
       "institutionCode":instId,
       "dependants":"",
       "academicRecords":"",
       "professionalRecords":"",
       "staffDocuments":staffDocumentDetails
        };
return staffObject;
}

function formatExisting(d){

var staffObject={
       "id":"",
       "staffCode":d.staffCode,
       "firstNames":d.firstNames,
       "lastName":d.lastName,
       "dateOfBirth":d.dateOfBirth,
       "nationality":d.nationality,
       "homeTown":d.homeTown,
       "residentialTown":d.residentialTown,
       "contact1":d.contact1,
       "backupContact":d.backupContact,
       "nationalIDType":d.nationalIDType,
       "nationalID":d.nationalID,
       "snnitNumber":d.snnitNumber,
       "maritalStatus":d.maritalStatus,
       "nameOfSpouse":d.nameOfSpouse,
       "dateOfEmployment":d.dateOfEmployment,
       "gender":d.gender,
       "level":d.level,
       "designation":d.designation,
       "staffPicture":"",
       "nextOfKing":d.nok,
       "institutionCode":instId,
       "dependants":"",
       "academicRecords":"",
       "professionalRecords":"",
       "staffDocuments":staffDocumentDetails
     }
return staffObject;
}



//============================Images UTIL====================================

    var input = document.querySelector(".imageInput")
    var output = document.querySelector(".imageOutput")
    var imagesArray = []

    function imageChange(el){
        var file = el.files
        imagesArray=[];
        imagesArray.push(file[0])
        displayImages(el.parentElement)
      }

 /* function displayImages(el) {
    var images = ""
    imagesArray.forEach((image, index) => {
      images += `<div class="crest">
                  <img src="${URL.createObjectURL(image)}" alt="image" id="crestImage" class="crest">
                  <span onclick="deleteImage(${index})">&times;</span>
                </div>`
    })
    el.getElementsByClassName('dependantPic')[0].innerHTML = images
  }*/


    function displayImages(el) {
        let images = "";
        imagesArray.forEach((image, index) => {
            let src;

            if (image instanceof File) {
                // File object: Generate a blob URL
                src = URL.createObjectURL(image);
            } else if (typeof image === "string" && image.startsWith("data:image")) {
                // Base64 string: Use it directly
                src = image;
            } else {
                console.error("Invalid image type:", image);
                return;
            }

            console.log("Rendering image with src:", src); // Debug log

            images += `<div class="crest">
                         <img src="${src}" alt="image" id="crestImage" class="crest">
                         <span onclick="deleteImage(${index})">&times;</span>
                       </div>`;
        });

      // Find the container element and render the images
      const container = el.getElementsByClassName('dependantPic')[0];
      if (!container) {
          console.error("Container for images not found!");
          return;
      }

      container.innerHTML = images;
  }


    function displayFetchImages(srcs) {
      var images = ""
        images += `<div class="crest">
                    <img src=`+ srcs + ` alt="image" id="crestImage" class="crest">
                  </div>`
      output.innerHTML = images
    }

    function deleteImage(index) {
      imagesArray.splice(index, 1)
      imagesArray=[];
      displayImages()
    }


    function getBase64Image(img) {
        var canvas = document.createElement("canvas");
        canvas.width = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL("image/png");
        return dataURL.split(",")[1].replace('"',"");
      }



