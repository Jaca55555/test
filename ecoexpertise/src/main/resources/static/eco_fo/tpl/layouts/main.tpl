<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>{{ title | title }}</title>
  <meta name="viewport" content="width=device-width">
  <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
  <!-- build:css styles/vendor.css -->
  <link rel="stylesheet" href="libs/swiper/swiper.min.css">
  <!-- endbuild -->

  <!-- build:css styles/main.css -->
  <link href="styles/main.css" rel="stylesheet">
  <!-- endbuild -->

</head>

<body>
<div class="main-wrapper">
  <header>
    <div class="header-top-wrapper">
      <div class="container">
        <div class="header-top">
          <div class="header-top-left">
            <div class="contact-number">
                +99871) 207-07-70 (1001
            </div>
            <div class="flex-center media-w-100">
              <ul>
                <li><a href="#"><img src="img/facebook-logo.svg" alt=""></a></li>
                <li><a href="#"><img src="img/twitter-logo.svg" alt=""></a></li>
                <li><a href="#"><img src="img/instagram-logo.svg" alt=""></a></li>
              </ul>
              <div class="lang-wrapper">
                <div class="current-lang">
                  Uzb
                </div>
                <div class="lang-list">
                  <a class="lang-item" href="#">Pyc</a>
                  <a class="lang-item" href="#">Eng</a>
                </div>
              </div>
            </div>
          </div>
          <div class="header-top-right">
              <ul class="header-top-nav-list">
                <li>
                  <a href="#">
                    <span th:text="#{front.one_id}">One ID orqali kirish</span>
                  </a>
                </li>
              <li>
                <a href="#">
                  <span th:text="#{front.eds}">ERI orqali kirish</span>
                </a>
              </li>
            </ul>
              <form action="">
                  <div class="form-block">
                      <input type="text" th:placeholder="#{front.placeholder}">
                      <button></button>
                  </div>
              </form>
          </div>
        </div>
      </div>
    </div>
    <div class="header-bottom-wrapper">
      <div class="container">
        <div class="menu-wrapper">
          <a href="#" class="logo">
            <img src="img/eco-logo.png" alt="">
            <div class="info">
              <span th:text="#{front.logo_info}">O'zbekiston Respublikasi ekologiya va atrof muhitni muhofaza qilish davlat qo'mitasi</span>
            </div>
          </a>
          <ul class="menu-nav">
            <li>
              <a href="#" class="active">
                <span th:text="#{front.menu_home}">Bosh sahifa</span>
              </a>
            </li>
            <li>
              <a href="#">
                <span th:text="#{front.menu_services}">Xizmatlar</span>
              </a>
            </li>
            <li>
              <a href="#">
                <span th:text="#{front.menu_news}">Yangiliklar</span>
              </a>
            </li>
            <li>
              <a href="#">
                <span th:text="#{front.menu_statistic}">Statistika</span>
              </a>
            </li>
            <li>
              <a href="#">
                <span th:text="#{front.menu_connect}">Bog’lanishlar</span>
              </a>
            </li>
          </ul>
          <div class="menu-btn"></div>
        </div>
      </div>
    </div>
  </header>
  {% block content %} {% endblock %}
  <footer>
    <div class="footer-center">
      <div class="container">
        <div class="footer-center-list">
          <div class="footer-center-item">
              <div class="img-block">
                  <img src="img/logo.png" alt="">
              </div>
              <div class="site-title">
                  <span th:text="#{front.footer_logo_title}">O'zbekiston Respublikasi ekologiya va atrof muhitni muhofaza qilish davlat qo'mitasi</span>
              </div>
              <div class="link-site">
                  <a href="http://eco.gov.uz">
                    <span th:text="#{front.footer_logo_info}">Veb saytga o'tish</span>
                  </a>
              </div>
          </div>
          <div class="footer-center-item">
            <div class="title">
              <span th:text="#{front.footer_menu_title}">MENU</span>
            </div>
            <ul class="footer-nav">
              <li>
                <a href="#">
                  <span th:text="#{front.menu_home}"></span>BOSH SAHIFA
                </a>
              </li>
              <li>
                <a href="#">
                  <span th:text="#{front.menu_services}">XIZMATLAR</span>
                </a>
              </li>
              <li>
                <a href="#">
                  <span th:text="#{front.menu_news}">YANGILIKLAR</span>
                </a>
              </li>
              <li>
                <a href="#">
                  <span th:text="#{front.menu_statistic}">STATISTIKA</span>
                </a>
              </li>
              <li>
                <a href="#">
                  <span th:text="#{front.menu_connect}">BOG’LANISH</span>
                </a>
              </li>
            </ul>
          </div>
          <div class="footer-center-item">
            <div class="title">
              <span th:text="#{front.footer_address_title}">“Bog’lanish”</span>
            </div>
            <div class="address icon">
              <div class="address-title">
                <span th:text="#{front.footer_address_subtitle}">Manzil:</span>
              </div>
              <div class="address-subtitle">
                <span th:text="#{front.footer_address}">100047, Toshkent shahar, Yashnоbod tumani, To'y-tepa ko'chasi 2A uy</span>
              </div>
            </div>
            <div class="phone icon">
              <div class="item">
                <span th:text="#{front.footer_phone}">(99871) 207-07-70 (ichki: 1001#),</span>
              </div>
              <div class="item">
                <span th:text="#{front.footer_phone}">(99871) 207-07-70 (ichki: 5015#)</span>
              </div>
            </div>
            <div class="email icon">
              <div class="item">info@uznature.uz</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <div class="container">
        <div class="footer-bottom-wrapper">
          <a href="#" class="logo">
            <img src="img/eco-logo.png" alt="">
            <div class="info">
                <span th:text="#{front.logo_info}">O'zbekiston Respublikasi ekologiya va atrof muhitni muhofaza qilish davlat qo'mitasi</span>
            </div>
          </a>
          <ul class="social-list">
            <li><a href="#"><img src="img/facebook-logo.svg" alt=""></a></li>
            <li><a href="#"><img src="img/instagram-logo.svg" alt=""></a></li>
            <li><a href="#"><img src="img/twitter-logo.svg" alt=""></a></li>
          </ul>
        </div>
      </div>
    </div>
  </footer>
  <div class="scroll-to-top"></div>
</div>

<!-- build:js scripts/jquery-3.3.1.min.js -->
<script src="./libs/Jquery/JQuery.js"></script>
<!-- endbuild -->
<!-- build:js scripts/vendor.min.js -->
<script src="./libs/swiper/swiper.min.js"></script>
<!-- endbuild -->
<!-- build:js scripts/main.js -->
<script src="/scripts/main.js"></script>
<!-- endbuild -->
</body>

</html>