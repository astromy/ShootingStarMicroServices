window.type;
window.id;
window.clonable;
window.clonable1b;
window.clonable2;
window.name=[];
window.resultlist=[];
window.el;
window.instId = $("meta[name='institutionId']").attr("content");
    type= $('[name="type"]').val();

var scriptsToRemove = [];
 const defaultScripts = [
 "vendor/jquery/dist/jquery.min.js",
 "vendor/jquery-ui/jquery-ui.min.js",
 "vendor/slimScroll/jquery.slimscroll.min.js",
 "vendor/bootstrap/dist/js/bootstrap.min.js",
 "vendor/metisMenu/dist/metisMenu.min.js",
 "vendor/iCheck/icheck.min.js",
 "vendor/peity/jquery.peity.min.js",
 "scripts/homer.js",
 "scripts/common.js",

 "vendor/datatables/media/js/jquery.dataTables.min.js",
 "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
 "vendor/pdfmake/build/pdfmake.min.js",
 "vendor/pdfmake/build/vfs_fonts.js",
 "vendor/datatables.net-buttons/js/buttons.html5.min.js",
 "vendor/datatables.net-buttons/js/buttons.print.min.js",
 "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
 "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",


 "vendor/jquery-flot/jquery.flot.js",
 "vendor/jquery-flot/jquery.flot.resize.js",
 "vendor/jquery-flot/jquery.flot.pie.js",
 "vendor/flot.curvedlines/curvedLines.js",
 "vendor/jquery.flot.spline/index.js",
 "vendor/moment/min/moment.min.js",
 "vendor/fullcalendar/dist/fullcalendar.min.js",
 "vendor/iCheck/icheck.min.js",
 "vendor/sparkline/index.js",
 "vendor/ladda/dist/spin.min.js",
 "vendor/ladda/dist/ladda.min.js",
 "vendor/ladda/dist/ladda.jquery.min.js",
 "vendor/summernote/dist/summernote.min.js",
 "dataTables/datatables.min.js"
 ];

 const absoultePath = [];


    const defaultLinks = [
    "images/favicon.ico",
    "vendor/fontawesome/css/font-awesome.css",
    "vendor/bootstrap/dist/css/bootstrap.css",
    "styles/style.css","styles/all.min.css",
    "dataTables/datatables.min.css"
    ];

    const absoultelinkPath = [];

// Track current state of active scripts and links
let activeScripts = new Set();
let activeLinks = new Set();


window.addEventListener("load", () => {

    $('#copyrightYear').text(getYear());

    function getYear(){
    var objToday = new Date();
    curYear = objToday.getFullYear();
    return curYear;
    }

    initializeDefaults();
    addEventListeners();  // Attach all event listeners
});

function initializeDefaults() {
    trackExistingResources("script", defaultScripts, activeScripts);
    trackExistingResources("link", defaultLinks, activeLinks);
}

function trackExistingResources(tagName, defaultArray, activeSet) {
    // Track all resources matching default scripts or links
    document.querySelectorAll(tagName).forEach((el) => {
        const srcOrHref = el.src || el.href;
        defaultArray.forEach((item) => {
            if (srcOrHref.includes(item)) {
                activeSet.add(srcOrHref);
            }
        });
    });
}





