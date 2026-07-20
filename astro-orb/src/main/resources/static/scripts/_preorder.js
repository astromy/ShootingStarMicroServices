var selectPlan,
    institution,
    slogan,
    country,
    region,
    city,
    email,
    contact1,
    contact2,
    bececode,
    postalAddress,
    streams,
    population,
    website;
var originalData = {};


var instId = $("meta[name='institutionId']").attr("content").split("/")[1];
fetchInstitution(instId.split(",")[0]);
$(".selectPlan").on("click", function (event) {
    event.preventDefault();
    $(".subscriptionOption").removeClass("active");
    $(this).closest(".subscriptionOption").addClass("active");
    var x = $(this).closest(".subscriptionOption").find(".subscriptionPlan");
    selectPlan = x.text();
});

$(".prev").click(function () {
    var tabs = $(".tab-pane");
    var tbs = $(".tab-pane.active");
    tabs.removeClass("active");
    var prevLi = tbs.prev().addClass("active");

    var header = $(".wizardTabs");
    var header1 = $(".wizardTabs.btn-primary");
    header.removeClass("btn-primary");
    header.addClass("btn-default");
    header1.prev().removeClass("btn-default").addClass("btn-primary");
});

$(".next").click(function () {
    var tabs = $(".tab-pane");
    var tbs = $(".tab-pane.active");
    tabs.removeClass("active");
    var nextLi = tbs.next().addClass("active");

    var header = $(".wizardTabs");
    var header1 = $(".wizardTabs.btn-primary");
    header.removeClass("btn-primary");
    header.addClass("btn-default");
    header1.next().removeClass("btn-default").addClass("btn-primary");
    document.getElementById("client").innerHTML =
        document.getElementsByName("clientName")[0].value;
});

$("#submitRequest").click(async function () {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    var approve = $(".approveCheck").is(":checked");
    if (approve) {
        // Got to step 1
        //  $('[href=#step1]').tab('show');
        await postdata();
        var jso = buildJson();
        // Serialize data to post method
        var datastring = $("#simpleForm").serialize();

        // Show notification
        //        Example code for post form

        return fetchPost("preRequestInstitution", jso).then(function (result) {
            displayFetchInstitution(result);
            $('.splash').css('display', 'none')
            swal({
                title: "Thank you!",
                text: "Operation processed successfully",
                type: "success",
            });
        });
    } else {
        // Show notification
        swal({
            title: "Error!",
            text: "You have to approve form checkbox.",
            type: "error",
        });
    }
});

async function postdata() {
    institution = $('[name="clientName"]').val() || originalData.name;
    slogan = $('[name="slogan"]').val() || originalData.slogan;
    country = $('[name="country"]').val() || originalData.country;
    region = $('[name="region"]').val() || originalData.region;
    city = $('[name="city"]').val() || originalData.city;
    email = $('[name="email"]').val() || originalData.email;
    contact1 = $('[name="contact1"]').val() || originalData.contact1;
    contact2 = $('[name="contact2"]').val() || originalData.contact2;
    bececode = $('[name="bececode"]').val() || originalData.bececode;
    postalAddress = $('[name="postalAddress"]').val() || originalData.postalAddress;
    streams = $('[name="streams"]').val() || originalData.streams;
    population = $('[name="population"]').val() || originalData.population;
    website = $('[name="website"]').val() || originalData.website;

    // Use UI selection if available, otherwise keep original
    subscription = selectPlan || originalData.subscription;

    // Process both images in parallel
    [crest, headSignature] = await Promise.all([
        getOptimizedImageData(imagesArray, 'logo', '#crestImage'),
        getOptimizedImageData(imagesArray1, 'headSignature', '#headSignatureImage')
    ])
}

