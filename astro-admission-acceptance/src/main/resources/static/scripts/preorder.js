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

;
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
var verfyOutput=document.querySelector("#crest_")
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
    verfyOutput=images
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

                        <p class="agreement">
                             1. INTRODUCTION
                        </p>

                        <p class="agreement">
                            Welcome to the ORB! We start every new subscriber relationship with a contract.
                            The following contract spells out what you can expect from us, and what we expect from you.
                            If you agree to what you read below, you should click "Yes" at the end of the contract to acknowledge that you have agreed.
                            We intend this to be the legal equivalent of your signature on a written contract, and equally binding.
                            Only by agreeing to this contract will you be able to access and use the services available on this Web Site.
                        </p>
                        <br/>
                        <p class="agreement">
                            2.	ACCESS AND SERVICES
                        </p>

                        <p class="agreement">
                            Your access to the various services available on this system depends on the level of access you select.
                            You may change or discontinue your account at any time.
                            We reserve the right to modify, suspend or terminate access to the service on our system at any time for
                            any reason without notice or refund, including the right to require you to change your login identification code or password.
                        </p>
                        <br/>
                        <p class="agreement">
                            3.	FEES AND PAYMENT
                        </p>

                        <p class="agreement">
                            Depending on the type of subscription chosen, we may charge you an annual fee for using our system which shall be depending on your school’s population.
                            You will be given the opportunity to pay by credit card or by check when you sign up.
                            You can cancel your account at any time, but you will remain liable for all charges accrued up to that time, including full annual charges for the year for which you discontinued service.
                            We reserve the right to change our fees at any time for any reason, but, whenever possible, we will give you at least one month's advance notice of such change.
                        </p>
                        <br/>
                        <p class="agreement">
                            4.	SYSTEM RULES
                        </p>

                        <p class="agreement">
                            You agree to be bound by certain rules which are important for the proper use of this service.
                            Your failure to follow these rules, whether listed below in the contract or in bulletins posted at various points in the system, may result in termination of your service.
                            First, do not tell others your password or let your account be used by anyone except yourself.
                            Third, while you should feel free to express yourself, you should respect other users of the system and not do anything to attack or injure others.
                            Fourth, do not use our system to commit a crime, or to plan, encourage or help others commit a crime, including crimes relating to computers.
                        </p>
                        <br/>
                        <p class="agreement">
                            5.	PROPRIETARY RIGHTS
                        </p>

                        <p class="agreement">
                            By uploading questions and their corresponding answers on our system, you are hereby granting to the us an unrestricted
                            license to use, copy, modify, adapt, or document in any form any set of questions and their corresponding answers,
                            information, or any underlying work in which you may possess proprietary rights, including but not limited to copyright rights.
                            All users of the system are therefore deemed to have disclaimed or waived all copyright ownership rights in
                            their uploaded questions and their corresponding answers, even if they contain copyright notices. You shall have absolutely no
                            recourse against us as the system provider for any alleged or actual infringement of any proprietary rights to which you may claim ownership.

                            <br/>
                            Your use of our system affords you access to many of the features of our system, but some aspects of our system remain within
                            our exclusive proprietary control. We or our suppliers own the intellectual property rights to any and all protectable
                            components of our system, including but not limited to the computer software, the related documentation, the end-user interfaces,
                            the name of our system, many of the individual features, and the collective works consisting of sequences of all public messages
                            on our system. You may not reproduce any sequence of messages from our system, either electronically or in print, without our
                            permission. In addition; you may not copy, modify, adapt, reproduce, translate, distribute, reverse engineer, decompile or
                            dissemble any aspect of the system which we or our suppliers own.
                        </p>
                        <br/>
                        <p class="agreement">
                            6.	LIMITATION OF LIABILITY
                        </p>

                        <p class="agreement">
                            You must bear the risk of any liability relating to your use of our system. We would not be able to afford to operate this
                            system if we were held accountable for every wrongful action by every Online subscriber. ACCORDINGLY, YOUR USE OF THE ONLINE
                            SYSTEM IS ENTIRELY AT YOUR SOLE RISK. WE WILL NOT BE RESPONSIBLE TO YOU OR ANY THIRD PARTIES FOR ANY DIRECT OR INDIRECT,
                            CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OR LOSSES YOU MAY INCUR IN CONNECTION WITH OUR SYSTEM, YOUR USE THEREOF OR ANY OF
                            THE DATA OR OTHER MATERIALS TRANSMITTED THROUGH OR RESIDING ON OUR SYSTEM, REGARDLESS OF THE TYPE OF CLAIM OR THE NATURE OF THE
                            CAUSE OF ACTION, EVEN IF WE HAVE ADVISED OF THE POSSIBILITY OF SUCH DAMAGE OR LOSS.
                        </p>
                        <br/>
                        <p class="agreement">
                            7.	INDEMNITY
                        </p>

                        <p class="agreement">
                            You shall defend and indemnify us and hold us harmless from and against any and all claims, proceedings, damages, injuries,
                            liabilities, losses, costs and expenses (including reasonable attorneys' fees), relating to any acts by you or materials or
                            information transmitted by you in connection with our system, leading wholly or partially to claims against us or our
                            system by other subscribers or third parties, regardless of the type of claim or the nature of the cause of action.
                        </p>
                        <br/>
                        <p class="agreement">
                            8.	DISCLAIMERS OF WARRANTY
                        </p>

                        <p class="agreement">
                            THE SYSTEM IS PROVIDED "AS IS" AND WE MAKE NO WARRANTIES, EXPRESS OR IMPLIED, AS TO THE MERCHANTABILITY, FITNESS FOR A
                            PARTICULAR USE OR PURPOSE, TITLE, NON-INFRINGEMENT OR ANY OTHER WARRANTY, CONDITION, GUARANTY, OR REPRESENTATION,
                            WHETHER ORAL, IN WRITING OR IN ELECTRONIC FORM, INCLUDING BUT NOT LIMITED TO THE ACCURACY OR COMPLETENESS OF ANY INFORMATION
                            CONTAINED THEREIN OR PROVIDED BY THE SERVICE.
                        </p>
                        <br/>
                        <p class="agreement">
                            9.	CHOICE OF LAW
                        </p>

                        <p class="agreement">
                            You agree that this Agreement shall for all purposes be governed by and construed in accordance with the laws of Ghana,
                            and that any action arising out of this Agreement shall be litigated and enforced under the laws of Ghana. In addition,
                            you agree to submit to the jurisdiction of the courts of Ghana, and that any legal action pursued by you shall be within
                            the exclusive jurisdiction of the courts of Ghana.
                        </p>
                        <br/>
                        <p class="agreement">
                            10.	ACKNOWLEDGMENT
                        </p>

                        <p class="agreement">
                            This Agreement represents the entire understanding between you and us regarding your relationship to Online and
                            supersedes any prior statements or representations. IF YOU AGREE TO BE BOUND BY THE TERMS OF THIS ONLINE SUBSCRIBER AGREEMENT,
                            please check the checkbox below. If you do not agree to the terms of the Subscriber Agreement, please log off the system.
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