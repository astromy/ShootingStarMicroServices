type = $('[name="type"]').val();
name = [];
resultlist = [];
id = null;
var currentLookupId = null;

fetchInstitution(instId.split(",")[0]);
//fetchInstitution(instId);

window.copyrights();

$(document).on("click", ".deleteClassGroup", async function () {
    if (!currentLookupId) {
        return;
    }
    var idToDelete = currentLookupId;
    swal({
        title: "Delete this entry?",
        text: "This can't be undone.",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "Delete",
        cancelButtonText: "Cancel",
    }, function (isConfirm) {
        if (!isConfirm) {
            return;
        }
        $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
        fetchPost("deleteLookUp", {val: String(idToDelete)}).then(function () {
            $("#example1").DataTable().destroy();
            $(".dismissClassGroup").click();
            fetchInstitution(instId.split(",")[0]);
            $('.splash').css('display', 'none');
            swal({
                title: "Deleted",
                text: "Entry removed successfully",
                type: "success",
            });
        }).catch(function (err) {
            $('.splash').css('display', 'none');
            swal({
                title: "Could not delete",
                text: err.message || "Something went wrong.",
                type: "error",
            });
        });
    });
});

$(".saveClassGroup").click(async function () {
    $('.splash').css({'display': 'block', 'background': '#ffffff3d'}).find('h1, p').remove();
    //postdata();
    var jso = postdata();
    return fetchPost("addLookUps", jso).then(function (result) {
        $("#example1").DataTable().destroy();
        $(".dismissClassGroup").click();
        populateTable(result);
        $('.splash').css('display', 'none')
        swal({
            title: "Thank you!",
            text: "Data Saved Successfully",
            type: "success",
        });
    });
});

function postdata() {
    resultlist = [];
    var module = $(".newClassGrouptxt");
    const classGroup = document.getElementsByClassName("newClassGrouptxt");
    for (let i = 0; i < classGroup.length; i++) {
        //name[i]=classGroup[i].value;
        var jsonObject = {
            id: $('.newClassGrouptxt').attr('id'),
            name: classGroup[i].value,
            type: type,
        };
        resultlist.push(jsonObject);
    }
    return resultlist;
}

/* function buildJson(){
    resultlist=[];
    for(var i=0;i<name.length; i++){
        var jsonObject={
            "id":id,
            "name":name[i],
            "type":type
        };
        resultlist.push(jsonObject);
      }
        return resultlist
    }*/

async function fetchInstitution(instId) {
    var v = instId.replace(/[\[\]']+/g, "");
    v = v.replace(/\//g, "");
    var instRequest = {val: type};
    return fetchPost("getLookUpByType", instRequest).then(function (result) {
        populateTable(result);
    });
}

function populateTable(data) {
    $("#classGroupTable").empty();
    data.forEach(function (d) {
        var details =
            "<tr id=" +
            d.id +
            "> <td hidden>" +
            d.id +
            " </td> <td> " +
            d.name +
            "</td> </tr>";
        $("#classGroupTable").append(details);

        var existing = $("#" + d.id);

        existing
            .attr({
                "data-toggle": "modal",
                "data-target": "#myclassGroupModal",
                style: "cursor: pointer",
            })
            .on("click", function () {
                var recordIndex = $(this).data("index");

                modalopn();
                $(".newClassGrouptxt").val(d.name);
                $('.newClassGrouptxt').attr('id', d.id);
                $(".modalbody").eq(1).empty();
                currentLookupId = d.id;
                window.currentLookupId = d.id;
                $(".deleteClassGroup").show();
            });
    });

    $(function () {
        // Initialize Example 1
        $("#example1").dataTable({
            dom: "<'row'<'col-sm-4'l><'col-sm-4 text-center'B><'col-sm-4'f>>tp",
            lengthMenu: [
                [10, 25, 50, -1],
                [10, 25, 50, "All"],
            ],
            buttons: [
                {extend: "copy", className: "btn-sm"},
                {extend: "csv", title: "Lookup List", className: "btn-sm"},
                {extend: "pdf", title: "Lookup List", className: "btn-sm"},
                {extend: "print", className: "btn-sm"},
            ],
        });
    });
}
