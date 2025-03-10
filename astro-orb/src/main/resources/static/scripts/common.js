window.type;
window.id;
window.clonable;
window.clonable1b;
window.clonable2;
window.name=[];
window.resultlist=[];
window.resultlist2=[];
cd4=[];
window.tabs;
window.specificTab;
window.specificStudentsTab;

window.instId = $("meta[name='institutionId']").attr("content");
window.type= $('[name="type"]').val();
cd4=$("meta[name='cd4']").attr("content").replace("[", "").replace("]", "").split(",");

var permissionGroups=[];
var permissionList=[];

const depGroups = document.querySelectorAll('.departmentGroup');
for (const depGroup of depGroups) {
    depGroup.style.display = 'none';
}

const links = document.querySelectorAll('.functionalGroup');
for (const link of links) {
    link.style.display = 'none';
}

cd4.forEach(function(p) {
var t1=p.trim().split(" ");
    if(!permissionGroups.includes(t1[0])){
    permissionGroups.push(t1[0]);
    }
    permissionList.push(t1[1]);
})

    permissionList = permissionList.filter(item => item !== undefined);

    permissionGroups = new Set(permissionGroups.map(item => item.replace(/_/g, ' ').replace("_", "").replace("-", "").trim().toLowerCase()));
    permissionList = new Set(permissionList.map(item => item.replace(/_/g, ' ').replace("_", "").replace("-", "").trim().toLowerCase()));

links.forEach(anchor => {
   const plainText = anchor.textContent.replace(/\s+/g, "").replace("_", "").replace("-", "").trim().toLowerCase();
           if ([...permissionList].some(permission => plainText.includes(permission.replace(/\s+/g, "").replace("_", "").replace("-", "").trim().toLowerCase()))) {
               anchor.style.removeProperty('display'); // Remove display style on match
           } else {
               anchor.style.display = 'none'; // Explicitly hide unmatched anchors
           }
});

