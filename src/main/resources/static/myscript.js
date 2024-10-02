const swiper = new Swiper('.swiper-container', {
    loop: true,
    spaceBetween:1,
    slidesPerView: 1, // 기본적으로 3개
    breakpoints: {
        970: {
            slidesPerView: 3, 
        },
        780: {
            slidesPerView: 3, // 태블릿에서 2개
        }
    },
    pagination: {
        el: '.swiper-pagination',
        clickable: true,
    },
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
});

