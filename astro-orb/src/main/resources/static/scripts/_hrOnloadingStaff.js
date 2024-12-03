var staffFname,staffLName,staffDoB,nationality,homeTown,city,contact1,bContact,IDType,iDNumber,snnitNum,maritalStatus,nameOfSpouse,DoE,gender,nextOfKing;
var dependantDetails=[];
var staffAcademicDetails=[];
var staffProfessionalDetails=[];
var staffDocumentDetails=[];
var exisitingstaff=[];
var firstName ,lastName,fullName,gender,dob,contact,bContact,doe,nationality,snnit,ht,residence,idType,idNumber,maritalStatus, nos, nok,code,designation,level;

var instId = $("meta[name='institutionId']").attr("content");
var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
    instId=v.replace(/\//g, '')
fetchStaffList(instId);


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

$('#submitRequest').click(async function() {
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
   "dependants":dependantDetails,
   "academicRecords":staffAcademicDetails,
   "professionalRecords":staffProfessionalDetails,
   "staffDocuments":staffDocumentDetails
    };
console.log(JSON.stringify(staffObject));

        return HttpPost("create-staff",staffObject)
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
    var depJson={
        "id":"",
        "name":document.querySelectorAll(".newDependantName")[i].value,
        "dateOfBirth":document.querySelectorAll(".newDependantDOB")[i].value,
        "relationType":document.querySelectorAll(".newRelationshipType")[i].options[document.querySelectorAll(".newRelationshipType")[i].selectedIndex].text,
        "gender": document.querySelectorAll(".newGenderSelect")[i].options[document.querySelectorAll(".newGenderSelect")[i].selectedIndex].text,
        "birthCertificate":doc.fileName+"_"+doc.fileType+"_"+doc.fileContent,
        "dependantPicture":getBase64Image(document.querySelectorAll(".dependantPic")[i].querySelector("img")),
        "institutionCode":instId,
        }
        dependantDetails.push(depJson);
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
        "institutionCode":instId,
        }
        staffAcademicDetails.push(academic);
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
        "institutionCode":instId,
        }
        staffProfessionalDetails.push(professional);
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

               switch (itra) {
                   case 1:
                       // Code for itra = 1
                       break;
                   case 2:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Dependants";
                      document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createDependant());

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
               switch (itra) {
                   case 1:
                       // Code for itra = 1
                       break;
                   case 2:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Dependants";

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
                       setFileInputFromByteArray(dep.birthCertificate,birthCertificate[name.length - 1]);

                    })

                       break;
                   case 3:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Academic Documents";

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
                       setFileInputFromByteArray(aca.supportingDocs,sd[name.length - 1]);

                    })

                       break;
                   case 4:
                      document.getElementsByClassName("modalbody")[0].innerHTML="";
                      document.getElementsByClassName('modal-title')[0].innerHTML="Add Professional Documents";

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
                       setFileInputFromByteArray(prof.supportingDocs,sd[name.length - 1]);

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
addTableRow('myTable'); // Replace 'myTable' with your actual table ID

async function setFileInputFromByteArray(base64String,el) {
 if(base64String!=null){
try {
  // Extract MIME type from Base64 string (e.g., "data:application/pdf;base64,...")
       const mimeType = base64String.match(/data:([^;]+);base64/)?.[1] || 'application/octet-stream';

       // Remove the Base64 metadata (if present)
       const base64Content = base64String;

    var name=base64String.split(("pdf_"))[0]+"pdf";
    const imgString=base64String.split(("_application/pdf_"))[1].split(',').pop();

        const blob = await base64ToBlobStream(imgString,"application/pdf");
        // Create a File object from the Blob
        const file = new File([blob], name, { type: "application/pdf" });


    console.log(imgString)

        // Create a DataTransfer object to hold the file
        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);

        // Assign the files to the input element (this simulates selecting the file)
        el.files = dataTransfer.files;

        // Trigger the 'change' event on the file input
        const event = new Event('change', { bubbles: true });
        el.dispatchEvent(event);
       // uploadPDF(el);
     console.log('File successfully attached:', el.files[0]);
    } catch (error) {
        console.error('Error attaching file:', error);
    }
   }
}