/*    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });*/
function addEventListeners() {
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("classgroup").addEventListener("click", classgroupnBuild);
    document.getElementById("classes").addEventListener("click", classesBuild);
    document.getElementById("subject").addEventListener("click", subjectBuild);
    document.getElementById("admission").addEventListener("click", admissionBuild);
    document.getElementById("department").addEventListener("click", departmentBuild);
    document.getElementById("grading").addEventListener("click", gradingBuild);
    document.getElementById("permissions").addEventListener("click", permissionsBuild);
    document.getElementById("onloading").addEventListener("click", onloadingBuild);
    document.getElementById("records").addEventListener("click", recordsBuild);
    document.getElementById("leave").addEventListener("click", leaveBuild);
    document.getElementById("appraisals").addEventListener("click", appraisalsBuild);
    document.getElementById("designation").addEventListener("click", designationBuild);
    document.getElementById("offloading").addEventListener("click", offloadingBuild);
    document.getElementById("billcreation").addEventListener("click", billcreationBuild);
    document.getElementById("billing").addEventListener("click", billingBuild);
    document.getElementById("feecollection").addEventListener("click", feecollectionBuild);
    document.getElementById("paymenthistory").addEventListener("click", paymenthistoryBuild);
    document.getElementById("paymentchecker").addEventListener("click", paymentcheckerBuild);
    document.getElementById("salarysetup").addEventListener("click", salarysetupBuild);
    document.getElementById("payslipgeneration").addEventListener("click", payslipgenerationBuild);
    document.getElementById("ledgerbooks").addEventListener("click", ledgerbooksBuild);
    document.getElementById("incomestatement").addEventListener("click", incomestatementBuild);
    document.getElementById("cashflow").addEventListener("click", cashflowBuild);
    document.getElementById("trialbalanace").addEventListener("click", trialbalanaceBuild);
    /*
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);
    document.getElementById("institution").addEventListener("click", institutionBuild);*/


/*
    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });
*/


}

    // Utility to remove unwanted scripts/links
function removeUnwantedResources(tagName) {
        const mother=document.getElementsByTagName("body")[0];
        var lst=[];
        lst=Array.from(mother.getElementsByTagName(tagName))
                  .filter(el => el.getAttribute('data-dynamic') === 'true');
        for(i=0; i< lst.length; i++){
              mother.removeChild(lst[i])
         }
        if(mother.getElementsByClassName("sweet-overlay").length>0){
        //mother.removeChild(mother.getElementsByClassName("sweet-overlay")[0].parentNode)
    }
}

// Utility to add new scripts or links
function addNewResources(tagName, newResources) {
    const parentTag = tagName === "script" ? "body" : "head";
    newResources.forEach((srcOrHref) => {
        const newElement = document.createElement(tagName);
        if (tagName === "script") {
            newElement.src = srcOrHref;
            newElement.type = "text/javascript";
        } else {
            newElement.href = srcOrHref;
            newElement.rel = "stylesheet";
        }
        newElement.setAttribute("data-dynamic", "true");  // Mark as dynamically added
        document.querySelector(parentTag).appendChild(newElement);
    });
}




function institutionBuild() {
    // Define new resources specific to this view
    const newScripts = [
 "vendor/jquery-validation/jquery.validate.min.js",
"vendor/sparkline/index.js",
"vendor/sweetalert/lib/sweet-alert.min.js",
"vendor/ladda/dist/spin.min.js",
"vendor/ladda/dist/ladda.min.js",
"vendor/ladda/dist/ladda.jquery.min.js",
"scripts/subscripts/institution.js"
    ];

    const newLinks = [
"vendor/sweetalert/lib/sweet-alert.css",
"vendor/fontawesome/css/font-awesome.css",
"vendor/metisMenu/dist/metisMenu.css",
"vendor/animate.css/animate.css",
"vendor/bootstrap/dist/css/bootstrap.css",
"vendor/ladda/dist/ladda-themeless.min.css"
    ];

    // Remove previous non-default scripts/links
    removeUnwantedResources("script", activeScripts);
    removeUnwantedResources("link", activeLinks);

    // Add new resources
    addNewResources("script", newScripts);
    addNewResources("link", newLinks);

    // Update the active state with new resources
    newScripts.forEach((src) => activeScripts.add(src));
    newLinks.forEach((href) => activeLinks.add(href));
}
//-------------------------------------------------------------------------------------------------------

