$(document).ready(function(){new Swiper(".top-slider",{navigation:{nextEl:".top-slider-button-next",prevEl:".top-slider-button-prev"},autoplay:{delay:5e3}}),new Swiper(".latest-articles-slider",{slidesPerView:3,spaceBetween:30,navigation:{nextEl:".latest-articles-button-next",prevEl:".latest-articles-button-prev"},autoplay:{delay:5e3},breakpoints:{575:{slidesPerView:1,spaceBetween:10},767:{slidesPerView:2,spaceBetween:20},768:{slidesPerView:3,spaceBetween:30}}}),new Swiper(".customers-slider",{slidesPerView:6,spaceBetween:30,autoplay:{delay:5e3},breakpoints:{400:{slidesPerView:1,spaceBetween:10},575:{slidesPerView:2,spaceBetween:10},767:{slidesPerView:3,spaceBetween:20},992:{slidesPerView:4,spaceBetween:30}}});$(".file-input-wrapper label").click(function(){$(this).addClass("done")}),$("ul.tabs li").click(function(){var e=$(this).attr("data-tab");$("ul.tabs li").removeClass("current"),$(".tab-content").removeClass("current"),$(this).addClass("current"),$("#"+e).addClass("current")}),$(".menu-btn").click(function(){$("header .menu-nav").toggleClass("show")}),$(".current-lang").click(function(){$(".lang-list").toggleClass("show"),$(document).bind("click.wrapper",function(e){0==$(e.target).closest(".current-lang").length&&($(".lang-list").removeClass("show"),$(document).unbind("click.wrapper"))})}),$(window).scroll(function(){50<=$(this).scrollTop()?$(".scroll-to-top").fadeIn(200):$(".scroll-to-top").fadeOut(200)}),$(".scroll-to-top").click(function(){$("body,html").animate({scrollTop:0},500)})});