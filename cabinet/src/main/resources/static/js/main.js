$(document).ready(function () {
    $(window).scroll(function() {
        if ($(window).scrollTop() > 50) {
            $('header, .navbar-brand.header-logo').addClass('bg-color');
        } else {
            $('header, .navbar-brand.header-logo').removeClass('bg-color');
        }
    });
});