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
       confdata();
    });

    $('.tnc').click(function(){
    document.getElementById('modb').innerHTML=contract;
    document.getElementById('contractClient').innerHTML=$('[name="clientName"]').val();
    document.getElementById('dtime').innerHTML=datetime();
    })

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

debugger;
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

        $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
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
                    text: "You have to first approve to the Terms and Conditions.",
                    type: "error"
                });
            }
});

$('#copyrightYear').text(getYear());

    function confdata(){

    $('[name="clientName_"]').text( $('[name="clientName"]').val());
    $('[name="slogan_"]').text($('[name="slogan"]').val());
    $('[name="country_"]').text($('[name="country"]').val());
    $('[name="region_"]').text($('[name="region"]').val());
    $('[name="city_"]').text($('[name="city"]').val());
    $('[name="email_"]').text($('[name="email"]').val());
    $('[name="contact1_"]').text($('[name="contact1"]').val());
    $('[name="contact2_"]').text($('[name="contact2"]').val());
    $('[name="bececode_"]').text($('[name="bececode"]').val());
    $('[name="postalAddress_"]').text($('[name="postalAddress"]').val());
    $('[name="streams_"]').text($('[name="streams"]').val());
    $('[name="population_"]').text($('[name="population"]').val());
    $('[name="website_"]').text($('[name="website"]').val());
    $('[name="subscription_"]').text(selectPlan);
    //crest=getBase64Image(document.getElementById("crestImage"));
    }
     
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

function datetime(){

var objToday = new Date(),
	weekday = new Array('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'),
	dayOfWeek = weekday[objToday.getDay()],
	domEnder = function() { var a = objToday; if (/1/.test(parseInt((a + "").charAt(0)))) return "th"; a = parseInt((a + "").charAt(1)); return 1 == a ? "st" : 2 == a ? "nd" : 3 == a ? "rd" : "th" }(),
	dayOfMonth = today + ( objToday.getDate() < 10) ? '0' + objToday.getDate() + domEnder : objToday.getDate() + domEnder,
	months = new Array('January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'),
	curMonth = months[objToday.getMonth()],
	curYear = objToday.getFullYear(),
	curHour = objToday.getHours() > 12 ? objToday.getHours() - 12 : (objToday.getHours() < 10 ? "0" + objToday.getHours() : objToday.getHours()),
	curMinute = objToday.getMinutes() < 10 ? "0" + objToday.getMinutes() : objToday.getMinutes(),
	curSeconds = objToday.getSeconds() < 10 ? "0" + objToday.getSeconds() : objToday.getSeconds(),
	curMeridiem = objToday.getHours() > 12 ? "PM" : "AM";
var today = curHour + ":" + curMinute + "." + curSeconds + curMeridiem + " " + dayOfWeek + " " + dayOfMonth + " of " + curMonth + ", " + curYear;
return objToday
}

function getYear(){
var objToday = new Date();
curYear = objToday.getFullYear();
return curYear;
}

$("#printToPdf").click(function() {
            var divContents = document.getElementById("modb").innerHTML;
            var printWindow = window.open('', '', 'height=400,width=800');
            printWindow.document.write('<html><head><title>DIV Contents</title>');
            printWindow.document.write('</head><body >');
            printWindow.document.write(divContents);
            printWindow.document.write('</body></html>');
            printWindow.document.close();
            printWindow.print();
            printWindow.close();
            $("#dismiss").click();
        });










