function classgroupnBuild() {

    // Define new resources specific to this view
        const newScripts = [
            "vendor/jquery-validation/jquery.validate.min.js",
                 "vendor/sweetalert/lib/sweet-alert.min.js",
                 "vendor/sparkline/index.js",
                 "scripts/subscripts/classgroup.js"
        ];
        const newLinks = [
            "vendor/sweetalert/lib/sweet-alert.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css"
        ];

        // Remove previous non-default scripts/links
        removeUnwantedResources("script", activeScripts);
        removeUnwantedResources("link", activeLinks);

        // Add new resources
        addNewResources("script", newScripts);
        addNewResources("link", newLinks);

        // Update the active state with new resources
        newScripts.forEach((src) => activeScripts.add(src));
        newLinks.forEach((href) => activeLinks.add(href));

}
//-------------------------------------------------------------------------------------------------------

function classesBuild() {

    // Define new resources specific to this view
        const newScripts = [
            "vendor/jquery-validation/jquery.validate.min.js",
                "vendor/sparkline/index.js",
                "vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                 "vendor/pdfmake/build/vfs_fonts.js",
                 "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                  "vendor/datatables.net-buttons/js/buttons.print.min.js",
                  "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                  "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",
                  "scripts/subscripts/classes.js"
        ];
        const newLinks = [
            "vendor/sweetalert/lib/sweet-alert.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css"
        ];

        // Remove previous non-default scripts/links
        removeUnwantedResources("script", activeScripts);
        removeUnwantedResources("link", activeLinks);

        // Add new resources
        addNewResources("script", newScripts);
        addNewResources("link", newLinks);

        // Update the active state with new resources
        newScripts.forEach((src) => activeScripts.add(src));
        newLinks.forEach((href) => activeLinks.add(href));

}

//-------------------------------------------------------------------------------------------------------

function subjectBuild() {

    // Define new resources specific to this view
        const newScripts = [
            "vendor/sparkline/index.js",
                "vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                "vendor/pdfmake/build/vfs_fonts.js",
                "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                "vendor/datatables.net-buttons/js/buttons.print.min.js",
                "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",
                "scripts/subscripts/subject.js"
        ];
        const newLinks = [
            "vendor/sweetalert/lib/sweet-alert.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css"
        ];

        // Remove previous non-default scripts/links
        removeUnwantedResources("script", activeScripts);
        removeUnwantedResources("link", activeLinks);

        // Add new resources
        addNewResources("script", newScripts);
        addNewResources("link", newLinks);

        // Update the active state with new resources
        newScripts.forEach((src) => activeScripts.add(src));
        newLinks.forEach((href) => activeLinks.add(href));

}

//-------------------------------------------------------------------------------------------------------

function admissionBuild() {

    // Define new resources specific to this view
        const newScripts = [
            "scripts/moment.min.js",
                "scripts/daterangepicker.js",
                "vendor/jquery-validation/jquery.validate.min.js",
                "vendor/sparkline/index.js",
                "vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                "vendor/pdfmake/build/vfs_fonts.js",
                "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                "vendor/datatables.net-buttons/js/buttons.print.min.js",
                "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",
                "scripts/subscripts/admissions.js"
        ];
        const newLinks = [
            "vendor/sweetalert/lib/sweet-alert.css",
                "styles/daterangepicker.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css"
        ];

        // Remove previous non-default scripts/links
        removeUnwantedResources("script", activeScripts);
        removeUnwantedResources("link", activeLinks);

        // Add new resources
        addNewResources("script", newScripts);
        addNewResources("link", newLinks);

        // Update the active state with new resources
        newScripts.forEach((src) => activeScripts.add(src));
        newLinks.forEach((href) => activeLinks.add(href));
}

//-------------------------------------------------------------------------------------------------------

