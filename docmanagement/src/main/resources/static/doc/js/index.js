$(document).ready(function() {
    if (window.matchMedia("(min-width:1200px)").matches) {
        document.querySelectorAll('.responsive').forEach(function (item, i) {
            let b = item.querySelectorAll('.office-card').length
            item.classList.add(`items${b}`)
        })
    }
      check()
      function check() {
          if (window.matchMedia("(max-width:1200px)").matches) {
              $('.responsive').slick({
                  dots: false,
                  speed: 1000,
                  slidesToShow: 5,
                  slidesToScroll: 2,
                  infinite: true,
                  responsive: [
                      {
                          breakpoint: 1650,
                          settings: {
                              slidesToShow: 4,
                              slidesToScroll: 2,
                              infinite: true,
                              dots: false
                          }
                      },
                      {
                          breakpoint: 1200,
                          settings: {
                              slidesToShow: 4,
                              slidesToScroll: 2
                          }
                      },
                      {
                          breakpoint: 900,
                          settings: {
                              slidesToShow: 3,
                              slidesToScroll: 1
                          }
                      },
                      {
                          breakpoint: 700,
                          settings: {
                              slidesToShow: 2,
                              slidesToScroll: 1
                          }
                      },
                      {
                          breakpoint: 550,
                          settings: {
                              slidesToShow: 1,
                              slidesToScroll: 1
                          }
                      }
                      // You can unslick at a given breakpoint now by adding:
                      // settings: "unslick"
                      // instead of a settings object
                  ]
              });
          }
      }
    window.onresize = check;
    $('.select2InsideModal').select2({
      dropdownParent: $(".modal")
    });
    $('.singleSelect').select2({
        placeholder: "Танланг",
        allowClear: true
    });
    $(".multiselect").select2({
      maximumSelectionLength: 3
      });
    $('.date').datepicker({
        format: "dd.mm.yyyy",
        todayHighlight: true,
        autoclose: true
                });
    // $('.date').bootstrapMaterialDatePicker({
    //     weekStart: 0,
    //     time: false
    // });
});


function switchLanguage(targetLanguage) {
    var currentUrl = window.location.href;

    var urlAndHash = currentUrl.split('#');

    currentUrl = urlAndHash[0];

    currentUrl = currentUrl.replace(/[&]?lang=(uz|oz|ru|en|uzc)/ig, '');

    if (currentUrl.indexOf('?') > 0) {
        currentUrl += '&lang='+targetLanguage;
    } else {
        currentUrl += '?lang='+targetLanguage;
    }
    if (urlAndHash.length > 1) currentUrl += '#' + urlAndHash[1];

    window.location.href = currentUrl;
}

function fileNameSubString(fileName) {
    var lastIndex = fileName.lastIndexOf('.');
    var subStringName = fileName.substring(0,lastIndex);
    var getExtension = fileName.substring(lastIndex,fileName.length);
    if (subStringName.length>10){
        subStringName = subStringName.substring(0,9)+'..';
    }
    subStringName += getExtension;
    return subStringName;
}

function docFileName(files) {
    if (files!=null && files.length>0){
        $.each(files,function (item, value) {
            $('#file_name_'+value.id).html('<span>'+fileNameSubString(value.name)+'</span>');
        })
    }
}

function printThisDiv(id) {
    try {
        $("#"+id).printThis({
            debug: true,               // show the iframe for debugging
            importCSS: true,            // import parent page css
            importStyle: true,         // import style tags
            printContainer: false,       // print outer container/$.selector
            loadCSS: [
                "/static/doc/assets/plugins/bootstrap/css/bootstrap.css",
                "/static/doc/css/my_style.css",
                "/static/doc/assets/css/style.css"
                    ],            // path to additional css file - use an array [] for multiple
            pageTitle: "",              // add title to print page
            removeInline: false,        // remove inline styles from print elements
            removeInlineSelector: "*",  // custom selectors to filter inline styles. removeInline must be true
            printDelay: 333,            // variable print delay
            header: null,               // prefix to html
            footer: null,               // postfix to html
            base: false,                // preserve the BASE tag or accept a string for the URL
            formValues: true,           // preserve input/form values
            canvas: true,              // copy canvas content
            doctypeString: '',       // enter a different doctype for older markup
            removeScripts: true,       // remove script tags from print content
            copyTagClasses: false,      // copy classes from the html & body tag
            beforePrintEvent: null,     // function for printEvent in iframe
            beforePrint: null,          // function called before iframe is filled
            afterPrint: null            // function called before iframe is removed
        });
    }catch (e) {

    }

}