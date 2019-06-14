var formatPrice = function(number, precision, decimalSeparator, thousandsSeparator){
    if(! /^([1-9][0-9]*)|(0)[.]?[0-9]*$/.test(number)) return '';
    var c = isNaN(precision = Math.abs(precision)) ? 2 : precision,
        d = decimalSeparator == undefined ? "." : decimalSeparator,
        t = thousandsSeparator == undefined ? "," : thousandsSeparator,
        s = number < 0 ? "-" : "",
        i = parseFloat(number = Math.abs(+number || 0).toFixed(precision));
    var parts = number.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, thousandsSeparator);

    return parts.join(".");
};

function bindSubRegionSelectToRegionSelect(subRegionSelector, regionSelector) {
    $(regionSelector).change(function(){
        var regionId = $(this).val();
        var srSelector = $(subRegionSelector);
        srSelector.find('option').hide();
        srSelector.find('option:first').show();

        if(regionId != '' && regionId != null) {
            srSelector.find('.sr-' + regionId).show();
        }else{
            srSelector.find('option:first').prop("selected", true);
            srSelector.change();
        }

        var selected = srSelector.find('option:selected');
        if(
            selected.length>0 &&
            selected.attr('class') != undefined &&
            selected.attr('class').length>3 &&
            (selected.attr('class').substr(3)!=regionId)
        ) {
            selected.prop("selected", false);
            srSelector.find('option:first').prop("selected", true);
            srSelector.change();
        }
        //srSelector.val('');
    });
    $(regionSelector).change();
}

function showAndHide(countryId,address,subRegion,region,addressStreetShow,applicantAddressStreet,applicantAddressStreetTex) {
        var countryIdn = $(countryId).val();
        var addres_show = $(address);
        var subRegions = $(subRegion);
        var regions = $(region);
        var addressStreetShowId = $(addressStreetShow);
        var applicantAddressStreetId = $(applicantAddressStreet);
        var applicantAddressStreetTexId = $(applicantAddressStreetTex);
        showOrHide(countryIdn,addres_show,subRegions,regions,addressStreetShowId,applicantAddressStreetId,applicantAddressStreetTexId);
    $(countryId).change(function () {
        var countryId = $(this).val();
        showOrHide(countryId,addres_show,subRegions,regions,addressStreetShowId,applicantAddressStreetId,applicantAddressStreetTexId);
    });
}

function showOrHide(countryId,addres_show,subRegions,regions,addressStreetShow,applicantAddressStreet,applicantAddressStreetTex) {
    if(countryId==860){
        addres_show.show();
        addressStreetShow.hide();
        subRegions.prop('required',true);
        regions.prop('required',true);
        applicantAddressStreet.prop('required',true);
        applicantAddressStreetTex.prop('required',false);
    }else{
        addres_show.hide();
        addressStreetShow.show();
        subRegions.prop('required',false);
        regions.prop('required',false);
        applicantAddressStreet.prop('required',false);
        applicantAddressStreetTex.prop('required',true);
    }

}
function bindPriceHelper(selector) {
    selector = $(selector);
    if(selector.length==0) return;
    var id = selector.attr('id');
    selector.after('<span class="miskin-price-helper" id="miskin-price-helper-'+id+'"></span>');
    selector.keyup(function(){
        selector.siblings('.miskin-price-helper').html(formatPrice($(this).val(), 2, '.', ' '));
    });
    selector.keyup();
}

function mahallaListGetSubRegionId(subRegionId,mahallaId) {
    subregionValNotIsEmpty(subRegionId,mahallaId);
    $(subRegionId).change(function(){
        subregionValNotIsEmpty(subRegionId,mahallaId);
    });
}

function subregionValNotIsEmpty(subRegionId,mahallaId) {
    var url = '/mgmt/mahalla/by_sub_region';
    var subregionVal = $(subRegionId).val();
    var srSelector = $(mahallaId);
    var mahallaVal=$(mahallaId).val();
    srSelector.find('option').hide();
    srSelector.find('option:first').show();
    if (subregionVal!="" && subregionVal!=null){
        var html = '';
        $.ajax({
            url: url,
            data: {
                sub_region_id: subregionVal,
                _csrf: $('#global_csrf').val()
            },
            type: 'post',
            dataType: 'json',
            cache: true,
            success: function (response) {
                $.each(response, function (index, value) {
                    html += '<option value="' + value.id + '"' ;
                    if ((mahallaVal!='' || mahallaVal!=null) && (mahallaVal==value.id)){
                        html+='selected="selected"';
                    }
                    html+='>' + value.title + '</option>'
                });
                $(mahallaId).find(':first-child').after(html);

            },
            error: function () {
            }
        });
    }
}