depGroups.forEach(anchor => {
   const plainText = anchor.textContent.trim().toLowerCase();
           if ([...permissionGroups].some(permission => plainText.includes(permission))) {
               anchor.style.removeProperty('display'); // Remove display style on match
           } else {
               anchor.style.display = 'none'; // Explicitly hide unmatched anchors
           }
});

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
    document.getElementById("scoreUpload").addEventListener("click", scoreUploadBuild);
    document.getElementById("terminalReport").addEventListener("click", terminalReportBuild);
    document.getElementById("transcript").addEventListener("click", studentsTranscriptBuild);

    document.getElementById("studEnrollment").addEventListener("click", studentBulkUploadBuild);
    /*document.getElementById("institution").addEventListener("click", institutionBuild);
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
                "vendor/sweetalert/lib/sweet-alert.min.js",
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
                "vendor/sweetalert/lib/sweet-alert.min.js",
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
                "vendor/sweetalert/lib/sweet-alert.min.js",
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
                "vendor/sweetalert/lib/sweet-alert.min.js",
                //"vendor/jquery-validation/jquery.validate.min.js",
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
                "vendor/sweetalert/lib/sweet-alert.min.js",
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

function scoreUploadBuild() {

    // Define new resources specific to this view
        const newScripts = [
                "vendor/sweetalert/lib/sweet-alert.min.js",
                 "scripts/subscripts/scoreUpload.js"
        ];
        const newLinks = [
                "vendor/sweetalert/lib/sweet-alert.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css",
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

function terminalReportBuild() {

    // Define new resources specific to this view
        const newScripts = [
                "vendor/sweetalert/lib/sweet-alert.min.js",
                 "scripts/subscripts/terminalReport.js"
        ];
        const newLinks = [
                "vendor/sweetalert/lib/sweet-alert.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css",
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

function studentsTranscriptBuild() {

    // Define new resources specific to this view
        const newScripts = [
                "vendor/sweetalert/lib/sweet-alert.min.js",
                 "scripts/subscripts/studentsTranscript.js"
        ];
        const newLinks = [
                "vendor/sweetalert/lib/sweet-alert.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css",
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



function studentBulkUploadBuild() {

    // Define new resources specific to this view
        const newScripts = [
                "vendor/sweetalert/lib/sweet-alert.min.js",
                 "scripts/subscripts/studentBulkUpload.js"
        ];
        const newLinks = [
                "vendor/sweetalert/lib/sweet-alert.css",
                "vendor/metisMenu/dist/metisMenu.css",
                "vendor/animate.css/animate.css",
                "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css",
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


//=========================================IMG UTIL =============================================



async function uploadIMGAsJSON(dataFile, statuses) {
            const input = dataFile//document.getElementById('pdfInput');
            const status =statuses //document.getElementById('status');

            // Check if a file is selected
            if (!input.files || input.files.length === 0) {
                status.textContent = "Please select an Image file!";
                status.style.color = "red";
                return;
            }

            const file = input.files[0];

            // Check if the selected file is a PDF
            if ((file.type).indexOf('image') ===-1 ) {
                status.textContent = "Only Image files are allowed!";
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



//=========================================PDF UTIL================================================


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



        async function setFileInputFromByteArray(base64String,el,extension,fileName) {

            if (!base64String) {
               console.error("Base64 string is empty or null.");
               return;
            }
            if(base64String!=null){
               try {

                   if(extension.indexOf("pdf")!=-1){
                   // Extract MIME type from Base64 string (e.g., "data:application/pdf;base64,...")
                  const mimeType = base64String.match(/data:([^;]+);base64/)?.[1] || 'application/octet-stream';
                      extension="application/pdf"

                      // Remove the Base64 metadata (if present)
                             const base64Content = base64String;


                              var name=base64String.split(("pdf_"))[0]+"pdf";
                                  const imgString=base64String.split(("_application/pdf_"))[1].split(',').pop();

                                  const blob = await base64ToBlobStream(imgString,extension);
                                  // Create a File object from the Blob
                                const  file = new File([blob], name, { type: extension});


                              // Create a DataTransfer object to hold the file
                              const dataTransfer = new DataTransfer();
                              dataTransfer.items.add(file);

                              // Assign the files to the input element (this simulates selecting the file)
                              el.files = dataTransfer.files;

                              // Trigger the 'change' event on the file input
                              const event = new Event('change', { bubbles: true });
                              el.dispatchEvent(event);
                             // uploadPDF(el);

                      }else{
                      const mimeType = base64String.match(/data:([^;]+);base64/)?.[1] || 'application/octet-stream';
                      extension="image/png"

                      // Remove the Base64 metadata (if present)
                             const base64Content = base64String.split((/_image.*?_/))[1].split(',').pop();

                                  const blob = await base64ToBlobStream(base64Content,extension);
                                  // Create a File object from the Blob
                                const  file = new File([blob], fileName, { type: "image/png"});

                                   console.log(base64Content)

                              // Create a DataTransfer object to hold the file
                              const dataTransfer = new DataTransfer();
                              dataTransfer.items.add(file);

                              // Assign the files to the input element (this simulates selecting the file)
                              el.files = dataTransfer.files;

                              // Trigger the 'change' event on the file input
                              const event = new Event('change', { bubbles: true });
                              el.dispatchEvent(event);
                             // uploadPDF(el);
                      }

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


//=========================================General File UTIL================================================


    async function uploadFileAsJSON(dataFile, statuses) {
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
           /* if (file.type !== "application/pdf") {
                status.textContent = "Only PDF files are allowed!";
                status.style.color = "red";
                return;
            }*/

            try {
                // Convert the file to a Base64 string
                const base64File =await convertFileToBase64(file);

                // Create the JSON object
                const jsonObject = {
                    fileName: file.name,
                    fileType: file.type,
                    fileContent: base64File // Add the Base64 string here
                };
                return jsonObject

            } catch (error) {
                status.textContent = `Error sending file: ${error.message}`;
                status.style.color = "red";
                console.error('Error:', error);
            }
            return null
    }


        async function setAllFileTypeInputFromByteArray(base64String, el, fileName) {
            if (!base64String) {
                console.error("Base64 string is empty or null.");
                return;
            }

            try {
                // Extract MIME type from Base64 string (e.g., "data:application/pdf;base64,...")
                const mimeTypeMatch = base64String.match(/data:([^;]+);base64,/);
                const mimeType = mimeTypeMatch ? mimeTypeMatch[1] : 'application/octet-stream'; // Default to octet-stream if not found

                // Detect file extension from MIME type
                let extension = mimeType.split('/')[1]; // Extract file extension from MIME type (e.g., 'pdf', 'png', 'jpeg', etc.)

                // Remove the Base64 metadata (everything before and including the comma)
                const base64Content = base64String.split(',').pop(); // Extract only the Base64 content

                // Convert the base64 string to Blob
                const blob = await base64ToBlobStream(base64Content, mimeType);

                // Determine file name (use provided name or generate one based on MIME type)
                let name = fileName || `file.${extension}`;

                // Create a File object from the Blob
                const file = new File([blob], name, { type: mimeType });

                // Create a DataTransfer object to hold the file (required for simulating file input)
                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);

                // Assign the files to the input element (this simulates selecting the file)
                el.files = dataTransfer.files;

                // Trigger the 'change' event on the file input
                const event = new Event('change', { bubbles: true });
                el.dispatchEvent(event);

                console.log('File successfully attached:', el.files[0]);
            } catch (error) {
                console.error('Error attaching file:', error);
            }
        }



        function uploadGeneralFile(el) {
            const fileInput = el; // The file input element
            const file = fileInput.files[0]; // Get the selected file
            const error = document.getElementById('error'); // Error display element
            const filePreview = el.parentElement.getElementsByClassName('fileOutput')[0]; // Preview container
            filePreview.innerHTML = ""; // Clear previous preview

            if (file) {
                const fileType = file.type; // Get the file's MIME type

                // Hide any previous error message
                error.style.display = 'none';

                // Handle PDF files
                if (fileType === 'application/pdf') {
                    // Create an object URL for the PDF file
                    const fileURL = URL.createObjectURL(file);

                    // Embed the PDF preview using an <iframe>
                    const iframe = document.createElement('iframe');
                    iframe.src = fileURL;
                    iframe.width = '100%';
                    iframe.style.border = '1px solid #ccc';
                    filePreview.appendChild(iframe);
                }
                // Handle image files (png, jpg, jpeg, gif, etc.)
                else if (fileType.startsWith('image/')) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        // Create an <img> element and set its source to the file content
                        const img = document.createElement('img');
                        img.src = e.target.result;
                        img.style.maxWidth = '100%'; // Limit the image width to the container's width
                        img.style.height = 'auto';
                        filePreview.appendChild(img);
                    };
                    reader.readAsDataURL(file); // Read the image file as a data URL
                }
                // Handle video files (mp4, webm, ogg, etc.)
                else if (fileType.startsWith('video/')) {
                    const fileURL = URL.createObjectURL(file);

                    // Create a <video> element to preview the video file
                    const video = document.createElement('video');
                    video.src = fileURL;
                    video.controls = true; // Add controls to the video player (play, pause, etc.)
                    video.width = '100%';
                    filePreview.appendChild(video);
                }
                // Handle text files (txt, csv, etc.)
                else if (fileType === 'text/plain') {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        // Display the file content inside a <pre> element
                        const pre = document.createElement('pre');
                        pre.textContent = e.target.result;
                        filePreview.appendChild(pre);
                    };
                    reader.readAsText(file); // Read the text file as text
                }
                // Handle unsupported file types
                else {
                    error.style.display = 'block';
                    error.textContent = 'Unsupported file type. Please upload a valid file.';
                }
            } else {
                alert('Please select a file to upload.');
            }
        }

async function base64ToJson(base64String) {
    const binaryString = atob(base64String);
        const bytes = new Uint8Array(binaryString.length);

        for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }

        const workbook = XLSX.read(bytes, { type: "array" });

        // Get first sheet name
        const sheetName = workbook.SheetNames[0];

        // Convert the sheet to JSON
        return XLSX.utils.sheet_to_json(workbook.Sheets[sheetName]);
}

function excelDateToJSDate(serial) {
    if (!serial || isNaN(serial)) return serial; // Return as-is if it's not a number

    const excelEpoch = new Date(1899, 11, 30); // Excel counts from Dec 30, 1899
    const jsDate = new Date(excelEpoch.getTime() + serial * 86400000); // Convert to JS date

    return jsDate.toISOString().split("T")[0]; // Format as "YYYY-MM-DD"
}