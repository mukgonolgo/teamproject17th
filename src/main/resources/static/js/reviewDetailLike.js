$(document).ready(function() {
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    const likeButton = $("#like-btn");
    const likeCountElem = $("#like-count");

	// 페이지 로드 시 초기 하트 아이콘 설정
	function setHeartIcon() {
	    if (likedByUser) {
	        likeButton.removeClass("fa-regular fa-heart").addClass("fa-solid fa-heart");  // 꽉 찬 하트
	        likeButton.css("color", "#ff2e2e");  // 꽉 찬 하트의 색상 설정
	    } else {
	        likeButton.removeClass("fa-solid fa-heart").addClass("fa-regular fa-heart");  // 빈 하트
	        likeButton.css("color", "#000000");  // 빈 하트일 때 색상 설정 (예: 검정색)
	    }
	}


    // 처음 페이지 로드 시 아이콘 설정
    setHeartIcon();

    // 좋아요 버튼 클릭 시 처리
    likeButton.click(function() {
        $.ajax({
            url: `/review/${reviewId}/like`,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken  // CSRF 토큰 추가
            },
            success: function(data) {
                // 서버 응답에 따라 좋아요 상태 및 하트 아이콘 갱신
                likedByUser = data.likedByUser;
                likeCountElem.text(data.likeCount);
                setHeartIcon();  // 상태에 따라 하트 아이콘 갱신
            },
            error: function(error) {
                console.error('Error:', error);
            }
        });
    });
});