function buildJson() {
    var jsonObject = {
        name: institution,
        slogan: slogan,
        country: country,
        region: region,
        city: city,
        email: email,
        contact1: contact1,
        contact2: contact2,
        bececode: bececode,
        postalAddress: postalAddress,
        streams: streams,
        population: population,
        website: website,
        subscription: subscription,
        status: "Pre-Order",
        creationDate: "",
        crest: crest,
        headSignature: headSignature,
    };

    return jsonObject;
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

function validateForm() {
    $("#institutionForm").validate({
        rules: {
            clientName: {
                required: true,
                minlength: 3,
            },
            slogan: {
                required: true,
                minlength: 5,
            },
            country: {
                required: true,
                minlength: 2,
            },
            region: {
                required: true,
                minlength: 3,
            },
            city: {
                required: true,
                minlength: 5,
            },
            email: {
                required: true,
                email: true,
            },
            contact1: {
                required: true,
                minlength: 10,
            },
            bececode: {
                required: true,
                minlength: 5,
            },
            postalAddress: {
                required: true,
                minlength: 3,
            },
            streams: {
                required: true,
                number: true,
            },
            population: {
                required: true,
                number: true,
            },
        },
        messages: {
            institution: {
                required: "Please enter the name of your Institution",
                minlength: "Please enter valid institutional Name",
            },
            slogan: {
                required: "Please enter your slogan or motto",
                minlength: "Slogan can't be less that 5 characters in lenght",
            },
            country: {
                required: "Please enter the country you operate from",
                minlength: "Please enter valid country",
            },
            region: {
                required: "Please enter the region or province you operate from",
                minlength: "Please enter valid region",
            },
            city: {
                required: "Please enter the city you operate from",
                minlength: "Please enter valid city",
            },
            email: {
                required: "Please enter your email",
                email: "Please enter valid email",
            },
            contact1: {
                required: "Please enter your phone number",
                minlength: "Please enter valid phone number",
            },
            bececode: {
                required: "Please enter your BECE code",
                minlength: "Please enter valid code",
            },
            postalAddress: {
                required: "Please enter your postal address",
                minlength: "Please enter valid address",
            },
            streams: {
                required: "Please enter the number of streams you run",
                number: "Please enter valid number",
            },
            population: {
                required: "Please enter your institutional population",
                number: "Please enter valid number",
            },
        },
        submitHandler: function (form) {
            form.submit();
        },
        errorPlacement: function (error, element) {
            $(element)
                .closest("form")
                .find("label[for='" + element.attr("id") + "']")
                .append(error);
        },
        errorElement: "span",
    });

    return true;
}

var input = document.querySelector(".imageInput");
var input1 = document.querySelector(".imageInput1");

var output = document.querySelector(".imageOutput");
var output1 = document.querySelector(".headSignature");

var imagesArray = [];
var imagesArray1 = [];

input.addEventListener("change", async () => {
    if (!input.files || !input.files[0]) return;

    const originalFile = input.files[0];

    try {
        output.innerHTML = "<p>Processing logo...</p>";

        // OPTIMIZE THE LOGO WITH PNG FORMAT
        const optimizedDataURL = await optimizeImage(originalFile, {
            maxSize: 400,
        });

        const optimizedBlob = dataURLToBlob(optimizedDataURL);
        const optimizedFileName = `logo-${originalFile.name.split('.')[0]}.png`;
        const optimizedFile = new File([optimizedBlob], optimizedFileName, {
            type: 'image/png'
        });

        imagesArray = [optimizedFile];
        displayImages();

    } catch (error) {
        console.error("Logo optimization failed:", error);
        // Fallback: use the original file
        imagesArray = [originalFile];
        displayImages();
    }
});

function displayImages() {
    let images = "";
    imagesArray.forEach((image, index) => {
        images += `<div class="crest">
                                  <img src="${URL.createObjectURL(image)}" alt="logo" id="crestImage" class="crest">
                                  <span onclick="deleteImage(${index})">&times;</span>
                                </div>`;
    });
    output.innerHTML = images;
}

window.deleteImage = function (index) {
    imagesArray.splice(index, 1);
    displayImages();
    input.value = '';
};


// 6. Helper function to convert a Data URL back to a Blob
// (Needed for creating the preview)
function dataURLToBlob(dataURL) {
    const parts = dataURL.split(';base64,');
    const contentType = parts[0].split(':')[1];
    const raw = window.atob(parts[1]);
    const uInt8Array = new Uint8Array(raw.length);

    for (let i = 0; i < raw.length; ++i) {
        uInt8Array[i] = raw.charCodeAt(i);
    }

    return new Blob([uInt8Array], {type: contentType});
}

// 7. Function to get the optimized base64 for the server
// Call this when you're ready to submit the form
async function getOptimizedImageData(imgArray, fileName, elementId) {
    if (imgArray.length > 0) {
        return new Promise((resolve) => {
            const reader = new FileReader();
            reader.onload = () => {
                const fullDataURL = reader.result;
                resolve(fullDataURL.split(',')[1]);
            };
            reader.readAsDataURL(imgArray[0]);
        });
    }

    // CASE 2: No new upload - capture the existing rendered image
    const crestImage = document.querySelector(elementId);
    if (crestImage && crestImage.src) {
        // Check if the src is a blob URL (from our preview) or an existing external image
        try {
            // Convert the <img> element to a File and optimize it
            const imageFile = await convertImgToFile(crestImage, fileName + ".png", 'image/png');
            const optimizedDataURL = await optimizeImage(imageFile, {
                maxSize: 400,
                // quality is ignored for PNG
            });

            // Return only the base64 part
            return optimizedDataURL.split(',')[1];

        } catch (error) {
            console.error("Failed to capture and optimize existing image:", error);
            return null;
        }
    }

    // If no image exists at all, return null
    return null;
}

function convertImgToFile(imgElement, fileName, fileType = 'image/png') {
    return new Promise((resolve, reject) => {
        // Check if the image has fully loaded and has a valid source
        if (!imgElement.complete || !imgElement.naturalWidth) {
            reject(new Error('Image is not loaded yet'));
            return;
        }

        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');

        canvas.width = imgElement.naturalWidth;
        canvas.height = imgElement.naturalHeight;

        ctx.drawImage(imgElement, 0, 0);

        canvas.toBlob(
            (blob) => {
                if (blob) {
                    resolve(new File([blob], fileName, {type: fileType}));
                } else {
                    reject(new Error('Canvas to Blob conversion failed'));
                }
            },
            fileType,
            1.0 // Quality - use 1.0 as optimization happens later
        );
    });
}


function displayFetchImages(srcs) {
    var images = "";
    images +=
        `<div class="crest">
                    <img src=` +
        srcs +
        ` alt="image" id="crestImage" class="crest">
                  </div>`;
    output.innerHTML = images;
}

input1.addEventListener("change", async () => {
    if (!input1.files || !input1.files[0]) return;

    const originalFile = input1.files[0];

    try {
        output1.innerHTML = "<p>Processing logo...</p>";

        // OPTIMIZE THE LOGO WITH PNG FORMAT
        const optimizedDataURL = await optimizeImage(originalFile, {
            maxSize: 400,
        });

        const optimizedBlob = dataURLToBlob(optimizedDataURL);
        const optimizedFileName = `logo-${originalFile.name.split('.')[0]}.png`;
        const optimizedFile = new File([optimizedBlob], optimizedFileName, {
            type: 'image/png'
        });

        imagesArray1 = [optimizedFile];
        displaySignatureImages();

    } catch (error) {
        console.error("Logo optimization failed:", error);
        // Fallback: use the original file
        imagesArray = [originalFile];
        displayImages();
    }
});

function displaySignatureImages() {
    let images = "";
    imagesArray1.forEach((image, index) => {
        images += `<div class="headSignature">
                                   <img src="${URL.createObjectURL(image)}" alt="logo" id="headSignatureImage" class="headSignature">
                                   <span onclick="deleteImage(${index})">&times;</span>
                                 </div>`;
    });
    output1.innerHTML = images;

}

window.deleteImage = function (index) {
    imagesArray1.splice(index, 1);
    displayImages();
    input.value = '';
};


// 6. Helper function to convert a Data URL back to a Blob
// (Needed for creating the preview)
function dataURLToBlob(dataURL) {
    const parts = dataURL.split(';base64,');
    const contentType = parts[0].split(':')[1];
    const raw = window.atob(parts[1]);
    const uInt8Array = new Uint8Array(raw.length);

    for (let i = 0; i < raw.length; ++i) {
        uInt8Array[i] = raw.charCodeAt(i);
    }

    return new Blob([uInt8Array], {type: contentType});
}


function displayHeadSignatureFetchImages(srcs) {
    var images = "";
    images +=
        `<div class="headSignature">
                    <img src=` +
        srcs +
        ` alt="image" id="headSignatureImage" class="headSignature">
                  </div>`;
    output1.innerHTML = images;
}

function deleteImage(index) {
    imagesArray.splice(index, 1);
    imagesArray = [];
    displayImages();
}

async function fetchInstitution(instId) {
    var v = instId.replace(/[\[\]']+/g, "");
    v = v.replace(/\//g, "");
    var instRequest = {val: v};
    return fetchPost("getInstitutionByCode", instRequest).then(function (result) {
        displayFetchInstitution(result);
    });
}

function displayFetchInstitution(result) {
    originalData = {...result};

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
    displayFetchImages("data:image/png;base64," + result.crest);
    displayHeadSignatureFetchImages(
        "data:image/png;base64," + result.headSignature
    );

    Array.from(document.getElementsByClassName("subscriptionPlan")).forEach(
        function (s) {
            if (s.innerHTML == result.subscription) {
                var t = s.closest(".subscriptionOption");
                t.classList.add("active");
            }
        }
    );
}


/**
 * Optimizes an image file using canvas
 * @param {File} file - The original image file
 * @param {Object} options - Compression options
 * @returns {Promise<String>} - A promise that resolves with a Base64 data URI
 */
async function optimizeImage(file, options) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        const reader = new FileReader();

        reader.onload = function (e) {
            img.src = e.target.result;
        };
        reader.onerror = reject;
        reader.readAsDataURL(file);

        img.onload = function () {
            const maxDimension = options.maxSize;
            let width = img.width;
            let height = img.height;

            // Calculate new dimensions to fit within maxDimension while preserving aspect ratio
            if (width > height) {
                if (width > maxDimension) {
                    height = Math.round((height * maxDimension) / width);
                    width = maxDimension;
                }
            } else {
                if (height > maxDimension) {
                    width = Math.round((width * maxDimension) / height);
                    height = maxDimension;
                }
            }

            const canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;

            const ctx = canvas.getContext('2d');

            // Optional: Set a white background if the logo doesn't have transparency
            // ctx.fillStyle = '#FFFFFF';
            // ctx.fillRect(0, 0, canvas.width, canvas.height);

            // Draw the resized image
            ctx.drawImage(img, 0, 0, width, height);

            // For logos, we convert to PNG for clarity, ignoring the 'quality' parameter
            canvas.toBlob(
                (blob) => {
                    const newReader = new FileReader();
                    newReader.onload = () => resolve(newReader.result);
                    newReader.onerror = reject;
                    newReader.readAsDataURL(blob);
                },
                'image/png' // Force PNG format for logos
            );
        };
        img.onerror = reject;
    });
}


