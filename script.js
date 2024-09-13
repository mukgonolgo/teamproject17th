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
        this.value = ''; // 입력 필드를 비웁니다.
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
    parentElement.remove(); // 부모 요소를 삭제
}

