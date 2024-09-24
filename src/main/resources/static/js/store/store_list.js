document.addEventListener("DOMContentLoaded", function() {
    const carousels = document.querySelectorAll('.carousel');

    carousels.forEach(carousel => {
        const carouselItems = carousel.querySelectorAll('.carousel-item');
        const prevButton = carousel.querySelector('.carousel-control-prev');
        const nextButton = carousel.querySelector('.carousel-control-next');

        if (carouselItems.length <= 1) {
            carousel.classList.remove('carousel'); // 캐로셀 클래스를 제거
            prevButton.style.display = 'none';
            nextButton.style.display = 'none';
        }
    });
	const button = document.querySelectorAll('.heart');

	button.addEventListener('click', () => {
	    button.classList.toggle('active');
	});
});