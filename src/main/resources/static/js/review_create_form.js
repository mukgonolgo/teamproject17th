let imageCount = 0;
const maxImages = 5;
const imageFiles = []; // 파일들을 저장할 배열
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

imageFiles.forEach((file, index) => {
    console.log(`파일 ${index}: ${file.name}`);
});

// Tag input handling
let S3_tags_input = $("#S3_tags_input");
let tagGroup = $("#tagGroup");
let tagsArray = []; // 태그를 배열로 저장
let S3_tagsOutput = $("#S3_tagsOutput");

S3_tags_input.keyup(function (key) {
    if (key.keyCode == 13 || key.keyCode == 32) {
        let tag = $(this).val().trim();
        tag = tag.replace(/[^\w\s가-힣ㄱ-ㅎㅏ-ㅣ]/g, '');
        if (tag === "" || tagsArray.includes(tag)) {
            alert("입력된 값이 없거나 중복된 태그입니다.(특수문자는 태그에 포함할 수 없습니다.)");
            return;
        }
        
        tagGroup.append(`<span class="badge badge-info badge-pill d-flex align-items-center p-2 mr-2">${tag}<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-x-circle-fill ml-1" viewBox="0 0 16 16"><path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/></svg></span>`);
        tagsArray.push(tag);
        S3_tagsOutput.val(tagsArray.join(','));

        $(this).val('');
        console.log(tagsArray);

        let tagNum = $("#tagGroup .badge").length;
        if (tagNum >= 5) {
            $(this).css('visibility', 'hidden'); // 태그가 5개일 때 입력 필드를 숨기되, 공간은 유지
        }
    }
});

$(document).on("click", "span.badge", function () {
    let removedTag = $(this).text().trim();
    tagsArray = tagsArray.filter(tag => tag !== removedTag);
    S3_tagsOutput.val(tagsArray.join(','));

    $(this).remove();

    let tagNum = $("#tagGroup .badge").length;
    if (tagNum < 5) {
        S3_tags_input.css('visibility', 'visible'); // 태그가 5개 미만일 때 입력 필드를 다시 표시
    }
});

// 별점 처리
new Vue({
    el: '#app',
    data: {
        selectedRating: 0 // 기본값을 0으로 설정하지만, 서버에서 받은 값으로 덮어씌움
    },
    computed: {
        ratingToPercent() {
            return this.selectedRating * 20; // 별점에 따른 백분율 계산
        }
    },
    methods: {
        setRating(n) {
            this.selectedRating = n;
            const fillElement = document.querySelector('.star-ratings-fill');
            fillElement.style.width = this.ratingToPercent + '%';

            // 별점 텍스트 업데이트
            document.querySelector('.selected-rating span').textContent = this.selectedRating;
        }
    },
    mounted() {
        // 서버에서 전달받은 기존 별점 값을 input 요소에서 가져와 selectedRating에 설정
        const existingRating = parseInt(document.getElementById('rating').value) || 0;
        this.selectedRating = existingRating;

        // 별점 UI와 텍스트 업데이트
        const fillElement = document.querySelector('.star-ratings-fill');
        fillElement.style.width = this.ratingToPercent + '%';
        document.querySelector('.selected-rating span').textContent = this.selectedRating;
    }
});

document.getElementById('reviewForm').addEventListener('submit', function(event) {
    event.preventDefault();

    let formData = new FormData(this);

    // FormData 로그로 확인
    formData.forEach((value, key) => {
        console.log(key + ': ' + value);  // 전송될 데이터를 콘솔에 출력하여 확인
    });

    // 파일 개수 로그 출력
    console.log("파일 개수:", formData.getAll('fileUpload').length);

    imageFiles.forEach((file) => {
        formData.append('fileUpload', file, file.name);
    });

    const tags = $("#S3_tagsOutput").val();
    const rating = document.getElementById('rating').value;

    // 중복 방지를 위해 이미 존재하는 필드는 삭제 후 다시 추가
    if (formData.has('rating')) {
        formData.delete('rating');
    }
    formData.append('rating', rating);

    if (formData.has('tags')) {
        formData.delete('tags');
    }
    formData.append('tags', tags);

    fetch('/review_create', {
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
    .catch((error) => {
        console.error('오류:', error);
    })
    .finally(() => {
        imageFiles.length = 0;
        imageCount = 0;
        document.querySelectorAll('.img-container').forEach(container => container.remove());
        document.getElementById('add-icon').style.display = 'flex';
    });
});
