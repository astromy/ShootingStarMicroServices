var selectPlan,institution,slogan,country,region,city,email,contact1,contact2,bececode,postalAddress,streams,population,website;
var instId = $("meta[name='institutionId']").attr("content");
fetchStaffList(instId.split(",")[0]);


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

    $('#submitRequest').click(async function() {
            var approve = $(".approveCheck").is(':checked');
            if(approve) {
               var c= validateForm();
               if(c===true){
                // Got to step 1
              //  $('[href=#step1]').tab('show');
                postdata();
                var jso= buildJson();
                // Serialize data to post method
                var datastring = $("#simpleForm").serialize();

                // Show notification
    //        Example code for post form

    return HttpPost("preRequestInstitution",jso)
    .then(function(result){
    displayFetchInstitution(result);
        swal({
               title: "Thank you!",
               text: "Operation processed successfully",
               type: "success"
          });
    })


            }
            } else {
                // Show notification
                swal({
                    title: "Error!",
                    text: "You have to approve form checkbox.",
                    type: "error"
                });
            }
});


    function postdata(){

    institution= $('[name="clientName"]').val();
    slogan= $('[name="slogan"]').val();
    country=$('[name="country"]').val();
    region=$('[name="region"]').val();
    city= $('[name="city"]').val();
    email= $('[name="email"]').val();
    contact1= $('[name="contact1"]').val();
    contact2= $('[name="contact2"]').val();
    bececode=$('[name="bececode"]').val();
    postalAddress= $('[name="postalAddress"]').val();
    streams= $('[name="streams"]').val();
    population=$('[name="population"]').val();
    website=$('[name="website"]').val();
    crest=getBase64Image(document.getElementById("crestImage"));
    }

    function buildJson(){
        var jsonObject={
            "name":institution,
            "slogan":slogan,
            "country":country,
            "region":region,
            "city":city,
            "email":email,
            "contact1":contact1,
            "contact2":contact2,
            "bececode":bececode,
            "postalAddress":postalAddress,
            "streams":streams,
            "population":population,
            "website":website,
            "subscription":selectPlan,
            "status":"Pre-Order",
            "creationDate":"",
            "crest":crest
        };

        return jsonObject
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

    function validateForm(){
    $("#institutionForm").validate({
        rules: {
            clientName: {
                required: true,
                minlength: 3
            },
            slogan: {
                required: true,
                minlength: 5
            },
            country: {
                required: true,
                minlength: 2
            },
            region: {
                required: true,
                minlength: 3
            },
            city: {
                required: true,
                minlength: 5
            },
            email: {
                required: true,
                email: true
            },
            contact1: {
                required: true,
                minlength: 10
            },
            bececode: {
                required: true,
                minlength: 5
            },
            postalAddress: {
                required: true,
                minlength: 3
            },
            streams: {
                required: true,
                number: true
            },
            population: {
                required: true,
                number: true
            }
        },
        messages: {

            institution: {
                required: "Please enter the name of your Institution",
                minlength: "Please enter valid institutional Name"
            },
            slogan: {
                required: "Please enter your slogan or motto",
                minlength: "Slogan can't be less that 5 characters in lenght"
            },
            country: {
                required: "Please enter the country you operate from",
                minlength: "Please enter valid country"
            },
            region: {
                required: "Please enter the region or province you operate from",
                minlength: "Please enter valid region"
            },
            city: {
                required: "Please enter the city you operate from",
                minlength: "Please enter valid city"
            },
            email: {
                required: "Please enter your email",
                email: "Please enter valid email"
            },
            contact1: {
                required: "Please enter your phone number",
                minlength: "Please enter valid phone number"
            },
            bececode: {
                required: "Please enter your BECE code",
                minlength: "Please enter valid code"
            },
            postalAddress: {
                required: "Please enter your postal address",
                minlength: "Please enter valid address"
            },
            streams: {
                required: "Please enter the number of streams you run",
                number: "Please enter valid number"
            },
            population: {
                required: "Please enter your institutional population",
                number: "Please enter valid number"
            }
        },
        submitHandler: function(form) {
            form.submit();
        },
        errorPlacement: function(error, element) {
            $( element )
                    .closest( "form" )
                    .find( "label[for='" + element.attr( "id" ) + "']" )
                    .append( error );
        },
        errorElement: "span",
    });

return true;
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

  async function fetchStaffList(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("get-staff-by-institution",instRequest)
     .then(function (result) {
    displayFetchInstitution(result);
    })
    }

    function displayFetchInstitution(result){
    $('[name="clientName"]').val(result.name);
    $('[name="slogan"]').val(result.slogan);
    $('[name="country"]').val(result.country);
    $('[name="region"]').val(result.region);
    $('[name="city"]').val(result.city);
    $('[name="email"]').val(result.email);
    $('[name="contact1"]').val(result.contact1);
    $('[name="contact2"]').val(result.contact2);
    $('[name="bececode"]').val(result.bececode);
    $('[name="postalAddress"]').val(result.postalAddress);
    $('[name="streams"]').val(result.streams);
    //$('[name="population"]').val()=result.;
    $('[name="website"]').val(result.website);
    displayFetchImages('data:image/png;base64,' + result.crest);

    Array.from(document.getElementsByClassName("subscriptionPlan")).forEach(function(s) {
            if(s.innerHTML==result.subscription){
              var t=s.closest('.subscriptionOption');
              t.classList.add("active");
            }
        })
  }

function addTableRow(tableId) {
    // Find the table by ID
    const table = document.getElementById(tableId);

    // Retrieve values from the specified input and select elements
    const firstName = document.getElementById('staffFName').value;
    const lastName = document.getElementById('staffLName').value;
    const fullName = `${firstName} ${lastName}`;
    const gender = document.getElementById('gender').value;
    const contact = document.getElementById('contact').value;
    const doe = document.getElementById('doe').value;
    const nationality = document.getElementById('nationality').value;
    const snnit = document.getElementById('snnit').value;

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

               document.getElementsByClassName('test')[0].addEventListener('click', function() {
                document.getElementsByClassName('modalbody')[0].insertAdjacentHTML('beforeend', createDependant());
               });
                break;
            case 3:
                // Code for itra = 3
                break;
            case 4:
                // Code for itra = 4
                break;
            default:
                // Optional: Code for other values of itra
                break;
        }

    });

    // Loop to create 8 cells and populate them with the appropriate values
    for (let i = 0; i < 8; i++) {
        const cell = row.insertCell();

     var v= instId.split(",")[0].replace(/[\[\]']+/g,'')
         v=v.replace(/\//g, '')


        // Populate each cell according to the specified requirements
        switch (i) {
            case 0:
                cell.textContent = "ST"+v+"";
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
}

// Call the function by passing the ID of the table
addTableRow('myTable'); // Replace 'myTable' with your actual table ID


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