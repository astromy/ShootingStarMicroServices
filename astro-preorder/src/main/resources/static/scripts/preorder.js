const countries = ["Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Côte d'Ivoire", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo (Congo-Brazzaville)", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czechia (Czech Republic)", "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Holy See", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar (Burma)", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia", "Norway", "Oman", "Pakistan", "Palau", "Palestine State", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"];

var selectPlan, institution, slogan, country, region, city, email, contact1, contact2, bececode,
    postalAddress, streams, population, website, crest;
var imagesArray = [];

document.addEventListener('DOMContentLoaded', function() {
    // Initialize components
    initPlanSelection();
    initTabNavigation();
    setupFormValidation();
    initImageUpload();
    initTermsAndConditions();
    initPrintButton();

    // Initialize country select
    const countrySelect = document.getElementById('country');
    countries.forEach(country => {
        const option = document.createElement('option');
        option.value = country;
        option.textContent = country;
        countrySelect.appendChild(option);
    });

    // Set copyright year
    document.getElementById('copyrightYear').textContent = new Date().getFullYear();
});

function isValidCountry(inputCountry) {
    return countries.includes(inputCountry);
}

function initPlanSelection() {
    $(".selectPlan").on('click', function(event) {
        event.preventDefault();
        $('.subscriptionOption').removeClass('active');
        $(this).closest(".subscriptionOption").addClass("active");
        var x = $(this).closest('.subscriptionOption').find('.subscriptionPlan');
        selectPlan = x.text();
    });
}

function initTabNavigation() {
    const tabs = document.querySelectorAll('.wizardTabs');
    const tabContents = document.querySelectorAll('.tab-pane');
    const nextButtons = document.querySelectorAll('.next');
    const prevButtons = document.querySelectorAll('.prev');

    let currentTab = 0;
    showTab(currentTab);

    function showTab(index) {
        tabContents.forEach((content, i) => {
            content.classList.toggle('active', i === index);
        });

        tabs.forEach((tab, i) => {
            tab.classList.toggle('btn-primary', i === index);
            tab.classList.toggle('btn-default', i !== index);
        });
    }

    nextButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            if (validateCurrentTab(currentTab)) {
                currentTab++;
                showTab(currentTab);
                document.getElementById('client').innerHTML = document.getElementsByName('clientName')[0].value;
                confdata();
            }
        });
    });

    prevButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            currentTab--;
            showTab(currentTab);
        });
    });
}

function validateCurrentTab(tabIndex) {
    if (tabIndex === 0) {
        // Validate form using jQuery Validation
        const formValid = $("#institutionForm").valid();
        const imageValid = validateImageUpload();

        if (!formValid || !imageValid) {
            // Scroll to first error if any
            const firstError = $(".is-invalid").first();
            if (firstError.length) {
                $('html, body').animate({
                    scrollTop: firstError.offset().top - 100
                }, 500);
            }
            return false;
        }
        return true;
    }
    return true;
}

function setupFormValidation() {
    // Custom validation methods
    $.validator.addMethod("ghanaPhone", function(value, element) {
        return this.optional(element) || /^(?:\+233|0)[235]\d{8}$/.test(value);
    }, "Please enter a valid Ghanaian phone number (format: +233XXXXXXXXX or 0XXXXXXXXX)");

    $.validator.addMethod("strictEmail", function(value, element) {
        return this.optional(element) ||
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value);
    }, "Please enter a valid email address (e.g., user@example.com)");

    $.validator.addMethod("validCountry", function(value, element) {
        return this.optional(element) || countries.includes(value);
    }, "Please select a valid country from the list");

    // Form validation
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
                validCountry: true
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
                email: false,
                strictEmail: true
            },
            contact1: {
                required: true,
                ghanaPhone: true
            },
            contact2: {
                ghanaPhone: true
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
            clientName: {
                required: "Please enter the name of your Institution",
                minlength: "Please enter valid institutional Name"
            },
            slogan: {
                required: "Please enter your slogan or motto",
                minlength: "Slogan must be at least 5 characters"
            },
            country: {
                required: "Please select a country",
                validCountry: "Please select a valid country from the list"
            },
            region: {
                required: "Please enter the region or province",
                minlength: "Region must be at least 3 characters"
            },
            city: {
                required: "Please enter the city",
                minlength: "City must be at least 5 characters"
            },
            email: {
                required: "Please enter your email address",
                strictEmail: "Must be a valid email (e.g., name@domain.com)"
            },
            contact1: {
                required: "Please enter your phone number",
                ghanaPhone: "Must be +233XXXXXXXXX or 0XXXXXXXXX (9 digits after prefix)"
            },
            contact2: {
                ghanaPhone: "Must be +233XXXXXXXXX or 0XXXXXXXXX (9 digits after prefix)"
            },
            bececode: {
                required: "Please enter your BECE code",
                minlength: "BECE code must be at least 5 characters"
            },
            postalAddress: {
                required: "Please enter your postal address",
                minlength: "Address must be at least 3 characters"
            },
            streams: {
                required: "Please enter number of streams",
                number: "Must be a valid number"
            },
            population: {
                required: "Please enter student population",
                number: "Must be a valid number"
            }
        },
        errorElement: "span",
        errorClass: "invalid-feedback",
        highlight: function(element) {
            $(element).addClass('is-invalid');
            $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight: function(element) {
            $(element).removeClass('is-invalid');
            $(element).closest('.form-group').removeClass('has-error');
        },
        errorPlacement: function(error, element) {
            if (element.attr("type") === "file") {
                error.insertAfter(element.parent());
            } else {
                error.insertAfter(element);
            }
        },
        invalidHandler: function(event, validator) {
            const errors = validator.numberOfInvalids();
            if (errors) {
                const message = errors === 1
                    ? 'There is 1 invalid field. Please correct it.'
                    : `There are ${errors} invalid fields. Please correct them.`;

                swal({
                    title: "Form Validation Errors",
                    text: message,
                    type: "error",
                    confirmButtonText: "OK, I'll fix them"
                });
            }
        }
    });

    // Real-time validation feedback
    $('#institutionForm input').on('blur', function() {
        $(this).valid();
    });
}

