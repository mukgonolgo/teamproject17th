function toggleLike(reviewId) {
    // AJAX 요청을 서버로 보내는 로직을 구현합니다.
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    $.ajax({
        url: `/review/${reviewId}/like`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
		success: function(data) {
		    console.log("Success data:", data); // 데이터 확인
		    const likeButton = $("#like-btn-" + reviewId);
		    const likeCountElem = $("#like-count-" + reviewId);

		    if (likeButton.length > 0) {
		        console.log("Like button found:", likeButton);
		    } else {
		        console.error("Like button not found for reviewId:", reviewId);
		    }

		    if (likeCountElem.length > 0) {
		        console.log("Like count element found:", likeCountElem);
		    } else {
		        console.error("Like count element not found for reviewId:", reviewId);
		    }

		    // 아이콘 및 카운트 업데이트
		    if (data.likedByUser) {
		        likeButton.removeClass('fa-regular fa-heart').addClass('fa-solid fa-heart');
		    } else {
		        likeButton.removeClass('fa-solid fa-heart').addClass('fa-regular fa-heart');
		    }
		    
		    likeCountElem.text(data.likeCount);
		

        },
        error: function(error) {
            console.error('Error:', error); // 에러 시 콘솔 출력
        }
    });
}
