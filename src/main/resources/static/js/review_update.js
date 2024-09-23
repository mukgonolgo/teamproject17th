let imageCount = 0;
const maxImages = 5;
const imageFiles = []; // 파일들을 저장할 배열

// 이미지 추가 함수
function addImage(event) {
    const files = Array.from(event.target.files);

    if (imageCount + files.length > maxImages) {
        alert("이미지는 최대 5개까지 추가할 수 있습니다.");
        return;
    }

    files.forEach(file => {
        if (imageCount >= maxImages) {
            alert("이미지는 최대 5개까지 추가할 수 있습니다.");
            return;
        }

        const reader = new FileReader();
        reader.onload = function (e) {
            const imgContainer = document.createElement('div');
            imgContainer.classList.add('img-container');

            const img = document.createElement('img');
            img.src = e.target.result;
            img.classList.add('uploaded-image');
            img.style.cursor = 'pointer';

            // 이미지 클릭 시 삭제 기능
            img.addEventListener('click', function () {
                imgContainer.remove();
                imageCount--;
                const index = imageFiles.findIndex(f => f.name === file.name && f.size === file.size);
                if (index > -1) {
                    imageFiles.splice(index, 1); // 배열에서 파일 제거
                }

                if (imageCount < maxImages) {
                    document.getElementById('add-icon').style.display = 'flex';
                }
            });

            imgContainer.appendChild(img);

            const iconContainer = document.querySelector('.icon-container');
            iconContainer.appendChild(imgContainer);

            imageCount++;
            imageFiles.push(file); // 배열에 파일 추가

            if (imageCount >= maxImages) {
                document.getElementById('add-icon').style.display = 'none';
            }

            console.log("현재 이미지 파일 배열:", imageFiles.map(file => file.name));
        };

        reader.readAsDataURL(file);
    });
}

// 태그 입력 및 삭제 기능
let S3_tags_input = $("#S3_tags_input");
let tagGroup = $("#tagGroup");
let tagsArray = []; // 태그를 배열로 저장
let S3_tagsOutput = $("#S3_tagsOutput");

S3_tags_input.keyup(function (key) {
    if (key.keyCode == 13 || key.keyCode == 32) { // Enter 또는 공백 입력 시
        let tag = $(this).val().trim();
        tag = tag.replace(/[^\w\s가-힣ㄱ-ㅎㅏ-ㅣ]/g, ''); // 특수문자 제거
        if (tag === "" || tagsArray.includes(tag)) {
            alert("입력된 값이 없거나 중복된 태그입니다.(특수문자는 태그에 포함할 수 없습니다.)");
            return;
        }

        // 태그 추가
        tagGroup.append(`<span class="badge badge-info badge-pill d-flex align-items-center p-2 mr-2">${tag}<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-x-circle-fill ml-1" viewBox="0 0 16 16"><path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/></svg></span>`);
        tagsArray.push(tag);
        S3_tagsOutput.val(tagsArray.join(',')); // 태그 값을 hidden 필드에 저장

        $(this).val('');
        console.log(tagsArray);

        let tagNum = $("#tagGroup .badge").length;
        if (tagNum >= 5) {
            $(this).css('visibility', 'hidden'); // 태그가 5개일 때 입력 필드를 숨김
        }
    }
});

// 태그 클릭 시 삭제 기능
// 태그 입력 및 삭제 기능
S3_tags_input.keyup(function (key) {
    if (key.keyCode == 13 || key.keyCode == 32) { // Enter 또는 공백 입력 시
        let tag = $(this).val().trim();
        tag = tag.replace(/[^\w\s가-힣ㄱ-ㅎㅏ-ㅣ]/g, ''); // 특수문자 제거
        if (tag === "" || tagsArray.includes(tag)) {
            alert("입력된 값이 없거나 중복된 태그입니다.");
            return;
        }

        // 태그 추가
        tagGroup.append(`<span class="badge badge-info badge-pill d-flex align-items-center p-2 mr-2">${tag}<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-x-circle-fill ml-1" viewBox="0 0 16 16"><path d="M16 8A8 8 0 1 1 0 8a 8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/></svg></span>`);
        tagsArray.push(tag);
        S3_tagsOutput.val(tagsArray.join(',')); // 태그 값을 hidden 필드에 저장

        $(this).val('');
    }
});

// 태그 클릭 시 삭제
$(document).on("click", "span.badge", function () {
    let removedTag = $(this).text().trim();
    tagsArray = tagsArray.filter(tag => tag !== removedTag);
    S3_tagsOutput.val(tagsArray.join(','));
    $(this).remove();
});


// 별점 처리 기능
new Vue({
    el: '#app',
    data: {
        selectedRating: 0 // 기본값을 0으로 설정
    },
    computed: {
        ratingToPercent() {
            return this.selectedRating * 20; // 별점에 따른 백분율 계산
        }
    },
    methods: {
        setRating(n) {
            this.selectedRating = n;
            document.getElementById('rating').value = n; // 별점 값을 hidden 필드에 저장
            const fillElement = document.querySelector('.star-ratings-fill');
            fillElement.style.width = this.ratingToPercent + '%';
            document.querySelector('.selected-rating').textContent = `별점 : ${this.selectedRating}점`;
        }
    },
    mounted() {
        const existingRating = parseInt(document.getElementById('rating').value) || 0;
        this.selectedRating = existingRating;

        const fillElement = document.querySelector('.star-ratings-fill');
        fillElement.style.width = this.ratingToPercent + '%';
        document.querySelector('.selected-rating').textContent = `별점 : ${this.selectedRating}점`;
    }
});


// 폼 제출 처리
document.getElementById('reviewForm').addEventListener('submit', function(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    let formData = new FormData(this);

    imageFiles.forEach((file) => {
        formData.append('fileUpload', file, file.name); // 파일 추가
    });

    const tags = $("#S3_tagsOutput").val();
    const rating = document.getElementById('rating').value;

    formData.append('tags', tags);   // 태그 추가
    formData.append('rating', rating); // 별점 추가

    fetch('/review_update', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok ' + response.statusText);
        }
        return response.json();
    })
    .then(data => {
        console.log('성공:', data);
        window.location.href = `/review_detail/${data.reviewId}`;
    })
    .catch(error => {
		console.log([...formData]);
        console.error('오류:', error);
    
    })
    .finally(() => {
        imageFiles.length = 0;
        imageCount = 0;
        document.querySelectorAll('.img-container').forEach(container => container.remove());
        document.getElementById('add-icon').style.display = 'flex';
    });
});
