const swiper1 = new Swiper('#swiper1', {
        slidesPerView: 2,
        spaceBetween: 10,
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
        scrollbar: {
            el: '.swiper-scrollbar',
            hide: false,
            draggable: true,
        },
        breakpoints: {
            992: {
                slidesPerView: 6,
            },
            768: {
                slidesPerView: 5,
            },
            576: {
                slidesPerView:4,
            },
            400: {
                slidesPerView:3,
            },
        },
        on: {
            slideChange: function () {
                updateNavigationButtons(this);
            },
            scrollbarDragMove: function () {
                updateNavigationButtons(this);
            },
        },           
    });

    const swiper2 = new Swiper('#swiper2', {
        slidesPerView: 2,
        spaceBetween: 10,
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
        scrollbar: {
            el: '.swiper-scrollbar',
            hide: false,
            draggable: true,
        },
        breakpoints: {
            992: {
                slidesPerView: 6,
            },
            768: {
                slidesPerView: 5,
            },
            576: {
                slidesPerView:4,
            },
            400: {
                slidesPerView:3,
            },
        },
        on: {
            slideChange: function () {
                updateNavigationButtons(this);
            },
            scrollbarDragMove: function () {
                updateNavigationButtons(this);
            },
        },
    });

    function updateNavigationButtons(swiper) {
        const nextButton = swiper.navigation.nextEl;
        const prevButton = swiper.navigation.prevEl;

        if (swiper.isEnd) {
            nextButton.style.display = 'none'; // 마지막 슬라이드에서 '다음' 버튼 숨기기
        } else {
            nextButton.style.display = 'block'; // 그 외에는 '다음' 버튼 보이기
        }

        if (swiper.isBeginning) {
            prevButton.style.display = 'none'; // 첫 슬라이드에서 '이전' 버튼 숨기기
        } else {
            prevButton.style.display = 'block'; // 그 외에는 '이전' 버튼 보이기
        }
}