function departmentBuild() {

    // Define new resources specific to this view
        const newScripts = [
            "scripts/moment.min.js",
                "scripts/daterangepicker.js",
                "vendor/jquery-validation/jquery.validate.min.js",
                "vendor/sparkline/index.js",
                "vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                "vendor/pdfmake/build/vfs_fonts.js",
                "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                "vendor/datatables.net-buttons/js/buttons.print.min.js",
                "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",
                "scripts/subscripts/department.js"
        ];
        const newLinks = [
            "vendor/sweetalert/lib/sweet-alert.css",
                "styles/daterangepicker.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css"
        ];

        // Remove previous non-default scripts/links
        removeUnwantedResources("script", activeScripts);
        removeUnwantedResources("link", activeLinks);

        // Add new resources
        addNewResources("script", newScripts);
        addNewResources("link", newLinks);

        // Update the active state with new resources
        newScripts.forEach((src) => activeScripts.add(src));
        newLinks.forEach((href) => activeLinks.add(href));


}

//-------------------------------------------------------------------------------------------------------

function gradingBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


    var script1 = document.createElement("script");
    script1.setAttribute("type", "text/javascript");
    script1.setAttribute("src", "vendor/jquery-validation/jquery.validate.min.js");
    document.getElementsByTagName("body")[0].appendChild(script1);

    var script2 = document.createElement("script");
    script2.setAttribute("type", "text/javascript");
    script2.setAttribute("src", "vendor/sparkline/index.js");
    document.getElementsByTagName("body")[0].appendChild(script2);

    var script3 = document.createElement("script");
    script3.setAttribute("type", "text/javascript");
    script3.setAttribute("src", "vendor/datatables/media/js/jquery.dataTables.min.js");
    document.getElementsByTagName("body")[0].appendChild(script3);

    var script4 = document.createElement("script");
    script4.setAttribute("type", "text/javascript");
    script4.setAttribute("src", "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js");
    document.getElementsByTagName("body")[0].appendChild(script4);

    var script5 = document.createElement("script");
    script5.setAttribute("type", "text/javascript");
    script5.setAttribute("src", "vendor/pdfmake/build/pdfmake.min.js");
    document.getElementsByTagName("body")[0].appendChild(script5);

    var script6 = document.createElement("script");
    script6.setAttribute("type", "text/javascript");
    script6.setAttribute("src", "vendor/pdfmake/build/vfs_fonts.js");
    document.getElementsByTagName("body")[0].appendChild(script6);

    var script7 = document.createElement("script");
    script7.setAttribute("type", "text/javascript");
    script7.setAttribute("src", "vendor/datatables.net-buttons/js/buttons.html5.min.js");
    document.getElementsByTagName("body")[0].appendChild(script7);

    var script8 = document.createElement("script");
    script8.setAttribute("type", "text/javascript");
    script8.setAttribute("src", "vendor/datatables.net-buttons/js/buttons.print.min.js");
    document.getElementsByTagName("body")[0].appendChild(script8);

    var script9 = document.createElement("script");
    script9.setAttribute("type", "text/javascript");
    script9.setAttribute("src", "vendor/datatables.net-buttons/js/dataTables.buttons.min.js");
    document.getElementsByTagName("body")[0].appendChild(script9);

    var script10 = document.createElement("script");
    script10.setAttribute("type", "text/javascript");
    script10.setAttribute("src", "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js");
    document.getElementsByTagName("body")[0].appendChild(script10);

    var script11 = document.createElement("script");
    script11.setAttribute("type", "text/javascript");
    script11.setAttribute("src", "scripts/subscripts/grading.js");
    document.getElementsByTagName("body")[0].appendChild(script11);


    /**========================================= */

    var link = document.createElement("link");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", "vendor/sweetalert/lib/sweet-alert.css");
    document.getElementsByTagName("head")[0].appendChild(link);

    var link1 = document.createElement("link");
    link1.setAttribute("rel", "stylesheet");
    link1.setAttribute("href", "vendor/metisMenu/dist/metisMenu.css");
    document.getElementsByTagName("head")[0].appendChild(link1);

    var link2 = document.createElement("link");
    link2.setAttribute("rel", "stylesheet");
    link2.setAttribute("href", "vendor/animate.css/animate.css");
    document.getElementsByTagName("head")[0].appendChild(link2);

    var link3 = document.createElement("link");
    link3.setAttribute("rel", "stylesheet");
    link3.setAttribute("href", "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css");
    document.getElementsByTagName("head")[0].appendChild(link3);

    /*var link4 = document.createElement("link");
    link4.setAttribute("rel", "stylesheet");
    link4.setAttribute("href", "fonts/pe-icon-7-stroke/css/pe-icon-7-stroke.css");
    document.getElementsByTagName("head")[0].appendChild(link4);

    var link5 = document.createElement("link");
    link5.setAttribute("rel", "stylesheet");
    link5.setAttribute("href", "fonts/pe-icon-7-stroke/css/helper.css");
    document.getElementsByTagName("head")[0].appendChild(link5);*/

}

