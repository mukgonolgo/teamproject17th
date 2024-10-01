let existingImagesArray = []; // 전역에서 기존 이미지 경로를 저장할 배열 초기화
let imagesToDelete = []; // 삭제할 이미지 경로를 저장하는 배열
let imageFiles = []; // 새로 추가된 파일들을 저장할 배열

document.addEventListener("DOMContentLoaded", function() {
    // CSRF 토큰 가져오기
    const csrfTokenElement = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderElement = document.querySelector('meta[name="_csrf_header"]');

    // CSRF 토큰이 제대로 가져와졌는지 확인
    if (csrfTokenElement && csrfHeaderElement) {
        const csrfToken = csrfTokenElement.getAttribute('content');
        const csrfHeader = csrfHeaderElement.getAttribute('content');

        const existingTags = document.getElementById('existingTags')?.value.split(',') || []; // 태그가 없는 경우 빈 배열로 초기화
        const existingImages = document.querySelectorAll('.existing-image img');

        // 기존 이미지 경로를 existingImagesArray에 추가
        existingImages.forEach(img => {
            const imageUrl = img.getAttribute('src');
            existingImagesArray.push(imageUrl);
        });

        // 기존 태그들을 화면에 렌더링
        existingTags.forEach(tag => {
            if (tag.trim() && !tagsArray.includes(tag.trim())) { // 중복 방지
                const tagElement = `<span class="badge badge-info badge-pill d-flex align-items-center p-2 mr-2">${tag.trim()}
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-x-circle-fill ml-1 delete-btn" viewBox="0 0 16 16" style="cursor: pointer;">
                    <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/>
                    </svg></span>`;
                tagGroup.append(tagElement); // 태그 렌더링
                tagsArray.push(tag.trim()); // 배열에 태그 추가
            }
        });

        // 이미지 삭제 기능 추가
        existingImages.forEach(img => {
            img.style.cursor = 'pointer';
            img.addEventListener('click', function() {
                const imgContainer = img.parentElement; // 이미지가 들어 있는 div
                const imageUrl = img.getAttribute('src'); // 이미지 경로 가져오기

                // 삭제할 이미지 경로 배열에 추가하고 existingImagesArray에서 제거
                imagesToDelete.push(imageUrl);
                existingImagesArray = existingImagesArray.filter(img => img !== imageUrl);

                // 서버로 이미지 삭제 요청 보내기
                fetch(`/delete_image_by_url`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken  // CSRF 토큰 포함
                    },
                    body: JSON.stringify({ imageUrl: imageUrl }) // 이미지 경로를 서버로 전송
                })
                .then(response => {
                    if (response.ok) {
                        // 이미지가 성공적으로 삭제되면 화면에서 제거
                        imgContainer.remove();
                        console.log(`이미지 삭제 완료: ${imageUrl}`);
                    } else {
                        console.error(`이미지 삭제 실패: ${response.statusText}`);
                    }
                })
                .catch(error => {
                    console.error(`이미지 삭제 중 오류 발생: ${error}`);
                });
            });
        });
        S3_tagsOutput.val(tagsArray.join(',')); // hidden 필드 업데이트
    } else {
        console.error('CSRF 토큰 또는 헤더가 정의되지 않았습니다.');
    }
});

// 이미지 추가 함수
function addImage(event) {
    const files = Array.from(event.target.files);

    files.forEach(file => {
        // 중복 확인: imageFiles 배열에 동일한 파일이 있으면 추가하지 않음
        if (imageFiles.some(existingFile => existingFile.name === file.name && existingFile.size === file.size)) {
            console.log("이미 추가된 파일입니다:", file.name);
            return;  // 중복된 파일이므로 추가하지 않음
        }

		const reader = new FileReader();
		        reader.onload = function(e) {
		            const imgContainer = document.createElement('div');
		            imgContainer.classList.add('img-container'); // 이미지 컨테이너 클래스 추가

		            const img = document.createElement('img');
		            img.src = e.target.result;
		            img.classList.add('uploaded-image'); // 이미지 클래스 추가
		            img.style.cursor = 'pointer';

		            imgContainer.appendChild(img); // 이미지 컨테이너에 이미지 추가
		            const iconContainer = document.querySelector('.icon-container');
		            iconContainer.appendChild(imgContainer); // icon-container에 imgContainer 추가

		            // 파일을 추가
		            imageFiles.push(file); // 새로 추가된 파일을 저장
		            console.log("파일이 추가되었습니다:", file.name);
		        };
        reader.readAsDataURL(file);
    });
}


// 태그 입력 및 삭제 기능
let S3_tags_input = $("#S3_tags_input");
let tagGroup = $("#tagGroup");
let tagsArray = []; // 태그를 배열로 저장
let S3_tagsOutput = $("#S3_tagsOutput");

S3_tags_input.keyup(function(key) {
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
$(document).on("click", ".badge", function() {
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

    // 서버로 전송할 파일 개수 확인
    console.log("서버로 전송될 파일 개수:", imageFiles.length);

    // 새 이미지 파일 추가 및 로그 출력
    imageFiles.forEach(function(file, index) {
        console.log("Index: " + index);  // 인덱스 출력
        console.log("File: " + file.name);  // 파일 이름 출력
        formData.append('fileUpload', file, file.name); // 새 파일 추가
    });

    // 기존 이미지 경로를 추가하여 서버로 전송
    formData.append('existingImages', existingImagesArray.join(','));  // 서버에서 처리할 상대 경로

    // 태그 데이터를 쉼표로 구분된 문자열로 변환하여 추가
    const tagsString = tagsArray.length > 0 ? tagsArray.join(',') : "";
    formData.append('tags', tagsString); // 태그 데이터를 폼 데이터에 추가

    // 리뷰 ID를 가져오기
    const reviewId = document.getElementById('reviewId').value;

    // CSRF 헤더를 설정하여 fetch 요청
    fetch(`/review_update/${reviewId}`, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(text => {
                console.error('서버 에러:', response.status, response.statusText, text);
                throw new Error('Network response was not ok');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.status === "success") {
            // 성공 시 리다이렉션 처리
            window.location.href = `/review_detail/${reviewId}`;
        } else {
            console.error('서버 응답 오류:', data.message);
        }
    })
    .catch(error => {
        console.log('폼 데이터 전송 오류:', error);
    });
});
