$(document).ready(function() {
      if (window.matchMedia("(min-width:1200px)").matches) {
          document.querySelectorAll('.responsive').forEach(function (item, i) {
              let b = item.querySelectorAll('.office-card').length
              item.classList.add(`items${b}`)
          })
      }
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
    $('.date').bootstrapMaterialDatePicker({
                    format: 'DD.MM.YYYY',
                    weekStart: 0,
                    time: false
                });
    if (window.matchMedia("(max-width:1200px)").matches) {
        $('.responsive').slick({
            dots: true,
            speed: 1000,
            slidesToShow: 5,
            slidesToScroll: 2,
            infinite: true,
            dots: true,
            responsive: [
              {
                breakpoint: 1650,
                settings: {
                  slidesToShow: 4,
                  slidesToScroll: 2,
                  infinite: true,
                  dots: true
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
});


