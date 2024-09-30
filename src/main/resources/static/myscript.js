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
document.getElementById('search-input').addEventListener('focus', function () {
    const dropdownMenu = document.getElementById('dropdown-menu');
    dropdownMenu.style.opacity = '1';
    dropdownMenu.style.visibility = 'visible';
    dropdownMenu.style.transform = 'translateY(0)';
});

document.getElementById('search-input').addEventListener('blur', function () {
    const dropdownMenu = document.getElementById('dropdown-menu');
    setTimeout(() => {
        dropdownMenu.style.opacity = '0';
        dropdownMenu.style.visibility = 'hidden';
        dropdownMenu.style.transform = 'translateY(-10px)';
    }, 200);
});

document.getElementById('search-input').addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        addRecentSearch(this.value);
        this.value = ''; 
    }
});

function addRecentSearch(searchTerm) {
    if (searchTerm.trim() === '') return;

    const recentSearches = document.getElementById('recent-searches');

    const newSearchItem = document.createElement('div');
    newSearchItem.className = "search-item";

    const searchText = document.createElement('span');
    searchText.innerText = searchTerm;

    const closeIcon = document.createElement('span');
    closeIcon.className = "close-icon";
    closeIcon.innerHTML = "✖️";

    closeIcon.onclick = function() {
        newSearchItem.remove();
    };

    newSearchItem.appendChild(searchText);
    newSearchItem.appendChild(closeIcon);

    recentSearches.prepend(newSearchItem);
}

function removeItem(element) {
    const parentElement = element.parentElement;
    parentElement.remove(); 
}

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