function base64ToBlobStream(base64String, mimeType = 'application/octet-stream', chunkSize = 1024 * 1024) {
    return new Promise((resolve, reject) => {
        try {
            const byteArrays = [];
            const totalChunks = Math.ceil(base64String.length / chunkSize);

            for (let i = 0; i < totalChunks; i++) {
                const chunk = base64String.slice(i * chunkSize, (i + 1) * chunkSize);
                const byteNumbers = Array.from(atob(chunk), (char) => char.charCodeAt(0));
                const byteArray = new Uint8Array(byteNumbers);
                byteArrays.push(byteArray);
            }

            const blob = new Blob(byteArrays, { type: 'application/octet-stream' });
            resolve(blob);
        } catch (error) {
            reject(error);
        }
    });
}

 function uploadPDF (el) {
                        const fileInput = el//document.getElementById('fileInput');
                        const file = fileInput.files[0];
                        const error = document.getElementById('error');
                        const pdfPreview = el.parentElement.getElementsByClassName('fileOutput')[0]
                        pdfPreview.innerHTML = ""; // Clear previous preview

                        if (file) {
                            const fileType = file.type;

                            // Check if the file type is PDF
                            if (fileType === 'application/pdf') {
                                error.style.display = 'none';

                                // Create an object URL for the file
                                const fileURL = URL.createObjectURL(file);

                                // Embed the PDF preview using <iframe>
                                const iframe = document.createElement('iframe');
                                iframe.src = fileURL;
                                iframe.width = '100%';
                               // iframe.height = '500px'; // Adjust as needed
                                iframe.style.border = '1px solid #ccc';

                                // Append the iframe to the preview container
                                pdfPreview.appendChild(iframe);
                            } else {
                                error.style.display = 'block';
                            }
                        } else {
                            alert('Please select a file to upload.');
                        }
                    };



var input = document.querySelector(".imageInput")
var output = document.querySelector(".imageOutput")
var imagesArray = []

function imageChange(el){
    var file = el.files
    imagesArray=[];
    imagesArray.push(file[0])
    displayImages(el.parentElement)
  }

  function displayImages(el) {
    var images = ""
    imagesArray.forEach((image, index) => {
      images += `<div class="crest">
                  <img src="${URL.createObjectURL(image)}" alt="image" id="crestImage" class="crest">
                  <span onclick="deleteImage(${index})">&times;</span>
                </div>`
    })
    el.getElementsByClassName('dependantPic')[0].innerHTML = images
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









async function uploadPDFAsJSON(dataFile, statuses) {
            const input = dataFile//document.getElementById('pdfInput');
            const status =statuses //document.getElementById('status');

            // Check if a file is selected
            if (!input.files || input.files.length === 0) {
                status.textContent = "Please select a PDF file!";
                status.style.color = "red";
                return;
            }

            const file = input.files[0];

            // Check if the selected file is a PDF
            if (file.type !== "application/pdf") {
                status.textContent = "Only PDF files are allowed!";
                status.style.color = "red";
                return;
            }

            try {
                // Convert the file to a Base64 string
                const base64PDF =await convertFileToBase64(file);

                // Create the JSON object
                const jsonObject = {
                    fileName: file.name,
                    fileType: file.type,
                    fileContent: base64PDF // Add the Base64 string here
                };
                return jsonObject

            } catch (error) {
                status.textContent = `Error sending file: ${error.message}`;
                status.style.color = "red";
                console.error('Error:', error);
            }
            return null
        }

        // Helper function to convert a file to Base64
        function convertFileToBase64(file) {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = () => resolve(reader.result.split(',')[1]); // Extract the Base64 part
                reader.onerror = reject;
                reader.readAsDataURL(file); // Read the file as a Data URL
            });
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