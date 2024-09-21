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