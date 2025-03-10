id=null;
fetchInstitutionBills(instId.split(",")[0]);

    window.copyrights();

var instCode;

    $('.saveBill').click(async function() {

                var jso= postdata();
                return HttpPost("create-bills",jso)
                .then(function(result){
                $('.dismissBill').click();
                 populateBills(result);
                    swal({
                           title: "Thank you!",
                           text: "Your application is being submitted",
                           type: "success"
                      });
                    })
                });

     
    function postdata(){
   let billsMother=[];
    resultlist=[]
    billsMother= document.getElementsByClassName('clonableBill');
        for(var i=0;i<billsMother.length; i++){
        var sel=billsMother[i].getElementsByClassName('newBillCategory')[0]
               var jsonObject={
               "billId":id,
               "bill_Name":billsMother[i].getElementsByClassName('newBillName')[0].value,
               "bill_Amount":billsMother[i].getElementsByClassName('newBillAmount')[0].value,
               "creation_Date":"",
               "bill_Description":billsMother[i].getElementsByClassName('newBillDescription')[0].value,
               "bill_Cat":sel.options[sel.selectedIndex].text,
               "institutionCode":instCode
           }
           resultlist.push(jsonObject);
    }
    return resultlist;
    }

    function buildJson(){

    for(var i=0;i<name.length; i++){
        var jsonObject={
            "id":id,
            "name":name[i],
            "type":type
        };
        resultlist.push(jsonObject);
      }
        return resultlist
    }


  async function fetchInstitutionBills(instId){

    var v= instId.replace(/[\[\]']+/g,'');
    v=v.replace(/\//g, '');
    instCode=v;
    var instRequest={"val":v}
    return  HttpPost("get-bills-by-institution",instRequest)
     .then(function (result) {
     populateBills(result);
  })
  }


  function populateBills(data){
  document.getElementsByClassName("content animate-panel")[0].innerHTML="";
var bar = new Promise((resolve, reject) => {
  data.forEach((d,index, array) =>  {
var n=index%4;
        if(index%4==0){
               window.createNewBillRow(d);
           }else{
               window.addBillItem(d);
           }
         if (index === array.length -1) resolve();
        });
    });
    bar.then(() => {
    addEvents();
        console.log('All done!');
    });
   }


function addEvents(){
// Get all elements with the class 'billContent'
const billContents = document.getElementsByClassName("billContent");

// Loop through each element and add event listener
for (let i = 0; i < billContents.length; i++) {
    billContents[i].addEventListener('click', function(event) {
        // 'this' or 'event.currentTarget' refers to the clicked element
        const clickedElement = this; // Or you can use event.currentTarget

        // Now you can extract the content of the clicked element
        const billName = clickedElement.getElementsByTagName("h3")[0].innerHTML.trim();
        const billAmount = clickedElement.getElementsByTagName("strong")[0].innerHTML.trim();
        const billDescription = clickedElement.getElementsByClassName("billDescription")[0].innerHTML.trim();
        const billCat = clickedElement.getElementsByTagName("strong")[1].innerHTML.trim();

        // Update the input fields
        document.getElementsByClassName("newBillName")[0].value = billName;
        document.getElementsByClassName("newBillName")[0].text = billName;

        document.getElementsByClassName("newBillAmount")[0].value = billAmount;
        document.getElementsByClassName("newBillAmount")[0].text = billAmount;

        document.getElementsByClassName("newBillDescription")[0].value = billDescription;
        document.getElementsByClassName("newBillDescription")[0].text = billDescription;

        setSelection(billCat);
    });
}
}

function setSelection(desiredText){
        // Select the dropdown element
        const selectElement = document.getElementsByClassName("newBillCategory")[0];

        // Loop through the options
        for (let i = 0; i < selectElement.options.length; i++) {
            if (selectElement.options[i].text === desiredText) {
                // Set the selected option
                selectElement.selectedIndex = i;
               // Exit the loop once the option is found and selected
                break;
            }
        }
   }