function validateImageUpload() {
    const imageInput = $('.imageInput');
    if (imagesArray.length === 0) {
        imageInput.addClass('is-invalid');
        const existingError = imageInput.parent().find('.invalid-feedback');
        if (!existingError.length) {
            imageInput.after('<div class="invalid-feedback">Please upload a school crest</div>');
        }
        return false;
    }
    imageInput.removeClass('is-invalid');
    imageInput.parent().find('.invalid-feedback').remove();
    return true;
}

function initImageUpload() {
    const input = document.querySelector(".imageInput");
    const output = document.querySelector(".imageOutput");
    const verifyOutput = document.querySelector("#crest_");

    input.addEventListener("change", () => {
        const file = input.files;
        imagesArray = [];
        if (file && file[0]) {
            imagesArray.push(file[0]);
            displayImages();
        }
    });

    function displayImages() {
        let images = "";
        imagesArray.forEach((image, index) => {
            images += `<div class="crest">
                        <img src="${URL.createObjectURL(image)}" alt="image" id="crestImage" class="crest">
                        <span onclick="deleteImage(${index})">&times;</span>
                      </div>`;
        });
        output.innerHTML = images;
        verifyOutput.innerHTML = images;
    }

    window.deleteImage = function(index) {
        imagesArray.splice(index, 1);
        displayImages();
        document.querySelector('.imageInput').value = '';
    };
}

function initTermsAndConditions() {
    $('.tnc').click(function() {
        document.getElementById('modb').innerHTML = contract;
        document.getElementById('contractClient').innerHTML = $('[name="clientName"]').val();
        document.getElementById('dtime').innerHTML = new Date().toLocaleString();
    });

    $('#submitRequest').click(function() {
        var approve = $(".approveCheck").is(':checked');
        if (!approve) {
            swal({
                title: "Error!",
                text: "You have to first approve to the Terms and Conditions.",
                type: "error"
            });
            return false;
        }

        // Final validation before submission
        if (!validateCurrentTab(0)) {
            return false;
        }

        postdata();
        var jso = buildJson();

        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: "preRequestInstitution",
            type: 'POST',
            data: JSON.stringify(jso),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(data) {
                swal({
                    title: "Thank you!",
                    text: "Your application is being submitted",
                    type: "success"
                });
            },
            error: function(errMsg) {
                swal({
                    title: "Sorry!",
                    text: "Operation Failed",
                    type: "error"
                });
            }
        });
    });
}

function initPrintButton() {
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
}

function confdata() {
    $('[name="clientName_"]').text($('[name="clientName"]').val());
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
}

function postdata() {
    institution = $('[name="clientName"]').val();
    slogan = $('[name="slogan"]').val();
    country = $('[name="country"]').val();
    region = $('[name="region"]').val();
    city = $('[name="city"]').val();
    email = $('[name="email"]').val();
    contact1 = $('[name="contact1"]').val();
    contact2 = $('[name="contact2"]').val();
    bececode = $('[name="bececode"]').val();
    postalAddress = $('[name="postalAddress"]').val();
    streams = $('[name="streams"]').val();
    population = $('[name="population"]').val();
    website = $('[name="website"]').val();

    // Get base64 image if available
    const crestImage = document.getElementById("crestImage");
    if (crestImage) {
        crest = getBase64Image(crestImage);
    }
}

function buildJson() {
    return {
        "name": institution,
        "slogan": slogan,
        "country": country,
        "region": region,
        "city": city,
        "email": email,
        "contact1": contact1,
        "contact2": contact2,
        "bececode": bececode,
        "postalAddress": postalAddress,
        "streams": streams,
        "population": population,
        "website": website,
        "subscription": selectPlan,
        "status": "Pre-Order",
        "creationDate": "",
        "crest": crest
    };
}

function getBase64Image(img) {
    var canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0);
    var dataURL = canvas.toDataURL("image/png");
    return dataURL.split(",")[1].replace('"', "");
}





































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