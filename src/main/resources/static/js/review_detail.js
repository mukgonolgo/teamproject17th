
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

    // 페이지 로드 시 서버로부터 댓글 리스트를 불러옴
    fetch(`/comments/reviews/${reviewId}`)
        .then(response => response.json())
        .then(comments => {
            const commentsList = document.querySelector(".comments-list");
            commentsList.innerHTML = '';  // 기존 댓글 초기화

            comments.forEach(comment => {
                const commentHtml = `
                    <div class="comment">
                        <img src="${comment.userImageUrl || '/img/user/default-profile.png'}" alt="프로필 사진" class="rounded-circle mt-3 S3_d_profile-img" style="width: 50px;">
                        <div class="comment-content">
                            <span class="font-weight-bold mr-2">${comment.username}</span>
                            <p>${comment.content}</p>
                            <p class="card-text">
                                <small class="text-muted">${new Date(comment.createDate).toLocaleString()}</small>
                                <span type="button" class="btn badge badge-secondary ml-1 reply-button" data-comment-id="${comment.commentId}">답글달기</span>
                            </p>
                        </div>
                    </div>`;
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
			console.log("서버 응답 데이터:", data);  // 서버로부터 받은 data 출력

            const commentsList = document.querySelector(".comments-list");
            const newCommentHtml = `
                <div class="comment">
                    <img src="${data.userImageUrl || '/img/user/default-profile.png'}" alt="프로필 사진" class="rounded-circle mt-3 S3_d_profile-img" style="width: 50px;">
                    <div class="comment-content">
                        <span class="font-weight-bold mr-2">${data.username}</span>
                        <p>${data.content}</p>
                        <p class="card-text">
                            <small class="text-muted">${new Date(data.createDate).toLocaleString()}</small>
                            <span class="badge badge-secondary ml-1 reply-button" data-comment-id="${data.commentId}">답글달기</span>
                        </p>
                    </div>
                </div>`;

            commentsList.insertAdjacentHTML("beforeend", newCommentHtml);
            commentContentElement.value = "";  // 입력 필드 초기화
        })
        .catch(error => {
            alert(error.message);
            console.error("Error occurred:", error);
        });
    });
});
