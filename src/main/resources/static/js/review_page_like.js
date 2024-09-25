$(document).ready(function() {
    // CSRF 토큰 설정
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

	 $(".like_box").each(function() {
	        const reviewContainer = $(this);  // 각 리뷰의 컨테이너
	        const likedByUser = reviewContainer.find(".liked-by-user").val() === 'true';  // likedByUser 상태 확인
	        const likeButton = reviewContainer.find(".like_btn");  // 하트 버튼
			const likeCountElem = reviewContainer.find(".likeCount");  // 좋아요 카운트 표시

	        // likedByUser 값에 따라 하트 아이콘 설정
	        if (likedByUser) {
	            likeButton.removeClass("fa-regular fa-heart").addClass("fa-solid fa-heart");
	            likeButton.css("color", "#ff2e2e");  // 꽉 찬 하트 색상
	        } else {
	            likeButton.removeClass("fa-solid fa-heart").addClass("fa-regular fa-heart");
	            likeButton.css("color", "#000000");  // 빈 하트 색상
	        }
			
			// 좋아요 수가 제대로 표시되는지 콘솔에 출력
			 console.log('좋아요 수:', likeCountElem.text());

			 if (likeCountElem.length && likeCountElem.text() !== "") {
			     console.log('좋아요 수:', likeCountElem.text());
			 } else {
			     console.log('좋아요 수가 없습니다.');
			 }
	    });

	    // 좋아요 버튼 클릭 이벤트
	    $(".tap_btn").click(function() {
	        const reviewContainer = $(this).closest(".d-flex.align-items-center");  // 클릭한 좋아요 버튼의 컨테이너 찾기
	        const reviewId = reviewContainer.find(".review-id").val();  // 리뷰 ID 가져오기
	        let likedByUser = reviewContainer.find(".liked-by-user").val() === 'true';  // 좋아요 상태 확인

	        const likeButton = reviewContainer.find(".like_btn");  // 하트 버튼
	        const likeCountElem = reviewContainer.find("span");  // 좋아요 카운트 표시

	        // 좋아요 상태 전환
	        likedByUser = !likedByUser;

	        // AJAX 요청 보내기
	        $.ajax({
	            url: `/review/${reviewId}/like`,
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json',
	                [csrfHeader]: csrfToken
	            },
	            success: function(data) {
	                // 성공적으로 데이터 수신 후 UI 업데이트
	                likedByUser = data.likedByUser;
	                const likeCount = data.likeCount;

	                // 하트 아이콘 업데이트
	                if (likedByUser) {
	                    likeButton.removeClass("fa-regular fa-heart").addClass("fa-solid fa-heart");
	                    likeButton.css("color", "#ff2e2e");  // 꽉 찬 하트 색상
	                } else {
	                    likeButton.removeClass("fa-solid fa-heart").addClass("fa-regular fa-heart");
	                    likeButton.css("color", "#000000");  // 빈 하트 색상
	                }

	                // 좋아요 수 업데이트
	                likeCountElem.text(likeCount);

	                // likedByUser 상태도 업데이트
	                reviewContainer.find(".liked-by-user").val(likedByUser);
	            },
	            error: function(error) {
	                console.error('Error:', error); // 에러 발생 시 콘솔 출력
	            }
	        });
	    });
	});
	document.addEventListener("DOMContentLoaded", function () {
	    // 각 리뷰마다 별점을 설정하기 위해 반복문 처리
	    document.querySelectorAll(".starRating").forEach(starContainer => {
	        const reviewId = starContainer.getAttribute("data-review-id"); // 각 리뷰 ID를 동적으로 가져옴

	        console.log("Fetching rating for reviewId:", reviewId); // 리뷰 ID 콘솔 출력

	        // 서버에서 별점 데이터를 가져오는 함수
	        function fetchRating(reviewId) {
	            fetch(`/api/review/${reviewId}`) // 리뷰 ID에 맞는 데이터를 가져옴
	                .then(response => {
	                    if (!response.ok) {
	                        throw new Error(`Failed to fetch rating for reviewId: ${reviewId}`);
	                    }
	                    return response.json();
	                })
	                .then(data => {
	                    console.log("Received rating data:", data); // 수신한 데이터 콘솔 출력
	                    // 별점을 렌더링
	                    renderStars(data.rating, starContainer);
	                })
	                .catch(error => console.error("별점 데이터를 불러오는 중 오류가 발생했습니다:", error));
	        }

	        // 별점을 렌더링하는 함수
	        function renderStars(rating, container) {
	            let starHtml = "";
	            const fullStars = Math.floor(rating); // 소수점 제거한 별점 수
	            const halfStar = rating % 1 >= 0.5; // 반 별 처리
	            const totalStars = 5;

	            for (let i = 0; i < fullStars; i++) {
	                starHtml += '<i class="fa-solid fa-star" style="color: #ffdd00;"></i>';
	            }
	            if (halfStar) {
	                starHtml += '<i class="fa-solid fa-star-half" style="color: #ffdd00;"></i>';
	            }
	            for (let i = fullStars + (halfStar ? 1 : 0); i < totalStars; i++) {
	                starHtml += '<i class="fa-solid fa-star" style="color: #e0e0e0;"></i>';
	            }

	            container.innerHTML = starHtml; // 별점 표시
	        }

	        // 리뷰 ID를 기반으로 별점 데이터를 가져옴
	        fetchRating(reviewId);
	    });
	});
