const swiper = new Swiper('.swiper-container', {
    loop: true,
    spaceBetween:1,
    slidesPerView: 1, // 기본적으로 3개
    breakpoints: {
        992: {
            slidesPerView: 3, 
        },
		768: {
			slidesPerView: 2, 
		},

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

