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


    const defaultlink = [
    "images/favicon.ico",
    "vendor/fontawesome/css/font-awesome.css",
    "vendor/bootstrap/dist/css/bootstrap.css",
    "styles/style.css","styles/all.min.css",
    "dataTables/datatables.min.css"
    ];
    const absoultelinkPath = [];

  Array.from(document.getElementsByTagName("script")).forEach(function(s) {
        defaultScripts.forEach(function(d,i) {
            if(s.src.indexOf(d,i)!=-1){
              absoultePath[i] = s.src
            }
        })
    })

      Array.from(document.getElementsByTagName("script")).forEach(function(s) {
          if (absoultePath.indexOf(s.src) == -1) {
             s.parentNode.removeChild(s);
           }
        })


window.addEventListener("load", (event) => {

    $('#copyrightYear').text(getYear());


    function getYear(){
    var objToday = new Date();
    curYear = objToday.getFullYear();
    return curYear;
    }

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
        }
    });

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


    $('link').each(function () {
        defaultlink.forEach((item, ind) => {

            if (this.href.indexOf(item) != -1) {
                absoultelinkPath[ind] = this.href
            }
        });
    });

    $('link').each(function () {

        if (absoultelinkPath.indexOf(this.href) == -1) {

            this.parentNode.removeChild(this);
        }
    });


})




function institutionBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });

    $('.sweet-overlay').each(function () {
     this.parentNode.parentNode.removeChild(this.parentNode);
    });

    $('.sweet-alert').each(function () {
     this.parentNode.parentNode.removeChild(this.parentNode);
    });

    var script1 = document.createElement("script");
    script1.setAttribute("type", "text/javascript");
    script1.setAttribute("src", "vendor/jquery-validation/jquery.validate.min.js");
    document.getElementsByTagName("body")[0].appendChild(script1);

    var script7 = document.createElement("script");
    script7.setAttribute("type", "text/javascript");
    script7.setAttribute("src", "vendor/sparkline/index.js");
    document.getElementsByTagName("body")[0].appendChild(script7);

    /*var script8 = document.createElement("script");
    script8.setAttribute("type", "text/javascript");
    script8.setAttribute("src", "vendor/sweetalert/lib/sweet-alert.min.js");
    document.getElementsByTagName("body")[0].appendChild(script8);*/

    var script12 = document.createElement("script");
    script12.setAttribute("type", "text/javascript");
    script12.setAttribute("src", "vendor/sweetalert/lib/sweet-alert.min.js");
    document.getElementsByTagName("body")[0].appendChild(script12);


    var script9 = document.createElement("script");
    script9.setAttribute("type", "text/javascript");
    script9.setAttribute("src", "vendor/ladda/dist/spin.min.js");
    document.getElementsByTagName("body")[0].appendChild(script9);

    var script10 = document.createElement("script");
    script10.setAttribute("type", "text/javascript");
    script10.setAttribute("src", "vendor/ladda/dist/ladda.min.js");
    document.getElementsByTagName("body")[0].appendChild(script10);

    var script11 = document.createElement("script");
    script11.setAttribute("type", "text/javascript");
    script11.setAttribute("src", "vendor/ladda/dist/ladda.jquery.min.js");
    document.getElementsByTagName("body")[0].appendChild(script11);

    var script15 = document.createElement("script");
    script15.setAttribute("type", "text/javascript");
    script15.setAttribute("src", "scripts/subscripts/institution.js");
    document.getElementsByTagName("body")[0].appendChild(script15);


    /**========================================= */

    var link = document.createElement("link");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", "vendor/sweetalert/lib/sweet-alert.css");
    document.getElementsByTagName("head")[0].appendChild(link);

    var link1 = document.createElement("link");
    link1.setAttribute("rel", "stylesheet");
    link1.setAttribute("href", "vendor/fontawesome/css/font-awesome.css");
    document.getElementsByTagName("head")[0].appendChild(link1);

    var link2 = document.createElement("link");
    link2.setAttribute("rel", "stylesheet");
    link2.setAttribute("href", "vendor/metisMenu/dist/metisMenu.css");
    document.getElementsByTagName("head")[0].appendChild(link2);

    var link3 = document.createElement("link");
    link3.setAttribute("rel", "stylesheet");
    link3.setAttribute("href", "vendor/animate.css/animate.css");
    document.getElementsByTagName("head")[0].appendChild(link3);

    var link4 = document.createElement("link");
    link4.setAttribute("rel", "stylesheet");
    link4.setAttribute("href", "vendor/bootstrap/dist/css/bootstrap.css");
    document.getElementsByTagName("head")[0].appendChild(link4);

    var link5 = document.createElement("link");
    link5.setAttribute("rel", "stylesheet");
    link5.setAttribute("href", "vendor/ladda/dist/ladda-themeless.min.css");
    document.getElementsByTagName("head")[0].appendChild(link5);
}
//-------------------------------------------------------------------------------------------------------

function classgroupnBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });

    $('.sweet-overlay').each(function () {
     this.parentNode.parentNode.removeChild(this.parentNode);
    });

    $('.sweet-alert').each(function () {
     this.parentNode.parentNode.removeChild(this.parentNode);
    });


    var script1 = document.createElement("script");
    script1.setAttribute("type", "text/javascript");
    script1.setAttribute("src", "vendor/jquery-validation/jquery.validate.min.js");
    document.getElementsByTagName("body")[0].appendChild(script1);

    var script12x = document.createElement("script");
    script12x.setAttribute("type", "text/javascript");
    script12x.setAttribute("src", "vendor/sweetalert/lib/sweet-alert.min.js");
    document.getElementsByTagName("body")[0].appendChild(script12x);

    var script2 = document.createElement("script");
    script2.setAttribute("type", "text/javascript");
    script2.setAttribute("src", "vendor/sparkline/index.js");
    document.getElementsByTagName("body")[0].appendChild(script2);

    var script11 = document.createElement("script");
    script11.setAttribute("type", "text/javascript");
    script11.setAttribute("src", "scripts/subscripts/classgroup.js");
    document.getElementsByTagName("body")[0].appendChild(script11);

    /**========================================= */

    var link = document.createElement("link");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", "vendor/sweetalert/lib/sweet-alert.css");
    document.getElementsByTagName("head")[0].appendChild(link);

    var link3 = document.createElement("link");
    link3.setAttribute("rel", "stylesheet");
    link3.setAttribute("href", "vendor/datatables.net-bs/css/dataTables.bootstrap.min.css");
    document.getElementsByTagName("head")[0].appendChild(link3);
    
    var link1 = document.createElement("link");
    link1.setAttribute("rel", "stylesheet");
    link1.setAttribute("href", "vendor/metisMenu/dist/metisMenu.css");
    document.getElementsByTagName("head")[0].appendChild(link1);
    
    var link2 = document.createElement("link");
    link2.setAttribute("rel", "stylesheet");
    link2.setAttribute("href", "vendor/animate.css/animate.css");
    document.getElementsByTagName("head")[0].appendChild(link2);

   /* var link4 = document.createElement("link");
    link4.setAttribute("rel", "stylesheet");
    link4.setAttribute("href", "fonts/pe-icon-7-stroke/css/pe-icon-7-stroke.css");
    document.getElementsByTagName("head")[0].appendChild(link4);*/
  /*
    var link5 = document.createElement("link");
    link5.setAttribute("rel", "stylesheet");
    link5.setAttribute("href", "fonts/pe-icon-7-stroke/css/helper.css");
    document.getElementsByTagName("head")[0].appendChild(link5);*/

}
//-------------------------------------------------------------------------------------------------------

function classesBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
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
    script11.setAttribute("src", "scripts/subscripts/classes.js");
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

function subjectBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });


   /* var script1 = document.createElement("script");
    script1.setAttribute("type", "text/javascript");
    script1.setAttribute("src", "vendor/jquery-validation/jquery.validate.min.js");
    document.getElementsByTagName("body")[0].appendChild(script1);*/

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
    script11.setAttribute("src", "scripts/subscripts/subject.js");
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
    
  /*  var link4 = document.createElement("link");
    link4.setAttribute("rel", "stylesheet");
    link4.setAttribute("href", "fonts/pe-icon-7-stroke/css/pe-icon-7-stroke.css");
    document.getElementsByTagName("head")[0].appendChild(link4);
    
    var link5 = document.createElement("link");
    link5.setAttribute("rel", "stylesheet");
    link5.setAttribute("href", "fonts/pe-icon-7-stroke/css/helper.css");
    document.getElementsByTagName("head")[0].appendChild(link5);*/

}

//-------------------------------------------------------------------------------------------------------

function admissionBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
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
    script11.setAttribute("src", "scripts/subscripts/admissions.js");
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
    
   /* var link4 = document.createElement("link");
    link4.setAttribute("rel", "stylesheet");
    link4.setAttribute("href", "fonts/pe-icon-7-stroke/css/pe-icon-7-stroke.css");
    document.getElementsByTagName("head")[0].appendChild(link4);
    
    var link5 = document.createElement("link");
    link5.setAttribute("rel", "stylesheet");
    link5.setAttribute("href", "fonts/pe-icon-7-stroke/css/helper.css");
    document.getElementsByTagName("head")[0].appendChild(link5);*/
}

//-------------------------------------------------------------------------------------------------------

function departmentBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function gradingBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
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
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
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
    
    /*var link5 = document.createElement("link");
    link5.setAttribute("rel", "stylesheet");
    link5.setAttribute("href", "fonts/pe-icon-7-stroke/css/helper.css");
    document.getElementsByTagName("head")[0].appendChild(link5);
    
    var link5a = document.createElement("link");
    link5a.setAttribute("rel", "stylesheet");
    link5a.setAttribute("href", "styles/switch.css");
    document.getElementsByTagName("head")[0].appendChild(link5a);*/

}

//-------------------------------------------------------------------------------------------------------

function onloadingBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function recordsBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function leaveBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function appraisalsBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function designationBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function offloadingBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function billcreationBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function billingBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
        }
    });

        if (absoultelinkPath.indexOf(this.href) == -1) {

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function feecollectionBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function paymenthistoryBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function paymentcheckerBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function salarysetupBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function payslipgenerationBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function ledgerbooksBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function incomestatementBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function cashflowBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
        }
    });




}

//-------------------------------------------------------------------------------------------------------

function trialbalanaceBuild() {

    $('script').each(function () {
        defaultScripts.forEach((item, ind) => {

            if (this.src.indexOf(item) != -1) {
                absoultePath[ind] = this.src
            }
        });
    });

    $('script').each(function () {

        if (absoultePath.indexOf(this.src) == -1) {

            this.parentNode.removeChild(this);
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

            this.parentNode.removeChild(this);
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
                    'Authorization': 'Bearer' + access_token
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
                  error : function(errMsg) {
                      swal({
                             title: "Sorry!",
                             text:  "Operation Failed\n" + errMsg ,
                             type: "error"
                        });
                  }
        });
      })
}

