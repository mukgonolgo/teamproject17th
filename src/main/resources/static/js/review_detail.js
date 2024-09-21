document.addEventListener("DOMContentLoaded", function() {
    const reviewIdElement = document.querySelector("#reviewId");
    const commentContentElement = document.querySelector("#commentContent");

    if (!reviewIdElement) {
        console.error("#reviewId 요소를 찾을 수 없습니다.");
        return;
    }
    if (!commentContentElement) {
        console.error("#commentContent 요소를 찾을 수 없습니다.");
        return;
    }

    const reviewId = reviewIdElement.value;

    // 댓글 목록 가져오기
    fetch(`/comments/reviews/${reviewId}`)
        .then(response => {
            console.log("댓글 리스트 응답 상태:", response.status);
            return response.json();
        })
        .then(comments => {
            console.log("서버로부터 받은 댓글 데이터:", comments);
            const commentsList = document.querySelector(".comments-list");
            commentsList.innerHTML = '';  // 기존 댓글 초기화
            
            comments.forEach(comment => {
                // 댓글 HTML 생성
                const commentHtml = `
				<hr />
                    <div class="comment">
                        <img src="${comment.userImage || '/img/user/default-profile.png'}" alt="프로필 사진" class="rounded-circle mt-2 S3_d_profile-img" style="width: 50px;height: 50px;">
                        <div class="comment-content">
                            <span class="font-weight-bold mr-2">${comment.username}</span>
                            <p>${comment.content}</p>
                            <p class="card-text">
                                <small class="text-muted">${new Date(comment.createDate).toLocaleString()}</small>
                                <span type="button" class="btn badge badge-secondary ml-1 reply-button" data-comment-id="${comment.commentId}">답글달기</span>
                            </p>
                        </div>
                    </div>
					   `;
                commentsList.insertAdjacentHTML("beforeend", commentHtml);
            });
        })
        .catch(error => {
            console.error("댓글을 불러오는 중 오류가 발생했습니다.", error);
        });

    // 댓글 작성 이벤트 핸들러
	document.querySelector("#submitComment").addEventListener("click", function(e) {
	    e.preventDefault();

	    const commentContent = commentContentElement.value;

	    if (!commentContent.trim()) {
	        alert("댓글을 입력하세요!");
	        return;
	    }

	    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
	    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

	    fetch(`/comments/reviews/${reviewId}`, {
	        method: "POST",
	        headers: {
	            "Content-Type": "application/x-www-form-urlencoded",
	            [csrfHeader]: csrfToken
	        },
	        body: `content=${encodeURIComponent(commentContent)}`
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error("댓글 작성에 실패했습니다.");
	        }
	        return response.json();
	    })
	    .then(data => {
	        console.log("서버 응답 데이터:", data);

	        // 데이터가 undefined인지 체크하고 기본값을 설정
	        const username = data.username || "알 수 없는 사용자";
	        const userImage = data.userImage || '/img/user/default-profile.png';

	        // 새로운 댓글 HTML 생성 및 삽입
	        const commentsList = document.querySelector(".comments-list");
	        const newCommentHtml = `
			<hr />
	            <div class="comment">
	                <img src="${userImage}" alt="프로필 사진" class="rounded-circle mt-2 S3_d_profile-img" style="width: 60px; height: 60px;">
	                <div class="comment-content">
	                    <span class="font-weight-bold mr-2">${username}</span>
	                    <p>${data.content}</p>
	                    <p class="card-text">
	                        <small class="text-muted">${new Date(data.createDate).toLocaleString()}</small>
	                        <span class="badge badge-secondary ml-1 reply-button" data-comment-id="${data.commentId}">답글달기</span>
	                    </p>
	                </div>
	            </div>
				   `;
	        commentsList.insertAdjacentHTML("beforeend", newCommentHtml);  // 새로운 댓글을 상단에 삽입
	        commentContentElement.value = "";  // 입력 필드 초기화
	    })
	    .catch(error => {
	        alert(error.message);
	        console.error("댓글 작성 중 오류가 발생했습니다:", error);
	    });
	});

});
document.addEventListener("DOMContentLoaded", function() {
    const isAuthenticated = document.getElementById("isAuthenticated").value === 'true';

    const commentContentElement = document.getElementById("commentContent");

    if (!isAuthenticated) {
        // 로그인하지 않은 경우 댓글 입력란을 회색으로 만들고 비활성화
        commentContentElement.style.backgroundColor = "#e0e0e0";  // 회색 배경색
        commentContentElement.placeholder = "로그인 후 댓글을 작성할 수 있습니다.";
        commentContentElement.disabled = true;  // 입력 비활성화
    }
});

/* 공유 모달창 기능 */
document.getElementById('shareBtn').addEventListener('click', function() {
    const modal = document.getElementById('shareModal');
    const shareUrl = window.location.href; // 현재 페이지 URL 가져오기
    document.getElementById('shareUrl').value = shareUrl; // 주소 입력란에 넣기

    // SNS 공유 링크 설정
    document.getElementById('facebookShare').href = `https://www.facebook.com/sharer/sharer.php?u=${shareUrl}`;
    document.getElementById('twitterShare').href = `https://twitter.com/intent/tweet?url=${shareUrl}&text=Check this out!`;

    modal.style.display = 'block'; // 모달창 보이기
});

// 모달창 닫기 버튼 기능
document.querySelector('.closeBtn').addEventListener('click', function() {
    document.getElementById('shareModal').style.display = 'none';
});

// 주소 복사 기능
document.getElementById('copyBtn').addEventListener('click', function() {
    const copyText = document.getElementById('shareUrl');
    copyText.select();
    document.execCommand('copy'); // 주소 복사
    alert('주소가 복사되었습니다!');
});
