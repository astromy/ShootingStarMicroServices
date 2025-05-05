var selectPlan,institution,slogan,country,region,city,email,contact1,contact2,bececode,postalAddress,streams,population,website;
var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
fetchInstitution(instId.split(",")[0]);
    $(".selectPlan").on('click', function(event){
         event.preventDefault();
         $('.subscriptionOption').removeClass('active');
         $(this).closest(".subscriptionOption").addClass("active");
         var x=$(this).closest('.subscriptionOption').find('.subscriptionPlan');
         selectPlan=x.text();
    });

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
       document.getElementById('client').innerHTML=document.getElementsByName('clientName')[0].value;
    }); 

    $('#submitRequest').click(async function() {
            var approve = $(".approveCheck").is(':checked');
            if(approve) {
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
    headSignature=getBase64Image(document.getElementById("headSignatureImage"));
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
            "crest":crest,
            "headSignature":headSignature
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
var input1 = document.querySelector(".imageInput1")

var output = document.querySelector(".imageOutput")
var output1 = document.querySelector(".headSignature")

var imagesArray = []
var imagesArray1 = []

input.addEventListener("change", () => {
    var file = input.files
    imagesArray=[];
    imagesArray.push(file[0])
    displayImages()
  })

  function displayImages() {
    var images = ""
    imagesArray.forEach((image, index) => {
      images += `<div class="crest">
                  <img src="${URL.createObjectURL(image)}" alt="image" id="crestImage" class="crest">
                  <span onclick="deleteImage(${index})">&times;</span>
                </div>`
    })
    output.innerHTML = images
  }

    function displayFetchImages(srcs) {
      var images = ""
        images += `<div class="crest">
                    <img src=`+ srcs + ` alt="image" id="crestImage" class="crest">
                  </div>`
      output.innerHTML = images
    }

input1.addEventListener("change", () => {
    var file = input1.files
    imagesArray1=[];
    imagesArray1.push(file[0])
    displayHeadSignatureImages()
  })

  function displayHeadSignatureImages() {
    var images = ""
    imagesArray1.forEach((image1, index) => {
      images += `<div class="headSignature">
                  <img src="${URL.createObjectURL(image1)}" alt="image" id="headSignatureImage" class="headSignature">
                  <span onclick="deleteImage(${index})">&times;</span>
                </div>`
    })
    output1.innerHTML = images
  }

    function displayHeadSignatureFetchImages(srcs) {
      var images = ""
        images += `<div class="headSignature">
                    <img src=`+ srcs + ` alt="image" id="headSignatureImage" class="headSignature">
                  </div>`
      output1.innerHTML = images
    }

  function deleteImage(index) {
    imagesArray.splice(index, 1)
    imagesArray=[];
    displayImages()
  }

  async function fetchInstitution(instId){
    var v= instId.replace(/[\[\]']+/g,'')
    v=v.replace(/\//g, '')
    var instRequest={"val":v}
    return  HttpPost("getInstitutionByCode",instRequest)
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
    displayHeadSignatureFetchImages('data:image/png;base64,' + result.headSignature);

    Array.from(document.getElementsByClassName("subscriptionPlan")).forEach(function(s) {
            if(s.innerHTML==result.subscription){
              var t=s.closest('.subscriptionOption');
              t.classList.add("active");
            }
        })
  }

