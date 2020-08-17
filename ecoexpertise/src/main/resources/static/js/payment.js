
$(document).ready(function () {
    $(".tel").mask('(99) 999-99-99');
    $("#cardNumber").mask('9999 9999 9999 9999');
});

let res;
$( "form:first" ).submit(function(e) {
    console.log("keldi");
    let form = $(this);
    console.log("keldi1111");
    console.log("action", $('#payment').attr('action'));
    console.log("action", form.attr('action'));

    if(res!=2){
        console.log("if res!=2");
        e.preventDefault();
    }
    $("#loader_box").css("display", "block");
    $.ajax( {
        url: form.attr( 'action' ),
        data: form.serialize(),
        success: function( response ) {
            console.log("22" + response);

            console.log(response);
            $("#loader_box").css("display", "none");

            $('form:first').attr('action', response.action_url);
            $("#message").text(response.message);
            $("#message1").text(response.message);
            console.log("response.message" + response.message);
            if(response.message!=''){

                $("#message").css('display',"block");
                $("#message1").css('display',"block");
                // $("#message").prop('style',"");
                $("#message1").prop('style',"");
            }else{
                $("#message").css('display',"none");
                $("#message1").css('display',"none");
            }

            if(response.trId>0){
                console.log("response.trId");

                $("form:first :input").prop("readonly", true);
                $("select").attr("readonly", true);
                $("input[name='trId']").val(response.trId);
                $("input[name='paymentId']").val(response.paymentId);
                $('#smsConfirmCode').remove();
                $("#trId").after("<div class='form-group row' id='smsConfirmCode'>\n" +
                    "            <div class='col-md-3 text-right'>\n" +
                    "                <label class='col-form-label' >"+confirmSms+ "</label>\n" +
                    "                <span class='required'>*</span>\n" +
                    "            </div>\n" +
                    "            <div class='col-md-6'>\n" +
                    "                <div class='input-group mb-3'>\n" +
                    "                    <input type='text' id='confirmSms' name='confirmSms' required='' class='form-control'>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </div>" )
            }

            if(response.res>1){
                console.log("response.res>1");

                res=2;
                $("form:first").submit();
            }
        },error: function (xhr, ajaxOptions, thrownError) {
            $("#loader_box").css("display", "none");
        }
    } );
});

