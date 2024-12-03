window.type;
window.id;
window.clonable;
window.clonable1b;
window.clonable2;
window.name=[];
window.resultlist=[];
window.resultlist2=[];
window.tabs;
window.specificTab;
window.specificStudentsTab;

window.instId = $("meta[name='institutionId']").attr("content");
window.type= $('[name="type"]').val();

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

/* "vendor/datatables/media/js/jquery.dataTables.min.js",
 "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
 "vendor/pdfmake/build/pdfmake.min.js",
 "vendor/pdfmake/build/vfs_fonts.js",
 "vendor/datatables.net-buttons/js/buttons.html5.min.js",
 "vendor/datatables.net-buttons/js/buttons.print.min.js",
 "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
 "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",*/


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


window.copyrights= function copyrights(){
    $('#copyrightYear').text(getYear());

    function getYear(){
    var objToday = new Date();
    var curYear = objToday.getFullYear();
    return curYear;
    }


    initializeDefaults();
    addEventListeners();  // Attach all event listeners
};

window.addEventListener("load", window.copyrights)

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
    document.getElementById("designation").addEventListener("click", designationBuild);
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
        if(mother.getElementsByClassName("sweet-overlay").length>1){
        mother.removeChild(mother.getElementsByClassName("sweet-overlay")[1].parentElement)
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
        "vendor/sparkline/index.js",
        "vendor/ladda/dist/spin.min.js",
        "vendor/ladda/dist/ladda.min.js",
        "vendor/ladda/dist/ladda.jquery.min.js",
        "vendor/sweetalert/lib/sweet-alert.min.js",
        "vendor/jquery-validation/jquery.validate.min.js",
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
        "vendor/sweetalert/lib/sweet-alert.min.js",
        "vendor/jquery-validation/jquery.validate.min.js",
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
                /*"vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                "vendor/pdfmake/build/vfs_fonts.js",
                "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                "vendor/datatables.net-buttons/js/buttons.print.min.js",
                "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",*/
                "vendor/jquery-validation/jquery.validate.min.js",
                "vendor/sweetalert/lib/sweet-alert.min.js",
                "vendor/jquery-validation/jquery.validate.min.js",
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
                /*"vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                "vendor/pdfmake/build/vfs_fonts.js",
                "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                "vendor/datatables.net-buttons/js/buttons.print.min.js",
                "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",*/
                "vendor/sweetalert/lib/sweet-alert.min.js",
                "vendor/jquery-validation/jquery.validate.min.js",
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
               /* "vendor/sparkline/index.js",
                "vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                "vendor/pdfmake/build/vfs_fonts.js",
                "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                "vendor/datatables.net-buttons/js/buttons.print.min.js",
                "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",*/
                "vendor/sweetalert/lib/sweet-alert.min.js",
                "vendor/jquery-validation/jquery.validate.min.js",
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
                /*"scripts/daterangepicker.js",
                "vendor/sparkline/index.js",
                "vendor/datatables/media/js/jquery.dataTables.min.js",
                "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
                "vendor/pdfmake/build/pdfmake.min.js",
                "vendor/pdfmake/build/vfs_fonts.js",
                "vendor/datatables.net-buttons/js/buttons.html5.min.js",
                "vendor/datatables.net-buttons/js/buttons.print.min.js",
                "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
                "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",*/
                "vendor/sweetalert/lib/sweet-alert.min.js",
                "vendor/jquery-validation/jquery.validate.min.js",
                "scripts/subscripts/department.js"
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

function gradingBuild() {

const newScripts = [
    "vendor/jquery-validation/jquery.validate.min.js",
   /* "vendor/sparkline/index.js",
    "vendor/datatables/media/js/jquery.dataTables.min.js",
    "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
    "vendor/pdfmake/build/pdfmake.min.js",
    "vendor/pdfmake/build/vfs_fonts.js",
    "vendor/datatables.net-buttons/js/buttons.html5.min.js",
    "vendor/datatables.net-buttons/js/buttons.print.min.js",
    "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
    "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",*/
    "vendor/sweetalert/lib/sweet-alert.min.js",
    "scripts/subscripts/grading.js"
     ];


    /**========================================= */
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

function permissionsBuild() {

const newScripts = [
    "scripts/moment.min.js",
    "vendor/jquery-validation/jquery.validate.min.js",
   /* "vendor/sparkline/index.js",
    "vendor/datatables/media/js/jquery.dataTables.min.js",
    "vendor/datatables.net-bs/js/dataTables.bootstrap.min.js",
    "vendor/pdfmake/build/pdfmake.min.js",
    "vendor/pdfmake/build/vfs_fonts.js",
    "vendor/datatables.net-buttons/js/buttons.html5.min.js",
    "vendor/datatables.net-buttons/js/buttons.print.min.js",
    "vendor/datatables.net-buttons/js/dataTables.buttons.min.js",
    "vendor/datatables.net-buttons-bs/js/buttons.bootstrap.min.js",*/
    "vendor/sweetalert/lib/sweet-alert.min.js",
    "scripts/subscripts/permissions.js"
];

    /**========================================= */
const newLinks = [
    "vendor/sweetalert/lib/sweet-alert.css",
    "vendor/metisMenu/dist/metisMenu.css",
    "vendor/animate.css/animate.css",
    "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css",
    "fonts/pe-icon-7-stroke/css/pe-icon-7-stroke.css",
    "styles/switch.css"
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

function designationBuild() {

    const newScripts = [
        "scripts/moment.min.js",
        "vendor/sweetalert/lib/sweet-alert.min.js",
        "vendor/jquery-validation/jquery.validate.min.js",
        "scripts/subscripts/hrDesignation.js"
    ];

    /**========================================= */
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

function onloadingBuild() {
 const newScripts = [
        "vendor/sparkline/index.js",
        "vendor/ladda/dist/spin.min.js",
        "vendor/ladda/dist/ladda.min.js",
        "vendor/ladda/dist/ladda.jquery.min.js",
        "vendor/sweetalert/lib/sweet-alert.min.js",
        "vendor/jquery-validation/jquery.validate.min.js",
        "scripts/subscripts/hrOnloading.js"
    ];

    const newLinks = [
        "vendor/sweetalert/lib/sweet-alert.css",
        "vendor/fontawesome/css/font-awesome.css",
        "vendor/metisMenu/dist/metisMenu.css",
        "vendor/animate.css/animate.css",
        "vendor/bootstrap/dist/css/bootstrap.css",
        "vendor/ladda/dist/ladda-themeless.min.css",
       "styles/daterangepicker.css",
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

function recordsBuild() {



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

function leaveBuild() {




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

function appraisalsBuild() {





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

function designationBuild_X() {





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

function offloadingBuild() {




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

function billcreationBuild() {

    const newScripts = [
        "scripts/moment.min.js",
        "vendor/sweetalert/lib/sweet-alert.min.js",
        "vendor/jquery-validation/jquery.validate.min.js",
        "scripts/subscripts/billCreation.js"
    ];

    /**========================================= */
    const newLinks = [
       "vendor/sweetalert/lib/sweet-alert.css",
       "vendor/metisMenu/dist/metisMenu.css",
       "vendor/animate.css/animate.css",
       "fonts/pe-icon-7-stroke/css/pe-icon-7-stroke.css",
       "fonts/pe-icon-7-stroke/css/helper.css"
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

function billingBuild() {

    // Define new resources specific to this view
        const newScripts = [
                "vendor/jquery-validation/jquery.validate.min.js",
                "vendor/sweetalert/lib/sweet-alert.min.js",
                "vendor/jquery-validation/jquery.validate.min.js",
                 "scripts/subscripts/financeBilling.js"
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

function feecollectionBuild() {





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

function paymenthistoryBuild() {





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

function paymentcheckerBuild() {





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

function salarysetupBuild() {





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

function payslipgenerationBuild() {





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

function ledgerbooksBuild() {





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

function incomestatementBuild() {





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

function cashflowBuild() {





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

function trialbalanaceBuild() {





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
                             text:  "Operation Failed\n"   + errorThrown ,
                             type: "error"
                        });
                  }
        });
      })
}

