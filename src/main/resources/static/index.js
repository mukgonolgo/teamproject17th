// Enable auto-slide with a 3-second interval
var myCarousel = document.querySelector('#carouselExampleCaptions');
var carousel = new bootstrap.Carousel(myCarousel, {
    interval: 3000, // 3 seconds
    ride: 'carousel', // Auto start
    wrap: true // Enable infinite loop
});

// Dynamically generate indicators based on the number of slides
var indicatorsContainer = document.querySelector('.carousel-indicators');
var carouselItems = document.querySelectorAll('.carousel-item');

carouselItems.forEach((item, index) => {
    var button = document.createElement('button');
    button.type = 'button';
    button.setAttribute('data-bs-target', '#carouselExampleCaptions');
    button.setAttribute('data-bs-slide-to', index);
    button.setAttribute('aria-label', 'Slide ' + (index + 1));

    if (index === 0) {
        button.classList.add('active');
        button.setAttribute('aria-current', 'true');
    }

    indicatorsContainer.appendChild(button);
});