//-------------------------------------------------------------------------------------------------------

function permissionsBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


    var script1b = document.createElement("script");
    script1b.setAttribute("type", "text/javascript");
    script1b.setAttribute("src", "scripts/moment.min.js");
    document.getElementsByTagName("body")[0].appendChild(script1b);

    var script1a = document.createElement("script");
    script1a.setAttribute("type", "text/javascript");
    script1a.setAttribute("src", "scripts/daterangepicker.js");
    document.getElementsByTagName("body")[0].appendChild(script1a);

    var script1 = document.createElement("script");
    script1.setAttribute("type", "text/javascript");
    script1.setAttribute("src", "vendor/jquery-validation/jquery.validate.min.js");
    document.getElementsByTagName("body")[0].appendChild(script1);

    var script2 = document.createElement("script");
    script2.setAttribute("type", "text/javascript");
    script2.setAttribute("src", "vendor/sparkline/index.js");
    document.getElementsByTagName("body")[0].appendChild(script2);

    var script3 = document.createElement("script");
    script3.setAttribute("type", "text/javascript");
    script3.setAttribute("src", "vendor/datatables/media/js/jquery.dataTables.min.js");
    document.getElementsByTagName("body")[0].appendChild(script3);

    var script4 = document.createElement("script");
    script4.setAttribute("type", "text/javascript");
    script4.setAttribute("src", "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js");
    document.getElementsByTagName("body")[0].appendChild(script4);

    var script5 = document.createElement("script");
    script5.setAttribute("type", "text/javascript");
    script5.setAttribute("src", "vendor/pdfmake/build/pdfmake.min.js");
    document.getElementsByTagName("body")[0].appendChild(script5);

    var script6 = document.createElement("script");
    script6.setAttribute("type", "text/javascript");
    script6.setAttribute("src", "vendor/pdfmake/build/vfs_fonts.js");
    document.getElementsByTagName("body")[0].appendChild(script6);

    var script7 = document.createElement("script");
    script7.setAttribute("type", "text/javascript");
    script7.setAttribute("src", "vendor/datatables.net-buttons/js/buttons.html5.min.js");
    document.getElementsByTagName("body")[0].appendChild(script7);

    var script8 = document.createElement("script");
    script8.setAttribute("type", "text/javascript");
    script8.setAttribute("src", "vendor/datatables.net-buttons/js/buttons.print.min.js");
    document.getElementsByTagName("body")[0].appendChild(script8);

    var script9 = document.createElement("script");
    script9.setAttribute("type", "text/javascript");
    script9.setAttribute("src", "vendor/datatables.net-buttons/js/dataTables.buttons.min.js");
    document.getElementsByTagName("body")[0].appendChild(script9);

    var script10 = document.createElement("script");
    script10.setAttribute("type", "text/javascript");
    script10.setAttribute("src", "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js");
    document.getElementsByTagName("body")[0].appendChild(script10);

    var script11 = document.createElement("script");
    script11.setAttribute("type", "text/javascript");
    script11.setAttribute("src", "scripts/subscripts/permissions.js");
    document.getElementsByTagName("body")[0].appendChild(script11);


    /**========================================= */

    var link = document.createElement("link");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", "vendor/sweetalert/lib/sweet-alert.css");
    document.getElementsByTagName("head")[0].appendChild(link);

    var link = document.createElement("link");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", "styles/daterangepicker.css");
    document.getElementsByTagName("head")[0].appendChild(link);

    var link1 = document.createElement("link");
    link1.setAttribute("rel", "stylesheet");
    link1.setAttribute("href", "vendor/metisMenu/dist/metisMenu.css");
    document.getElementsByTagName("head")[0].appendChild(link1);

    var link2 = document.createElement("link");
    link2.setAttribute("rel", "stylesheet");
    link2.setAttribute("href", "vendor/animate.css/animate.css");
    document.getElementsByTagName("head")[0].appendChild(link2);

    var link3 = document.createElement("link");
    link3.setAttribute("rel", "stylesheet");
    link3.setAttribute("href", "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css");
    document.getElementsByTagName("head")[0].appendChild(link3);

    var link4 = document.createElement("link");
    link4.setAttribute("rel", "stylesheet");
    link4.setAttribute("href", "fonts/pe-icon-7-stroke/css/pe-icon-7-stroke.css");
    document.getElementsByTagName("head")[0].appendChild(link4);

    var link5 = document.createElement("link");
    link5.setAttribute("rel", "stylesheet");
    link5.setAttribute("href", "styles/switch.css");
    document.getElementsByTagName("head")[0].appendChild(link5);

   /* var link5a = document.createElement("link");
    link5a.setAttribute("rel", "stylesheet");
    link5a.setAttribute("href", "styles/switch.css");
    document.getElementsByTagName("head")[0].appendChild(link5a);*/

}

