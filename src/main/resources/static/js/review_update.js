document.addEventListener("DOMContentLoaded", function() {
    // 서버에서 받은 태그 문자열을 쉼표로 구분된 배열로 변환
    const existingTags = document.getElementById('existingTags').value.split(',');

    // 기존 태그들을 화면에 렌더링
    existingTags.forEach(tag => {
        if (tag.trim() && !tagsArray.includes(tag.trim())) { // 중복 방지
            // 태그에 삭제 버튼을 추가해서 렌더링
            const tagElement = `<span class="badge badge-info badge-pill d-flex align-items-center p-2 mr-2">${tag.trim()}
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-x-circle-fill ml-1 delete-btn" viewBox="0 0 16 16" style="cursor: pointer;">
                <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/>
                </svg></span>`;
            
            tagGroup.append(tagElement); // 태그 렌더링
            tagsArray.push(tag.trim()); // 배열에 태그 추가
        }
    });

    S3_tagsOutput.val(tagsArray.join(',')); // hidden 필드 업데이트

    // 삭제 버튼에 이벤트 추가
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const tagElement = this.parentElement;
            const removedTag = tagElement.textContent.trim();
            tagsArray = tagsArray.filter(tag => tag !== removedTag); // 배열에서 해당 태그 제거
            S3_tagsOutput.val(tagsArray.join(',')); // hidden 필드 업데이트
            tagElement.remove(); // 화면에서 태그 제거
        });
    });
});

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
			    imgContainer.remove(); // 이미지를 화면에서 제거
			    imageCount--;

			    // 파일을 배열에서 찾아 제거 (참조를 직접 비교)
			    const index = imageFiles.indexOf(file); 
			    if (index > -1) {
			        imageFiles.splice(index, 1); // 배열에서 파일 제거
			    }

			    // 이미지가 5개 미만일 때 파일 업로드 버튼을 다시 보여줌
			    if (imageCount < maxImages) {
			        document.getElementById('add-icon').style.display = 'flex';
			    }

			    console.log("현재 이미지 파일 배열:", imageFiles.map(file => file.name));
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
        const newTagElement = `<span class="badge badge-info badge-pill d-flex align-items-center p-2 mr-2">${tag}
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-x-circle-fill ml-1 delete-btn" viewBox="0 0 16 16" style="cursor: pointer;">
            <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/>
            </svg></span>`;
        
        tagGroup.append(newTagElement); // 태그 렌더링

        tagsArray.push(tag);
        S3_tagsOutput.val(tagsArray.join(',')); // 태그 값을 hidden 필드에 저장

        $(this).val(''); // 입력 필드 초기화

        // 삭제 버튼에 이벤트 추가
        document.querySelectorAll('.delete-btn').forEach(button => {
            button.addEventListener('click', function() {
                const tagElement = this.parentElement;
                const removedTag = tagElement.textContent.trim();
                tagsArray = tagsArray.filter(tag => tag !== removedTag); // 배열에서 해당 태그 제거
                S3_tagsOutput.val(tagsArray.join(',')); // hidden 필드 업데이트
                tagElement.remove(); // 화면에서 태그 제거
            });
        });

        // 태그가 5개일 때 입력 필드를 숨김
        if (tagsArray.length >= 5) {
            $(this).css('visibility', 'hidden');
        }
    }
});

// 태그 클릭 시 삭제 (중복 방지를 위해 이벤트 위임 사용)
$(document).on("click", ".badge", function () {
    let removedTag = $(this).contents().get(0).nodeValue.trim(); // 태그 텍스트만 추출
    tagsArray = tagsArray.filter(tag => tag !== removedTag); // 배열에서 해당 태그 제거
    S3_tagsOutput.val(tagsArray.join(',')); // hidden 필드 업데이트
    $(this).remove(); // 화면에서 태그 제거

    // 태그가 5개 미만일 때 입력 필드를 다시 보임
    if (tagsArray.length < 5) {
        S3_tags_input.css('visibility', 'visible');
    }
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

    // 이미지 파일 추가
    imageFiles.forEach((file) => {
        formData.append('fileUpload', file, file.name); // 파일 추가
    });

    // 태그 데이터를 쉼표로 구분된 문자열로 변환하여 추가
    const tagsString = tagsArray.join(','); // 태그 배열을 쉼표로 구분된 문자열로 변환
    formData.append('tags', tagsString); // 태그 데이터를 폼 데이터에 추가

    const rating = document.getElementById('rating').value;
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
    });
});
