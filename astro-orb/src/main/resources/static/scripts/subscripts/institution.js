$(function () {

    //function institutionBuild() {
    let institution = `<div class="content animate-panel" id="pagecontent">
    <div class="row">
               <div class="col-lg-12">
                   <div class="hpanel">
                       <div class="panel-body">
   
                           <!--<form name="simpleForm" novalidate="novalidate" id="simpleForm" role="form">-->
   
                               <div class="text-center m-b-md" id="wizardControl">
   
                                   <a class="btn btn-primary wizardTabs" data-toggle="tab">Step 1 - Institution Data</a>
                                   <a class="btn btn-default wizardTabs" data-toggle="tab">Step 2 - Subscription Plan</a>
                                   <a class="btn btn-default wizardTabs" data-toggle="tab">Step 3 - Review Application</a>
                                   <a class="btn btn-default wizardTabs" data-toggle="tab">Step 4 - Approval</a>
   
                               </div>
   
                               <div class="tab-content">
                                   <div class="p-m tab-pane active">
                                       <!-- <form name="simpleForm" id="simpleForm" action="">-->
                                       <form id="institutionForm" role="form">
                                           <div class="row">
                                               <div class="col-lg-3 text-center">
                                                   <i class="fa fa-university fa-5x text-gradien" style="color: cornflowerblue;"></i>
                                                   <p class="small m-t-md">
                                                       <strong>Astromy LLC's Orb</strong> is a comprehensive school
                                                       management system designed with just the needs of the schools in
                                                       mind.
                                                       <br/><br/>Pre-ordering this system means, you putting your school on
                                                       the fastest track to orbit success
                                                   </p>
                                               </div>
                                               <div class="col-lg-9">
                                                   <div class="row">
                                                       <div class="form-group col-lg-12">
                                                           <label for="clientName">Institution</label>
                                                           <input type="text" requ ired id="clientName" class="form-control"
                                                                  name="clientName" minlength="3" placeholder="School Name">
                                                       </div>
                                                       <div class="form-group col-lg-12">
                                                           <label for="slogan">Slogan of Institution</label>
                                                           <input type="text" id="slogan" class="form-control"
                                                                  name="slogan" placeholder="School Slogan" minlength="5">
                                                       </div>
                                                       <div class="form-group col-lg-6">
                                                           <label for="country">Country</label>
                                                           <input type="text" autocomplete="off" id="country"
                                                                  class="form-control" placeholder="Country"
                                                                  name="country" minlength="2">
                                                       </div>
                                                       <div class="form-group col-lg-6">
                                                           <label for="region">Region</label>
                                                           <input type="text" autocomplete="off" required id="region"
                                                                  class="form-control" placeholder="Region" name="region" minlength="3">
                                                       </div>
                                                       <div class="form-group col-lg-6">
                                                           <label for="city">City</label>
                                                           <input type="text" required id="city" class="form-control"
                                                                  placeholder="City" name="city" minlength="5">
                                                       </div>
                                                       <div class="form-group col-lg-6">
                                                           <label for="email">Email</label>
                                                           <input type="text" autocomplete="off" required id="email"
                                                                  class="form-control" name="email"
                                                                  placeholder="Official email of school">
                                                       </div>
                                                   </div>
                                               </div>
                                           </div>
                                           <div class="row">
                                               <div class="col-lg-3 text-center">
                                                   <div>
                                                       <label for="crest">School Crest</label>
                                                   </div>
                                                   <output class="imageOutput" name="crest" id="crest"
                                                           style="height: 220px; width:220px; border-radius: 10px;display: inline-block;"></output>
                                                   <input class="imageInput" type="file"
                                                          style="width: 200px;padding: 12px;display: inline;"
                                                          accept="image/jpeg, image/png, image/jpg">
                                               </div>
                                               <div class="col-lg-9">
                                                   <div class="row">
   
                                                       <div class="form-group col-lg-6">
                                                           <label for="contact1">Contact</label>
                                                           <input type="text" required id="contact1" class="form-control"
                                                                  placeholder="Main Contact Number" name="contact1" minlength="10">
                                                       </div>
                                                       <div class="form-group col-lg-6">
                                                           <label for="contact2">Alt Contact</label>
                                                           <input type="text" id="contact2" class="form-control"
                                                                  placeholder="Altanative Contact" name="contact2" minlength="10">
                                                       </div>
                                                       <div class="form-group col-lg-6">
                                                           <label for="bececode">WAEC Code</label>
                                                           <input type="text" required id="bececode" class="form-control"
                                                                  placeholder="WAEC Code of School" name="bececode" minlength="5">
                                                       </div>
                                                       <div class="form-group col-lg-6">
                                                           <label for="postalAddress">Postal Address</label>
                                                           <input type="text" required id="postalAddress"
                                                                  class="form-control" name="postalAddress"
                                                                  placeholder="Official Postal Address of school" minlength="3">
                                                       </div>
   
                                                       <div class="form-group col-lg-6">
                                                           <label for="streams">Streams</label>
                                                           <input type="number" required id="streams" class="form-control"
                                                                  name="streams" placeholder="Number of Streams running">
                                                       </div>
   
                                                       <div class="form-group col-lg-6">
                                                           <label for="population">Population</label>
                                                           <input type="number" required id="population"
                                                                  class="form-control" name="population"
                                                                  placeholder="Student Population">
                                                       </div>
   
                                                       <div class="form-group col-lg-6">
                                                           <label for="website">Website</label>
                                                           <input type="text" id="website" class="form-control"
                                                                  name="website" placeholder="School website">
                                                       </div>

                                               <div class="col-lg-3 text-center">
                                                   <div>
                                                       <label for="headSignature">Head Signature</label>
                                                   </div>
                                                   <output class="headSignature" name="headSignature" id="headSignature"
                                                           style="height: 60px; border-radius: 10px;display: inline-block;"></output>
                                                   <input class="imageInput1" type="file"
                                                          style="width: 200px;padding: 12px;display: inline;"
                                                          accept="image/jpeg, image/png, image/jpg">
                                               </div>
                                                       <div>
                                                           <button id="instSubmit" class="btn btn-sm btn-primary m-t-n-xs"
                                                                   style="visibility: hidden;" type="submit">
                                                               <strong>Submit</strong></button>
                                                       </div>
                                                   </div>
                                               </div>
                                           </div>
                                       </form>
                                       <!-- <input id="fsubmit" type="submit" style="display: none;"></input>
                                        </form>-->
   
                                       <div class="text-right m-t-xs">
                                           <a class="btn btn-default prev">Previous</a>
                                           <a class="btn btn-default next">Next</a>
                                       </div>
   
                                   </div>
   
                                   <div class="p-m tab-pane">
   
                                       <div class="row">
                                           <div class="col-lg-12 text-center">
                                               <i class="pe-7s-credit fa-5x text-muted"></i>
                                               <p class="small m-t-md">
                                                   <strong>Subscriptions are One-time </strong>Choose from the various
                                                   plans. As a pre-ordered subscription, you are pushed 1 level up onto the
                                                   next plan.
                                               </p>
                                           </div>
                                           <div class="col-lg-12">
                                               <div class="row">
   
                                                   <div class="form-group col-sm-3">
                                                       <div class="hpanel plan-box hyellow subscriptionOption">
                                                           <div class="panel-heading hbuilt text-center">
                                                               <h4 class="font-bold subscriptionPlan">Free plan</h4>
                                                           </div>
                                                           <div class="panel-body">
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
                                                               <table class="table">
                                                                   <thead>
                                                                   <tr>
                                                                       <td>
                                                                           Features
                                                                       </td>
                                                                   </tr>
                                                                   </thead>
                                                                   <tbody>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Monthly
                                                                           Support
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Support by
                                                                           mail
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-square-o"></i> Website
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-square-o"></i> Support by calls
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-square-o"></i> Customization
                                                                       </td>
                                                                   </tr>
                                                                   </tbody>
                                                               </table>
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
   
                                                               <h4 class="font-bold">
                                                                   GHS 0/student
                                                               </h4>
                                                               <a class="btn btn-warning btn-sm m-t-xs selectPlan">Select
                                                                   plan</a>
                                                           </div>
                                                       </div>
                                                   </div>
   
                                                   <div class="form-group col-sm-3">
                                                       <div class="hpanel plan-box hgreen subscriptionOption">
                                                           <div class="panel-heading hbuilt text-center">
                                                               <h4 class="font-bold subscriptionPlan">Basic plan</h4>
                                                           </div>
                                                           <div class="panel-body">
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
                                                               <table class="table">
                                                                   <thead>
                                                                   <tr>
                                                                       <td>
                                                                           Features
                                                                       </td>
                                                                   </tr>
                                                                   </thead>
                                                                   <tbody>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Support
                                                                           Every 2 Weeks
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Support by
                                                                           mail
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Website
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-square-o"></i> Support by calls
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-square-o"></i> Customization
                                                                       </td>
                                                                   </tr>
                                                                   </tbody>
                                                               </table>
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
   
                                                               <h4 class="font-bold">
                                                                   GHS 60/student
                                                               </h4>
                                                               <a class="btn btn-success btn-sm m-t-xs selectPlan">Select
                                                                   plan</a>
                                                           </div>
                                                       </div>
                                                   </div>
   
                                                   <div class="form-group col-sm-3">
                                                       <div class="hpanel plan-box hblue subscriptionOption">
                                                           <div class="panel-heading hbuilt text-center">
                                                               <h4 class="font-bold subscriptionPlan">Standard plan</h4>
                                                           </div>
                                                           <div class="panel-body">
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
                                                               <table class="table">
                                                                   <thead>
                                                                   <tr>
                                                                       <td>
                                                                           Features
                                                                       </td>
                                                                   </tr>
                                                                   </thead>
                                                                   <tbody>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Weekly
                                                                           Support
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Support by
                                                                           mail
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Website
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Support by
                                                                           calls
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-square-o"></i> Customization
                                                                       </td>
                                                                   </tr>
                                                                   </tbody>
                                                               </table>
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
   
                                                               <h4 class="font-bold">
                                                                   GHS 150/student
                                                               </h4>
                                                               <a class="btn btn-info btn-sm m-t-xs selectPlan">Select
                                                                   plan</a>
                                                           </div>
                                                       </div>
                                                   </div>
   
                                                   <div class="form-group col-sm-3">
                                                       <div class="hpanel plan-box hred subscriptionOption">
                                                           <div class="panel-heading hbuilt text-center">
                                                               <h4 class="font-bold subscriptionPlan">Professional
                                                                   plan</h4>
                                                           </div>
                                                           <div class="panel-body">
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
                                                               <table class="table">
                                                                   <thead>
                                                                   <tr>
                                                                       <td>
                                                                           Features
                                                                       </td>
                                                                   </tr>
                                                                   </thead>
                                                                   <tbody>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Daily
                                                                           Support
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Support by
                                                                           mail
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Website
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i> Support by
                                                                           calls
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td>
                                                                           <i class="fa fa-check-square-o"></i>
                                                                           Customization
                                                                       </td>
                                                                   </tr>
                                                                   </tbody>
                                                               </table>
                                                               <p class="text-muted">
                                                                   Lorem ipsum dolor sit amet, illum fastidii dissentias
                                                                   quo ne. Sea ne sint animal iisque, nam an soluta
                                                                   sensibus.
                                                               </p>
   
                                                               <h4 class="font-bold">
                                                                   GHS 300/student
                                                               </h4>
                                                               <a class="btn btn-danger btn-sm m-t-xs selectPlan">Select
                                                                   plan</a>
                                                           </div>
                                                       </div>
                                                   </div>
   
   
                                               </div>
                                           </div>
                                       </div>
                                       <div class="text-right m-t-xs">
                                           <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                           <a class="btn btn-default next" href="#" title="dropdown">Next</a>
                                       </div>
   
                                   </div>
   
                                   <div class="p-m tab-pane">
   
                                       <div class="row">
                                           <div class="col-lg-12 text-center">
                                               <i class="pe-7s-credit fa-5x text-muted"></i>
                                               <p class="small m-t-md">
                                                   <strong>Review Information Provided</strong>Double check the information you provided.
                                               </p>
                                           </div>
                                           <div class="col-lg-12">
                                               <div class="row">
                                               </div>
                                           </div>
                                       </div>
                                       
                                       <div class="text-right m-t-xs">
                                           <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                           <a class="btn btn-default next" href="#" title="dropdown">Next</a>
                                       </div>
                                   </div>
   
                                   <div class="tab-pane">
                                       <div class="row text-center m-t-lg m-b-lg">
                                           <div class="col-lg-12">
                                               <i class="pe-7s-check fa-5x text-muted"></i>
                                               <p class="small m-t-md">
                                                   <strong>By submitting this form</strong> ,You are committing to an
                                                   agreement between your company <b><span id="client"></span></b>
                                                   and Astromy LLC <br/>to treate you as a customer who has placed a
                                                   request for our product under the following terms
                                                   <br/><a href="#" title="dropdown">Read Terms</a>
                                               </p>
                                           </div>
                                           <div class="checkbox col-lg-12">
                                               <input type="checkbox" class="i-checks approveCheck" placeholder="approve"
                                                      title="approve" name="approve">
                                               Approve this form
                                           </div>
                                       </div>
                                       <div class="text-right m-t-xs">
                                           <a class="btn btn-default prev" href="#" title="dropdown">Previous</a>
                                           <a class="btn btn-default next" href="#" title="dropdown">Next</a>
                                           <button id="submitRequest" type="submit" class="ladda-button btn btn-success"
                                                   data-style="expand-left"><span class="ladda-label">Submit</span><span
                                                   class="ladda-spinner"></span>
                                               <div class="ladda-progress" style="width: 0px;"></div>
                                           </button>
   
                                           <!--<a class="btn btn-success submitWizard" href="#" title="dropdown">Submit</a>-->
                                       </div>
                                   </div>
                               </div>
                           <!--</form>-->
   
                       </div>
                   </div>
               </div>
   </div>
           </div>`



    document.getElementById("wrapper").innerHTML = institution;

    var script14 = document.createElement("script");
    script14.setAttribute("type", "text/javascript");
    script14.setAttribute("src", "scripts/_preorder.js");
    script14.setAttribute("data-dynamic", "true");
    document.getElementsByTagName("body")[0].appendChild(script14);
});

//document.getElementById("institution").addEventListener("click", institutionBuild);



/*

// Bind normal buttons
$('.ladda-button').ladda('bind', { timeout: 2000 });

// Bind progress buttons and simulate loading progress
Ladda.bind('.progress-demo .ladda-button', {
    callback: function (instance) {
        var progress = 0;
        var interval = setInterval(function () {
            progress = Math.min(progress + Math.random() * 0.1, 1);
            instance.setProgress(progress);

            if (progress === 1) {
                instance.stop();
                clearInterval(interval);
            }
        }, 200);
    }
});


var l = $('.ladda-button-demo').ladda();

l.click(function () {
    // Start loading
    l.ladda('start');

    // Timeout example
    // Do something in backend and then stop ladda
    setTimeout(function () {
        l.ladda('stop');
    }, 12000)


});*/