//-------------------------------------------------------------------------------------------------------

function onloadingBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function recordsBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function leaveBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function appraisalsBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function designationBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });

}

//-------------------------------------------------------------------------------------------------------

function offloadingBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });

}

//-------------------------------------------------------------------------------------------------------

function billcreationBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {


           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {


           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {


           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function billingBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });


    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

$('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function feecollectionBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });

}

//-------------------------------------------------------------------------------------------------------

function paymenthistoryBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function paymentcheckerBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });

}

//-------------------------------------------------------------------------------------------------------

function salarysetupBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function payslipgenerationBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function ledgerbooksBuild() {

    $('script').each(function () {

            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });

}

//-------------------------------------------------------------------------------------------------------

function incomestatementBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function cashflowBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {
        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {
        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------

function trialbalanaceBuild() {

    $('script').each(function () {
            if (absoultePath.indexOf('subscripts') != -1) {
           scriptsToRemove.push(this);
            }
        });

    $('script').each(function () {
        defaultScripts.forEach((item, ind) =>{
            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
            });
        });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {
           scriptsToRemove.push(this);
        }
    });

    $('link').each(function () {
        defaultlink.forEach((item, ind) => {
            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {
           scriptsToRemove.push(this);
        }
    });

    scriptsToRemove.forEach(function(script) {
        if (script.parentNode) {
            script.parentNode.removeChild(script);
        }
    });


}

//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------------

function HttpPost(url,data){

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var access_token = $("meta[name='scope']").attr("content");

      return new Promise((resolve) => {
      $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + access_token
                },
            url : url,
            type : 'POST',
            data : JSON.stringify(data),
            beforeSend : function(xhr) {
                xhr.setRequestHeader(header, token);
            },
                 success: function (data) {
                         resolve(data);
                 },
                  error : function(jqXHR, textStatus, errorThrown) {
                  console.log("Error Details: ", jqXHR, textStatus, errorThrown);
                      swal({
                             title: "Sorry!",
                             text:  "Operation Failed\n"   + jqXHR.responseText ,
                             type: "error"
                        });
                  }
        });
      })
}