var contract=`

            <div class="col-lg-12">
                <div class="hpanel blog-article-box">
                    <div class="panel-heading">
                        <h4>Astromy ORB Client Contract </h4>
                        <small>This shall be a binding legal document between <b><span id="contractClient"></span> and Astromy LLC Company Limited</b></small>
                        <div class="text-muted small">
                            Created by: <span class="font-bold">Astromy LLC Company Ltd on </span><span id="dtime"></span>
                        </div>
                    </div>
                    <div class="panel-body">
                        <p>
                            Maecenas placerat facilisis interdum. Mauris eu dolor nisi. Suspendisse ullamcorper purus nec nibh maximus, ut sollicitudin enim venenatis.
                            Nullam interdum, odio sit amet dapibus mollis, ligula diam pretium sapien, eget suscipit felis ipsum sed lorem. <br/>
                            <br/> Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur ultrices aliquam nisi, vel semper augue auctor eu. Pellentesque a
                            sollicitudin tellus. Aenean posuere pharetra ipsum, ornare pretium lorem convallis non. Vivamus ac sodales sem. Nunc congue dolor ut dui maximus, imperdiet sollicitudin
                            velit auctor. Integer tincidunt iaculis vehicula. Nunc faucibus orci non imperdiet ultricies. Nunc pellentesque dui nisi, vel convallis quam malesuada ornare. Nunc ac purus
                            velit. Cras aliquet porta sodales. Proin blandit ornare bibendum.
                        </p>
                        <br/>
                        <blockquote class="pull-left" style="max-width: 250px">
                            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
                        </blockquote>
                        <p>
                            There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in
                            some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum...
                        </p>
                        <br/>

                        <p>
                            Duis rutrum felis at lorem scelerisque, vel iaculis risus viverra. Integer sed gravida libero. Maecenas sit amet lacus et erat rhoncus sodales quis non nunc. Morbi in
                            mattis massa. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Phasellus bibendum posuere sapien eget vehicula. Nulla sit amet
                            laoreet ante, sed ornare mauris. In interdum ex et leo suscipit sagittis. Pellentesque ac eleifend quam. Nam eu lacinia lacus. Vestibulum lacinia nisl lectus, fringilla
                            molestie diam imperdiet dignissim.
                        </p>

                        <p>
                            In scelerisque urna ut neque imperdiet, sit amet pretium eros suscipit. Cras efficitur ante sit amet mi porta, varius volutpat nulla hendrerit. Pellentesque nec risus
                            malesuada, scelerisque libero at, lacinia magna. Aliquam tellus nunc, viverra in ipsum sed, tristique finibus nibh. Proin gravida lobortis erat, nec aliquam lorem eleifend
                            eget. Integer quis augue id felis ultricies finibus. Curabitur et ligula mauris. Suspendisse vel fringilla quam. Quisque quis metus rhoncus, eleifend leo in, sagittis
                            libero. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Fusce ullamcorper nisl risus, a scelerisque dui hendrerit nec.
                        </p>
                        <br/>
                        <blockquote class="pull-right" style="max-width: 250px">
                            <p>Fusce ac tellus eu nisl lobortis maximus</p>
                        </blockquote>
                        <p>
                            Praesent eget euismod nibh. Fusce ac tellus eu nisl lobortis maximus ac eget sapien. Nulla malesuada mauris non nulla imperdiet ullamcorper.
                        </p>
                        <br/>

                        <p>
                            Sed porta libero metus, nec feugiat enim pharetra vel. Sed vel sagittis augue. Donec hendrerit nibh ac dolor lobortis, eu varius odio sollicitudin. Proin non condimentum
                            nulla, quis dictum leo. Vestibulum lobortis urna eu mauris viverra porttitor. Cras consequat leo condimentum lacus viverra sollicitudin. Donec dignissim ornare est, nec
                            scelerisque purus mollis eu. Phasellus dictum suscipit ligula. Donec malesuada gravida velit. Nulla egestas diam in ligula mollis, nec tincidunt diam porta. Proin eleifend
                            lacinia diam quis pretium. Sed lacinia varius nisi et euismod. Ut ac arcu vulputate, porta nibh non, ultricies erat. Nulla facilisi.
                        </p>

                    </div>
                    <div class="panel-footer">
                        <div class="checkbox" style="margin-left: 10px">
                            <input type="checkbox" class="i-checks approveCheck" placeholder="approve"
                                   title="approve" name="approve">
                            Having read and Understand the terms and conditions, approve to this contract
                        </div>
                    </div>
                </div>
            </div>
`