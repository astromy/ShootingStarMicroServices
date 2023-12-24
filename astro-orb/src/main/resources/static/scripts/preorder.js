var selectPlan,institution,slogan,country,region,city,email,contact1,contact2,bececode,postalAddress,streams,population,website;

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

    $('#submitRequest').click(function() {
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
debugger


	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var access_token = $("meta[name='scope']").attr("content");

        $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer' + access_token
                },
            url : "preRequestInstitution",
            type : 'POST',
            data : JSON.stringify(jso),
            beforeSend : function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            cache : false,
            contentType : false,
            processData : false,
            xhr : function() {
                var myXhr = $.ajaxSettings.xhr();
                if (myXhr.upload) {
                    myXhr.upload.addEventListener('progress', function(
                            e) {
                        if (e.lengthComputable) {
                            $('progress').attr({
                                value : e.loaded,
                                max : e.total,
                            });
                        }
                    }, false);
                }
                return myXhr;
            },
            success : function(data) {
                    swal({
                           title: "Thank you!",
                           text: "Your application is being submitted",
                           type: "success"
                      });
            },
            error : function(errMsg) {
                swal({
                       title: "Sorry!",
                       text:  "Operation Failed",
                       type: "error"
                  });
            }
        });


	/*$.post("preRequestInstitution", jso, function(data) {

	})*/

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


const input = document.querySelector(".imageInput")
const output = document.querySelector(".imageOutput")
let imagesArray = []

input.addEventListener("change", () => {
    const file = input.files
    imagesArray=[];
    imagesArray.push(file[0])
    displayImages()
  })

  function displayImages() {
    let images = ""
    imagesArray.forEach((image, index) => {
      images += `<div class="crest">
                  <img src="${URL.createObjectURL(image)}" alt="image" id="crestImage" class="crest">
                  <span onclick="deleteImage(${index})">&times;</span>
                </div>`
    })
    output.innerHTML = images
  }

  function deleteImage(index) {
    imagesArray.splice(index, 1)
    imagesArray=[];
    displayImages()
  }

